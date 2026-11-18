package com.example.pethealthmanager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.pethealthmanager.ui.theme.Coral
import com.example.pethealthmanager.ui.theme.InkSoft
import com.example.pethealthmanager.viewmodel.HomeTab

@Composable
fun AppBottomBar(
    selected: HomeTab,
    onSelect: (HomeTab) -> Unit
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        shadowElevation = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            HomeTab.entries.forEach { tab ->
                val active = tab == selected
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (active) Coral.copy(alpha = 0.10f) else Color.Transparent)
                        .clickable { onSelect(tab) }
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val tint = if (active) Coral else InkSoft
                    when (tab) {
                        HomeTab.PETS -> PawPrint(color = tint, modifier = Modifier.size(21.dp))
                        HomeTab.HEALTH -> TabIcon(Icons.Filled.Favorite, tint)
                        HomeTab.REMINDERS -> TabIcon(Icons.Filled.Notifications, tint)
                        HomeTab.PROFILE -> TabIcon(Icons.Filled.Person, tint)
                    }
                    Spacer(Modifier.size(4.dp))
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = tint
                    )
                }
            }
        }
    }
}

@Composable
private fun TabIcon(icon: ImageVector, tint: Color) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(21.dp)
    )
}
