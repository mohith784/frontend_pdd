package com.example.grocerymanagerwithai.model
import java.io.Serializable


data class ProductResponse(
    val status: String,
    val data: List<Product>?,
    val message: String? = null
)

data class Product(
    val id: String,
    val product_name: String,
    val quantity: String,
    val expiry_date: String,
    val weekly_sold: String,
    val image_path: String
) : Serializable
