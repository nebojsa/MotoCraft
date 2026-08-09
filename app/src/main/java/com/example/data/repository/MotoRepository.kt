package com.example.data.repository

import com.example.data.dao.MotoDao
import com.example.data.entities.BuildProject
import com.example.data.entities.MaintenanceRecord
import com.example.data.entities.MarketplaceItem
import com.example.data.entities.Modification
import com.example.data.entities.Motorcycle
import com.example.data.entities.SeatMaterial
import com.example.data.entities.ServiceReminder
import kotlinx.coroutines.flow.Flow

class MotoRepository(private val dao: MotoDao) {
    val motorcycles: Flow<List<Motorcycle>> = dao.getAllMotorcycles()
    val allModifications: Flow<List<Modification>> = dao.getAllModifications()
    val marketplaceItems: Flow<List<MarketplaceItem>> = dao.getAllMarketplaceItems()
    val maintenanceRecords: Flow<List<MaintenanceRecord>> = dao.getAllMaintenanceRecords()
    val serviceReminders: Flow<List<ServiceReminder>> = dao.getAllReminders()
    val seatMaterials: Flow<List<SeatMaterial>> = dao.getAllSeatMaterials()
    val buildProjects: Flow<List<BuildProject>> = dao.getAllProjects()

    fun getModificationsForBike(bikeId: Long): Flow<List<Modification>> =
        dao.getModificationsForBike(bikeId)

    fun getMaintenanceForBike(bikeId: Long): Flow<List<MaintenanceRecord>> =
        dao.getMaintenanceForBike(bikeId)

    fun getRemindersForBike(bikeId: Long): Flow<List<ServiceReminder>> =
        dao.getRemindersForBike(bikeId)

    fun getProjectsForBike(bikeId: Long): Flow<List<BuildProject>> =
        dao.getProjectsForBike(bikeId)

    suspend fun insertMotorcycle(motorcycle: Motorcycle): Long = dao.insertMotorcycle(motorcycle)
    suspend fun updateMotorcycle(motorcycle: Motorcycle) = dao.updateMotorcycle(motorcycle)
    suspend fun deleteMotorcycle(motorcycle: Motorcycle) = dao.deleteMotorcycle(motorcycle)

    suspend fun insertModification(modification: Modification): Long = dao.insertModification(modification)
    suspend fun updateModification(modification: Modification) = dao.updateModification(modification)
    suspend fun deleteModification(modification: Modification) = dao.deleteModification(modification)

    suspend fun insertMarketplaceItem(item: MarketplaceItem): Long = dao.insertMarketplaceItem(item)
    suspend fun updateMarketplaceItem(item: MarketplaceItem) = dao.updateMarketplaceItem(item)
    suspend fun deleteMarketplaceItem(item: MarketplaceItem) = dao.deleteMarketplaceItem(item)

    suspend fun insertMaintenanceRecord(record: MaintenanceRecord): Long = dao.insertMaintenanceRecord(record)
    suspend fun deleteMaintenanceRecord(record: MaintenanceRecord) = dao.deleteMaintenanceRecord(record)

    suspend fun insertServiceReminder(reminder: ServiceReminder): Long = dao.insertServiceReminder(reminder)
    suspend fun updateServiceReminder(reminder: ServiceReminder) = dao.updateServiceReminder(reminder)
    suspend fun deleteServiceReminder(reminder: ServiceReminder) = dao.deleteServiceReminder(reminder)

    suspend fun insertSeatMaterial(material: SeatMaterial): Long = dao.insertSeatMaterial(material)
    suspend fun updateSeatMaterial(material: SeatMaterial) = dao.updateSeatMaterial(material)
    suspend fun deleteSeatMaterial(material: SeatMaterial) = dao.deleteSeatMaterial(material)

    suspend fun insertBuildProject(project: BuildProject): Long = dao.insertBuildProject(project)
    suspend fun updateBuildProject(project: BuildProject) = dao.updateBuildProject(project)
    suspend fun deleteBuildProject(project: BuildProject) = dao.deleteBuildProject(project)
}
