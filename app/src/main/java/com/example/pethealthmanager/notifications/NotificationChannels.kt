package com.example.pethealthmanager.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationChannels {

    const val REMINDERS = "pet_care_reminders"

    fun ensure(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        val channel = NotificationChannel(
            REMINDERS,
            "Pet care reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Vaccine, deworming, medication and custom care reminders"
        }
        manager.createNotificationChannel(channel)
    }
}
