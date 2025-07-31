package com.example.grocerymanagerwithai

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.grocerymanagerwithai.api.RetrofitClient
import com.example.grocerymanagerwithai.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var signupText: TextView
    private lateinit var progressBar: ProgressBar

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /* ---------- View refs ---------- */
        emailInput   = findViewById(R.id.editTextText)
        passwordInput= findViewById(R.id.editTextTextPassword3)
        loginButton  = findViewById(R.id.logIn)
        signupText   = findViewById(R.id.textView7)
        progressBar  = findViewById(R.id.progressBar)

        /* ---------- Login ---------- */
        loginButton.setOnClickListener {
            val email    = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            loginButton.isEnabled  = false

            RetrofitClient.instance.loginUser(email, password)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        progressBar.visibility = View.GONE
                        loginButton.isEnabled  = true

                        val body = response.body()
                        if (response.isSuccessful && body?.status == true) {
                            // Save login status and email
                            val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                            sharedPref.edit()
                                .putBoolean("isLoggedIn", true)
                                .putString("email", email) // <-- Save email
                                .apply()

                            startActivity(Intent(this@LoginActivity, DashBoard::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                body?.message ?: "Login failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        progressBar.visibility = View.GONE
                        loginButton.isEnabled  = true
                        Toast.makeText(
                            this@LoginActivity,
                            "Error: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }

        /* ---------- Sign‑up link ---------- */
        signupText.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}
