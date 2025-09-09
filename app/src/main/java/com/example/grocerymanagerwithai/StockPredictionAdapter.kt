package com.example.grocerymanagerwithai.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.grocerymanagerwithai.R
import com.example.grocerymanagerwithai.model.StockPrediction

class StockPredictionAdapter(
    private val predictionList: List<StockPrediction>
) : RecyclerView.Adapter<StockPredictionAdapter.PredictionViewHolder>() {

    companion object {
        // Use your Retrofit base URL
        private const val BASE_URL = "https://qzg134r4-80.inc1.devtunnels.ms/appp/"
    }

    inner class PredictionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productNameText)
        val quantity: TextView = itemView.findViewById(R.id.quantityText)
        val weeklySold: TextView = itemView.findViewById(R.id.weeklySoldText)
        val weeksLeft: TextView = itemView.findViewById(R.id.weeksLeftText)
        val status: TextView = itemView.findViewById(R.id.statusText)
        val quantityToBuy: TextView = itemView.findViewById(R.id.quantityToBuyText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredictionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return PredictionViewHolder(view)
    }

    override fun onBindViewHolder(holder: PredictionViewHolder, position: Int) {
        val item = predictionList[position]
        holder.productName.text = item.product
        holder.quantity.text = "Quantity: ${item.quantity}"
        holder.weeklySold.text = "Weekly Sold: ${item.weekly_sold}"
        holder.weeksLeft.text = "Weeks Left: ${item.weeks_left}"
        holder.status.text = "Status: ${item.status}"
        holder.quantityToBuy.text = "To Buy: ${item.quantity_to_buy}"

        // If backend returns just "uploads/xyz.jpg", append BASE_URL
        val imageUrl = if (item.image_path.startsWith("http")) {
            item.image_path
        } else {
            BASE_URL + item.image_path
        }

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_done_green)
            .error(R.drawable.ic_done_green) // show error image if load fails
            .into(holder.productImage)
    }

    override fun getItemCount(): Int = predictionList.size
}
