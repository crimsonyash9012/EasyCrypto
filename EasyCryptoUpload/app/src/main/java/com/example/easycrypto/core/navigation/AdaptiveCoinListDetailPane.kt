@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.example.easycrypto.core.navigation

import android.widget.Toast
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.easycrypto.core.presentation.util.ObserveAsEvents
import com.example.easycrypto.core.presentation.util.toString
import com.example.easycrypto.crypto.presentation.coin_detail.CoinDetailScreen
import com.example.easycrypto.crypto.presentation.coin_list.CoinListAction
import com.example.easycrypto.crypto.presentation.coin_list.CoinListEvent
import com.example.easycrypto.crypto.presentation.coin_list.CoinListScreen
import com.example.easycrypto.crypto.presentation.coin_list.CoinListViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AdaptiveCoinListDetailPane(
    modifier: Modifier = Modifier,
    // using same view model for both of them so that we can display them together
    viewModel: CoinListViewModel = koinViewModel(),
    navController: NavController
){
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    ObserveAsEvents(events = viewModel.events) {event->
        when(event){
            is CoinListEvent.Error ->{
                Toast.makeText(
                    context,
                    event.error.toString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    val navigator = rememberListDetailPaneScaffoldNavigator<Any>()
    NavigableListDetailPaneScaffold(
        navigator = navigator,
        listPane = {
            AnimatedPane {
                CoinListScreen(
                    state = state,
                    onAction = { action -> // intercepting the actions
                        viewModel.onAction(action)
                        when(action){
                            is CoinListAction.OnCoinClick -> {
                                navigator.navigateTo(
                                    pane = ListDetailPaneScaffoldRole.Detail
                                )
                            }
                             is CoinListAction.OnSearchQueryChanged -> {}
                        }
                    },
                    onProfileClick = {
                        navController.navigate("profile")
                    },
                    onWalletClick = {
                        navController.navigate("wallet")
                    }
                )
            }
        },
        detailPane = {
            AnimatedPane {
                CoinDetailScreen(state = state)
            }
        },
        modifier = modifier
    )
}