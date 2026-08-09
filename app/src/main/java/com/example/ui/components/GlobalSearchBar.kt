package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TwoWheeler
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
import com.example.data.entities.BuildProject
import com.example.data.entities.MaintenanceRecord
import com.example.data.entities.MarketplaceItem
import com.example.data.entities.Modification
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.CardDark
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletPurple
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class GlobalSearchCategory(val title: String) {
    ALL("All Results"),
    BUILDS("Builds & Mods"),
    MARKETPLACE("Marketplace Parts"),
    MAINTENANCE("Maintenance Logs")
}

@Composable
fun GlobalSearchBarCard(
    query: String,
    onQueryChange: (String) -> Unit,
    buildProjects: List<BuildProject>,
    modifications: List<Modification>,
    marketplaceItems: List<MarketplaceItem>,
    maintenanceRecords: List<MaintenanceRecord>,
    onBuyMarketplaceItem: (MarketplaceItem) -> Unit,
    onPayMaintenanceRecord: (MaintenanceRecord) -> Unit,
    onGenerateInvoiceRecord: (MaintenanceRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(GlobalSearchCategory.ALL) }

    val filteredBuilds = remember(query, buildProjects) {
        if (query.isBlank()) emptyList()
        else buildProjects.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.notes.contains(query, ignoreCase = true) ||
            it.status.contains(query, ignoreCase = true)
        }
    }

    val filteredMods = remember(query, modifications) {
        if (query.isBlank()) emptyList()
        else modifications.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.brand.contains(query, ignoreCase = true) ||
            it.notes.contains(query, ignoreCase = true) ||
            it.category.name.contains(query, ignoreCase = true)
        }
    }

    val filteredParts = remember(query, marketplaceItems) {
        if (query.isBlank()) emptyList()
        else marketplaceItems.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.description.contains(query, ignoreCase = true) ||
            it.fitment.contains(query, ignoreCase = true) ||
            it.category.contains(query, ignoreCase = true) ||
            it.sellerName.contains(query, ignoreCase = true)
        }
    }

    val filteredMaint = remember(query, maintenanceRecords) {
        if (query.isBlank()) emptyList()
        else maintenanceRecords.filter {
            it.serviceType.contains(query, ignoreCase = true) ||
            it.description.contains(query, ignoreCase = true) ||
            it.linkedPartName.contains(query, ignoreCase = true) ||
            it.performedBy.contains(query, ignoreCase = true)
        }
    }

    val totalMatches = filteredBuilds.size + filteredMods.size + filteredParts.size + filteredMaint.size

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, if (query.isNotBlank()) AmberOrange else CardBorderDark, RoundedCornerShape(16.dp))
            .animateContentSize()
            .testTag("global_search_bar_card")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Search Text Field
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Global search builds, parts, maintenance logs...", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Global Search", tint = AmberOrange) },
                trailingIcon = {
                    if (query.isNotBlank()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF12161A),
                    unfocusedContainerColor = Color(0xFF12161A),
                    focusedBorderColor = AmberOrange,
                    unfocusedBorderColor = CardBorderDark,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("global_search_text_input")
            )

            if (query.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))

                // Category Filter Pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(GlobalSearchCategory.values()) { cat ->
                        val isSelected = selectedCategory == cat
                        val count = when (cat) {
                            GlobalSearchCategory.ALL -> totalMatches
                            GlobalSearchCategory.BUILDS -> filteredBuilds.size + filteredMods.size
                            GlobalSearchCategory.MARKETPLACE -> filteredParts.size
                            GlobalSearchCategory.MAINTENANCE -> filteredMaint.size
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AmberOrange else Color(0xFF1B222B))
                                .border(1.dp, if (isSelected) AmberOrange else CardBorderDark, RoundedCornerShape(8.dp))
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${cat.title} ($count)",
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.Black else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (totalMatches == 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No matching builds, parts, or maintenance logs found for \"$query\"",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Section 1: Saved Builds & Modifications
                        if ((selectedCategory == GlobalSearchCategory.ALL || selectedCategory == GlobalSearchCategory.BUILDS) &&
                            (filteredBuilds.isNotEmpty() || filteredMods.isNotEmpty())
                        ) {
                            Text("SAVED BUILDS & MODIFICATIONS (${filteredBuilds.size + filteredMods.size})", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TechCyan)

                            filteredBuilds.forEach { build ->
                                SearchResultBuildItem(build = build)
                            }

                            filteredMods.forEach { mod ->
                                SearchResultModItem(mod = mod)
                            }
                        }

                        // Section 2: Marketplace Parts
                        if ((selectedCategory == GlobalSearchCategory.ALL || selectedCategory == GlobalSearchCategory.MARKETPLACE) &&
                            filteredParts.isNotEmpty()
                        ) {
                            Text("MARKETPLACE PARTS (${filteredParts.size})", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AmberOrange)

                            filteredParts.forEach { item ->
                                SearchResultMarketplaceItem(
                                    item = item,
                                    onBuyNow = { onBuyMarketplaceItem(item) }
                                )
                            }
                        }

                        // Section 3: Maintenance Records
                        if ((selectedCategory == GlobalSearchCategory.ALL || selectedCategory == GlobalSearchCategory.MAINTENANCE) &&
                            filteredMaint.isNotEmpty()
                        ) {
                            Text("MAINTENANCE LOGS (${filteredMaint.size})", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)

                            filteredMaint.forEach { record ->
                                SearchResultMaintenanceItem(
                                    record = record,
                                    onPayOnline = { onPayMaintenanceRecord(record) },
                                    onGenerateInvoice = { onGenerateInvoiceRecord(record) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultBuildItem(build: BuildProject) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF181F27))
            .border(1.dp, CardBorderDark, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Speed, contentDescription = null, tint = TechCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = build.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = "Target Date: ${build.targetCompletionDate} • Status: ${build.status}", color = TextMuted, fontSize = 10.sp)
                }
            }
            Text(text = "$${String.format("%.2f", build.targetBudget)}", color = TechCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
private fun SearchResultModItem(mod: Modification) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF181F27))
            .border(1.dp, CardBorderDark, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TwoWheeler, contentDescription = null, tint = VioletPurple, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "${mod.brand} ${mod.title}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = "Category: ${mod.category.name.replace("_", " ")} • Status: ${mod.status.name}", color = TextMuted, fontSize = 10.sp)
                }
            }
            Text(text = "$${String.format("%.2f", mod.cost)}", color = AmberOrange, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
private fun SearchResultMarketplaceItem(
    item: MarketplaceItem,
    onBuyNow: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF1A221C))
            .border(1.dp, EmeraldGreen.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = AmberOrange, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = item.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Text(text = "Fitment: ${item.fitment} • Seller: ${item.sellerName}", color = TextMuted, fontSize = 10.sp)
                Text(text = "$${String.format("%.2f", item.price)}", color = EmeraldGreen, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            }

            Button(
                onClick = onBuyNow,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.testTag("search_buy_now_btn_${item.id}")
            ) {
                Icon(Icons.Default.Payment, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Buy Now", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun SearchResultMaintenanceItem(
    record: MaintenanceRecord,
    onPayOnline: () -> Unit,
    onGenerateInvoice: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val totalCost = record.cost + record.linkedPartCost

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF1B232D))
            .border(1.dp, CardBorderDark, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Build, contentDescription = null, tint = TechCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = record.serviceType, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Text(text = "$${String.format("%.2f", totalCost)}", color = TechCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Text(text = "Mileage: ${record.mileage} km • Date: ${dateFormat.format(Date(record.date))}", color = TextMuted, fontSize = 10.sp)

            Spacer(modifier = Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                    onClick = onGenerateInvoice,
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = TechCyan, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PDF Invoice", color = TextPrimary, fontSize = 10.sp)
                }

                Button(
                    onClick = onPayOnline,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Default.Payment, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pay Online", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            }
        }
    }
}
