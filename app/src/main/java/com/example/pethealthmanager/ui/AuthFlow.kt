package com.example.pethealthmanager.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pethealthmanager.AuthScreen
import com.example.pethealthmanager.AuthViewModel
import com.example.pethealthmanager.ui.screens.LoginScreen
import com.example.pethealthmanager.ui.screens.RegisterScreen
import com.example.pethealthmanager.ui.screens.SuccessScreen
import com.example.pethealthmanager.ui.theme.Coral
import com.example.pethealthmanager.ui.theme.InkSoft

@Composable
fun AuthFlow(vm: AuthViewModel) {
    BackHandler {
        if (vm.screen == AuthScreen.SUCCESS) vm.finishAuth() else vm.exitAuth()
    }

    Box(Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = vm.screen,
            transitionSpec = {
                val forward = targetState.ordinal >= initialState.ordinal
                val direction = if (forward) 1 else -1
                (
                    slideInHorizontally(animationSpec = tween(420)) { width -> width * direction } +
                        fadeIn(animationSpec = tween(280))
                    ) togetherWith (
                    slideOutHorizontally(animationSpec = tween(420)) { width -> -width * direction } +
                        fadeOut(animationSpec = tween(180))
                    )
            },
            label = "auth-screen"
        ) { screen ->
            when (screen) {
                AuthScreen.LOGIN -> LoginScreen(vm)
                AuthScreen.REGISTER -> RegisterScreen(vm)
                AuthScreen.SUCCESS -> SuccessScreen(vm, onContinue = vm::finishAuth)
            }
        }

        if (vm.screen != AuthScreen.SUCCESS) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 14.dp, end = 16.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.92f))
                    .clickable { vm.exitAuth() }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "CLOSE",
                    style = MaterialTheme.typography.labelSmall,
                    color = Coral
                )
            }
        }
    }
}
