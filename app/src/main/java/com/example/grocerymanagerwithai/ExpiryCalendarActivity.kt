package com.example.grocerymanagerwithai

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerymanagerwithai.api.RetrofitClient
import com.example.grocerymanagerwithai.model.ExpiryProduct
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ExpiryCalendarActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var tvSelectedDate: TextView
    private lateinit var rvExpiryProducts: RecyclerView
    private lateinit var adapter: CalendarAdapter
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private lateinit var toggleTop: ToggleButton

    // Bottom menu toggles
    private lateinit var toggle1: LinearLayout   // Calendar
    private lateinit var toggle2: LinearLayout   // Home/Dashboard
    private lateinit var toggle3: LinearLayout   // Shopping

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar) // XML must include bottom menu

        // Initialize views
        calendarView = findViewById(R.id.calendarView)
        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        rvExpiryProducts = findViewById(R.id.rvExpiryProducts)
        toggleTop = findViewById(R.id.toggleTop)

        // Bottom menu
        toggle1 = findViewById(R.id.toggle1)
        toggle2 = findViewById(R.id.toggle2)
        toggle3 = findViewById(R.id.toggle3)

        // RecyclerView setup
        adapter = CalendarAdapter(mutableListOf())
        rvExpiryProducts.layoutManager = LinearLayoutManager(this)
        rvExpiryProducts.adapter = adapter

        // Show today's products
        val todayStr = sdf.format(Date(calendarView.date))
        tvSelectedDate.text = "Selected: $todayStr"
        fetchProductsForDate(todayStr)

        // Calendar date change
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            tvSelectedDate.text = "Selected: $selectedDate"
            fetchProductsForDate(selectedDate)
        }

        // Toggle button action
        toggleTop.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Calendar ON", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Calendar OFF", Toast.LENGTH_SHORT).show()
            }
        }

        // Bottom menu click listeners
        toggle1.setOnClickListener {
            // Already in calendar
            Toast.makeText(this, "Calendar Selected", Toast.LENGTH_SHORT).show()
        }

        toggle2.setOnClickListener {
            // Go to Dashboard
            startActivity(Intent(this, DashBoard::class.java))
            finish()
        }

        toggle3.setOnClickListener {
            // Open Shopping Activity
            startActivity(Intent(this, ShoppingGeneratorActivity::class.java))
        }
    }

    private fun fetchProductsForDate(date: String) {
        RetrofitClient.instance.getExpiryProducts(date)
            .enqueue(object : Callback<List<ExpiryProduct>> {
                override fun onResponse(
                    call: Call<List<ExpiryProduct>>,
                    response: Response<List<ExpiryProduct>>
                ) {
                    if (response.isSuccessful) {
                        val products = response.body() ?: emptyList()
                        adapter.updateList(products)
                        if (products.isEmpty()) {
                            Toast.makeText(
                                this@ExpiryCalendarActivity,
                                "No products expiring on $date",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@ExpiryCalendarActivity,
                            "Failed to load data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<ExpiryProduct>>, t: Throwable) {
                    Toast.makeText(
                        this@ExpiryCalendarActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
