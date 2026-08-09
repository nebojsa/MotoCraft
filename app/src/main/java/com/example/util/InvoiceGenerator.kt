package com.example.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entities.MaintenanceRecord
import com.example.ui.components.StyledTextField
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.CardDark
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CompanyDetails(
    val companyName: String = "MotoCraft Custom & Workshop LLC",
    val taxId: String = "US-9842104-MC",
    val address: String = "742 Performance Way, Speed City",
    val phone: String = "+1 (555) 019-2831",
    val email: String = "service@motocraft.app",
    val website: String = "www.motocraft.app",
    val defaultTaxRatePercent: Double = 8.5
)

data class InvoiceLineItem(
    val description: String,
    val quantity: Double,
    val unitPrice: Double
) {
    val totalAmount: Double get() = quantity * unitPrice
}

data class ServiceInvoice(
    val invoiceNumber: String,
    val date: Long = System.currentTimeMillis(),
    val companyDetails: CompanyDetails,
    val bikeName: String,
    val mileageKm: Int,
    val customerName: String = "Valued Rider",
    val items: List<InvoiceLineItem>,
    val taxRatePercent: Double = 8.5,
    val notes: String = "Thank you for trusting MotoCraft Workshop with your machine!"
) {
    val subtotal: Double get() = items.sumOf { it.totalAmount }
    val taxAmount: Double get() = subtotal * (taxRatePercent / 100.0)
    val grandTotal: Double get() = subtotal + taxAmount

    fun generatePlainTextInvoice(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val sb = StringBuilder()
        sb.appendLine("==================================================")
        sb.appendLine("            WORKSHOP SERVICE INVOICE              ")
        sb.appendLine("==================================================")
        sb.appendLine(companyDetails.companyName.uppercase())
        sb.appendLine("Tax ID / EIN: ${companyDetails.taxId}")
        sb.appendLine("Address: ${companyDetails.address}")
        sb.appendLine("Phone: ${companyDetails.phone} | Email: ${companyDetails.email}")
        sb.appendLine("--------------------------------------------------")
        sb.appendLine("INVOICE #: $invoiceNumber")
        sb.appendLine("DATE: ${dateFormat.format(Date(date))}")
        sb.appendLine("CUSTOMER: $customerName")
        sb.appendLine("VEHICLE: $bikeName (Odometer: $mileageKm km)")
        sb.appendLine("--------------------------------------------------")
        sb.appendLine("ITEMS & SERVICE DESCRIPTION:")
        items.forEachIndexed { index, item ->
            sb.appendLine("${index + 1}. ${item.description}")
            sb.appendLine("   Qty: ${item.quantity} x $${String.format("%.2f", item.unitPrice)} = $${String.format("%.2f", item.totalAmount)}")
        }
        sb.appendLine("--------------------------------------------------")
        sb.appendLine("SUBTOTAL:     $${String.format("%.2f", subtotal)}")
        sb.appendLine("EST. TAX (${taxRatePercent}%): $${String.format("%.2f", taxAmount)}")
        sb.appendLine("GRAND TOTAL:  $${String.format("%.2f", grandTotal)}")
        sb.appendLine("==================================================")
        if (notes.isNotBlank()) {
            sb.appendLine("NOTES: $notes")
            sb.appendLine("==================================================")
        }
        return sb.toString()
    }
}

object InvoiceGenerator {

    fun createFromMaintenanceLog(
        record: MaintenanceRecord,
        bikeName: String,
        company: CompanyDetails
    ): ServiceInvoice {
        val invoiceNo = "INV-MC-${(System.currentTimeMillis() % 100000)}"
        val items = listOf(
            InvoiceLineItem(
                description = record.serviceType + if (record.description.isNotBlank()) " - ${record.description}" else "",
                quantity = 1.0,
                unitPrice = record.cost
            )
        )
        return ServiceInvoice(
            invoiceNumber = invoiceNo,
            date = record.date,
            companyDetails = company,
            bikeName = bikeName,
            mileageKm = record.mileage,
            items = items,
            taxRatePercent = company.defaultTaxRatePercent
        )
    }
}

