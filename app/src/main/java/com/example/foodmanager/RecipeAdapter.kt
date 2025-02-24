package com.example.foodmanager

import android.content.Intent
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

        holder.title.text = recipe.title
        holder.servings.text = "Количество порций: ${recipe.servings}"

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, RecipeDetailActivity::class.java)
            intent.putExtra("RECIPE_ID", recipe.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return recipes.size
    }
}