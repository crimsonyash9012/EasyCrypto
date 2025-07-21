package com.example.easycrypto.crypto.presentation.models

data class User(
    val email: String = "",
    val username: String = "",
    val cryptocurrenciesOwned: List<String> = emptyList(),
    val moneyFromEachCurrency: List<Double> = emptyList(),
    val cryptoAmounts: List<Double> = emptyList(),
    val currentMoney: Double = 0.0,
    val walletMoney: Double = 1_000_000.0 // default starting balance
)
