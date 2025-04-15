package ru.eaosipov.imdrived.app.src.kotlin.client.register

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultLauncher
import ru.eaosipov.imdrived.R
import ru.eaosipov.imdrived.app.src.kotlin.service.entities.UserRegistrationData
import ru.eaosipov.imdrived.databinding.ActivityRegistrationStep3Binding

/**
 * RegistrationStep3Activity — экран для загрузки документов и фото пользователя.
 * Позволяет пользователю загрузить фото профиля, водительского удостоверения и паспорта,
 * а также ввести номер и дату выдачи водительского удостоверения.
 */
class RegistrationStep3Activity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationStep3Binding

    private lateinit var ivProfilePicture: ImageView
    private lateinit var etLicenseNumber: EditText
    private lateinit var etIssueDate: EditText
    private lateinit var btnUploadLicense: Button
    private lateinit var btnUploadPassport: Button
    private lateinit var btnNext: Button
    private lateinit var btnBack: Button

    // Флаги, показывающие, что фото успешно выбраны (лицевой и паспорта)
    private var isLicensePhotoUploaded = false
    private var isPassportPhotoUploaded = false
    private var isPhotoUploaded = false

    // Можно сохранить выбранные Uri, если нужно их использовать далее
    private var licensePhotoUri: Uri? = null
    private var passportPhotoUri: Uri? = null
    private var photoUri: Uri? = null

    // Лончеры для выбора изображений из галереи
    private lateinit var profileImagePickerLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var licenseImagePickerLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var passportImagePickerLauncher: ActivityResultLauncher<Array<String>>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationStep3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация лончеров для выбора изображений
        profileImagePickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                // Сохраняем доступ на чтение выбранного файла
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                contentResolver.takePersistableUriPermission(it, takeFlags)
                photoUri = it
                isPhotoUploaded = true
                binding.ivProfilePicture.setImageURI(it)
                Snackbar.make(binding.root, "Фото профиля загружено.", Snackbar.LENGTH_SHORT).show()
                updateNextButtonState()
            } ?: Snackbar.make(binding.root, "Фотография не выбрана.", Snackbar.LENGTH_SHORT).show()
        }

        licenseImagePickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                contentResolver.takePersistableUriPermission(it, takeFlags)
                licensePhotoUri = it
                isLicensePhotoUploaded = true
                Snackbar.make(binding.root, "Фото водительского удостоверения загружено.", Snackbar.LENGTH_SHORT).show()
                updateNextButtonState()
            } ?: Snackbar.make(binding.root, "Фотография не выбрана.", Snackbar.LENGTH_SHORT).show()
        }

        passportImagePickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                contentResolver.takePersistableUriPermission(it, takeFlags)
                passportPhotoUri = it
                isPassportPhotoUploaded = true
                Snackbar.make(binding.root, "Фото паспорта загружено.", Snackbar.LENGTH_SHORT).show()
                updateNextButtonState()
            } ?: Snackbar.make(binding.root, "Фотография не выбрана.", Snackbar.LENGTH_SHORT).show()
        }

        // Инициализация View-элементов
        ivProfilePicture = findViewById(R.id.ivProfilePicture)
        etLicenseNumber = findViewById(R.id.etLicenseNumber)
        etIssueDate = findViewById(R.id.etIssueDate)
        btnUploadLicense = findViewById(R.id.btnUploadLicense)
        btnUploadPassport = findViewById(R.id.btnUploadPassport)
        btnNext = findViewById(R.id.btnNext)
        btnBack = findViewById(R.id.btnBack)

        // Изначально обновляем состояние кнопки "Далее"
        updateNextButtonState()

        // Слушатели изменений для обязательных полей
        etLicenseNumber.addTextChangedListener(textWatcher)
        etIssueDate.addTextChangedListener(textWatcher)

        // Обработка нажатия на поле даты выдачи
        etIssueDate.setOnClickListener { showDatePickerDialog() }
        etIssueDate.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) showDatePickerDialog()
        }

        // Обработка нажатия на иконку фото профиля
        ivProfilePicture.setOnClickListener {
            profileImagePickerLauncher.launch(arrayOf("image/*"))
        }

        // Обработка нажатия на кнопку "Загрузить фото" для водительского удостоверения
        btnUploadLicense.setOnClickListener {
            licenseImagePickerLauncher.launch(arrayOf("image/*"))
        }

        // Обработка нажатия на кнопку "Загрузить фото" для паспорта
        btnUploadPassport.setOnClickListener {
            passportImagePickerLauncher.launch(arrayOf("image/*"))
        }

        // Обработка нажатия кнопки "Далее"
        btnNext.setOnClickListener {
            if (validateInput()) {
                val extras = intent.extras
                val userData = UserRegistrationData(
                    email = extras?.getString("email") ?: "",
                    password = extras?.getString("password") ?: "",
                    lastName = extras?.getString("lastName") ?: "",
                    firstName = extras?.getString("firstName") ?: "",
                    middleName = extras?.getString("middleName"),
                    birthDate = extras?.getString("birthDate") ?: "",
                    gender = extras?.getString("gender") ?: "",
                    licenseNumber = etLicenseNumber.text.toString().trim(),
                    issueDate = etIssueDate.text.toString().trim(),
                    licensePhotoUri = licensePhotoUri.toString(),
                    passportPhotoUri = passportPhotoUri.toString(),
                    profilePhotoUri = photoUri?.toString()
                )

                // Здесь вызывается ViewModel для сохранения данных
                val registrationViewModel = RegistrationViewModel(applicationContext)
                registrationViewModel.saveRegistrationData(userData) { success ->
                    if (success) {
                        // Переход на экран успешной регистрации
                        val intent = Intent(this, SuccessActivity::class.java)
                        intent.putExtra("email", extras?.getString("email") ?: "dima@mail.ru")
                        startActivity(intent)
                        finish()
                    } else {
                        Snackbar.make(findViewById(android.R.id.content),
                            "Ошибка при сохранении данных регистрации.",
                            Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Обработка нажатия кнопки "Назад"
        btnBack.setOnClickListener {
            finish() // Возврат на предыдущий экран регистрации (шаг 2)
        }
    }

    /**
     * Показывает диалог выбора даты для поля даты выдачи.
     */
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
            etIssueDate.setText(dateFormat.format(calendar.time))
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

    // Слушатель изменений текста для обновления состояния кнопки "Далее"
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            updateNextButtonState()
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    /**
     * Обновляет состояние кнопки "Далее" в зависимости от заполненности полей.
     */
    private fun updateNextButtonState() {
        btnNext.isEnabled = areRequiredFieldsFilled() && isLicensePhotoUploaded && isPassportPhotoUploaded
    }

    /**
     * Проверяет, заполнены ли обязательные поля.
     * @return true, если все обязательные поля заполнены, иначе false.
     */
    private fun areRequiredFieldsFilled(): Boolean {
        val licenseNumber = etLicenseNumber.text.toString().trim()
        val issueDate = etIssueDate.text.toString().trim()
        return licenseNumber.isNotEmpty() && issueDate.isNotEmpty()
    }

    /**
     * Проверяет корректность введённых данных.
     * @return true, если данные корректны, иначе false.
     */
    private fun validateInput(): Boolean {
        val licenseNumber = etLicenseNumber.text.toString().trim()
        val issueDate = etIssueDate.text.toString().trim()

        // Проверка заполненности обязательных полей и загрузки фото
        if (licenseNumber.isEmpty() || issueDate.isEmpty() || !isLicensePhotoUploaded || !isPassportPhotoUploaded) {
            Snackbar.make(findViewById(android.R.id.content),
                "Пожалуйста, заполните все обязательные поля.",
                Snackbar.LENGTH_SHORT).show()
            return false
        }

        // Проверка корректности формата даты выдачи (DD/MM/YYYY)
        val datePattern = Regex("^(0[1-9]|[12]\\d|3[01])/(0[1-9]|1[0-2])/\\d{4}$")
        if (!datePattern.matches(issueDate)) {
            Snackbar.make(findViewById(android.R.id.content),
                "Введите корректную дату выдачи.",
                Snackbar.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}