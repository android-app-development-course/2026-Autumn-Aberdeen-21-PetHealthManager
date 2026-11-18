package com.example.pethealthmanager.ui.screens.reminders

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.pethealthmanager.data.model.Pet
import com.example.pethealthmanager.data.model.Reminder
import com.example.pethealthmanager.data.model.ReminderKind
import com.example.pethealthmanager.ui.components.ConfirmDialog
import com.example.pethealthmanager.ui.components.EmptyState
import com.example.pethealthmanager.ui.components.ScreenHeader
import com.example.pethealthmanager.ui.components.SectionCard
import com.example.pethealthmanager.ui.components.SoftButton
import com.example.pethealthmanager.ui.theme.Coral
import com.example.pethealthmanager.ui.theme.CoralDeep
import com.example.pethealthmanager.ui.theme.CreamDeep
import com.example.pethealthmanager.ui.theme.ErrorInk
import com.example.pethealthmanager.ui.theme.FurTanDeep
import com.example.pethealthmanager.ui.theme.Ink
import com.example.pethealthmanager.ui.theme.InkSoft
import com.example.pethealthmanager.ui.theme.Lavender
import com.example.pethealthmanager.ui.theme.Mint
import com.example.pethealthmanager.ui.theme.Sky
import com.example.pethealthmanager.util.Dates
import com.example.pethealthmanager.viewmodel.RemindersViewModel

