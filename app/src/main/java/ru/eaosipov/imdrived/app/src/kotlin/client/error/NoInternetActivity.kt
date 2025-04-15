package ru.eaosipov.imdrived.app.src.kotlin.client.error

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import ru.eaosipov.imdrived.R
import ru.eaosipov.imdrived.app.src.kotlin.client.onboarding.OnboardingActivity

/**
 * Экран, отображаемый при отсутствии интернет-соединения.
 * Позволяет пользователю проверить подключение и перейти к следующему экрану,
 * если соединение восстановлено.
 */
class NoInternetActivity : AppCompatActivity() {
    private lateinit var button: MaterialButton // Кнопка для повторной проверки соединения

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Включаем режим "edge-to-edge" для полноэкранного отображения
        enableEdgeToEdge()
        // Устанавливаем макет для активности
        setContentView(R.layout.activity_no_internet)
        // Запускаем проверку соединения
        checkConnection()
    }

    /**
     * Проверяет интернет-соединение и настраивает обработчик клика на кнопку.
     * Если соединение есть, переходит на экран OnboardingActivity.
     */
    private fun checkConnection() {
        // Находим кнопку в макете по её ID
        button = findViewById(R.id.btnRetry)
        // Устанавливаем обработчик клика на кнопку
        button.setOnClickListener {
            // Проверяем наличие интернет-соединения
            if (isOnline()) {
                // Если соединение есть, создаём Intent для перехода на OnboardingActivity
                val intent = Intent(this, OnboardingActivity::class.java)
                // Запускаем новую активность
                startActivity(intent)
                // Завершаем текущую активность, чтобы пользователь не мог вернуться назад
                finish()
            }
        }
    }
}