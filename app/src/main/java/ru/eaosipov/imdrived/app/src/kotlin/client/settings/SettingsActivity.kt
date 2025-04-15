package ru.eaosipov.imdrived.app.src.kotlin.client.settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.net.Uri
import ru.eaosipov.imdrived.app.src.kotlin.client.bookings.MyBookingsActivity
import ru.eaosipov.imdrived.app.src.kotlin.client.favorite.FavoriteActivity
import ru.eaosipov.imdrived.app.src.kotlin.client.lessor.ConnectCarActivity
import ru.eaosipov.imdrived.app.src.kotlin.service.db.AppDatabase
import ru.eaosipov.imdrived.app.src.kotlin.service.repository.UserRepository
import ru.eaosipov.imdrived.app.src.kotlin.client.main.MainActivity
import ru.eaosipov.imdrived.app.src.kotlin.client.profile.ProfileActivity
import ru.eaosipov.imdrived.databinding.ActivitySettingsBinding

/**
 * SettingsActivity - экран настроек пользователя.
 * Отображает профиль (аватар, имя, email) и список пунктов меню настроек.
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Загружаем данные профиля (в реальном приложении данные берутся из SharedPreferences или базы данных)
        loadUserProfile()

        setupClickListeners()
    }

    /**
     * Загружает данные профиля пользователя из базы данных.
     * Обновляет UI с полученными данными (имя, email, аватар).
     */
    private fun loadUserProfile() {
        // Получаем email текущего пользователя из SharedPreferences или другого источника
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
                    // Обновляем UI
                    binding.tvUserName.text = "${userData.firstName} ${userData.lastName}"
                    binding.tvUserEmail.text = userData.email
                    // Если URI фото не пустой, устанавливаем его в ImageView
                    userData.profilePhotoUri?.let { photoUriString ->
                        val photoUri = Uri.parse(photoUriString)
                        binding.ivAvatar.setImageURI(photoUri)
                    }
                } else {
                    binding.tvUserName.text = "Пользователь не найден"
                    binding.tvUserEmail.text = ""
                }
            }
        } else {
            binding.tvUserName.text = "Пользователь не найден"
            binding.tvUserEmail.text = ""
        }
    }

    /**
     * Настраивает обработчики кликов для всех элементов UI.
     */
    private fun setupClickListeners() {
        // При нажатии на блок профиля переходим на экран "Профиль"
        binding.llProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Переход "Мои бронирования"
        binding.tvMyBookings.setOnClickListener {
            val intent = Intent(this, MyBookingsActivity::class.java)
            startActivity(intent)
        }

        // Пункт "Тема" — здесь можно открыть диалог выбора темы или отдельный экран
        binding.tvTheme.setOnClickListener {
            Snackbar.make(binding.root, "Выбор темы", Snackbar.LENGTH_SHORT).show()
        }

        // Пункт "Уведомления" — открытие настроек уведомлений
        binding.tvNotifications.setOnClickListener {
            Snackbar.make(binding.root, "Настройки уведомлений", Snackbar.LENGTH_SHORT).show()
        }

        // Переход "Подключить свой автомобиль"
        binding.tvConnectCar.setOnClickListener {
            startActivity(Intent(this, ConnectCarActivity::class.java))
            Snackbar.make(binding.root, "Подключить свой автомобиль", Snackbar.LENGTH_SHORT).show()
        }

        // Пункт "Помощь"
        binding.tvHelp.setOnClickListener {
            Snackbar.make(binding.root, "Помощь", Snackbar.LENGTH_SHORT).show()
        }

        // Пункт "Пригласи друга"
        binding.tvInviteFriend.setOnClickListener {
            Snackbar.make(binding.root, "Пригласи друга", Snackbar.LENGTH_SHORT).show()
        }

        // Нижняя навигационная панель:
        binding.btnHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        binding.btnFavorites.setOnClickListener {
            // Переход на экран избранное
            startActivity(Intent(this, FavoriteActivity::class.java))
            finish()
        }
        binding.btnSettings.setOnClickListener {
            // Так как мы уже на экране настроек, можно оставить пустым или просто показать Snackbar.
            Snackbar.make(binding.root, "Уже на экране настроек", Snackbar.LENGTH_SHORT).show()
        }
    }
}