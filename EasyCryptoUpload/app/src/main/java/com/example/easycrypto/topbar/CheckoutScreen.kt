package com.example.easycrypto.topbar




import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.animation.core.*
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.example.easycrypto.core.database.AppwriteRepository
import com.example.easycrypto.core.database.ProfileViewModel
import com.example.easycrypto.core.database.UserPreferences
import com.example.easycrypto.crypto.domain.Coin
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    symbol: String,
    price: Double,
    amount: String,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val appwriteRepo = remember { AppwriteRepository(context) }

    val amountToPay = amount.toDoubleOrNull() ?: 0.0
    var walletBalance by remember { mutableStateOf(0.0) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    val contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black

//    val user = viewModel.user
//    walletBalance = user!!.walletMoney

    LaunchedEffect(Unit) {
        val userId = UserPreferences.getUserId(context)
        val user = appwriteRepo.getUser()
        walletBalance = user!!.walletMoney
    }

    Log.e("userId", symbol)

    Log.e("checkout wallet", walletBalance.toString())

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                Button(onClick = {
                    showSuccessDialog = false
                    navController.navigate("profile") {
//                        popUpTo("checkout/$symbol/$price/$amount") { inclusive = true }
                        popUpTo("profile") {
                            inclusive = true
                        }
                    }
                }) {
                    Text("OK", style = MaterialTheme.typography.labelMedium)
                }
            },
            title = { Text("Payment Successful", style = MaterialTheme.typography.headlineLarge) },
            text = { Text("Your purchase has been completed successfully.") }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout", fontWeight = FontWeight.Bold, color = contentColor, style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    Text(
                        text = "Cancel",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable { navController.popBackStack() }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    val totalAmount = amountToPay
                    val coinSymbol = symbol
                    val coinPrice = price
                    Log.e("onClick wallet", coinSymbol + " " + coinPrice.toString())
                    if (coinPrice <= 0.0) {
                        Toast.makeText(context, "Invalid or unknown coin price!", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    if (walletBalance >= totalAmount + 220.00) {
                        viewModel.buyCrypto(
                            coinId = coinSymbol,
                            amountInvested = totalAmount,
                            currentPrice = coinPrice
                        )
                        walletBalance -= totalAmount
                        showSuccessDialog = true
                    } else {
                        Toast.makeText(context, "Insufficient wallet balance!", Toast.LENGTH_LONG).show()
                    }
                }
                ,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Pay", color = contentColor, style = MaterialTheme.typography.labelMedium)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CardInfo(title = "Total Currencies", value = amountToPay)
            CardInfo(title = "Platform Fee", value = 20.00)
            CardInfo(title = "Service Tax", value = 200.00)

            // Wallet section
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
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Total payable amount", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("$ " + formatCurrency(amountToPay + 220.00), fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Wallet, contentDescription = "Wallet")
                        Column(Modifier.weight(1f).padding(start = 12.dp)) {
                            Text("EasyCrypto Wallet", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                            Text("$ " + formatCurrency(walletBalance), style = MaterialTheme.typography.bodySmall)
                        }
                        Text("Change", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}


@Composable
fun CardInfo(title: String, value: Double) {
    val contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 15.dp,
                shape = RectangleShape,
                ambientColor = MaterialTheme.colorScheme.primary,
                spotColor = MaterialTheme.colorScheme.primary,
            ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = contentColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$ " + formatCurrency(value),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End
            )
        }
    }
}


//@PreviewLightDark
//@Composable
//private fun preview(){
//    CheckoutScreen(rememberNavController(), "2342342", "2342342")
//}
