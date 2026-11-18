package com.example.pethealthmanager.ui.screens.health

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pethealthmanager.data.model.HealthEntry
import com.example.pethealthmanager.data.model.HealthEntryType
import com.example.pethealthmanager.data.model.Pet
import com.example.pethealthmanager.ui.components.ConfirmDialog
import com.example.pethealthmanager.ui.components.EmptyState
import com.example.pethealthmanager.ui.components.LocalImage
import com.example.pethealthmanager.ui.components.ScreenHeader
import com.example.pethealthmanager.ui.components.SectionCard
import com.example.pethealthmanager.ui.components.SegmentedControl
import com.example.pethealthmanager.ui.components.SoftButton
import com.example.pethealthmanager.ui.components.StatTile
import com.example.pethealthmanager.ui.components.SymptomBar
import com.example.pethealthmanager.ui.components.WeightChart
import com.example.pethealthmanager.ui.theme.Coral
import com.example.pethealthmanager.ui.theme.CoralDeep
import com.example.pethealthmanager.ui.theme.CreamDeep
import com.example.pethealthmanager.ui.theme.ErrorInk
import com.example.pethealthmanager.ui.theme.ErrorSoft
import com.example.pethealthmanager.ui.theme.FurTanDeep
import com.example.pethealthmanager.ui.theme.Ink
import com.example.pethealthmanager.ui.theme.InkSoft
import com.example.pethealthmanager.ui.theme.Lavender
import com.example.pethealthmanager.ui.theme.Mango
import com.example.pethealthmanager.ui.theme.Mint
import com.example.pethealthmanager.ui.theme.Sky
import com.example.pethealthmanager.util.Dates
import com.example.pethealthmanager.viewmodel.HealthViewModel

@Composable
fun HealthScreen(
    viewModel: HealthViewModel,
    pets: List<Pet>,
    selectedPetId: Long?,
    onSelectPet: (Long) -> Unit,
    onAddPet: () -> Unit,
    onAddRecord: (HealthEntryType?) -> Unit,
    onOpenPets: () -> Unit
) {
    val entries by viewModel.entries.collectAsStateWithLifecycle()
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var pendingDelete by remember { mutableStateOf<HealthEntry?>(null) }

    Column(Modifier.fillMaxSize()) {
        ScreenHeader(
            kicker = "PET WELLNESS",
            title = "Health log",
            subtitle = "Daily care and medical history in one place.",
            pets = pets,
            selectedPetId = selectedPetId,
            onSelectPet = onSelectPet,
            onAddPet = onAddPet
        )

        if (pets.isEmpty()) {
            EmptyState(
                title = "No pet profile yet",
                message = "Create a pet profile before logging meals, symptoms or vet visits.",
                actionLabel = "CREATE PET PROFILE",
                onAction = onAddPet
            )
            return@Column
        }

        Column(Modifier.padding(horizontal = 20.dp)) {
            SegmentedControl(
                options = listOf("Daily", "Medical", "Trends"),
                selectedIndex = tab,
                onSelect = { tab = it }
            )
        }

        Spacer(Modifier.height(12.dp))

        when (tab) {
            0 -> RecordList(
                entries = entries.filter { it.type.isDaily },
                emptyTitle = "No daily records yet",
                emptyMessage = "Log meals, water, stool, urine, vomit or weight.",
                onAdd = { onAddRecord(null) },
                onDelete = { pendingDelete = it }
            )

            1 -> RecordList(
                entries = entries.filter { !it.type.isDaily },
                emptyTitle = "No medical records yet",
                emptyMessage = "Keep track of vet visits, vaccines, deworming, medication and check-ups.",
                onAdd = { onAddRecord(null) },
                onDelete = { pendingDelete = it }
            )

            else -> TrendsSection(
                entries = entries,
                onLogWeight = { onAddRecord(HealthEntryType.WEIGHT) },
                modifier = Modifier.weight(1f)
            )
        }
    }

    pendingDelete?.let { entry ->
        ConfirmDialog(
            title = "Delete ${entry.type.label.lowercase()} record?",
            message = "This health record will be removed.",
            confirmLabel = "DELETE RECORD",
            onConfirm = {
                viewModel.deleteEntry(entry)
                pendingDelete = null
            },
            onDismiss = { pendingDelete = null }
        )
    }
}

