package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.entities.MaterialType
import com.example.data.entities.OrderStatus
import com.example.data.entities.PaymentStatus
import com.example.data.entities.SeatMaterial
import com.example.data.entities.SeatOrder
import com.example.data.repository.MotoRepository
import com.example.util.BillingManager
import com.example.util.CompanyDetails
import com.example.util.InvoiceGenerator
import com.example.util.ServiceInvoice
import com.example.util.WorkshopFinancialSummary
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CraftViewModel(private val repository: MotoRepository) : ViewModel() {

    val seatOrders: StateFlow<List<SeatOrder>> = repository.seatOrders
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val seatMaterials: StateFlow<List<SeatMaterial>> = repository.seatMaterials
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val financialSummary: StateFlow<WorkshopFinancialSummary> = seatOrders
        .map { orders -> BillingManager.calculateWorkshopSummary(orders) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WorkshopFinancialSummary(0.0, 0.0, 0, 0, 0)
        )

    fun calculateSeatRestructure(
        bikerHeightCm: Double,
        bikerWeightKg: Double,
        bikerInseamCm: Double,
        ridingPosture: String,
        seatLengthCm: Double,
        seatWidthCm: Double
    ): SeatCalculatorResult {
        // Ergonomics calculation logic
        val recommendedFoamThicknessMm = when {
            bikerWeightKg > 100 -> 55.0
            bikerWeightKg > 80 -> 45.0
            else -> 35.0
        }

        val recommendedDensity = when {
            bikerWeightKg > 90 -> "80kg/m³ Super High Density"
            else -> "60kg/m³ Medium Density"
        }

        val heightOffsetStr = when {
            bikerInseamCm < 75 -> "-15mm Low Profile Cut"
            bikerInseamCm > 88 -> "+20mm High Stance Cut"
            else -> "Standard OEM Height Level"
        }

        val textureAndColor = when (ridingPosture) {
            "Sport / Track" -> "Perforated Anti-Slip / Racing Red & Jet Black"
            "Upright Cruiser / Chopper" -> "Diamond Tuck & Roll / Vintage Cognac Brown"
            "Cafe Racer / Custom" -> "Smooth Minimalist Grain / Midnight Black"
            else -> "Hexagon Stitched Heavy Vinyl / Desert Tan & Espresso"
        }

        val pressureIndexStr = when {
            bikerWeightKg > 95 -> "High Pressure Area - Gel Insert Required"
            else -> "Balanced Distribution"
        }

        val recommendedMaterialsList = mutableListOf<String>()
        recommendedMaterialsList.add("Base Foam: $recommendedDensity ($recommendedFoamThicknessMm mm)")
        if (bikerWeightKg > 85) {
            recommendedMaterialsList.add("Anatomical Gel Pad: 15mm Medical Grade Center Channel")
        }
        recommendedMaterialsList.add("Covering: $textureAndColor")

        return SeatCalculatorResult(
            bikerHeightCm = bikerHeightCm,
            bikerWeightKg = bikerWeightKg,
            bikerInseamCm = bikerInseamCm,
            ridingPosture = ridingPosture,
            baseFoamThicknessMm = recommendedFoamThicknessMm,
            gelPadAreaSqCm = if (bikerWeightKg > 85) 350.0 else 0.0,
            coverMaterialSqFt = 3.5,
            estimatedComfortRating = if (bikerWeightKg > 85) "9.8/10 Long Distance Ergonomic" else "9.2/10 Sport Touring Comfort",
            recommendedSeatHeightOffset = heightOffsetStr,
            seatWidthSpec = "${seatWidthCm.toInt()}cm Wide Contour",
            pressureReliefIndex = pressureIndexStr,
            recommendedCoverTextureAndColor = textureAndColor,
            recommendedMaterials = recommendedMaterialsList
        )
    }

    // Seat Order Management
    fun createSeatOrder(
        customerName: String,
        customerPhone: String,
        motorcycleModel: String,
        bikerHeightCm: Double,
        bikerWeightKg: Double,
        bikerInseamCm: Double,
        ridingPosture: String,
        coverMaterialName: String,
        coverTexture: String,
        colorOption: String,
        foamThicknessMm: Double,
        hasGelPad: Boolean,
        baseMaterialCost: Double,
        laborCost: Double,
        depositAmount: Double,
        notes: String
    ) {
        viewModelScope.launch {
            val order = SeatOrder(
                customerName = customerName,
                customerPhone = customerPhone,
                motorcycleModel = motorcycleModel,
                bikerHeightCm = bikerHeightCm,
                bikerWeightKg = bikerWeightKg,
                bikerInseamCm = bikerInseamCm,
                ridingPosture = ridingPosture,
                coverMaterialName = coverMaterialName,
                coverTexture = coverTexture,
                colorOption = colorOption,
                foamThicknessMm = foamThicknessMm,
                hasGelPad = hasGelPad,
                baseMaterialCost = baseMaterialCost,
                laborCost = laborCost,
                depositAmount = depositAmount,
                orderStatus = OrderStatus.IN_PROGRESS,
                paymentStatus = if (depositAmount >= (baseMaterialCost + laborCost)) PaymentStatus.PAID_IN_FULL else if (depositAmount > 0) PaymentStatus.DEPOSIT_PAID else PaymentStatus.UNPAID,
                notes = notes
            )
            repository.insertSeatOrder(order)
        }
    }

    fun updateOrderStatus(order: SeatOrder, newStatus: OrderStatus) {
        viewModelScope.launch {
            repository.updateSeatOrder(order.copy(orderStatus = newStatus))
        }
    }

    fun updatePaymentStatus(order: SeatOrder, newPaymentStatus: PaymentStatus) {
        viewModelScope.launch {
            repository.updateSeatOrder(order.copy(paymentStatus = newPaymentStatus))
        }
    }

    fun deleteSeatOrder(order: SeatOrder) {
        viewModelScope.launch {
            repository.deleteSeatOrder(order)
        }
    }

    // Seat Material Inventory Management
    fun addSeatMaterial(
        name: String,
        type: MaterialType,
        texture: String,
        colorOption: String,
        quantityOnHand: Double,
        unit: String,
        unitCost: Double,
        colorOrGrade: String,
        reorderLevel: Double,
        dimensions: String,
        assignedProject: String
    ) {
        viewModelScope.launch {
            val material = SeatMaterial(
                name = name,
                type = type,
                texture = texture,
                colorOption = colorOption,
                quantityOnHand = quantityOnHand,
                unit = unit,
                unitCost = unitCost,
                colorOrGrade = colorOrGrade,
                reorderLevel = reorderLevel,
                dimensions = dimensions,
                assignedProject = assignedProject
            )
            repository.insertSeatMaterial(material)
        }
    }

    fun adjustMaterialQuantity(material: SeatMaterial, delta: Double) {
        viewModelScope.launch {
            val updated = material.copy(quantityOnHand = (material.quantityOnHand + delta).coerceAtLeast(0.0))
            repository.updateSeatMaterial(updated)
        }
    }

    fun deleteSeatMaterial(material: SeatMaterial) {
        viewModelScope.launch {
            repository.deleteSeatMaterial(material)
        }
    }

    fun generateOrderInvoice(order: SeatOrder, company: CompanyDetails): ServiceInvoice {
        return InvoiceGenerator.createFromSeatOrder(order, company)
    }
}
