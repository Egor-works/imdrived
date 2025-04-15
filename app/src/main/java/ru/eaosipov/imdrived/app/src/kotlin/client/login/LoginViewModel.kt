package ru.eaosipov.imdrived.app.src.kotlin.client.login

import kotlinx.coroutines.*
import ru.eaosipov.imdrived.app.src.kotlin.service.repository.UserRepository

/**
 * LoginViewModel - ViewModel для экрана авторизации.
 * Отвечает за логику проверки учетных данных и взаимодействие с репозиторием пользователей.
 */
class LoginViewModel(private val userRepository: UserRepository) {

    // Область видимости для корутин, используемых в ViewModel
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    /**
     * Выполняет вход пользователя, проверяя email и пароль.
     * @param email электронная почта пользователя
     * @param password пароль пользователя
     * @param callback функция обратного вызова с результатом авторизации:
     *                 - success: Boolean - успешность операции
     *                 - token: String? - токен авторизации (если успешно)
     *                 - errorMsg: String? - сообщение об ошибке (если неуспешно)
     */
    fun login(email: String, password: String, callback: (Boolean, String?, String?) -> Unit) {
        viewModelScope.launch {
            // Получаем пользователя из базы данных по email
            val user = userRepository.getUserByEmail(email)
            if (user == null) {
                // Пользователь не найден
                callback(false, null, "Пользователь с таким email не найден")
                return@launch
            }
            // Проверяем совпадение пароля
            if (user.password != password) {
                // Пароль неверный
                callback(false, null, "Неверный пароль")
            } else {
                // Авторизация успешна - возвращаем токен (в реальном приложении токен генерируется сервером)
                callback(true, "fake_token_123456", null)
            }
        }
    }

    /**
     * Симулирует вход через Google OAuth.
     * @param callback функция обратного вызова с результатом:
     *                 - success: Boolean - успешность операции
     *                 - token: String? - токен авторизации (если успешно)
     *                 - errorMsg: String? - сообщение об ошибке (если неуспешно)
     */
    fun googleLogin(callback: (Boolean, String?, String?) -> Unit) {
        viewModelScope.launch {
            // Симуляция задержки запроса к серверу Google
            delay(1500)
            // Симуляция успешного входа - возвращаем фейковый токен
            callback(true, "google_fake_token_abcdef", null)
        }
    }

    /**
     * Вызывается при уничтожении ViewModel для отмены всех корутин.
     */
    fun onCleared() {
        viewModelScope.cancel()
    }
}