package com.example.grocerymanagerwithai.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false) // ✅ Using item.xml now

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

        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val formattedDate = try {
            val date = inputFormat.parse(product.expiry_date)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            product.expiry_date
        }
        holder.expiryDate.text = "Expiry: $formattedDate"

        val imageUrl = BASE_URL  + product.image_path

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_done_green)
            .error(R.drawable.ic_done_green)
            .into(holder.productImage)

        if (showControls) {
            holder.editButton.visibility = View.VISIBLE
            holder.deleteButton.visibility = View.VISIBLE
            holder.editButton.setOnClickListener { onEditClick?.invoke(product) }
            holder.deleteButton.setOnClickListener { onDeleteClick?.invoke(product) }
        } else {
            holder.editButton.visibility = View.GONE
            holder.deleteButton.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = products.size
}
