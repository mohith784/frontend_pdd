package com.example.grocerymanagerwithai

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HelpFaq : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_faq)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val faq1 = findViewById<TextView>(R.id.faq1)
        val faq2 = findViewById<TextView>(R.id.faq2)
        val faq3 = findViewById<TextView>(R.id.faq3)

        faq1.setOnClickListener {
            Toast.makeText(this, "Opening App Details", Toast.LENGTH_SHORT).show()
            Log.d("HelpFaq", "faq1 clicked")
            startActivity(Intent(this, AppDo::class.java))
        }

        faq2.setOnClickListener {
            Toast.makeText(this, "Opening Add Product Help", Toast.LENGTH_SHORT).show()
            Log.d("HelpFaq", "faq2 clicked")
            startActivity(Intent(this, HowAdd::class.java))
        }

        faq3.setOnClickListener {
            Toast.makeText(this, "Opening Stock Prediction Help", Toast.LENGTH_SHORT).show()
            Log.d("HelpFaq", "faq3 clicked")
            startActivity(Intent(this, HowStock::class.java))
        }
    }
}
