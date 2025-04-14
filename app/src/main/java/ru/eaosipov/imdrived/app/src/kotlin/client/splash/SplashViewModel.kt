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
 * SplashViewModel - отвечает за логику загрузки и навигацию.
 */
class SplashViewModel(context: Context) : ViewModel() {

    private val _navigateTo = MutableLiveData<Destination>()
    val navigateTo: LiveData<Destination> get() = _navigateTo

    private val authRepository = AuthRepository(context)
    // Получаем SharedPreferences для проверки, пройден ли онбординг
    private val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    init {
        viewModelScope.launch {
            delay(2000) // Ждем 2 секунды
            //_navigateTo.postValue(Destination.AUTH_CHOICE)
            if (authRepository.isUserLoggedIn()) {
                _navigateTo.postValue(Destination.MAIN) // Если пользователь авторизован — главный экран
            } else {
                // Проверяем, прошёл ли пользователь онбординг
                val onboardingCompleted = sharedPreferences.getBoolean("onboarding_complete", false)
                if (!onboardingCompleted) {
                    _navigateTo.postValue(Destination.ONBOARDING) // Переход на онбординг
                } else {
                    _navigateTo.postValue(Destination.AUTH_CHOICE) // Переход на экран входа/регистрации
                }
            }
            /*if (authRepository.isUserLoggedIn()) {
                _navigateTo.postValue(Destination.MAIN) // Переход на главный экран
            } else {
                _navigateTo.postValue(Destination.AUTH_CHOICE) // Переход на экран входа
            }*/
        }
    }
}

/**
 * Перечисление возможных направлений перехода после экрана загрузки.
 */
enum class Destination {
    AUTH_CHOICE, // экран выбора входа/регистрации
    MAIN,
    ONBOARDING
}