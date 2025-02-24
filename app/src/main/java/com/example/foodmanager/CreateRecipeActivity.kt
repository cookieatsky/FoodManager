package com.example.foodmanager

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class CreateRecipeActivity : AppCompatActivity() {
    private lateinit var titleInput: EditText
    private lateinit var servingsInput: EditText
    private lateinit var stepsInput: EditText
    private lateinit var productsContainer: LinearLayout
    private lateinit var categorySpinner: Spinner // Spinner для выбора категории
    private lateinit var imageView: ImageView // ImageView для отображения загруженного изображения
    private var selectedImageUri: Uri? = null // URI выбранного изображения

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_recipes)

        databaseHelper = DatabaseHelper(this)

        // Инициализация полей ввода
        titleInput = findViewById(R.id.edit_text_title)
        servingsInput = findViewById(R.id.edit_text_servings)
        stepsInput = findViewById(R.id.edit_text_steps)
        productsContainer = findViewById(R.id.products_container)
        imageView = findViewById(R.id.imageView)

        // Инициализация Spinner для выбора категории
        categorySpinner = findViewById(R.id.spinner_categories)
        loadCategoriesIntoSpinner(categorySpinner)

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

        // Обработчик нажатия на кнопку "Загрузить фотографию"
        val buttonUploadImage: Button = findViewById(R.id.button_upload_image)
        buttonUploadImage.setOnClickListener {
            openGallery()
        }
    }

    private fun loadCategoriesIntoSpinner(spinner: Spinner) {
        val categories = databaseHelper.getAllCategories()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories.map { it.name })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun loadProductsIntoSpinner(spinner: Spinner) {
        val products = databaseHelper.getAllProducts()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, products)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun saveRecipe() {
        val productQuantities = mutableListOf<ProductQuantity>()

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

        val title = titleInput.text.toString().trim()
        val servings = servingsInput.text.toString().toIntOrNull() ?: 0
        val steps = stepsInput.text.toString()
        val selectedCategory = categorySpinner.selectedItem as? Category
        val selectedCategoryId = selectedCategory?.id

        // Код для получения изображения из URI и сохранения его
        val imagePath = saveImageToFile() // Сохраняем изображение и получаем путь к нему

        // Сохранение рецепта с продуктами и выбранной категорией
        databaseHelper.addRecipe(title, productQuantities, servings, steps, imagePath, selectedCategoryId)

        Toast.makeText(this, "Рецепт сохранен!", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun addProductField() {
        val productField = LayoutInflater.from(this).inflate(R.layout.product_input_item, null)

        val productSpinner = productField.findViewById<Spinner>(R.id.spinner_products)
        val quantityInput = productField.findViewById<EditText>(R.id.edit_text_quantity)

        loadProductsIntoSpinner(productSpinner)

        productsContainer.addView(productField)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            imageView.setImageURI(selectedImageUri)
            imageView.visibility = View.VISIBLE // Делаем ImageView видимым
        }
    }

    private fun saveImageToFile(): String? {
        selectedImageUri?.let { uri ->
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream) // Декодируем в Bitmap
            val fileName = "recipe_image_${System.currentTimeMillis()}.png" // Задаем имя файла
            val file = File(filesDir, fileName) // Определяем файл
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) // Сохранение изображения в файл
            }
            return file.absolutePath // Возвращаем путь к файлу
        }
        return null
    }
}