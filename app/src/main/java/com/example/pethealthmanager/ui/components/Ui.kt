package com.example.pethealthmanager.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pethealthmanager.ui.theme.Apricot
import com.example.pethealthmanager.ui.theme.Coral
import com.example.pethealthmanager.ui.theme.CoralDeep
import com.example.pethealthmanager.ui.theme.ErrorInk
import com.example.pethealthmanager.ui.theme.ErrorSoft
import com.example.pethealthmanager.ui.theme.Ink
import com.example.pethealthmanager.ui.theme.InkSoft
import com.example.pethealthmanager.ui.theme.Mint
import com.example.pethealthmanager.ui.theme.MintSoft

/** Big rounded call-to-action with a warm gradient and coral glow. */
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 58.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .shadow(
                elevation = if (enabled) 14.dp else 0.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Coral,
                spotColor = Coral
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (enabled) {
                    Brush.horizontalGradient(listOf(CoralDeep, Coral, Color(0xFFFF9A3C)))
                } else {
                    Brush.horizontalGradient(listOf(Color(0xFFEDE0D4), Color(0xFFEDE0D4)))
                }
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (enabled) Color.White else Color(0xFFA8968A),
            style = MaterialTheme.typography.labelLarge,
            fontSize = 16.sp
        )
    }
}

/**
 * Labelled text field with warm outline, password visibility toggle
 * and keyboard "next"/"done" handling.
 */
@Composable
fun PetTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isPassword: Boolean = false,
    imeAction: ImeAction = ImeAction.Next,
    onDone: () -> Unit = {},
    keyboardType: KeyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else 5
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = InkSoft)
        Spacer(Modifier.height(7.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            shape = RoundedCornerShape(16.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Ink),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFB9A79C)
                )
            },
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            trailingIcon = if (isPassword) {
                {
                    TextButton(
                        onClick = { passwordVisible = !passwordVisible },
                        contentPadding = PaddingValues(horizontal = 10.dp)
                    ) {
                        Text(
                            text = if (passwordVisible) "HIDE" else "SHOW",
                            style = MaterialTheme.typography.labelSmall,
                            color = Coral
                        )
                    }
                }
            } else {
                null
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                onDone = {
                    focusManager.clearFocus()
                    onDone()
                }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Coral,
                unfocusedBorderColor = Apricot,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Coral,
                focusedTextColor = Ink,
                unfocusedTextColor = Ink
            )
        )
    }
}

/** Rounded status banner that animates in for errors and confirmations. */
@Composable
fun MessageBanner(
    message: String?,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = !message.isNullOrBlank(),
        enter = expandVertically(spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn(),
        exit = shrinkVertically(tween(160)) + fadeOut(tween(120)),
        modifier = modifier
    ) {
        Column {
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isError) ErrorSoft else MintSoft)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusDot(isError = isError, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(10.dp))
                Text(
                    text = message.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isError) ErrorInk else Color(0xFF1E7D52)
                )
            }
        }
    }
}

@Composable
private fun StatusDot(isError: Boolean, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        if (isError) {
            drawCircle(ErrorInk)
            drawLine(
                color = Color.White,
                start = Offset(w * 0.5f, w * 0.26f),
                end = Offset(w * 0.5f, w * 0.60f),
                strokeWidth = w * 0.11f,
                cap = StrokeCap.Round
            )
            drawCircle(Color.White, radius = w * 0.065f, center = Offset(w * 0.5f, w * 0.75f))
        } else {
            drawCircle(Mint)
            val check = Path().apply {
                moveTo(w * 0.28f, w * 0.52f)
                lineTo(w * 0.44f, w * 0.67f)
                lineTo(w * 0.73f, w * 0.35f)
            }
            drawPath(check, Color.White, style = Stroke(width = w * 0.10f, cap = StrokeCap.Round))
        }
    }
}

/** Paw-shaped attempt counter shown after a failed login. */
@Composable
fun AttemptsHint(
    attemptsLeft: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        repeat(total) { index ->
            PawPrint(
                color = if (index < attemptsLeft) Coral else Color(0xFFEADCCF),
                modifier = Modifier.size(14.dp)
            )
            if (index < total - 1) Spacer(Modifier.width(4.dp))
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text = if (attemptsLeft == 0) {
                "No attempts left"
            } else {
                "$attemptsLeft of $total attempts left"
            },
            style = MaterialTheme.typography.labelSmall,
            color = if (attemptsLeft == 0) ErrorInk else InkSoft
        )
    }
}

/** Logo + brand name + headline text that sits on top of the hero gradient. */
@Composable
fun HeroIntro(
    kicker: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PawBadge(size = 42.dp)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = "Pet Health Manager",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = kicker,
                    color = Color.White.copy(alpha = 0.80f),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        Spacer(Modifier.height(22.dp))
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.displaySmall,
            fontSize = 31.sp,
            lineHeight = 35.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = subtitle,
            color = Color.White.copy(alpha = 0.88f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun FooterNote(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PawPrint(Coral.copy(alpha = 0.55f), Modifier.size(13.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = InkSoft.copy(alpha = 0.85f),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.width(8.dp))
        PawPrint(Coral.copy(alpha = 0.55f), Modifier.size(13.dp))
    }
}
