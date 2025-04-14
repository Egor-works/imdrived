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

            setupTopBar()
            setupRecyclerView()
            loadFavoriteCars()
        }

        private fun setupTopBar() {
            binding.tvTitle.text = "Избранное"
        }

        private fun setupRecyclerView() {
            adapter = FavoriteCarAdapter { car, action ->
                when (action) {
                    FavoriteCarAdapter.Action.BOOK -> {
                        // Переход на экран оформления аренды
                        val intent = Intent(this, RentCarActivity::class.java)
                        intent.putExtra("car_id", car.id)
                        startActivity(intent)
                    }
                    FavoriteCarAdapter.Action.DETAILS -> {
                        // Переход на экран с детальной информацией об автомобиле
                        val intent = Intent(this, CarDetailsActivity::class.java)
                        intent.putExtra("car_id", car.id)
                        startActivity(intent)
                    }
                }
            }

            // Нижняя навигационная панель:
            binding.btnHome.setOnClickListener {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            binding.btnFavorites.setOnClickListener {
                // Экран избранное
                startActivity(Intent(this, FavoriteActivity::class.java))
                finish()
                Snackbar.make(binding.root, "Избранное", Snackbar.LENGTH_SHORT).show()
            }
            binding.btnSettings.setOnClickListener {
                // Так как мы уже на экране настроек, можно оставить пустым или просто показать Snackbar.
                startActivity(Intent(this, SettingsActivity::class.java))
                finish()
            }

            binding.rvFavoriteCars.layoutManager = LinearLayoutManager(this)
            binding.rvFavoriteCars.adapter = adapter
        }

        private fun loadFavoriteCars() {
            lifecycleScope.launch {
                try {
                    // Загрузка избранных автомобилей из базы данных
                    val favorites = fetchFavoriteCars()
                    adapter.submitList(favorites)
                } catch (e: Exception) {
                    Toast.makeText(this@FavoriteActivity, "Ошибка загрузки избранного.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private suspend fun fetchFavoriteCars(): List<Car> = withContext(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(applicationContext)
            // Предположим, что у вас в Car есть поле isFavorite (или другой способ отметить избранное)
            db.carDao().getFavoriteCars()
        }
    }
