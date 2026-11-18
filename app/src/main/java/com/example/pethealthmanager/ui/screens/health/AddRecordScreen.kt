package com.example.pethealthmanager.ui.screens.health

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pethealthmanager.data.model.HealthEntryDraft
import com.example.pethealthmanager.data.model.HealthEntryType
import com.example.pethealthmanager.data.model.Pet
import com.example.pethealthmanager.ui.components.DatePill
import com.example.pethealthmanager.ui.components.EmptyState
import com.example.pethealthmanager.ui.components.FormScreen
import com.example.pethealthmanager.ui.components.HintNote
import com.example.pethealthmanager.ui.components.LocalImage
import com.example.pethealthmanager.ui.components.MessageBanner
import com.example.pethealthmanager.ui.components.PetTextField
import com.example.pethealthmanager.ui.components.PillChip
import com.example.pethealthmanager.ui.components.SoftButton
import com.example.pethealthmanager.ui.theme.Apricot
import com.example.pethealthmanager.ui.theme.Coral
import com.example.pethealthmanager.ui.theme.Ink
import com.example.pethealthmanager.ui.theme.InkSoft
import com.example.pethealthmanager.util.Dates

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddRecordScreen(
    pet: Pet?,
    defaultType: HealthEntryType?,
    onBack: () -> Unit,
    onSave: (HealthEntryDraft) -> Unit,
    onImportImage: (Uri, (String?) -> Unit) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    if (pet == null) {
        FormScreen(
            title = "New record",
            kicker = "HEALTH LOG",
            onBack = onBack
        ) {
            EmptyState(
                title = "Select a pet first",
                message = "Add or choose a pet profile before logging health records.",
                actionLabel = "GO BACK",
                onAction = onBack
            )
        }
        return
    }

    var type by rememberSaveable { mutableStateOf(defaultType ?: HealthEntryType.MEAL) }
    var occurredAt by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    var amountText by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var imagePath by rememberSaveable { mutableStateOf<String?>(null) }
    var abnormal by rememberSaveable { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onImportImage(uri) { path -> if (path != null) imagePath = path }
        }
    }

    FormScreen(
        title = "New record",
        kicker = "HEALTH LOG · ${pet.name.uppercase()}",
        onBack = onBack,
        primaryLabel = "SAVE RECORD",
        onPrimary = {
            val amount = amountText.trim().replace(',', '.').toDoubleOrNull()
            when {
                amountText.isNotBlank() && amount == null ->
                    error = "Amount must be a number."

                type == HealthEntryType.WEIGHT && amount == null ->
                    error = "Enter the weight in kilograms."

                amount != null && amount <= 0 ->
                    error = "Amount must be greater than zero."

                else -> {
                    error = null
                    onSave(
                        HealthEntryDraft(
                            type = type,
                            occurredAt = occurredAt,
                            amount = amount,
                            note = note.trim(),
                            imagePath = imagePath,
                            isAbnormal = type == HealthEntryType.VOMIT || abnormal
                        )
                    )
                }
            }
        }
    ) {
        Text(
            text = "What happened?",
            style = MaterialTheme.typography.titleMedium,
            color = Ink
        )
        Spacer(Modifier.height(10.dp))
        Text("DAILY", style = MaterialTheme.typography.labelSmall, color = InkSoft)
        Spacer(Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HealthEntryType.entries.filter { it.isDaily }.forEach { option ->
                PillChip(
                    text = option.label,
                    selected = type == option,
                    onClick = {
                        type = option
                        amountText = ""
                        abnormal = false
                        error = null
                    }
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        Text("MEDICAL", style = MaterialTheme.typography.labelSmall, color = InkSoft)
        Spacer(Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HealthEntryType.entries.filter { !it.isDaily }.forEach { option ->
                PillChip(
                    text = option.label,
                    selected = type == option,
                    onClick = {
                        type = option
                        amountText = ""
                        abnormal = false
                        error = null
                    }
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DatePill(
                label = "DATE",
                millis = occurredAt,
                onPick = { pickRecordDate(context, occurredAt) { occurredAt = it } },
                modifier = Modifier.weight(1f)
            )
            TimePill(
                millis = occurredAt,
                onPick = { pickRecordTime(context, occurredAt) { occurredAt = it } },
                modifier = Modifier.weight(1f)
            )
        }

        val unit = type.unit
        if (unit != null) {
            Spacer(Modifier.height(16.dp))
            PetTextField(
                label = "AMOUNT (${unit.uppercase()})",
                value = amountText,
                onValueChange = { amountText = it; error = null },
                placeholder = when (type) {
                    HealthEntryType.WEIGHT -> "e.g. 12.5"
                    HealthEntryType.WATER -> "e.g. 250"
                    else -> "e.g. 120"
                },
                keyboardType = KeyboardType.Decimal
            )
        }

        if (type.supportsAbnormal && type != HealthEntryType.VOMIT) {
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Mark as abnormal",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Ink
                    )
                    Text(
                        text = "Counts toward symptom frequency",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkSoft
                    )
                }
                Switch(
                    checked = abnormal,
                    onCheckedChange = { abnormal = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Coral,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Apricot
                    )
                )
            }
        }

        if (type == HealthEntryType.VOMIT) {
            Spacer(Modifier.height(14.dp))
            HintNote("Every vomit entry counts toward the 30-day symptom chart.", accent = Coral)
        }

        if (type == HealthEntryType.VACCINE || type == HealthEntryType.DEWORMING) {
            Spacer(Modifier.height(14.dp))
            HintNote(
                text = "Saving this record also updates matching reminders for ${pet.name}.",
                accent = Coral
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
        Text("PHOTO", style = MaterialTheme.typography.labelSmall, color = InkSoft)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (imagePath != null) {
                LocalImage(
                    path = imagePath,
                    contentDescription = "Selected photo",
                    modifier = Modifier
                        .size(82.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    SoftButton(
                        text = "CHANGE",
                        onClick = {
                            photoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    )
                    TextButton(onClick = { imagePath = null }) {
                        Text(
                            text = "REMOVE",
                            style = MaterialTheme.typography.labelSmall,
                            color = InkSoft
                        )
                    }
                }
            } else {
                SoftButton(
                    text = "ADD PHOTO",
                    onClick = {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
            }
        }

        MessageBanner(message = error, isError = true)
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun TimePill(
    millis: Long,
    onPick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text("TIME", style = MaterialTheme.typography.labelSmall, color = InkSoft)
        Spacer(Modifier.height(7.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(1.dp, Apricot, RoundedCornerShape(16.dp))
                .clickable(onClick = onPick)
                .padding(horizontal = 14.dp, vertical = 13.dp)
        ) {
            Text(
                text = Dates.formatTime(millis),
                style = MaterialTheme.typography.bodyLarge,
                color = Ink
            )
        }
    }
}

private fun pickRecordDate(context: Context, current: Long, onPicked: (Long) -> Unit) {
    val (year, month, day) = Dates.dateParts(current)
    val (hour, minute) = Dates.timeParts(current)
    DatePickerDialog(
        context,
        { _, pickedYear, pickedMonth, pickedDay ->
            val date = Dates.dateAt(pickedYear, pickedMonth, pickedDay)
            onPicked(Dates.combine(date, hour, minute))
        },
        year,
        month,
        day
    ).show()
}

private fun pickRecordTime(context: Context, current: Long, onPicked: (Long) -> Unit) {
    val (hour, minute) = Dates.timeParts(current)
    TimePickerDialog(
        context,
        { _, pickedHour, pickedMinute ->
            onPicked(Dates.combine(current, pickedHour, pickedMinute))
        },
        hour,
        minute,
        true
    ).show()
}
