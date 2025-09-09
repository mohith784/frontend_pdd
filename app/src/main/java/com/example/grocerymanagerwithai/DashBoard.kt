package com.example.grocerymanagerwithai

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerymanagerwithai.adapter.TopSellingAdapter
import com.example.grocerymanagerwithai.api.RetrofitClient
import kotlinx.coroutines.launch

class DashBoard : AppCompatActivity() {

    // Top section
    private lateinit var addProduct: MaterialCardView
    private lateinit var viewProducts: MaterialCardView
    private lateinit var predictStock: MaterialCardView
    private lateinit var settings: FloatingActionButton
    private lateinit var lowStockCard: MaterialCardView
    private lateinit var lowStockText: TextView
    private lateinit var faq: MaterialCardView
    private lateinit var recyclerView: RecyclerView

    // Bottom navigation
    private lateinit var bottomNavigationView: BottomNavigationView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dash_board)

        // Top section initialization
        addProduct = findViewById(R.id.addProduct)
        viewProducts = findViewById(R.id.viewProducts)
        predictStock = findViewById(R.id.predictStock)
        settings = findViewById(R.id.settings)
        lowStockCard = findViewById(R.id.lowStockCard)
        lowStockText = findViewById(R.id.lowStockAlertText)
        faq = findViewById(R.id.faq)
        recyclerView = findViewById(R.id.topSellingRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Bottom navigation initialization
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Top section click listeners
        addProduct.setOnClickListener {
            startActivity(Intent(this, AddProduct::class.java))
        }

        viewProducts.setOnClickListener {
            startActivity(Intent(this, MyProducts::class.java))
        }

        predictStock.setOnClickListener {
            startActivity(Intent(this, StockPredictt::class.java))
        }

        settings.setOnClickListener {
            startActivity(Intent(this, Settings::class.java))
        }

        lowStockCard.setOnClickListener {
            startActivity(Intent(this, Lowstock::class.java))
        }

        faq.setOnClickListener {
            startActivity(Intent(this, HelpFaq::class.java))
        }

        // Bottom navigation click listeners
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_calendar -> {
                    startActivity(Intent(this, ExpiryCalendarActivity::class.java))
                    true
                }
                R.id.nav_home -> {
                    // Already on home, do nothing or refresh
                    true
                }
                R.id.nav_shopping -> {
                    startActivity(Intent(this, ShoppingGeneratorActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // Fetch top selling products
        fetchTopSellingProducts()
    }

    private fun fetchTopSellingProducts() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getTopSellingProducts()
                if (response.status == "success") {
                    val productList = response.data
                    recyclerView.adapter = TopSellingAdapter(productList)
                } else {
                    Toast.makeText(this@DashBoard, "No top selling data", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DashBoard, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
