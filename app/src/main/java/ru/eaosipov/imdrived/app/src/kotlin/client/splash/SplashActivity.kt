package ru.eaosipov.imdrived.app.src.kotlin.client.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ru.eaosipov.imdrived.app.src.kotlin.client.auth.UserAccessActivity
import ru.eaosipov.imdrived.app.src.kotlin.client.main.MainActivity
import ru.eaosipov.imdrived.app.src.kotlin.client.onboarding.OnboardingActivity
import ru.eaosipov.imdrived.databinding.ActivitySplashBinding

/**
 * SplashActivity - экран загрузки приложения.
 * Отображает логотип, название и слоган.
 * Через 2-3 секунды или после проверки токена переходит на нужный экран.
 */
class SplashActivity : AppCompatActivity() {

    // Инициализация ViewBinding для доступа к элементам макета
    private lateinit var binding: ActivitySplashBinding

    // Инициализация ViewModel для управления логикой экрана загрузки
    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Настройка ViewBinding для текущего макета
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Создание ViewModel с использованием фабрики, передавая контекст приложения
        viewModel = ViewModelProvider(
            this,
            SplashViewModelFactory(applicationContext)
        ).get(SplashViewModel::class.java)

        // Наблюдение за изменениями в LiveData для определения, куда перейти после загрузки
        viewModel.navigateTo.observe(this) { destination ->
            when (destination) {
                // Переход на экран выбора авторизации (вход/регистрация)
                Destination.AUTH_CHOICE -> startActivity(Intent(this, UserAccessActivity::class.java))
                // Переход на главный экран приложения
                Destination.MAIN -> startActivity(Intent(this, MainActivity::class.java))
                // Переход на экран онбординга (первое знакомство с приложением)
                Destination.ONBOARDING -> startActivity(Intent(this, OnboardingActivity::class.java))
            }
            // Завершение текущей активности после перехода
            finish()
        }
    }
}