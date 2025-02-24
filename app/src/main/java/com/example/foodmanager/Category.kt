package com.example.foodmanager

data class Category(
    val id: Int,
    val name: String,
    val parentId: Int?, // ID родительской категории
    val subcategories: MutableList<Category> = mutableListOf() // Подкатегории
)