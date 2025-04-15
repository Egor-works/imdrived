package ru.eaosipov.imdrived.app.src.kotlin.client.register

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.eaosipov.imdrived.app.src.kotlin.service.db.AppDatabase
import ru.eaosipov.imdrived.app.src.kotlin.service.entities.UserRegistrationData
import ru.eaosipov.imdrived.app.src.kotlin.service.repository.UserRepository

/**
 * RegistrationViewModel - ViewModel для управления данными регистрации пользователя.
 * Обеспечивает взаимодействие с репозиторием для сохранения данных пользователя.
 */
class RegistrationViewModel(context: Context) : ViewModel() {

    // Репозиторий для работы с данными пользователя
    private val repository: UserRepository

    init {
        // Инициализация репозитория с доступом к DAO (Data Access Object) базы данных
        val dao = AppDatabase.getDatabase(context).userRegistrationDao()
        repository = UserRepository(dao)
    }

    /**
     * Сохраняет данные регистрации пользователя в базу данных.
     *
     * @param userData Данные пользователя для сохранения.
     * @param onComplete Колбэк, вызываемый после завершения операции.
     *                   Передаёт `true`, если сохранение прошло успешно, и `false` в случае ошибки.
     */
    fun saveRegistrationData(userData: UserRegistrationData, onComplete: (Boolean) -> Unit) {
        // Запуск корутины в рамках жизненного цикла ViewModel
        viewModelScope.launch {
            try {
                // Попытка вставки данных пользователя в базу данных
                repository.insertUserData(userData)
                // Успешное завершение операции
                onComplete(true)
            } catch (e: Exception) {
                // Обработка ошибки: логирование и вызов колбэка с `false`
                e.printStackTrace()
                onComplete(false)
            }
        }
    }
}