package com.example.pethealthmanager.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.pethealthmanager.ui.theme.Coral
import com.example.pethealthmanager.ui.theme.CreamDeep
import com.example.pethealthmanager.ui.theme.InkSoft
import com.example.pethealthmanager.util.Dates

@Composable
fun WeightChart(
    points: List<Pair<Long, Double>>,
    modifier: Modifier = Modifier
) {
    val sorted = points.sortedBy { it.first }
    Column(modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
        ) {
            if (sorted.isEmpty()) return@Canvas

            val left = 6.dp.toPx()
            val right = size.width - 6.dp.toPx()
            val top = 12.dp.toPx()
            val bottom = size.height - 6.dp.toPx()

            val minValue = sorted.minOf { it.second }
            val maxValue = sorted.maxOf { it.second }
            val spread = (maxValue - minValue).coerceAtLeast(0.4)
            val low = minValue - spread * 0.22
            val high = maxValue + spread * 0.22

            fun xAt(index: Int): Float = if (sorted.size == 1) {
                (left + right) / 2f
            } else {
                left + (right - left) * index / (sorted.size - 1)
            }

            fun yAt(value: Double): Float =
                bottom - ((value - low) / (high - low) * (bottom - top)).toFloat()

            for (step in 0..3) {
                val y = top + (bottom - top) * step / 3f
                drawLine(
                    color = CreamDeep,
                    start = Offset(left, y),
                    end = Offset(right, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val line = Path()
            sorted.forEachIndexed { index, (_, value) ->
                if (index == 0) line.moveTo(xAt(index), yAt(value))
                else line.lineTo(xAt(index), yAt(value))
            }

            val fill = Path().apply {
                addPath(line)
                lineTo(xAt(sorted.lastIndex), bottom)
                lineTo(xAt(0), bottom)
                close()
            }

            drawPath(
                path = fill,
                brush = Brush.verticalGradient(
                    colors = listOf(Coral.copy(alpha = 0.28f), Color.Transparent),
                    startY = top,
                    endY = bottom
                )
            )
            drawPath(
                path = line,
                color = Coral,
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            sorted.forEachIndexed { index, (_, value) ->
                val center = Offset(xAt(index), yAt(value))
                drawCircle(Color.White, radius = 6.dp.toPx(), center = center)
                drawCircle(Coral, radius = 3.5.dp.toPx(), center = center)
            }
        }

        if (sorted.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = Dates.formatShortDate(sorted.first().first),
                    style = MaterialTheme.typography.labelSmall,
                    color = InkSoft
                )
                Text(
                    text = Dates.formatShortDate(sorted.last().first),
                    style = MaterialTheme.typography.labelSmall,
                    color = InkSoft
                )
            }
        }
    }
}

@Composable
fun SymptomBar(
    label: String,
    count: Int,
    maxCount: Int,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = InkSoft,
            modifier = Modifier.width(78.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(10.dp)
                .clip(RoundedCornerShape(50))
                .background(CreamDeep)
        ) {
            if (count > 0 && maxCount > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(count.toFloat() / maxCount)
                        .clip(RoundedCornerShape(50))
                        .background(accent)
                )
            }
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodyLarge,
            color = if (count > 0) accent else InkSoft,
            modifier = Modifier.width(22.dp)
        )
    }
}
