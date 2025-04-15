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

/**
 * RentCarActivity — экран для оформления аренды автомобиля.
 * Позволяет выбрать даты аренды, рассчитать стоимость и подтвердить бронирование.
 */
class RentCarActivity : AppCompatActivity() {

    // Привязка к макету активности
    private lateinit var binding: ActivityRentCarBinding

    // ID автомобиля, переданный из предыдущего экрана
    private var carId: Long = -1

    // Константы для расчёта стоимости
    private val daysCount = 3 // Количество дней аренды по умолчанию
    private val insurancePerDay = 300 // Стоимость страховки за день
    private val deposit = 15000 // Залог за автомобиль

    // Данные текущего пользователя
    private var currentUserId: Int = -1 // ID пользователя
    private var currentUserName: String? = null // Имя пользователя
    private var currentUserEmail: String? = null // Email пользователя

    // Выбранные даты аренды (начало и конец)
    private var selectedStartDate: Long? = 1743984000000L // 7 апреля 2025 (пример)
    private var selectedEndDate: Long? = 1744243200000L   // 10 апреля 2025 (пример)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Инициализация привязки к макету
        binding = ActivityRentCarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получение ID автомобиля из Intent
        carId = intent.getLongExtra("car_id", -1L)

        // Проверка корректности ID автомобиля
        if (carId == -1L) {
            Toast.makeText(this, "Некорректный ID автомобиля", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Загрузка данных пользователя из SharedPreferences
        loadUserData()

        // Настройка слушателей событий
        setupListeners()

        // Загрузка данных об автомобиле и его деталях
        loadCarAndDetails()
    }

    /**
     * Обновляет раздел с расчётом стоимости аренды.
     * Вызывается при изменении дат аренды.
     */
    private fun updateCostSection() {
        if (selectedStartDate != null && selectedEndDate != null) {
            val start = selectedStartDate!!
            val end = selectedEndDate!!

            // Проверка, что дата окончания позже даты начала
            if (end <= start) {
                Toast.makeText(this, "Дата окончания должна быть позже даты начала", Toast.LENGTH_SHORT).show()
                return
            }

            // Расчёт количества дней аренды
            val days = ((end - start) / (1000 * 60 * 60 * 24)).toInt()

            lifecycleScope.launch {
                // Получение данных об автомобиле
                val car = fetchCarById(carId)
                val rentalPricePerDay = car?.pricePerHour ?: 0.0

                // Расчёт стоимости аренды, страховки и итоговой суммы
                val rentalCost = rentalPricePerDay * days
                val insuranceCost = insurancePerDay * days
                val totalCost = rentalCost + insuranceCost

                // Обновление UI
                binding.tvRentalCostLabel.text = "Аренда автомобиля x$days дня:"
                binding.tvInsuranceLabel.text = "Страховка x$days дня:"
                binding.tvRentalCost.text = "₽$rentalCost"
                binding.tvInsuranceCost.text = "₽$insuranceCost"
                binding.tvTotalCost.text = "₽$totalCost"
            }
        }
    }

    /**
     * Загружает данные текущего пользователя из SharedPreferences.
     */
    private fun loadUserData() {
        val prefs: SharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        currentUserEmail = prefs.getString("user_email", null)

        if (currentUserEmail != null) {
            lifecycleScope.launch {
                // Получение данных пользователя из базы данных
                val dao = AppDatabase.getDatabase(applicationContext).userRegistrationDao()
                val userRepository = UserRepository(dao)
                val userData = withContext(Dispatchers.IO) {
                    userRepository.getUserByEmail(currentUserEmail!!)
                }
                if (userData != null) {
                    // Сохранение данных пользователя
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

    /**
     * Настраивает слушатели событий для кнопок и других элементов UI.
     */
    private fun setupListeners() {
        // Обработка нажатия кнопки "Назад"
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Обработка нажатия кнопки "Продолжить"
        binding.btnContinue.setOnClickListener {
            saveBooking()
            val intent = Intent(this, SuccessBookingActivity::class.java)
            startActivity(intent)
        }

        // Обработка выбора даты начала аренды
        binding.llStartDate.setOnClickListener {
            showDatePicker { date ->
                selectedStartDate = date
                binding.tvStartDate.text = formatDate(date)
                updateCostSection()
            }
        }

        // Обработка выбора даты окончания аренды
        binding.llEndDate.setOnClickListener {
            showDatePicker { date ->
                selectedEndDate = date
                binding.tvEndDate.text = formatDate(date)
                updateCostSection()
            }
        }
    }

    /**
     * Загружает данные об автомобиле и его деталях.
     */
    private fun loadCarAndDetails() {
        showLoading(true)

        lifecycleScope.launch {
            try {
                // Получение данных об автомобиле и его деталях
                val car = fetchCarById(carId)
                val carDetails = fetchCarDetailsById(carId)

                if (car != null && carDetails != null) {
                    // Заполнение UI данными
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

    /**
     * Получает данные об автомобиле по его ID.
     */
    private suspend fun fetchCarById(id: Long): Car? = withContext(Dispatchers.IO) {
        AppDatabase.getDatabase(applicationContext).carDao().getCarById(id)
    }

    /**
     * Получает детали автомобиля по его ID.
     */
    private suspend fun fetchCarDetailsById(id: Long): CarDetailsPartial? = withContext(Dispatchers.IO) {
        AppDatabase.getDatabase(applicationContext).carDetailsDao().getCarDetailsByCarId(id)
    }

    /**
     * Заполняет UI данными об автомобиле.
     */
    private fun fillUI(car: Car, details: CarDetailsPartial) {
        // Расчёт стоимости аренды, страховки и итоговой суммы
        val rentalPrice = car.pricePerHour?.times(daysCount) ?: 0.0
        val insurancePrice = insurancePerDay * daysCount
        val total = rentalPrice + insurancePrice

        // Загрузка изображения автомобиля
        Picasso.get()
            .load(car.imageUri)
            .placeholder(R.drawable.ic_car)
            .into(binding.ivCarImage)

        // Установка названия и бренда автомобиля
        binding.tvCarModel.text = car.model
        binding.tvCarBrand.text = car.brand
        binding.tvRentalPricePerDay.text = "₽${car.pricePerHour}/день"

        // Установка дат аренды (пример)
        binding.tvStartDate.text = "10:00 08 апр 2025"
        binding.tvEndDate.text = "10:00 11 апр 2025"

        // Установка адреса
        binding.tvCarLocationText.text = details.location_address

        // Установка стоимости
        binding.tvRentalCost.text = "₽$rentalPrice"
        binding.tvInsuranceCost.text = "₽$insurancePrice"
        binding.tvTotalCost.text = "₽$total"
        binding.tvDepositCost.text = "₽$deposit"
    }

    /**
     * Показывает или скрывает индикатор загрузки.
     */
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    /**
     * Сохраняет данные бронирования в базу данных.
     */
    private fun saveBooking() {
        // Получение выбранных дат аренды
        val startDate = selectedStartDate ?: System.currentTimeMillis()
        val endDate = selectedEndDate ?: (startDate + (daysCount * 24 * 60 * 60 * 1000))

        lifecycleScope.launch {
            try {
                // Расчёт количества дней аренды
                val actualDays = ((endDate - startDate) / (1000 * 60 * 60 * 24)).toInt()
                val carPrice = fetchCarById(carId)?.pricePerHour ?: 0.0

                // Создание объекта бронирования
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

                // Сохранение бронирования в базу данных
                AppDatabase.getDatabase(applicationContext).bookingDao().insertBooking(booking)

                // Переход на экран успешного бронирования
                val intent = Intent(this@RentCarActivity, SuccessBookingActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this@RentCarActivity, "Ошибка сохранения бронирования", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Показывает диалог выбора даты.
     */
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

    /**
     * Форматирует дату в строку.
     */
    private fun formatDate(timeMillis: Long): String {
        val sdf = SimpleDateFormat("HH:mm dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timeMillis))
    }
}