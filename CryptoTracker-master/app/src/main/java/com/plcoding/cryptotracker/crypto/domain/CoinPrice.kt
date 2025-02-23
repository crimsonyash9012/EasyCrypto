package com.plcoding.cryptotracker.crypto.domain

import java.time.ZonedDateTime

data class CoinPrice(
    val priceUsd: Double,
    // we can also use Long over here, but difficult to convert it
    val dateTime: ZonedDateTime
)
