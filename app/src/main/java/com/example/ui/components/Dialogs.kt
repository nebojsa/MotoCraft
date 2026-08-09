package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.entities.MaterialType
import com.example.data.entities.ModCategory
import com.example.data.entities.ModStatus
import com.example.data.entities.PartCondition
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.CardDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AddBikeDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, model: String, year: Int, odometer: Int, budget: Double, engineSpec: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var yearStr by remember { mutableStateOf("2024") }
    var odoStr by remember { mutableStateOf("1000") }
    var budgetStr by remember { mutableStateOf("10000") }
    var engineSpec by remember { mutableStateOf("998cc Inline-4") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        title = { Text("Add New Motorcycle", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StyledTextField(value = name, onValueChange = { name = it }, label = "Bike Name (e.g. Yamaha R1M)", tag = "input_bike_name")
                StyledTextField(value = model, onValueChange = { model = it }, label = "Model Name", tag = "input_bike_model")
                StyledTextField(value = yearStr, onValueChange = { yearStr = it }, label = "Year", keyboardType = KeyboardType.Number, tag = "input_bike_year")
                StyledTextField(value = odoStr, onValueChange = { odoStr = it }, label = "Odometer (km)", keyboardType = KeyboardType.Number, tag = "input_bike_odo")
                StyledTextField(value = budgetStr, onValueChange = { budgetStr = it }, label = "Total Build Budget ($)", keyboardType = KeyboardType.Number, tag = "input_bike_budget")
                StyledTextField(value = engineSpec, onValueChange = { engineSpec = it }, label = "Engine Spec", tag = "input_bike_engine")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(
                            name,
                            model,
                            yearStr.toIntOrNull() ?: 2024,
                            odoStr.toIntOrNull() ?: 0,
                            budgetStr.toDoubleOrNull() ?: 5000.0,
                            engineSpec
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AmberOrange),
                modifier = Modifier.testTag("confirm_add_bike_btn")
            ) {
                Text("Add Bike", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}

@Composable
fun AddModDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        category: ModCategory,
        brand: String,
        cost: Double,
        status: ModStatus,
        hpGain: Double,
        torqueGain: Double,
        weightSaved: Double,
        notes: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(ModCategory.EXHAUST) }
    var categoryDropdown by remember { mutableStateOf(false) }
    var brand by remember { mutableStateOf("") }
    var costStr by remember { mutableStateOf("500") }
    var status by remember { mutableStateOf(ModStatus.PLANNED) }
    var statusDropdown by remember { mutableStateOf(false) }
    var hpStr by remember { mutableStateOf("0") }
    var torqueStr by remember { mutableStateOf("0") }
    var weightStr by remember { mutableStateOf("0") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        title = { Text("Log Performance Upgrade", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StyledTextField(value = title, onValueChange = { title = it }, label = "Modification Title", tag = "input_mod_title")
                StyledTextField(value = brand, onValueChange = { brand = it }, label = "Manufacturer / Brand", tag = "input_mod_brand")

                // Category dropdown
                Text(text = "Category: ${category.name.replace("_", " ")}", color = TextSecondary)
                OutlinedButton(
                    onClick = { categoryDropdown = true },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Select Category", color = AmberOrange)
                }
                DropdownMenu(expanded = categoryDropdown, onDismissRequest = { categoryDropdown = false }) {
                    ModCategory.values().forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name.replace("_", " ")) },
                            onClick = {
                                category = cat
                                categoryDropdown = false
                            }
                        )
                    }
                }

                StyledTextField(value = costStr, onValueChange = { costStr = it }, label = "Cost ($)", keyboardType = KeyboardType.Number, tag = "input_mod_cost")

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StyledTextField(value = hpStr, onValueChange = { hpStr = it }, label = "+HP Gain", keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f), tag = "input_mod_hp")
                    StyledTextField(value = torqueStr, onValueChange = { torqueStr = it }, label = "+Nm Torque", keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f), tag = "input_mod_torque")
                    StyledTextField(value = weightStr, onValueChange = { weightStr = it }, label = "-kg Saved", keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f), tag = "input_mod_weight")
                }

                StyledTextField(value = notes, onValueChange = { notes = it }, label = "Notes / Specs", tag = "input_mod_notes")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            title,
                            category,
                            brand,
                            costStr.toDoubleOrNull() ?: 0.0,
                            status,
                            hpStr.toDoubleOrNull() ?: 0.0,
                            torqueStr.toDoubleOrNull() ?: 0.0,
                            weightStr.toDoubleOrNull() ?: 0.0,
                            notes
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AmberOrange),
                modifier = Modifier.testTag("confirm_add_mod_btn")
            ) {
                Text("Save Upgrade", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}

@Composable
fun AddListingDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, category: String, price: Double, condition: PartCondition, fitment: String, description: String, sellerContact: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Exhaust") }
    var priceStr by remember { mutableStateOf("250") }
    var condition by remember { mutableStateOf(PartCondition.LIKE_NEW) }
    var fitment by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("user@motocraft.app") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        title = { Text("List Part on Marketplace", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StyledTextField(value = title, onValueChange = { title = it }, label = "Part Title", tag = "input_listing_title")
                StyledTextField(value = category, onValueChange = { category = it }, label = "Category (Exhaust, Brakes, Seat)", tag = "input_listing_cat")
                StyledTextField(value = priceStr, onValueChange = { priceStr = it }, label = "Price ($)", keyboardType = KeyboardType.Number, tag = "input_listing_price")
                StyledTextField(value = fitment, onValueChange = { fitment = it }, label = "Fitment Models / Years", tag = "input_listing_fitment")
                StyledTextField(value = description, onValueChange = { description = it }, label = "Part Description & Condition", tag = "input_listing_desc")
                StyledTextField(value = contact, onValueChange = { contact = it }, label = "Contact Email", tag = "input_listing_contact")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            title,
                            category,
                            priceStr.toDoubleOrNull() ?: 100.0,
                            condition,
                            fitment,
                            description,
                            contact
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AmberOrange),
                modifier = Modifier.testTag("confirm_create_listing_btn")
            ) {
                Text("Publish Listing", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}

@Composable
fun AddMaintenanceDialog(
    onDismiss: () -> Unit,
    onConfirm: (serviceType: String, odometer: Int, cost: Double, notes: String) -> Unit
) {
    var serviceType by remember { mutableStateOf("") }
    var odoStr by remember { mutableStateOf("12000") }
    var costStr by remember { mutableStateOf("80") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        title = { Text("Log Maintenance Service", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StyledTextField(value = serviceType, onValueChange = { serviceType = it }, label = "Service Performed (e.g. Oil Change)", tag = "input_maint_type")
                StyledTextField(value = odoStr, onValueChange = { odoStr = it }, label = "Odometer (km)", keyboardType = KeyboardType.Number, tag = "input_maint_odo")
                StyledTextField(value = costStr, onValueChange = { costStr = it }, label = "Service Cost ($)", keyboardType = KeyboardType.Number, tag = "input_maint_cost")
                StyledTextField(value = notes, onValueChange = { notes = it }, label = "Notes / Parts Used", tag = "input_maint_notes")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (serviceType.isNotBlank()) {
                        onConfirm(serviceType, odoStr.toIntOrNull() ?: 0, costStr.toDoubleOrNull() ?: 0.0, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AmberOrange)
            ) {
                Text("Log Service", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}

@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, intervalKm: Int, notes: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var intervalStr by remember { mutableStateOf("3000") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        title = { Text("Set Automated Service Reminder", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StyledTextField(value = title, onValueChange = { title = it }, label = "Reminder Title (e.g. Chain Lube)", tag = "input_reminder_title")
                StyledTextField(value = intervalStr, onValueChange = { intervalStr = it }, label = "Service Interval (Every X km)", keyboardType = KeyboardType.Number, tag = "input_reminder_interval")
                StyledTextField(value = notes, onValueChange = { notes = it }, label = "Service Notes", tag = "input_reminder_notes")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, intervalStr.toIntOrNull() ?: 3000, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AmberOrange)
            ) {
                Text("Set Reminder", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}

@Composable
fun AddMaterialDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, type: MaterialType, quantity: Double, unit: String, unitCost: Double, color: String, reorderLevel: Double, dimensions: String, project: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(MaterialType.HIGH_DENSITY_FOAM) }
    var typeDropdown by remember { mutableStateOf(false) }
    var qtyStr by remember { mutableStateOf("5") }
    var unit by remember { mutableStateOf("slabs") }
    var costStr by remember { mutableStateOf("30") }
    var color by remember { mutableStateOf("Black") }
    var reorderStr by remember { mutableStateOf("2") }
    var dimensions by remember { mutableStateOf("50cm x 40cm x 30mm") }
    var project by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        title = { Text("Add Seat Workshop Material", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StyledTextField(value = name, onValueChange = { name = it }, label = "Material Name", tag = "input_mat_name")

                // Type selector
                Text(text = "Type: ${type.name.replace("_", " ")}", color = TextSecondary)
                OutlinedButton(
                    onClick = { typeDropdown = true },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Select Material Type", color = AmberOrange)
                }
                DropdownMenu(expanded = typeDropdown, onDismissRequest = { typeDropdown = false }) {
                    MaterialType.values().forEach { t ->
                        DropdownMenuItem(
                            text = { Text(t.name.replace("_", " ")) },
                            onClick = {
                                type = t
                                typeDropdown = false
                            }
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StyledTextField(value = qtyStr, onValueChange = { qtyStr = it }, label = "Quantity", keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f), tag = "input_mat_qty")
                    StyledTextField(value = unit, onValueChange = { unit = it }, label = "Unit (slabs, sq ft, cans)", modifier = Modifier.weight(1f), tag = "input_mat_unit")
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StyledTextField(value = costStr, onValueChange = { costStr = it }, label = "Unit Cost ($)", keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f), tag = "input_mat_cost")
                    StyledTextField(value = reorderStr, onValueChange = { reorderStr = it }, label = "Reorder Threshold", keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f), tag = "input_mat_reorder")
                }

                StyledTextField(value = color, onValueChange = { color = it }, label = "Color / Spec Grade", tag = "input_mat_color")
                StyledTextField(value = dimensions, onValueChange = { dimensions = it }, label = "Dimensions / Size", tag = "input_mat_dim")
                StyledTextField(value = project, onValueChange = { project = it }, label = "Assigned Custom Project", tag = "input_mat_project")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(
                            name,
                            type,
                            qtyStr.toDoubleOrNull() ?: 1.0,
                            unit,
                            costStr.toDoubleOrNull() ?: 10.0,
                            color,
                            reorderStr.toDoubleOrNull() ?: 2.0,
                            dimensions,
                            project
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AmberOrange),
                modifier = Modifier.testTag("confirm_add_mat_btn")
            ) {
                Text("Add Material", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}

@Composable
fun AddProjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, budget: Double, targetDate: String, notes: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var budgetStr by remember { mutableStateOf("5000") }
    var targetDate by remember { mutableStateOf("Q4 2026") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        title = { Text("New Custom Build Project", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StyledTextField(value = name, onValueChange = { name = it }, label = "Project Name (e.g. Track Build)", tag = "input_project_name")
                StyledTextField(value = budgetStr, onValueChange = { budgetStr = it }, label = "Target Budget ($)", keyboardType = KeyboardType.Number, tag = "input_project_budget")
                StyledTextField(value = targetDate, onValueChange = { targetDate = it }, label = "Target Completion Date", tag = "input_project_date")
                StyledTextField(value = notes, onValueChange = { notes = it }, label = "Project Notes / Goals", tag = "input_project_notes")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, budgetStr.toDoubleOrNull() ?: 1000.0, targetDate, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AmberOrange),
                modifier = Modifier.testTag("confirm_add_project_btn")
            ) {
                Text("Create Project", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}

@Composable
fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier.fillMaxWidth(),
    tag: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextSecondary) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = CardDark,
            unfocusedContainerColor = CardDark,
            focusedBorderColor = AmberOrange,
            unfocusedBorderColor = CardBorderDark,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.testTag(tag)
    )
}
