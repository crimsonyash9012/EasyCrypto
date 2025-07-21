package com.example.easycrypto.crypto.data.networking

import com.example.easycrypto.core.data.networking.constructUrl
import com.example.easycrypto.core.data.networking.safeCall
import com.example.easycrypto.core.domain.util.NetworkError
import com.example.easycrypto.core.domain.util.Result
import com.example.easycrypto.core.domain.util.map
import com.example.easycrypto.crypto.data.mappers.toCoin
import com.example.easycrypto.crypto.data.mappers.toCoinPrice
import com.example.easycrypto.crypto.data.networking.dto.CoinHistoryDto
import com.example.easycrypto.crypto.data.networking.dto.CoinResponseDto
import com.example.easycrypto.crypto.domain.Coin
import com.example.easycrypto.crypto.domain.CoinDataSource
import com.example.easycrypto.crypto.domain.CoinPrice
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import java.time.ZoneId
import java.time.ZonedDateTime

class RemoteCoinDataSource(
    private val httpClient: HttpClient
): CoinDataSource {
    override suspend fun getCoins(): Result<List<Coin>, NetworkError> {
        return safeCall<CoinResponseDto> {
            httpClient.get(
                urlString = constructUrl("/assets")
            )
        }.map{ response->
            response.data.map{
                it.toCoin()
            }
        }
    }

    override suspend fun getCoinHistory(
        coinId: String,
        start: ZonedDateTime,
        end: ZonedDateTime
    ): Result<List<CoinPrice>, NetworkError> {

        val startMilis = start
            .withZoneSameInstant(ZoneId.of("UTC"))
            .toInstant()
            .toEpochMilli()
        val endMilis = end
            .withZoneSameInstant(ZoneId.of("UTC"))
            .toInstant()
            .toEpochMilli()

        return safeCall<CoinHistoryDto> {
            httpClient.get(
                urlString = constructUrl("/assets/$coinId/history")
            ){
                parameter("interval", "h6")
                parameter("start", startMilis)
                parameter("end", endMilis)

            }
        }.map { response ->
            response.data.map {it.toCoinPrice()}
        }
    }
}