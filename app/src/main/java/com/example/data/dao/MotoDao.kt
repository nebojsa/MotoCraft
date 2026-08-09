package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entities.BuildProject
import com.example.data.entities.MaintenanceRecord
import com.example.data.entities.MarketplaceItem
import com.example.data.entities.Modification
import com.example.data.entities.Motorcycle
import com.example.data.entities.SeatMaterial
import com.example.data.entities.ServiceReminder
import kotlinx.coroutines.flow.Flow

@Dao
interface MotoDao {
    // Motorcycles
    @Query("SELECT * FROM motorcycles ORDER BY isPrimary DESC, id ASC")
    fun getAllMotorcycles(): Flow<List<Motorcycle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMotorcycle(motorcycle: Motorcycle): Long

    @Update
    suspend fun updateMotorcycle(motorcycle: Motorcycle)

    @Delete
    suspend fun deleteMotorcycle(motorcycle: Motorcycle)

    // Modifications
    @Query("SELECT * FROM modifications WHERE motorcycleId = :bikeId ORDER BY id DESC")
    fun getModificationsForBike(bikeId: Long): Flow<List<Modification>>

    @Query("SELECT * FROM modifications ORDER BY id DESC")
    fun getAllModifications(): Flow<List<Modification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModification(modification: Modification): Long

    @Update
    suspend fun updateModification(modification: Modification)

    @Delete
    suspend fun deleteModification(modification: Modification)

    // Marketplace Items
    @Query("SELECT * FROM marketplace_items ORDER BY id DESC")
    fun getAllMarketplaceItems(): Flow<List<MarketplaceItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarketplaceItem(item: MarketplaceItem): Long

    @Update
    suspend fun updateMarketplaceItem(item: MarketplaceItem)

    @Delete
    suspend fun deleteMarketplaceItem(item: MarketplaceItem)

    // Maintenance Records
    @Query("SELECT * FROM maintenance_records WHERE motorcycleId = :bikeId ORDER BY date DESC")
    fun getMaintenanceForBike(bikeId: Long): Flow<List<MaintenanceRecord>>

    @Query("SELECT * FROM maintenance_records ORDER BY date DESC")
    fun getAllMaintenanceRecords(): Flow<List<MaintenanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenanceRecord(record: MaintenanceRecord): Long

    @Delete
    suspend fun deleteMaintenanceRecord(record: MaintenanceRecord)

    // Service Reminders
    @Query("SELECT * FROM service_reminders WHERE motorcycleId = :bikeId ORDER BY id ASC")
    fun getRemindersForBike(bikeId: Long): Flow<List<ServiceReminder>>

    @Query("SELECT * FROM service_reminders ORDER BY id ASC")
    fun getAllReminders(): Flow<List<ServiceReminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServiceReminder(reminder: ServiceReminder): Long

    @Update
    suspend fun updateServiceReminder(reminder: ServiceReminder)

    @Delete
    suspend fun deleteServiceReminder(reminder: ServiceReminder)

    // Seat Materials
    @Query("SELECT * FROM seat_materials ORDER BY type ASC, name ASC")
    fun getAllSeatMaterials(): Flow<List<SeatMaterial>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeatMaterial(material: SeatMaterial): Long

    @Update
    suspend fun updateSeatMaterial(material: SeatMaterial)

    @Delete
    suspend fun deleteSeatMaterial(material: SeatMaterial)

    // Build Projects
    @Query("SELECT * FROM build_projects WHERE motorcycleId = :bikeId ORDER BY id ASC")
    fun getProjectsForBike(bikeId: Long): Flow<List<BuildProject>>

    @Query("SELECT * FROM build_projects ORDER BY id ASC")
    fun getAllProjects(): Flow<List<BuildProject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuildProject(project: BuildProject): Long

    @Update
    suspend fun updateBuildProject(project: BuildProject)

    @Delete
    suspend fun deleteBuildProject(project: BuildProject)
}
