package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.data.entities.MaintenanceRecord
import com.example.data.entities.SeatOrder
import com.example.ui.components.StyledTextField
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.CardDark
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.io.File
import java.io.FileOutputStream
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
    val customerEmail: String = "rider@example.com",
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
        sb.appendLine("CUSTOMER: $customerName ($customerEmail)")
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

    fun generateHtmlInvoice(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Invoice $invoiceNumber - ${companyDetails.companyName}</title>
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #0f1419; color: #e7e9ea; margin: 0; padding: 20px; }
                    .invoice-container { max-width: 720px; margin: 0 auto; background-color: #161e27; border: 1px solid #2f3336; border-radius: 12px; padding: 32px; box-shadow: 0 8px 24px rgba(0,0,0,0.5); }
                    .header { display: flex; justify-content: space-between; align-items: flex-start; border-bottom: 2px solid #ff9800; padding-bottom: 20px; margin-bottom: 24px; }
                    .company-name { font-size: 24px; font-weight: 800; color: #ff9800; letter-spacing: -0.5px; }
                    .company-info { font-size: 13px; color: #8b98a5; margin-top: 4px; line-height: 1.5; }
                    .badge { display: inline-block; background-color: #ff980022; color: #ff9800; border: 1px solid #ff9800; font-size: 11px; font-weight: 700; padding: 4px 10px; border-radius: 20px; text-transform: uppercase; }
                    .inv-number { font-size: 18px; font-weight: 700; color: #ffffff; margin-top: 6px; }
                    .grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; background-color: #1c2732; padding: 16px; border-radius: 8px; margin-bottom: 24px; border: 1px solid #2f3336; }
                    .label { font-size: 11px; text-transform: uppercase; color: #8b98a5; font-weight: 700; letter-spacing: 0.5px; }
                    .value { font-size: 14px; font-weight: 600; color: #ffffff; margin-top: 2px; }
                    table { width: 100%; border-collapse: collapse; margin-bottom: 24px; }
                    th { background-color: #253341; color: #ff9800; font-size: 12px; text-transform: uppercase; text-align: left; padding: 12px; font-weight: 700; }
                    td { padding: 14px 12px; border-bottom: 1px solid #2f3336; font-size: 14px; color: #e7e9ea; }
                    .totals-container { display: flex; justify-content: flex-end; margin-bottom: 24px; }
                    .totals-box { width: 280px; }
                    .total-row { display: flex; justify-content: space-between; font-size: 14px; color: #8b98a5; padding: 4px 0; }
                    .grand-total { display: flex; justify-content: space-between; font-size: 18px; font-weight: 800; color: #00e676; border-top: 2px solid #00e676; padding-top: 10px; margin-top: 8px; }
                    .notes-card { background-color: #1c2732; border-left: 4px solid #ff9800; padding: 14px; border-radius: 4px; font-size: 13px; color: #8b98a5; line-height: 1.5; }
                    .footer { text-align: center; margin-top: 32px; font-size: 12px; color: #6e767d; }
                </style>
            </head>
            <body>
                <div class="invoice-container">
                    <div class="header">
                        <div>
                            <div class="company-name">${companyDetails.companyName}</div>
                            <div class="company-info">Tax ID: ${companyDetails.taxId}</div>
                            <div class="company-info">${companyDetails.address}</div>
                            <div class="company-info">${companyDetails.phone} &bull; ${companyDetails.email}</div>
                        </div>
                        <div style="text-align: right;">
                            <span class="badge">Official Invoice</span>
                            <div class="inv-number"># $invoiceNumber</div>
                            <div class="company-info">Date: ${dateFormat.format(Date(date))}</div>
                        </div>
                    </div>

                    <div class="grid">
                        <div>
                            <div class="label">Customer / Rider</div>
                            <div class="value">$customerName ($customerEmail)</div>
                        </div>
                        <div>
                            <div class="label">Motorcycle & Mileage</div>
                            <div class="value">$bikeName ${if (mileageKm > 0) "($mileageKm km)" else ""}</div>
                        </div>
                    </div>

                    <table>
                        <thead>
                            <tr>
                                <th>Item</th>
                                <th>Description</th>
                                <th>Qty</th>
                                <th>Unit Price</th>
                                <th style="text-align: right;">Total</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${items.mapIndexed { index, item -> """
                                <tr>
                                    <td>${index + 1}</td>
                                    <td>${item.description}</td>
                                    <td>${item.quantity}</td>
                                    <td>$${String.format("%.2f", item.unitPrice)}</td>
                                    <td style="text-align: right;">$${String.format("%.2f", item.totalAmount)}</td>
                                </tr>
                            """.trimIndent() }.joinToString("\n")}
                        </tbody>
                    </table>

                    <div class="totals-container">
                        <div class="totals-box">
                            <div class="total-row"><span>Subtotal</span><span>$${String.format("%.2f", subtotal)}</span></div>
                            <div class="total-row"><span>Tax (${taxRatePercent}%)</span><span>$${String.format("%.2f", taxAmount)}</span></div>
                            <div class="grand-total"><span>Grand Total</span><span>$${String.format("%.2f", grandTotal)}</span></div>
                        </div>
                    </div>

                    ${if (notes.isNotBlank()) """<div class="notes-card"><strong>Notes & Terms:</strong> $notes</div>""" else ""}

                    <div class="footer">
                        Generated via MotoCraft Workshop System &bull; Thank you for your business!
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()
    }
}

object InvoiceGenerator {

    /**
     * Generates a PDF file on disk for the given ServiceInvoice using Android's native PdfDocument.
     */
    fun generatePdfFile(context: Context, invoice: ServiceInvoice): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 dimensions in points
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint()
        val titlePaint = Paint()
        val headerPaint = Paint()

        // Background
        canvas.drawColor(AndroidColor.WHITE)

        // Header Background Bar
        paint.color = AndroidColor.parseColor("#161E27")
        canvas.drawRect(0f, 0f, 595f, 110f, paint)

        // Title
        titlePaint.color = AndroidColor.parseColor("#FF9800")
        titlePaint.textSize = 20f
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(invoice.companyDetails.companyName.uppercase(), 30f, 45f, titlePaint)

        // Subtitle Info
        paint.color = AndroidColor.parseColor("#8B98A5")
        paint.textSize = 10f
        canvas.drawText("Tax ID: ${invoice.companyDetails.taxId} | Phone: ${invoice.companyDetails.phone}", 30f, 65f, paint)
        canvas.drawText("${invoice.companyDetails.address} | ${invoice.companyDetails.email}", 30f, 80f, paint)

        // Invoice Badge Right
        headerPaint.color = AndroidColor.WHITE
        headerPaint.textSize = 18f
        headerPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("INVOICE #${invoice.invoiceNumber}", 350f, 50f, headerPaint)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        paint.color = AndroidColor.parseColor("#00E676")
        canvas.drawText("Date: ${dateFormat.format(Date(invoice.date))}", 350f, 70f, paint)

        // Customer & Vehicle Info Box
        paint.color = AndroidColor.parseColor("#F0F4F8")
        canvas.drawRoundRect(30f, 130f, 565f, 210f, 8f, 8f, paint)

        paint.color = AndroidColor.parseColor("#1C2732")
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("CUSTOMER / RIDER:", 45f, 155f, paint)
        canvas.drawText("MOTORCYCLE / ODOMETER:", 310f, 155f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("${invoice.customerName} (${invoice.customerEmail})", 45f, 175f, paint)
        canvas.drawText("${invoice.bikeName} (${invoice.mileageKm} km)", 310f, 175f, paint)

        // Table Header
        paint.color = AndroidColor.parseColor("#253341")
        canvas.drawRect(30f, 230f, 565f, 255f, paint)

        headerPaint.color = AndroidColor.WHITE
        headerPaint.textSize = 10f
        canvas.drawText("#", 40f, 247f, headerPaint)
        canvas.drawText("SERVICE / PART DESCRIPTION", 70f, 247f, headerPaint)
        canvas.drawText("QTY", 380f, 247f, headerPaint)
        canvas.drawText("UNIT PRICE", 430f, 247f, headerPaint)
        canvas.drawText("TOTAL", 510f, 247f, headerPaint)

        // Table Items
        var currentY = 280f
        paint.color = AndroidColor.parseColor("#333333")
        paint.textSize = 10f

        invoice.items.forEachIndexed { index, item ->
            canvas.drawText("${index + 1}", 40f, currentY, paint)

            val desc = if (item.description.length > 50) item.description.take(47) + "..." else item.description
            canvas.drawText(desc, 70f, currentY, paint)
            canvas.drawText("${item.quantity}", 380f, currentY, paint)
            canvas.drawText("$${String.format("%.2f", item.unitPrice)}", 430f, currentY, paint)
            canvas.drawText("$${String.format("%.2f", item.totalAmount)}", 510f, currentY, paint)

            paint.color = AndroidColor.parseColor("#E0E0E0")
            canvas.drawLine(30f, currentY + 10f, 565f, currentY + 10f, paint)
            paint.color = AndroidColor.parseColor("#333333")

            currentY += 30f
        }

        // Totals Box Right
        val totalsStartY = currentY + 20f
        paint.color = AndroidColor.parseColor("#666666")
        canvas.drawText("Subtotal:", 380f, totalsStartY, paint)
        canvas.drawText("$${String.format("%.2f", invoice.subtotal)}", 510f, totalsStartY, paint)

        canvas.drawText("Tax (${invoice.taxRatePercent}%):", 380f, totalsStartY + 20f, paint)
        canvas.drawText("$${String.format("%.2f", invoice.taxAmount)}", 510f, totalsStartY + 20f, paint)

        paint.color = AndroidColor.parseColor("#00897B")
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 12f
        canvas.drawText("Grand Total:", 380f, totalsStartY + 45f, paint)
        canvas.drawText("$${String.format("%.2f", invoice.grandTotal)}", 510f, totalsStartY + 45f, paint)

        // Footer Notes
        if (invoice.notes.isNotBlank()) {
            paint.color = AndroidColor.parseColor("#F5F5F5")
            canvas.drawRoundRect(30f, 720f, 565f, 780f, 6f, 6f, paint)

            paint.color = AndroidColor.parseColor("#333333")
            paint.textSize = 9f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("NOTES & WORKSHOP WARRANTY:", 40f, 738f, paint)

            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            canvas.drawText(invoice.notes.take(90), 40f, 758f, paint)
        }

        // Footer Brand
        paint.color = AndroidColor.parseColor("#999999")
        paint.textSize = 8f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Generated by MotoCraft Workshop Management System • www.motocraft.app", 130f, 815f, paint)

        pdfDocument.finishPage(page)

        // Save PDF file
        val invoicesDir = File(context.cacheDir, "invoices")
        if (!invoicesDir.exists()) invoicesDir.mkdirs()

        val pdfFile = File(invoicesDir, "Invoice_${invoice.invoiceNumber}.pdf")
        FileOutputStream(pdfFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        return pdfFile
    }

    /**
     * Generates a PDF bill and opens the user's preferred Email app pre-filled with recipient address,
     * invoice subject, body summary, and attached PDF document.
     */
    fun sendInvoicePdfByEmail(context: Context, invoice: ServiceInvoice, recipientEmail: String) {
        try {
            val pdfFile = generatePdfFile(context, invoice)
            val pdfUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val subject = "Invoice #${invoice.invoiceNumber} - ${invoice.companyDetails.companyName}"
            val bodyText = buildString {
                append("Dear ${invoice.customerName},\n\n")
                append("Please find attached your official PDF invoice for recent service on your motorcycle (${invoice.bikeName}).\n\n")
                append("Invoice #: ${invoice.invoiceNumber}\n")
                append("Subtotal: $${String.format("%.2f", invoice.subtotal)}\n")
                append("Tax (${invoice.taxRatePercent}%): $${String.format("%.2f", invoice.taxAmount)}\n")
                append("Grand Total: $${String.format("%.2f", invoice.grandTotal)}\n\n")
                if (invoice.notes.isNotBlank()) {
                    append("Notes: ${invoice.notes}\n\n")
                }
                append("Thank you for choosing ${invoice.companyDetails.companyName}!\n\n")
                append("Best regards,\n${invoice.companyDetails.companyName} Team")
            }

            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail.ifBlank { invoice.customerEmail }))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, bodyText)
                putExtra(Intent.EXTRA_STREAM, pdfUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(emailIntent, "Send PDF Bill via Email...")
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "Error creating/sending PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun shareInvoiceDocument(context: Context, invoice: ServiceInvoice) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, "Workshop Invoice ${invoice.invoiceNumber} - ${invoice.bikeName}")
            putExtra(Intent.EXTRA_TEXT, invoice.generatePlainTextInvoice())
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Invoice Document")
        context.startActivity(shareIntent)
    }

    fun shareInvoiceHtml(context: Context, invoice: ServiceInvoice) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, "Workshop Invoice ${invoice.invoiceNumber} (HTML)")
            putExtra(Intent.EXTRA_TEXT, invoice.generateHtmlInvoice())
            type = "text/html"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share HTML Invoice Document")
        context.startActivity(shareIntent)
    }

    fun createFromMaintenanceLog(
        record: MaintenanceRecord,
        bikeName: String,
        company: CompanyDetails
    ): ServiceInvoice {
        val invoiceNo = "INV-MC-${(System.currentTimeMillis() % 100000)}"
        val items = mutableListOf<InvoiceLineItem>()

        items.add(
            InvoiceLineItem(
                description = record.serviceType + if (record.description.isNotBlank()) " - ${record.description}" else "",
                quantity = 1.0,
                unitPrice = record.cost
            )
        )

        if (record.linkedPartName.isNotBlank()) {
            items.add(
                InvoiceLineItem(
                    description = "Aftermarket Part: ${record.linkedPartName}",
                    quantity = 1.0,
                    unitPrice = record.linkedPartCost
                )
            )
        }

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

    fun createFromSeatOrder(
        order: SeatOrder,
        company: CompanyDetails
    ): ServiceInvoice {
        val invoiceNo = "INV-SEAT-${order.id.toString().padStart(4, '0')}"
        val lineItems = mutableListOf<InvoiceLineItem>()

        lineItems.add(
            InvoiceLineItem(
                description = "Custom Saddle Base & Materials (${order.coverMaterialName} - ${order.coverTexture}, ${order.colorOption})",
                quantity = 1.0,
                unitPrice = order.baseMaterialCost
            )
        )

        if (order.hasGelPad) {
            lineItems.add(
                InvoiceLineItem(
                    description = "Anatomical Medical Gel Insert (~${order.gelPadAreaSqCm.toInt()} cm²)",
                    quantity = 1.0,
                    unitPrice = 45.0
                )
            )
        }

        lineItems.add(
            InvoiceLineItem(
                description = "Master Craft Upholstery & Shaping Labor (${order.ridingPosture} Spec, ${order.foamThicknessMm.toInt()}mm Foam)",
                quantity = 1.0,
                unitPrice = order.laborCost
            )
        )

        return ServiceInvoice(
            invoiceNumber = invoiceNo,
            date = order.orderDate,
            companyDetails = company,
            bikeName = order.motorcycleModel,
            mileageKm = 0,
            customerName = order.customerName,
            items = lineItems,
            taxRatePercent = company.defaultTaxRatePercent,
            notes = "Status: ${order.orderStatus.name} | Deposit Paid: $${String.format("%.2f", order.depositAmount)} | Remaining Balance: $${String.format("%.2f", order.balanceDue)}"
        )
    }
}

@Composable
fun InvoiceGeneratorDialog(
    initialRecord: MaintenanceRecord?,
    bikeName: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var companyName by remember { mutableStateOf("MotoCraft Custom Workshop LLC") }
    var taxId by remember { mutableStateOf("US-9842104-MC") }
    var companyAddress by remember { mutableStateOf("742 Performance Way, Speed City") }
    var companyPhone by remember { mutableStateOf("+1 (555) 019-2831") }
    var companyEmail by remember { mutableStateOf("service@motocraft.app") }
    var customerName by remember { mutableStateOf("Valued Rider") }
    var customerEmail by remember { mutableStateOf("rider@example.com") }
    var taxRateStr by remember { mutableStateOf("8.5") }

    var serviceDesc by remember { mutableStateOf(initialRecord?.serviceType ?: "Full Synthetic Oil & Filter Service") }
    var mileageStr by remember { mutableStateOf((initialRecord?.mileage ?: 12500).toString()) }
    var costStr by remember { mutableStateOf((initialRecord?.cost ?: 120.0).toString()) }
    var notesStr by remember { mutableStateOf("All torques set to OEM specifications. Certified road test complete.") }

    var isCompanyEditExpanded by remember { mutableStateOf(false) }
    var exportFormatIsHtml by remember { mutableStateOf(false) }
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
        customerEmail = customerEmail,
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

                // Section 2: Service & Customer Inputs
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StyledTextField(value = customerName, onValueChange = { customerName = it }, label = "Customer Name", modifier = Modifier.weight(1f), tag = "inv_cust_name")
                    StyledTextField(value = customerEmail, onValueChange = { customerEmail = it }, label = "Recipient Email", modifier = Modifier.weight(1.2f), tag = "inv_cust_email")
                }

                StyledTextField(value = serviceDesc, onValueChange = { serviceDesc = it }, label = "Service Description & Details", tag = "inv_svc_desc")

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StyledTextField(value = mileageStr, onValueChange = { mileageStr = it }, label = "Odometer (km)", keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f), tag = "inv_mileage")
                    StyledTextField(value = costStr, onValueChange = { costStr = it }, label = "Service Fee ($)", keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f), tag = "inv_cost")
                }

                StyledTextField(value = notesStr, onValueChange = { notesStr = it }, label = "Invoice Footer Notes / Terms", tag = "inv_notes")

                // Section 3: Live Formatted Invoice Preview & Export Options
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("PREVIEW FORMAT: ", color = TextMuted, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                TextButton(
                                    onClick = { exportFormatIsHtml = !exportFormatIsHtml },
                                    modifier = Modifier.testTag("toggle_format_btn")
                                ) {
                                    Icon(if (exportFormatIsHtml) Icons.Default.Code else Icons.Default.Description, contentDescription = null, tint = AmberOrange, modifier = Modifier.padding(end = 4.dp))
                                    Text(if (exportFormatIsHtml) "HTML Document" else "Plain Text", color = AmberOrange, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }

                            Text("Total: $${String.format("%.2f", currentInvoice.grandTotal)}", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (exportFormatIsHtml) currentInvoice.generateHtmlInvoice() else currentInvoice.generatePlainTextInvoice(),
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }

                if (invoiceCopiedNotification) {
                    Text(
                        text = "✓ Document copied to Clipboard!",
                        color = EmeraldGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // PDF Email Action Button
                Button(
                    onClick = {
                        InvoiceGenerator.sendInvoicePdfByEmail(context, currentInvoice, customerEmail)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("send_pdf_email_btn")
                ) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SEND PDF BILL TO EMAIL", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val textToCopy = if (exportFormatIsHtml) currentInvoice.generateHtmlInvoice() else currentInvoice.generatePlainTextInvoice()
                            clipboardManager.setText(AnnotatedString(textToCopy))
                            invoiceCopiedNotification = true
                        },
                        modifier = Modifier.weight(1f).testTag("copy_invoice_btn")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy", color = TextPrimary, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            if (exportFormatIsHtml) {
                                InvoiceGenerator.shareInvoiceHtml(context, currentInvoice)
                            } else {
                                InvoiceGenerator.shareInvoiceDocument(context, currentInvoice)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberOrange),
                        modifier = Modifier.weight(1f).testTag("share_invoice_doc_btn")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextSecondary)
            }
        }
    )
}
