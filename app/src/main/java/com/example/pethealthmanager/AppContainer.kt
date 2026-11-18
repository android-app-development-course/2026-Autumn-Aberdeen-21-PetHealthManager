package com.example.pethealthmanager

import android.content.Context
import com.example.pethealthmanager.data.ImageStorage
import com.example.pethealthmanager.data.repository.AccountRepository
import com.example.pethealthmanager.data.repository.HealthRepository
import com.example.pethealthmanager.data.repository.InMemoryRepository
import com.example.pethealthmanager.data.repository.PetRepository
import com.example.pethealthmanager.data.repository.ReminderRepository
import com.example.pethealthmanager.notifications.ReminderScheduler

/**
 * Hand-written singleton container. Swap the backing store for Room later
 * without touching any screen or ViewModel.
 */
class AppContainer(context: Context) {

    private val store = InMemoryRepository()

    val accountRepository: AccountRepository = store
    val petRepository: PetRepository = store
    val healthRepository: HealthRepository = store
    val reminderRepository: ReminderRepository = store

    val imageStorage = ImageStorage(context.applicationContext)
    val reminderScheduler = ReminderScheduler(context.applicationContext)
}
