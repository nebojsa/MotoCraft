package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PartCondition { NEW, LIKE_NEW, USED, REFURBISHED }

@Entity(tableName = "marketplace_items")
data class MarketplaceItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String,
    val price: Double,
    val condition: PartCondition,
    val fitment: String,
    val description: String,
    val sellerName: String,
    val sellerContact: String,
    val isUserListing: Boolean = false,
    val isSaved: Boolean = false,
    val datePosted: Long = System.currentTimeMillis()
)
