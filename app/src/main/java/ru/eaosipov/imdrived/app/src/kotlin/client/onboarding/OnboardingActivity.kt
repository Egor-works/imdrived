package ru.eaosipov.imdrived.app.src.kotlin.client.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import ru.eaosipov.imdrived.R
import ru.eaosipov.imdrived.app.src.kotlin.client.auth.UserAccessActivity


class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var btnSkip: Button
    private lateinit var btnNext: Button

    private lateinit var onboardingAdapter: OnboardingAdapter
    private val onboardingItems = listOf(
        OnboardingItem(
            title = "Аренда автомобилей",
            description = "Открой для себя удобный и доступный способ передвижения ",
            imageRes = R.drawable.onboarding_image1 // замените на свой ресурс
        ),
        OnboardingItem(
            title = "Безопасно и удобно",
            description = "Арендуй автомобиль и наслаждайся его удобством",
            imageRes = R.drawable.onboarding_image2 // замените на свой ресурс
        ),
        OnboardingItem(
            title = "Лучшие предложения",
            description = "Выбирай понравившееся среди сотен доступных автомобилей",
            imageRes = R.drawable.onboarding_image3 // замените на свой ресурс
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
        
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.viewPager)
        btnSkip = findViewById(R.id.btnSkip)
        btnNext = findViewById(R.id.btnNext)

        onboardingAdapter = OnboardingAdapter(onboardingItems)
        viewPager.adapter = onboardingAdapter

        // Обновление текста кнопки "Далее" в зависимости от текущего слайда
        updateButtonText(viewPager.currentItem)

        btnSkip.setOnClickListener {
            completeOnboarding()
        }

        btnNext.setOnClickListener {
            if (viewPager.currentItem < onboardingItems.size - 1) {
                viewPager.currentItem = viewPager.currentItem + 1
            } else {
                completeOnboarding()
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateButtonText(position)
            }
        })
    }

    private fun updateButtonText(position: Int) {
        // Если последний слайд, меняем текст на "Поехали"
        if (position == onboardingItems.size - 1) {
            btnNext.text = "Поехали"
        } else {
            btnNext.text = "Далее"
        }
    }

    private fun completeOnboarding() {
        // Сохраняем флаг, что пользователь прошёл онбординг
        val sharedPref = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("onboarding_complete", true)
            apply()
        }
        
        navigateToAuthScreen()
    }
    
    private fun navigateToAuthScreen() {
        // Переход на экран входа/регистрации
        startActivity(Intent(this, UserAccessActivity::class.java))
        finish()
    }
    
    companion object {
        // Метод для проверки необходимости показа онбординга
        fun shouldShowOnboarding(context: Context): Boolean {
            val sharedPref = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
            val onboardingComplete = sharedPref.getBoolean("onboarding_complete", false)
            // Также проверяем наличие токена, если есть система авторизации
            // val hasToken = sharedPref.getString("access_token", null) != null
            // return !onboardingComplete && !hasToken
            return !onboardingComplete
        }
    }
}
