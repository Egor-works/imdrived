package ru.eaosipov.imdrived.app.src.kotlin.client.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import ru.eaosipov.imdrived.R
import ru.eaosipov.imdrived.app.src.kotlin.client.auth.UserAccessActivity

/**
 * OnboardingActivity - экран ознакомления с приложением (онбординг).
 * Показывает пользователю несколько слайдов с информацией о возможностях приложения.
 */
class OnboardingActivity : AppCompatActivity() {

    // Элементы интерфейса
    private lateinit var viewPager: ViewPager2 // ViewPager для слайдов онбординга
    private lateinit var btnSkip: Button      // Кнопка "Пропустить"
    private lateinit var btnNext: Button      // Кнопка "Далее" или "Поехали"

    // Адаптер для ViewPager
    private lateinit var onboardingAdapter: OnboardingAdapter

    // Список слайдов онбординга
    private val onboardingItems = listOf(
        OnboardingItem(
            title = "Аренда автомобилей",
            description = "Открой для себя удобный и доступный способ передвижения",
            imageRes = R.drawable.onboarding_image1 // Ресурс изображения для первого слайда
        ),
        OnboardingItem(
            title = "Безопасно и удобно",
            description = "Арендуй автомобиль и наслаждайся его удобством",
            imageRes = R.drawable.onboarding_image2 // Ресурс изображения для второго слайда
        ),
        OnboardingItem(
            title = "Лучшие предложения",
            description = "Выбирай понравившееся среди сотен доступных автомобилей",
            imageRes = R.drawable.onboarding_image3 // Ресурс изображения для третьего слайда
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Проверяем, проходил ли пользователь онбординг ранее
        val sharedPref = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val onboardingComplete = sharedPref.getBoolean("onboarding_complete", false)

        // Если онбординг уже пройден, сразу переходим к экрану авторизации
        if (onboardingComplete) {
            navigateToAuthScreen()
            return
        }

        // Устанавливаем макет для активности
        setContentView(R.layout.activity_onboarding)

        // Инициализация элементов интерфейса
        viewPager = findViewById(R.id.viewPager)
        btnSkip = findViewById(R.id.btnSkip)
        btnNext = findViewById(R.id.btnNext)

        // Настройка адаптера для ViewPager
        onboardingAdapter = OnboardingAdapter(onboardingItems)
        viewPager.adapter = onboardingAdapter

        // Обновление текста кнопки "Далее" в зависимости от текущего слайда
        updateButtonText(viewPager.currentItem)

        // Обработка нажатия на кнопку "Пропустить"
        btnSkip.setOnClickListener {
            completeOnboarding() // Завершаем онбординг
        }

        // Обработка нажатия на кнопку "Далее" или "Поехали"
        btnNext.setOnClickListener {
            if (viewPager.currentItem < onboardingItems.size - 1) {
                // Переход к следующему слайду
                viewPager.currentItem = viewPager.currentItem + 1
            } else {
                // Если это последний слайд, завершаем онбординг
                completeOnboarding()
            }
        }

        // Слушатель изменения текущего слайда
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateButtonText(position) // Обновляем текст кнопки
            }
        })
    }

    /**
     * Обновляет текст кнопки "Далее" в зависимости от текущего слайда.
     * @param position Позиция текущего слайда.
     */
    private fun updateButtonText(position: Int) {
        // Если это последний слайд, меняем текст на "Поехали"
        if (position == onboardingItems.size - 1) {
            btnNext.text = "Поехали"
        } else {
            btnNext.text = "Далее"
        }
    }

    /**
     * Завершает процесс онбординга:
     * 1. Сохраняет флаг о завершении онбординга.
     * 2. Переходит на экран авторизации.
     */
    private fun completeOnboarding() {
        // Сохраняем флаг, что пользователь прошёл онбординг
        val sharedPref = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("onboarding_complete", true)
            apply()
        }

        // Переход на экран авторизации
        navigateToAuthScreen()
    }

    /**
     * Переход на экран авторизации.
     */
    private fun navigateToAuthScreen() {
        startActivity(Intent(this, UserAccessActivity::class.java))
        finish() // Закрываем текущую активность
    }

    companion object {
        /**
         * Проверяет, нужно ли показывать онбординг пользователю.
         * @param context Контекст приложения.
         * @return true, если онбординг нужно показать, иначе false.
         */
        fun shouldShowOnboarding(context: Context): Boolean {
            val sharedPref = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
            val onboardingComplete = sharedPref.getBoolean("onboarding_complete", false)
            return !onboardingComplete
        }
    }
}