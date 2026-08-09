package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.entities.BuildProject
import com.example.data.entities.MaintenanceRecord
import com.example.data.entities.MarketplaceItem
import com.example.data.entities.MaterialType
import com.example.data.entities.ModCategory
import com.example.data.entities.ModStatus
import com.example.data.entities.Modification
import com.example.data.entities.Motorcycle
import com.example.data.entities.PartCondition
import com.example.data.entities.SeatMaterial
import com.example.data.entities.ServiceReminder
import com.example.data.repository.MotoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BuildBudgetStats(
    val totalBudget: Double = 0.0,
    val totalModSpent: Double = 0.0,
    val totalMaintenanceSpent: Double = 0.0,
    val totalMaterialsSpent: Double = 0.0,
    val remainingBudget: Double = 0.0,
    val totalHpGain: Double = 0.0,
    val totalTorqueGainNm: Double = 0.0,
    val totalWeightSavedKg: Double = 0.0,
    val installedModCount: Int = 0,
    val pendingModCount: Int = 0,
    val categoryBreakdown: Map<String, Double> = emptyMap()
)

data class SeatCalculatorResult(
    val bikerHeightCm: Double = 175.0,
    val bikerWeightKg: Double = 85.0,
    val bikerInseamCm: Double = 80.0,
    val ridingPosture: String = "Touring / Adventure",
    val baseFoamThicknessMm: Double = 40.0,
    val gelPadAreaSqCm: Double = 300.0,
    val coverMaterialSqFt: Double = 3.5,
    val estimatedComfortRating: String = "Plush Long-Distance Ergonomics",
    val recommendedSeatHeightOffset: String = "Standard height with tapered thigh relief",
    val seatWidthSpec: String = "18cm Tapered Nose / 32cm Bucket",
    val pressureReliefIndex: String = "Optimal (1.1 N/cm²)",
    val recommendedCoverTextureAndColor: String = "Diamond Stitched Marine Vinyl in Espresso Brown",
    val recommendedMaterials: List<String> = emptyList()
)

class MotoViewModel(private val repository: MotoRepository) : ViewModel() {

    val selectedBikeId = MutableStateFlow<Long>(0L) // 0 = All Bikes

