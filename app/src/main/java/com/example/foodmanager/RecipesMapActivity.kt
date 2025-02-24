package com.example.foodmanager

import android.content.ContentValues
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecipesMapActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var buttonCreateCategory: Button
    private lateinit var categoryAdapter: CategoryAdapter
    private var categories: MutableList<Category> = mutableListOf()
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes_map)

        databaseHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerView_categories)
        buttonCreateCategory = findViewById(R.id.button_create_category)

        // Настройка RecyclerView
        categoryAdapter = CategoryAdapter(categories)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = categoryAdapter

        // Загружаем категории из базы данных
        loadCategoriesFromDatabase()

        // Обработчик нажатия кнопки "Создать категорию"
        buttonCreateCategory.setOnClickListener {
            createNewCategory()
        }
    }

    private fun loadCategoriesFromDatabase() {
        categories.clear()
        categories.addAll(databaseHelper.getAllCategories()) // Получаем категории из базы
        categoryAdapter.notifyDataSetChanged() // Обновляем адаптер
    }

    private fun createNewCategory() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Введите название категории")

        val input = EditText(this)
        input.hint = "Название категории"

        // Spinner для выбора родительской категории
        val parentCategorySpinner = Spinner(this)

        // Получаем все категории из базы данных
        val parentCategoryList = databaseHelper.getAllCategories()
        val parentCategoryNames = mutableListOf("Нет родителя") + parentCategoryList.map { it.name }

        // Создание адаптера для Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, parentCategoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        parentCategorySpinner.adapter = adapter

        // Создаем контейнер для размещения поля ввода и Spinner
        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
        container.addView(input)
        container.addView(parentCategorySpinner)

        builder.setView(container)

        builder.setPositiveButton("Создать") { dialog, which ->
            val newCategoryName = input.text.toString().trim()
            val newCategoryId: Int?

            if (newCategoryName.isNotEmpty()) {
                val selectedParentCategoryPosition = parentCategorySpinner.selectedItemPosition
                val selectedParentCategoryId = if (selectedParentCategoryPosition == 0) {
                    null
                } else {
                    parentCategoryList[selectedParentCategoryPosition - 1].id
                }

                newCategoryId = saveNewCategoryToDatabase(newCategoryName, selectedParentCategoryId)

                if (newCategoryId != null) {
                    // Вставляем категорию
                    if (selectedParentCategoryId == null) {
                        // Если корневая категория
                        categories.add(Category(newCategoryId, newCategoryName, null))
                    } else {
                        // Если подкатегория, находим индекс для её вставки
                        val insertionIndex = findInsertionIndexAfterParent(selectedParentCategoryId)
                        categories.add(insertionIndex, Category(newCategoryId, newCategoryName, selectedParentCategoryId))
                    }
                    categoryAdapter.notifyDataSetChanged()
                }
            } else {
                Toast.makeText(this, "Имя категории не может быть пустым", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Отмена") { dialog, which -> dialog.cancel() }
        builder.show()
    }

    // Функция для нахождения индекса для вставки подкатегории
    private fun findInsertionIndexAfterParent(parentId: Int?): Int {
        // Находим индекс родительской категории
        val parentIndex = categories.indexOfFirst { it.id == parentId }
        if (parentIndex == -1) return categories.size // Если родительская категория не найдена, добавляем в конец

        var nextIndex = parentIndex + 1
        // Ищем подкатегории, принадлежащие родительской категории
        while (nextIndex < categories.size) {
            if (categories[nextIndex].parentId == parentId) {
                nextIndex++ // Пропускаем подкатегории этого родителя
            } else {
                break // Находим место для вставки
            }
        }
        return nextIndex // Возвращаем индекс для вставки
    }

    private fun saveNewCategoryToDatabase(name: String, parentId: Int?): Int? {
        val db = databaseHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CATEGORY_NAME, name)
            put(DatabaseHelper.COLUMN_CATEGORY_PARENT_ID, parentId)
        }
        val id = db.insert(DatabaseHelper.TABLE_CATEGORY, null, values)

        return if (id != -1L) id.toInt() else null // Преобразуем ID в Int
    }
}