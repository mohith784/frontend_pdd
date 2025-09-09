package com.example.grocerymanagerwithai.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.grocerymanagerwithai.R
import com.example.grocerymanagerwithai.model.Product

class ShoppingAdapter(
    private val productList: List<Product>
) : RecyclerView.Adapter<ShoppingAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProductImage: ImageView = itemView.findViewById(R.id.ivProductImage)
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val tvExpiry: TextView = itemView.findViewById(R.id.tvExpiry)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shopping, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.tvProductName.text = product.product_name
        holder.tvQuantity.text = "Quantity: ${product.quantity}"
        holder.tvExpiry.text = "Expiry: ${product.expiry_date}"

        Glide.with(holder.itemView.context)
            .load(product.image_path)
            .placeholder(R.drawable.ic_done_green) // Add placeholder in drawable
            .into(holder.ivProductImage)
    }

    override fun getItemCount(): Int = productList.size
}