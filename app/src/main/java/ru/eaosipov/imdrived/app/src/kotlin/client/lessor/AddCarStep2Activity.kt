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

    private lateinit var yearOfRelease: EditText
    private lateinit var brand: EditText
    private lateinit var model: EditText
    private lateinit var transmission: Spinner
    private lateinit var description: EditText
    private lateinit var mileage: EditText
    private lateinit var btnNext: Button
    private lateinit var btnBack: AppCompatImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_car_step2)

        // Инициализация View из разметки
        yearOfRelease = findViewById(R.id.etYearOfRelease)
        brand = findViewById(R.id.etCarBrand)
        model = findViewById(R.id.etCarModel)
        transmission = findViewById(R.id.transmissionSpinner)
        description = findViewById(R.id.etCarDescription)
        mileage = findViewById(R.id.etMileageInput)
        btnNext = findViewById(R.id.btnNext)
        btnBack = findViewById(R.id.btnBack)


        // Изначально кнопка "Далее" выключена (установлено в разметке)
        updateNextButtonState()

        // Добавляем слушатели для обязательных полей
        brand.addTextChangedListener(textWatcher)
        model.addTextChangedListener(textWatcher)
        yearOfRelease.addTextChangedListener(textWatcher)

        // Добавляем слушатель на выбор элемента
        transmission.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Вызываем обновление кнопки
                updateNextButtonState()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Здесь можно ничего не обрабатывать
            }
        }

        // Обработка нажатия кнопки "Далее"
        btnNext.setOnClickListener {
            val locationAddress = intent.getStringExtra("locationAddress")
            val addCarDataStep2 = Bundle().apply {
                putString("locationAddress", locationAddress)
                putString("yearOfRelease", yearOfRelease.text.toString().trim())
                putString("brand", brand.text.toString().trim())
                putString("model", model.text.toString().trim())
                putString("transmission", transmission.selectedItem.toString())
                putString("description", description.text.toString().trim())
            }

            // Если данные валидны, завершаем регистрацию и переходим на главный экран
                val intent = Intent(this, AddCarStep3Activity::class.java)
                intent.putExtras(addCarDataStep2)
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
        val yearOfRelease = yearOfRelease.text.toString().trim()
        val brand = brand.text.toString().trim()
        val model = model.text.toString().trim()
        val transmission = transmission.selectedItem.toString()

        return yearOfRelease.isNotEmpty() && brand.isNotEmpty() && model.isNotEmpty() && transmission != "Трансмиссия"
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