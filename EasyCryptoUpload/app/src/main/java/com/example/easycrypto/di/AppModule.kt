package com.example.easycrypto.di

import com.example.easycrypto.core.data.networking.HttpClientFactory
import com.example.easycrypto.core.database.AppwriteRepository
import com.example.easycrypto.core.database.AuthViewModel
import com.example.easycrypto.core.database.ProfileViewModel
import com.example.easycrypto.crypto.data.networking.RemoteCoinDataSource
import com.example.easycrypto.crypto.domain.CoinDataSource
import com.example.easycrypto.crypto.presentation.coin_list.CoinListViewModel
import io.ktor.client.engine.cio.CIO
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

// data injection
val appModule = module {
    single { HttpClientFactory.create(CIO.create()) }
    single { AppwriteRepository(get()) }
    viewModel {
        AuthViewModel(get())
    }
    viewModel { ProfileViewModel(get()) }
    // whenever we request the abstraction of this interface ->inject the implementation instead
    singleOf(::RemoteCoinDataSource).bind<CoinDataSource>()
    viewModelOf(::CoinListViewModel)
}