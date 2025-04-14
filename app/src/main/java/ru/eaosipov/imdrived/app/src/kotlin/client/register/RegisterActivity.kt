package ru.eaosipov.imdrived.app.src.kotlin.client.register

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import ru.eaosipov.imdrived.databinding.ActivityRegisterBinding

/**
 * RegisterActivity - экран для создания нового аккаунта.
 * Реализует проверку корректности email, совпадение паролей и обязательность согласия с условиями.
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private var isPasswordVisible = false
    private var isRepeatPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        // Обработка показа/скрытия пароля
        binding.ivTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            binding.etPassword.transformationMethod = if (isPasswordVisible) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }
            binding.etPassword.setSelection(binding.etPassword.text.length)
        }

        binding.ivToggleRepeatPassword.setOnClickListener {
            isRepeatPasswordVisible = !isRepeatPasswordVisible
            binding.etRepeatPassword.transformationMethod = if (isRepeatPasswordVisible) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }
            binding.etRepeatPassword.setSelection(binding.etRepeatPassword.text.length)
        }

        // Обработка кнопки "Далее"
        binding.btnNext.setOnClickListener {
            if (!validateInput()) {
                return@setOnClickListener
            }
            // Если все поля валидны, переходим к следующему этапу регистрации.
            // Создаём Intent для запуска экрана RegistrationStep2Activity.
            val intent = Intent(this, RegistrationStep2Activity::class.java).apply {
                putExtra("email", binding.etEmail.text.toString().trim())
                putExtra("password", binding.etPassword.text.toString())
            }
            startActivity(intent)
            // При необходимости можно завершить текущую активность:
            // finish()
        }

        // Обработка кнопки "Назад"
        binding.btnBack.setOnClickListener {
            finish() // Возврат на предыдущий экран
        }
    }

    // Функция валидации данных
    private fun validateInput(): Boolean {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val repeatPassword = binding.etRepeatPassword.text.toString()

        // Проверка email по паттерну "name@domainname.ru"
        val emailPattern = Regex("^[\\w.-]+@[\\w.-]+\\.ru\$")
        if (!emailPattern.matches(email)) {
            Snackbar.make(binding.root, "Введите корректный адрес электронной почты.", Snackbar.LENGTH_SHORT).show()
            return false
        }

        // Проверка наличия пароля
        if (password.isEmpty()) {
            Snackbar.make(binding.root, "Введите пароль.", Snackbar.LENGTH_SHORT).show()
            return false
        }

        // Проверка совпадения паролей
        if (repeatPassword.isEmpty() || password != repeatPassword) {
            Snackbar.make(binding.root, "Пароли не совпадают.", Snackbar.LENGTH_SHORT).show()
            return false
        }

        // Проверка, что чекбокс отмечен
        if (!binding.cbAgreement.isChecked) {
            Snackbar.make(binding.root, "Необходимо согласиться с условиями обслуживания и политикой конфиденциальности.", Snackbar.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}