package ru.eaosipov.imdrived.app.src.kotlin.client.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.content.Context
import ru.eaosipov.imdrived.app.src.kotlin.service.repository.AuthRepository

/**
 * SplashViewModel - ViewModel для экрана загрузки (SplashScreen).
 * Отвечает за логику проверки авторизации пользователя и определения следующего экрана.
 */
class SplashViewModel(context: Context) : ViewModel() {

    // MutableLiveData для хранения направления навигации
    private val _navigateTo = MutableLiveData<Destination>()
    // LiveData для наблюдения за направлением навигации (только для чтения)
    val navigateTo: LiveData<Destination> get() = _navigateTo

    // Репозиторий для работы с авторизацией
    private val authRepository = AuthRepository(context)
    // SharedPreferences для проверки состояния онбординга
    private val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    // Блок инициализации ViewModel
    init {
        viewModelScope.launch {
            // Искусственная задержка для отображения SplashScreen (2 секунды)
            delay(2000)

            // Проверяем, авторизован ли пользователь
            if (authRepository.isUserLoggedIn()) {
                // Если авторизован, переходим на главный экран
                _navigateTo.postValue(Destination.MAIN)
            } else {
                // Если не авторизован, проверяем, пройден ли онбординг
                val onboardingCompleted = sharedPreferences.getBoolean("onboarding_complete", false)
                if (!onboardingCompleted) {
                    // Если онбординг не пройден, переходим на экран онбординга
                    _navigateTo.postValue(Destination.ONBOARDING)
                } else {
                    // Если онбординг пройден, переходим на экран выбора входа/регистрации
                    _navigateTo.postValue(Destination.AUTH_CHOICE)
                }
            }
        }
    }
}

/**
 * Перечисление возможных направлений навигации после SplashScreen.
 */
enum class Destination {
    AUTH_CHOICE, // Экран выбора входа или регистрации
    MAIN,        // Главный экран приложения
    ONBOARDING   // Экран онбординга (первый запуск приложения)
}