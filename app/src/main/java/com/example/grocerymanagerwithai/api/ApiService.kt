package com.example.grocerymanagerwithai.api

import com.example.grocerymanagerwithai.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    /* ----------  USER ---------- */

    @Multipart
    @POST("register.php")
    fun registerUser(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login.php")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    /* ----------  PRODUCTS ---------- */

    @Multipart
    @POST("add_product.php")
    fun addProduct(
        @Part("product_name") name: RequestBody,
        @Part("quantity") qty: RequestBody,
        @Part("expiry_date") expiry: RequestBody,
        @Part image: MultipartBody.Part?,
        @Part("weekly_sold") weeklySold: RequestBody
    ): Call<GenericResponse>

    @Multipart
    @POST("edit_product.php")
    fun editProduct(
        @Part("id") id: RequestBody,
        @Part("product_name") productName: RequestBody,
        @Part("quantity") quantity: RequestBody,
        @Part("expiry_date") expiryDate: RequestBody,
        @Part("weekly_sold") weeklySold: RequestBody,
        @Part image: MultipartBody.Part?
    ): Call<GenericResponse>

    @GET("view_products.php")
    suspend fun getProducts(): ProductResponse

    /* ----------  DELETE PRODUCT ---------- */
    @GET("delete.php")
    fun deleteProduct(@Query("id") id: Int): Call<DeleteResponse>
    // Using GenericResponse so success/message will match
    // If your delete.php returns { "success": true, "message": "Deleted" }
    // you can also create a DeleteResponse model separately.

    /* ----------  STOCK PREDICTION ---------- */

    @GET("predict.php")
    suspend fun getStockPredictions(): StockPredictionResponse

    /* ----------  TOP SELLING PRODUCTS ---------- */

    @GET("top_selling.php")
    suspend fun getTopSellingProducts(): TopSellingResponse

    /* ----------  EXPIRY PRODUCTS (Calendar) ---------- */

    @GET("expiry_calendar.php")
    fun getExpiryProducts(
        @Query("date") date: String
    ): Call<List<ExpiryProduct>>
    /* ----------  CHANGE PASSWORD ---------- */

    @FormUrlEncoded
    @POST("change_password.php")
    fun changePassword(
        @Field("email") email: String,
        @Field("old_password") oldPassword: String,
        @Field("new_password") newPassword: String
    ): Call<GenericResponse>


}
