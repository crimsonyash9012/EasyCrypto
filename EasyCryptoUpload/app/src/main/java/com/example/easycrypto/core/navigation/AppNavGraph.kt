package com.example.easycrypto.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.easycrypto.crypto.presentation.coin_list.CoinListScreen
import com.example.easycrypto.crypto.presentation.coin_list.CoinListViewModel
import com.example.easycrypto.crypto.presentation.initial.signup.SignUpScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = "signup") {

        composable("signup") {
            SignUpScreen(
                onLoginClick = {
                    navController.navigate("coinlist") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }

        composable("coinlist") {
            val viewModel = koinViewModel<CoinListViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()

            CoinListScreen(
                state = state,
                onAction = viewModel::onAction
            )
        }
    }
}
