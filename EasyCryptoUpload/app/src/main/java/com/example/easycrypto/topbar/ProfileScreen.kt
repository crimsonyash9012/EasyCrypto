package com.example.easycrypto.topbar

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.easycrypto.R
import com.example.easycrypto.core.database.ProfileViewModel
import com.example.easycrypto.crypto.domain.Coin
import com.example.easycrypto.topbar.model.Holding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import com.example.easycrypto.crypto.presentation.coin_list.CoinListState
import com.example.easycrypto.crypto.presentation.coin_list.components.previewCoin
import com.example.easycrypto.crypto.presentation.models.CoinUi
import com.example.easycrypto.crypto.presentation.models.User
import com.example.easycrypto.crypto.presentation.models.toCoin
import com.example.easycrypto.crypto.presentation.models.toCoinUi
import com.example.easycrypto.topbar.items.BuyCoinDialog
import com.example.easycrypto.topbar.items.CryptoRow
import com.example.easycrypto.topbar.items.SellCoinDialog
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.*

private val NEWS = listOf(
    "Bitcoin Inventor is Unknown",
    "Some Countries Ban Cryptocurrencies",
    "Ethereum Was Crowdfunded",
    "China Is The Largest Miner",
    "Anyone Can Create A Crypto"
)


@Composable
fun ProfileScreen(
    state: CoinListState,
    navController: NavController,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    var showBuyDialog by remember { mutableStateOf(false) }
    var showSellDialog by remember { mutableStateOf(false) }


    val user = viewModel.user
    val isLoading = viewModel.isLoading

//    LaunchedEffect(user, state.coins) {
//        if (user != null && state.coins.isNotEmpty()) {
//            viewModel.updateCurrentHoldingsValue(state.coins)
//        }
//    }


    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    user?.let { userData ->

        val totalInvested = user.currentMoney
        val totalCurrentValue = getPriceFromCoins(user.moneyFromEachCurrency, user.cryptocurrenciesOwned, state, userData)
        val totalGain = totalCurrentValue - totalInvested
        val percentGain = if (totalInvested != 0.0) (totalGain / totalInvested) * 100 else 0.0


        val ownedCrypto = userData.cryptocurrenciesOwned.mapIndexedNotNull { index, symbol ->
            val liveCoin = state.coins.find { it.symbol.equals(symbol, ignoreCase = true) } ?: return@mapIndexedNotNull null

            Coin(
                id = symbol,
                rank = liveCoin.rank,
                name = liveCoin.name,
                symbol = liveCoin.symbol,
                priceUsd = liveCoin.priceUsd.value,
                marketCapUsd = liveCoin.marketCapUsd.value,
                changePercent24Hr = liveCoin.changePercent24Hr.value
            ).toCoinUi()
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
        ) {
            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineLarge,
                color = contentColor
            )

            Spacer(Modifier.height(24.dp))

            // User Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* Navigate to profile details */ },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.btc),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )

                Spacer(Modifier.width(16.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        text = userData.username,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = contentColor
                    )
                    Text(
                        text = userData.email,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Arrow",
                    tint = contentColor
                )
            }

            Spacer(Modifier.height(32.dp))

            // Fun Fact Card
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .shadow(
                        elevation = 15.dp,
                        shape = RectangleShape,
                        ambientColor = MaterialTheme.colorScheme.primary,
                        spotColor = MaterialTheme.colorScheme.primary,
                    ),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = contentColor
                )
            ){
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Did you know?",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                        Text(
                            text = NEWS.random(),
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    Icon(
                        painter = painterResource(id = R.drawable.stock),
                        contentDescription = "Stock Icon",
                        modifier = Modifier.size(64.dp),
                        tint = contentColor
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Wallet Summary
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 15.dp,
                        shape = RectangleShape,
                        ambientColor = MaterialTheme.colorScheme.primary,
                        spotColor = MaterialTheme.colorScheme.primary,
                    ),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Crypto", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("Your crypto balance", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        formatCurrency(totalCurrentValue),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = "Total gain: ${formatCurrency(totalGain)} (${
                            String.format("%.2f", percentGain)
                        }%)",
                        color = if (totalGain >= 0) Color(0xFF2E7D32) else Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    onClick = { showBuyDialog = true }
                ) {
                    Text("Buy", fontSize = 16.sp)
                }
                Button(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    onClick = { showSellDialog = true }
                ) {
                    Text("Sell", fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Owned Cryptos
            Text(
                "Your crypto",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                color = contentColor
            )

            Spacer(Modifier.height(8.dp))

            for(i in 0..ownedCrypto.size-1){
                val crypto = ownedCrypto.get(i)
                val newPrice = crypto.priceUsd.value * userData.cryptoAmounts.get(i)
                CryptoRow(crypto, newPrice)
            }

            Divider(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            )

            // Placeholder trending section
            Text(
                "Explore more crypto",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                color = contentColor
            )

            Spacer(Modifier.height(8.dp))

            val trendingCrypto = state.coins
                .sortedBy { it.rank }
                .take(5)

            trendingCrypto.forEach { CryptoRow(it, it.priceUsd.value) }

            if (showBuyDialog) {
                val coins = mutableListOf<Coin>()
                state.coins.forEach{ it->
                    coins.add(it.toCoin())
                }
                BuyCoinDialog(
                    coins = coins,
                    onDismiss = { showBuyDialog = false },
                    onProceed = { selectedCoin, amount ->
                        navController.navigate("checkout/${selectedCoin.symbol}/${amount}/${selectedCoin.priceUsd}")

                        Log.e("profile", selectedCoin.symbol)
                        showBuyDialog = false
                    }
                )
            }

            if (showSellDialog) {
                val coins = state.coins.map { it.toCoin() }

                SellCoinDialog(
                    coins = coins,
                    onDismiss = { showSellDialog = false },
                    onProceed = { selectedCoin, amount ->
                        viewModel.sellCrypto(
                            coinId = selectedCoin.symbol,
                            amountSold = amount.toDoubleOrNull() ?: 0.0,
                            currentPrice = selectedCoin.priceUsd
                        )
                        showSellDialog = false
                    }
                )
            }

            Spacer(Modifier.height(32.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                onClick = {
                    viewModel.logout(navController)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Logout", fontSize = 16.sp)
            }


        }
    }
}

private fun getPriceFromCoins(moneyFromEachCurrency: List<Double>, ownedCrypto: List<String>,state: CoinListState,  userData: User): Double{
    var sum = 0.00

    val ownedCrypto = userData.cryptocurrenciesOwned.mapIndexedNotNull { index, symbol ->
        val liveCoin = state.coins.find { it.symbol.equals(symbol, ignoreCase = true) }
            ?: return@mapIndexedNotNull null
        val investedAmount =
            (userData.cryptoAmounts.getOrNull(index)) ?: 0.0

        sum += investedAmount * liveCoin.priceUsd.value
//            val coinsOwned = if (liveCoin.priceUsd.value != 0.0) investedAmount / liveCoin.priceUsd.value else 0.0
//            val currentValue = coinsOwned * liveCoin.priceUsd.value

        Coin(
            id = symbol,
            rank = liveCoin.rank,
            name = liveCoin.name,
            symbol = liveCoin.symbol,
            priceUsd = liveCoin.priceUsd.value,
            marketCapUsd = liveCoin.marketCapUsd.value,
            changePercent24Hr = liveCoin.changePercent24Hr.value
        ).toCoinUi()
    }

    return sum
}

@PreviewLightDark
@Composable
private fun preview(){

    ProfileScreen(
        state = CoinListState(
            coins = (1..100).map{
                previewCoin.copy(id = it.toString())
            }
        ),
        rememberNavController()
    )
}
