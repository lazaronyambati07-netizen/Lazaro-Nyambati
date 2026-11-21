package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BoostOrderDao {
    @Query("SELECT * FROM boost_orders ORDER BY createdAtMillis DESC")
    fun observeAllOrders(): Flow<List<BoostOrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: BoostOrderEntity): Long

    @Query("DELETE FROM boost_orders WHERE id = :orderId")
    suspend fun deleteOrderById(orderId: Long)

    @Query("DELETE FROM boost_orders")
    suspend fun clearAllOrders()
}
