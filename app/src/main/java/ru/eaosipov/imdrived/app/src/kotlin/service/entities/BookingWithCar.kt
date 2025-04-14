package ru.eaosipov.imdrived.app.src.kotlin.service.entities

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Связывает данные о бронировании с соответствующим автомобилем,
 * позволяя извлекать информацию из обеих таблиц одновременно.
 */
data class BookingWithCar(
    /**
     * Включает подробные сведения о самом бронировании.
     */
    @Embedded
    val booking: Booking,

    /**
     * Содержит объект автомобиля, связанного с бронированием,
     * на основе полей "car_id" и "id".
     */
    @Relation(
        parentColumn = "car_id",
        entityColumn = "id"
    )
    val car: Car
)