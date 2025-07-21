package com.example.easycrypto.crypto.presentation.coin_list

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.easycrypto.core.database.AppwriteRepository
import com.example.easycrypto.core.database.ProfileViewModel
import com.example.easycrypto.core.database.UserPreferences
import com.example.easycrypto.crypto.presentation.coin_list.components.CoinListItem
import com.example.easycrypto.crypto.presentation.coin_list.components.CoinListToolbar
import com.example.easycrypto.crypto.presentation.coin_list.components.previewCoin
import com.example.easycrypto.ui.theme.CryptoTrackerTheme
import org.koin.androidx.compose.koinViewModel


@Composable
fun CoinListScreen(
    state: CoinListState,
    onAction: (CoinListAction) -> Unit,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onWalletClick: () -> Unit = {}

){
    // for toast, we need context

    val contentColor = if(isSystemInDarkTheme()) Color.White else Color.Black

    var showWelcomeDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val viewModel : ProfileViewModel = koinViewModel()
//    val repository = AppwriteRepository(context)
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()

        if (UserPreferences.isNewUser(context)) {
            showWelcomeDialog = true
            UserPreferences.setNewUserFlag(context, false)
        }
    }

    if (showWelcomeDialog) {
        androidx.compose.material3.AlertDialog(
            modifier = Modifier.background(MaterialTheme.colorScheme.primary),
            onDismissRequest = { showWelcomeDialog = false },
            confirmButton = {
                Button(onClick = { showWelcomeDialog = false }) {
                    Text("Continue to the App",  style = MaterialTheme.typography.labelMedium)
                }
            },
            title = {
                Text("Welcome to EasyCrypto!", color = contentColor, fontSize = 20.sp)
            },
            text = {
                Text(
                    "Thank you for downloading EasyCrypto!\n\nWe're thrilled to have you on board.\n\n" +
                            "To show our appreciation, we’ve added a special gift just for you:\n\n" +
                            "$1,000,000 in-app credits — absolutely FREE — ready for your transactions.\n\n" +
                            "Enjoy the journey!"
                ,
                    color = contentColor)
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.background
        )
    }




    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    )
    {
        CoinListToolbar(
            onProfileClick = onProfileClick,
            onWalletClick = onWalletClick,
            onSearch = { query ->
                onAction(CoinListAction.OnSearchQueryChanged(query))
            }
        )
        if(state.isLoading){
            Box(
                modifier = modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator()
            }
        }
        else{
            LazyColumn(
                modifier = modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.coins){ coinUi ->
                    CoinListItem(
                        coinUi = coinUi,
                        onClick = {onAction(CoinListAction.OnCoinClick(coinUi))},
                        modifier = Modifier.fillMaxWidth()
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun CoinListScreenPreview(){
    CryptoTrackerTheme {
        CoinListScreen(
            state = CoinListState(
                coins = (1..100).map{
                    previewCoin.copy(id = it.toString())
                }
            ),
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background),
            onAction = {}
        )
    }
}