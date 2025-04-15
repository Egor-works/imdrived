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

    // Объявление переменных для элементов интерфейса
    private lateinit var locationAddress: EditText  // Поле для ввода адреса размещения автомобиля
    private lateinit var btnNext: Button            // Кнопка "Далее"
    private lateinit var btnBack: AppCompatImageButton  // Кнопка "Назад"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_car_step1)  // Установка разметки для активности

        // Инициализация элементов интерфейса из разметки
        locationAddress = findViewById(R.id.etLocationAddress)
        btnNext = findViewById(R.id.btnNext)
        btnBack = findViewById(R.id.btnBack)

        // Обновление состояния кнопки "Далее" (изначально выключена)
        updateNextButtonState()

        // Добавление слушателя изменений текста для поля адреса
        locationAddress.addTextChangedListener(textWatcher)

        // Слушатель клика по полю адреса (для обновления состояния кнопки)
        locationAddress.setOnClickListener { _ ->
            updateNextButtonState()
        }

        // Обработка нажатия кнопки "Далее"
        btnNext.setOnClickListener {
            // Создание Bundle для передачи данных на следующий экран
            val addCarDataStep1 = Bundle().apply {
                putString("locationAddress", locationAddress.text.toString().trim())  // Сохранение адреса
            }

            // Создание Intent для перехода на следующий экран (AddCarStep2Activity)
            val intent = Intent(this, AddCarStep2Activity::class.java)
            intent.putExtras(addCarDataStep1)  // Передача данных
            startActivity(intent)  // Запуск следующей активности
        }

        // Обработка нажатия кнопки "Назад"
        btnBack.setOnClickListener {
            finish()  // Завершение текущей активности (возврат на предыдущий экран)
        }
    }

    // Метод для обновления состояния кнопки "Далее"
    private fun updateNextButtonState() {
        btnNext.isEnabled = areRequiredFieldsFilled()  // Активация кнопки, если поля заполнены
    }

    // Метод для проверки заполненности обязательных полей
    private fun areRequiredFieldsFilled(): Boolean {
        val locationAddress = locationAddress.text.toString().trim()  // Получение текста из поля адреса

        return locationAddress.isNotEmpty()  // Возвращает true, если поле не пустое
    }

    // Слушатель изменений текста для обновления состояния кнопки "Далее"
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            updateNextButtonState()  // Обновление состояния кнопки после изменения текста
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Не используется
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Не используется
        }
    }
}