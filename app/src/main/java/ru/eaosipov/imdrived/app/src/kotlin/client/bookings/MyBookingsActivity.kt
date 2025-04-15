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

/**
 * MyBookingsActivity — экран, отображающий список бронирований текущего пользователя.
 * Пользователь может просматривать свои активные и завершенные бронирования,
 * а также переходить к деталям конкретного бронирования.
 */
class MyBookingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyBookingsBinding
    private lateinit var adapter: BookingAdapter

    private var currentUserId: Int = -1  // ID пользователя, инициализируется значением по умолчанию (-1)
    private var currentUserEmail: String? = null  // Email пользователя, может быть null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyBookingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Настройка верхней панели (кнопка "Назад" и заголовок)
        setupTopBar()
        // Настройка RecyclerView для отображения списка бронирований
        setupRecyclerView()
        // Загрузка списка бронирований для текущего пользователя
        loadBookings()
    }

    /**
     * Настройка верхней панели экрана:
     * - Установка заголовка "Мои бронирования".
     * - Обработка нажатия на кнопку "Назад" (закрытие экрана).
     */
    private fun setupTopBar() {
        binding.btnBack.setOnClickListener {
            finish()  // Закрытие текущего экрана
        }
        binding.tvScreenTitle.text = "Мои бронирования"  // Установка заголовка
    }

    /**
     * Настройка RecyclerView для отображения списка бронирований:
     * - Инициализация адаптера BookingAdapter.
     * - Установка LinearLayoutManager для вертикального списка.
     * - Обработка нажатия на элемент списка:
     *   - Если бронирование завершено, показывается Toast.
     *   - Если бронирование активно, открывается экран BookingDetailsActivity.
     */
    private fun setupRecyclerView() {
        adapter = BookingAdapter { bookingWithCar ->
            if (bookingWithCar.booking.isEnded) {
                Toast.makeText(this, "Аренда завершена", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, BookingDetailsActivity::class.java)
                intent.putExtra("id", bookingWithCar.booking.id)  // Передача ID бронирования
                intent.putExtra("car_id", bookingWithCar.car.id)  // Передача ID автомобиля
                intent.putExtra("user_id", bookingWithCar.booking.userId)  // Передача ID пользователя
                startActivity(intent)
            }
        }
        binding.rvBookings.layoutManager = LinearLayoutManager(this)  // Вертикальный список
        binding.rvBookings.adapter = adapter  // Установка адаптера
    }

    /**
     * Загрузка списка бронирований для текущего пользователя:
     * - Получение email пользователя из SharedPreferences.
     * - Поиск пользователя в базе данных по email.
     * - Загрузка списка бронирований для найденного пользователя.
     * - Обновление списка в адаптере.
     * - Обработка ошибок (например, отсутствие пользователя или бронирований).
     */
    private fun loadBookings() {
        lifecycleScope.launch {
            try {
                val prefs: SharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
                currentUserEmail = prefs.getString("user_email", null)  // Получение email из SharedPreferences

                if (currentUserEmail != null) {
                    val dao = AppDatabase.getDatabase(applicationContext).userRegistrationDao()
                    val userRepository = UserRepository(dao)
                    val userData = withContext(Dispatchers.IO) {
                        userRepository.getUserByEmail(currentUserEmail!!)  // Поиск пользователя по email
                    }
                    if (userData != null) {
                        currentUserId = userData.id  // Сохранение ID пользователя
                    } else {
                        Toast.makeText(this@MyBookingsActivity, "Пользователь не найден", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MyBookingsActivity, "Email пользователя не найден", Toast.LENGTH_SHORT).show()
                }
                val bookings = fetchBookingsForUser(currentUserId)  // Загрузка бронирований
                adapter.submitList(bookings)  // Обновление списка в адаптере
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Ошибка загрузки бронирований. Попробуйте снова.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Переопределение метода onResume:
     * - При возвращении на экран обновляется список бронирований.
     */
    override fun onResume() {
        super.onResume()
        loadBookings()  // Перезагрузка списка бронирований
    }

    /**
     * Получение списка бронирований для пользователя:
     * @param userId — ID пользователя.
     * @return Список бронирований с данными об автомобилях.
     */
    private suspend fun fetchBookingsForUser(userId: Int): List<BookingWithCar> = withContext(Dispatchers.IO) {
        val db = AppDatabase.getDatabase(applicationContext)
        db.bookingDao().getBookingsForUser(userId)  // Запрос к базе данных
    }
}