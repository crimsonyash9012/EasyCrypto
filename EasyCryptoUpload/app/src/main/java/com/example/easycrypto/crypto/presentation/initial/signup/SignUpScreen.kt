package com.example.easycrypto.crypto.presentation.initial.signup

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.easycrypto.R
import com.example.easycrypto.crypto.presentation.initial.AuthTextField
import com.example.easycrypto.ui.theme.CryptoTrackerTheme
import com.example.easycrypto.crypto.presentation.anim.TopFadeIn
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.easycrypto.core.database.AuthState
import com.example.easycrypto.core.database.AuthViewModel
import org.koin.androidx.compose.koinViewModel
import kotlinx.coroutines.flow.collectLatest


private val spaceMono = FontFamily(
    Font(R.font.space_mono_regular, FontWeight.Normal),
    Font(R.font.space_mono_bold, FontWeight.Bold)
)


@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit = {},
    onSignUpClick: (email: String, username: String, password: String, confirmPassword: String) -> Unit = { _, _, _, _ -> }
) {
    val contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current
    val viewModel: AuthViewModel = koinViewModel()
    val state = viewModel.authState.collectAsState().value

    // Control visibility of elements for staggered animation
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    LaunchedEffect(state) {
        when (state) {
            is AuthState.Success -> {
                Toast.makeText(context, "Signup Successful!", Toast.LENGTH_SHORT).show()
                onSignUpClick(email, username, password, confirmPassword)
                viewModel.resetState()
            }
            is AuthState.Error -> {
                Toast.makeText(context, (state as AuthState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopFadeIn(visible, delayMillis = 0) {
            Text(
                text = "Popular cryptocurrencies. All on EasyCrypto",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                ),
                fontSize = 22.sp,
                fontFamily = spaceMono,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }

        TopFadeIn(visible, delayMillis = 100) {
            AuthTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Email"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TopFadeIn(visible, delayMillis = 200) {
            AuthTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = "Username"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TopFadeIn(visible, delayMillis = 300) {
            AuthTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                isPassword = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TopFadeIn(visible, delayMillis = 400) {
            AuthTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = "Confirm Password",
                isPassword = true
            )
        }

        Spacer(modifier = Modifier.height(32.dp))


        // sign up button
        TopFadeIn(visible, delayMillis = 500) {
            Button(
                onClick = {
                    viewModel.signUpUser(email, username, password, confirmPassword)
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Sign Up", style = MaterialTheme.typography.labelMedium)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TopFadeIn(visible, delayMillis = 600) {
            Divider(color = MaterialTheme.colorScheme.surfaceVariant)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TopFadeIn(visible, delayMillis = 700) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account?",
                    color = contentColor.copy(alpha = 0.7f)
                )
                TextButton(onClick = onLoginClick) {
                    Text(
                        text = "Login",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}


    @PreviewLightDark
@Composable
fun PreviewSignUpScreen() {
    CryptoTrackerTheme {
        SignUpScreen()
    }
}

