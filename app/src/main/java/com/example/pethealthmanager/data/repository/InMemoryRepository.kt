package com.example.pethealthmanager.data.repository

import com.example.pethealthmanager.data.model.DEWORMING_KINDS
import com.example.pethealthmanager.data.model.HealthEntry
import com.example.pethealthmanager.data.model.HealthEntryDraft
import com.example.pethealthmanager.data.model.HealthEntryType
import com.example.pethealthmanager.data.model.Pet
import com.example.pethealthmanager.data.model.PetDraft
import com.example.pethealthmanager.data.model.Reminder
import com.example.pethealthmanager.data.model.ReminderDraft
import com.example.pethealthmanager.data.model.ReminderKind
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Single in-memory implementation of every repository interface.
 * A future Room upgrade only needs to replace this class and keep the interfaces.
 */
class InMemoryRepository : AccountRepository, PetRepository, HealthRepository, ReminderRepository {

    private val ids = AtomicLong(1)
    private fun nextId(): Long = ids.getAndIncrement()

    private val accounts = mutableMapOf<String, String>()
    private val _currentUser = MutableStateFlow<String?>(null)
    override val currentUser: StateFlow<String?> = _currentUser

    private val _pets = MutableStateFlow<List<Pet>>(emptyList())
    override val pets: StateFlow<List<Pet>> = _pets

    private val _selectedPetId = MutableStateFlow<Long?>(null)
    override val selectedPetId: StateFlow<Long?> = _selectedPetId

    private val _entries = MutableStateFlow<List<HealthEntry>>(emptyList())
    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())

    override fun register(username: String, password: String): AuthResult {
        if (accounts.containsKey(username)) return AuthResult.UserExists
        accounts[username] = password
        return AuthResult.Success
    }

    override fun authenticate(username: String, password: String): AuthResult {
        val stored = accounts[username] ?: return AuthResult.UnknownUser
        if (stored != password) return AuthResult.WrongPassword
        _currentUser.value = username
        return AuthResult.Success
    }

    override fun logout() {
        _currentUser.value = null
    }

    override fun pet(id: Long): Pet? = _pets.value.firstOrNull { it.id == id }

    override fun selectPet(id: Long) {
        if (_pets.value.any { it.id == id }) _selectedPetId.value = id
    }

    override fun addPet(draft: PetDraft): Pet {
        val pet = Pet(
            id = nextId(),
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
        _pets.update { it + pet }
        _selectedPetId.value = pet.id
        return pet
    }

    override fun updatePet(pet: Pet) {
        _pets.update { list -> list.map { if (it.id == pet.id) pet else it } }
    }

    override fun deletePet(id: Long) {
        _pets.update { list -> list.filterNot { it.id == id } }
        _entries.update { list -> list.filterNot { it.petId == id } }
        _reminders.update { list -> list.filterNot { it.petId == id } }
        if (_selectedPetId.value == id) {
            _selectedPetId.value = _pets.value.firstOrNull()?.id
        }
    }

    override fun updateWeight(petId: Long, weightKg: Double) {
        _pets.update { list ->
            list.map { if (it.id == petId) it.copy(weightKg = weightKg) else it }
        }
    }

    override fun observeEntries(petId: Long): Flow<List<HealthEntry>> =
        _entries.map { list ->
            list.filter { it.petId == petId }.sortedByDescending { it.occurredAt }
        }

    override fun addEntry(petId: Long, draft: HealthEntryDraft): HealthEntry {
        val entry = HealthEntry(
            id = nextId(),
            petId = petId,
            type = draft.type,
            occurredAt = draft.occurredAt,
            amount = draft.amount,
            note = draft.note,
            imagePath = draft.imagePath,
            isAbnormal = draft.isAbnormal
        )
        _entries.update { it + entry }
        return entry
    }

    override fun deleteEntry(id: Long) {
        _entries.update { list -> list.filterNot { it.id == id } }
    }

    override fun observeReminders(petId: Long): Flow<List<Reminder>> =
        _reminders.map { list ->
            list.filter { it.petId == petId }.sortedBy { it.nextDueAt }
        }

    override fun reminder(id: Long): Reminder? = _reminders.value.firstOrNull { it.id == id }

    override fun addReminder(petId: Long, draft: ReminderDraft): Reminder {
        val reminder = Reminder(
            id = nextId(),
            petId = petId,
            kind = draft.kind,
            title = draft.title,
            lastDoneAt = draft.lastDoneAt,
            intervalDays = draft.intervalDays,
            intervalHours = draft.intervalHours,
            dose = draft.dose,
            nextDueAt = nextDue(draft.lastDoneAt, draft.intervalDays, draft.intervalHours),
            enabled = draft.enabled,
            note = draft.note
        )
        _reminders.update { it + reminder }
        return reminder
    }

    override fun updateReminder(id: Long, draft: ReminderDraft): Reminder? {
        var updated: Reminder? = null
        _reminders.update { list ->
            list.map { current ->
                if (current.id != id) return@map current
                current.copy(
                    kind = draft.kind,
                    title = draft.title,
                    lastDoneAt = draft.lastDoneAt,
                    intervalDays = draft.intervalDays,
                    intervalHours = draft.intervalHours,
                    dose = draft.dose,
                    nextDueAt = nextDue(draft.lastDoneAt, draft.intervalDays, draft.intervalHours),
                    enabled = draft.enabled,
                    note = draft.note
                ).also { updated = it }
            }
        }
        return updated
    }

    override fun deleteReminder(id: Long) {
        _reminders.update { list -> list.filterNot { it.id == id } }
    }

    override fun markDone(id: Long, doneAt: Long): Reminder? {
        var updated: Reminder? = null
        _reminders.update { list ->
            list.map { current ->
                if (current.id != id) return@map current
                current.copy(
                    lastDoneAt = doneAt,
                    nextDueAt = nextDue(doneAt, current.intervalDays, current.intervalHours)
                ).also { updated = it }
            }
        }
        return updated
    }

    override fun setEnabled(id: Long, enabled: Boolean): Reminder? {
        var updated: Reminder? = null
        _reminders.update { list ->
            list.map { current ->
                if (current.id != id) return@map current
                current.copy(enabled = enabled).also { updated = it }
            }
        }
        return updated
    }

    override fun applyMedicalRecord(
        petId: Long,
        type: HealthEntryType,
        occurredAt: Long
    ): List<Reminder> {
        val matches: (ReminderKind) -> Boolean = when (type) {
            HealthEntryType.VACCINE -> { kind -> kind == ReminderKind.VACCINE }
            HealthEntryType.DEWORMING -> { kind -> kind in DEWORMING_KINDS }
            else -> return emptyList()
        }
        val changed = mutableListOf<Reminder>()
        _reminders.update { list ->
            list.map { current ->
                if (current.petId != petId || !matches(current.kind)) return@map current
                current.copy(
                    lastDoneAt = occurredAt,
                    nextDueAt = nextDue(occurredAt, current.intervalDays, current.intervalHours)
                ).also { changed += it }
            }
        }
        return changed
    }

    private fun nextDue(lastDoneAt: Long, intervalDays: Int?, intervalHours: Int?): Long {
        val millis = when {
            intervalHours != null -> intervalHours * 3_600_000L
            intervalDays != null -> intervalDays * 86_400_000L
            else -> 0L
        }
        return lastDoneAt + millis
    }
}
