package com.example.grocerymanagerwithai

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.grocerymanagerwithai.api.RetrofitClient
import com.example.grocerymanagerwithai.model.RegisterResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {

    lateinit var fullName: EditText
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var confirmPassword: EditText
    lateinit var signupButton: Button
    lateinit var loginText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        // Link views
        fullName = findViewById(R.id.fullName)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.confirmPassword)
        signupButton = findViewById(R.id.signupButton)
        loginText = findViewById(R.id.loginText)

        // Go to login screen if already have account
        loginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // On Sign Up click
        signupButton.setOnClickListener {
            val name = fullName.text.toString().trim()
            val emailText = email.text.toString().trim()
            val pass = password.text.toString().trim()
            val confirmPass = confirmPassword.text.toString().trim()

            if (name.isEmpty() || emailText.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass != confirmPass) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ Convert to RequestBody
            val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val emailPart = emailText.toRequestBody("text/plain".toMediaTypeOrNull())
            val passPart = pass.toRequestBody("text/plain".toMediaTypeOrNull())

            // ✅ Make the API call
            RetrofitClient.instance.registerUser(namePart, emailPart, passPart)
                .enqueue(object : Callback<RegisterResponse> {
                    override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            if (result?.status == true) {
                                Toast.makeText(this@SignupActivity, "Registered successfully", Toast.LENGTH_SHORT).show()

                                val sharedPref = getSharedPreferences("LoginSession", Context.MODE_PRIVATE)
                                with(sharedPref.edit()) {
                                    putBoolean("isLoggedIn", true)
                                    apply()
                                }

                                startActivity(Intent(this@SignupActivity, DashBoard::class.java))
                                finish()
                            } else {
                                Toast.makeText(this@SignupActivity, result?.message ?: "Registration failed", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@SignupActivity, "Server error", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        Toast.makeText(this@SignupActivity, "Failed: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }
    }
}
