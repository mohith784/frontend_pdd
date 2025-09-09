package com.example.grocerymanagerwithai.model

import com.google.gson.annotations.SerializedName

data class ExpiryProduct(

@SerializedName("product_name")
val productName: String,

@SerializedName("expiry_date")
val expiryDate: String,  // non-nullable String

@SerializedName("image_path")
val imagePath: String? = null
)