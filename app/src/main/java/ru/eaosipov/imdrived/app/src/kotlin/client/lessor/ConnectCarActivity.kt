package ru.eaosipov.imdrived.app.src.kotlin.client.lessor

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.appcompat.app.AppCompatActivity
import ru.eaosipov.imdrived.app.src.kotlin.client.register.RegistrationStep2Activity
import ru.eaosipov.imdrived.databinding.ActivityConnectCarBinding

class ConnectCarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConnectCarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnectCarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup your UI elements from Figma here
        setupUI()
    }
    private fun setupUI() {

        // Обработка кнопки "Далее"
        binding.btnNext.setOnClickListener {
            val intent = Intent(this, AddCarStep1Activity::class.java)
            startActivity(intent)

        }

        // Обработка кнопки "Назад"
        binding.btnBack.setOnClickListener {
            finish() // Возврат на предыдущий экран
        }
    }
}