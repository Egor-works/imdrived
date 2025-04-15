package ru.eaosipov.imdrived.app.src.kotlin.client.rental

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.eaosipov.imdrived.app.src.kotlin.client.bookings.MyBookingsActivity
import ru.eaosipov.imdrived.app.src.kotlin.client.main.MainActivity
import ru.eaosipov.imdrived.databinding.ActivitySuccessBookingBinding

/**
 * SuccessBookingActivity — экран, отображающий успешное бронирование автомобиля.
 * Предоставляет пользователю варианты: перейти к своим бронированиям или вернуться на главный экран.
 */
class SuccessBookingActivity : AppCompatActivity() {

    // Привязка к макету ActivitySuccessBookingBinding для работы с элементами интерфейса
    private lateinit var binding: ActivitySuccessBookingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Инициализация привязки к макету
        binding = ActivitySuccessBookingBinding.inflate(layoutInflater)
        // Установка корневого представления для активности
        setContentView(binding.root)

        // Настройка обработчиков кликов для элементов интерфейса
        setupClickListeners()
    }

    /**
     * Настраивает обработчики кликов для элементов интерфейса.
     */
    private fun setupClickListeners() {
        // Обработчик клика по тексту "Перейти к своим бронированиям"
        binding.tvGoToBookings.setOnClickListener {
            // Создание Intent для перехода на экран списка бронирований
            val intent = Intent(this, MyBookingsActivity::class.java)
            // Запуск активности
            startActivity(intent)
            // Завершение текущей активности
            finish()
        }

        // Обработчик клика по кнопке "Далее"
        binding.btnHome.setOnClickListener {
            // Создание Intent для перехода на главный экран
            val intent = Intent(this, MainActivity::class.java)
            // Запуск активности
            startActivity(intent)
            // Завершение текущей активности
            finish()
        }
    }
}