package com.example.pethealthmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pethealthmanager.AuthViewModel
import com.example.pethealthmanager.ui.components.AttemptsHint
import com.example.pethealthmanager.ui.components.DogIllustration
import com.example.pethealthmanager.ui.components.FooterNote
import com.example.pethealthmanager.ui.components.GradientButton
import com.example.pethealthmanager.ui.components.HeroBackdrop
import com.example.pethealthmanager.ui.components.HeroIntro
import com.example.pethealthmanager.ui.components.MessageBanner
import com.example.pethealthmanager.ui.components.PetTextField
import com.example.pethealthmanager.ui.theme.Coral
import com.example.pethealthmanager.ui.theme.Cream
import com.example.pethealthmanager.ui.theme.ErrorInk
import com.example.pethealthmanager.ui.theme.Ink
import com.example.pethealthmanager.ui.theme.InkSoft

@Composable
fun LoginScreen(vm: AuthViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .navigationBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(346.dp)
            ) {
                HeroBackdrop(Modifier.matchParentSize())
                HeroIntro(
                    kicker = "PET WELLNESS, EVERY DAY",
                    title = "Care for your\nfurry friend.",
                    subtitle = "Health records, meals and happy\nmoments — one warm place."
                )
                DogIllustration(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 6.dp)
                        .size(206.dp, 176.dp)
                        .offset(y = 16.dp)
                )
            }

            Column(Modifier.offset(y = (-56).dp)) {
                LoginCard(vm)
                Spacer(Modifier.height(22.dp))
                FooterNote("Pet Health Manager · v1.0")
                Spacer(Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun LoginCard(vm: AuthViewModel) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(32.dp),
        color = Color.White,
        shadowElevation = 18.dp
    ) {
        Column(Modifier.padding(horizontal = 22.dp, vertical = 24.dp)) {
            Text(
                text = "Welcome back",
                style = MaterialTheme.typography.headlineSmall,
                color = Ink
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = "Log in to keep your pet's health on track.",
                style = MaterialTheme.typography.bodyMedium,
                color = InkSoft
            )
            Spacer(Modifier.height(20.dp))

            PetTextField(
                label = "USERNAME",
                value = vm.loginName,
                onValueChange = vm::updateLoginName,
                placeholder = "e.g. lily_the_cat"
            )
            Spacer(Modifier.height(14.dp))
            PetTextField(
                label = "PASSWORD",
                value = vm.loginPassword,
                onValueChange = vm::updateLoginPassword,
                placeholder = "Your password",
                isPassword = true,
                imeAction = ImeAction.Done,
                onDone = vm::login
            )

            MessageBanner(message = vm.message, isError = vm.messageIsError)

            if (vm.isLoginLocked || vm.attemptsLeft < AuthViewModel.MAX_ATTEMPTS) {
                Spacer(Modifier.height(14.dp))
                AttemptsHint(attemptsLeft = vm.attemptsLeft, total = AuthViewModel.MAX_ATTEMPTS)
            }

            Spacer(Modifier.height(20.dp))
            GradientButton(
                text = if (vm.isLoginLocked) "LOGIN DISABLED" else "LOGIN",
                onClick = vm::login,
                enabled = !vm.isLoginLocked
            )

            if (vm.isLoginLocked) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Too many failed attempts. Restart the app to try again.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ErrorInk,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "New to the family?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkSoft
                )
                TextButton(
                    onClick = vm::goToRegister,
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(
                        text = "REGISTER",
                        style = MaterialTheme.typography.labelSmall,
                        color = Coral
                    )
                }
            }
        }
    }
}
