package com.plcoding.cryptotracker.crypto.data.networking.dto

import kotlinx.serialization.Serializable

// data transfer object
/** why aren't we using gson
 * we couple our domain layout to implementation detail
 * we want to isolate our domain layer
 * it's fine for smaller projects to leave the domain layer
 */
@Serializable
data class CoinDto(
    val id: String,
    val rank: Int,
    val name: String,
    val symbol: String,
    val marketCapUsd: Double,
    val priceUsd: Double,
    val changePercent24Hr: Double
)
