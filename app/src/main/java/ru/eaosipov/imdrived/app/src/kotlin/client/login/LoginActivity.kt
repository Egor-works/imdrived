package ru.eaosipov.imdrived.app.src.kotlin.client.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import ru.eaosipov.imdrived.app.src.kotlin.service.repository.AuthRepository
import ru.eaosipov.imdrived.app.src.kotlin.service.db.AppDatabase
import ru.eaosipov.imdrived.app.src.kotlin.service.repository.UserRepository
import ru.eaosipov.imdrived.app.src.kotlin.client.main.MainActivity
import ru.eaosipov.imdrived.app.src.kotlin.client.register.RegisterActivity
import ru.eaosipov.imdrived.databinding.ActivityLoginBinding

/**
 * LoginActivity - экран входа в аккаунт.
 * Отображает поля для ввода email и пароля, а также кнопки:
 * "Войти", "Войти через Google" и "Зарегистрироваться".
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var authRepository: AuthRepository
    private lateinit var userRepository: UserRepository
    private val viewModel by lazy { LoginViewModel(userRepository) }

    // Флаг, показывающий состояние видимости пароля
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Инициализация ViewBinding для доступа к элементам разметки
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация UserRepository через базу Room:
        val dao = AppDatabase.getDatabase(applicationContext).userRegistrationDao()
        userRepository = UserRepository(dao)

        // Инициализация репозитория авторизации с передачей контекста
        authRepository = AuthRepository(applicationContext)

        // Настройка пользовательского интерфейса
        setupUI()
        // Наблюдение за изменениями в ViewModel (если требуется)
        observeViewModel()
    }

    /**
     * Настройка пользовательского интерфейса:
     * - Добавление слушателей для полей ввода
     * - Обработка нажатий на кнопки
     */
    private fun setupUI() {
        // Слушатель изменений текста для полей ввода
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { updateLoginButtonState() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        binding.etEmail.addTextChangedListener(textWatcher)
        binding.etPassword.addTextChangedListener(textWatcher)

        // Обработка нажатия на иконку для показа/скрытия пароля
        binding.ivTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                // Показываем пароль
                binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                // Скрываем пароль
                binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            // Перемещаем курсор в конец строки
            binding.etPassword.setSelection(binding.etPassword.text.length)
        }

        // Обработка нажатия кнопки "Войти"
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            // Проверка корректности email
            if (!isValidEmail(email)) {
                Snackbar.make(binding.root, "Введите корректную электронную почту", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Отправляем запрос на авторизацию через ViewModel
            viewModel.login(email, password) { success, token, errorMsg ->
                if (success) {
                    // Сохраняем email в репозитории
                    authRepository.saveEmail(email)
                    // Переходим на главный экран
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    // Показываем ошибку авторизации
                    Snackbar.make(binding.root, errorMsg ?: "Ошибка авторизации", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        // Обработка кнопки "Войти через Google"
        binding.btnGoogleLogin.setOnClickListener {
            // Здесь должна быть интеграция с Google OAuth
            // Для примера - симулируем успешный вход
            viewModel.googleLogin { success, token, errorMsg ->
                if (success) {
                    // Сохраняем токен и переходим на главный экран
                    authRepository.saveToken(token ?: "")
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    // Показываем ошибку
                    Snackbar.make(binding.root, errorMsg ?: "Ошибка авторизации через Google", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        // Кнопка "Зарегистрироваться" - переход на экран регистрации
        binding.btnGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    /**
     * Обновляет состояние кнопки "Войти":
     * - Кнопка активна, если оба поля (email и пароль) заполнены
     */
    private fun updateLoginButtonState() {
        val emailFilled = binding.etEmail.text.isNotEmpty()
        val passwordFilled = binding.etPassword.text.isNotEmpty()
        binding.btnLogin.isEnabled = emailFilled && passwordFilled
    }

    /**
     * Проверяет корректность email по стандартному паттерну.
     * @param email - строка с email для проверки
     * @return true, если email корректен
     */
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Наблюдение за изменениями в ViewModel (если требуется).
     * В текущей реализации не используется.
     */
    private fun observeViewModel() {
        // Можно добавить наблюдение за LiveData из ViewModel
    }
}