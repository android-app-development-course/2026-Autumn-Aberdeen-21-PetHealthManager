package com.example.pethealthmanager.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.pethealthmanager.ui.theme.Blush
import com.example.pethealthmanager.ui.theme.Coral
import com.example.pethealthmanager.ui.theme.FurTan
import com.example.pethealthmanager.ui.theme.FurTanDeep
import com.example.pethealthmanager.ui.theme.Ink
import com.example.pethealthmanager.ui.theme.Mango
import com.example.pethealthmanager.ui.theme.Mint

/** Draws a stylised paw print. Used for logos and decorative confetti. */
fun DrawScope.drawPaw(
    center: Offset,
    radius: Float,
    color: Color,
    rotation: Float = 0f
) {
    rotate(degrees = rotation, pivot = center) {
        drawOval(
            color = color,
            topLeft = Offset(center.x - radius * 0.52f, center.y - radius * 0.06f),
            size = Size(radius * 1.04f, radius * 0.92f)
        )
        val toes = listOf(
            Offset(-0.66f, -0.34f),
            Offset(-0.25f, -0.78f),
            Offset(0.25f, -0.78f),
            Offset(0.66f, -0.34f)
        )
        toes.forEach { (dx, dy) ->
            drawOval(
                color = color,
                topLeft = Offset(
                    center.x + dx * radius - radius * 0.23f,
                    center.y + dy * radius - radius * 0.27f
                ),
                size = Size(radius * 0.46f, radius * 0.54f)
            )
        }
    }
}

@Composable
fun PawPrint(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        drawPaw(
            center = Offset(size.width / 2f, size.height * 0.55f),
            radius = size.minDimension * 0.47f,
            color = color
        )
    }
}

/** Rounded-square logo tile with a paw inside. */
@Composable
fun PawBadge(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    background: Color = Color.White,
    pawColor: Color = Coral
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.32f))
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        PawPrint(color = pawColor, modifier = Modifier.size(size * 0.62f))
    }
}

/** Warm gradient header with organic bottom edge, circles and paw confetti. */
@Composable
fun HeroBackdrop(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val blob = Path().apply {
            moveTo(0f, 0f)
            lineTo(w, 0f)
            lineTo(w, h * 0.94f)
            cubicTo(w * 0.80f, h * 0.86f, w * 0.62f, h * 1.00f, w * 0.42f, h * 0.94f)
            cubicTo(w * 0.26f, h * 0.90f, w * 0.10f, h * 1.00f, 0f, h * 0.96f)
            close()
        }
        drawPath(
            path = blob,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFF7C50), Color(0xFFFF9E3F), Color(0xFFFFB532)),
                endY = h
            )
        )
        // Decorations are clipped to the blob so nothing spills onto the cream paper.
        clipPath(blob) {
            drawCircle(
                color = Color.White.copy(alpha = 0.10f),
                radius = w * 0.42f,
                center = Offset(w * 0.94f, h * 0.02f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                radius = w * 0.16f,
                center = Offset(w * 0.10f, h * 0.88f)
            )
            drawPaw(
                center = Offset(w * 0.88f, h * 0.42f),
                radius = size.minDimension * 0.085f,
                color = Color.White.copy(alpha = 0.20f),
                rotation = -16f
            )
            drawPaw(
                center = Offset(w * 0.09f, h * 0.30f),
                radius = size.minDimension * 0.055f,
                color = Color.White.copy(alpha = 0.16f),
                rotation = 14f
            )
            drawPaw(
                center = Offset(w * 0.70f, h * 0.12f),
                radius = size.minDimension * 0.045f,
                color = Color.White.copy(alpha = 0.14f),
                rotation = -6f
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.50f),
                radius = 2.4f,
                center = Offset(w * 0.64f, h * 0.30f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.35f),
                radius = 1.8f,
                center = Offset(w * 0.30f, h * 0.44f)
            )
        }
    }
}

/**
 * The mascot: a friendly sitting puppy drawn entirely with Canvas primitives,
 * so the app needs no bitmap assets.
 */
@Composable
fun DogIllustration(
    modifier: Modifier = Modifier,
    showHeart: Boolean = true
) {
    Canvas(modifier) {
        val k = minOf(size.width / 300f, size.height / 250f)
        translate(
            left = (size.width - 300f * k) / 2f,
            top = (size.height - 250f * k) / 2f
        ) {
            scale(scaleX = k, scaleY = k, pivot = Offset.Zero) {
                drawDog(showHeart)
            }
        }
    }
}

