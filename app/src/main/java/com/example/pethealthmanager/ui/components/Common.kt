package com.example.pethealthmanager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pethealthmanager.data.model.Pet
import com.example.pethealthmanager.ui.theme.Apricot
import com.example.pethealthmanager.ui.theme.Coral
import com.example.pethealthmanager.ui.theme.Cream
import com.example.pethealthmanager.ui.theme.CreamDeep
import com.example.pethealthmanager.ui.theme.ErrorInk
import com.example.pethealthmanager.ui.theme.Ink
import com.example.pethealthmanager.ui.theme.InkSoft
import com.example.pethealthmanager.ui.theme.Mango
import com.example.pethealthmanager.util.Dates

@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    padding: Dp = 18.dp,
    container: Color = Color.White,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = container,
        shadowElevation = 6.dp
    ) {
        Column(Modifier.padding(padding), content = content)
    }
}

@Composable
fun SoftButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    accent: Color = Coral
) {
    Box(
        modifier = modifier
            .height(46.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (enabled) accent.copy(alpha = 0.12f) else CreamDeep)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
            color = if (enabled) accent else InkSoft
        )
    }
}

@Composable
fun SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CreamDeep)
            .padding(4.dp)
    ) {
        options.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 1.dp)
                    .then(
                        if (selected) Modifier.shadow(3.dp, RoundedCornerShape(14.dp))
                        else Modifier
                    )
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (selected) Color.White else Color.Transparent)
                    .clickable { onSelect(index) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = if (selected) Coral else InkSoft
                )
            }
        }
    }
}

@Composable
fun PillChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) Coral else CreamDeep)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color.White else Ink
        )
    }
}

@Composable
fun DatePill(
    label: String,
    millis: Long?,
    onPick: () -> Unit,
    modifier: Modifier = Modifier,
    onClear: (() -> Unit)? = null
) {
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = InkSoft)
        Spacer(Modifier.height(7.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(1.dp, Apricot, RoundedCornerShape(16.dp))
                .clickable(onClick = onPick)
                .padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = millis?.let { Dates.formatDate(it) } ?: "Select a date",
                style = MaterialTheme.typography.bodyLarge,
                color = if (millis != null) Ink else Color(0xFFB9A79C),
                modifier = Modifier.weight(1f)
            )
            if (millis != null && onClear != null) {
                Text(
                    text = "CLEAR",
                    style = MaterialTheme.typography.labelSmall,
                    color = Coral,
                    modifier = Modifier.clickable(onClick = onClear)
                )
            }
        }
    }
}

@Composable
fun ScreenHeader(
    kicker: String,
    title: String,
    subtitle: String,
    pets: List<Pet>,
    selectedPetId: Long?,
    onSelectPet: (Long) -> Unit,
    onAddPet: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PawBadge(size = 40.dp)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Pet Health Manager",
                    style = MaterialTheme.typography.titleMedium,
                    color = Ink
                )
                Text(
                    text = kicker,
                    style = MaterialTheme.typography.labelSmall,
                    color = InkSoft
                )
            }
            if (pets.isNotEmpty()) {
                PetSwitcher(
                    pets = pets,
                    selectedPetId = selectedPetId,
                    onSelect = onSelectPet,
                    onAddPet = onAddPet
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.displaySmall,
            fontSize = 29.sp,
            lineHeight = 33.sp,
            color = Ink
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = InkSoft
        )
    }
}

@Composable
fun PetSwitcher(
    pets: List<Pet>,
    selectedPetId: Long?,
    onSelect: (Long) -> Unit,
    onAddPet: () -> Unit
) {
    if (pets.isEmpty()) return
    var expanded by remember { mutableStateOf(false) }
    val selected = pets.firstOrNull { it.id == selectedPetId } ?: pets.firstOrNull()

    Box {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color.White)
                .clickable { expanded = true }
                .padding(start = 5.dp, end = 9.dp, top = 5.dp, bottom = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PetAvatar(pet = selected, size = 28.dp)
            Spacer(Modifier.width(7.dp))
            Text(
                text = selected?.name.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 92.dp)
            )
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Switch pet",
                tint = InkSoft,
                modifier = Modifier.size(18.dp)
            )
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            pets.forEach { pet ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PetAvatar(pet = pet, size = 28.dp)
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = pet.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (pet.id == selected?.id) Coral else Ink
                            )
                        }
                    },
                    onClick = {
                        onSelect(pet.id)
                        expanded = false
                    }
                )
            }
            HorizontalDivider(color = CreamDeep)
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Add, contentDescription = null, tint = Coral)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "Add another pet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Coral
                        )
                    }
                },
                onClick = {
                    expanded = false
                    onAddPet()
                }
            )
        }
    }
}

@Composable
fun FormScreen(
    title: String,
    kicker: String,
    onBack: () -> Unit,
    primaryLabel: String? = null,
    primaryEnabled: Boolean = true,
    onPrimary: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        containerColor = Cream,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Ink,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Ink
                    )
                    Text(
                        text = kicker,
                        style = MaterialTheme.typography.labelSmall,
                        color = InkSoft
                    )
                }
            }
        },
        bottomBar = {
            if (primaryLabel != null) {
                Surface(color = Color.White, shadowElevation = 12.dp) {
                    Box(
                        Modifier
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        GradientButton(
                            text = primaryLabel,
                            onClick = onPrimary,
                            enabled = primaryEnabled
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            content = content
        )
    }
}

@Composable
fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    art: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        art?.invoke()
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = Ink,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = InkSoft,
            textAlign = TextAlign.Center
        )
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(20.dp))
            GradientButton(
                text = actionLabel,
                onClick = onAction,
                modifier = Modifier.fillMaxWidth(),
                height = 52.dp
            )
        }
    }
}

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(28.dp),
        title = { Text(title, style = MaterialTheme.typography.headlineSmall, color = Ink) },
        text = { Text(message, style = MaterialTheme.typography.bodyMedium, color = InkSoft) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmLabel, style = MaterialTheme.typography.labelSmall, color = ErrorInk)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", style = MaterialTheme.typography.labelSmall, color = InkSoft)
            }
        }
    )
}

@Composable
fun HintNote(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color = Mango
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(accent.copy(alpha = 0.13f))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PawPrint(color = accent, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Ink
        )
    }
}

@Composable
fun StatTile(
    value: String,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(accent.copy(alpha = 0.12f))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = accent
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = InkSoft
        )
    }
}

@Composable
fun LabelValueRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = InkSoft)
        Text(value, style = MaterialTheme.typography.bodyLarge, color = Ink)
    }
}
