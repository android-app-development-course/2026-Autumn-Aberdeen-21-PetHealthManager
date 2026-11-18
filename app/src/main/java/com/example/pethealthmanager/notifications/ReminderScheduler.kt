package com.example.pethealthmanager.notifications

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.pethealthmanager.data.model.Reminder
import java.util.concurrent.TimeUnit

class ReminderScheduler(private val context: Context) {

    fun schedule(reminder: Reminder, petName: String) {
        NotificationChannels.ensure(context)
        val workManager = WorkManager.getInstance(context)
        if (!reminder.enabled) {
            workManager.cancelUniqueWork(workName(reminder.id))
            return
        }
        val delay = (reminder.nextDueAt - System.currentTimeMillis()).coerceAtLeast(0L)
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag(petTag(reminder.petId))
            .setInputData(
                workDataOf(
                    ReminderWorker.KEY_NOTIFICATION_ID to reminder.id.toInt(),
                    ReminderWorker.KEY_TITLE to reminder.title,
                    ReminderWorker.KEY_PET to petName,
                    ReminderWorker.KEY_KIND to reminder.kind.label,
                    ReminderWorker.KEY_DOSE to reminder.dose
                )
            )
            .build()
        workManager.enqueueUniqueWork(workName(reminder.id), ExistingWorkPolicy.REPLACE, request)
    }

    fun cancel(reminderId: Long) {
        WorkManager.getInstance(context).cancelUniqueWork(workName(reminderId))
    }

    fun cancelForPet(petId: Long) {
        WorkManager.getInstance(context).cancelAllWorkByTag(petTag(petId))
    }

    private fun workName(reminderId: Long) = "reminder_$reminderId"

    private fun petTag(petId: Long) = "pet_$petId"
}
