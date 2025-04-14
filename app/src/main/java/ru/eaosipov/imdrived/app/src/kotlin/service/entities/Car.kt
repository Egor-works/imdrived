package ru.eaosipov.imdrived.app.src.kotlin.service.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cars")
data class Car(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imageUri: String,       // URI или путь к изображению автомобиля
    val model: String,          // Модель автомобиля
    val brand: String,          // Марка автомобиля
    val pricePerHour: Double,   // Цена за час аренды
    val fuelType: String,       // Тип топлива: "Дизель" или "Бензин"
    val transmissionType: String, // Тип передач: "А/Т" (автомат) или "М/К" (механика)
    val isFavorite: Boolean = false // Новое поле, отвечающее за избранность
)