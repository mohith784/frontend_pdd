package com.example.grocerymanagerwithai

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.grocerymanagerwithai.model.ExpiryProduct
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(
    private val productList: MutableList<ExpiryProduct> // mutable list for updating data
) : RecyclerView.Adapter<CalendarAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.txtProductName)
        val expiry: TextView = itemView.findViewById(R.id.txtExpiryDate)
        val image: ImageView = itemView.findViewById(R.id.imgProduct)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.name.text = product.productName
        holder.expiry.text = "Expires: ${product.expiryDate}"

        // Calculate days left and decide color for expiry text
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val expiryDate = sdf.parse(product.expiryDate)
            if (expiryDate != null) {
                val today = Calendar.getInstance().time
                val diffInMillis = expiryDate.time - today.time
                val daysLeft = (diffInMillis / (1000L * 60 * 60 * 24)).toInt()
                when {
                    daysLeft < 0 -> {
                        // expired - grey color
                        holder.expiry.setTextColor(Color.parseColor("#9E9E9E"))
                    }
                    daysLeft <= 7 -> {
                        // expiring soon - red color
                        holder.expiry.setTextColor(Color.RED)
                    }
                    else -> {
                        // normal - black color
                        holder.expiry.setTextColor(Color.BLACK)
                    }
                }
            } else {
                holder.expiry.setTextColor(Color.BLACK)
            }
        } catch (e: Exception) {
            holder.expiry.setTextColor(Color.BLACK)
        }

        // Load product image if available, else show placeholder
        if (!product.imagePath.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(product.imagePath)
                .into(holder.image)
        } else {
            holder.image.setImageResource(android.R.drawable.ic_menu_gallery)
        }
    }

    /**
     * Replace current list with a new list and refresh RecyclerView
     */
    fun updateList(newList: List<ExpiryProduct>) {
        productList.clear()
        productList.addAll(newList)
        notifyDataSetChanged()
    }
}