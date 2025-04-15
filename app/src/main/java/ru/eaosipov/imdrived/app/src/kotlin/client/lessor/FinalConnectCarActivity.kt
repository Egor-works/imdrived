package ru.eaosipov.imdrived.app.src.kotlin.client.lessor

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.eaosipov.imdrived.app.src.kotlin.client.main.MainActivity
import ru.eaosipov.imdrived.databinding.ActivityConnectCarFinalBinding

class FinalConnectCarActivity : AppCompatActivity() {
    // Привязка к разметке через View Binding
    private lateinit var binding: ActivityConnectCarFinalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Инициализация View Binding
        binding = ActivityConnectCarFinalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Настройка пользовательского интерфейса
        setupUI()
    }

    /**
     * Настройка элементов пользовательского интерфейса.
     * Здесь обрабатываются все кнопки и другие интерактивные элементы.
     */
    private fun setupUI() {
        // Обработка нажатия кнопки "Далее"
        binding.btnNext.setOnClickListener {
            // Создание Intent для перехода на главный экран (MainActivity)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // Завершение текущей активности, чтобы пользователь не мог вернуться назад
            finish()
        }

        // Обработка нажатия кнопки "Назад"
        binding.btnBack.setOnClickListener {
            // Завершение текущей активности и возврат на предыдущий экран
            finish()
        }
    }
}