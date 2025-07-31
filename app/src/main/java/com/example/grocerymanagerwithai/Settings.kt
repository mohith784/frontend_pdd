package com.example.grocerymanagerwithai

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat

class Settings : AppCompatActivity() {

    private lateinit var thresholdInput: EditText
    private lateinit var switchLowStock: SwitchCompat
    private lateinit var switchDarkMode: SwitchCompat
    private lateinit var logoutTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)

        // Initialize UI elements
        thresholdInput = findViewById(R.id.threshold_input)
        switchLowStock = findViewById(R.id.switch_low_stock)
        switchDarkMode = findViewById(R.id.switch_dark_mode)
        logoutTextView = findViewById(R.id.tv_logout)
        emailTextView = findViewById(R.id.emailTextView)
        val backButton = findViewById<ImageView>(R.id.back_button)

        // Load and apply saved threshold
        val savedThreshold = sharedPreferences.getInt("low_stock_threshold", 10)
        thresholdInput.setText(savedThreshold.toString())


        emailTextView.text = sharedPreferences.getString("email","")
        // Handle threshold value changes
        thresholdInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val value = s.toString().toIntOrNull()
                value?.let {
                    sharedPreferences.edit().putInt("low_stock_threshold", it).apply()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Load and apply Low Stock switch state
        val isLowStockEnabled = sharedPreferences.getBoolean("LOW_STOCK", true)
        switchLowStock.isChecked = isLowStockEnabled

        switchLowStock.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("LOW_STOCK", isChecked).apply()
        }

        // Load and apply dark mode setting
        val darkModeOn = sharedPreferences.getBoolean("dark_mode", false)
        switchDarkMode.isChecked = darkModeOn
        AppCompatDelegate.setDefaultNightMode(
            if (darkModeOn) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        // Logout and redirect to login screen
        logoutTextView.setOnClickListener {
            sharedPreferences.edit().clear().apply()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Back navigation
        backButton.setOnClickListener {
            finish()
        }
    }
}
