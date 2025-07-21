package com.example.easycrypto.crypto.presentation.coin_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easycrypto.core.domain.util.onError
import com.example.easycrypto.core.domain.util.onSuccess
import com.example.easycrypto.crypto.domain.CoinDataSource
import com.example.easycrypto.crypto.presentation.coin_detail.DataPoint
import com.example.easycrypto.crypto.presentation.models.CoinUi
import com.example.easycrypto.crypto.presentation.models.toCoinUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CoinListViewModel(
    // don't import the RemoteCoinDataSource as it'll violate the structure
    private val coinDataSource: CoinDataSource
): ViewModel() {
    private val _state = MutableStateFlow(CoinListState())
        val state = _state.onStart {loadCoins()}
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                // it'll execute it as long as there's no more subscriber + 5sec
                CoinListState()
            )// immutable variant of _state

    /*
    init{
        loadCoins()
    }
    * it can make it harder to make test cases for the view model
    * we can't control when this function fires, it always executes when we create a new view model
     */

    // building a channel - by using this, errors are not cached and won't be recalled whenever
    // a new subscriber hits
    private val _events = Channel<CoinListEvent>()
    val events  = _events.receiveAsFlow()
    fun onAction(action: CoinListAction){
        when (action){
            is CoinListAction.OnCoinClick ->{
                selectCoin(action.coinUi)
                _state.update { it.copy(
                    selectedCoin = action.coinUi
                )
                }
            }

            is CoinListAction.OnSearchQueryChanged ->{
                searchCoins(action.query)
            }
//            CoinListAction.OnRefresh ->{
//                loadCoins()
//            }
        }
    }

    private fun selectCoin(coinUi: CoinUi){
        _state.update{it.copy(selectedCoin = coinUi)}
        viewModelScope.launch {
            coinDataSource
                .getCoinHistory(
                    coinId = coinUi.id,
                    start = ZonedDateTime.now().minusDays(5),
                    end = ZonedDateTime.now()
                )
                .onSuccess { history->
                    val dataPoints = history
                        .sortedBy { it.dateTime }
                        .map {
                            DataPoint(
                                x = it.dateTime.hour.toFloat(),
                                y = it.priceUsd.toFloat(),
                                xLabel = DateTimeFormatter
                                    .ofPattern("ha\nM/d")
                                    .format(it.dateTime)
                            )
                        }
                    _state.update {
                        it.copy(
                            selectedCoin = it.selectedCoin?.copy(
                                coinPriceHistory = dataPoints
                            )
                        )
                    }
                }
                .onError { error->
                    _events.send(CoinListEvent.Error(error))
                }
        }
    }
    private fun loadCoins(){
        viewModelScope.launch {
            _state.update { it.copy(
                isLoading = true
            ) }

            coinDataSource
                .getCoins()
                .onSuccess { coins->
                    _state.update { it.copy(
                        isLoading = false,
                        coins = coins.map{it.toCoinUi()}
                    ) }

                }.onError { error ->
                    _state.update { it.copy(isLoading = false) }
                    // sending event into such a channel
                    // we won't get these errors again when there's a config change
                    _events.send(CoinListEvent.Error(error))
                }
        }
    }

    private fun searchCoins(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            coinDataSource
                .getCoins()
                .onSuccess { coins ->
                    val filtered = coins.filter {
                        it.name.contains(query, ignoreCase = true)
                    }.map { it.toCoinUi() }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            coins = filtered
                        )
                    }
                }
                .onError { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(CoinListEvent.Error(error))
                }
        }
    }

}