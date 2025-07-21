package com.example.easycrypto

import android.app.Application
import com.example.easycrypto.core.database.Constants
import com.example.easycrypto.di.appModule
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Databases
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class CryptoTrackerApp: Application() {

    lateinit var client: Client
    lateinit var account: Account
    lateinit var database: Databases
    override fun onCreate() {
        super.onCreate()

        client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1") // Replace with your Appwrite URL
            .setProject(Constants().PROJECT_ID)               // Get from Appwrite dashboard

        account = Account(client)
        database = Databases(client)

        startKoin {
            androidContext(this@CryptoTrackerApp)
            androidLogger()
            modules(appModule)
        }
    }
}