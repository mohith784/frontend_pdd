package com.example.grocerymanagerwithai

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.grocerymanagerwithai.api.RetrofitClient
import com.example.grocerymanagerwithai.model.GenericResponse
import com.example.grocerymanagerwithai.model.Product
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

class EditProductt : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etQty: EditText
    private lateinit var etExpiry: EditText
    private lateinit var etWeeklySold: EditText
    private lateinit var btnUpdate: Button
    private lateinit var imgProduct: ImageView
    private lateinit var tvCancel: TextView

    private var imageUri: Uri? = null
    private var productId: Int = -1

    private val PICK_IMAGE_REQUEST = 1
    private val PERMISSION_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_productt)

        // Initialize views
        etName = findViewById(R.id.etName)
        etQty = findViewById(R.id.etQty)
        etExpiry = findViewById(R.id.etExpiry)
        etWeeklySold = findViewById(R.id.etWeeklySold)
        btnUpdate = findViewById(R.id.btnUpdate)
        imgProduct = findViewById(R.id.imgProduct)
        tvCancel = findViewById(R.id.tvCancel)

        checkStoragePermission()

        // Populate existing product data
        val product = intent.getSerializableExtra("product") as? Product
        if (product != null) {
            productId = product.id.toString().toIntOrNull() ?: -1
            etName.setText(product.product_name)
            etQty.setText(product.quantity)
            etExpiry.setText(product.expiry_date)
            etWeeklySold.setText(product.weekly_sold)

            // Load image if available
            val imageUrl = "https://qzg134r4-80.inc1.devtunnels.ms/appp/${product.image_path}"
            Glide.with(this).load(imageUrl).into(imgProduct)
        } else {
            Toast.makeText(this, "Failed to load product data", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Image picker
        imgProduct.setOnClickListener {
            if (hasStoragePermission()) openImagePicker() else checkStoragePermission()
        }

        // Expiry date picker
        etExpiry.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                { _, y, m, d ->
                    etExpiry.setText(String.format("%04d-%02d-%02d", y, m + 1, d))
                },
                year, month, day
            )
            datePicker.show()
        }

        tvCancel.setOnClickListener { finish() }

        btnUpdate.setOnClickListener {
            updateProduct()
        }
    }

    private fun updateProduct() {
        val name = etName.text.toString().trim()
        val qty = etQty.text.toString().trim()
        val expiry = etExpiry.text.toString().trim()
        val weeklySold = etWeeklySold.text.toString().trim()

        if (name.isEmpty() || qty.isEmpty() || expiry.isEmpty() || weeklySold.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val qtyBody = qty.toRequestBody("text/plain".toMediaTypeOrNull())
        val expiryBody = expiry.toRequestBody("text/plain".toMediaTypeOrNull())
        val weeklySoldBody = weeklySold.toRequestBody("text/plain".toMediaTypeOrNull())
        val idBody = productId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val imagePart = imageUri?.let { uri ->
            val filePath = getRealPathFromURI(uri)
            filePath?.let {
                val file = File(it)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", file.name, requestFile)
            }
        }

        RetrofitClient.instance.editProduct(
            idBody, nameBody, qtyBody, expiryBody, weeklySoldBody, imagePart
        ).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@EditProductt, response.body()?.message ?: "Updated", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditProductt, response.body()?.message ?: "Update failed", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Toast.makeText(this@EditProductt, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun hasStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun checkStoragePermission() {
        if (!hasStoragePermission()) {
            val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
            else
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data?.data != null) {
            imageUri = data.data
            imgProduct.setImageURI(imageUri)
        }
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex)
            }
        }
        return null
    }
}
