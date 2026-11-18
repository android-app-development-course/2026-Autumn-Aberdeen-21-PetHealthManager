package com.example.pethealthmanager.ui.screens.pets

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pethealthmanager.data.model.Gender
import com.example.pethealthmanager.data.model.Pet
import com.example.pethealthmanager.data.model.PetDraft
import com.example.pethealthmanager.data.model.Species
import com.example.pethealthmanager.ui.components.DatePill
import com.example.pethealthmanager.ui.components.FormScreen
import com.example.pethealthmanager.ui.components.MessageBanner
import com.example.pethealthmanager.ui.components.PetAvatar
import com.example.pethealthmanager.ui.components.PetTextField
import com.example.pethealthmanager.ui.components.PillChip
import com.example.pethealthmanager.ui.components.SoftButton
import com.example.pethealthmanager.ui.theme.InkSoft
import com.example.pethealthmanager.util.Dates

@Composable
fun PetEditorScreen(
    existing: Pet?,
    onBack: () -> Unit,
    onSave: (PetDraft) -> Unit,
    onImportImage: (Uri, (String?) -> Unit) -> Unit
) {
    val context = LocalContext.current
    val key = existing?.id
    var name by rememberSaveable(key) { mutableStateOf(existing?.name.orEmpty()) }
    var species by rememberSaveable(key) { mutableStateOf(existing?.species ?: Species.DOG) }
    var breed by rememberSaveable(key) { mutableStateOf(existing?.breed.orEmpty()) }
    var gender by rememberSaveable(key) { mutableStateOf(existing?.gender ?: Gender.UNKNOWN) }
    var birthDate by rememberSaveable(key) { mutableStateOf(existing?.birthDate) }
    var adoptionDate by rememberSaveable(key) { mutableStateOf(existing?.adoptionDate) }
    var avatarPath by rememberSaveable(key) { mutableStateOf(existing?.avatarPath) }
    var weightText by rememberSaveable(key) {
        mutableStateOf(
            existing?.weightKg?.let { value ->
                if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
            }.orEmpty()
        )
    }
    var notes by rememberSaveable(key) { mutableStateOf(existing?.notes.orEmpty()) }
    var error by remember { mutableStateOf<String?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onImportImage(uri) { path ->
                if (path != null) avatarPath = path
            }
        }
    }

    val previewPet = Pet(
        id = existing?.id ?: 0L,
        name = name,
        species = species,
        avatarPath = avatarPath
    )

    FormScreen(
        title = if (existing == null) "Add pet" else "Edit pet",
        kicker = "PET PROFILE",
        onBack = onBack,
        primaryLabel = "SAVE PET",
        onPrimary = {
            val trimmedName = name.trim()
            val weight = weightText.trim().replace(',', '.').toDoubleOrNull()
            when {
                trimmedName.isEmpty() -> error = "Please enter your pet's name."
                weightText.isNotBlank() && weight == null -> error = "Weight must be a number."
                weight != null && weight <= 0 -> error = "Weight must be greater than zero."
                else -> {
                    error = null
                    onSave(
                        PetDraft(
                            name = trimmedName,
                            species = species,
                            breed = breed.trim(),
                            gender = gender,
                            birthDate = birthDate,
                            weightKg = weight,
                            avatarPath = avatarPath,
                            adoptionDate = adoptionDate,
                            notes = notes.trim()
                        )
                    )
                }
            }
        }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PetAvatar(pet = previewPet, size = 92.dp)
            Spacer(Modifier.width(16.dp))
            Column {
                SoftButton(
                    text = "CHANGE PHOTO",
                    onClick = {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
                if (avatarPath != null) {
                    TextButton(onClick = { avatarPath = null }) {
                        Text(
                            text = "REMOVE PHOTO",
                            style = MaterialTheme.typography.labelSmall,
                            color = InkSoft
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        PetTextField(
            label = "NAME",
            value = name,
            onValueChange = { name = it; error = null },
            placeholder = "e.g. Mochi"
        )

        Spacer(Modifier.height(16.dp))
        Text("SPECIES", style = MaterialTheme.typography.labelSmall, color = InkSoft)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Species.entries.forEach { option ->
                PillChip(
                    text = option.label,
                    selected = species == option,
                    onClick = { species = option }
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        PetTextField(
            label = "BREED",
            value = breed,
            onValueChange = { breed = it },
            placeholder = "e.g. Shiba Inu"
        )

        Spacer(Modifier.height(16.dp))
        Text("GENDER", style = MaterialTheme.typography.labelSmall, color = InkSoft)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Gender.entries.forEach { option ->
                PillChip(
                    text = option.label,
                    selected = gender == option,
                    onClick = { gender = option }
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        DatePill(
            label = "BIRTHDAY",
            millis = birthDate,
            onPick = { pickDate(context, birthDate) { birthDate = it } },
            onClear = { birthDate = null }
        )

        Spacer(Modifier.height(16.dp))
        PetTextField(
            label = "CURRENT WEIGHT (KG)",
            value = weightText,
            onValueChange = { weightText = it; error = null },
            placeholder = "e.g. 12.5",
            keyboardType = KeyboardType.Decimal
        )

        Spacer(Modifier.height(16.dp))
        DatePill(
            label = "HOMECOMING DATE",
            millis = adoptionDate,
            onPick = { pickDate(context, adoptionDate) { adoptionDate = it } },
            onClear = { adoptionDate = null }
        )

        Spacer(Modifier.height(16.dp))
        PetTextField(
            label = "NOTES",
            value = notes,
            onValueChange = { notes = it },
            placeholder = "Allergies, habits, favourite food...",
            singleLine = false,
            minLines = 3,
            imeAction = ImeAction.Done
        )

        MessageBanner(message = error, isError = true)
        Spacer(Modifier.height(8.dp))
    }
}

private fun pickDate(context: Context, initial: Long?, onPicked: (Long) -> Unit) {
    val base = initial ?: Dates.today()
    val (year, month, day) = Dates.dateParts(base)
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