@Composable
private fun RecordList(
    entries: List<HealthEntry>,
    emptyTitle: String,
    emptyMessage: String,
    onAdd: () -> Unit,
    onDelete: (HealthEntry) -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        if (entries.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                EmptyState(
                    title = emptyTitle,
                    message = emptyMessage,
                    actionLabel = "ADD RECORD",
                    onAction = onAdd
                )
            }
        } else {
            val sorted = entries.sortedByDescending { it.occurredAt }
            val grouped = sorted.groupBy { Dates.startOfDay(it.occurredAt) }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                grouped.forEach { (day, dayEntries) ->
                    item(key = "day_$day") {
                        Text(
                            text = dayLabel(day),
                            style = MaterialTheme.typography.labelSmall,
                            color = InkSoft,
                            modifier = Modifier.padding(top = 10.dp, bottom = 2.dp)
                        )
                    }
                    items(dayEntries, key = { it.id }) { entry ->
                        RecordCard(entry = entry, onDelete = { onDelete(entry) })
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 18.dp)
                .size(58.dp)
                .shadow(14.dp, CircleShape, ambientColor = Coral, spotColor = Coral)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(CoralDeep, Coral, Color(0xFFFF9A3C))))
                .clickable(onClick = onAdd),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add record",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun RecordCard(entry: HealthEntry, onDelete: () -> Unit) {
    val accent = entry.type.accent()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(accent.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = entry.type.label.take(1),
                    style = MaterialTheme.typography.titleMedium,
                    color = accent,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.type.label,
                        style = MaterialTheme.typography.titleMedium,
                        color = Ink
                    )
                    if (entry.isAbnormal && entry.type != HealthEntryType.VOMIT) {
                        Spacer(Modifier.width(7.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(ErrorSoft)
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ABNORMAL",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                color = ErrorInk
                            )
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = Dates.formatTime(entry.occurredAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = InkSoft
                    )
                }

                if (entry.amount != null && entry.type.unit != null) {
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = "${Dates.amountText(entry.amount)} ${entry.type.unit}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = accent
                    )
                }

                if (entry.note.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = entry.note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkSoft,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (entry.imagePath != null) {
                Spacer(Modifier.width(10.dp))
                LocalImage(
                    path = entry.imagePath,
                    contentDescription = "Record photo",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }

            Spacer(Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onDelete),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete record",
                    tint = InkSoft.copy(alpha = 0.65f),
                    modifier = Modifier.size(17.dp)
                )
            }
        }
    }
}

@Composable
private fun TrendsSection(
    entries: List<HealthEntry>,
    onLogWeight: () -> Unit,
    modifier: Modifier = Modifier
) {
    val weights = entries
        .filter { it.type == HealthEntryType.WEIGHT && it.amount != null }
        .sortedBy { it.occurredAt }
        .map { it.occurredAt to (it.amount ?: 0.0) }
        .takeLast(20)

    val monthStart = System.currentTimeMillis() - 30L * 86_400_000L
    val recent = entries.filter { it.occurredAt >= monthStart }
    val vomit = recent.count { it.type == HealthEntryType.VOMIT }
    val looseStool = recent.count { it.type == HealthEntryType.STOOL && it.isAbnormal }
    val urineIssue = recent.count { it.type == HealthEntryType.URINE && it.isAbnormal }
    val symptoms = listOf(
        Triple("Vomit", vomit, ErrorInk),
        Triple("Loose stool", looseStool, FurTanDeep),
        Triple("Urine issue", urineIssue, Sky)
    )
    val maxSymptom = symptoms.maxOfOrNull { it.second } ?: 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        SectionCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Weight trend",
                        style = MaterialTheme.typography.titleMedium,
                        color = Ink
                    )
                    Text(
                        text = "Last ${weights.size.coerceAtLeast(0)} readings",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkSoft
                    )
                }
                SoftButton(text = "LOG WEIGHT", onClick = onLogWeight)
            }

            Spacer(Modifier.height(12.dp))

            if (weights.isEmpty()) {
                Text(
                    text = "No weight entries yet. Log a weight record to draw the curve.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkSoft
                )
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatTile(
                        value = Dates.weightText(weights.last().second),
                        label = "LATEST",
                        accent = Mint,
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        value = "${weights.size}",
                        label = "READINGS",
                        accent = Coral,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(12.dp))
                WeightChart(points = weights)
            }
        }

        Spacer(Modifier.height(14.dp))

        SectionCard {
            Text(
                text = "Symptoms · last 30 days",
                style = MaterialTheme.typography.titleMedium,
                color = Ink
            )
            Spacer(Modifier.height(12.dp))
            symptoms.forEach { (label, count, accent) ->
                SymptomBar(
                    label = label,
                    count = count,
                    maxCount = maxSymptom,
                    accent = accent
                )
                Spacer(Modifier.height(9.dp))
            }
            if (maxSymptom == 0) {
                Text(
                    text = "No symptoms logged in the last 30 days.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkSoft
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        SectionCard {
            Text(
                text = "At a glance",
                style = MaterialTheme.typography.titleMedium,
                color = Ink
            )
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile(
                    value = recent.size.toString(),
                    label = "RECORDS · 30D",
                    accent = Lavender,
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    value = (vomit + looseStool + urineIssue).toString(),
                    label = "SYMPTOM EVENTS",
                    accent = Mango,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(30.dp))
    }
}

private fun dayLabel(dayStart: Long): String {
    val relative = Dates.relativeDay(dayStart)
    return if (relative == "Today" || relative == "Yesterday") {
        relative.uppercase()
    } else {
        Dates.formatDate(dayStart).uppercase()
    }
}

private fun HealthEntryType.accent(): Color = when (this) {
    HealthEntryType.MEAL -> Mango
    HealthEntryType.WATER -> Sky
    HealthEntryType.STOOL -> FurTanDeep
    HealthEntryType.URINE -> Lavender
    HealthEntryType.VOMIT -> ErrorInk
    HealthEntryType.WEIGHT -> Mint
    HealthEntryType.VET_VISIT -> Coral
    HealthEntryType.VACCINE -> Mint
    HealthEntryType.DEWORMING -> FurTanDeep
    HealthEntryType.MEDICATION -> Lavender
    HealthEntryType.SURGERY -> ErrorInk
    HealthEntryType.CHECKUP -> Mango
}
