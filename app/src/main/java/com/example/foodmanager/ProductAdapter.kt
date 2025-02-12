package com.example.foodmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(private val products: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.productNameTextView)
        val unit: TextView = itemView.findViewById(R.id.unitTextView)
        val proteins: TextView = itemView.findViewById(R.id.proteinsTextView)
        val carbs: TextView = itemView.findViewById(R.id.carbsTextView)
        val fats: TextView = itemView.findViewById(R.id.fatsTextView)
        val calories: TextView = itemView.findViewById(R.id.caloriesTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.name.text = product.name
        holder.unit.text = product.unit
        holder.proteins.text = "Белки: ${product.proteins}"
        holder.carbs.text = "Углеводы: ${product.carbs}"
        holder.fats.text = "Жиры: ${product.fats}"
        holder.calories.text = "Калории: ${product.calories}"
    }

    override fun getItemCount(): Int {
        return products.size
    }
}