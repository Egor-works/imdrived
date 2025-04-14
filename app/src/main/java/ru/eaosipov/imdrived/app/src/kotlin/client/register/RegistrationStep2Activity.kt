package ru.eaosipov.imdrived.app.src.kotlin.client.register

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import ru.eaosipov.imdrived.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * RegistrationStep2Activity — экран для ввода дополнительных данных.
 */
class RegistrationStep2Activity : AppCompatActivity() {

    private lateinit var etLastName: EditText
    private lateinit var etFirstName: EditText
    private lateinit var etMiddleName: EditText
    private lateinit var etBirthDate: EditText
    private lateinit var rgGender: RadioGroup
    private lateinit var btnNext: Button
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_step2)

        // Инициализация View из разметки
        etLastName = findViewById(R.id.etLastName)
        etFirstName = findViewById(R.id.etFirstName)
        etMiddleName = findViewById(R.id.etMiddleName)
        etBirthDate = findViewById(R.id.etBirthDate)
        rgGender = findViewById(R.id.rgGender)
        btnNext = findViewById(R.id.btnNext)
        btnBack = findViewById(R.id.btnBack)

        // Изначально кнопка "Далее" выключена (установлено в разметке)
        updateNextButtonState()

        // Добавляем слушатели для обязательных полей
        etLastName.addTextChangedListener(textWatcher)
        etFirstName.addTextChangedListener(textWatcher)
        etBirthDate.addTextChangedListener(textWatcher)

        rgGender.setOnCheckedChangeListener { _, _ ->
            updateNextButtonState()
        }

        // Обработка нажатия на поле даты рождения
        etBirthDate.setOnClickListener { showDatePickerDialog() }
        etBirthDate.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) showDatePickerDialog()
        }

        // Обработка нажатия кнопки "Далее"
        btnNext.setOnClickListener {
            if (validateInput()) {
                val email = intent.getStringExtra("email")
                val password = intent.getStringExtra("password")
                val registrationDataStep2 = Bundle().apply {
                    putString("email", email)
                    putString("password", password)
                    putString("lastName", etLastName.text.toString().trim())
                    putString("firstName", etFirstName.text.toString().trim())
                    putString("middleName", etMiddleName.text.toString().trim())
                    putString("birthDate", etBirthDate.text.toString().trim())
                    putString("gender", if (rgGender.checkedRadioButtonId == R.id.rbMale) "Мужской" else "Женский")
                }

                // Если данные валидны, завершаем регистрацию и переходим на главный экран
                val intent = Intent(this, RegistrationStep3Activity::class.java)
                intent.putExtras(registrationDataStep2)
                startActivity(intent)
                finish()
            }
        }

        // Обработка нажатия кнопки "Назад"
        btnBack.setOnClickListener {
            // Возвращаемся к предыдущему экрану регистрации
            finish()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
            etBirthDate.setText(dateFormat.format(calendar.time))
        }

        val datePickerDialog = DatePickerDialog(
            this,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    // Текстовый слушатель для обновления состояния кнопки "Далее"
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            updateNextButtonState()
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    // Обновление состояния кнопки "Далее"
    private fun updateNextButtonState() {
        btnNext.isEnabled = areRequiredFieldsFilled()
    }

    // Проверка, что обязательные поля заполнены
    private fun areRequiredFieldsFilled(): Boolean {
        val lastName = etLastName.text.toString().trim()
        val firstName = etFirstName.text.toString().trim()
        val birthDate = etBirthDate.text.toString().trim()
        val isGenderSelected = rgGender.checkedRadioButtonId != -1

        return lastName.isNotEmpty() && firstName.isNotEmpty() && birthDate.isNotEmpty() && isGenderSelected
    }

    // Валидация введённых данных
    private fun validateInput(): Boolean {
        val lastName = etLastName.text.toString().trim()
        val firstName = etFirstName.text.toString().trim()
        val birthDate = etBirthDate.text.toString().trim()

        // Проверка заполненности обязательных полей
        if (lastName.isEmpty() || firstName.isEmpty() || birthDate.isEmpty() || rgGender.checkedRadioButtonId == -1) {
            Snackbar.make(findViewById(android.R.id.content),
                "Пожалуйста, заполните все обязательные поля.",
                Snackbar.LENGTH_SHORT).show()
            return false
        }

        // Проверка корректности формата даты рождения (MM/DD/YYYY)
        val datePattern = Regex("^(0[1-9]|1[0-2])/(0[1-9]|[12]\\d|3[01])/\\d{4}$")
        if (!datePattern.matches(birthDate)) {
            Snackbar.make(findViewById(android.R.id.content),
                "Введите корректную дату рождения.",
                Snackbar.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}