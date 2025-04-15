// Пакет, в котором находится данный класс  
package ru.eaosipov.imdrived.app.src.kotlin.client.auth

// Импорт необходимых классов и библиотек  
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.eaosipov.imdrived.app.src.kotlin.client.login.LoginActivity
import ru.eaosipov.imdrived.app.src.kotlin.client.register.RegisterActivity
import ru.eaosipov.imdrived.databinding.ActivityAuthChoiceBinding

/**
 * UserAccessActivity — экран, позволяющий пользователю выбрать действие:
 * авторизоваться или зарегистрироваться в приложении.
 * Отображаются название и логотип, а также две кнопки:
 * «Войти» и «Зарегистрироваться». Нажатие на любую из них
 * открывает соответствующий экран.
 */
class UserAccessActivity : AppCompatActivity() {

    // Переменная для привязки представления (View Binding)  
    private lateinit var viewBinding: ActivityAuthChoiceBinding

    /**
     * Метод onCreate вызывается при создании активности.
     * Здесь инициализируются все компоненты интерфейса и логика.
     *
     * @param savedInstanceState Сохранённое состояние активности (если есть).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация View Binding для данного экрана  
        viewBinding = ActivityAuthChoiceBinding.inflate(layoutInflater)

        // Установка корневого представления активности  
        setContentView(viewBinding.root)

        // Обработчик нажатия на кнопку "Войти"  
        viewBinding.btnLogin.setOnClickListener {
            // Создание Intent для перехода к экрану авторизации (LoginActivity)  
            val loginIntent = Intent(this, LoginActivity::class.java)

            // Запуск активности авторизации  
            startActivity(loginIntent)
        }

        // Обработчик нажатия на кнопку "Зарегистрироваться"  
        viewBinding.btnRegister.setOnClickListener {
            // Создание Intent для перехода к экрану регистрации (RegisterActivity)  
            val registerIntent = Intent(this, RegisterActivity::class.java)

            // Запуск активности регистрации  
            startActivity(registerIntent)
        }
    }
}