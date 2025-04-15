package ru.eaosipov.imdrived.app.src.kotlin.client.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * SplashViewModelFactory - фабрика для создания экземпляра SplashViewModel.
 * Позволяет передавать контекст приложения в ViewModel.
 *
 * Наследуется от ViewModelProvider.NewInstanceFactory() для корректного создания ViewModel.
 */
class SplashViewModelFactory(private val context: Context) : ViewModelProvider.NewInstanceFactory() {

    /**
     * Создает экземпляр ViewModel указанного класса.
     *
     * @param modelClass класс ViewModel, который нужно создать
     * @return созданный экземпляр ViewModel
     * @throws IllegalArgumentException если переданный класс не является SplashViewModel
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Проверяем, что запрашиваемый класс - это SplashViewModel
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            // Создаем экземпляр SplashViewModel, передавая контекст
            return SplashViewModel(context) as T
        }
        // Если запрошенный класс не SplashViewModel, выбрасываем исключение
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}