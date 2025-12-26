package com.example.testapplication.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val price: Double,
    val unitsPerPackage: Int = 1,
    val quantityPerUnit: Double,
    val unit: String, // kg, g, l, ml, units, etc.
) : Parcelable {
    val totalQuantity: Double get() = unitsPerPackage * quantityPerUnit
    
    val pricePerBaseUnit: Double get() {
        val u = unit.lowercase()
        val factor = when {
            u.startsWith("g") && u.length == 1 -> 1000.0
            u.startsWith("ml") -> 1000.0
            else -> 1.0
        }
        return (price / totalQuantity) * factor
    }

    val baseUnit: String get() {
        val u = unit.lowercase()
        return when {
            u.contains("g") -> "kg"
            u.contains("l") || u.contains("ml") -> "l"
            else -> unit
        }
    }
}
