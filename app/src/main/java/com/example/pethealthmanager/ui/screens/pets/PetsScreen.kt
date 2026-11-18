package com.example.pethealthmanager.ui.screens.pets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pethealthmanager.data.model.Pet
import com.example.pethealthmanager.ui.components.ConfirmDialog
import com.example.pethealthmanager.ui.components.DogIllustration
import com.example.pethealthmanager.ui.components.EmptyState
import com.example.pethealthmanager.ui.components.PetAvatar
import com.example.pethealthmanager.ui.components.ScreenHeader
import com.example.pethealthmanager.ui.components.SectionCard
import com.example.pethealthmanager.ui.components.SoftButton
import com.example.pethealthmanager.ui.components.StatTile
import com.example.pethealthmanager.ui.theme.Apricot
import com.example.pethealthmanager.ui.theme.Coral
import com.example.pethealthmanager.ui.theme.CreamDeep
import com.example.pethealthmanager.ui.theme.ErrorInk
import com.example.pethealthmanager.ui.theme.Ink
import com.example.pethealthmanager.ui.theme.InkSoft
import com.example.pethealthmanager.ui.theme.Mint
import com.example.pethealthmanager.util.Dates
import com.example.pethealthmanager.viewmodel.PetsViewModel

@Composable
fun PetsScreen(
    viewModel: PetsViewModel,
    pets: List<Pet>,
    selectedPetId: Long?,
    onAddPet: () -> Unit,
    onEditPet: (Long) -> Unit
) {
    val selected = pets.firstOrNull { it.id == selectedPetId } ?: pets.firstOrNull()
    var pendingDelete by remember { mutableStateOf<Pet?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ScreenHeader(
            kicker = "YOUR COMPANIONS",
            title = "Pet profiles",
            subtitle = "Everything about your furry family.",
            pets = pets,
            selectedPetId = selectedPetId,
            onSelectPet = viewModel::selectPet,
            onAddPet = onAddPet
        )

        if (pets.isEmpty()) {
            SectionCard(
                modifier = Modifier.padding(horizontal = 20.dp),
                padding = 24.dp
            ) {
                EmptyState(
                    title = "Create your first pet profile",
                    message = "Add a name, birthday and weight to start tracking meals, health records and reminders.",
                    actionLabel = "CREATE PET PROFILE",
                    onAction = onAddPet,
                    art = {
                        DogIllustration(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(176.dp)
                        )
                    }
                )
            }
            Spacer(Modifier.height(24.dp))
        } else {
            LazyRow(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pets, key = { it.id }) { pet ->
                    PetSelectorCard(
                        pet = pet,
                        selected = pet.id == selected?.id,
                        onClick = { viewModel.selectPet(pet.id) }
                    )
                }
                item {
                    AddPetCard(onClick = onAddPet)
                }
            }

            Spacer(Modifier.height(16.dp))

            selected?.let { pet ->
                PetHeroCard(
                    pet = pet,
                    onEdit = { onEditPet(pet.id) },
                    onDelete = { pendingDelete = pet }
                )
                Spacer(Modifier.height(14.dp))
                KeyDatesCard(pet)
                if (pet.notes.isNotBlank()) {
                    Spacer(Modifier.height(14.dp))
                    SectionCard(Modifier.padding(horizontal = 20.dp)) {
                        Text(
                            text = "Notes",
                            style = MaterialTheme.typography.titleMedium,
                            color = Ink
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = pet.notes,
                            style = MaterialTheme.typography.bodyLarge,
                            color = InkSoft
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
        }
    }

    pendingDelete?.let { pet ->
        ConfirmDialog(
            title = "Delete ${pet.name}?",
            message = "Health records and reminders for ${pet.name} will also be removed. This cannot be undone.",
            confirmLabel = "DELETE PET",
            onConfirm = {
                viewModel.deletePet(pet)
                pendingDelete = null
            },
            onDismiss = { pendingDelete = null }
        )
    }
}

@Composable
private fun PetSelectorCard(
    pet: Pet,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(22.dp)
    Column(
        modifier = Modifier
            .width(140.dp)
            .clip(shape)
            .background(Color.White)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Coral else CreamDeep,
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PetAvatar(pet = pet, size = 52.dp)
        Spacer(Modifier.height(8.dp))
        Text(
            text = pet.name,
            style = MaterialTheme.typography.titleMedium,
            color = Ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = pet.species.label,
            style = MaterialTheme.typography.bodyMedium,
            color = InkSoft,
            maxLines = 1
        )
    }
}

@Composable
private fun AddPetCard(onClick: () -> Unit) {
    val shape = RoundedCornerShape(22.dp)
    Column(
        modifier = Modifier
            .width(140.dp)
            .clip(shape)
            .background(CreamDeep.copy(alpha = 0.55f))
            .border(1.dp, Apricot, shape)
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = Coral,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Add pet",
            style = MaterialTheme.typography.titleMedium,
            color = Coral
        )
        Text(
            text = "New profile",
            style = MaterialTheme.typography.bodyMedium,
            color = InkSoft
        )
    }
}

@Composable
private fun PetHeroCard(
    pet: Pet,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    SectionCard(
        modifier = Modifier.padding(horizontal = 20.dp),
        padding = 20.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PetAvatar(pet = pet, size = 78.dp)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = pet.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Ink
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = buildString {
                        append(pet.species.label)
                        if (pet.breed.isNotBlank()) append(" · ${pet.breed}")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkSoft
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    InfoChip(pet.gender.label)
                    pet.birthDate?.let { InfoChip(Dates.ageText(it)) }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SoftButton(
                text = "EDIT PROFILE",
                onClick = onEdit,
                modifier = Modifier.weight(1f)
            )
            SoftButton(
                text = "DELETE",
                onClick = onDelete,
                accent = ErrorInk,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile(
                value = pet.weightKg?.let(Dates::weightText) ?: "—",
                label = "CURRENT WEIGHT",
                accent = Mint,
                modifier = Modifier.weight(1f)
            )
            StatTile(
                value = pet.birthDate?.let(Dates::ageText) ?: "—",
                label = "AGE",
                accent = Coral,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun InfoChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(CreamDeep)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = InkSoft,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun KeyDatesCard(pet: Pet) {
    SectionCard(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Key dates",
            style = MaterialTheme.typography.titleMedium,
            color = Ink
        )
        Spacer(Modifier.height(8.dp))

        if (pet.birthDate == null && pet.adoptionDate == null) {
            Text(
                text = "Add a birthday or homecoming date when you edit this profile.",
                style = MaterialTheme.typography.bodyMedium,
                color = InkSoft
            )
            return@SectionCard
        }

        pet.birthDate?.let { birth ->
            val days = Dates.daysUntil(Dates.nextAnniversary(birth))
            DateRow(
                label = "Birthday",
                value = Dates.formatDate(birth),
                extra = if (days == 0) "Today!" else "In $days days"
            )
        }
        pet.adoptionDate?.let { adoption ->
            val days = Dates.daysUntil(Dates.nextAnniversary(adoption))
            DateRow(
                label = "Homecoming anniversary",
                value = Dates.formatDate(adoption),
                extra = if (days == 0) "Today!" else "In $days days"
            )
        }
    }
}

@Composable
private fun DateRow(label: String, value: String, extra: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = InkSoft
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = Ink
            )
        }
        Text(
            text = extra,
            style = MaterialTheme.typography.labelSmall,
            color = Coral,
            textAlign = TextAlign.End
        )
    }
}
