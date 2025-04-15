package ru.eaosipov.imdrived.app.src.kotlin.client.lessor

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import ru.eaosipov.imdrived.R

class AddCarStep2Activity : AppCompatActivity() {

    // Объявление переменных для элементов интерфейса
    private lateinit var yearOfRelease: EditText // Поле для ввода года выпуска автомобиля
    private lateinit var brand: EditText // Поле для ввода марки автомобиля
    private lateinit var model: EditText // Поле для ввода модели автомобиля
    private lateinit var transmission: Spinner // Выпадающий список для выбора типа трансмиссии
    private lateinit var description: EditText // Поле для ввода описания автомобиля
    private lateinit var mileage: EditText // Поле для ввода пробега автомобиля
    private lateinit var btnNext: Button // Кнопка "Далее" для перехода к следующему шагу
    private lateinit var btnBack: AppCompatImageButton // Кнопка "Назад" для возврата к предыдущему шагу

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_car_step2) // Установка разметки для активности

        // Инициализация элементов интерфейса из разметки
        yearOfRelease = findViewById(R.id.etYearOfRelease)
        brand = findViewById(R.id.etCarBrand)
        model = findViewById(R.id.etCarModel)
        transmission = findViewById(R.id.transmissionSpinner)
        description = findViewById(R.id.etCarDescription)
        mileage = findViewById(R.id.etMileageInput)
        btnNext = findViewById(R.id.btnNext)
        btnBack = findViewById(R.id.btnBack)

        // Изначально кнопка "Далее" выключена (установлено в разметке)
        updateNextButtonState() // Обновление состояния кнопки "Далее"

        // Добавление слушателей изменений текста для обязательных полей
        brand.addTextChangedListener(textWatcher)
        model.addTextChangedListener(textWatcher)
        yearOfRelease.addTextChangedListener(textWatcher)

        // Добавление слушателя на выбор элемента в выпадающем списке трансмиссии
        transmission.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Обновление состояния кнопки "Далее" при выборе элемента
                updateNextButtonState()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Ничего не делаем, если ничего не выбрано
            }
        }

        // Обработка нажатия кнопки "Далее"
        btnNext.setOnClickListener {
            // Получение данных из предыдущего шага (адрес расположения автомобиля)
            val locationAddress = intent.getStringExtra("locationAddress")

            // Создание Bundle для передачи данных на следующий шаг
            val addCarDataStep2 = Bundle().apply {
                putString("locationAddress", locationAddress)
                putString("yearOfRelease", yearOfRelease.text.toString().trim())
                putString("brand", brand.text.toString().trim())
                putString("model", model.text.toString().trim())
                putString("transmission", transmission.selectedItem.toString())
                putString("description", description.text.toString().trim())
            }

            // Переход к следующему шагу (AddCarStep3Activity) с передачей данных
            val intent = Intent(this, AddCarStep3Activity::class.java)
            intent.putExtras(addCarDataStep2)
            startActivity(intent)
        }

        // Обработка нажатия кнопки "Назад"
        btnBack.setOnClickListener {
            // Возврат к предыдущему шагу (AddCarStep1Activity)
            finish()
        }
    }

    // Метод для обновления состояния кнопки "Далее"
    private fun updateNextButtonState() {
        btnNext.isEnabled = areRequiredFieldsFilled() // Активация кнопки, если все обязательные поля заполнены
    }

    // Метод для проверки заполнения обязательных полей
    private fun areRequiredFieldsFilled(): Boolean {
        val yearOfRelease = yearOfRelease.text.toString().trim()
        val brand = brand.text.toString().trim()
        val model = model.text.toString().trim()
        val transmission = transmission.selectedItem.toString()

        // Проверка, что все обязательные поля заполнены и выбран тип трансмиссии (не дефолтное значение)
        return yearOfRelease.isNotEmpty() && brand.isNotEmpty() && model.isNotEmpty() && transmission != "Трансмиссия"
    }

    // Текстовый слушатель для обновления состояния кнопки "Далее" при изменении текста
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            updateNextButtonState() // Обновление состояния кнопки после изменения текста
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Ничего не делаем перед изменением текста
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Ничего не делаем во время изменения текста
        }
    }
}