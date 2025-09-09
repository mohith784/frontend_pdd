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
import com.example.grocerymanagerwithai.api.RetrofitClient
import com.example.grocerymanagerwithai.model.GenericResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class AddProduct : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etQty: EditText
    private lateinit var etExpiry: EditText
    private lateinit var etWeeklySold: EditText
    private lateinit var btnSave: Button
    private lateinit var imgProduct: ImageView
    private var imageUri: Uri? = null
    private var imageFile: File? = null   // store converted file

    private val PICK_IMAGE_REQUEST = 1
    private val PERMISSION_REQUEST_CODE = 123

    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), value)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        etName = findViewById(R.id.etName)
        etQty = findViewById(R.id.etQty)
        etExpiry = findViewById(R.id.etExpiry)
        etWeeklySold = findViewById(R.id.etWeeklySold)
        btnSave = findViewById(R.id.btnsave)
        imgProduct = findViewById(R.id.imgProduct)

        checkStoragePermission()

        imgProduct.setOnClickListener {
            if (hasStoragePermission()) {
                openImagePicker()
            } else {
                checkStoragePermission()
            }
        }

        etExpiry.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, y, m, d ->
                val formattedDate = String.format("%04d-%02d-%02d", y, m + 1, d)
                etExpiry.setText(formattedDate)
            }, year, month, day)

            datePicker.show()
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val qty = etQty.text.toString().toIntOrNull() ?: 0
            val exp = etExpiry.text.toString().trim()
            val weeklySold = etWeeklySold.text.toString().trim()

            if (name.isEmpty() || exp.isEmpty() || weeklySold.isEmpty() || qty == 0) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nameBody = toRequestBody(name)
            val qtyBody = toRequestBody(qty.toString())
            val expiryBody = toRequestBody(exp)
            val weeklySoldBody = toRequestBody(weeklySold)

            val imagePart = imageFile?.let { file ->
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", file.name, requestFile)
            }

            RetrofitClient.instance.addProduct(
                nameBody,
                qtyBody,
                expiryBody,
                imagePart,
                weeklySoldBody
            ).enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    val body = response.body()
                    if (body?.success == true) {
                        Toast.makeText(this@AddProduct, body.message, Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@AddProduct,
                            body?.message ?: "Server error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AddProduct,
                        "Network error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun hasStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun checkStoragePermission() {
        if (!hasStoragePermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission denied. Cannot select image.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data?.data != null) {
            imageUri = data.data
            imgProduct.setImageURI(imageUri)

            // Convert URI to File for upload
            imageUri?.let {
                imageFile = uriToFile(it)
            }
        }
    }

    // Convert Uri -> File (works for both gallery & camera)
    private fun uriToFile(uri: Uri): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("upload_", ".jpg", cacheDir)
            FileOutputStream(tempFile).use { output ->
                inputStream.copyTo(output)
            }
            tempFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
