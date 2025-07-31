package com.example.grocerymanagerwithai.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerymanagerwithai.R
import com.example.grocerymanagerwithai.model.TopSellingProduct

class TopSellingAdapter(private val productList: List<TopSellingProduct>) :
    RecyclerView.Adapter<TopSellingAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.productName)
        val sold: TextView = itemView.findViewById(R.id.weeklySold)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_top_selling, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = productList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]
        holder.name.text = product.product_name
        holder.sold.text = "Sold: ${product.weekly_sold}"
    }
}
