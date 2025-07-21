package com.example.easycrypto.core.database

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.easycrypto.crypto.presentation.models.User
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.Document
import io.appwrite.services.Account
import io.appwrite.services.Databases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppwriteRepository(
    val context: Context
) {

    private val client = Client(context)
        .setEndpoint("https://cloud.appwrite.io/v1")
        .setProject(Constants().PROJECT_ID)

    private val account = Account(client)
    private val database = Databases(client)

    suspend fun signUpUser(
        email: String,
        password: String,
        username: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val userId = ID.unique()

            withContext(Dispatchers.IO) {
                account.create(userId, email, password)

                val data = mapOf(
                    "email" to email,
                    "username" to username,
                    "cryptosOwned" to listOf<String>(),
                    "cryptoAmounts" to listOf<Double>(),
                    "cryptoValues" to listOf<Double>(),
                    "walletBalance" to 1000000.0,
                    "currentMoney" to 0.0
                )

                database.createDocument(
                    databaseId = Constants().DATABASE_ID,
                    collectionId = Constants().COLLECTION_USER_ID,
                    documentId = userId,
                    data = data
                )
                UserPreferences.saveUserId(context, userId)
                UserPreferences.setNewUserFlag(context, true)
            }

            onSuccess()
        } catch (e: Exception) {
            onError("Signup failed: ${e.message}")
        }
    }


    suspend fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            withContext(Dispatchers.IO) {
                account.createEmailPasswordSession(email, password)
            }
            val user = account.get()
            UserPreferences.saveUserId(context, user.id)
            onSuccess()
        } catch (e: Exception) {
            onError("Login failed: ${e.message}")
        }
    }


//    suspend fun fetchUserDocument() : User?{
//        try {
//            val document = withContext(Dispatchers.IO) {
//                database.getDocument(
//                    databaseId = Constants().DATABASE_ID,
//                    collectionId = Constants().COLLECTION_USER_ID,
//                    documentId = userId
//                )
//            }
//            onResult(document)
//        } catch (e: Exception) {
//            onError("Fetch failed: ${e.message}")
//        }
//    }

    suspend fun checkIfLoggedIn(): Boolean {
        return try {
            val session = account.getSession("current")
            true
        } catch (e: AppwriteException) {
            false
        }
    }

    suspend fun getUser(): User? {
        return try {
            val userId = UserPreferences.getUserId(context)
            val document = withContext(Dispatchers.IO) {
                database.getDocument(
                    databaseId = Constants().DATABASE_ID,
                    collectionId = Constants().COLLECTION_USER_ID,
                    documentId = userId.toString()
                )
            }

            val data = document.data

            User(
                email = data["email"] as? String ?: "",
                username = data["username"] as? String ?: "",
                cryptocurrenciesOwned = data["cryptosOwned"] as? List<String> ?: emptyList(),
                moneyFromEachCurrency = (data["cryptoValues"] as? List<*>)?.mapNotNull { (it as? Number)?.toDouble() } ?: emptyList(),
                cryptoAmounts = (data["cryptoAmounts"] as? List<*>)?.mapNotNull { (it as? Number)?.toDouble() } ?: emptyList(),
                currentMoney = (data["currentMoney"] as? Number)?.toDouble() ?: 0.0,
                walletMoney = (data["walletBalance"] as? Number)?.toDouble() ?: 0.0
            )

        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUserDocument(user: User) {
        withContext(Dispatchers.IO) {
            val data = mapOf(
                "email" to user.email,
                "username" to user.username,
                "cryptosOwned" to user.cryptocurrenciesOwned,
                "cryptoValues" to user.moneyFromEachCurrency,
                "cryptoAmounts" to user.cryptoAmounts,
                "currentMoney" to user.currentMoney,
                "walletBalance" to user.walletMoney
            )

            database.updateDocument(
                databaseId = Constants().DATABASE_ID,
                collectionId = Constants().COLLECTION_USER_ID,
                documentId = UserPreferences.getUserId(context).toString(),
                data = data
            )
        }
    }

    suspend fun logout(
    ) {
        try {
            withContext(Dispatchers.IO) {
                account.deleteSession("current")
            }
            UserPreferences.clearUserId(context)
        } catch (e: Exception) {
            Toast.makeText(context, "Logout failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


}