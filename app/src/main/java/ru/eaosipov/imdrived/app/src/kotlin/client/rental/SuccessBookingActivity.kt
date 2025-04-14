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

    private lateinit var binding: ActivitySuccessBookingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuccessBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // При нажатии на текст "Перейти к своим бронированиям"
        binding.tvGoToBookings.setOnClickListener {
            // Если экран бронирований уже реализован, переходите к нему:
            startActivity(Intent(this, MyBookingsActivity::class.java))
            finish()
            //Toast.makeText(this, "Переход в избранное!", Toast.LENGTH_SHORT).show()
        }

        // Кнопка "Далее" ведет на главный экран
        binding.btnHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
