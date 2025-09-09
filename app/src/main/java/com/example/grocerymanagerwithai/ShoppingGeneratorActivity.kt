package com.example.grocerymanagerwithai

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.grocerymanagerwithai.api.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ShoppingGeneratorActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var btnClearList: Button
    private lateinit var adapter: ArrayAdapter<String>
    private val shoppingList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping)

        listView = findViewById(R.id.lvShoppingList)
        btnClearList = findViewById(R.id.btnClearList)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, shoppingList)
        listView.adapter = adapter

        fetchProductsFromViewProducts()

        btnClearList.setOnClickListener {
            shoppingList.clear()
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "Shopping list cleared", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchProductsFromViewProducts() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getProducts()
                if (response.status == "success" && response.data != null) {
                    shoppingList.clear()
                    shoppingList.addAll(response.data.map { product ->
                        "${product.product_name} - Qty: ${product.quantity}"
                    })
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        this@ShoppingGeneratorActivity,
                        response.message ?: "No products found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IOException) {
                Toast.makeText(this@ShoppingGeneratorActivity, "Network error", Toast.LENGTH_SHORT).show()
            } catch (e: HttpException) {
                Toast.makeText(this@ShoppingGeneratorActivity, "Server error", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@ShoppingGeneratorActivity, "Unexpected error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}