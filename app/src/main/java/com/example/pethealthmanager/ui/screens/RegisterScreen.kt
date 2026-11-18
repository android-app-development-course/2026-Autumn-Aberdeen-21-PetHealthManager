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
import androidx.compose.ui.unit.dp
import com.example.pethealthmanager.AuthViewModel
import com.example.pethealthmanager.ui.components.FooterNote
import com.example.pethealthmanager.ui.components.GradientButton
import com.example.pethealthmanager.ui.components.HeroBackdrop
import com.example.pethealthmanager.ui.components.HeroIntro
import com.example.pethealthmanager.ui.components.MessageBanner
import com.example.pethealthmanager.ui.components.PetTextField
import com.example.pethealthmanager.ui.theme.Coral
import com.example.pethealthmanager.ui.theme.Cream
import com.example.pethealthmanager.ui.theme.Ink
import com.example.pethealthmanager.ui.theme.InkSoft

@Composable
fun RegisterScreen(vm: AuthViewModel) {
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
                    .height(264.dp)
            ) {
                HeroBackdrop(Modifier.matchParentSize())
                HeroIntro(
                    kicker = "JOIN THE PACK",
                    title = "Create your\naccount.",
                    subtitle = "Your pet's wellness journey starts here."
                )
            }

            Column(Modifier.offset(y = (-44).dp)) {
                RegisterCard(vm)
                Spacer(Modifier.height(22.dp))
                FooterNote("Your account stays on this device")
                Spacer(Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun RegisterCard(vm: AuthViewModel) {
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
                text = "Create your account",
                style = MaterialTheme.typography.headlineSmall,
                color = Ink
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = "A few details and you are ready to go.",
                style = MaterialTheme.typography.bodyMedium,
                color = InkSoft
            )
            Spacer(Modifier.height(20.dp))

            PetTextField(
                label = "USERNAME",
                value = vm.registerName,
                onValueChange = vm::updateRegisterName,
                placeholder = "At least 3 characters"
            )
            Spacer(Modifier.height(14.dp))
            PetTextField(
                label = "PASSWORD",
                value = vm.registerPassword,
                onValueChange = vm::updateRegisterPassword,
                placeholder = "At least 6 characters",
                isPassword = true
            )
            Spacer(Modifier.height(14.dp))
            PetTextField(
                label = "CONFIRM PASSWORD",
                value = vm.registerConfirm,
                onValueChange = vm::updateRegisterConfirm,
                placeholder = "Repeat your password",
                isPassword = true,
                imeAction = ImeAction.Done,
                onDone = vm::register
            )

            MessageBanner(message = vm.message, isError = vm.messageIsError)

            Spacer(Modifier.height(20.dp))
            GradientButton(
                text = "REGISTER",
                onClick = vm::register
            )

            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already registered?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkSoft
                )
                TextButton(
                    onClick = vm::goToLogin,
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(
                        text = "LOG IN",
                        style = MaterialTheme.typography.labelSmall,
                        color = Coral
                    )
                }
            }
        }
    }
}
