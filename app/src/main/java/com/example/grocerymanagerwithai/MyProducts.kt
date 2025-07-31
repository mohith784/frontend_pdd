package com.example.grocerymanagerwithai

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerymanagerwithai.api.RetrofitClient
import com.example.grocerymanagerwithai.adapter.ProductAdapter
import com.example.grocerymanagerwithai.model.Product
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MyProducts : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter

    private lateinit var totalProductsText: TextView
    private lateinit var lowStockText: TextView
    private lateinit var expiringSoonText: TextView

    private var productList: MutableList<Product> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_products)

        recyclerView = findViewById(R.id.productRecyclerView)
        totalProductsText = findViewById(R.id.totalProductsText)
        lowStockText = findViewById(R.id.lowStockText)
        expiringSoonText = findViewById(R.id.expiringSoonText)

        recyclerView.layoutManager = LinearLayoutManager(this)

        productAdapter = ProductAdapter(
            productList,
            onEditClick = { product ->
                Toast.makeText(this, "Edit: ${product.product_name}", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { product ->
                productList.remove(product)
                productAdapter.notifyDataSetChanged()
                updateSummary()
            }
        )

        recyclerView.adapter = productAdapter
        fetchProducts()
    }

    private fun fetchProducts() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getProducts()
                if (response.status == "success" && response.data != null) {
                    productList.clear()
                    productList.addAll(response.data)
                    productAdapter.notifyDataSetChanged()
                    updateSummary()
                } else {
                    Toast.makeText(
                        this@MyProducts,
                        response.message ?: "No products found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("MyProducts", "Error fetching products", e)
                Toast.makeText(this@MyProducts, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateSummary() {
        totalProductsText.text = productList.size.toString()

        // Count low stock (quantity < 5)
        val lowStockCount = productList.count { it.quantity.toIntOrNull() ?: 0 < 5 }
        lowStockText.text = lowStockCount.toString()

        // Optional: Count expiring soon (within 7 days)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val thresholdDate = calendar.time

        val expiringSoonCount = productList.count {
            try {
                val expiryDate = dateFormat.parse(it.expiry_date ?: "")
                expiryDate != null && expiryDate.before(thresholdDate)
            } catch (e: Exception) {
                false
            }
        }
        expiringSoonText.text = expiringSoonCount.toString()
    }
}
