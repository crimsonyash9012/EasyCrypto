package com.example.easycrypto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import com.example.easycrypto.core.navigation.AdaptiveCoinListDetailPane
import com.example.easycrypto.ui.theme.CryptoTrackerTheme

import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.easycrypto.core.database.AppwriteRepository
import com.example.easycrypto.crypto.presentation.coin_list.CoinListViewModel
import com.example.easycrypto.crypto.presentation.initial.signup.LogInScreen
import com.example.easycrypto.crypto.presentation.initial.signup.SignUpScreen
import com.example.easycrypto.topbar.CheckoutScreen
import com.example.easycrypto.topbar.ProfileScreen
import com.example.easycrypto.topbar.WalletScreen
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appwriteRepository = AppwriteRepository(this)
        setContent {
            CryptoTrackerTheme {
                val navController = rememberNavController()
                var isLoggedIn by remember { mutableStateOf<Boolean?>(null) }
                val viewModel: CoinListViewModel = koinViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    isLoggedIn = appwriteRepository.checkIfLoggedIn()
                }

                when (isLoggedIn) {
                    null -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    else -> {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            NavHost(
                                navController = navController,
                                startDestination = if (isLoggedIn == true) "coinlist" else "signup",
                                modifier = Modifier.padding(innerPadding)
                            ) {
                                composable("signup") {
                                    SignUpScreen(
                                        onLoginClick = {
                                            navController.navigate("login") {
                                                popUpTo("signup") { inclusive = true }
                                            }
                                        },
                                        onSignUpClick = { _, _, _, _ ->
                                            navController.navigate("coinlist") {
                                                popUpTo("signup") { inclusive = true }
                                            }
                                        }
                                    )
                                }

                                composable("coinlist") {
                                    AdaptiveCoinListDetailPane(navController = navController)
                                }

                                composable("profile") {
                                    ProfileScreen(state, navController)
                                }

                                composable("wallet") {
                                    WalletScreen(navController)
                                }
                                composable(
                                    "checkout/{symbol}/{amount}/{price}",
                                    arguments = listOf(
                                        navArgument("symbol") { type = NavType.StringType },
                                        navArgument("amount") { type = NavType.StringType },
                                        navArgument("price") { type = NavType.FloatType }
                                    )
                                ) { backStackEntry ->
                                    val coinId = backStackEntry.arguments?.getString("symbol") ?: ""
                                    val amount = backStackEntry.arguments?.getString("amount") ?: "0.0"
                                    val price = backStackEntry.arguments?.getFloat("price")?.toDouble() ?: 0.0
                                    CheckoutScreen(navController = navController, symbol = coinId, amount = amount, price = price)
                                }


                                composable("login") {
                                    LogInScreen(
                                        onSignUpClick = {
                                            navController.navigate("signup") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        },
                                        onLoginClick = { _, _ ->
                                            navController.navigate("coinlist") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}

