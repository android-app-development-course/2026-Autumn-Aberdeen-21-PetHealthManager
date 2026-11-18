package com.example.pethealthmanager.data.model

enum class Species(val label: String) {
    DOG("Dog"),
    CAT("Cat"),
    OTHER("Other")
}

enum class Gender(val label: String) {
    MALE("Male"),
    FEMALE("Female"),
    UNKNOWN("Unknown")
}

data class Pet(
    val id: Long,
    val name: String,
    val species: Species,
    val breed: String = "",
    val gender: Gender = Gender.UNKNOWN,
    val birthDate: Long? = null,
    val weightKg: Double? = null,
    val avatarPath: String? = null,
    val adoptionDate: Long? = null,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class PetDraft(
    val name: String,
    val species: Species,
    val breed: String = "",
    val gender: Gender = Gender.UNKNOWN,
    val birthDate: Long? = null,
    val weightKg: Double? = null,
    val avatarPath: String? = null,
    val adoptionDate: Long? = null,
    val notes: String = ""
)

enum class HealthCategory(val label: String) {
    DAILY("Daily"),
    MEDICAL("Medical")
}

enum class HealthEntryType(
    val label: String,
    val category: HealthCategory,
    val unit: String? = null,
    val supportsAbnormal: Boolean = false
) {
    MEAL("Meal", HealthCategory.DAILY, "g"),
    WATER("Water", HealthCategory.DAILY, "ml"),
    STOOL("Stool", HealthCategory.DAILY, supportsAbnormal = true),
    URINE("Urine", HealthCategory.DAILY, supportsAbnormal = true),
    VOMIT("Vomit", HealthCategory.DAILY, supportsAbnormal = true),
    WEIGHT("Weight", HealthCategory.DAILY, "kg"),
    VET_VISIT("Vet visit", HealthCategory.MEDICAL),
    VACCINE("Vaccine", HealthCategory.MEDICAL),
    DEWORMING("Deworming", HealthCategory.MEDICAL),
    MEDICATION("Medication", HealthCategory.MEDICAL),
    SURGERY("Surgery", HealthCategory.MEDICAL),
    CHECKUP("Check-up", HealthCategory.MEDICAL);

    val isDaily: Boolean get() = category == HealthCategory.DAILY

    /** Vomit always counts; stool/urine only when the entry is marked abnormal. */
    val isSymptomType: Boolean
        get() = this == VOMIT || supportsAbnormal
}

data class HealthEntry(
    val id: Long,
    val petId: Long,
    val type: HealthEntryType,
    val occurredAt: Long,
    val amount: Double? = null,
    val note: String = "",
    val imagePath: String? = null,
    val isAbnormal: Boolean = false
)

data class HealthEntryDraft(
    val type: HealthEntryType,
    val occurredAt: Long,
    val amount: Double? = null,
    val note: String = "",
    val imagePath: String? = null,
    val isAbnormal: Boolean = false
)

enum class ReminderKind(val label: String) {
    VACCINE("Vaccine"),
    DEWORMING_INTERNAL("Deworming · internal"),
    DEWORMING_EXTERNAL("Deworming · external"),
    DEWORMING_BOTH("Deworming · both"),
    MEDICATION("Medication"),
    CUSTOM("Custom")
}

val DEWORMING_KINDS = setOf(
    ReminderKind.DEWORMING_INTERNAL,
    ReminderKind.DEWORMING_EXTERNAL,
    ReminderKind.DEWORMING_BOTH
)

data class Reminder(
    val id: Long,
    val petId: Long,
    val kind: ReminderKind,
    val title: String,
    val lastDoneAt: Long? = null,
    val intervalDays: Int? = null,
    val intervalHours: Int? = null,
    val dose: String = "",
    val nextDueAt: Long,
    val enabled: Boolean = true,
    val note: String = ""
) {
    fun isOverdue(now: Long = System.currentTimeMillis()): Boolean =
        enabled && nextDueAt <= now

    fun intervalText(): String = when {
        intervalHours != null && intervalHours == 24 -> "Once a day"
        intervalHours != null && intervalHours % 24 == 0 -> "Every ${intervalHours / 24} days"
        intervalHours != null -> "Every $intervalHours h"
        intervalDays != null -> "Every $intervalDays days"
        else -> "No repeat"
    }
}

data class ReminderDraft(
    val kind: ReminderKind,
    val title: String,
    val lastDoneAt: Long,
    val intervalDays: Int? = null,
    val intervalHours: Int? = null,
    val dose: String = "",
    val note: String = "",
    val enabled: Boolean = true
)

data class VaccinePreset(val label: String, val days: Int)

val VACCINE_PRESETS = listOf(
    VaccinePreset("Rabies", 365),
    VaccinePreset("DHPP", 365),
    VaccinePreset("FVRCP", 365),
    VaccinePreset("Bordetella", 365),
    VaccinePreset("Booster", 21),
    VaccinePreset("Custom", 365)
)

val CUSTOM_INTERVAL_PRESETS = listOf(7, 14, 30, 90, 180, 365)

data class SymptomCount(val type: HealthEntryType, val count: Int)
