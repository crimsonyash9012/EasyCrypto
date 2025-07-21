package com.example.easycrypto.topbar


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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.TouchApp
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
import androidx.compose.foundation.Canvas
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.example.easycrypto.core.database.ProfileViewModel
import com.example.easycrypto.crypto.domain.Coin
import com.example.easycrypto.crypto.presentation.models.DisplayableNumber
import com.example.easycrypto.crypto.presentation.models.toCoinUi
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale


public fun formatCurrency(currency: Double) : String {
    val formatter  = NumberFormat.getNumberInstance(Locale.getDefault()).apply { // different countries have different comma separation
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return formatter.format(currency)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    navController: NavController,
    viewModel: ProfileViewModel = koinViewModel()
) {

    val arcColor1 = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    val arcColor2 = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)

    val user = viewModel.user
    val isLoading = viewModel.isLoading

    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val contentColor = if(isSystemInDarkTheme()) Color.White else Color.Black

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = colorScheme.primary
                    )
                }
            }
        },
        bottomBar = {
        }
    ) { paddingValues ->

        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Main Balance
                Text("Wallet Cash", fontSize = 16.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Text("\$" + formatCurrency(user!!.walletMoney) , fontSize = 36.sp, style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))

                // Accounts Button
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(
                        "Accounts",
                        color = Color.Black,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ActionButton(icon = Icons.Default.Add, label = "Add money")
                    ActionButton(icon = Icons.Default.SyncAlt, label = "Exchange")
                    ActionButton(icon = Icons.Default.Info, label = "Details")
                    ActionButton(icon = Icons.Default.MoreHoriz, label = "More")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // No Transactions
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .shadow(
                            elevation = 15.dp,
                            shape = RectangleShape,
                            ambientColor = colorScheme.primary,
                            spotColor = colorScheme.primary,
                        ),
                    shape = RoundedCornerShape(15.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = colorScheme.primary
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surfaceContainer,
                        contentColor = contentColor
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.AttachMoney,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "No Free Cash Available",
                            color = Color.Gray,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .shadow(
                            elevation = 15.dp,
                            shape = RectangleShape,
                            ambientColor = colorScheme.primary,
                            spotColor = colorScheme.primary,
                        ),
                    shape = RoundedCornerShape(15.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = colorScheme.primary
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surfaceContainer,
                        contentColor = contentColor
                    )
                )
                {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    )
                    {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.TouchApp,
                                contentDescription = null,
                                tint = colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    "Customize your Home screen",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Text(
                                    "Long press and drag widgets to reorder",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
            }

            // Bottom glow arc animation
            val infiniteTransition = rememberInfiniteTransition(label = "arcWave")

            val animatedHeight1 by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 180f, // 🌊 higher wave
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "wave1"
            )

            val animatedHeight2 by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 150f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2500, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "wave2"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val arcWidth = canvasWidth * 1.5f

                    // First wave arc
                    drawArc(
                        color = arcColor1,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(
                            (canvasWidth - arcWidth) / 2f,
                            size.height - animatedHeight1
                        ),
                        size = Size(arcWidth, animatedHeight1 * 2)
                    )

                    // Second wave arc
                    drawArc(
                        color = arcColor2,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(
                            (canvasWidth - arcWidth) / 2f,
                            size.height - animatedHeight2 - 30f // shift slightly for layering
                        ),
                        size = Size(arcWidth, animatedHeight2 * 2)
                    )
                }
            }
        }

    }

}

@Composable
fun ActionButton(icon: ImageVector, label: String) {
    val contentColor = if(isSystemInDarkTheme()) Color.Black else Color.White
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color = colorScheme.primary, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = contentColor)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 12.sp, style = MaterialTheme.typography.labelSmall)
    }
}



@PreviewLightDark
@Composable
private fun preview(){
    WalletScreen(rememberNavController())
}
