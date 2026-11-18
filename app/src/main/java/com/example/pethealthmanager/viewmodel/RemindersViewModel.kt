package com.example.pethealthmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pethealthmanager.data.model.Pet
import com.example.pethealthmanager.data.model.Reminder
import com.example.pethealthmanager.data.model.ReminderDraft
import com.example.pethealthmanager.data.repository.PetRepository
import com.example.pethealthmanager.data.repository.ReminderRepository
import com.example.pethealthmanager.notifications.ReminderScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

class RemindersViewModel(
    private val petRepository: PetRepository,
    private val reminderRepository: ReminderRepository,
    private val scheduler: ReminderScheduler
) : ViewModel() {

    private val selectedPetId = petRepository.selectedPetId

    val selectedPet: StateFlow<Pet?> = combine(petRepository.pets, selectedPetId) { list, id ->
        list.firstOrNull { it.id == id } ?: list.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val reminders: StateFlow<List<Reminder>> = selectedPetId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else reminderRepository.observeReminders(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun reminder(id: Long): Reminder? = reminderRepository.reminder(id)

    fun addReminder(draft: ReminderDraft) {
        val petId = selectedPetId.value ?: return
        val petName = petRepository.pet(petId)?.name.orEmpty()
        val reminder = reminderRepository.addReminder(petId, draft)
        scheduler.schedule(reminder, petName)
    }

    fun updateReminder(id: Long, draft: ReminderDraft) {
        val updated = reminderRepository.updateReminder(id, draft) ?: return
        val petName = petRepository.pet(updated.petId)?.name.orEmpty()
        if (updated.enabled) scheduler.schedule(updated, petName) else scheduler.cancel(updated.id)
    }

    fun deleteReminder(reminder: Reminder) {
        scheduler.cancel(reminder.id)
        reminderRepository.deleteReminder(reminder.id)
    }

    fun markDone(reminder: Reminder) {
        val updated = reminderRepository.markDone(reminder.id, System.currentTimeMillis()) ?: return
        val petName = petRepository.pet(updated.petId)?.name.orEmpty()
        scheduler.schedule(updated, petName)
    }

    fun setEnabled(reminder: Reminder, enabled: Boolean) {
        val updated = reminderRepository.setEnabled(reminder.id, enabled) ?: return
        val petName = petRepository.pet(updated.petId)?.name.orEmpty()
        if (enabled) scheduler.schedule(updated, petName) else scheduler.cancel(reminder.id)
    }
}
