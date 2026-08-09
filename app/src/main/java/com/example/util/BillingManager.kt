package com.example.util

import com.example.data.entities.OrderStatus
import com.example.data.entities.PaymentStatus
import com.example.data.entities.SeatOrder

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
        // Base foam cost ($0.75 per mm of density)
        val foam = foamThicknessMm * 0.85
        // Medical gel pad cost
        val gel = if (hasGelPad) 45.0 else 0.0
        // Cover material (~3.5 sq ft needed per seat)
        val cover = coverMaterialUnitCost * 3.5
        // Labor tier
        val labor = when (ridingPosture) {
            "Sport / Track" -> 110.0
            "Upright Cruiser / Chopper" -> 150.0
            "Cafe Racer / Custom" -> 160.0
            else -> 130.0 // Touring / Adventure
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
}
