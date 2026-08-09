package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "build_projects")
data class BuildProject(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val motorcycleId: Long,
    val name: String,
    val targetBudget: Double,
    val targetCompletionDate: String,
    val status: String = "Active",
    val notes: String = ""
)
