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
    val baseFoamThicknessMm: Double,
    val gelPadAreaSqCm: Double,
    val coverMaterialSqFt: Double,
    val estimatedComfortRating: String, // "Firm Sport", "Medium Dual-Sport", "Plush Touring"
    val recommendedMaterials: List<String>
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
        val maintSpent = logs.sumOf { it.cost }
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
        date: Long = System.currentTimeMillis()
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
                    cost = cost
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
        quantity: Double,
        unit: String,
        unitCost: Double,
        color: String,
        reorderLevel: Double,
        dimensions: String,
        project: String
    ) {
        viewModelScope.launch {
            repository.insertSeatMaterial(
                SeatMaterial(
                    name = name,
                    type = type,
                    quantityOnHand = quantity,
                    unit = unit,
                    unitCost = unitCost,
                    colorOrGrade = color,
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

    // Calculator Utility for Seat Restructuring
    fun calculateSeatRestructure(
        riderWeightKg: Double,
        seatLengthCm: Double,
        seatWidthCm: Double,
        rideType: String // "Sport / Track", "Touring / Adventure", "Cafe Racer / Custom"
    ): SeatCalculatorResult {
        val baseFoam = when {
            riderWeightKg > 90 -> 50.0
            riderWeightKg > 75 -> 40.0
            else -> 30.0
        }
        val gelArea = (seatLengthCm * 0.6) * (seatWidthCm * 0.8)
        val coverSqFt = ((seatLengthCm + 15) * (seatWidthCm + 15)) / 929.03

        val rating = when (rideType) {
            "Sport / Track" -> "Firm High Density (75-80 Density Base)"
            "Touring / Adventure" -> "Dual-Layer Memory Foam + 25mm Gel Pad"
            else -> "Classic Tuck & Roll with 30mm Medium Foam"
        }

        val recs = mutableListOf<String>()
        recs.add("${baseFoam.toInt()}mm High-Density Base Foam Layer")
        recs.add("Anatomical Tailbone Pressure Relief Channel")
        if (rideType.contains("Touring")) {
            recs.add("Medical Grade 25mm Gel Insert (${gelArea.toInt()} cm² area)")
            recs.add("20mm Visco Memory Foam Cushion Overlay")
        }
        recs.add("Heavy Duty Waterproof Liner Shield")
        recs.add("UV & Abrasion Resistant Marine Grade Vinyl / Leather Cover (~${String.format("%.2f", coverSqFt)} sq ft)")

        return SeatCalculatorResult(
            baseFoamThicknessMm = baseFoam,
            gelPadAreaSqCm = gelArea,
            coverMaterialSqFt = coverSqFt,
            estimatedComfortRating = rating,
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
