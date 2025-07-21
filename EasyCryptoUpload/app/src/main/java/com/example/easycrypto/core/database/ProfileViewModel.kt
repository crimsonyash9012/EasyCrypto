package com.example.easycrypto.core.database

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.easycrypto.crypto.presentation.models.CoinUi
import com.example.easycrypto.crypto.presentation.models.User
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: AppwriteRepository
) : ViewModel() {

    var user by mutableStateOf<User?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set



    fun loadUserProfile() {
        viewModelScope.launch {
            isLoading = true
//            val userId = UserPreferences.getUserId(context) ?: return@launch
            user = repository.getUser()
            isLoading = false
        }
    }


    fun buyCrypto(
        coinId: String,
        amountInvested: Double,
        currentPrice: Double
    ) {
        viewModelScope.launch {
            val currentUser = repository.getUser() ?: return@launch

            val index = currentUser.cryptocurrenciesOwned.indexOf(coinId)
            val updatedOwned = currentUser.cryptocurrenciesOwned.toMutableList()
            val updatedCryptoAmounts = currentUser.cryptoAmounts.toMutableList()
            val updatedMoneyFromCurrency = currentUser.moneyFromEachCurrency.toMutableList()

            val coins = amountInvested / currentPrice
            val updatedWalletMoney = currentUser.walletMoney - amountInvested - 220.0
            val addedMoney = currentUser.currentMoney + amountInvested

            Log.d("UserUpdate", "Coins: $coins")
            Log.d("UserUpdate", "CryptoAmounts: $updatedCryptoAmounts")
            Log.d("UserUpdate", "MoneyFromEachCurrency: $updatedMoneyFromCurrency")
            Log.d("UserUpdate", "CurrentMoney: $addedMoney")

            if (index != -1) {
                updatedCryptoAmounts[index] += coins
                updatedMoneyFromCurrency[index] += amountInvested
            } else {
                updatedOwned.add(coinId)
                updatedCryptoAmounts.add(coins)
                updatedMoneyFromCurrency.add(amountInvested)
            }

            val updatedUser = currentUser.copy(
                cryptocurrenciesOwned = updatedOwned,
                cryptoAmounts = updatedCryptoAmounts,
                moneyFromEachCurrency = updatedMoneyFromCurrency,
                walletMoney = updatedWalletMoney,
                currentMoney = addedMoney
            )

            user = updatedUser

            repository.updateUserDocument(updatedUser)
        }
    }

    fun sellCrypto(
        coinId: String,
        amountSold: Double,
        currentPrice: Double
    ) {
        viewModelScope.launch {
            val currentUser = repository.getUser() ?: return@launch

            val index = currentUser.cryptocurrenciesOwned.indexOf(coinId)
            val updatedOwned = currentUser.cryptocurrenciesOwned.toMutableList()
            val updatedCryptoAmounts = currentUser.cryptoAmounts.toMutableList()
            val updatedMoneyFromCurrency = currentUser.moneyFromEachCurrency.toMutableList()

            val coins = amountSold / currentPrice
            val updatedWalletMoney = currentUser.walletMoney + amountSold
            val newMoney = currentUser.currentMoney - amountSold


            if (index == -1) {
                Toast.makeText(repository.context, "You don't have that amount of money!!", Toast.LENGTH_SHORT).show()
                return@launch
            } else {
//                updatedOwned.add(coinId)
                if(coins >= updatedCryptoAmounts.get(index)){
                    Toast.makeText(repository.context, "You can't sell that amount of money!!", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                else{
                    val newAmount = updatedCryptoAmounts.get(index) - coins
                    val newUpdatedMoney = updatedMoneyFromCurrency.get(index) - amountSold
                    updatedMoneyFromCurrency.set(index, newUpdatedMoney)
                    updatedCryptoAmounts.set(index, newAmount)

                    val updatedUser = currentUser.copy(
                        cryptocurrenciesOwned = updatedOwned,
                        cryptoAmounts = updatedCryptoAmounts,
                        moneyFromEachCurrency = updatedMoneyFromCurrency,
                        walletMoney = updatedWalletMoney,
                        currentMoney = newMoney
                    )

                    user = updatedUser

                    repository.updateUserDocument(updatedUser)
                }
            }
        }
    }


//    fun getLivePriceForCoin(coinId: String): Double {
//        return repository.getCachedLiveCoins().find { it.symbol.equals(coinId, ignoreCase = true) }?.priceUsd?.value ?: 0.0
//    }



//    fun updateMoneyFromEachCurrency(
//        user: User,
//        currentPrices: Map<String, Double>
//    ): User {
//        val updatedValues = user.cryptocurrenciesOwned.mapIndexed { index, coinId ->
//            val price = currentPrices[coinId] ?: 0.0
//            val invested = user.cryptoAmounts[index]
//            val coinsOwned = invested / price
//            coinsOwned * price
//        }
//
//        return user.copy(moneyFromEachCurrency = updatedValues)
//    }

    fun updateCurrentHoldingsValue(liveCoins: List<CoinUi>) {
        viewModelScope.launch {
            user?.let { currentUser ->

                val updatedMoneyFromEachCurrency = currentUser.cryptocurrenciesOwned.mapIndexed { index, symbol ->
                    val investedAmount = currentUser.cryptoAmounts.getOrNull(index) ?: 0.0

                    val coin = liveCoins.find { it.symbol.equals(symbol, ignoreCase = true) }
                    if (coin != null && coin.priceUsd.value > 0) {
                        val approxQuantity = investedAmount / coin.priceUsd.value
                        approxQuantity * coin.priceUsd.value
                    } else {
                        investedAmount
                    }
                }

                val totalCurrentValue = updatedMoneyFromEachCurrency.sum()

                val updatedUser = currentUser.copy(
                    moneyFromEachCurrency = updatedMoneyFromEachCurrency,
                    currentMoney = totalCurrentValue
                )

                repository.updateUserDocument(updatedUser)

                user = updatedUser
            }
        }
    }

    fun logout(navController: NavController) {
        viewModelScope.launch {
            repository.logout()
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

}

