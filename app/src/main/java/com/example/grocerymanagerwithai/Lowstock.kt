package com.example.grocerymanagerwithai

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerymanagerwithai.adapter.ProductAdapter
import com.example.grocerymanagerwithai.api.RetrofitClient
import com.example.grocerymanagerwithai.model.Product
import kotlinx.coroutines.launch

class Lowstock : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private var lowStockList: MutableList<Product> = mutableListOf()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lowstock)

        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

        // Check if Low Stock Alert is enabled
        val isLowStockEnabled = sharedPreferences.getBoolean("LOW_STOCK", true)
        if (!isLowStockEnabled) {
            Toast.makeText(this, "Low Stock Alerts are turned off in settings", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Get the threshold value (default to 5)
        val threshold = sharedPreferences.getInt("low_stock_threshold", 5)

        recyclerView = findViewById(R.id.lowStockRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        productAdapter = ProductAdapter(
            lowStockList,
            onEditClick = { product -> /* handle edit */ },
            onDeleteClick = { product -> /* handle delete */ }
        )
        recyclerView.adapter = productAdapter

        fetchLowStockItems(threshold)
    }

    private fun fetchLowStockItems(threshold: Int) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getProducts()
                if (response.status == "success" && response.data != null) {
                    val allProducts = response.data

                    val filtered = allProducts.filter {
                        (it.quantity.toIntOrNull() ?: 0) < threshold
                    }

                    lowStockList.clear()
                    lowStockList.addAll(filtered)
                    productAdapter.notifyDataSetChanged()

                    if (filtered.isEmpty()) {
                        Toast.makeText(this@Lowstock, "All items are sufficiently stocked", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@Lowstock, "Failed to load items", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Lowstock, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
