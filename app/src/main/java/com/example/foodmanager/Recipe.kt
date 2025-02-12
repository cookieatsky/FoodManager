package com.example.foodmanager

data class Recipe(
    val id: Int,
    val product: String,
    val servings: Int,
    val steps: String,
    val image: ByteArray? // если вы планируете хранить изображение
)

