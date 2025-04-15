package ru.eaosipov.imdrived.app.src.kotlin.client.search

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.eaosipov.imdrived.app.src.kotlin.service.db.AppDatabase
import ru.eaosipov.imdrived.app.src.kotlin.service.entities.Car
import ru.eaosipov.imdrived.app.src.kotlin.client.details.CarDetailsActivity
import ru.eaosipov.imdrived.app.src.kotlin.client.main.CarAdapter
import ru.eaosipov.imdrived.app.src.kotlin.client.main.MainActivity
import ru.eaosipov.imdrived.app.src.kotlin.client.rental.RentCarActivity
import ru.eaosipov.imdrived.databinding.ActivitySearchResultsBinding

/**
 * SearchResultsActivity - экран, отображающий результаты поиска автомобилей.
 * Позволяет пользователю просматривать список найденных автомобилей,
 * переходить к их деталям или оформлять аренду.
 */
class SearchResultsActivity : AppCompatActivity() {

    // Привязка к макету активности
    private lateinit var binding: ActivitySearchResultsBinding

    // Адаптер для RecyclerView, отображающего список автомобилей
    private lateinit var carAdapter: CarAdapter

    // Поисковый запрос, переданный через Intent
    private val searchQuery: String by lazy { intent.getStringExtra("search_query") ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Инициализация привязки к макету
        binding = ActivitySearchResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Обработчик нажатия на кнопку возврата на главный экран
        binding.btnBackToMain.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Настройка RecyclerView для отображения списка автомобилей
        setupRecyclerView()

        // Настройка слушателей (например, для кнопки повторной загрузки)
        setupListeners()

        // Загрузка результатов поиска
        loadSearchResults()
    }

    /**
     * Настраивает RecyclerView для отображения списка автомобилей.
     * Использует адаптер CarAdapter для обработки кликов по элементам списка.
     */
    private fun setupRecyclerView() {
        carAdapter = CarAdapter { car, action ->
            when (action) {
                // Обработка нажатия на кнопку "Арендовать"
                CarAdapter.Action.BOOK -> {
                    val intent = Intent(this, RentCarActivity::class.java)
                    intent.putExtra("car_id", car.id) // Передача ID автомобиля
                    startActivity(intent)
                }
                // Обработка нажатия на кнопку "Детали"
                CarAdapter.Action.DETAILS -> {
                    val intent = Intent(this, CarDetailsActivity::class.java)
                    intent.putExtra("car_id", car.id) // Передача ID автомобиля
                    startActivity(intent)
                }
            }
        }
        // Настройка RecyclerView с LinearLayoutManager и адаптером
        binding.rvSearchResults.apply {
            layoutManager = LinearLayoutManager(this@SearchResultsActivity)
            adapter = carAdapter
        }
    }

    /**
     * Настраивает слушатели для элементов интерфейса.
     * Например, кнопка "Повторить" для повторной загрузки данных.
     */
    private fun setupListeners() {
        binding.btnRetry.setOnClickListener {
            loadSearchResults()
        }
    }

    /**
     * Загружает результаты поиска из базы данных.
     * Показывает индикатор загрузки и обрабатывает возможные ошибки.
     */
    private fun loadSearchResults() {
        // Показываем индикатор загрузки и скрываем сообщение об ошибке
        binding.progressBar.visibility = android.view.View.VISIBLE
        binding.layoutError.visibility = android.view.View.GONE

        lifecycleScope.launch {
            try {
                // Получение списка автомобилей из базы данных
                val cars = fetchCarsFromDatabase(searchQuery)
                // Обновление списка в адаптере
                carAdapter.submitList(cars)
            } catch (e: Exception) {
                // В случае ошибки показываем сообщение и кнопку "Повторить"
                binding.layoutError.visibility = android.view.View.VISIBLE
                Snackbar.make(binding.root, "Ошибка загрузки данных. Попробуйте снова.", Snackbar.LENGTH_SHORT).show()
            } finally {
                // Скрываем индикатор загрузки после завершения операции
                binding.progressBar.visibility = android.view.View.GONE
            }
        }
    }

    /**
     * Получает список автомобилей из базы данных в соответствии с поисковым запросом.
     * @param query Поисковый запрос.
     * @return Список автомобилей.
     */
    private suspend fun fetchCarsFromDatabase(query: String): List<Car> = withContext(Dispatchers.IO) {
        val db = AppDatabase.getDatabase(applicationContext)
        if (query.isBlank()) {
            // Если запрос пустой, возвращаем все автомобили
            db.carDao().getAllCars()
        } else {
            // Иначе выполняем поиск по запросу
            db.carDao().searchCars(query)
        }
    }
}