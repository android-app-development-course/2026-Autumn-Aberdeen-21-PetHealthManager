package com.example.pethealthmanager.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pethealthmanager.MainActivity
import com.example.pethealthmanager.R

/**
 * Self-contained on purpose: the in-memory repository may be empty after a
 * process restart, so everything the notification needs travels in inputData.
 */
class ReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    companion object {
        const val KEY_NOTIFICATION_ID = "notification_id"
        const val KEY_TITLE = "title"
        const val KEY_PET = "pet_name"
        const val KEY_KIND = "kind"
        const val KEY_DOSE = "dose"
    }

    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        val notificationId = inputData.getInt(KEY_NOTIFICATION_ID, 0)
        val title = inputData.getString(KEY_TITLE)?.takeIf { it.isNotBlank() } ?: return Result.success()
        val petName = inputData.getString(KEY_PET).orEmpty()
        val kind = inputData.getString(KEY_KIND).orEmpty()
        val dose = inputData.getString(KEY_DOSE).orEmpty()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        NotificationChannels.ensure(applicationContext)
        val manager = NotificationManagerCompat.from(applicationContext)
        if (!manager.areNotificationsEnabled()) return Result.success()

        val headline = when {
            petName.isNotBlank() && kind.isNotBlank() -> "$petName · $kind"
            petName.isNotBlank() -> petName
            else -> "Pet care reminder"
        }
        val body = if (dose.isNotBlank()) "$title · $dose" else title

        val contentIntent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            notificationId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, NotificationChannels.REMINDERS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(headline)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(notificationId, notification)
        return Result.success()
    }
}
