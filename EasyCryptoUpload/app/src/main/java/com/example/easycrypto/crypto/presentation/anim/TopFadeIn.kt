package com.example.easycrypto.crypto.presentation.anim

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*

@Composable
fun TopFadeIn(
    visible: Boolean,
    delayMillis: Int = 0,
    content: @Composable () -> Unit
) {
    val enterTransition = remember {
        slideInVertically(
            initialOffsetY = { -40 },
            animationSpec = tween(durationMillis = 300, delayMillis = delayMillis)
        ) + fadeIn(animationSpec = tween(300, delayMillis = delayMillis))
    }

    AnimatedVisibility(
        visible = visible,
        enter = enterTransition
    ) {
        content()
    }
}
