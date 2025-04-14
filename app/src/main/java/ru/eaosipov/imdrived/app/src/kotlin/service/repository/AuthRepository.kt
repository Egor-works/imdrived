package ru.eaosipov.imdrived.app.src.kotlin.service.repository

import android.content.Context
import android.content.SharedPreferences

/**
 * AuthRepository - управляет хранением токена и проверкой аутентификации.
 */
class AuthRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun isUserLoggedIn(): Boolean {
        val token = prefs.getString("access_token", null)
        return token != null
    }

    fun saveToken(token: String) {
        prefs.edit().putString("access_token", token).apply()
    }

    // Дополнительные методы для работы с данными пользователя, если потребуется
    fun saveEmail(email: String) {
        // Пример сохранения дополнительных данных пользователя
        prefs.edit().putString("user_email", email).apply()
    }

    fun clearEmail() {
        prefs.edit().remove("user_email").apply()
    }

    fun clearToken() {
        prefs.edit().remove("access_token").apply()
    }
}
