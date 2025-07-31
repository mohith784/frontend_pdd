import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.grocerymanagerwithai.R
import com.example.grocerymanagerwithai.api.RetrofitClient
import kotlinx.coroutines.launch

class ProductActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_products) // your XML file

        fetchProducts()
    }

    private fun fetchProducts() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getProducts()

                if (response.status == "success" && response.data != null) {
                    for (product in response.data) {
                        // You can bind to RecyclerView or display in Toast for testing
                        println("Product: ${product.product_name}, Quantity: ${product.quantity}")
                    }
                } else {
                    Toast.makeText(this@ProductActivity, response.message ?: "No products", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@ProductActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
