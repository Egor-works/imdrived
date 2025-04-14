package ru.eaosipov.imdrived.app.src.kotlin.client.register

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.eaosipov.imdrived.app.src.kotlin.service.db.AppDatabase
import ru.eaosipov.imdrived.app.src.kotlin.service.entities.UserRegistrationData
import ru.eaosipov.imdrived.app.src.kotlin.service.repository.UserRepository

class RegistrationViewModel(context: Context) : ViewModel() {

    private val repository: UserRepository

    init {
        val dao = AppDatabase.getDatabase(context).userRegistrationDao()
        repository = UserRepository(dao)
    }

    fun saveRegistrationData(userData: UserRegistrationData, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                repository.insertUserData(userData)
                onComplete(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false)
            }
        }
    }
}
