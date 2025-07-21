package com.example.easycrypto.crypto.domain

import com.example.easycrypto.core.domain.util.NetworkError
import com.example.easycrypto.core.domain.util.Result
import java.time.ZonedDateTime

// abstract the functions of what a coin data source should do
interface CoinDataSource {
    suspend fun getCoins() : Result<List<Coin>, NetworkError>
    suspend fun getCoinHistory(
        coinId: String,
        start: ZonedDateTime,
        end: ZonedDateTime
    ): Result<List<CoinPrice>, NetworkError>
}