private fun DrawScope.drawDog(showHeart: Boolean) {
    drawOval(Color(0x14000000), topLeft = Offset(62f, 224f), size = Size(196f, 22f))

    val tail = Path().apply {
        moveTo(220f, 200f)
        quadraticTo(266f, 206f, 272f, 172f)
        quadraticTo(274f, 154f, 258f, 150f)
    }
    drawPath(tail, FurTanDeep, style = Stroke(width = 18f, cap = StrokeCap.Round))

    drawOval(FurTan, topLeft = Offset(82f, 148f), size = Size(150f, 94f))
    drawOval(Color(0xFFFFE6C9), topLeft = Offset(112f, 178f), size = Size(90f, 50f))

    drawRoundRect(
        color = FurTanDeep.copy(alpha = 0.55f),
        topLeft = Offset(104f, 212f),
        size = Size(34f, 30f),
        cornerRadius = CornerRadius(15f)
    )
    drawRoundRect(
        color = FurTanDeep.copy(alpha = 0.55f),
        topLeft = Offset(176f, 212f),
        size = Size(34f, 30f),
        cornerRadius = CornerRadius(15f)
    )

    rotate(-20f, pivot = Offset(92f, 118f)) {
        drawOval(FurTanDeep, topLeft = Offset(44f, 40f), size = Size(52f, 92f))
    }
    rotate(20f, pivot = Offset(208f, 118f)) {
        drawOval(FurTanDeep, topLeft = Offset(204f, 40f), size = Size(52f, 92f))
    }

    drawCircle(FurTan, radius = 58f, center = Offset(150f, 98f))
    drawOval(Color(0xFFFFEEDB), topLeft = Offset(114f, 94f), size = Size(72f, 60f))

    drawCircle(Ink, radius = 7f, center = Offset(126f, 84f))
    drawCircle(Ink, radius = 7f, center = Offset(174f, 84f))
    drawCircle(Color.White, radius = 2.5f, center = Offset(123.5f, 81.5f))
    drawCircle(Color.White, radius = 2.5f, center = Offset(171.5f, 81.5f))

    drawCircle(Blush.copy(alpha = 0.45f), radius = 9f, center = Offset(103f, 114f))
    drawCircle(Blush.copy(alpha = 0.45f), radius = 9f, center = Offset(197f, 114f))

    drawOval(Color(0xFF4A312A), topLeft = Offset(139f, 104f), size = Size(22f, 18f))

    val smile = Path().apply {
        moveTo(150f, 122f)
        lineTo(150f, 131f)
        moveTo(150f, 131f)
        quadraticTo(141f, 142f, 132f, 136f)
        moveTo(150f, 131f)
        quadraticTo(159f, 142f, 168f, 136f)
    }
    drawPath(smile, Color(0xFF4A312A), style = Stroke(width = 4f, cap = StrokeCap.Round))

    drawRoundRect(
        color = Blush,
        topLeft = Offset(141f, 138f),
        size = Size(18f, 14f),
        cornerRadius = CornerRadius(7f)
    )

    drawRoundRect(
        color = Coral,
        topLeft = Offset(104f, 152f),
        size = Size(92f, 20f),
        cornerRadius = CornerRadius(10f)
    )
    drawCircle(Mango, radius = 10f, center = Offset(150f, 182f))
    drawCircle(Color.White.copy(alpha = 0.6f), radius = 3.5f, center = Offset(147f, 179f))

    if (showHeart) {
        drawPath(heartPath(Offset(252f, 56f), 24f), Coral)
        drawPath(heartPath(Offset(274f, 84f), 13f), Color.White.copy(alpha = 0.95f))
        drawCircle(Color.White.copy(alpha = 0.7f), radius = 2.2f, center = Offset(238f, 96f))
    }
}

private fun heartPath(center: Offset, size: Float): Path {
    val h = size
    return Path().apply {
        moveTo(center.x, center.y + h * 0.32f)
        cubicTo(
            center.x - h * 0.95f, center.y - h * 0.10f,
            center.x - h * 0.55f, center.y - h * 0.72f,
            center.x, center.y - h * 0.25f
        )
        cubicTo(
            center.x + h * 0.55f, center.y - h * 0.72f,
            center.x + h * 0.95f, center.y - h * 0.10f,
            center.x, center.y + h * 0.32f
        )
        close()
    }
}

@Composable
fun CheckBadge(modifier: Modifier = Modifier, size: Dp = 20.dp) {
    Canvas(modifier.size(size)) {
        val w = this.size.width
        drawCircle(Mint)
        val check = Path().apply {
            moveTo(w * 0.28f, w * 0.52f)
            lineTo(w * 0.44f, w * 0.67f)
            lineTo(w * 0.73f, w * 0.35f)
        }
        drawPath(
            path = check,
            color = Color.White,
            style = Stroke(width = w * 0.10f, cap = StrokeCap.Round)
        )
    }
}
