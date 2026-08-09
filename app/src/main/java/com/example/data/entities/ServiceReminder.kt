package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "service_reminders")
data class ServiceReminder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val motorcycleId: Long,
    val title: String,
    val intervalKm: Int,
    val lastServiceKm: Int,
    val isCompleted: Boolean = false,
    val notes: String = ""
)
