package com.example.easycrypto.crypto.presentation.coin_list

import com.example.easycrypto.crypto.presentation.models.CoinUi

sealed interface CoinListAction {
    data class OnCoinClick(val coinUi: CoinUi): CoinListAction
    data class OnSearchQueryChanged(val query: String): CoinListAction
//    data object OnRefresh: CoinListAction
}