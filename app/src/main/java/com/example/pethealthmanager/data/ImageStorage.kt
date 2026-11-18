package com.example.pethealthmanager.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class ImageStorage(private val context: Context) {

    private val directory: File
        get() = File(context.filesDir, "pet_images").apply { mkdirs() }

    suspend fun import(uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val target = File(directory, "img_${UUID.randomUUID()}.jpg")
            val stream = context.contentResolver.openInputStream(uri) ?: return@withContext null
            stream.use { input ->
                target.outputStream().use { output -> input.copyTo(output) }
            }
            if (target.length() == 0L) {
                target.delete()
                null
            } else {
                target.absolutePath
            }
        } catch (_: Exception) {
            null
        }
    }

    fun delete(path: String?) {
        if (path.isNullOrBlank()) return
        try {
            File(path).takeIf { it.exists() }?.delete()
        } catch (_: Exception) {
            // A missing image must never block profile editing.
        }
    }
}
