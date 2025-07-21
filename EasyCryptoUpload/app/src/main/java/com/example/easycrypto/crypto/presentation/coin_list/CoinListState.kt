package com.example.easycrypto.crypto.presentation.coin_list

import androidx.compose.runtime.Immutable
import com.example.easycrypto.crypto.presentation.models.CoinUi

@Immutable
data class CoinListState(
    val isLoading: Boolean = false,
    val coins: List<CoinUi> = emptyList(),
    val selectedCoin: CoinUi? = null
)
