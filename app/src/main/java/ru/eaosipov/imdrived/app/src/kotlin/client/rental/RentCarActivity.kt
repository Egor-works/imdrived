package ru.eaosipov.imdrived.app.src.kotlin.client.rental

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.SharedPreferences
import java.util.Calendar
import android.app.DatePickerDialog
import ru.eaosipov.imdrived.R
import ru.eaosipov.imdrived.app.src.kotlin.service.db.AppDatabase
import ru.eaosipov.imdrived.app.src.kotlin.service.entities.Booking
import ru.eaosipov.imdrived.app.src.kotlin.service.entities.Car
import ru.eaosipov.imdrived.app.src.kotlin.service.entities.CarDetailsPartial
import ru.eaosipov.imdrived.app.src.kotlin.service.repository.UserRepository
import ru.eaosipov.imdrived.databinding.ActivityRentCarBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

class RentCarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRentCarBinding
    private var carId: Long = -1
    private val daysCount = 3
    private val insurancePerDay = 300
    private val deposit = 15000

    // Вспомогательные данные для бронирования
    private var currentUserId: Int = -1  // ID пользователя
    private var currentUserName: String? = null  // Имя пользователя
    private var currentUserEmail: String? = null  // Email пользователя

    private var selectedStartDate: Long? = 1743984000000L // 7 апреля 2025
    private var selectedEndDate: Long? = 1744243200000L   // 10 апреля 2025

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRentCarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        carId = intent.getLongExtra("car_id", -1L)

        if (carId == -1L) {
            Toast.makeText(this, "Некорректный ID автомобиля", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Загружаем данные текущего пользователя из SharedPreferences
        loadUserData()

        setupListeners()
        loadCarAndDetails()
    }

    private fun updateCostSection() {
        if (selectedStartDate != null && selectedEndDate != null) {
            val start = selectedStartDate!!
            val end = selectedEndDate!!

            if (end <= start) {
                Toast.makeText(this, "Дата окончания должна быть позже даты начала", Toast.LENGTH_SHORT).show()
                return
            }

            val days = ((end - start) / (1000 * 60 * 60 * 24)).toInt()

            lifecycleScope.launch {
                val car = fetchCarById(carId)
                val rentalPricePerDay = car?.pricePerHour ?: 0.0

                val rentalCost = rentalPricePerDay * days
                val insuranceCost = insurancePerDay * days
                val totalCost = rentalCost + insuranceCost

                // Обновляем поля
                binding.tvRentalCostLabel.text = "Аренда автомобиля x$days дня:"
                binding.tvInsuranceLabel.text = "Страховка x$days дня:"
                binding.tvRentalCost.text = "₽$rentalCost"
                binding.tvInsuranceCost.text = "₽$insuranceCost"
                binding.tvTotalCost.text = "₽$totalCost"
            }
        }
    }

    private fun loadUserData() {
        val prefs: SharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        currentUserEmail = prefs.getString("user_email", null)

        if (currentUserEmail != null) {
            lifecycleScope.launch {
                val dao = AppDatabase.getDatabase(applicationContext).userRegistrationDao()
                val userRepository = UserRepository(dao)
                val userData = withContext(Dispatchers.IO) {
                    userRepository.getUserByEmail(currentUserEmail!!)
                }
                if (userData != null) {
                    // Сохраняем данные о пользователе
                    currentUserId = userData.id
                    currentUserName = "${userData.firstName} ${userData.lastName}"
                } else {
                    Toast.makeText(this@RentCarActivity, "Пользователь не найден", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this@RentCarActivity, "Email пользователя не найден", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnContinue.setOnClickListener {
            //Toast.makeText(this, "Бронирование оформлено!", Toast.LENGTH_SHORT).show()
            saveBooking()
            val intent = Intent(this, SuccessBookingActivity::class.java)
            startActivity(intent)
        }

        binding.llStartDate.setOnClickListener {
            showDatePicker { date ->
                selectedStartDate = date
                binding.tvStartDate.text = formatDate(date)
                updateCostSection()
            }
        }

        binding.llEndDate.setOnClickListener {
            showDatePicker { date ->
                selectedEndDate = date
                binding.tvEndDate.text = formatDate(date)
                updateCostSection()
            }
        }
    }

    private fun loadCarAndDetails() {
        showLoading(true)

        lifecycleScope.launch {
            try {
                val car = fetchCarById(carId)
                val carDetails = fetchCarDetailsById(carId)

                if (car != null && carDetails != null) {
                    fillUI(car, carDetails)
                } else {
                    Toast.makeText(this@RentCarActivity, "Данные автомобиля не найдены", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RentCarActivity, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
            } finally {
                showLoading(false)
            }
        }
    }

    private suspend fun fetchCarById(id: Long): Car? = withContext(Dispatchers.IO) {
        AppDatabase.getDatabase(applicationContext).carDao().getCarById(id)
    }

    private suspend fun fetchCarDetailsById(id: Long): CarDetailsPartial? = withContext(Dispatchers.IO) {
        AppDatabase.getDatabase(applicationContext).carDetailsDao().getCarDetailsByCarId(id)
    }

    private fun fillUI(car: Car, details: CarDetailsPartial) {
        val rentalPrice = car?.pricePerHour?.times(daysCount) ?: 0.0
        val insurancePrice = insurancePerDay * daysCount
        val total = rentalPrice + insurancePrice

        // Картинка и название
        Picasso.get()
            .load(car.imageUri)
            .placeholder(R.drawable.ic_car)
            .into(binding.ivCarImage)

        binding.tvCarModel.text = car.model
        binding.tvCarBrand.text = car.brand
        binding.tvRentalPricePerDay.text = "₽${car.pricePerHour}/день"

        // Даты аренды — временно захардкожены
        binding.tvStartDate.text = "10:00 08 апр 2025"
        binding.tvEndDate.text = "10:00 11 апр 2025"

        // Адрес
        binding.tvCarLocationText.text = details.location_address

        // Стоимость
        binding.tvRentalCost.text = "₽$rentalPrice"
        binding.tvInsuranceCost.text = "₽$insurancePrice"
        binding.tvTotalCost.text = "₽$total"
        binding.tvDepositCost.text = "₽$deposit"
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        //binding.scrollView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun saveBooking() {
        // Получаем текущие даты (для примера: начало и конец аренды)
        val startDate = selectedStartDate ?: System.currentTimeMillis()
        val endDate = selectedEndDate ?: (startDate + (daysCount * 24 * 60 * 60 * 1000))

        lifecycleScope.launch {
            try {
                val actualDays = ((endDate - startDate) / (1000 * 60 * 60 * 24)).toInt()
                val carPrice = fetchCarById(carId)?.pricePerHour ?: 0.0
                // Создаём объект бронирования
                val booking = Booking(
                    carId = carId,
                    userId = currentUserId,
                    startDate = startDate,
                    endDate = endDate,
                    totalPrice = (insurancePerDay * actualDays) + (actualDays * carPrice),
                    carPrice = actualDays * carPrice,
                    insurancePrice = insurancePerDay * actualDays,
                    bookDays = actualDays,
                    deposit = deposit
                )

                // Вставляем бронирование в базу данных
                AppDatabase.getDatabase(applicationContext).bookingDao().insertBooking(booking)

                // После успешного бронирования переходим на экран с подтверждением
                val intent = Intent(this@RentCarActivity, SuccessBookingActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this@RentCarActivity, "Ошибка сохранения бронирования", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePicker(onDateSelected: (Long) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedCal = Calendar.getInstance()
                selectedCal.set(year, month, dayOfMonth, 10, 0)
                onDateSelected(selectedCal.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun formatDate(timeMillis: Long): String {
        val sdf = SimpleDateFormat("HH:mm dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timeMillis))
    }
}
