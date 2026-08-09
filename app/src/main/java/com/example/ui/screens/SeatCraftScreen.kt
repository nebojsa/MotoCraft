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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Square
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.entities.MaterialType
import com.example.data.entities.SeatMaterial
import com.example.ui.components.BadgeChip
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.CardDark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletPurple
import com.example.ui.viewmodel.SeatCalculatorResult

@Composable
fun SeatCraftScreen(
    materials: List<SeatMaterial>,
    onCalculateSpec: (Double, Double, Double, String) -> SeatCalculatorResult,
    onAddMaterialClicked: () -> Unit,
    onAdjustQuantity: (SeatMaterial, Double) -> Unit,
    onDeleteMaterial: (SeatMaterial) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTypeFilter by remember { mutableStateOf<MaterialType?>(null) }

    // Calculator parameters state
    var riderWeightKg by remember { mutableStateOf(85.0) }
    var seatLengthCm by remember { mutableStateOf(55.0) }
    var seatWidthCm by remember { mutableStateOf(28.0) }
    var ridingStyle by remember { mutableStateOf("Touring / Adventure") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val calcResult = onCalculateSpec(riderWeightKg, seatLengthCm, seatWidthCm, ridingStyle)

    val filteredMaterials = if (selectedTypeFilter == null) {
        materials
    } else {
        materials.filter { it.type == selectedTypeFilter }
    }

    val lowStockCount = materials.count { it.quantityOnHand <= it.reorderLevel }
    val totalInventoryValue = materials.sumOf { it.quantityOnHand * it.unitCost }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddMaterialClicked,
                containerColor = AmberOrange,
                contentColor = Color.Black,
                modifier = Modifier.testTag("fab_add_material")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Material")
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
                .testTag("seat_craft_screen_list"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Hero Workshop Banner Card
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
                            .height(160.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_seat_craft_1786275376745),
                            contentDescription = "Seat Craft Workshop",
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
                            BadgeChip(text = "Seat Repair & Restructuring", color = AmberOrange)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Saddle Ergonomics & Materials",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 20.sp
                                )
                            )
                            Text(
                                text = "Custom Gel, High-Density Foam & Leather Inventory",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }
                    }
                }
            }

            // SEAT RESTRUCTURING SPEC CALCULATOR TOOL
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Handyman, contentDescription = null, tint = AmberOrange)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SEAT RESTRUCTURING PLANNER",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary,
                                    letterSpacing = 1.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Riding Style Selector
                        Box {
                            OutlinedButton(
                                onClick = { dropdownExpanded = true },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Ride Style: $ridingStyle", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                                    Text(text = "Change", color = AmberOrange, fontSize = 12.sp)
                                }
                            }

                            DropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false },
                                modifier = Modifier.background(CardDark)
                            ) {
                                listOf("Sport / Track", "Touring / Adventure", "Cafe Racer / Custom").forEach { style ->
                                    DropdownMenuItem(
                                        text = { Text(style, color = TextPrimary) },
                                        onClick = {
                                            ridingStyle = style
                                            dropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Rider Weight Slider
                        Text(
                            text = "Rider Weight: ${riderWeightKg.toInt()} kg",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        )
                        Slider(
                            value = riderWeightKg.toFloat(),
                            onValueChange = { riderWeightKg = it.toDouble() },
                            valueRange = 50f..130f,
                            colors = SliderDefaults.colors(
                                thumbColor = AmberOrange,
                                activeTrackColor = AmberOrange
                            )
                        )

                        // Seat Length Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Seat Length: ${seatLengthCm.toInt()} cm", style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary))
                            Text(text = "Seat Width: ${seatWidthCm.toInt()} cm", style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary))
                        }
                        Slider(
                            value = seatLengthCm.toFloat(),
                            onValueChange = { seatLengthCm = it.toDouble() },
                            valueRange = 30f..85f,
                            colors = SliderDefaults.colors(thumbColor = TechCyan, activeTrackColor = TechCyan)
                        )

                        // Generated Spec Output Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceDark)
                                .border(1.dp, CardBorderDark, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "CALCULATED RESTRUCTURE SPECIFICATIONS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AmberOrange,
                                        fontSize = 11.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Ergonomic Spec: ${calcResult.estimatedComfortRating}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                calcResult.recommendedMaterials.forEach { rec ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = rec, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // MATERIALS INVENTORY SECTION HEADER
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "WORKSHOP MATERIALS INVENTORY",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "Total Stock Value: $${String.format("%.2f", totalInventoryValue)}",
                            style = MaterialTheme.typography.bodySmall.copy(color = EmeraldGreen, fontWeight = FontWeight.SemiBold)
                        )
                    }

                    if (lowStockCount > 0) {
                        BadgeChip(
                            text = "$lowStockCount LOW STOCK WARNING",
                            color = CrimsonRed
                        )
                    }
                }
            }

            // Type Filters
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChipPill(
                            label = "All Materials",
                            isSelected = selectedTypeFilter == null,
                            onClick = { selectedTypeFilter = null }
                        )
                    }
                    items(MaterialType.values()) { type ->
                        FilterChipPill(
                            label = type.name.replace("_", " "),
                            isSelected = selectedTypeFilter == type,
                            onClick = { selectedTypeFilter = type }
                        )
                    }
                }
            }

            if (filteredMaterials.isEmpty()) {
                item {
                    Text(
                        text = "No seat materials in inventory.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            } else {
                items(filteredMaterials) { mat ->
                    MaterialItemCard(
                        material = mat,
                        onAdjustQty = { delta -> onAdjustQuantity(mat, delta) },
                        onDelete = { onDeleteMaterial(mat) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun MaterialItemCard(
    material: SeatMaterial,
    onAdjustQty: (Double) -> Unit,
    onDelete: () -> Unit
) {
    val isLowStock = material.quantityOnHand <= material.reorderLevel

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isLowStock) CrimsonRed.copy(alpha = 0.5f) else CardBorderDark, RoundedCornerShape(14.dp))
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
                            text = material.type.name.replace("_", " "),
                            color = VioletPurple
                        )
                        if (isLowStock) {
                            Spacer(modifier = Modifier.width(6.dp))
                            BadgeChip(text = "LOW STOCK", color = CrimsonRed)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = material.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Material", tint = CrimsonRed, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Spec/Grade: ${material.colorOrGrade} • Dim: ${material.dimensions}",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp)
            )

            if (material.assignedProject.isNotBlank()) {
                Text(
                    text = "Project: ${material.assignedProject}",
                    style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontSize = 12.sp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${String.format("%.1f", material.quantityOnHand)} ${material.unit}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isLowStock) CrimsonRed else AmberOrange,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = "$${String.format("%.2f", material.unitCost)} / ${material.unit}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onAdjustQty(-1.0) },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CardBorderDark)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Minus", tint = TextPrimary)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { onAdjustQty(1.0) },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AmberOrange)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Plus", tint = Color.Black)
                    }
                }
            }
        }
    }
}
