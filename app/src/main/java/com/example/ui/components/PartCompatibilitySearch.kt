package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.CardDark
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletPurple

fun launchGoogleSearch(context: Context, query: String) {
    if (query.isBlank()) return
    val encodedQuery = Uri.encode(query.trim())
    val searchUrl = "https://www.google.com/search?q=$encodedQuery"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl))
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not launch web browser for search.", Toast.LENGTH_SHORT).show()
    }
}

enum class SearchFocus(val label: String, val queryModifier: String) {
    OEM_FITMENT("OEM Fitment", "compatibility OEM fitment mounting"),
    FORUM_DISCUSSIONS("Owner Forums", "fitment forum user reviews installation issues"),
    ECU_TUNING("ECU & Tuning", "ECU flash tuning required stage 1 map"),
    MOUNTING_HARDWARE("Hardware & Brackets", "bracket slip-on mounting hardware specs")
}

@Composable
fun PartCompatibilitySearchCard(
    defaultBikeModel: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var bikeModel by remember { mutableStateOf(defaultBikeModel.ifBlank { "Yamaha MT-09 2023" }) }
    var partName by remember { mutableStateOf("") }
    var selectedFocus by remember { mutableStateOf(SearchFocus.OEM_FITMENT) }
    var isExpanded by remember { mutableStateOf(false) }

    val computedSearchQuery = remember(bikeModel, partName, selectedFocus) {
        buildString {
            if (partName.isNotBlank()) append("${partName.trim()} ")
            if (bikeModel.isNotBlank()) append("${bikeModel.trim()} ")
            append(selectedFocus.queryModifier)
        }.trim()
    }

    val presetPartExamples = listOf(
        "Akrapovic Full Exhaust System",
        "Ohlins STX46 Rear Shock",
        "Brembo RCS19 Master Cylinder",
        "Dynojet Power Commander V",
        "ASV Unbreakable Levers"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, CardBorderDark, RoundedCornerShape(20.dp))
            .animateContentSize()
            .testTag("google_part_compatibility_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(TechCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = TechCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "GOOGLE FITMENT VERIFIER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TechCyan,
                                    letterSpacing = 1.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            BadgeChip(text = "LIVE SEARCH", color = AmberOrange)
                        }
                        Text(
                            text = "Check Part Compatibility",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }
                }

                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.Build else Icons.Default.TwoWheeler,
                        contentDescription = "Toggle Options",
                        tint = AmberOrange
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Inputs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = bikeModel,
                    onValueChange = { bikeModel = it },
                    label = { Text("Bike Model & Year", color = TextMuted, fontSize = 11.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF141822),
                        unfocusedContainerColor = Color(0xFF141822),
                        focusedBorderColor = TechCyan,
                        unfocusedBorderColor = CardBorderDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_bike_model_compat")
                )

                OutlinedTextField(
                    value = partName,
                    onValueChange = { partName = it },
                    label = { Text("Aftermarket Part / SKU", color = TextMuted, fontSize = 11.sp) },
                    placeholder = { Text("e.g. Akrapovic Exhaust", color = TextMuted, fontSize = 11.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF141822),
                        unfocusedContainerColor = Color(0xFF141822),
                        focusedBorderColor = TechCyan,
                        unfocusedBorderColor = CardBorderDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("input_part_name_compat")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Preset Suggestions
            Text("QUICK SUGGESTIONS:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(presetPartExamples) { preset ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1B2230))
                            .border(1.dp, CardBorderDark, RoundedCornerShape(8.dp))
                            .clickable { partName = preset }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(preset, fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Focus Area Chips
            Text("SEARCH FOCUS:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SearchFocus.values().forEach { focus ->
                    val isSelected = selectedFocus == focus
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) TechCyan else Color(0xFF1B2230))
                            .clickable { selectedFocus = focus }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = focus.label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Google Query Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF12161F))
                    .border(1.dp, TechCyan.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("GOOGLE SEARCH QUERY:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TechCyan)
                        Text(
                            text = computedSearchQuery.ifBlank { "Enter part & bike model..." },
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = TextPrimary,
                            maxLines = 2
                        )
                    }

                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(computedSearchQuery))
                            Toast.makeText(context, "Search query copied!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Query", tint = TextSecondary, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Launch Action Button
            Button(
                onClick = {
                    val query = if (partName.isBlank()) "$bikeModel aftermarket part compatibility fitment" else computedSearchQuery
                    launchGoogleSearch(context, query)
                },
                colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("btn_launch_google_compat_search")
            ) {
                Icon(Icons.Default.OpenInNew, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "VERIFY COMPATIBILITY ON GOOGLE SEARCH",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun PartCompatibilitySearchDialog(
    bikeModel: String,
    partName: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var customPartName by remember { mutableStateOf(partName) }
    var customBikeModel by remember { mutableStateOf(bikeModel.ifBlank { "Yamaha MT-09" }) }
    var selectedFocus by remember { mutableStateOf(SearchFocus.OEM_FITMENT) }

    val query = remember(customBikeModel, customPartName, selectedFocus) {
        "$customPartName $customBikeModel ${selectedFocus.queryModifier}".trim()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Search, contentDescription = null, tint = TechCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Google Fitment Check", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Verify whether this aftermarket item is compatible with your bike model using live Google Search results.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                OutlinedTextField(
                    value = customBikeModel,
                    onValueChange = { customBikeModel = it },
                    label = { Text("Motorcycle Model & Year", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF141822),
                        unfocusedContainerColor = Color(0xFF141822),
                        focusedBorderColor = TechCyan,
                        unfocusedBorderColor = CardBorderDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("dialog_bike_model_input")
                )

                OutlinedTextField(
                    value = customPartName,
                    onValueChange = { customPartName = it },
                    label = { Text("Part Name / SKU", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF141822),
                        unfocusedContainerColor = Color(0xFF141822),
                        focusedBorderColor = TechCyan,
                        unfocusedBorderColor = CardBorderDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("dialog_part_name_input")
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF12161F))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Query: $query",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = TechCyan
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    launchGoogleSearch(context, query)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
                modifier = Modifier.testTag("dialog_confirm_google_search")
            ) {
                Icon(Icons.Default.OpenInNew, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Search Google", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
