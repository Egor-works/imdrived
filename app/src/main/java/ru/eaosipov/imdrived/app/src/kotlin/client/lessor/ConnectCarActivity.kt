package ru.eaosipov.imdrived.app.src.kotlin.client.lessor

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.appcompat.app.AppCompatActivity
import ru.eaosipov.imdrived.app.src.kotlin.client.register.RegistrationStep2Activity
import ru.eaosipov.imdrived.databinding.ActivityConnectCarBinding

class ConnectCarActivity : AppCompatActivity() {
    // Привязка для доступа к элементам интерфейса через View Binding
    private lateinit var binding: ActivityConnectCarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Инициализация View Binding и установка корневого представления
        binding = ActivityConnectCarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Настройка пользовательского интерфейса
        setupUI()
    }

    /**
     * Настройка элементов пользовательского интерфейса.
     * Здесь обрабатываются клики по кнопкам и другие взаимодействия.
     */
    private fun setupUI() {
        // Обработка нажатия кнопки "Далее"
        binding.btnNext.setOnClickListener {
            // Создание Intent для перехода на следующий экран (AddCarStep1Activity)
            val intent = Intent(this, AddCarStep1Activity::class.java)
            startActivity(intent)
        }

        // Обработка нажатия кнопки "Назад"
        binding.btnBack.setOnClickListener {
            // Завершение текущей активности и возврат на предыдущий экран
            finish()
        }
    }
}