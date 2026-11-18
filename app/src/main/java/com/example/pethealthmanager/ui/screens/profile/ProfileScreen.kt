package com.example.pethealthmanager.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pethealthmanager.AuthViewModel
import com.example.pethealthmanager.ui.components.FooterNote
import com.example.pethealthmanager.ui.components.GradientButton
import com.example.pethealthmanager.ui.components.HintNote
import com.example.pethealthmanager.ui.components.LabelValueRow
import com.example.pethealthmanager.ui.components.PawBadge
import com.example.pethealthmanager.ui.components.ScreenHeader
import com.example.pethealthmanager.ui.components.SectionCard
import com.example.pethealthmanager.ui.components.SoftButton
import com.example.pethealthmanager.ui.theme.Coral
import com.example.pethealthmanager.ui.theme.CreamDeep
import com.example.pethealthmanager.ui.theme.ErrorInk
import com.example.pethealthmanager.ui.theme.Ink
import com.example.pethealthmanager.ui.theme.InkSoft
import com.example.pethealthmanager.ui.theme.Mango

@Composable
fun ProfileScreen(authViewModel: AuthViewModel) {
    val user by authViewModel.currentUser.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ScreenHeader(
            kicker = "ACCOUNT",
            title = "Profile",
            subtitle = "Sign in when you want, or keep browsing as a guest.",
            pets = emptyList(),
            selectedPetId = null,
            onSelectPet = {},
            onAddPet = {}
        )

        if (user == null) {
            GuestCard(
                onLogin = authViewModel::startLogin,
                onRegister = authViewModel::startRegister
            )
        } else {
            SignedInCard(
                username = user.orEmpty(),
                onLogout = authViewModel::logout
            )
        }

        Spacer(Modifier.height(14.dp))
        AboutCard()
        Spacer(Modifier.height(22.dp))
        FooterNote("Pet Health Manager · Warm care, every day")
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun GuestCard(
    onLogin: () -> Unit,
    onRegister: () -> Unit
) {
    SectionCard(
        modifier = Modifier.padding(horizontal = 20.dp),
        padding = 20.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PawBadge(size = 52.dp, background = CreamDeep)
            Spacer(Modifier.size(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Guest mode",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Ink
                )
                Text(
                    text = "All pet features are available without an account.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkSoft
                )
            }
        }

        Spacer(Modifier.height(18.dp))
        GradientButton(text = "LOG IN", onClick = onLogin, height = 52.dp)
        Spacer(Modifier.height(10.dp))
        SoftButton(
            text = "CREATE ACCOUNT",
            onClick = onRegister,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
        HintNote("Accounts and pet data live in memory for now. They reset when the app process is cleared.")
    }
}

@Composable
private fun SignedInCard(
    username: String,
    onLogout: () -> Unit
) {
    SectionCard(
        modifier = Modifier.padding(horizontal = 20.dp),
        padding = 20.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Coral, Mango))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = username.take(1).uppercase(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
            }
            Spacer(Modifier.size(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = username,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Ink
                )
                Text(
                    text = "Signed in on this device",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkSoft
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        SoftButton(
            text = "LOG OUT",
            onClick = onLogout,
            accent = ErrorInk,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
        HintNote("Logging out does not delete your pets. They are shared across guest and signed-in sessions.")
    }
}

@Composable
private fun AboutCard() {
    SectionCard(
        modifier = Modifier.padding(horizontal = 20.dp),
        padding = 20.dp
    ) {
        Text(
            text = "About this app",
            style = MaterialTheme.typography.titleMedium,
            color = Ink
        )
        Spacer(Modifier.height(8.dp))
        LabelValueRow("Version", "1.0")
        LabelValueRow("Profile support", "Multiple pets")
        LabelValueRow("Reminders", "Local notifications")
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Built with Kotlin and Jetpack Compose. Health data stays on this device.",
            style = MaterialTheme.typography.bodyMedium,
            color = InkSoft
        )
    }
}
