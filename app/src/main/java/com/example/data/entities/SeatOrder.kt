package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class OrderStatus {
    PENDING,
    IN_PROGRESS,
    READY_FOR_FITTING,
    COMPLETED,
    DELIVERED
}

enum class PaymentStatus {
    UNPAID,
    DEPOSIT_PAID,
    PAID_IN_FULL
}

@Entity(tableName = "seat_orders")
data class SeatOrder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerName: String,
    val customerPhone: String = "",
    val motorcycleModel: String = "Yamaha MT-09",
    val bikerHeightCm: Double = 175.0,
    val bikerWeightKg: Double = 85.0,
    val bikerInseamCm: Double = 80.0,
    val ridingPosture: String = "Touring / Adventure",
    val coverMaterialName: String = "Italian Cognac Full-Grain Leather",
    val coverTexture: String = "Vintage Distressed Grain",
    val colorOption: String = "Espresso Cognac",
    val foamThicknessMm: Double = 40.0,
    val hasGelPad: Boolean = true,
    val gelPadAreaSqCm: Double = 300.0,
    val baseMaterialCost: Double = 110.0,
    val laborCost: Double = 140.0,
    val depositAmount: Double = 100.0,
    val orderStatus: OrderStatus = OrderStatus.IN_PROGRESS,
    val paymentStatus: PaymentStatus = PaymentStatus.DEPOSIT_PAID,
    val orderDate: Long = System.currentTimeMillis(),
    val estimatedCompletionDate: Long = System.currentTimeMillis() + (86400000L * 7),
    val notes: String = ""
) {
    val subtotal: Double get() = baseMaterialCost + laborCost
    val balanceDue: Double get() = when (paymentStatus) {
        PaymentStatus.PAID_IN_FULL -> 0.0
        PaymentStatus.DEPOSIT_PAID -> (subtotal - depositAmount).coerceAtLeast(0.0)
        PaymentStatus.UNPAID -> subtotal
    }
}
