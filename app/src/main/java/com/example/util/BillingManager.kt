package com.example.util

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entities.OrderStatus
import com.example.data.entities.PaymentStatus
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
import java.util.UUID

enum class OnlinePaymentMethod(val displayName: String, val providerName: String) {
    GOOGLE_PAY("Google Pay", "Google Pay 1-Tap Checkout"),
    CREDIT_CARD("Credit / Debit Card", "Stripe Gateway"),
    PAYPAL("PayPal Express", "PayPal Checkout"),
    APPLE_PAY("Apple Pay", "Apple Pay Digital Wallet"),
    SQUARE_PAY("Square Terminal", "Square POS / Online")
}

data class CardDetails(
    val cardNumber: String = "",
    val cardHolder: String = "",
    val expiryDate: String = "",
    val cvv: String = ""
)

data class PaymentResult(
    val isSuccess: Boolean,
    val transactionId: String = "",
    val amountPaid: Double = 0.0,
    val paymentMethod: OnlinePaymentMethod = OnlinePaymentMethod.GOOGLE_PAY,
    val timestamp: Long = System.currentTimeMillis(),
    val errorMessage: String? = null
)

data class SeatCostEstimation(
    val foamCost: Double,
    val gelPadCost: Double,
    val coverMaterialCost: Double,
    val laborCost: Double,
    val subtotal: Double,
    val estimatedTax: Double,
    val totalCost: Double,
    val suggestedDeposit: Double
)

data class WorkshopFinancialSummary(
    val totalRevenueCollected: Double,
    val pendingBalancesOutstanding: Double,
    val totalOrdersCount: Int,
    val completedOrdersCount: Int,
    val inProgressOrdersCount: Int
)

object BillingManager {

    fun calculateSeatBuildCost(
        bikerWeightKg: Double,
        foamThicknessMm: Double,
        hasGelPad: Boolean,
        coverMaterialUnitCost: Double,
        ridingPosture: String,
        taxRatePercent: Double = 8.5
    ): SeatCostEstimation {
        val foam = foamThicknessMm * 0.85
        val gel = if (hasGelPad) 45.0 else 0.0
        val cover = coverMaterialUnitCost * 3.5
        val labor = when (ridingPosture) {
            "Sport / Track" -> 110.0
            "Upright Cruiser / Chopper" -> 150.0
            "Cafe Racer / Custom" -> 160.0
            else -> 130.0
        }

        val subtotal = foam + gel + cover + labor
        val tax = subtotal * (taxRatePercent / 100.0)
        val grandTotal = subtotal + tax
        val deposit = (grandTotal * 0.40).coerceAtLeast(80.0)

        return SeatCostEstimation(
            foamCost = foam,
            gelPadCost = gel,
            coverMaterialCost = cover,
            laborCost = labor,
            subtotal = subtotal,
            estimatedTax = tax,
            totalCost = grandTotal,
            suggestedDeposit = deposit
        )
    }

    fun calculateWorkshopSummary(orders: List<SeatOrder>, taxRatePercent: Double = 8.5): WorkshopFinancialSummary {
        var totalCollected = 0.0
        var pendingBalance = 0.0
        var completed = 0
        var inProgress = 0

        orders.forEach { order ->
            val subtotalWithTax = order.subtotal * (1 + taxRatePercent / 100.0)
            when (order.paymentStatus) {
                PaymentStatus.PAID_IN_FULL -> {
                    totalCollected += subtotalWithTax
                }
                PaymentStatus.DEPOSIT_PAID -> {
                    totalCollected += order.depositAmount
                    pendingBalance += (subtotalWithTax - order.depositAmount).coerceAtLeast(0.0)
                }
                PaymentStatus.UNPAID -> {
                    pendingBalance += subtotalWithTax
                }
            }

            if (order.orderStatus == OrderStatus.COMPLETED || order.orderStatus == OrderStatus.DELIVERED) {
                completed++
            } else if (order.orderStatus == OrderStatus.IN_PROGRESS || order.orderStatus == OrderStatus.READY_FOR_FITTING) {
                inProgress++
            }
        }

        return WorkshopFinancialSummary(
            totalRevenueCollected = totalCollected,
            pendingBalancesOutstanding = pendingBalance,
            totalOrdersCount = orders.size,
            completedOrdersCount = completed,
            inProgressOrdersCount = inProgress
        )
    }

    /**
     * Simulates processing an online payment through Google Pay, Stripe, or PayPal
     */
    fun processOnlinePayment(
        amount: Double,
        method: OnlinePaymentMethod,
        cardDetails: CardDetails? = null
    ): PaymentResult {
        if (amount <= 0) {
            return PaymentResult(
                isSuccess = false,
                errorMessage = "Invalid amount to process."
            )
        }

        if (method == OnlinePaymentMethod.CREDIT_CARD) {
            val num = cardDetails?.cardNumber?.replace(" ", "") ?: ""
            if (num.length < 12) {
                return PaymentResult(
                    isSuccess = false,
                    errorMessage = "Please enter a valid credit card number."
                )
            }
        }

        val txnId = "TXN-${method.name.take(3)}-${UUID.randomUUID().toString().take(8).uppercase()}"
        return PaymentResult(
            isSuccess = true,
            transactionId = txnId,
            amountPaid = amount,
            paymentMethod = method,
            timestamp = System.currentTimeMillis()
        )
    }
}

