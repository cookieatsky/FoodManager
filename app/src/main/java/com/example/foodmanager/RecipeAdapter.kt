package com.example.foodmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecipeAdapter(private val recipes: List<Recipe>) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.titleTextView) // Заголовок рецепта
        val servings: TextView = itemView.findViewById(R.id.servingsTextView) // Количество порций
        val steps: TextView = itemView.findViewById(R.id.stepsTextView) // Пошаговые инструкции
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]

        holder.title.text = recipe.title // Отображаем заголовок
        holder.servings.text = "Количество порций: ${recipe.servings}" // Отображаем количество порций
        holder.steps.text = "Пошаговое приготовление: ${recipe.steps}" // Отображаем пошаговые инструкции

        // Формируем строку для всех продуктов
        val productNames = recipe.products.joinToString { "${it.product} (${it.quantity})" }
        holder.steps.text = holder.steps.text.toString() + "\nПродукты: $productNames" // Отображаем продукты
    }

    override fun getItemCount(): Int {
        return recipes.size
    }
}