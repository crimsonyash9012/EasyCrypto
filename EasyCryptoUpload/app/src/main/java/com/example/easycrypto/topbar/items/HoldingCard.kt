package com.example.easycrypto.topbar.items

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.easycrypto.topbar.model.Holding

@Composable
fun HoldingCard(holding: Holding) {
    val contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RectangleShape,
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = holding.coinName,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor
            )
            Text(
                text = "$${String.format("%.2f", holding.investedAmount)}",
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor
            )
        }
    }
}


@Preview
@Composable
private fun preview(){
    HoldingCard(Holding("Bitcoin", 1231.03))
}
