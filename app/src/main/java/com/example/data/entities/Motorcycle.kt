package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "motorcycles")
data class Motorcycle(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val model: String,
    val year: Int,
    val odometerKm: Int,
    val totalBudget: Double,
    val engineSpec: String = "998cc Inline-4",
    val isPrimary: Boolean = false,
    val notes: String = ""
)
