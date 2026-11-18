package com.example.pethealthmanager.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pethealthmanager.data.ImageStorage
import com.example.pethealthmanager.data.model.HealthEntry
import com.example.pethealthmanager.data.model.HealthEntryDraft
import com.example.pethealthmanager.data.model.HealthEntryType
import com.example.pethealthmanager.data.model.Pet
import com.example.pethealthmanager.data.repository.HealthRepository
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
import kotlinx.coroutines.launch

class HealthViewModel(
    private val petRepository: PetRepository,
    private val healthRepository: HealthRepository,
    private val reminderRepository: ReminderRepository,
    private val scheduler: ReminderScheduler,
    private val imageStorage: ImageStorage
) : ViewModel() {

    private val selectedPetId = petRepository.selectedPetId

    val selectedPet: StateFlow<Pet?> = combine(petRepository.pets, selectedPetId) { list, id ->
        list.firstOrNull { it.id == id } ?: list.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val entries: StateFlow<List<HealthEntry>> = selectedPetId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else healthRepository.observeEntries(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addEntry(draft: HealthEntryDraft) {
        val petId = selectedPetId.value ?: return
        healthRepository.addEntry(petId, draft)

        if (draft.type == HealthEntryType.WEIGHT && draft.amount != null && draft.amount > 0) {
            petRepository.updateWeight(petId, draft.amount)
        }

        if (draft.type == HealthEntryType.VACCINE || draft.type == HealthEntryType.DEWORMING) {
            val petName = petRepository.pet(petId)?.name.orEmpty()
            reminderRepository
                .applyMedicalRecord(petId, draft.type, draft.occurredAt)
                .forEach { scheduler.schedule(it, petName) }
        }
    }

    fun deleteEntry(entry: HealthEntry) {
        imageStorage.delete(entry.imagePath)
        healthRepository.deleteEntry(entry.id)
    }

    fun importImage(uri: Uri, onReady: (String?) -> Unit) {
        viewModelScope.launch {
            onReady(imageStorage.import(uri))
        }
    }
}
