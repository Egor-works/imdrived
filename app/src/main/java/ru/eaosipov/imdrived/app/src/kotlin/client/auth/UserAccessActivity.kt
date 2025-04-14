package ru.eaosipov.imdrived.app.src.kotlin.client.auth

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

    private lateinit var viewBinding: ActivityAuthChoiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAuthChoiceBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // Нажатие на кнопку входа перенаправляет к окну авторизации
        viewBinding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Нажатие на кнопку регистрации перенаправляет к окну создания учётной записи
        viewBinding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
