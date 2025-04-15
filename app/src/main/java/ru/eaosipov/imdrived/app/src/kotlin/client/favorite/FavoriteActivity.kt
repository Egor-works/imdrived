package ru.eaosipov.imdrived.app.src.kotlin.client.favorite

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import ru.eaosipov.imdrived.app.src.kotlin.client.rental.RentCarActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.eaosipov.imdrived.app.src.kotlin.service.db.AppDatabase
import ru.eaosipov.imdrived.app.src.kotlin.service.entities.Car
import ru.eaosipov.imdrived.app.src.kotlin.client.details.CarDetailsActivity
import ru.eaosipov.imdrived.app.src.kotlin.client.main.MainActivity
import ru.eaosipov.imdrived.app.src.kotlin.client.settings.SettingsActivity
import ru.eaosipov.imdrived.databinding.ActivityFavoriteBinding

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var adapter: FavoriteCarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTopBar() // Настройка верхней панели
        setupRecyclerView() // Настройка RecyclerView для отображения списка избранных автомобилей
        loadFavoriteCars() // Загрузка списка избранных автомобилей
    }

    // Настройка верхней панели (заголовок экрана)
    private fun setupTopBar() {
        binding.tvTitle.text = "Избранное" // Установка текста заголовка
    }

    // Настройка RecyclerView и адаптера для списка избранных автомобилей
    private fun setupRecyclerView() {
        // Инициализация адаптера с обработчиком действий (бронирование или просмотр деталей)
        adapter = FavoriteCarAdapter { car, action ->
            when (action) {
                FavoriteCarAdapter.Action.BOOK -> {
                    // Переход на экран оформления аренды с передачей ID автомобиля
                    val intent = Intent(this, RentCarActivity::class.java)
                    intent.putExtra("car_id", car.id)
                    startActivity(intent)
                }
                FavoriteCarAdapter.Action.DETAILS -> {
                    // Переход на экран деталей автомобиля с передачей ID автомобиля
                    val intent = Intent(this, CarDetailsActivity::class.java)
                    intent.putExtra("car_id", car.id)
                    startActivity(intent)
                }
            }
        }

        // Настройка нижней навигационной панели
        binding.btnHome.setOnClickListener {
            // Переход на главный экран
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Закрытие текущего экрана
        }
        binding.btnFavorites.setOnClickListener {
            // Уже находимся на экране избранного, поэтому просто показываем уведомление
            Snackbar.make(binding.root, "Избранное", Snackbar.LENGTH_SHORT).show()
        }
        binding.btnSettings.setOnClickListener {
            // Переход на экран настроек
            startActivity(Intent(this, SettingsActivity::class.java))
            finish() // Закрытие текущего экрана
        }

        // Настройка RecyclerView: вертикальный список с адаптером
        binding.rvFavoriteCars.layoutManager = LinearLayoutManager(this)
        binding.rvFavoriteCars.adapter = adapter
    }

    // Загрузка списка избранных автомобилей из базы данных
    private fun loadFavoriteCars() {
        lifecycleScope.launch {
            try {
                val favorites = fetchFavoriteCars() // Получение списка избранных автомобилей
                adapter.submitList(favorites) // Обновление списка в адаптере
            } catch (e: Exception) {
                // Обработка ошибки загрузки
                Toast.makeText(this@FavoriteActivity, "Ошибка загрузки избранного.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Получение списка избранных автомобилей из базы данных (в фоновом потоке)
    private suspend fun fetchFavoriteCars(): List<Car> = withContext(Dispatchers.IO) {
        val db = AppDatabase.getDatabase(applicationContext)
        db.carDao().getFavoriteCars() // Запрос к базе данных
    }
}