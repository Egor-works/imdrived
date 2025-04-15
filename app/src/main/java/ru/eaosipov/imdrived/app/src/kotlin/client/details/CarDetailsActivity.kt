package ru.eaosipov.imdrived.app.src.kotlin.client.details

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import ru.eaosipov.imdrived.app.src.kotlin.client.rental.RentCarActivity
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.eaosipov.imdrived.R
import ru.eaosipov.imdrived.app.src.kotlin.service.db.AppDatabase
import ru.eaosipov.imdrived.app.src.kotlin.service.entities.Car
import ru.eaosipov.imdrived.app.src.kotlin.service.entities.CarDetailsPartial
import ru.eaosipov.imdrived.databinding.ActivityCarDetailsBinding

/**
 * CarDetailsActivity — экран с подробной информацией об автомобиле.
 * Позволяет просматривать детали автомобиля, добавлять его в избранное
 * и переходить к бронированию.
 */
class CarDetailsActivity : AppCompatActivity() {

    // Привязка к макету активности
    private lateinit var binding: ActivityCarDetailsBinding

    // ID автомобиля, переданный из предыдущего экрана
    private var carId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получаем ID автомобиля из Intent
        carId = intent.getLongExtra("car_id", -1L)

        // Проверяем корректность ID автомобиля
        if (carId == -1L) {
            Toast.makeText(this, "Некорректный ID автомобиля", Toast.LENGTH_SHORT).show()
            finish() // Закрываем активность, если ID невалидный
            return
        }

        // Настраиваем слушатели кнопок
        setupListeners()

        // Загружаем данные об автомобиле
        loadCarDetails()
    }

    /**
     * Настройка слушателей для кнопок:
     * - Кнопка "Назад" закрывает активность.
     * - Кнопка "Избранное" добавляет/удаляет автомобиль из избранного.
     * - Кнопка "Забронировать" открывает экран бронирования.
     */
    private fun setupListeners() {
        // Обработка нажатия на кнопку "Назад"
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Обработка нажатия на кнопку "Избранное"
        binding.btnFavorite.setOnClickListener {
            // Запускаем асинхронную операцию для обновления статуса избранного
            lifecycleScope.launch {
                // Получаем данные об автомобиле из базы
                val db = AppDatabase.getDatabase(applicationContext)
                val car = withContext(Dispatchers.IO) { db.carDao().getCarById(carId) }

                if (car != null) {
                    // Инвертируем текущий статус избранного
                    val newFavoriteStatus = !car.isFavorite

                    // Обновляем статус в базе данных
                    withContext(Dispatchers.IO) {
                        db.carDao().updateFavoriteStatus(car.id, newFavoriteStatus)
                    }

                    // Обновляем UI в главном потоке
                    if (newFavoriteStatus) {
                        binding.btnFavorite.setImageResource(R.drawable.ic_heart_filled)
                        Toast.makeText(this@CarDetailsActivity, "Добавлено в избранное", Toast.LENGTH_SHORT).show()
                    } else {
                        binding.btnFavorite.setImageResource(R.drawable.ic_heart)
                        Toast.makeText(this@CarDetailsActivity, "Удалено из избранного", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@CarDetailsActivity, "Ошибка: автомобиль не найден", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Обработка нажатия на кнопку "Забронировать"
        binding.btnBookCar.setOnClickListener {
            // Открываем экран бронирования, передавая ID автомобиля
            val intent = Intent(this, RentCarActivity::class.java)
            intent.putExtra("car_id", carId)
            startActivity(intent)
        }
    }

    /**
     * Загрузка данных об автомобиле и его деталях.
     * Показывает индикатор загрузки во время выполнения запросов.
     */
    private fun loadCarDetails() {
        showLoading(true) // Показываем индикатор загрузки

        lifecycleScope.launch {
            try {
                // Получаем данные об автомобиле и его деталях
                val car = fetchCarById(carId)
                val carDetails = fetchCarDetailsById(carId)

                if (car != null && carDetails != null) {
                    // Заполняем UI данными
                    binding.tvCarTitle.text = car.model
                    binding.tvCarLocation.text = carDetails.location_address
                    binding.tvCarDescription.text = carDetails.model_description
                    binding.tvCarPrice.text = "₽${car.pricePerHour}/час"

                    // Загружаем изображение автомобиля с помощью Picasso
                    Picasso.get()
                        .load(car.imageUri)
                        .placeholder(R.drawable.ic_car) // Заглушка на время загрузки
                        .into(binding.ivCarImage)

                    // Устанавливаем иконку избранного в зависимости от статуса
                    binding.btnFavorite.setImageResource(
                        if (car.isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart
                    )
                } else {
                    Toast.makeText(this@CarDetailsActivity, "Автомобиль не найден", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CarDetailsActivity, "Ошибка загрузки данных. Попробуйте снова.", Toast.LENGTH_SHORT).show()
            } finally {
                showLoading(false) // Скрываем индикатор загрузки
            }
        }
    }

    /**
     * Получение данных об автомобиле по его ID.
     * @param id — ID автомобиля.
     * @return Объект Car или null, если автомобиль не найден.
     */
    private suspend fun fetchCarById(id: Long): Car? = withContext(Dispatchers.IO) {
        val db = AppDatabase.getDatabase(applicationContext)
        db.carDao().getCarById(id)
    }

    /**
     * Получение дополнительных деталей об автомобиле по его ID.
     * @param id — ID автомобиля.
     * @return Объект CarDetailsPartial или null, если детали не найдены.
     */
    private suspend fun fetchCarDetailsById(id: Long): CarDetailsPartial? = withContext(Dispatchers.IO) {
        val db = AppDatabase.getDatabase(applicationContext)
        db.carDetailsDao().getCarDetailsByCarId(id)
    }

    /**
     * Показывает или скрывает индикатор загрузки.
     * @param isLoading — true, если нужно показать индикатор, иначе false.
     */
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.scrollView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }
}