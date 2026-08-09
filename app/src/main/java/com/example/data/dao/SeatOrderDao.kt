package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entities.OrderStatus
import com.example.data.entities.PaymentStatus
import com.example.data.entities.SeatOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface SeatOrderDao {
    @Query("SELECT * FROM seat_orders ORDER BY orderDate DESC")
    fun getAllSeatOrders(): Flow<List<SeatOrder>>

    @Query("SELECT * FROM seat_orders WHERE id = :id")
    fun getSeatOrderById(id: Long): Flow<SeatOrder?>

    @Query("SELECT * FROM seat_orders WHERE orderStatus = :status ORDER BY orderDate DESC")
    fun getOrdersByStatus(status: OrderStatus): Flow<List<SeatOrder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeatOrder(order: SeatOrder): Long

    @Update
    suspend fun updateSeatOrder(order: SeatOrder)

    @Delete
    suspend fun deleteSeatOrder(order: SeatOrder)

    @Query("UPDATE seat_orders SET orderStatus = :status WHERE id = :id")
    suspend fun updateOrderStatus(id: Long, status: OrderStatus)

    @Query("UPDATE seat_orders SET paymentStatus = :status WHERE id = :id")
    suspend fun updatePaymentStatus(id: Long, status: PaymentStatus)
}
