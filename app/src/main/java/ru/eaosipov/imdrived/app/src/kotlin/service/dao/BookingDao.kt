package ru.eaosipov.imdrived.app.src.kotlin.service.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import ru.eaosipov.imdrived.app.src.kotlin.service.entities.Booking
import ru.eaosipov.imdrived.app.src.kotlin.service.entities.BookingWithCar

@Dao
interface BookingDao {

    // Вставка нового бронирования
    @Insert
    suspend fun insertBooking(booking: Booking)

    // Получение всех бронирований
    @Query("SELECT * FROM bookings")
    suspend fun getAllBookings(): List<Booking>

    // Получение бронирований по ID автомобиля
    @Query("SELECT * FROM bookings WHERE id = :id LIMIT 1")
    suspend fun getBookingById(id: Long): BookingWithCar

    @Query("UPDATE bookings SET is_ended = :isEnded WHERE id = :bookingId")
    suspend fun updateBookingStatus(bookingId: Long, isEnded: Boolean)

    // Получение бронирований по ID автомобиля
    @Query("SELECT * FROM bookings WHERE car_id = :carId")
    suspend fun getBookingsByCarId(carId: Long): List<Booking>

    // Получаем бронирования для заданного пользователя с данными об автомобиле (JOIN через Room)
    @Transaction
    @Query("SELECT * FROM bookings WHERE user_id = :userId")
    suspend fun getBookingsForUser(userId: Int): List<BookingWithCar>
}
