package com.example.foodmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(private val categories: List<Category>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryText: TextView = itemView.findViewById(R.id.categoryTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]

        // Получаем отступ на основе родительского ID
        val indentation = getIndentation(category.parentId)
        holder.categoryText.text = "$indentation${category.name}" // Устанавливаем текст с отступом
    }

    override fun getItemCount(): Int = categories.size

    // Функция для получения уровня вложенности и соответствующих отступов
    private fun getIndentation(parentId: Int?): String {
        var level = 0
        var currentCategoryId: Int? = parentId // Используем переменную для хранения ID родителя

        while (currentCategoryId != null) {
            val currentCategory = categories.find { it.id == currentCategoryId } // Ищем текущую категорию по ID

            if (currentCategory != null) {
                level++ // Увеличиваем уровень вложенности
                currentCategoryId = currentCategory.parentId // Переходим к родительской категории
            } else {
                currentCategoryId = null // Если категория не найдена, завершаем цикл
            }
        }

        return "-".repeat(level) // Генерируем строку с символами "-" в зависимости от уровня вложенности
    }
}