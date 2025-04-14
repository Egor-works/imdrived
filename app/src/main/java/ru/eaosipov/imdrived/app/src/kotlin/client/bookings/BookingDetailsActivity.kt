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

class BookingDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingDetailsBinding
    private var bookingId: Long = -1
    private var carId: Long = -1
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Предполагается, что оба ID передаются через Intent
        bookingId = intent.getLongExtra("id", -1L)
        carId = intent.getLongExtra("car_id", -1L)
        userId = intent.getIntExtra("user_id", -1)

        if (bookingId == -1L || carId == -1L) {
            Toast.makeText(this, "Некорректный ID бронирования/автомобиля", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupListeners()
        loadBookingDetails()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnCancelBooking.setOnClickListener {
            cancelBooking()
        }
    }

    private fun loadBookingDetails() {
        lifecycleScope.launch {
            try {
                // Получаем данные о бронировании с автомобилем
                val bookingWithCar = fetchBookingWithCar(bookingId)
                // Получаем дополнительные данные об автомобиле (например, адрес)
                val carDetailsPartial = fetchCarDetailsPartial(carId)

                if (bookingWithCar != null && carDetailsPartial != null) {
                    // Обновляем UI на основе полученных данных
                    val booking = bookingWithCar.booking
                    val car = bookingWithCar.car

                    binding.tvBookingId.text = "Бронирование #${booking.id}"
                    binding.tvCarDetails.text = "${car.brand} ${car.model}"

                    // В динамике обновляем информацию о стоимости, исходя из количества дней аренды
                    binding.tvRentalCostLabel.text = "Аренда автомобиля x${booking.bookDays} дня:"
                    binding.tvRentalCost.text = "₽${booking.carPrice * booking.bookDays}"

                    binding.tvInsuranceCostLabel.text = "Страховка x${booking.bookDays} дня:"
                    binding.tvInsuranceCost.text = "₽${booking.insurancePrice * booking.bookDays}"

                    binding.tvTotalCost.text = "₽${booking.totalPrice}"

                    // Выводим адрес (из CarDetailsPartial)
                    binding.tvCarLocation.text = "Адрес: ${carDetailsPartial.location_address}"

                    // Загрузка изображения автомобиля
                    Picasso.get()
                        .load(car.imageUri)
                        .placeholder(ru.eaosipov.imdrived.R.drawable.ic_car)
                        .into(binding.ivCarImage)

                    // Здесь можно добавить логику загрузки данных о водителе, если требуется
                    val  userData = fetchDriverById(userId)
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

    private suspend fun fetchBookingWithCar(bookingId: Long): BookingWithCar? = withContext(Dispatchers.IO) {
        val db = AppDatabase.getDatabase(applicationContext)
        db.bookingDao().getBookingById(bookingId)
    }

    private suspend fun fetchCarDetailsPartial(carId: Long): CarDetailsPartial? = withContext(Dispatchers.IO) {
        val db = AppDatabase.getDatabase(applicationContext)
        db.carDetailsDao().getCarDetailsByCarId(carId)
    }

    private suspend fun fetchDriverById(userId: Int): UserRegistrationData? = withContext(Dispatchers.IO) {
        val db = AppDatabase.getDatabase(applicationContext)
        db.userRegistrationDao().getUserById(userId)
    }

    // Метод отмены бронирования: обновляем поле isEnded на true
    private fun cancelBooking() {
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val db = AppDatabase.getDatabase(applicationContext)
                    db.bookingDao().updateBookingStatus(bookingId, true)
                }
                Toast.makeText(this@BookingDetailsActivity, "Бронирование отменено", Toast.LENGTH_SHORT).show()
                finish()  // Возврат на предыдущий экран или экран со списком бронирований
            } catch (e: Exception) {
                Toast.makeText(this@BookingDetailsActivity, "Ошибка отмены бронирования", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
