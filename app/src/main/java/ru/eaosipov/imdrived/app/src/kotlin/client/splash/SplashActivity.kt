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

    private lateinit var binding: ActivitySplashBinding
    private lateinit var viewModel: SplashViewModel// by viewModels() Используем ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Подключаем ViewBinding
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Создаем ViewModel, передавая Context
        viewModel = ViewModelProvider(this,  SplashViewModelFactory(applicationContext))
            .get(SplashViewModel::class.java)

        viewModel.navigateTo.observe(this) { destination ->
            when (destination) {
                // Вместо Login выбираем UserAccessActivity
                Destination.AUTH_CHOICE -> startActivity(Intent(this, UserAccessActivity::class.java))
                Destination.MAIN -> startActivity(Intent(this, MainActivity::class.java))
                Destination.ONBOARDING -> startActivity(Intent(this, OnboardingActivity::class.java))
            }
            finish()
        }
        // Наблюдаем за статусом навигации
        /* viewModel.navigateTo.observe(this, Observer { destination ->
            when (destination) {
                Destination.LOGIN -> startActivity(Intent(this, LoginActivity::class.java))
                Destination.MAIN -> startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        })*/
    }
}

