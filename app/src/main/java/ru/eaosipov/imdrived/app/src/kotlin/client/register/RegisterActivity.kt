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

    private var isPasswordVisible = false // Флаг видимости пароля
    private var isRepeatPasswordVisible = false // Флаг видимости повторного пароля

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater) // Инициализация ViewBinding
        setContentView(binding.root) // Установка корневого View

        setupUI() // Настройка пользовательского интерфейса
    }

    /**
     * Настройка пользовательского интерфейса.
     * Добавляет обработчики кликов для кнопок и полей ввода.
     */
    private fun setupUI() {
        // Обработка показа/скрытия пароля
        binding.ivTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible // Инвертируем флаг видимости
            binding.etPassword.transformationMethod = if (isPasswordVisible) {
                HideReturnsTransformationMethod.getInstance() // Показываем пароль
            } else {
                PasswordTransformationMethod.getInstance() // Скрываем пароль
            }
            binding.etPassword.setSelection(binding.etPassword.text.length) // Устанавливаем курсор в конец текста
        }

        // Обработка показа/скрытия повторного пароля
        binding.ivToggleRepeatPassword.setOnClickListener {
            isRepeatPasswordVisible = !isRepeatPasswordVisible // Инвертируем флаг видимости
            binding.etRepeatPassword.transformationMethod = if (isRepeatPasswordVisible) {
                HideReturnsTransformationMethod.getInstance() // Показываем пароль
            } else {
                PasswordTransformationMethod.getInstance() // Скрываем пароль
            }
            binding.etRepeatPassword.setSelection(binding.etRepeatPassword.text.length) // Устанавливаем курсор в конец текста
        }

        // Обработка кнопки "Далее"
        binding.btnNext.setOnClickListener {
            if (!validateInput()) { // Проверяем валидность введённых данных
                return@setOnClickListener // Если данные невалидны, прерываем выполнение
            }
            // Если все поля валидны, переходим к следующему этапу регистрации.
            val intent = Intent(this, RegistrationStep2Activity::class.java).apply {
                putExtra("email", binding.etEmail.text.toString().trim()) // Передаём email
                putExtra("password", binding.etPassword.text.toString()) // Передаём пароль
            }
            startActivity(intent) // Запускаем следующий экран
        }

        // Обработка кнопки "Назад"
        binding.btnBack.setOnClickListener {
            finish() // Возврат на предыдущий экран
        }
    }

    /**
     * Проверяет корректность введённых данных.
     * @return true, если все поля валидны, иначе false.
     */
    private fun validateInput(): Boolean {
        val email = binding.etEmail.text.toString().trim() // Получаем email
        val password = binding.etPassword.text.toString() // Получаем пароль
        val repeatPassword = binding.etRepeatPassword.text.toString() // Получаем повторный пароль

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

        return true // Все проверки пройдены
    }
}