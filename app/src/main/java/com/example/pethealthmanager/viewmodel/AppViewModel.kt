package com.example.pethealthmanager.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.pethealthmanager.data.model.HealthEntryType

enum class HomeTab(val label: String) {
    PETS("Pets"),
    HEALTH("Health"),
    REMINDERS("Reminders"),
    PROFILE("Profile")
}

sealed interface AppScreen {
    data class PetEditor(val petId: Long?) : AppScreen
    data class RecordEditor(val defaultType: HealthEntryType?) : AppScreen
    data class ReminderEditor(val reminderId: Long?) : AppScreen
}

class AppViewModel : ViewModel() {

    var tab by mutableStateOf(HomeTab.PETS)
        private set

    private val backStack = mutableStateListOf<AppScreen>()

    val screen: AppScreen?
        get() = backStack.lastOrNull()

    fun selectTab(next: HomeTab) {
        tab = next
        backStack.clear()
    }

    fun open(target: AppScreen) {
        backStack.add(target)
    }

    fun back(): Boolean {
        if (backStack.isEmpty()) return false
        backStack.removeAt(backStack.lastIndex)
        return true
    }

    fun popToRoot() {
        backStack.clear()
    }
}
