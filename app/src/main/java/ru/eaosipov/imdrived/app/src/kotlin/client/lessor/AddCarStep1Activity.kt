package ru.eaosipov.imdrived.app.src.kotlin.client.lessor

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import ru.eaosipov.imdrived.R

class AddCarStep1Activity : AppCompatActivity() {

    private lateinit var locationAddress: EditText
    private lateinit var btnNext: Button
    private lateinit var btnBack: AppCompatImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_car_step1)

        // Инициализация View из разметки
        locationAddress = findViewById(R.id.etLocationAddress)
        btnNext = findViewById(R.id.btnNext)
        btnBack = findViewById(R.id.btnBack)


        // Изначально кнопка "Далее" выключена (установлено в разметке)
        updateNextButtonState()

        // Добавляем слушатели для обязательных полей
        locationAddress.addTextChangedListener(textWatcher)

        locationAddress.setOnClickListener { _ ->
            updateNextButtonState()
        }

        // Обработка нажатия кнопки "Далее"
        btnNext.setOnClickListener {
                val addCarDataStep1 = Bundle().apply {
                    putString("locationAddress", locationAddress.text.toString().trim())
                }

                // Если данные валидны, завершаем регистрацию и переходим на главный экран
                val intent = Intent(this, AddCarStep2Activity::class.java)
                intent.putExtras(addCarDataStep1)
                startActivity(intent)
        }

        // Обработка нажатия кнопки "Назад"
        btnBack.setOnClickListener {
            // Возвращаемся к предыдущему экрану регистрации
            finish()
        }
    }
    // Обновление состояния кнопки "Далее"
    private fun updateNextButtonState() {
        btnNext.isEnabled = areRequiredFieldsFilled()
    }
    // Проверка, что обязательные поля заполнены
    private fun areRequiredFieldsFilled(): Boolean {
        val locationAddress = locationAddress.text.toString().trim()

        return locationAddress.isNotEmpty()
    }
    // Текстовый слушатель для обновления состояния кнопки "Далее"
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            updateNextButtonState()
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }
}