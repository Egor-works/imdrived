package ru.eaosipov.imdrived.app.src.kotlin.client.lessor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import ru.eaosipov.imdrived.R

class AddCarStep3Activity : AppCompatActivity() {
    // Код запроса для выбора изображения
    private val imagePickerRequestCode = 101

    // Список ImageView для отображения выбранных изображений
    private lateinit var imageViews: List<ImageView>

    // Список URI выбранных изображений
    private var selectedImages: MutableList<Uri> = mutableListOf()

    // Кнопки "Далее" и "Назад"
    private lateinit var btnNext: Button
    private lateinit var btnBack: AppCompatImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_car_step3)

        // Инициализация списка ImageView из разметки
        imageViews = listOf(
            findViewById(R.id.photo1),
            findViewById(R.id.photo2),
            findViewById(R.id.photo3),
            findViewById(R.id.photo4),
            findViewById(R.id.photo5)
        )

        // Инициализация кнопок
        btnNext = findViewById(R.id.btnNext)
        btnBack = findViewById(R.id.btnBack)

        // Установка обработчиков кликов для каждого ImageView
        imageViews.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                // Если количество выбранных изображений меньше или равно текущему индексу,
                // открываем выбор изображения
                if (selectedImages.size <= index) {
                    pickImage()
                }
            }
        }

        // Обработка нажатия кнопки "Далее"
        btnNext.setOnClickListener {
            // Переход на финальный экран добавления автомобиля
            val intent = Intent(this, FinalConnectCarActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Обработка нажатия кнопки "Назад"
        btnBack.setOnClickListener {
            // Возврат на предыдущий экран
            finish()
        }
    }

    // Метод для открытия галереи и выбора изображения
    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*" // Указываем тип контента (изображения)
        startActivityForResult(intent, imagePickerRequestCode)
    }

    // Обработка результата выбора изображения
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Проверяем, что запрос соответствует нашему и результат успешный
        if (requestCode == imagePickerRequestCode && resultCode == RESULT_OK) {
            val uri = data?.data ?: return // Получаем URI выбранного изображения
            // Если количество выбранных изображений меньше доступных ImageView,
            // добавляем URI в список и отображаем изображение
            if (selectedImages.size < imageViews.size) {
                selectedImages.add(uri)
                imageViews[selectedImages.size - 1].setImageURI(uri)
            }
        }
    }
}