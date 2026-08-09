package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entities.MaintenanceRecord
import com.example.data.entities.Motorcycle
import com.example.data.entities.ServiceReminder
import com.example.ui.components.BadgeChip
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.CardDark
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.TextButton

@Composable
fun MaintenanceScreen(
    motorcycle: Motorcycle?,
    maintenanceRecords: List<MaintenanceRecord>,
    reminders: List<ServiceReminder>,
    onAddLogClicked: () -> Unit,
    onAddReminderClicked: () -> Unit,
    onCompleteReminder: (ServiceReminder) -> Unit,
    onDeleteLog: (MaintenanceRecord) -> Unit,
    onGenerateInvoice: (MaintenanceRecord?) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentOdometer = motorcycle?.odometerKm ?: 0

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddLogClicked,
                containerColor = AmberOrange,
                contentColor = Color.Black,
                modifier = Modifier.testTag("fab_add_maintenance_log")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Log Service")
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
                .testTag("maintenance_screen_list"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Odometer Tracker Header
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CURRENT ODOMETER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary,
                                    letterSpacing = 1.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$currentOdometer km",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AmberOrange,
                                    fontSize = 24.sp
                                )
                            )
                            Text(
                                text = motorcycle?.name ?: "Primary Bike",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(AmberOrange.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = AmberOrange, modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }

            // Automated Service Interval Reminders Header & List
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = AmberOrange, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AUTOMATED SERVICE REMINDERS",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    OutlinedButton(
                        onClick = onAddReminderClicked,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = AmberOrange, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Reminder", color = TextPrimary, fontSize = 11.sp)
                    }
                }
            }

            if (reminders.isEmpty()) {
                item {
                    Text(
                        text = "No service interval reminders configured.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            } else {
                items(reminders) { reminder ->
                    val nextDueKm = reminder.lastServiceKm + reminder.intervalKm
                    val kmRemaining = nextDueKm - currentOdometer

                    val (statusText, statusColor) = when {
                        kmRemaining < 0 -> "OVERDUE by ${-kmRemaining} km!" to CrimsonRed
                        kmRemaining <= 500 -> "DUE SOON (${kmRemaining} km left)" to AmberOrange
                        else -> "OK (${kmRemaining} km left)" to EmeraldGreen
                    }

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
                                    text = reminder.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )

                                BadgeChip(text = statusText, color = statusColor)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Interval: Every ${reminder.intervalKm} km • Last done at ${reminder.lastServiceKm} km • Next due at ${nextDueKm} km",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp)
                            )

                            if (reminder.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = reminder.notes,
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = { onCompleteReminder(reminder) },
                                colors = ButtonDefaults.buttonColors(containerColor = statusColor),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Mark Service Performed Now", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Maintenance Log History Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MAINTENANCE SERVICE LOGS (${maintenanceRecords.size})",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(
                            onClick = { onGenerateInvoice(null) },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = AmberOrange, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Create Invoice", color = TextPrimary, fontSize = 11.sp)
                        }
                    }
                }
            }

            if (maintenanceRecords.isEmpty()) {
                item {
                    Text(
                        text = "No maintenance logs recorded yet.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            } else {
                items(maintenanceRecords) { record ->
                    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    val dateStr = dateFormat.format(Date(record.serviceDate))

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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = record.serviceType,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "$dateStr • ${record.odometerKm} km • ${record.performedBy}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp)
                                    )
                                    if (record.notes.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = record.notes,
                                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "$${String.format("%.2f", record.cost)}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = AmberOrange
                                        )
                                    )
                                    IconButton(onClick = { onDeleteLog(record) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Log", tint = CrimsonRed, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = { onGenerateInvoice(record) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = TechCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Generate Workshop Invoice", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
