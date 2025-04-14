package ru.eaosipov.imdrived.app.src.kotlin.client.bookings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Toast
import ru.eaosipov.imdrived.app.src.kotlin.service.db.AppDatabase
import ru.eaosipov.imdrived.app.src.kotlin.service.entities.BookingWithCar
import ru.eaosipov.imdrived.app.src.kotlin.service.repository.UserRepository
import ru.eaosipov.imdrived.databinding.ActivityMyBookingsBinding

class MyBookingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyBookingsBinding
    private lateinit var adapter: BookingAdapter

    private var currentUserId: Int = -1  // ID пользователя
    private var currentUserEmail: String? = null  // Email пользователя


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyBookingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTopBar()
        setupRecyclerView()
        loadBookings()
    }

    private fun setupTopBar() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.tvScreenTitle.text = "Мои бронирования"
    }

    private fun setupRecyclerView() {
        adapter = BookingAdapter { bookingWithCar ->
            // Обработка нажатия на бронирование:
            if (bookingWithCar.booking.isEnded) {
                // Если аренда завершена, показываем сообщение
                Toast.makeText(this, "Аренда завершена", Toast.LENGTH_SHORT).show()
            } else {
                // Если аренда не завершена, переходим на экран с деталями бронирования
                val intent = Intent(this, BookingDetailsActivity::class.java)
                intent.putExtra("id", bookingWithCar.booking.id)
                intent.putExtra("car_id", bookingWithCar.car.id)
                intent.putExtra("user_id", bookingWithCar.booking.userId)
                startActivity(intent)
            }
        }
        binding.rvBookings.layoutManager = LinearLayoutManager(this)
        binding.rvBookings.adapter = adapter
    }

    private fun loadBookings() {
        // Показываем индикатор загрузки (если есть)
        lifecycleScope.launch {
            try {
                val prefs: SharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
                currentUserEmail = prefs.getString("user_email", null)

                if (currentUserEmail != null) {
                        val dao = AppDatabase.getDatabase(applicationContext).userRegistrationDao()
                        val userRepository = UserRepository(dao)
                        val userData = withContext(Dispatchers.IO) {
                            userRepository.getUserByEmail(currentUserEmail!!)
                        }
                        if (userData != null) {
                            // Сохраняем данные о пользователе
                            currentUserId = userData.id
                        } else {
                            Toast.makeText(this@MyBookingsActivity, "Пользователь не найден", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this@MyBookingsActivity, "Email пользователя не найден", Toast.LENGTH_SHORT).show()
                }
                val bookings = fetchBookingsForUser(currentUserId)
                adapter.submitList(bookings)
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Ошибка загрузки бронирований. Попробуйте снова.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadBookings()  // Перезагрузка списка бронирований
    }

    private suspend fun fetchBookingsForUser(userId: Int): List<BookingWithCar> = withContext(Dispatchers.IO) {
        val db = AppDatabase.getDatabase(applicationContext)
        db.bookingDao().getBookingsForUser(userId)
    }
}
