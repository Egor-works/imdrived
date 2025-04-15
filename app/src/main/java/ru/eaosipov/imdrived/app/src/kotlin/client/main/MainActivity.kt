package ru.eaosipov.imdrived.app.src.kotlin.client.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import ru.eaosipov.imdrived.app.src.kotlin.client.rental.RentCarActivity
import ru.eaosipov.imdrived.app.src.kotlin.client.details.CarDetailsActivity
import ru.eaosipov.imdrived.app.src.kotlin.client.favorite.FavoriteActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.eaosipov.imdrived.app.src.kotlin.service.db.AppDatabase
import ru.eaosipov.imdrived.app.src.kotlin.service.entities.Car
import ru.eaosipov.imdrived.app.src.kotlin.client.search.SearchResultsActivity
import ru.eaosipov.imdrived.app.src.kotlin.client.settings.SettingsActivity
import ru.eaosipov.imdrived.databinding.ActivityMainBinding

/**
 * MainActivity - главный экран приложения, отображающий список доступных автомобилей.
 * Позволяет выполнять поиск, переход к деталям автомобиля, бронирование и навигацию по разделам.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // ViewBinding для доступа к элементам интерфейса
    private lateinit var carAdapter: CarAdapter // Адаптер для RecyclerView с автомобилями

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Инициализация ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView() // Настройка RecyclerView
        setupListeners() // Настройка обработчиков событий
        loadCarData() // Загрузка данных об автомобилях
    }

    /**
     * Настраивает RecyclerView для отображения списка автомобилей.
     */
    private fun setupRecyclerView() {
        // Инициализация адаптера с обработчиками кликов
        carAdapter = CarAdapter { car, action ->
            when (action) {
                CarAdapter.Action.BOOK -> {
                    // Переход на экран бронирования с передачей ID автомобиля
                    val intent = Intent(this, RentCarActivity::class.java)
                    intent.putExtra("car_id", car.id)
                    startActivity(intent)
                }
                CarAdapter.Action.DETAILS -> {
                    // Переход на экран деталей автомобиля с передачей ID
                    val intent = Intent(this, CarDetailsActivity::class.java)
                    intent.putExtra("car_id", car.id)
                    startActivity(intent)
                }
            }
        }
        // Настройка RecyclerView
        binding.rvCars.apply {
            layoutManager = LinearLayoutManager(this@MainActivity) // Линейный макет (вертикальный список)
            adapter = carAdapter // Установка адаптера
        }
    }

    /**
     * Настраивает обработчики событий для элементов интерфейса.
     */
    private fun setupListeners() {
        // Обработка нажатия на иконку поиска
        binding.ivSearchIcon.setOnClickListener {
            val query = binding.etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                // Переход на экран результатов поиска с передачей запроса
                startActivity(Intent(this, SearchResultsActivity::class.java).apply {
                    putExtra("search_query", query)
                })
                Snackbar.make(binding.root, "Результаты поиска", Snackbar.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Введите марку для поиска", Toast.LENGTH_SHORT).show()
            }
        }

        // Обработка нажатия на кнопку "Повторить попытку" при ошибке загрузки
        binding.btnRetry.setOnClickListener {
            loadCarData()
        }

        // Навигационная панель внизу экрана:
        binding.btnHome.setOnClickListener {
            // Переход на главный экран (текущий экран)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        binding.btnFavorites.setOnClickListener {
            // Переход на экран избранного
            startActivity(Intent(this, FavoriteActivity::class.java))
            finish()
        }
        binding.btnSettings.setOnClickListener {
            // Переход на экран настроек
            startActivity(Intent(this, SettingsActivity::class.java))
            finish()
        }
    }

    /**
     * Загружает данные об автомобилях из базы данных.
     */
    private fun loadCarData() {
        // Показываем индикатор загрузки и скрываем сообщение об ошибке
        binding.progressBar.visibility = android.view.View.VISIBLE
        binding.layoutError.visibility = android.view.View.GONE

        lifecycleScope.launch {
            try {
                // Получение списка автомобилей из базы данных
                val cars = fetchCarsFromDatabase()
                // Обновление адаптера новыми данными
                carAdapter.submitList(cars)
            } catch (e: Exception) {
                // В случае ошибки показываем сообщение
                binding.layoutError.visibility = android.view.View.VISIBLE
            } finally {
                // Скрываем индикатор загрузки
                binding.progressBar.visibility = android.view.View.GONE
            }
        }
    }

    /**
     * Получает список автомобилей из базы данных (выполняется в фоновом потоке).
     */
    private suspend fun fetchCarsFromDatabase(): List<Car> = withContext(Dispatchers.IO) {
        // Получение экземпляра базы данных
        val db = AppDatabase.getDatabase(applicationContext)
        // Запрос всех автомобилей из базы
        db.carDao().getAllCars()
    }
}