    val motorcycles: StateFlow<List<Motorcycle>> = repository.motorcycles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedMotorcycle: StateFlow<Motorcycle?> = combine(motorcycles, selectedBikeId) { bikes, id ->
        if (id == 0L) bikes.firstOrNull { it.isPrimary } ?: bikes.firstOrNull()
        else bikes.firstOrNull { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val modifications: StateFlow<List<Modification>> = combine(repository.allModifications, selectedBikeId) { mods, bikeId ->
        if (bikeId == 0L) mods else mods.filter { it.motorcycleId == bikeId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val marketplaceItems: StateFlow<List<MarketplaceItem>> = repository.marketplaceItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val maintenanceRecords: StateFlow<List<MaintenanceRecord>> = combine(repository.maintenanceRecords, selectedBikeId) { logs, bikeId ->
        if (bikeId == 0L) logs else logs.filter { it.motorcycleId == bikeId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val serviceReminders: StateFlow<List<ServiceReminder>> = combine(repository.serviceReminders, selectedBikeId) { reminders, bikeId ->
        if (bikeId == 0L) reminders else reminders.filter { it.motorcycleId == bikeId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val seatMaterials: StateFlow<List<SeatMaterial>> = repository.seatMaterials
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val buildProjects: StateFlow<List<BuildProject>> = combine(repository.buildProjects, selectedBikeId) { projects, bikeId ->
        if (bikeId == 0L) projects else projects.filter { it.motorcycleId == bikeId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Overall Build & Budget Analytics
    val budgetStats: StateFlow<BuildBudgetStats> = combine(
        selectedMotorcycle,
        modifications,
        maintenanceRecords,
        seatMaterials
    ) { bike, mods, logs, materials ->
        val bikeBudget = bike?.totalBudget ?: 15000.0
        val modSpent = mods.sumOf { it.cost }
        val maintSpent = logs.sumOf { it.cost + it.linkedPartCost }
        val matSpent = materials.sumOf { it.quantityOnHand * it.unitCost }
        val totalSpent = modSpent + maintSpent

        val installedMods = mods.filter { it.status == ModStatus.INSTALLED }
        val hp = installedMods.sumOf { it.hpGain }
        val torque = installedMods.sumOf { it.torqueGainNm }
        val weight = installedMods.sumOf { it.weightReductionKg }

        val breakdown = mods.groupBy { it.category.name.replace("_", " ") }
            .mapValues { entry -> entry.value.sumOf { it.cost } }

        BuildBudgetStats(
            totalBudget = bikeBudget,
            totalModSpent = modSpent,
            totalMaintenanceSpent = maintSpent,
            totalMaterialsSpent = matSpent,
            remainingBudget = (bikeBudget - totalSpent).coerceAtLeast(0.0),
            totalHpGain = hp,
            totalTorqueGainNm = torque,
            totalWeightSavedKg = weight,
            installedModCount = installedMods.size,
            pendingModCount = mods.size - installedMods.size,
            categoryBreakdown = breakdown
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BuildBudgetStats())

    fun selectBike(bikeId: Long) {
        selectedBikeId.value = bikeId
    }

    // Motorcycle Actions
    fun addMotorcycle(name: String, model: String, year: Int, odometer: Int, budget: Double, engineSpec: String) {
        viewModelScope.launch {
            repository.insertMotorcycle(
                Motorcycle(
                    name = name,
                    model = model,
                    year = year,
                    odometerKm = odometer,
                    totalBudget = budget,
                    engineSpec = engineSpec,
                    isPrimary = false
                )
            )
        }
    }

    // Modification Actions
    fun addModification(
        title: String,
        category: ModCategory,
        brand: String,
        cost: Double,
        status: ModStatus,
        hpGain: Double,
        torqueGain: Double,
        weightSaved: Double,
        notes: String
    ) {
        val bikeId = selectedMotorcycle.value?.id ?: 1L
        viewModelScope.launch {
            repository.insertModification(
                Modification(
                    motorcycleId = bikeId,
                    title = title,
                    category = category,
                    brand = brand,
                    cost = cost,
                    status = status,
                    hpGain = hpGain,
                    torqueGainNm = torqueGain,
                    weightReductionKg = weightSaved,
                    notes = notes
                )
            )
        }
    }

    fun updateModStatus(mod: Modification, newStatus: ModStatus) {
        viewModelScope.launch {
            repository.updateModification(mod.copy(status = newStatus))
        }
    }

    fun deleteModification(mod: Modification) {
        viewModelScope.launch {
            repository.deleteModification(mod)
        }
    }

    // Marketplace Actions
    fun addMarketplaceListing(
        title: String,
        category: String,
        price: Double,
        condition: PartCondition,
        fitment: String,
        description: String,
        sellerContact: String
    ) {
        viewModelScope.launch {
            repository.insertMarketplaceItem(
                MarketplaceItem(
                    title = title,
                    category = category,
                    price = price,
                    condition = condition,
                    fitment = fitment,
                    description = description,
                    sellerName = "You (SaaS Seller)",
                    sellerContact = sellerContact,
                    isUserListing = true,
                    isSaved = false
                )
            )
        }
    }

    fun toggleSaveItem(item: MarketplaceItem) {
        viewModelScope.launch {
            repository.updateMarketplaceItem(item.copy(isSaved = !item.isSaved))
        }
    }

    fun deleteMarketplaceItem(item: MarketplaceItem) {
        viewModelScope.launch {
            repository.deleteMarketplaceItem(item)
        }
    }

    // Maintenance Actions
    fun addMaintenanceRecord(
        serviceType: String,
        mileage: Int,
        cost: Double,
        description: String,
        date: Long = System.currentTimeMillis(),
        linkedPartName: String = "",
        linkedPartCost: Double = 0.0
    ) {
        val bikeId = selectedMotorcycle.value?.id ?: 1L
        viewModelScope.launch {
            repository.insertMaintenanceRecord(
                MaintenanceRecord(
                    motorcycleId = bikeId,
                    serviceType = serviceType,
                    mileage = mileage,
                    description = description,
                    date = date,
                    cost = cost,
                    linkedPartName = linkedPartName,
                    linkedPartCost = linkedPartCost
                )
            )
            // Also update bike odometer if higher
            selectedMotorcycle.value?.let { currentBike ->
                if (mileage > currentBike.odometerKm) {
                    repository.updateMotorcycle(currentBike.copy(odometerKm = mileage))
                }
            }
        }
    }

    fun deleteMaintenanceRecord(record: MaintenanceRecord) {
        viewModelScope.launch {
            repository.deleteMaintenanceRecord(record)
        }
    }

    fun addServiceReminder(title: String, intervalKm: Int, notes: String) {
        val bike = selectedMotorcycle.value
        val bikeId = bike?.id ?: 1L
        val currentOdo = bike?.odometerKm ?: 0
        viewModelScope.launch {
            repository.insertServiceReminder(
                ServiceReminder(
                    motorcycleId = bikeId,
                    title = title,
                    intervalKm = intervalKm,
                    lastServiceKm = currentOdo,
                    isCompleted = false,
                    notes = notes
                )
            )
        }
    }

    fun completeReminder(reminder: ServiceReminder) {
        val bike = selectedMotorcycle.value
        val currentOdo = bike?.odometerKm ?: (reminder.lastServiceKm + reminder.intervalKm)
        viewModelScope.launch {
            repository.updateServiceReminder(
                reminder.copy(
                    lastServiceKm = currentOdo,
                    isCompleted = false
                )
            )
            // Auto log maintenance
            repository.insertMaintenanceRecord(
                MaintenanceRecord(
                    motorcycleId = reminder.motorcycleId,
                    serviceType = reminder.title + " (Scheduled)",
                    mileage = currentOdo,
                    description = "Completed automated reminder maintenance",
                    cost = 0.0
                )
            )
        }
    }

    // Seat Material Actions
    fun addSeatMaterial(
        name: String,
        type: MaterialType,
        texture: String = "Smooth Matte",
        colorOption: String = "Jet Black",
        quantity: Double,
        unit: String,
        unitCost: Double,
        color: String = "",
        reorderLevel: Double = 2.0,
        dimensions: String = "",
        project: String = ""
    ) {
        viewModelScope.launch {
            repository.insertSeatMaterial(
                SeatMaterial(
                    name = name,
                    type = type,
                    texture = texture,
                    colorOption = colorOption,
                    quantityOnHand = quantity,
                    unit = unit,
                    unitCost = unitCost,
                    colorOrGrade = if (color.isNotBlank()) color else colorOption,
                    reorderLevel = reorderLevel,
                    dimensions = dimensions,
                    assignedProject = project
                )
            )
        }
    }

    fun adjustMaterialQuantity(material: SeatMaterial, delta: Double) {
        val newQty = (material.quantityOnHand + delta).coerceAtLeast(0.0)
        viewModelScope.launch {
            repository.updateSeatMaterial(material.copy(quantityOnHand = newQty))
        }
    }

    fun deleteSeatMaterial(material: SeatMaterial) {
        viewModelScope.launch {
            repository.deleteSeatMaterial(material)
        }
    }

    // Build Project Actions
    fun addBuildProject(name: String, budget: Double, targetDate: String, notes: String) {
        val bikeId = selectedMotorcycle.value?.id ?: 1L
        viewModelScope.launch {
            repository.insertBuildProject(
                BuildProject(
                    motorcycleId = bikeId,
                    name = name,
                    targetBudget = budget,
                    targetCompletionDate = targetDate,
                    status = "Active",
                    notes = notes
                )
            )
        }
    }

    // Script & Calculator Utility for Biker Ergonomics & Seat Restructuring
    fun calculateSeatRestructure(
        bikerHeightCm: Double = 175.0,
        bikerWeightKg: Double = 85.0,
        bikerInseamCm: Double = 80.0,
        rideType: String = "Touring / Adventure", // "Sport / Track", "Touring / Adventure", "Upright Cruiser / Chopper", "Cafe Racer / Custom"
        seatLengthCm: Double = 55.0,
        seatWidthCm: Double = 28.0
    ): SeatCalculatorResult {
        val baseFoam = when {
            bikerWeightKg > 100 -> 55.0
            bikerWeightKg > 85 -> 45.0
            bikerWeightKg > 70 -> 35.0
            else -> 25.0
        }

        // Height & Inseam Reach Analysis Script
        val seatHeightOffset = when {
            bikerInseamCm < 75.0 -> "-18 mm (Dished front nose & narrow thigh taper for 100% foot grounding)"
            bikerInseamCm < 82.0 -> "Standard Seat Height (-5 mm front taper for effortless stop-and-go)"
            bikerInseamCm > 88.0 -> "+25 mm Tall Riser Foam (+15mm rear pocket setback for leg comfort)"
            else -> "Standard OEM Height (+5 mm cushion lift)"
        }

        // Pressure relief calculation
        val seatAreaSqCm = seatLengthCm * seatWidthCm
        val pressureNPerSqCm = (bikerWeightKg * 9.81) / (seatAreaSqCm * 0.45)
        val pressureReliefRating = if (pressureNPerSqCm < 1.0) "Ultra-Light Pressure (0.85 N/cm²)" else "Optimized Pressure Distribution (${String.format("%.2f", pressureNPerSqCm)} N/cm²)"

        val noseWidth = (seatWidthCm * 0.55).toInt()
        val bucketWidth = seatWidthCm.toInt()
        val widthSpec = "$noseWidth cm Tapered Nose / $bucketWidth cm Seating Bucket"

        val gelArea = (seatLengthCm * 0.55) * (seatWidthCm * 0.7)
        val coverSqFt = ((seatLengthCm + 16) * (seatWidthCm + 16)) / 929.03

        val (coverTextureColor, comfortRating) = when (rideType) {
            "Sport / Track" -> "Perforated High-Grip Vinyl in Jet Black" to "Firm High-Grip Feedback (80 Density Base)"
            "Upright Cruiser / Chopper" -> "Tuck & Roll Vintage Leather in Cognac Brown" to "Classic Plush Cruiser Cushion"
            "Cafe Racer / Custom" -> "Diamond Stitched Alcantara in Espresso Brown" to "Firm Custom Contour with Tail Roll"
            else -> "Diamond Tuck Marine Vinyl in Jet Black / Espresso" to "Plush Long-Distance Touring Ergonomics"
        }

        val recs = mutableListOf<String>()
        recs.add("${baseFoam.toInt()}mm 80-Density Rebound Polyurethane Base")
        recs.add("Anatomical Coccyx & Prostate Pressure Relief Groove")
        if (bikerWeightKg > 80 || rideType.contains("Touring")) {
            recs.add("Medical Grade 20mm Visco-Gel Insert (~${gelArea.toInt()} cm² area)")
            recs.add("15mm High-Density Memory Foam Topper")
        }
        recs.add("Waterproof TPU Barrier Liner Shield")
        recs.add("$coverTextureColor (~${String.format("%.2f", coverSqFt)} sq ft)")

        return SeatCalculatorResult(
            bikerHeightCm = bikerHeightCm,
            bikerWeightKg = bikerWeightKg,
            bikerInseamCm = bikerInseamCm,
            ridingPosture = rideType,
            baseFoamThicknessMm = baseFoam,
            gelPadAreaSqCm = gelArea,
            coverMaterialSqFt = coverSqFt,
            estimatedComfortRating = comfortRating,
            recommendedSeatHeightOffset = seatHeightOffset,
            seatWidthSpec = widthSpec,
            pressureReliefIndex = pressureReliefRating,
            recommendedCoverTextureAndColor = coverTextureColor,
            recommendedMaterials = recs
        )
    }
}

class MotoViewModelFactory(private val repository: MotoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MotoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MotoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
