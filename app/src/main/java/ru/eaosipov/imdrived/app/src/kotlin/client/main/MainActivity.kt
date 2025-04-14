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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var carAdapter: CarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        loadCarData()
    }

    private fun setupRecyclerView() {
        carAdapter = CarAdapter { car, action ->
            when (action) {
                CarAdapter.Action.BOOK -> {
                    // TODO: Сделать экран "Оформление аренды"
                    // Переход на экран "Оформление аренды"
                    //startActivity(Intent(this, RentCarActivity::class.java).apply {
                    //    putExtra("car_id", car.id)
                    //})
                    //Snackbar.make(binding.root, "Оформление аренды", Snackbar.LENGTH_SHORT).show()
                    val intent = Intent(this, RentCarActivity::class.java)
                    intent.putExtra("car_id", car.id) // передаем ID авто
                    startActivity(intent)
                }
                CarAdapter.Action.DETAILS -> {
                    // TODO: Сделать экран "Детали"
                    // Переход на экран "Детали"
                    //startActivity(Intent(this, CarDetailsActivity::class.java).apply {
                    //    putExtra("car_id", car.id)
                    //})
                    //Snackbar.make(binding.root, "Детали", Snackbar.LENGTH_SHORT).show()
                    val intent = Intent(this, CarDetailsActivity::class.java)
                    intent.putExtra("car_id", car.id) // передаем ID авто
                    startActivity(intent)
                }
            }
        }
        binding.rvCars.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = carAdapter
        }
    }

    private fun setupListeners() {
        // Обработка нажатия на кнопку поиска
        binding.ivSearchIcon.setOnClickListener {
            val query = binding.etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                // TODO: Сделать экран "Результаты поиска"
                // Переход на экран "Результаты поиска"
                startActivity(Intent(this, SearchResultsActivity::class.java).apply {
                    putExtra("search_query", query)
                })
                Snackbar.make(binding.root, "Результаты поиска", Snackbar.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Введите марку для поиска", Toast.LENGTH_SHORT).show()
            }
        }

        // Обработка кнопки "Повторить попытку" при ошибке загрузки
        binding.btnRetry.setOnClickListener {
            loadCarData()
        }

        // Нижняя навигационная панель:
        binding.btnHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        binding.btnFavorites.setOnClickListener {
            // TODO: Сделать потом экран избранное
            startActivity(Intent(this, FavoriteActivity::class.java))
            finish()
            //Snackbar.make(binding.root, "Избранное", Snackbar.LENGTH_SHORT).show()
        }
        binding.btnSettings.setOnClickListener {
            // Так как мы уже на экране настроек, можно оставить пустым или просто показать Snackbar.
            startActivity(Intent(this, SettingsActivity::class.java))
            finish()
        }
    }

    private fun loadCarData() {
        // Показываем индикатор загрузки и скрываем ошибки
        binding.progressBar.visibility = android.view.View.VISIBLE
        binding.layoutError.visibility = android.view.View.GONE

        lifecycleScope.launch {
            //try {
                // Получение данных из базы данных
                val cars = fetchCarsFromDatabase()
                // Обновляем адаптер с данными
                carAdapter.submitList(cars)
            //} //catch (e: Exception) {
                // Обработка ошибок
              //  binding.layoutError.visibility = android.view.View.VISIBLE
            //} finally {
                // Скрываем индикатор загрузки
                binding.progressBar.visibility = android.view.View.GONE
            }
        //}
    }

    private suspend fun fetchCarsFromDatabase(): List<Car> = withContext(Dispatchers.IO) {
        // Создание или получение экземпляра базы данных
        val db = AppDatabase.getDatabase(applicationContext)
        // Выполнение запроса к базе данных
        db.carDao().getAllCars()
    }

}
