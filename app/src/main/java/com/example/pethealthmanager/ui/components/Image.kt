package com.example.pethealthmanager.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.pethealthmanager.data.model.Pet
import com.example.pethealthmanager.ui.theme.Coral
import com.example.pethealthmanager.ui.theme.CreamDeep
import com.example.pethealthmanager.ui.theme.Mango
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun LocalImage(
    path: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val image by produceState<ImageBitmap?>(initialValue = null, key1 = path) {
        value = if (path.isNullOrBlank()) {
            null
        } else {
            withContext(Dispatchers.IO) { decodeSampled(path)?.asImageBitmap() }
        }
    }
    val bitmap = image
    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Box(modifier.background(CreamDeep))
    }
}

@Composable
fun PetAvatar(
    pet: Pet?,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(size * 0.34f)
    val path = pet?.avatarPath
    if (path != null) {
        LocalImage(
            path = path,
            contentDescription = pet?.name,
            modifier = modifier.size(size).clip(shape)
        )
    } else {
        Box(
            modifier = modifier
                .size(size)
                .clip(shape)
                .background(Brush.linearGradient(listOf(Coral, Mango))),
            contentAlignment = Alignment.Center
        ) {
            PawPrint(color = Color.White, modifier = Modifier.size(size * 0.55f))
        }
    }
}

private fun decodeSampled(path: String, maxSize: Int = 900): Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(path, bounds)
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

    var sample = 1
    while (bounds.outWidth / sample > maxSize || bounds.outHeight / sample > maxSize) {
        sample *= 2
    }
    return BitmapFactory.decodeFile(
        path,
        BitmapFactory.Options().apply { inSampleSize = sample }
    )
}
