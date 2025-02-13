package com.example.foodmanager

data class Recipe(
    val id: Int,
    val title: String, // Поле для заголовка рецепта
    val products: List<ProductQuantity>, // Список продуктов и их количеств
    val servings: Int,
    val steps: String,
    val image: ByteArray? // Данные изображения
)

data class ProductQuantity(
    val product: String,
    val quantity: Int // Количество продукта
)

