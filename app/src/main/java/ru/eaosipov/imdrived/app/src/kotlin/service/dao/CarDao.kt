package ru.eaosipov.imdrived.app.src.kotlin.service.dao

import androidx.room.*
import ru.eaosipov.imdrived.app.src.kotlin.service.entities.Car

@Dao
interface CarDao {

    @Query("SELECT * FROM cars")
    suspend fun getAllCars(): List<Car>

    @Query("""
        SELECT * FROM cars 
        WHERE 
            brand LIKE '%' || :query || '%' 
            OR model LIKE '%' || :query || '%' 
            OR fuelType LIKE '%' || :query || '%' 
            OR transmissionType LIKE '%' || :query || '%'
    """)
    suspend fun searchCars(query: String): List<Car>

    @Query("SELECT * FROM cars WHERE id = :id LIMIT 1")
    suspend fun getCarById(id: Long): Car?

    // Получаем все автомобили, которые добавлены в избранное
    @Query("SELECT * FROM cars WHERE isFavorite = 1")
    suspend fun getFavoriteCars(): List<Car>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: Car)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cars: List<Car>)

    @Update
    suspend fun updateCar(car: Car)

    // Обновление поля "избранное" для автомобиля
    @Query("UPDATE cars SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)

    @Delete
    suspend fun deleteCar(car: Car)
}
