package com.example.foodmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CreateRecipeActivity : AppCompatActivity() {
    private lateinit var titleInput: EditText
    private lateinit var servingsInput: EditText
    private lateinit var stepsInput: EditText
    private lateinit var productsContainer: LinearLayout

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_recipes)

        databaseHelper = DatabaseHelper(this)

        // Инициализация полей ввода
        titleInput = findViewById(R.id.edit_text_title) // Инициализируем поле для названия
        servingsInput = findViewById(R.id.edit_text_servings)
        stepsInput = findViewById(R.id.edit_text_steps)
        productsContainer = findViewById(R.id.products_container)

        // Инициализируем динамическое добавление полей
        addProductField() // Добавление поля для первого продукта

        // Обработчик нажатия кнопки "Сохранить"
        val buttonSaveRecipe: Button = findViewById(R.id.button_save_recipe)
        buttonSaveRecipe.setOnClickListener {
            saveRecipe()
        }

        // Обработчик нажатия на кнопку "Добавить продукт"
        val buttonAddProduct: Button = findViewById(R.id.button_add_product)
        buttonAddProduct.setOnClickListener {
            addProductField()
        }
    }

    private fun loadProductsIntoSpinner(spinner: Spinner) {
        val products = databaseHelper.getAllProducts()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, products)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun saveRecipe() {
        val productQuantities = mutableListOf<ProductQuantity>() // Здесь теперь используется правильный класс

        for (i in 0 until productsContainer.childCount) {
            val productField = productsContainer.getChildAt(i)
            val productSpinner = productField.findViewById<Spinner>(R.id.spinner_products)
            val quantityInput = productField.findViewById<EditText>(R.id.edit_text_quantity)

            val product = productSpinner.selectedItem.toString()
            val quantity = quantityInput.text.toString().toIntOrNull() ?: 0

            if (product.isNotBlank() && quantity > 0) {
                productQuantities.add(ProductQuantity(product, quantity))
            }
        }

        val title = titleInput.text.toString().trim() // Получаем название рецепта
        val servings = servingsInput.text.toString().toIntOrNull() ?: 0
        val steps = stepsInput.text.toString()

        // Сохранение рецепта с продуктами
        databaseHelper.addRecipe(title, productQuantities, servings, steps, null) // Передаем название рецепта

        Toast.makeText(this, "Рецепт сохранен!", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun addProductField() {
        val productField = LayoutInflater.from(this).inflate(R.layout.product_input_item, null)

        val productSpinner = productField.findViewById<Spinner>(R.id.spinner_products)
        val quantityInput = productField.findViewById<EditText>(R.id.edit_text_quantity)

        // Загружаем продукты для выбора в новый productSpinner
        loadProductsIntoSpinner(productSpinner)

        // Добавляем новую строку в контейнер
        productsContainer.addView(productField)
    }
}