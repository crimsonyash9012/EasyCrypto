package com.example.easycrypto.crypto.presentation.coin_list

import com.example.easycrypto.core.domain.util.NetworkError

sealed interface CoinListEvent {
    // we want to pass the error message to our ui whenever error occurs
    data class Error(val error: NetworkError): CoinListEvent
}