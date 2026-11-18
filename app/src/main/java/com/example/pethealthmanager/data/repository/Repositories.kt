package com.example.pethealthmanager.data.repository

import com.example.pethealthmanager.data.model.HealthEntry
import com.example.pethealthmanager.data.model.HealthEntryDraft
import com.example.pethealthmanager.data.model.HealthEntryType
import com.example.pethealthmanager.data.model.Pet
import com.example.pethealthmanager.data.model.PetDraft
import com.example.pethealthmanager.data.model.Reminder
import com.example.pethealthmanager.data.model.ReminderDraft
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

sealed interface AuthResult {
    data object Success : AuthResult
    data object UserExists : AuthResult
    data object UnknownUser : AuthResult
    data object WrongPassword : AuthResult
}

interface AccountRepository {
    val currentUser: StateFlow<String?>

    fun register(username: String, password: String): AuthResult
    fun authenticate(username: String, password: String): AuthResult
    fun logout()
}

interface PetRepository {
    val pets: StateFlow<List<Pet>>
    val selectedPetId: StateFlow<Long?>

    fun pet(id: Long): Pet?
    fun selectPet(id: Long)
    fun addPet(draft: PetDraft): Pet
    fun updatePet(pet: Pet)
    fun deletePet(id: Long)
    fun updateWeight(petId: Long, weightKg: Double)
}

interface HealthRepository {
    fun observeEntries(petId: Long): Flow<List<HealthEntry>>
    fun addEntry(petId: Long, draft: HealthEntryDraft): HealthEntry
    fun deleteEntry(id: Long)
}

interface ReminderRepository {
    fun observeReminders(petId: Long): Flow<List<Reminder>>
    fun reminder(id: Long): Reminder?
    fun addReminder(petId: Long, draft: ReminderDraft): Reminder
    fun updateReminder(id: Long, draft: ReminderDraft): Reminder?
    fun deleteReminder(id: Long)
    fun markDone(id: Long, doneAt: Long): Reminder?
    fun setEnabled(id: Long, enabled: Boolean): Reminder?
    fun applyMedicalRecord(petId: Long, type: HealthEntryType, occurredAt: Long): List<Reminder>
}
