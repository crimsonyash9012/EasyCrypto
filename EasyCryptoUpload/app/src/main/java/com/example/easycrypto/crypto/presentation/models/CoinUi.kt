package com.example.easycrypto.crypto.presentation.models

import androidx.annotation.DrawableRes
import com.example.easycrypto.crypto.domain.Coin
import com.example.easycrypto.core.presentation.util.getDrawableIdForCoin
import com.example.easycrypto.crypto.presentation.coin_detail.DataPoint
import java.text.NumberFormat
import java.util.Locale

data class CoinUi(
    val id: String,
    val rank : Int,
    val name : String,
    val symbol : String,
    val marketCapUsd: DisplayableNumber,
    val priceUsd : DisplayableNumber,
    val changePercent24Hr : DisplayableNumber,
    @DrawableRes val iconRes : Int, // not a random picture, only drawable res
    val coinPriceHistory: List<DataPoint> = emptyList()
){

}

data class DisplayableNumber( // we don't want data to be formatted later, hence already using formatted data class
    val value : Double,
    val formatted : String,

)

fun Coin.toCoinUi() : CoinUi{
    return CoinUi(
        id = id,
        name = name,
        symbol = symbol,
        rank = rank,
        priceUsd = priceUsd.toDisplayableNumber(),
        marketCapUsd = marketCapUsd.toDisplayableNumber(),
        changePercent24Hr = changePercent24Hr.toDisplayableNumber(),
        iconRes = getDrawableIdForCoin(symbol)
    )
}

fun CoinUi.toCoin() : Coin{
    return Coin(
        id = id,
        rank = rank,
        name = name,
        symbol = symbol,
        marketCapUsd = marketCapUsd.value,
        priceUsd = priceUsd.value,
        changePercent24Hr = changePercent24Hr.value
    )
}

fun Double.toDisplayableNumber() : DisplayableNumber{
    val formatter  = NumberFormat.getNumberInstance(Locale.getDefault()).apply { // different countries have different comma separation
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return DisplayableNumber(
        value = this,
        formatted = formatter.format(this)
    )
}
