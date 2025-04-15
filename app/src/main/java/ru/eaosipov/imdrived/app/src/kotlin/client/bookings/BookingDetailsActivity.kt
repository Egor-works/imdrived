package ru.eaosipov.imdrived.app.src.kotlin.client.bookings

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
import ru.eaosipov.imdrived.app.src.kotlin.service.db.AppDatabase
import ru.eaosipov.imdrived.app.src.kotlin.service.entities.BookingWithCar
import ru.eaosipov.imdrived.app.src.kotlin.service.entities.UserRegistrationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.eaosipov.imdrived.app.src.kotlin.service.entities.CarDetailsPartial
import ru.eaosipov.imdrived.databinding.ActivityBookingDetailsBinding

/**
 * BookingDetailsActivity — экран, отображающий детали бронирования автомобиля.
 * Показывает информацию о бронировании, автомобиле, стоимости аренды, страховке,
 * а также данные о водителе. Пользователь может отменить бронирование.
 */
class BookingDetailsActivity : AppCompatActivity() {

    // Привязка к макету активности
    private lateinit var binding: ActivityBookingDetailsBinding

    // ID бронирования, автомобиля и пользователя
    private var bookingId: Long = -1
    private var carId: Long = -1
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Инициализация привязки к макету
        binding = ActivityBookingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получение ID бронирования, автомобиля и пользователя из Intent
        bookingId = intent.getLongExtra("id", -1L)
        carId = intent.getLongExtra("car_id", -1L)
        userId = intent.getIntExtra("user_id", -1)

        // Проверка корректности ID
        if (bookingId == -1L || carId == -1L) {
            Toast.makeText(this, "Некорректный ID бронирования/автомобиля", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Настройка слушателей кнопок
        setupListeners()
        // Загрузка деталей бронирования
        loadBookingDetails()
    }

    /**
     * Настройка слушателей для кнопок:
     * - Кнопка "Назад" закрывает активность.
     * - Кнопка "Отменить бронирование" вызывает метод отмены бронирования.
     */
    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnCancelBooking.setOnClickListener {
            cancelBooking()
        }
    }

    /**
     * Загрузка деталей бронирования:
     * - Получение данных о бронировании и автомобиле.
     * - Обновление UI на основе полученных данных.
     */
    private fun loadBookingDetails() {
        lifecycleScope.launch {
            try {
                // Получение данных о бронировании с автомобилем
                val bookingWithCar = fetchBookingWithCar(bookingId)
                // Получение дополнительных данных об автомобиле (например, адрес)
                val carDetailsPartial = fetchCarDetailsPartial(carId)

                if (bookingWithCar != null && carDetailsPartial != null) {
                    // Обновление UI
                    val booking = bookingWithCar.booking
                    val car = bookingWithCar.car

                    // Отображение ID бронирования
                    binding.tvBookingId.text = "Бронирование #${booking.id}"

                    // Отображение марки и модели автомобиля
                    binding.tvCarDetails.text = "${car.brand} ${car.model}"

                    // Расчет и отображение стоимости аренды
                    binding.tvRentalCostLabel.text = "Аренда автомобиля x${booking.bookDays} дня:"
                    binding.tvRentalCost.text = "₽${booking.carPrice * booking.bookDays}"

                    // Расчет и отображение стоимости страховки
                    binding.tvInsuranceCostLabel.text = "Страховка x${booking.bookDays} дня:"
                    binding.tvInsuranceCost.text = "₽${booking.insurancePrice * booking.bookDays}"

                    // Отображение общей стоимости
                    binding.tvTotalCost.text = "₽${booking.totalPrice}"

                    // Отображение адреса автомобиля
                    binding.tvCarLocation.text = "Адрес: ${carDetailsPartial.location_address}"

                    // Загрузка изображения автомобиля с использованием Picasso
                    Picasso.get()
                        .load(car.imageUri)
                        .placeholder(ru.eaosipov.imdrived.R.drawable.ic_car)
                        .into(binding.ivCarImage)

                    // Получение и отображение данных о водителе
                    val userData = fetchDriverById(userId)
                    if (userData != null) {
                        binding.tvDriverName.text = "${userData.firstName} ${userData.lastName}"
                        binding.tvDriverLicense.text = "${userData.licenseNumber}"
                    }

                } else {
                    Toast.makeText(this@BookingDetailsActivity, "Данные бронирования не найдены", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@BookingDetailsActivity, "Ошибка загрузки данных. Попробуйте снова.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Получение данных о бронировании с автомобилем из базы данных.
     * @param bookingId ID бронирования.
     * @return BookingWithCar или null, если данные не найдены.
     */
    private suspend fun fetchBookingWithCar(bookingId: Long): BookingWithCar? = withContext(Dispatchers.IO) {
        val db = AppDatabase.getDatabase(applicationContext)
        db.bookingDao().getBookingById(bookingId)
    }

    /**
     * Получение дополнительных данных об автомобиле из базы данных.
     * @param carId ID автомобиля.
     * @return CarDetailsPartial или null, если данные не найдены.
     */
    private suspend fun fetchCarDetailsPartial(carId: Long): CarDetailsPartial? = withContext(Dispatchers.IO) {
        val db = AppDatabase.getDatabase(applicationContext)
        db.carDetailsDao().getCarDetailsByCarId(carId)
    }

    /**
     * Получение данных о водителе из базы данных.
     * @param userId ID пользователя.
     * @return UserRegistrationData или null, если данные не найдены.
     */
    private suspend fun fetchDriverById(userId: Int): UserRegistrationData? = withContext(Dispatchers.IO) {
        val db = AppDatabase.getDatabase(applicationContext)
        db.userRegistrationDao().getUserById(userId)
    }

    /**
     * Отмена бронирования:
     * - Обновление статуса бронирования (isEnded = true).
     * - Закрытие активности после успешной отмены.
     */
    private fun cancelBooking() {
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val db = AppDatabase.getDatabase(applicationContext)
                    db.bookingDao().updateBookingStatus(bookingId, true)
                }
                Toast.makeText(this@BookingDetailsActivity, "Бронирование отменено", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@BookingDetailsActivity, "Ошибка отмены бронирования", Toast.LENGTH_SHORT).show()
            }
        }
    }
}