package ru.eaosipov.imdrived.app.src.kotlin.client.error

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import ru.eaosipov.imdrived.R
import ru.eaosipov.imdrived.app.src.kotlin.client.onboarding.OnboardingActivity


class NoInternetActivity : AppCompatActivity() {
    private lateinit var button: MaterialButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_no_internet)
        checkConnection()
    }


    private fun checkConnection() {
        button = findViewById(R.id.btnRetry)
        button.setOnClickListener {
            if (isOnline()) {
                val i = Intent(this, OnboardingActivity::class.java)
                startActivity(i)
            }
        }
    }
}
