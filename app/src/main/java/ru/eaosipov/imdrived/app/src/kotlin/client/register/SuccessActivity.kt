package ru.eaosipov.imdrived.app.src.kotlin.client.register

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.eaosipov.imdrived.app.src.kotlin.service.repository.AuthRepository
import ru.eaosipov.imdrived.app.src.kotlin.client.main.MainActivity
import ru.eaosipov.imdrived.databinding.ActivitySuccessBinding

/**
 * SuccessActivity — экран поздравления после успешной регистрации.
 */
class SuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuccessBinding
    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация репозитория авторизации с передачей контекста
        authRepository = AuthRepository(applicationContext)

        // Обработка нажатия кнопки "Далее"
        binding.btnNext.setOnClickListener {
            val email = intent.extras?.getString("email") ?: "dima@mail.ru"
            authRepository.saveEmail(email)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
