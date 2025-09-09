package com.example.grocerymanagerwithai.model

data class StockPredictionResponse(
    val status: String,
    val data: List<StockPrediction>
)
