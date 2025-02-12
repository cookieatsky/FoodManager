package com.example.foodmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecipeAdapter(private val recipes: List<Recipe>) :
    RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val product: TextView = itemView.findViewById(R.id.productTextView)
        val servings: TextView = itemView.findViewById(R.id.servingsTextView)
        val steps: TextView = itemView.findViewById(R.id.stepsTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]

        // Формируем строку для всех продуктов
        val productNames = recipe.products.joinToString { "${it.product} (${it.quantity})" }
        holder.product.text = "Продукты: $productNames"
        holder.servings.text = "Количество порций: ${recipe.servings}"
        holder.steps.text = "Пошаговое приготовление: ${recipe.steps}"
    }

    override fun getItemCount(): Int {
        return recipes.size
    }
}