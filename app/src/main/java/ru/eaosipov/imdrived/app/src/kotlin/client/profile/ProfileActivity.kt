package ru.eaosipov.imdrived.app.src.kotlin.client.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.eaosipov.imdrived.R
import ru.eaosipov.imdrived.app.src.kotlin.service.repository.AuthRepository
import ru.eaosipov.imdrived.app.src.kotlin.service.db.AppDatabase
import ru.eaosipov.imdrived.app.src.kotlin.service.repository.UserRepository
import ru.eaosipov.imdrived.app.src.kotlin.client.login.LoginActivity
import ru.eaosipov.imdrived.app.src.kotlin.client.main.MainActivity
import ru.eaosipov.imdrived.app.src.kotlin.client.settings.SettingsActivity
import ru.eaosipov.imdrived.databinding.ActivityProfileBinding

/**
 * ProfileActivity - экран профиля пользователя.
 * Позволяет просматривать и редактировать данные профиля, включая аватар.
 */
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var authRepository: AuthRepository
    private lateinit var ivAvatar: ImageView

    // Флаг, указывающий, было ли загружено новое фото
    private var isNewPhotoUploaded = false
    // URI нового фото профиля
    private var newPhotoUri: Uri? = null
    // Лончер для выбора изображения из галереи
    private lateinit var profileImagePickerLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Инициализация ViewBinding
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получаем ссылку на ImageView для аватара
        ivAvatar = findViewById(R.id.ivAvatar)

        // Регистрируем лончер для выбора изображения
        profileImagePickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                // Сохраняем разрешение на чтение выбранного файла
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                contentResolver.takePersistableUriPermission(it, takeFlags)
                newPhotoUri = it
                isNewPhotoUploaded = true
                binding.ivAvatar.setImageURI(it)
                Snackbar.make(binding.root, "Фото профиля загружено.", Snackbar.LENGTH_SHORT).show()
            } ?: Snackbar.make(binding.root, "Фотография не выбрана.", Snackbar.LENGTH_SHORT).show()
        }

        // Инициализация репозитория авторизации
        authRepository = AuthRepository(applicationContext)

        // Загружаем данные пользователя
        loadUserData()

        // Настраиваем обработчики кликов
        setupClickListeners()
    }

    /**
     * Загружает данные пользователя из базы данных и обновляет UI.
     */
    private fun loadUserData() {
        // Получаем email текущего пользователя из SharedPreferences
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val userEmail = prefs.getString("user_email", null)

        if (userEmail != null) {
            lifecycleScope.launch {
                val dao = AppDatabase.getDatabase(applicationContext).userRegistrationDao()
                val userRepository = UserRepository(dao)
                // Запускаем запрос в IO-потоке
                val userData = withContext(Dispatchers.IO) {
                    userRepository.getUserByEmail(userEmail)
                }
                if (userData != null) {
                    // Обновляем UI данными пользователя
                    binding.tvUserFullName.text = "${userData.firstName} ${userData.lastName}"
                    binding.tvEmail.text = userData.email
                    // Если URI фото не пустой, устанавливаем его в ImageView
                    userData.profilePhotoUri?.let { photoUriString ->
                        val photoUri = Uri.parse(photoUriString)
                        binding.ivAvatar.setImageURI(photoUri)
                    }
                    binding.tvGender.text = userData.gender
                    // Заглушка для Google email
                    binding.tvGoogleEmail.text = "google@mail.com"
                } else {
                    binding.tvUserFullName.text = "Пользователь не найден"
                    binding.tvEmail.text = ""
                }
            }
        } else {
            binding.tvUserFullName.text = "Пользователь не найден"
            binding.tvEmail.text = ""
        }
    }

    /**
     * Настраивает обработчики кликов для элементов UI.
     */
    private fun setupClickListeners() {
        // При нажатии на аватар открываем галерею для выбора нового фото
        binding.ivAvatar.setOnClickListener {
            profileImagePickerLauncher.launch(arrayOf("image/*"))
        }

        // Обработка клика по полю "Пароль" для смены пароля
        binding.llPassword.setOnClickListener {
            Snackbar.make(binding.root, "Сменить пароль", Snackbar.LENGTH_SHORT).show()
        }

        // Обработка клика по панели "Выйти из профиля"
        binding.llSignOut.setOnClickListener {
            // Очищаем токен и переходим на экран входа
            authRepository.clearToken()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Нижняя навигационная панель:
        // Нажатие на иконку домой
        binding.btnHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        // Нажатие на иконку избранное
        binding.btnFavorites.setOnClickListener {
            Snackbar.make(binding.root, "Избранное", Snackbar.LENGTH_SHORT).show()
        }
        // Нажатие на иконку шестеренки
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            data?.data?.let { selectedImageUri ->
                // Обновляем ImageView
                binding.ivAvatar.setImageURI(selectedImageUri)
                val newPhotoUri = selectedImageUri.toString()

                // Обновляем запись в базе данных
                val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
                val userEmail = prefs.getString("user_email", null)
                if (userEmail != null) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val dao = AppDatabase.getDatabase(applicationContext).userRegistrationDao()
                        val userRepository = UserRepository(dao)
                        // Получаем пользователя из БД
                        val userData = withContext(Dispatchers.IO) {
                            userRepository.getUserByEmail(userEmail)
                        }
                        if (userData != null) {
                            // Обновляем поле с URI нового изображения
                            val updatedUser = userData.copy(profilePhotoUri = selectedImageUri.toString())
                            // Вызываем метод обновления
                            withContext(Dispatchers.IO) {
                                userRepository.updateUser(updatedUser)
                            }
                        }
                    }
                }
            }
        }
    }
}