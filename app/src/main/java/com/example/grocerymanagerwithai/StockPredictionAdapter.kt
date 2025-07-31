package com.example.grocerymanagerwithai.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerymanagerwithai.R
import com.example.grocerymanagerwithai.model.StockPrediction

class StockPredictionAdapter(
    private val predictionList: List<StockPrediction>
) : RecyclerView.Adapter<StockPredictionAdapter.PredictionViewHolder>() {

    inner class PredictionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
    }

    override fun getItemCount(): Int = predictionList.size
}
