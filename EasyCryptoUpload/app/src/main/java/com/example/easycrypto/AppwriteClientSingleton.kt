package com.example.easycrypto

import android.content.Context
import io.appwrite.Client
import io.appwrite.services.Account

object AppwriteClientSingleton {
    private var _client: Client? = null
    private var _account: Account? = null

    fun init(context: Context) {
        _client = Client(context)
            .setEndpoint("https://cloud.appwrite.io/v1") // Replace if you're self-hosting
            .setProject("YOUR_PROJECT_ID") // TODO: Replace with your actual Project ID

        _account = Account(_client!!)
    }

    val account: Account
        get() = _account ?: throw IllegalStateException("Appwrite not initialized. Call init(context) first.")
}
