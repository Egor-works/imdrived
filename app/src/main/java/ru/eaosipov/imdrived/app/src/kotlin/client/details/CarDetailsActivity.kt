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

class CarDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarDetailsBinding
    private var carId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        carId = intent.getLongExtra("car_id", -1L)

        if (carId == -1L) {
            Toast.makeText(this, "Некорректный ID автомобиля", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupListeners()
        loadCarDetails()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnFavorite.setOnClickListener {
            // Здесь будет логика добавления в избранное
            // Запускаем асинхронную операцию для переключения состояния избранного
            lifecycleScope.launch {
                // Обращаемся к базе в IO-потоке
                val db = AppDatabase.getDatabase(applicationContext)
                val car = withContext(Dispatchers.IO) { db.carDao().getCarById(carId) }
                if (car != null) {
                    // Вычисляем новое состояние избранного: переключаем флаг
                    val newFavoriteStatus = !car.isFavorite
                    withContext(Dispatchers.IO) {
                        db.carDao().updateFavoriteStatus(car.id, newFavoriteStatus)
                    }
                    // Обновляем UI в главном потоке: меняем изображение кнопки
                    if (newFavoriteStatus) {
                        binding.btnFavorite.setImageResource(R.drawable.ic_heart_filled)
                        Toast.makeText(this@CarDetailsActivity, "Добавлено в избранное", Toast.LENGTH_SHORT).show()
                    } else {
                        binding.btnFavorite.setImageResource(R.drawable.ic_heart)  // Предположим, исходная иконка – outline
                        Toast.makeText(this@CarDetailsActivity, "Удалено из избранного", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@CarDetailsActivity, "Ошибка: автомобиль не найден", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnBookCar.setOnClickListener {
            // Здесь логика перехода на экран бронирования
            // Toast.makeText(this, "Переход к бронированию", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, RentCarActivity::class.java)
            intent.putExtra("car_id", carId) // передаем ID авто
            startActivity(intent)
        }
    }

    private fun loadCarDetails() {
        showLoading(true)

        lifecycleScope.launch {
            try {
                val car = fetchCarById(carId)
                val carDetails = fetchCarDetailsById(carId)

                if (car != null && carDetails != null) {
                    // Заполняем UI
                    binding.tvCarTitle.text = car.model
                    binding.tvCarLocation.text = carDetails.location_address
                    binding.tvCarDescription.text = carDetails.model_description
                    binding.tvCarPrice.text = "₽${car.pricePerHour}/час"

                    Picasso.get()
                        .load(car.imageUri)
                        .placeholder(R.drawable.ic_car) // если хочешь заглушку
                        //.error(R.drawable.image_error)             // если ошибка загрузки
                        .into(binding.ivCarImage)

                    // Устанавливаем правильную иконку избранного
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
                showLoading(false)
            }
        }
    }

    private suspend fun fetchCarById(id: Long): Car? = withContext(Dispatchers.IO) {
        val db = AppDatabase.getDatabase(applicationContext)
        db.carDao().getCarById(id)
    }

    private suspend fun fetchCarDetailsById(id: Long): CarDetailsPartial? = withContext(Dispatchers.IO) {
        val db = AppDatabase.getDatabase(applicationContext)
        db.carDetailsDao().getCarDetailsByCarId(id)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.scrollView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }
}
