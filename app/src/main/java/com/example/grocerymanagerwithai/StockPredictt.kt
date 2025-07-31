package com.example.grocerymanagerwithai

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerymanagerwithai.adapter.StockPredictionAdapter
import com.example.grocerymanagerwithai.api.RetrofitClient
import com.example.grocerymanagerwithai.model.StockPrediction
import kotlinx.coroutines.launch

class StockPredictt : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var backButton: ImageButton
    private lateinit var refreshButton: ImageButton
    private lateinit var adapter: StockPredictionAdapter
    private val predictionList = mutableListOf<StockPrediction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_predict)

        recyclerView = findViewById(R.id.stockRecyclerView)
        backButton = findViewById(R.id.backButton)
        refreshButton = findViewById(R.id.refreshButton)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StockPredictionAdapter(predictionList)
        recyclerView.adapter = adapter

        backButton.setOnClickListener {
            finish()
        }

        refreshButton.setOnClickListener {
            fetchPredictions()
        }

        fetchPredictions()
    }

    private fun fetchPredictions() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getStockPrediction()
                predictionList.clear()
                predictionList.addAll(response)
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Toast.makeText(this@StockPredictt, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
