package com.example.foodmanager

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
//import kotlinx.android.synthetic.main.activity_create_product.* // Импортируйте это для использования синтетических свойств

class CreateProductActivity : AppCompatActivity() {

    private lateinit var unitSpinner: Spinner
    private lateinit var productNameInput: EditText
    private lateinit var proteinInput: EditText
    private lateinit var carbInput: EditText
    private lateinit var fatInput: EditText
    private lateinit var caloriesInput: EditText
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_product)

        databaseHelper = DatabaseHelper(this)

        // Устанавливаем обработчик для отступов окна
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Инициализация Spinner
        unitSpinner = findViewById(R.id.unit_spinner)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.units_array,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        unitSpinner.adapter = adapter

        // Инициализация полей ввода
        productNameInput = findViewById(R.id.product_name)
        proteinInput = findViewById(R.id.protein_input)
        carbInput = findViewById(R.id.carb_input)
        fatInput = findViewById(R.id.fat_input)
        caloriesInput = findViewById(R.id.calories_input)

        // Обработчик нажатия кнопки "Сохранить"
        val saveButton = findViewById<Button>(R.id.SaveProductButton) // Убедитесь, что ID соответствует вашему файлу макета
        saveButton.setOnClickListener {
            saveProduct()
        }
    }

    private fun saveProduct() {
        val name = productNameInput.text.toString()
        val unit = unitSpinner.selectedItem.toString()
        val proteins = proteinInput.text.toString().toFloatOrNull() ?: 0f
        val carbs = carbInput.text.toString().toFloatOrNull() ?: 0f
        val fats = fatInput.text.toString().toFloatOrNull() ?: 0f
        val calories = caloriesInput.text.toString().toFloatOrNull() ?: 0f

        // Добавление продукта в базу данных
        databaseHelper.addProduct(name, unit, proteins, carbs, fats, calories)

        // Уведомление об успешном сохранении
        Toast.makeText(this, "Данные сохранены!", Toast.LENGTH_SHORT).show()

        // Очистить поля для ввода
        productNameInput.text.clear()
        proteinInput.text.clear()
        carbInput.text.clear()
        fatInput.text.clear()
        caloriesInput.text.clear()
    }



}