@Composable
fun InvoiceGeneratorDialog(
    initialRecord: MaintenanceRecord?,
    bikeName: String,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current

    var companyName by remember { mutableStateOf("MotoCraft Custom Workshop LLC") }
    var taxId by remember { mutableStateOf("US-9842104-MC") }
    var companyAddress by remember { mutableStateOf("742 Performance Way, Speed City") }
    var companyPhone by remember { mutableStateOf("+1 (555) 019-2831") }
    var companyEmail by remember { mutableStateOf("service@motocraft.app") }
    var customerName by remember { mutableStateOf("Rider / Bike Owner") }
    var taxRateStr by remember { mutableStateOf("8.5") }

    var serviceDesc by remember { mutableStateOf(initialRecord?.serviceType ?: "Full Synthetic Oil & Filter Service") }
    var mileageStr by remember { mutableStateOf((initialRecord?.mileage ?: 12500).toString()) }
    var costStr by remember { mutableStateOf((initialRecord?.cost ?: 120.0).toString()) }
    var notesStr by remember { mutableStateOf("All torques set to OEM specifications. Certified road test complete.") }

    var isCompanyEditExpanded by remember { mutableStateOf(false) }
    var invoiceCopiedNotification by remember { mutableStateOf(false) }

    val taxRate = taxRateStr.toDoubleOrNull() ?: 8.5
    val mileage = mileageStr.toIntOrNull() ?: 0
    val cost = costStr.toDoubleOrNull() ?: 0.0

    val currentCompany = CompanyDetails(
        companyName = companyName,
        taxId = taxId,
        address = companyAddress,
        phone = companyPhone,
        email = companyEmail,
        defaultTaxRatePercent = taxRate
    )

    val currentInvoice = ServiceInvoice(
        invoiceNumber = "INV-MC-${(System.currentTimeMillis() % 100000)}",
        date = initialRecord?.date ?: System.currentTimeMillis(),
        companyDetails = currentCompany,
        bikeName = bikeName,
        mileageKm = mileage,
        customerName = customerName,
        items = listOf(
            InvoiceLineItem(description = serviceDesc, quantity = 1.0, unitPrice = cost)
        ),
        taxRatePercent = taxRate,
        notes = notesStr
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = AmberOrange)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Workshop Invoice Generator", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Section 1: Company Details Configuration Toggle
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E242B)),
                    modifier = Modifier.fillMaxWidth().border(1.dp, CardBorderDark, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Business, contentDescription = null, tint = TechCyan)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Company & Workshop Branding", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            TextButton(onClick = { isCompanyEditExpanded = !isCompanyEditExpanded }) {
                                Text(if (isCompanyEditExpanded) "Hide" else "Edit Details", color = TechCyan, fontSize = 12.sp)
                            }
                        }

                        if (isCompanyEditExpanded) {
                            Spacer(modifier = Modifier.height(6.dp))
                            StyledTextField(value = companyName, onValueChange = { companyName = it }, label = "Company Name", tag = "inv_co_name")
                            StyledTextField(value = taxId, onValueChange = { taxId = it }, label = "Tax ID / EIN", tag = "inv_co_taxid")
                            StyledTextField(value = companyAddress, onValueChange = { companyAddress = it }, label = "Workshop Address", tag = "inv_co_addr")
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                StyledTextField(value = companyPhone, onValueChange = { companyPhone = it }, label = "Phone", modifier = Modifier.weight(1f), tag = "inv_co_phone")
                                StyledTextField(value = companyEmail, onValueChange = { companyEmail = it }, label = "Email", modifier = Modifier.weight(1f), tag = "inv_co_email")
                            }
                            StyledTextField(value = taxRateStr, onValueChange = { taxRateStr = it }, label = "Tax Rate (%)", keyboardType = KeyboardType.Number, tag = "inv_co_tax")
                        } else {
                            Text(text = "$companyName • $taxId", color = TextSecondary, fontSize = 11.sp)
                            Text(text = "$companyAddress | $companyPhone", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }

                // Section 2: Service & Invoice Data Inputs
                StyledTextField(value = customerName, onValueChange = { customerName = it }, label = "Customer / Owner Name", tag = "inv_cust_name")
                StyledTextField(value = serviceDesc, onValueChange = { serviceDesc = it }, label = "Service Description & Details", tag = "inv_svc_desc")

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StyledTextField(value = mileageStr, onValueChange = { mileageStr = it }, label = "Odometer (km)", keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f), tag = "inv_mileage")
                    StyledTextField(value = costStr, onValueChange = { costStr = it }, label = "Service Fee ($)", keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f), tag = "inv_cost")
                }

                StyledTextField(value = notesStr, onValueChange = { notesStr = it }, label = "Invoice Footer Notes / Terms", tag = "inv_notes")

                // Section 3: Live Formatted Invoice Preview
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF12161A)),
                    modifier = Modifier.fillMaxWidth().border(1.dp, CardBorderDark, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("LIVE INVOICE PREVIEW", color = AmberOrange, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text("Total: $${String.format("%.2f", currentInvoice.grandTotal)}", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = currentInvoice.generatePlainTextInvoice(),
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }

                if (invoiceCopiedNotification) {
                    Text(
                        text = "✓ Formatted Invoice copied to Clipboard!",
                        color = EmeraldGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val fullText = currentInvoice.generatePlainTextInvoice()
                    clipboardManager.setText(AnnotatedString(fullText))
                    invoiceCopiedNotification = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = AmberOrange),
                modifier = Modifier.testTag("copy_invoice_btn")
            ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy & Export Invoice", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextSecondary)
            }
        }
    )
}
