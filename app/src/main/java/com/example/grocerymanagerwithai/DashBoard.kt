package com.example.grocerymanagerwithai

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerymanagerwithai.adapter.TopSellingAdapter
import com.example.grocerymanagerwithai.api.RetrofitClient
import kotlinx.coroutines.launch

class DashBoard : AppCompatActivity() {

    private lateinit var addProduct: LinearLayout
    private lateinit var viewProducts: LinearLayout
    private lateinit var predictStock: LinearLayout
    private lateinit var settings: LinearLayout
    private lateinit var lowStockLayout: LinearLayout
    private lateinit var lowStockText: TextView
    private lateinit var faq: LinearLayout
    private lateinit var recyclerView: RecyclerView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dash_board)

        // Initialize views
        addProduct = findViewById(R.id.addProduct)
        viewProducts = findViewById(R.id.viewProducts)
        predictStock = findViewById(R.id.predictStock)
        settings = findViewById(R.id.settings)
        lowStockLayout = findViewById(R.id.lowStockText)
        lowStockText = findViewById(R.id.lowStockAlertText)
        faq = findViewById(R.id.faq)
        recyclerView = findViewById(R.id.topSellingRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Click listeners
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

        lowStockLayout.setOnClickListener {
            startActivity(Intent(this, Lowstock::class.java))
        }

        lowStockText.setOnClickListener {
            startActivity(Intent(this, Lowstock::class.java))
        }

        faq.setOnClickListener {
            startActivity(Intent(this, HelpFaq::class.java))
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
