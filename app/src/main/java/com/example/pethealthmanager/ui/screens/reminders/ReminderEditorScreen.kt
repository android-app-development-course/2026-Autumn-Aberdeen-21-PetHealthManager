package com.example.pethealthmanager.ui.screens.reminders

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.pethealthmanager.data.model.CUSTOM_INTERVAL_PRESETS
import com.example.pethealthmanager.data.model.Pet
import com.example.pethealthmanager.data.model.Reminder
import com.example.pethealthmanager.data.model.ReminderDraft
import com.example.pethealthmanager.data.model.ReminderKind
import com.example.pethealthmanager.data.model.VACCINE_PRESETS
import com.example.pethealthmanager.ui.components.DatePill
import com.example.pethealthmanager.ui.components.EmptyState
import com.example.pethealthmanager.ui.components.FormScreen
import com.example.pethealthmanager.ui.components.HintNote
import com.example.pethealthmanager.ui.components.MessageBanner
import com.example.pethealthmanager.ui.components.PetTextField
import com.example.pethealthmanager.ui.components.PillChip
import com.example.pethealthmanager.ui.theme.Coral
import com.example.pethealthmanager.ui.theme.ErrorInk
import com.example.pethealthmanager.ui.theme.ErrorSoft
import com.example.pethealthmanager.ui.theme.Ink
import com.example.pethealthmanager.ui.theme.InkSoft
import com.example.pethealthmanager.ui.theme.Mint
import com.example.pethealthmanager.ui.theme.MintSoft
import com.example.pethealthmanager.util.Dates

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReminderEditorScreen(
    pet: Pet?,
    existing: Reminder?,
    onBack: () -> Unit,
    onSave: (ReminderDraft) -> Unit
) {
    val context = LocalContext.current

    if (pet == null) {
        FormScreen(
            title = if (existing == null) "New reminder" else "Edit reminder",
            kicker = "REMINDERS",
            onBack = onBack
        ) {
            EmptyState(
                title = "Select a pet first",
                message = "Add or choose a pet profile before creating reminders.",
                actionLabel = "GO BACK",
                onAction = onBack
            )
        }
        return
    }

    val key = existing?.id
    val startPreset = VACCINE_PRESETS.firstOrNull { it.label == existing?.title }?.label
        ?: VACCINE_PRESETS.first().label

    var kind by rememberSaveable(key) { mutableStateOf(existing?.kind ?: ReminderKind.VACCINE) }
    var preset by rememberSaveable(key) { mutableStateOf(startPreset) }
    var title by rememberSaveable(key) { mutableStateOf(existing?.title ?: startPreset) }
    var lastDoneAt by rememberSaveable(key) { mutableStateOf(existing?.lastDoneAt ?: Dates.today()) }
    var intervalDays by rememberSaveable(key) { mutableStateOf<Int?>(existing?.intervalDays ?: 365) }
    var intervalHours by rememberSaveable(key) { mutableStateOf<Int?>(existing?.intervalHours) }
    var dose by rememberSaveable(key) { mutableStateOf(existing?.dose.orEmpty()) }
    var note by rememberSaveable(key) { mutableStateOf(existing?.note.orEmpty()) }
    var error by remember { mutableStateOf<String?>(null) }

    val enabled = existing?.enabled ?: true

    fun applyKindDefaults(next: ReminderKind) {
        kind = next
        intervalHours = null
        when (next) {
            ReminderKind.VACCINE -> {
                preset = VACCINE_PRESETS.first().label
                title = VACCINE_PRESETS.first().label
                intervalDays = VACCINE_PRESETS.first().days
            }
            ReminderKind.DEWORMING_INTERNAL -> {
                title = "Internal deworming"
                intervalDays = 90
            }
            ReminderKind.DEWORMING_EXTERNAL -> {
                title = "External deworming"
                intervalDays = 30
            }
            ReminderKind.DEWORMING_BOTH -> {
                title = "Deworming · internal + external"
                intervalDays = 30
            }
            ReminderKind.MEDICATION -> {
                title = "Medication"
                intervalDays = null
                intervalHours = 24
            }
            ReminderKind.CUSTOM -> {
                title = "Custom reminder"
                intervalDays = 30
            }
        }
        error = null
    }

    val showTitleField = when (kind) {
        ReminderKind.MEDICATION, ReminderKind.CUSTOM -> true
        ReminderKind.VACCINE -> preset == "Custom"
        else -> false
    }
    val showDayPresets = kind != ReminderKind.MEDICATION
    val customPreset = kind == ReminderKind.VACCINE && preset == "Custom"

    FormScreen(
        title = if (existing == null) "New reminder" else "Edit reminder",
        kicker = "REMINDERS · ${pet.name.uppercase()}",
        onBack = onBack,
        primaryLabel = "SAVE REMINDER",
        onPrimary = {
            val finalTitle = when {
                kind == ReminderKind.MEDICATION && title.isBlank() -> "Medication"
                kind == ReminderKind.CUSTOM && title.isBlank() -> "Custom reminder"
                else -> title.trim()
            }
            if (finalTitle.isEmpty()) {
                error = "Enter a reminder name."
            } else if (intervalDays == null && intervalHours == null) {
                error = "Choose how often this reminder repeats."
            } else {
                error = null
                onSave(
                    ReminderDraft(
                        kind = kind,
                        title = finalTitle,
                        lastDoneAt = lastDoneAt,
                        intervalDays = intervalDays,
                        intervalHours = intervalHours,
                        dose = dose.trim(),
                        note = note.trim(),
                        enabled = enabled
                    )
                )
            }
        }
    ) {
        Text(
            text = "What should we track?",
            style = MaterialTheme.typography.titleMedium,
            color = Ink
        )
        Spacer(Modifier.height(10.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ReminderKind.entries.forEach { option ->
                PillChip(
                    text = option.chipLabel(),
                    selected = kind == option,
                    onClick = { if (kind != option) applyKindDefaults(option) }
                )
            }
        }

        when (kind) {
            ReminderKind.VACCINE -> {
                Spacer(Modifier.height(18.dp))
                Text("VACCINE", style = MaterialTheme.typography.labelSmall, color = InkSoft)
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VACCINE_PRESETS.forEach { option ->
                        PillChip(
                            text = option.label,
                            selected = preset == option.label,
                            onClick = {
                                preset = option.label
                                title = option.label
                                intervalDays = option.days
                                intervalHours = null
                                error = null
                            }
                        )
                    }
                }
            }

            ReminderKind.DEWORMING_INTERNAL,
            ReminderKind.DEWORMING_EXTERNAL,
            ReminderKind.DEWORMING_BOTH -> {
                Spacer(Modifier.height(14.dp))
                HintNote(
                    text = when (kind) {
                        ReminderKind.DEWORMING_INTERNAL -> "Internal deworming is usually repeated every 90 days."
                        ReminderKind.DEWORMING_EXTERNAL -> "External parasite protection is usually monthly."
                        else -> "Combined internal and external protection is usually monthly."
                    },
                    accent = Coral
                )
            }

            ReminderKind.MEDICATION -> Unit

            ReminderKind.CUSTOM -> Unit
        }

        if (showTitleField) {
            Spacer(Modifier.height(16.dp))
            PetTextField(
                label = if (kind == ReminderKind.MEDICATION) "MEDICATION NAME" else "REMINDER NAME",
                value = title,
                onValueChange = { title = it; error = null },
                placeholder = if (kind == ReminderKind.MEDICATION) "e.g. Heartworm pill" else "e.g. Grooming day"
            )
        }

        Spacer(Modifier.height(16.dp))
        DatePill(
            label = "LAST DONE",
            millis = lastDoneAt,
            onPick = { pickDate(context, lastDoneAt) { lastDoneAt = it } }
        )

        if (kind == ReminderKind.MEDICATION) {
            Spacer(Modifier.height(16.dp))
            PetTextField(
                label = "DOSE",
                value = dose,
                onValueChange = { dose = it },
                placeholder = "e.g. 1 tablet",
                imeAction = ImeAction.Done
            )

            Spacer(Modifier.height(16.dp))
            Text("FREQUENCY", style = MaterialTheme.typography.labelSmall, color = InkSoft)
            Spacer(Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    24 to "Once a day",
                    12 to "Twice a day",
                    8 to "Three times a day"
                ).forEach { (hours, label) ->
                    PillChip(
                        text = label,
                        selected = intervalHours == hours,
                        onClick = { intervalHours = hours; intervalDays = null; error = null }
                    )
                }
            }
        }

        if (showDayPresets) {
            Spacer(Modifier.height(16.dp))
            Text("REPEAT", style = MaterialTheme.typography.labelSmall, color = InkSoft)
            Spacer(Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CUSTOM_INTERVAL_PRESETS.forEach { days ->
                    PillChip(
                        text = "$days days",
                        selected = intervalDays == days,
                        onClick = {
                            intervalDays = days
                            intervalHours = null
                            error = null
                        }
                    )
                }
            }
            if (intervalDays != null && intervalDays !in CUSTOM_INTERVAL_PRESETS) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Current cycle: every $intervalDays days",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkSoft
                )
            }
        }

        if (kind == ReminderKind.CUSTOM) {
            Spacer(Modifier.height(16.dp))
            PetTextField(
                label = "DOSE / DETAIL",
                value = dose,
                onValueChange = { dose = it },
                placeholder = "Optional detail"
            )
        }

        Spacer(Modifier.height(16.dp))
        PetTextField(
            label = "NOTES",
            value = note,
            onValueChange = { note = it },
            placeholder = "Anything worth remembering",
            singleLine = false,
            minLines = 3,
            imeAction = ImeAction.Done
        )

        Spacer(Modifier.height(16.dp))

        val previewDue = duePreview(lastDoneAt, intervalDays, intervalHours)
        val previewOverdue = previewDue <= System.currentTimeMillis()
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = if (previewOverdue) ErrorSoft else MintSoft
        ) {
            Column(Modifier.padding(18.dp)) {
                Text("NEXT DUE", style = MaterialTheme.typography.labelSmall, color = InkSoft)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = Dates.formatDate(previewDue),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Ink
                )
                Text(
                    text = Dates.dueText(previewDue),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (previewOverdue) ErrorInk else InkSoft
                )
            }
        }

        if (customPreset) {
            Spacer(Modifier.height(14.dp))
            HintNote("Set a custom cycle with the repeat buttons above.")
        } else if (kind == ReminderKind.VACCINE) {
            Spacer(Modifier.height(14.dp))
            HintNote("Annual vaccines use a 365-day cycle. Boosters can be shortened to 21 days.")
        }

        MessageBanner(message = error, isError = true)
        Spacer(Modifier.height(8.dp))
    }
}

private fun ReminderKind.chipLabel(): String = when (this) {
    ReminderKind.VACCINE -> "Vaccine"
    ReminderKind.DEWORMING_INTERNAL -> "Internal"
    ReminderKind.DEWORMING_EXTERNAL -> "External"
    ReminderKind.DEWORMING_BOTH -> "Both"
    ReminderKind.MEDICATION -> "Medication"
    ReminderKind.CUSTOM -> "Custom"
}

private fun duePreview(lastDoneAt: Long, intervalDays: Int?, intervalHours: Int?): Long {
    val millis = when {
        intervalHours != null -> intervalHours * 3_600_000L
        intervalDays != null -> intervalDays * 86_400_000L
        else -> 0L
    }
    return lastDoneAt + millis
}

private fun pickDate(context: Context, initial: Long, onPicked: (Long) -> Unit) {
    val (year, month, day) = Dates.dateParts(initial)
    DatePickerDialog(
        context,
        { _, pickedYear, pickedMonth, pickedDay ->
            onPicked(Dates.dateAt(pickedYear, pickedMonth, pickedDay))
        },
        year,
        month,
        day
    ).show()
}
