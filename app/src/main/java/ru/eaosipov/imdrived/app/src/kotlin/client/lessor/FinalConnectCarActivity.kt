package ru.eaosipov.imdrived.app.src.kotlin.client.lessor

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.eaosipov.imdrived.app.src.kotlin.client.main.MainActivity
import ru.eaosipov.imdrived.databinding.ActivityConnectCarBinding
import ru.eaosipov.imdrived.databinding.ActivityConnectCarFinalBinding

class FinalConnectCarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConnectCarFinalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnectCarFinalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup your UI elements from Figma here
        setupUI()
    }
    private fun setupUI() {

        // Обработка кнопки "Далее"
        binding.btnNext.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }

        // Обработка кнопки "Назад"
        binding.btnBack.setOnClickListener {
            finish() // Возврат на предыдущий экран
        }
    }
}