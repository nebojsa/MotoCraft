package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maintenance_records")
data class MaintenanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val motorcycleId: Long = 1L,
    val serviceType: String,
    val mileage: Int,
    val description: String = "",
    val date: Long = System.currentTimeMillis(),
    val cost: Double = 0.0,
    val performedBy: String = "Owner / Self",
    val linkedPartName: String = "",
    val linkedPartCost: Double = 0.0
) {
    // Convenience properties for backwards compatibility
    val odometerKm: Int get() = mileage
    val notes: String get() = description
    val serviceDate: Long get() = date
}

typealias MaintenanceLog = MaintenanceRecord

