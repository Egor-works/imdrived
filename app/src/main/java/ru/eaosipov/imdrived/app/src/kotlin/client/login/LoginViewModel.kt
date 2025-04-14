package ru.eaosipov.imdrived.app.src.kotlin.client.login

import kotlinx.coroutines.*
import ru.eaosipov.imdrived.app.src.kotlin.service.repository.UserRepository

/**
 * LoginViewModel - отвечает за логику авторизации.
 * Здесь реализована симуляция запроса на сервер для авторизации.
 */
class LoginViewModel(private val userRepository: UserRepository) {

    // Можно использовать CoroutineScope для асинхронных операций
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    /**
     * Симулирует процесс входа.
     * @param email электронная почта
     * @param password пароль
     * @param callback результат авторизации: success, токен и сообщение об ошибке
     */
    /*fun login(email: String, password: String, callback: (Boolean, String?, String?) -> Unit) {
        viewModelScope.launch {
            // Симуляция задержки запроса к серверу
            delay(1500)
            // Простейшая логика: если пароль равен "123456", считаем авторизацию успешной
            if (password == "123456") {
                callback(true, "fake_token_123456", null)
            } else {
                callback(false, null, "Неверные учетные данные")
            }
        }
    }*/
    fun login(email: String, password: String, callback: (Boolean, String?, String?) -> Unit) {
        viewModelScope.launch {
            // Запрашиваем пользователя по email из базы данных
            val user = userRepository.getUserByEmail(email)
            if (user == null) {
                // Пользователь не найден
                callback(false, null, "Пользователь с таким email не найден")
                return@launch
            }
            // Если пользователь найден, сравниваем пароли
            if (user.password != password) {
                // Пароли не совпадают
                callback(false, null, "Неверный пароль")
            } else {
                // Авторизация успешна – возвращаем, например, токен
                callback(true, "fake_token_123456", null)
            }
        }
    }

    /**
     * Симулирует вход через Google.
     */
    fun googleLogin(callback: (Boolean, String?, String?) -> Unit) {
        viewModelScope.launch {
            // Симуляция задержки запроса
            delay(1500)
            // Симуляция успешного входа через Google
            callback(true, "google_fake_token_abcdef", null)
        }
    }

    fun onCleared() {
        viewModelScope.cancel()
    }
}
