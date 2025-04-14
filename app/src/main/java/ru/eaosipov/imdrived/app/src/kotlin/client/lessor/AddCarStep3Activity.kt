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
    private val imagePickerRequestCode = 101
    private lateinit var imageViews: List<ImageView>
    private var selectedImages: MutableList<Uri> = mutableListOf()
    private lateinit var btnNext: Button
    private lateinit var btnBack: AppCompatImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_car_step3)

        imageViews = listOf(
            findViewById(R.id.photo1),
            findViewById(R.id.photo2),
            findViewById(R.id.photo3),
            findViewById(R.id.photo4),
            findViewById(R.id.photo5)
        )

        btnNext = findViewById(R.id.btnNext)
        btnBack = findViewById(R.id.btnBack)

        imageViews.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                if (selectedImages.size <= index) {
                    pickImage()
                }
            }
        }

        btnNext.setOnClickListener {

            // Если данные валидны, завершаем регистрацию и переходим на главный экран
                val intent = Intent(this, FinalConnectCarActivity::class.java)
                startActivity(intent)
                finish()
        }

        // Обработка нажатия кнопки "Назад"
        btnBack.setOnClickListener {
            // Возвращаемся к предыдущему экрану регистрации
            finish()
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, imagePickerRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imagePickerRequestCode && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            if (selectedImages.size < imageViews.size) {
                selectedImages.add(uri)
                imageViews[selectedImages.size - 1].setImageURI(uri)
            }
        }
    }
}