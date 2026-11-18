package com.example.pethealthmanager.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pethealthmanager.AuthViewModel
import com.example.pethealthmanager.ui.components.CheckBadge
import com.example.pethealthmanager.ui.components.DogIllustration
import com.example.pethealthmanager.ui.components.FooterNote
import com.example.pethealthmanager.ui.components.GradientButton
import com.example.pethealthmanager.ui.components.PawBadge
import com.example.pethealthmanager.ui.components.drawPaw
import com.example.pethealthmanager.ui.theme.Coral
import com.example.pethealthmanager.ui.theme.Cream
import com.example.pethealthmanager.ui.theme.CreamDeep
import com.example.pethealthmanager.ui.theme.Ink
import com.example.pethealthmanager.ui.theme.InkSoft
import com.example.pethealthmanager.ui.theme.Mango
import com.example.pethealthmanager.ui.theme.Mint

@Composable
fun SuccessScreen(vm: AuthViewModel, onContinue: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Cream, Color(0xFFFFE7CE))))
    ) {
        SuccessBackdrop(Modifier.matchParentSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            DogIllustration(Modifier.size(236.dp, 200.dp))
            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CheckBadge(Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "LOGIN SUCCESSFUL",
                    style = MaterialTheme.typography.labelSmall,
                    color = Mint
                )
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = "Welcome, ${vm.loggedInName}!",
                style = MaterialTheme.typography.displaySmall,
                fontSize = 34.sp,
                lineHeight = 40.sp,
                color = Ink,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Your pet's health dashboard is ready.",
                style = MaterialTheme.typography.bodyLarge,
                color = InkSoft,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))
            CareCard()

            Spacer(Modifier.height(26.dp))
            GradientButton(text = "CONTINUE", onClick = onContinue)

            Spacer(Modifier.height(12.dp))
            FooterNote("Pet Health Manager · Warm care, every day")
            Spacer(Modifier.height(18.dp))
        }
    }
}

@Composable
private fun CareCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        shadowElevation = 10.dp
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PawBadge(size = 34.dp, background = CreamDeep)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = "You're ready to go",
                        style = MaterialTheme.typography.titleMedium,
                        color = Ink
                    )
                    Text(
                        text = "Your pets stay on this device",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkSoft
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            CareRow("Pet profiles", "Multiple companions", Mint)
            CareRow("Health log", "Daily and medical", Mango)
            CareRow("Smart reminders", "Vaccines and doses", Coral)
        }
    }
}

@Composable
private fun CareRow(title: String, detail: String, dotColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = Ink,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = detail,
            style = MaterialTheme.typography.bodyMedium,
            color = InkSoft
        )
    }
}

@Composable
private fun SuccessBackdrop(modifier: Modifier) {
    Canvas(modifier) {
        drawCircle(
            color = Coral.copy(alpha = 0.10f),
            radius = size.width * 0.40f,
            center = Offset(size.width * 0.98f, size.height * 0.06f)
        )
        drawCircle(
            color = Mango.copy(alpha = 0.18f),
            radius = size.width * 0.30f,
            center = Offset(size.width * 0.02f, size.height * 0.92f)
        )
        drawPaw(
            center = Offset(size.width * 0.10f, size.height * 0.24f),
            radius = size.width * 0.045f,
            color = Coral.copy(alpha = 0.16f),
            rotation = 18f
        )
        drawPaw(
            center = Offset(size.width * 0.90f, size.height * 0.42f),
            radius = size.width * 0.038f,
            color = Mango.copy(alpha = 0.45f),
            rotation = -12f
        )
        drawPaw(
            center = Offset(size.width * 0.16f, size.height * 0.66f),
            radius = size.width * 0.035f,
            color = Coral.copy(alpha = 0.12f),
            rotation = 8f
        )
        drawPaw(
            center = Offset(size.width * 0.82f, size.height * 0.84f),
            radius = size.width * 0.05f,
            color = Coral.copy(alpha = 0.10f),
            rotation = -20f
        )
    }
}
