package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ModStatus { PLANNED, IN_PROGRESS, INSTALLED }

enum class ModCategory {
    ENGINE_ECU,
    EXHAUST,
    SUSPENSION,
    BRAKES,
    SEAT_ERGONOMICS,
    AESTHETIC_CARBON,
    ELECTRONICS
}

@Entity(tableName = "modifications")
data class Modification(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val motorcycleId: Long,
    val title: String,
    val category: ModCategory,
    val brand: String,
    val cost: Double,
    val status: ModStatus,
    val hpGain: Double = 0.0,
    val torqueGainNm: Double = 0.0,
    val weightReductionKg: Double = 0.0,
    val installationDate: Long = System.currentTimeMillis(),
    val notes: String = ""
)
