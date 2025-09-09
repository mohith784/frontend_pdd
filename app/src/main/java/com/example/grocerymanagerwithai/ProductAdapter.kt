package com.example.grocerymanagerwithai.adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.grocerymanagerwithai.EditProductt
import com.example.grocerymanagerwithai.R
import com.example.grocerymanagerwithai.model.Product
import java.text.SimpleDateFormat
import java.util.*

class ProductAdapter(
    private val products: List<Product>,
    private val onEditClick: ((Product) -> Unit)? = null,
    private val onDeleteClick: ((Product) -> Unit)? = null,
    private val showControls: Boolean = true
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var alertShown = false

    companion object {
        private const val BASE_URL = "https://qzg134r4-80.inc1.devtunnels.ms/appp/"
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productNameText)
        val quantity: TextView = itemView.findViewById(R.id.quantityText)
        val expiryDate: TextView = itemView.findViewById(R.id.expiryDateText)
        val editButton: ImageView = itemView.findViewById(R.id.editButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)

        if (!alertShown && products.size < 3 && showControls) {
            Toast.makeText(
                parent.context,
                "Alert: Only ${products.size} product(s) in stock!",
                Toast.LENGTH_LONG
            ).show()
            alertShown = true
        }

        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        holder.productName.text = product.product_name
        holder.quantity.text = "Quantity: ${product.quantity}"

        // Format and display expiry date
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val expiryDateText = try {
            val date = inputFormat.parse(product.expiry_date)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            product.expiry_date
        }
        holder.expiryDate.text = "Expiry: $expiryDateText"

        // 🔴🟠 Expiry Color Logic
        try {
            val expiryDate = inputFormat.parse(product.expiry_date)
            val today = Date()

            val calendar = Calendar.getInstance()
            calendar.time = today
            calendar.add(Calendar.DAY_OF_YEAR, 7)
            val sevenDaysFromNow = calendar.time

            when {
                expiryDate != null && expiryDate.before(today) -> {
                    holder.itemView.setBackgroundColor(Color.parseColor("#FFCDD2")) // light red
                }
                expiryDate != null && expiryDate.before(sevenDaysFromNow) -> {
                    holder.itemView.setBackgroundColor(Color.parseColor("#FFE0B2")) // light orange
                }
                else -> {
                    holder.itemView.setBackgroundColor(Color.WHITE)
                }
            }
        } catch (e: Exception) {
            holder.itemView.setBackgroundColor(Color.WHITE) // fallback
        }

        // Load product image
        val imageUrl = BASE_URL + product.image_path
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_done_green)
            .error(R.drawable.ic_done_green)
            .into(holder.productImage)

        // Show or hide edit/delete buttons
        if (showControls) {
            holder.editButton.visibility = View.VISIBLE
            holder.deleteButton.visibility = View.VISIBLE

            holder.editButton.setOnClickListener {
                val context = holder.itemView.context
                val intent = Intent(context, EditProductt::class.java)
                intent.putExtra("product", product) // ✅ Send Product
                context.startActivity(intent)
            }

            holder.deleteButton.setOnClickListener {
                onDeleteClick?.invoke(product)
            }
        } else {
            holder.editButton.visibility = View.GONE
            holder.deleteButton.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = products.size
}