@Composable
fun OnlinePaymentCheckoutDialog(
    itemTitle: String,
    amountToPay: Double,
    customerEmail: String,
    onPaymentSuccess: (PaymentResult) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedMethod by remember { mutableStateOf(OnlinePaymentMethod.GOOGLE_PAY) }
    var cardNumber by remember { mutableStateOf("4532 8920 1192 8831") }
    var cardHolder by remember { mutableStateOf("John Rider") }
    var expiryDate by remember { mutableStateOf("12/28") }
    var cvv by remember { mutableStateOf("382") }

    var isProcessing by remember { mutableStateOf(false) }
    var paymentResultState by remember { mutableStateOf<PaymentResult?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Payment, contentDescription = null, tint = EmeraldGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Online Payment Gateway", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Checkout Amount Banner
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF16211B)),
                    modifier = Modifier.fillMaxWidth().border(1.dp, EmeraldGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = itemTitle, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "Target Email: $customerEmail", color = TextMuted, fontSize = 11.sp)
                        }
                        Text(
                            text = "$${String.format("%.2f", amountToPay)}",
                            color = EmeraldGreen,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        )
                    }
                }

                if (paymentResultState == null && !isProcessing) {
                    Text("SELECT PAYMENT METHOD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TechCyan)

                    // Method Selection Cards
                    OnlinePaymentMethod.values().forEach { method ->
                        val isSelected = method == selectedMethod
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF1E2833) else Color(0xFF12161A)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedMethod = method }
                                .border(
                                    1.dp,
                                    if (isSelected) TechCyan else CardBorderDark,
                                    RoundedCornerShape(10.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedMethod = method },
                                    colors = RadioButtonDefaults.colors(selectedColor = TechCyan)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(text = method.displayName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = method.providerName, color = TextMuted, fontSize = 10.sp)
                                }
                            }
                        }
                    }

                    // Credit Card Inputs if selected
                    if (selectedMethod == OnlinePaymentMethod.CREDIT_CARD) {
                        Spacer(modifier = Modifier.height(4.dp))
                        StyledTextField(value = cardNumber, onValueChange = { cardNumber = it }, label = "Card Number", tag = "pay_card_num")
                        StyledTextField(value = cardHolder, onValueChange = { cardHolder = it }, label = "Cardholder Name", tag = "pay_card_holder")
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            StyledTextField(value = expiryDate, onValueChange = { expiryDate = it }, label = "MM/YY", modifier = Modifier.weight(1f), tag = "pay_expiry")
                            StyledTextField(value = cvv, onValueChange = { cvv = it }, label = "CVV", keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f), tag = "pay_cvv")
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("256-Bit SSL Encrypted & PCI-DSS Compliant Payment", color = TextMuted, fontSize = 10.sp)
                    }
                } else if (isProcessing) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = TechCyan)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Authorizing Payment via ${selectedMethod.displayName}...", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                } else paymentResultState?.let { res ->
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Payment Approved!", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Txn ID: ${res.transactionId}", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Button to send PDF bill to email immediately
                        Button(
                            onClick = {
                                val invoice = ServiceInvoice(
                                    invoiceNumber = res.transactionId,
                                    companyDetails = CompanyDetails(),
                                    bikeName = itemTitle,
                                    mileageKm = 0,
                                    customerName = cardHolder.ifBlank { "Rider Customer" },
                                    customerEmail = customerEmail,
                                    items = listOf(InvoiceLineItem(description = itemTitle, quantity = 1.0, unitPrice = amountToPay))
                                )
                                InvoiceGenerator.sendInvoicePdfByEmail(context, invoice, customerEmail)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("send_email_after_payment_btn")
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SEND PDF BILL TO EMAIL NOW", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (paymentResultState == null && !isProcessing) {
                Button(
                    onClick = {
                        isProcessing = true
                        val cardDetails = if (selectedMethod == OnlinePaymentMethod.CREDIT_CARD) {
                            CardDetails(cardNumber, cardHolder, expiryDate, cvv)
                        } else null

                        // Simulate payment API roundtrip delay
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            isProcessing = false
                            val result = BillingManager.processOnlinePayment(amountToPay, selectedMethod, cardDetails)
                            paymentResultState = result
                            if (result.isSuccess) {
                                onPaymentSuccess(result)
                            }
                        }, 1200)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedMethod == OnlinePaymentMethod.GOOGLE_PAY) Color.Black else EmeraldGreen
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("process_online_payment_btn")
                ) {
                    Icon(
                        if (selectedMethod == OnlinePaymentMethod.GOOGLE_PAY) Icons.Default.AccountBalanceWallet else Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedMethod == OnlinePaymentMethod.GOOGLE_PAY) "PAY WITH GOOGLE PAY" else "PROCESS $${String.format("%.2f", amountToPay)} PAYMENT",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            } else if (paymentResultState != null) {
                TextButton(onClick = onDismiss) {
                    Text("Done", color = TextPrimary)
                }
            }
        },
        dismissButton = {
            if (paymentResultState == null) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        }
    )
}
