package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "boost_orders")
data class BoostOrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageId: String,
    val packageName: String,
    val platformName: String,
    val amountKes: Int,
    val targetLink: String,
    val phone: String,
    val status: String, // "PROMPT_SENT" or "FAILED"
    val checkoutRequestId: String? = null,
    val merchantRequestId: String? = null,
    val responseMessage: String,
    val createdAtMillis: Long = System.currentTimeMillis()
)