@Composable
fun RemindersScreen(
    viewModel: RemindersViewModel,
    pets: List<Pet>,
    selectedPetId: Long?,
    onSelectPet: (Long) -> Unit,
    onAddPet: () -> Unit,
    onAddReminder: () -> Unit,
    onEditReminder: (Long) -> Unit,
    onOpenPets: () -> Unit
) {
    val reminders by viewModel.reminders.collectAsStateWithLifecycle()
    var pendingDelete by remember { mutableStateOf<Reminder?>(null) }

    val context = androidx.compose.ui.platform.LocalContext.current
    var notificationsGranted by remember { mutableStateOf(hasNotificationPermission(context)) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> notificationsGranted = granted }

    Column(Modifier.fillMaxSize()) {
        ScreenHeader(
            kicker = "CARE ROUTINE",
            title = "Reminders",
            subtitle = "Vaccines, deworming and medication, always on schedule.",
            pets = pets,
            selectedPetId = selectedPetId,
            onSelectPet = onSelectPet,
            onAddPet = onAddPet
        )

        if (pets.isEmpty()) {
            EmptyState(
                title = "No pet profile yet",
                message = "Create a pet profile before setting up vaccine or medication reminders.",
                actionLabel = "CREATE PET PROFILE",
                onAction = onAddPet
            )
            return@Column
        }

        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 2.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!notificationsGranted) {
                    item(key = "permission") {
                        NotificationPermissionCard(
                            onAllow = {
                                if (Build.VERSION.SDK_INT >= 33) {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            }
                        )
                    }
                }

                if (reminders.isEmpty()) {
                    item(key = "empty") {
                        EmptyState(
                            title = "No reminders yet",
                            message = "Add a vaccine, deworming or medication reminder to get notified when it is due.",
                            actionLabel = "ADD REMINDER",
                            onAction = onAddReminder
                        )
                    }
                } else {
                    val now = System.currentTimeMillis()
                    val groups = listOf(
                        "OVERDUE" to reminders.filter { it.isOverdue(now) },
                        "DUE SOON" to reminders.filter {
                            it.enabled && it.nextDueAt > now && Dates.daysUntil(it.nextDueAt) <= 7
                        },
                        "UPCOMING" to reminders.filter {
                            it.enabled && it.nextDueAt > now && Dates.daysUntil(it.nextDueAt) > 7
                        },
                        "PAUSED" to reminders.filter { !it.enabled }
                    )
                    groups.forEach { (label, groupReminders) ->
                        if (groupReminders.isNotEmpty()) {
                            item(key = "group_$label") { GroupLabel(label, groupReminders.size) }
                            items(groupReminders, key = { it.id }) { reminder ->
                                ReminderCard(
                                    reminder = reminder,
                                    onEdit = { onEditReminder(reminder.id) },
                                    onDelete = { pendingDelete = reminder },
                                    onDone = { viewModel.markDone(reminder) },
                                    onToggle = { viewModel.setEnabled(reminder, !reminder.enabled) }
                                )
                            }
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
                    .clickable(onClick = onAddReminder),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add reminder",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }

    pendingDelete?.let { reminder ->
        ConfirmDialog(
            title = "Delete \"${reminder.title}\"?",
            message = "This reminder and its notification will be removed.",
            confirmLabel = "DELETE REMINDER",
            onConfirm = {
                viewModel.deleteReminder(reminder)
                pendingDelete = null
            },
            onDismiss = { pendingDelete = null }
        )
    }
}

@Composable
private fun NotificationPermissionCard(onAllow: () -> Unit) {
    SectionCard(container = CreamDeep, padding = 16.dp) {
        Text(
            text = "Turn on reminders",
            style = MaterialTheme.typography.titleMedium,
            color = Ink
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Allow notifications so vaccine and medication reminders can reach you on time.",
            style = MaterialTheme.typography.bodyMedium,
            color = InkSoft
        )
        Spacer(Modifier.height(12.dp))
        SoftButton(text = "ALLOW NOTIFICATIONS", onClick = onAllow)
    }
}

@Composable
private fun GroupLabel(label: String, count: Int) {
    Text(
        text = "$label · $count",
        style = MaterialTheme.typography.labelSmall,
        color = InkSoft,
        modifier = Modifier.padding(top = 10.dp, bottom = 2.dp)
    )
}

@Composable
private fun ReminderCard(
    reminder: Reminder,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDone: () -> Unit,
    onToggle: () -> Unit
) {
    val accent = reminder.kind.accent()
    val dueColor = when {
        !reminder.enabled -> InkSoft
        reminder.isOverdue() -> ErrorInk
        Dates.daysUntil(reminder.nextDueAt) <= 7 -> Coral
        else -> Mint
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        shadowElevation = 3.dp
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(accent.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = reminder.title.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = accent,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onEdit)
                ) {
                    Text(
                        text = reminder.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Ink,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = reminder.kind.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = InkSoft
                    )
                }

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onDelete),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete reminder",
                        tint = InkSoft.copy(alpha = 0.65f),
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = Dates.dueText(reminder.nextDueAt),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = dueColor,
                    modifier = Modifier.weight(1f)
                )
                if (!reminder.enabled) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(CreamDeep)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "PAUSED",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                            color = InkSoft
                        )
                    }
                }
            }

            Spacer(Modifier.height(2.dp))
            val detail = buildString {
                append(reminder.intervalText())
                if (reminder.dose.isNotBlank()) append(" · ${reminder.dose}")
                reminder.lastDoneAt?.let { append(" · last ${Dates.formatDate(it)}") }
            }
            Text(
                text = detail,
                style = MaterialTheme.typography.bodyMedium,
                color = InkSoft
            )

            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MiniAction(text = "DONE", color = Mint, onClick = onDone)
                MiniAction(
                    text = if (reminder.enabled) "PAUSE" else "RESUME",
                    color = InkSoft,
                    onClick = onToggle
                )
                MiniAction(text = "EDIT", color = Coral, onClick = onEdit)
            }
        }
    }
}

@Composable
private fun MiniAction(text: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
            color = color
        )
    }
}

private fun hasNotificationPermission(context: Context): Boolean =
    Build.VERSION.SDK_INT < 33 ||
        context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED

private fun ReminderKind.accent(): Color = when (this) {
    ReminderKind.VACCINE -> Mint
    ReminderKind.DEWORMING_INTERNAL,
    ReminderKind.DEWORMING_EXTERNAL,
    ReminderKind.DEWORMING_BOTH -> FurTanDeep
    ReminderKind.MEDICATION -> Lavender
    ReminderKind.CUSTOM -> Sky
}
