package com.example.grocerymanagerwithai.model

data class StockPrediction(
    val product: String,
    val quantity: Int,
    val weekly_sold: Int,
    val weeks_left: Double,
    val status: String,
    val quantity_to_buy: Int
)
