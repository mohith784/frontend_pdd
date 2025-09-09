package com.example.grocerymanagerwithai

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerymanagerwithai.adapter.ProductAdapter
import com.example.grocerymanagerwithai.api.RetrofitClient
import com.example.grocerymanagerwithai.model.DeleteResponse
import com.example.grocerymanagerwithai.model.Product
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MyProducts : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter

    private lateinit var searchView: SearchView

    private lateinit var totalProductsCard: LinearLayout
    private lateinit var expiredProductsCard: LinearLayout
    private lateinit var expiringSoonCard: LinearLayout

    private lateinit var totalProductsText: TextView
    private lateinit var expiredProductsText: TextView
    private lateinit var expiringSoonText: TextView

    private val productList: MutableList<Product> = mutableListOf()
    private val originalList: MutableList<Product> = mutableListOf()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_products)

        // Top bar buttons
        findViewById<ImageButton>(R.id.backButton).setOnClickListener { finish() }
        findViewById<ImageButton>(R.id.refreshButton).setOnClickListener { fetchProducts() }

        searchView = findViewById(R.id.searchView)

        recyclerView = findViewById(R.id.productRecyclerView)

        totalProductsCard = findViewById(R.id.totalProductsCard)
        expiredProductsCard = findViewById(R.id.expiredProductsCard)
        expiringSoonCard = findViewById(R.id.expiringSoonCard)

        totalProductsText = findViewById(R.id.totalProductsText)
        expiredProductsText = findViewById(R.id.expiredText)
        expiringSoonText = findViewById(R.id.expiringSoonText)

        // RecyclerView setup
        recyclerView.layoutManager = LinearLayoutManager(this)
        productAdapter = ProductAdapter(
            productList,
            onEditClick = { product ->
                Toast.makeText(this, "Edit: ${product.product_name}", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { product ->
                showDeleteConfirmation(product)
            }
        )
        recyclerView.adapter = productAdapter

        // Card clicks
        totalProductsCard.setOnClickListener { showAllProducts() }
        expiredProductsCard.setOnClickListener { showExpired() }
        expiringSoonCard.setOnClickListener { showExpiringSoon() }

        // Search
        setupSearch()

        // Load products
        fetchProducts()
    }

    // -------- Search --------
    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterProducts(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterProducts(newText)
                return true
            }
        })
    }

    private fun filterProducts(query: String?) {
        if (query.isNullOrBlank()) {
            productList.clear()
            productList.addAll(originalList)
        } else {
            productList.clear()
            productList.addAll(
                originalList.filter { it.product_name.contains(query, ignoreCase = true) }
            )
        }
        productAdapter.notifyDataSetChanged()
    }

    // -------- Fetch products --------
    private fun fetchProducts() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getProducts()
                if (response.status == "success" && response.data != null) {
                    originalList.clear()
                    originalList.addAll(response.data)

                    productList.clear()
                    productList.addAll(originalList)
                    productAdapter.notifyDataSetChanged()

                    updateSummary()
                } else {
                    Toast.makeText(this@MyProducts, response.message ?: "No products found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("MyProducts", "Error fetching products", e)
                Toast.makeText(this@MyProducts, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // -------- Summary cards --------
    private fun updateSummary() {
        totalProductsText.text = originalList.size.toString()

        val today = todayDate()
        val thresholdDate = daysFromToday(7)

        val expiringSoonCount = originalList.count { product ->
            val d = parseDate(product.expiry_date)
            d != null && d.after(today) && d.before(thresholdDate)
        }
        expiringSoonText.text = expiringSoonCount.toString()

        val expiredCount = originalList.count { product ->
            val d = parseDate(product.expiry_date)
            d != null && d.before(today)
        }
        expiredProductsText.text = expiredCount.toString()
    }

    // -------- Filters --------
    private fun showAllProducts() {
        productList.clear()
        productList.addAll(originalList)
        productAdapter.notifyDataSetChanged()
        Toast.makeText(this, "Showing all products", Toast.LENGTH_SHORT).show()
    }

    private fun showExpiringSoon() {
        val today = todayDate()
        val thresholdDate = daysFromToday(7)
        productList.clear()
        productList.addAll(
            originalList.filter { product ->
                val d = parseDate(product.expiry_date)
                d != null && d.after(today) && d.before(thresholdDate)
            }
        )
        productAdapter.notifyDataSetChanged()
        Toast.makeText(this, "Showing expiring soon products", Toast.LENGTH_SHORT).show()
    }

    private fun showExpired() {
        val today = todayDate()
        productList.clear()
        productList.addAll(
            originalList.filter { product ->
                val d = parseDate(product.expiry_date)
                d != null && d.before(today)
            }
        )
        productAdapter.notifyDataSetChanged()
        Toast.makeText(this, "Showing expired products", Toast.LENGTH_SHORT).show()
    }

    // -------- Delete product --------
    private fun showDeleteConfirmation(product: Product) {
        AlertDialog.Builder(this)
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete ${product.product_name}?")
            .setPositiveButton("Yes") { _, _ -> deleteProduct(product) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteProduct(product: Product) {
        val productId = product.id.toInt() // convert if your id is String
        val call = RetrofitClient.instance.deleteProduct(productId)

        call.enqueue(object : Callback<DeleteResponse> {
            override fun onResponse(call: Call<DeleteResponse>, response: Response<DeleteResponse>) {
                val body = response.body()
                if (response.isSuccessful && body != null && body.success) {
                    originalList.remove(product)
                    productList.remove(product)
                    productAdapter.notifyDataSetChanged()
                    updateSummary()
                    Toast.makeText(this@MyProducts, "Deleted successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MyProducts, body?.message ?: "Failed to delete", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                Toast.makeText(this@MyProducts, "Error deleting product", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // -------- Date helpers --------
    private fun parseDate(dateStr: String?): Date? {
        if (dateStr.isNullOrBlank()) return null
        return try { dateFormat.parse(dateStr) } catch (_: Exception) { null }
    }

    private fun todayDate(): Date = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time

    private fun daysFromToday(days: Int): Date = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_YEAR, days)
    }.time
}
