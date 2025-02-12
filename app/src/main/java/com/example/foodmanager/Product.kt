package com.example.foodmanager

data class Product(
    val id: Int,
    val name: String,
    val unit: String,
    val proteins: Float,
    val carbs: Float,
    val fats: Float,
    val calories: Float
)