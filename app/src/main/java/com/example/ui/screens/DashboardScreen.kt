package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.entities.BuildProject
import com.example.data.entities.Motorcycle
import com.example.ui.components.BadgeChip
import com.example.ui.components.ProgressBarWithLabel
import com.example.ui.components.StatCard
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
import com.example.ui.viewmodel.BuildBudgetStats

@Composable
fun DashboardScreen(
    motorcycle: Motorcycle?,
    stats: BuildBudgetStats,
    buildProjects: List<BuildProject>,
    onAddProjectClicked: () -> Unit,
    onAddModClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("dashboard_screen_content"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Hero Banner Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorderDark, RoundedCornerShape(20.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_moto_hero_1786275363750),
                        contentDescription = "Motorcycle Hero",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        CardDark.copy(alpha = 0.85f),
                                        CardDark
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        BadgeChip(
                            text = motorcycle?.engineSpec ?: "Custom Build",
                            color = AmberOrange
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = motorcycle?.name ?: "Motorcycle Build SaaS",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 22.sp
                            )
                        )
                        Text(
                            text = "Model Year ${motorcycle?.year ?: 2023} • Odometer: ${motorcycle?.odometerKm ?: 0} km",
                            style = MaterialTheme.typography.bodySmall.color(TextSecondary)
                        )
                    }
                }
            }
        }

        // Quick Action Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onAddModClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = AmberOrange),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_add_mod_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Upgrade", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onAddProjectClicked,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_add_project_btn")
                ) {
                    Icon(Icons.Default.Build, contentDescription = null, tint = AmberOrange)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Project", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Performance Gains Summary Grid
        item {
            Text(
                text = "PERFORMANCE & GAINS SUMMARY",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Horsepower",
                    value = "+${String.format("%.1f", stats.totalHpGain)} HP",
                    subtitle = "${stats.installedModCount} Mods Installed",
                    icon = Icons.Default.ElectricBolt,
                    accentColor = AmberOrange,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Torque",
                    value = "+${String.format("%.1f", stats.totalTorqueGainNm)} Nm",
                    subtitle = "Dyno Optimized",
                    icon = Icons.Default.Speed,
                    accentColor = TechCyan,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Weight Saved",
                    value = "-${String.format("%.1f", stats.totalWeightSavedKg)} kg",
                    subtitle = "Lightweight",
                    icon = Icons.Default.FitnessCenter,
                    accentColor = EmeraldGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Budget Spending Dashboard
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardDark)
                    .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "BUILD SPENDING DASHBOARD",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "$${String.format("%.2f", stats.totalModSpent + stats.totalMaintenanceSpent)} Total Spent",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                            )
                        }

                        BadgeChip(
                            text = "Budget: $${String.format("%.0f", stats.totalBudget)}",
                            color = TechCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ProgressBarWithLabel(
                        label = "Overall Budget Used",
                        current = stats.totalModSpent + stats.totalMaintenanceSpent,
                        max = stats.totalBudget,
                        color = if (stats.totalModSpent + stats.totalMaintenanceSpent > stats.totalBudget) CrimsonRed else AmberOrange
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Breakdown by category
                    Text(
                        text = "Spending by Category",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    if (stats.categoryBreakdown.isEmpty()) {
                        Text(
                            text = "No modifications logged yet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    } else {
                        stats.categoryBreakdown.forEach { (catName, amount) ->
                            ProgressBarWithLabel(
                                label = catName,
                                current = amount,
                                max = stats.totalBudget,
                                color = when (catName) {
                                    "EXHAUST" -> AmberOrange
                                    "ENGINE ECU" -> TechCyan
                                    "SUSPENSION" -> EmeraldGreen
                                    "SEAT ERGONOMICS" -> VioletPurple
                                    else -> TextSecondary
                                }
                            )
                        }
                    }
                }
            }
        }

        // Active Build Projects
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CUSTOM BUILD PROJECTS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                )
                Button(
                    onClick = onAddProjectClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = CardBorderDark)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = AmberOrange, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Project", color = TextPrimary, fontSize = 12.sp)
                }
            }
        }

        if (buildProjects.isEmpty()) {
            item {
                Text(
                    text = "No custom build projects added yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        } else {
            items(buildProjects) { project ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = project.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            BadgeChip(
                                text = project.status,
                                color = EmeraldGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Target Date: ${project.targetCompletionDate} • Target Budget: $${String.format("%.0f", project.targetBudget)}",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                        if (project.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = project.notes,
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

// Extension helper for text style color
private fun androidx.compose.ui.text.TextStyle.color(color: Color) = this.copy(color = color)
