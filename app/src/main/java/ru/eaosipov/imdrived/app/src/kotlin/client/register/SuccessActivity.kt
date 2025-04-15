package ru.eaosipov.imdrived.app.src.kotlin.client.register

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.eaosipov.imdrived.app.src.kotlin.service.repository.AuthRepository
import ru.eaosipov.imdrived.app.src.kotlin.client.main.MainActivity
import ru.eaosipov.imdrived.databinding.ActivitySuccessBinding

/**
 * SuccessActivity — экран поздравления после успешной регистрации.
 * Отображает сообщение об успешной регистрации и предоставляет кнопку для перехода в главное приложение.
 */
class SuccessActivity : AppCompatActivity() {

    // Привязка к элементам интерфейса через View Binding
    private lateinit var binding: ActivitySuccessBinding

    // Репозиторий для работы с данными авторизации
    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация View Binding
        binding = ActivitySuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация репозитория авторизации с передачей контекста приложения
        authRepository = AuthRepository(applicationContext)

        // Обработка нажатия кнопки "Далее"
        binding.btnNext.setOnClickListener {
            // Получение email из Intent (или использование значения по умолчанию)
            val email = intent.extras?.getString("email") ?: "dima@mail.ru"

            // Сохранение email в репозитории для дальнейшего использования
            authRepository.saveEmail(email)

            // Создание Intent для перехода на главный экран приложения
            val intent = Intent(this, MainActivity::class.java)

            // Запуск MainActivity
            startActivity(intent)

            // Завершение текущей активности, чтобы пользователь не мог вернуться назад
            finish()
        }
    }
}