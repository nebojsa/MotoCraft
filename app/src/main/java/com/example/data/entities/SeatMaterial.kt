package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MaterialType {
    HIGH_DENSITY_FOAM,
    MEMORY_FOAM,
    GEL_PAD,
    MARINE_VINYL,
    GENUINE_LEATHER,
    ALCANTARA,
    WATERPROOF_LINER,
    STITCHING_THREAD,
    ADHESIVE_SPRAY
}

@Entity(tableName = "seat_materials")
data class SeatMaterial(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: MaterialType,
    val quantityOnHand: Double,
    val unit: String,
    val unitCost: Double,
    val colorOrGrade: String,
    val reorderLevel: Double,
    val dimensions: String = "",
    val assignedProject: String = "",
    val notes: String = ""
)
