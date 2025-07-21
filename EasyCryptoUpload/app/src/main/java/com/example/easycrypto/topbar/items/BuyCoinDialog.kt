package com.example.easycrypto.topbar.items

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import com.example.easycrypto.crypto.presentation.coin_list.CoinListState
import com.example.easycrypto.crypto.presentation.coin_list.components.dummyCoin
import com.example.easycrypto.crypto.presentation.coin_list.components.previewCoin
import com.example.easycrypto.crypto.presentation.initial.AuthTextField
import com.example.easycrypto.crypto.presentation.models.CoinUi
import com.example.easycrypto.crypto.presentation.models.toCoinUi
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyCoinDialog(
    coins: List<Coin>,
    onDismiss: () -> Unit,
    onProceed: (Coin, String) -> Unit
) {
    var selectedCoinIndex by remember { mutableStateOf(0) }
    var investmentAmount by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black

    val selectedCoin = coins.getOrNull(selectedCoinIndex)

    Card(
        modifier = Modifier
            .padding(8.dp)
            .shadow(
                elevation = 15.dp,
                shape = RectangleShape,
                ambientColor = MaterialTheme.colorScheme.primary,
                spotColor = MaterialTheme.colorScheme.primary,
            ),
        shape = RectangleShape,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = contentColor
        )
    )
    {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {},
            dismissButton = {},
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text("Buy Cryptocurrency", style = MaterialTheme.typography.titleMedium)

                    Spacer(Modifier.height(12.dp))

                    // Dropdown menu
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .shadow(
                                    elevation = 15.dp,
                                    shape = RectangleShape,
                                    ambientColor = MaterialTheme.colorScheme.primary,
                                    spotColor = MaterialTheme.colorScheme.primary,
                                ),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                contentColor = contentColor
                            )
                        ) {
                            TextField(
                                value = selectedCoin?.name ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Select Coin") },
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.KeyboardArrowRight,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                coins.forEachIndexed { index, coin ->
                                    DropdownMenuItem(
                                        text = { Text(coin.name, fontSize = 16.sp) },
                                        onClick = {
                                            selectedCoinIndex = index
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Amount input
                    AuthTextField(
                        value = investmentAmount,
                        onValueChange = { investmentAmount = it },
                        placeholder = "Amount in USD"
                    )

                    Spacer(Modifier.height(12.dp))

                    // Current price info
                    selectedCoin?.let {
                        Text(
                            "1 ${it.symbol.uppercase()} = $ ${it.toCoinUi().priceUsd.formatted}",
                            style = MaterialTheme.typography.bodySmall,
                            color = contentColor
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Buttons
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(50.dp),
                            onClick = onDismiss
                        )
                        {
                            Text("Cancel", style = MaterialTheme.typography.labelMedium, color = contentColor)
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (selectedCoin != null && investmentAmount.toDoubleOrNull() != null) {
                                    onProceed(selectedCoin, investmentAmount)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(50.dp),
                            enabled = selectedCoin != null && investmentAmount.toDoubleOrNull() != null
                        ) {
                            Text("Proceed",
                                color = contentColor, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        )
    }
}

@PreviewLightDark
@Composable
private fun preview(){
    val coins = listOf(
        dummyCoin,
        dummyCoin,
        dummyCoin,
        dummyCoin
    )
    BuyCoinDialog(
        coins,
    {},
    {_,_ ->}
    )
}
