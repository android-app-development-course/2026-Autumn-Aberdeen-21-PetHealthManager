package com.example.pethealthmanager.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.pethealthmanager.AuthViewModel
import com.example.pethealthmanager.PetHealthApp
import com.example.pethealthmanager.ui.components.AppBottomBar
import com.example.pethealthmanager.ui.screens.health.AddRecordScreen
import com.example.pethealthmanager.ui.screens.health.HealthScreen
import com.example.pethealthmanager.ui.screens.pets.PetEditorScreen
import com.example.pethealthmanager.ui.screens.pets.PetsScreen
import com.example.pethealthmanager.ui.screens.profile.ProfileScreen
import com.example.pethealthmanager.ui.screens.reminders.ReminderEditorScreen
import com.example.pethealthmanager.ui.screens.reminders.RemindersScreen
import com.example.pethealthmanager.ui.theme.Cream
import com.example.pethealthmanager.viewmodel.AppScreen
import com.example.pethealthmanager.viewmodel.AppViewModel
import com.example.pethealthmanager.viewmodel.HealthViewModel
import com.example.pethealthmanager.viewmodel.HomeTab
import com.example.pethealthmanager.viewmodel.PetsViewModel
import com.example.pethealthmanager.viewmodel.RemindersViewModel

@Composable
fun AppRoot() {
    val context = LocalContext.current
    val container = remember(context) {
        (context.applicationContext as PetHealthApp).container
    }

    val appVm: AppViewModel = viewModel()
    val authVm: AuthViewModel = viewModel(
        factory = viewModelFactory {
            initializer { AuthViewModel(container.accountRepository) }
        }
    )
    val petsVm: PetsViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                PetsViewModel(
                    petRepository = container.petRepository,
                    imageStorage = container.imageStorage,
                    scheduler = container.reminderScheduler
                )
            }
        }
    )
    val healthVm: HealthViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                HealthViewModel(
                    petRepository = container.petRepository,
                    healthRepository = container.healthRepository,
                    reminderRepository = container.reminderRepository,
                    scheduler = container.reminderScheduler,
                    imageStorage = container.imageStorage
                )
            }
        }
    )
    val remindersVm: RemindersViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                RemindersViewModel(
                    petRepository = container.petRepository,
                    reminderRepository = container.reminderRepository,
                    scheduler = container.reminderScheduler
                )
            }
        }
    )

    if (authVm.authActive) {
        AuthFlow(authVm)
        return
    }

    val screen = appVm.screen
    BackHandler(enabled = screen != null) { appVm.back() }

    if (screen != null) {
        when (screen) {
            is AppScreen.PetEditor -> {
                val existing = screen.petId?.let { petsVm.pet(it) }
                PetEditorScreen(
                    existing = existing,
                    onBack = appVm::back,
                    onSave = { draft ->
                        if (existing == null) {
                            petsVm.addPet(draft)
                        } else {
                            petsVm.updatePet(
                                existing.copy(
                                    name = draft.name,
                                    species = draft.species,
                                    breed = draft.breed,
                                    gender = draft.gender,
                                    birthDate = draft.birthDate,
                                    weightKg = draft.weightKg,
                                    avatarPath = draft.avatarPath,
                                    adoptionDate = draft.adoptionDate,
                                    notes = draft.notes
                                )
                            )
                        }
                        appVm.back()
                    },
                    onImportImage = petsVm::importImage
                )
            }

            is AppScreen.RecordEditor -> {
                val pet by healthVm.selectedPet.collectAsStateWithLifecycle()
                AddRecordScreen(
                    pet = pet,
                    defaultType = screen.defaultType,
                    onBack = appVm::back,
                    onSave = { draft ->
                        healthVm.addEntry(draft)
                        appVm.back()
                    },
                    onImportImage = healthVm::importImage
                )
            }

            is AppScreen.ReminderEditor -> {
                val pet by remindersVm.selectedPet.collectAsStateWithLifecycle()
                val existing = screen.reminderId?.let { remindersVm.reminder(it) }
                ReminderEditorScreen(
                    pet = pet,
                    existing = existing,
                    onBack = appVm::back,
                    onSave = { draft ->
                        if (existing == null) {
                            remindersVm.addReminder(draft)
                        } else {
                            remindersVm.updateReminder(existing.id, draft)
                        }
                        appVm.back()
                    }
                )
            }
        }
        return
    }

    val pets by petsVm.pets.collectAsStateWithLifecycle()
    val selectedPetId by petsVm.selectedPetId.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Cream,
        bottomBar = {
            AppBottomBar(selected = appVm.tab, onSelect = appVm::selectTab)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (appVm.tab) {
                HomeTab.PETS -> PetsScreen(
                    viewModel = petsVm,
                    pets = pets,
                    selectedPetId = selectedPetId,
                    onAddPet = { appVm.open(AppScreen.PetEditor(null)) },
                    onEditPet = { id -> appVm.open(AppScreen.PetEditor(id)) }
                )

                HomeTab.HEALTH -> HealthScreen(
                    viewModel = healthVm,
                    pets = pets,
                    selectedPetId = selectedPetId,
                    onSelectPet = petsVm::selectPet,
                    onAddPet = { appVm.open(AppScreen.PetEditor(null)) },
                    onAddRecord = { type -> appVm.open(AppScreen.RecordEditor(type)) },
                    onOpenPets = { appVm.selectTab(HomeTab.PETS) }
                )

                HomeTab.REMINDERS -> RemindersScreen(
                    viewModel = remindersVm,
                    pets = pets,
                    selectedPetId = selectedPetId,
                    onSelectPet = petsVm::selectPet,
                    onAddPet = { appVm.open(AppScreen.PetEditor(null)) },
                    onAddReminder = { appVm.open(AppScreen.ReminderEditor(null)) },
                    onEditReminder = { id -> appVm.open(AppScreen.ReminderEditor(id)) },
                    onOpenPets = { appVm.selectTab(HomeTab.PETS) }
                )

                HomeTab.PROFILE -> ProfileScreen(authVm)
            }
        }
    }
}
