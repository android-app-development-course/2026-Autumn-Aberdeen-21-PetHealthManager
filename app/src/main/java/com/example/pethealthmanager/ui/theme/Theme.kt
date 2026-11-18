package com.example.pethealthmanager.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Warm, high-saturation brand colours.
val Coral = Color(0xFFFF6B4A)
val CoralDeep = Color(0xFFE8502F)
val Mango = Color(0xFFFFB020)
val Apricot = Color(0xFFFFD9AE)
val Cream = Color(0xFFFFF7EE)
val CreamDeep = Color(0xFFFFEEDC)
val Ink = Color(0xFF3B2A22)
val InkSoft = Color(0xFF8C6F61)
val Mint = Color(0xFF2FAE72)
val MintSoft = Color(0xFFDEF5E9)
val Blush = Color(0xFFFF8FA3)
val ErrorSoft = Color(0xFFFFE7E0)
val ErrorInk = Color(0xFFC0392B)
val FurTan = Color(0xFFF3B87E)
val FurTanDeep = Color(0xFFD08A4B)
val Sky = Color(0xFF4E9FD1)
val Lavender = Color(0xFF8B7FD4)
val Berry = Color(0xFFD4678A)

private val PetColorScheme = lightColorScheme(
    primary = Coral,
    onPrimary = Color.White,
    primaryContainer = Apricot,
    onPrimaryContainer = Ink,
    secondary = Mango,
    onSecondary = Ink,
    background = Cream,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = CreamDeep,
    onSurfaceVariant = InkSoft,
    error = ErrorInk,
    onError = Color.White,
    outline = Apricot
)

private val PetTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 30.sp,
        lineHeight = 34.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 26.sp,
        lineHeight = 30.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 19.sp,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 19.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        letterSpacing = 1.2.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 10.5.sp,
        letterSpacing = 1.4.sp
    )
)

private val PetShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(30.dp)
)

@Composable
fun PetHealthTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PetColorScheme,
        typography = PetTypography,
        shapes = PetShapes,
        content = content
    )
}
