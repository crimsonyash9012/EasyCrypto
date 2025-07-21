package com.example.easycrypto.topbar.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.easycrypto.crypto.presentation.models.CoinUi
import com.example.easycrypto.crypto.presentation.models.User
import com.example.easycrypto.topbar.formatCurrency

@Composable
fun CryptoRow(crypto: CoinUi, price: Double) {
    val percentage = crypto.changePercent24Hr.formatted

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = crypto.iconRes),
                contentDescription = crypto.name,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(crypto.name, style = MaterialTheme.typography.labelMedium, fontSize = 16.sp)
                Text(crypto.symbol.uppercase(), color = Color.Gray, fontSize = 16.sp)
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(formatCurrency(price) , style = MaterialTheme.typography.labelMedium)
            Text(
                text = "$percentage%",
                color = if (crypto.changePercent24Hr.value >= 0) Color(0xFF2E7D32) else Color.Red
            )
        }
    }
}