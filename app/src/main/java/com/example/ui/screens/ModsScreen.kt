package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entities.ModCategory
import com.example.data.entities.ModStatus
import com.example.data.entities.Modification
import com.example.data.entities.Motorcycle
import com.example.ui.components.BadgeChip
import com.example.ui.components.PartCompatibilitySearchCard
import com.example.ui.components.PartCompatibilitySearchDialog
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.CardDark
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletPurple

@Composable
fun ModsScreen(
    modifications: List<Modification>,
    motorcycle: Motorcycle? = null,
    onAddModClicked: () -> Unit,
    onUpdateStatus: (Modification, ModStatus) -> Unit,
    onDeleteMod: (Modification) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryFilter by remember { mutableStateOf<ModCategory?>(null) }
    var selectedModForCompatCheck by remember { mutableStateOf<Modification?>(null) }

    val defaultBikeName = motorcycle?.let { "${it.year} ${it.name} ${it.model}".trim() } ?: "Yamaha MT-09 2023"

    val filteredMods = if (selectedCategoryFilter == null) {
        modifications
    } else {
        modifications.filter { it.category == selectedCategoryFilter }
    }

    val installed = filteredMods.filter { it.status == ModStatus.INSTALLED }
    val totalHp = installed.sumOf { it.hpGain }
    val totalTorque = installed.sumOf { it.torqueGainNm }
    val totalWeight = installed.sumOf { it.weightReductionKg }
    val totalCost = filteredMods.sumOf { it.cost }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddModClicked,
                containerColor = AmberOrange,
                contentColor = Color.Black,
                modifier = Modifier.testTag("fab_add_mod")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Upgrade")
            }
        },
        containerColor = Color.Transparent,
        modifier = modifier.fillMaxSize()
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .testTag("mods_screen_list"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Category Filter Bar
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChipPill(
                            label = "All Categories",
                            isSelected = selectedCategoryFilter == null,
                            onClick = { selectedCategoryFilter = null }
                        )
                    }
                    items(ModCategory.values()) { category ->
                        FilterChipPill(
                            label = category.name.replace("_", " "),
                            isSelected = selectedCategoryFilter == category,
                            onClick = { selectedCategoryFilter = category }
                        )
                    }
                }
            }

            // Google Part Fitment Check Tool
            item {
                PartCompatibilitySearchCard(defaultBikeModel = defaultBikeName)
            }

            // Overview Banner
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PERFORMANCE MODS OVERVIEW",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "Total Invested: $${String.format("%.2f", totalCost)}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AmberOrange
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            GainStatItem(label = "Power Gain", value = "+${String.format("%.1f", totalHp)} HP", icon = Icons.Default.ElectricBolt, color = AmberOrange)
                            GainStatItem(label = "Torque Gain", value = "+${String.format("%.1f", totalTorque)} Nm", icon = Icons.Default.Speed, color = TechCyan)
                            GainStatItem(label = "Weight Reduction", value = "-${String.format("%.1f", totalWeight)} kg", icon = Icons.Default.FitnessCenter, color = EmeraldGreen)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "UPGRADES & MODIFICATIONS (${filteredMods.size})",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                )
            }

            if (filteredMods.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No modifications logged in this category yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                    }
                }
            } else {
                items(filteredMods) { mod ->
                    ModCardItem(
                        mod = mod,
                        onUpdateStatus = { newStatus -> onUpdateStatus(mod, newStatus) },
                        onVerifyFitment = { selectedModForCompatCheck = mod },
                        onDelete = { onDeleteMod(mod) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    selectedModForCompatCheck?.let { mod ->
        PartCompatibilitySearchDialog(
            bikeModel = defaultBikeName,
            partName = "${mod.brand} ${mod.title}",
            onDismiss = { selectedModForCompatCheck = null }
        )
    }
}

@Composable
fun FilterChipPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) AmberOrange else CardDark)
            .border(1.dp, if (isSelected) AmberOrange else CardBorderDark, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.Black else TextSecondary,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
fun GainStatItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp))
        Text(text = label, style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = TextSecondary))
    }
}

@Composable
fun ModCardItem(
    mod: Modification,
    onUpdateStatus: (ModStatus) -> Unit,
    onVerifyFitment: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val statusColor = when (mod.status) {
        ModStatus.INSTALLED -> EmeraldGreen
        ModStatus.IN_PROGRESS -> TechCyan
        ModStatus.PLANNED -> AmberOrange
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BadgeChip(
                            text = mod.category.name.replace("_", " "),
                            color = VioletPurple
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = mod.brand,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = mod.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 16.sp
                        )
                    )
                }

                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = TextSecondary)
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier
                            .background(CardDark)
                            .border(1.dp, CardBorderDark, RoundedCornerShape(12.dp))
                    ) {
                        DropdownMenuItem(
                            text = { Text("Mark Installed", color = EmeraldGreen) },
                            onClick = {
                                onUpdateStatus(ModStatus.INSTALLED)
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Mark In Progress", color = TechCyan) },
                            onClick = {
                                onUpdateStatus(ModStatus.IN_PROGRESS)
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Mark Planned", color = AmberOrange) },
                            onClick = {
                                onUpdateStatus(ModStatus.PLANNED)
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Verify Google Fitment", color = TechCyan) },
                            onClick = {
                                onVerifyFitment()
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Upgrade", color = CrimsonRed) },
                            onClick = {
                                onDelete()
                                menuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Specs Row (+HP, +Torque, -Weight)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (mod.hpGain > 0) {
                    BadgeChip(text = "+${mod.hpGain} HP", color = AmberOrange)
                }
                if (mod.torqueGainNm > 0) {
                    BadgeChip(text = "+${mod.torqueGainNm} Nm", color = TechCyan)
                }
                if (mod.weightReductionKg > 0) {
                    BadgeChip(text = "-${mod.weightReductionKg} kg", color = EmeraldGreen)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${String.format("%.2f", mod.cost)}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        fontSize = 18.sp
                    )
                )

                BadgeChip(
                    text = mod.status.name.replace("_", " "),
                    color = statusColor
                )
            }

            if (mod.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = mod.notes,
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 12.sp)
                )
            }
        }
    }
}
