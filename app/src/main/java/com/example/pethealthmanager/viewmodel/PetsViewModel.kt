package com.example.pethealthmanager.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pethealthmanager.data.ImageStorage
import com.example.pethealthmanager.data.model.Pet
import com.example.pethealthmanager.data.model.PetDraft
import com.example.pethealthmanager.data.repository.PetRepository
import com.example.pethealthmanager.notifications.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PetsViewModel(
    private val petRepository: PetRepository,
    private val imageStorage: ImageStorage,
    private val scheduler: ReminderScheduler
) : ViewModel() {

    val pets = petRepository.pets
    val selectedPetId = petRepository.selectedPetId

    val selectedPet: StateFlow<Pet?> = combine(pets, selectedPetId) { list, id ->
        list.firstOrNull { it.id == id } ?: list.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun selectPet(id: Long) = petRepository.selectPet(id)

    fun pet(id: Long): Pet? = petRepository.pet(id)

    fun addPet(draft: PetDraft) {
        petRepository.addPet(draft)
    }

    fun updatePet(pet: Pet) {
        val previousAvatar = petRepository.pet(pet.id)?.avatarPath
        petRepository.updatePet(pet)
        if (previousAvatar != null && previousAvatar != pet.avatarPath) {
            imageStorage.delete(previousAvatar)
        }
    }

    fun deletePet(pet: Pet) {
        scheduler.cancelForPet(pet.id)
        imageStorage.delete(pet.avatarPath)
        petRepository.deletePet(pet.id)
    }

    fun importImage(uri: Uri, onReady: (String?) -> Unit) {
        viewModelScope.launch {
            onReady(imageStorage.import(uri))
        }
    }
}
