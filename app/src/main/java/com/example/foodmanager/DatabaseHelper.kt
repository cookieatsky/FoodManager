package com.example.foodmanager

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "FoodManagerDB.db"
        private const val DATABASE_VERSION = 3 // Увеличиваем версию базы данных

        const val TABLE_PRODUCT = "Product"
        const val COLUMN_ID = "ID"
        const val COLUMN_NAME = "name"
        const val COLUMN_UNIT = "unit" // Единица измерения
        const val COLUMN_PROTEINS = "proteins"
        const val COLUMN_CARBS = "carbs"
        const val COLUMN_FATS = "fats"
        const val COLUMN_CALORIES = "calories"

        // Константы для таблицы рецептов
        const val TABLE_RECIPE = "Recipe"
        const val COLUMN_SERVINGS = "servings"
        const val COLUMN_STEPS = "steps"
        const val COLUMN_IMAGE = "image" // Если вы планируете хранить изображения

        // Константы для таблицы ингредиентов
        const val TABLE_RECIPE_INGREDIENTS = "RecipeIngredients"
        const val COLUMN_RECIPE_ID = "recipe_id" // ID рецепта
        const val COLUMN_PRODUCT_NAME = "product_name" // Название продукта
        const val COLUMN_QUANTITY = "quantity" // Количество продукта
    }



    override fun onCreate(db: SQLiteDatabase?) {
        // Создание таблицы продуктов
        val createTableProduct = (
                "CREATE TABLE IF NOT EXISTS ${TABLE_PRODUCT} (" +
                        "${COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "${COLUMN_NAME} TEXT, " +
                        "${COLUMN_UNIT} TEXT, " +
                        "${COLUMN_PROTEINS} REAL, " +
                        "${COLUMN_CARBS} REAL, " +
                        "${COLUMN_FATS} REAL, " +
                        "${COLUMN_CALORIES} REAL" +
                        ");"
                )

        // Создание таблицы рецептов
        val createTableRecipe = (
                "CREATE TABLE IF NOT EXISTS ${TABLE_RECIPE} (" +
                        "${COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "${COLUMN_SERVINGS} INTEGER, " +
                        "${COLUMN_STEPS} TEXT, " +
                        "${COLUMN_IMAGE} BLOB" +
                        ");"
                )

        // Создание таблицы ингредиентов рецепта
        val createTableRecipeIngredients = (
                "CREATE TABLE IF NOT EXISTS ${TABLE_RECIPE_INGREDIENTS} (" +
                        "${COLUMN_RECIPE_ID} INTEGER, " +
                        "${COLUMN_PRODUCT_NAME} TEXT, " +
                        "${COLUMN_QUANTITY} INTEGER, " +
                        "FOREIGN KEY(${COLUMN_RECIPE_ID}) REFERENCES ${TABLE_RECIPE}(${COLUMN_ID})" +
                        ");"
                )

        db?.execSQL(createTableProduct)
        db?.execSQL(createTableRecipe)
        db?.execSQL(createTableRecipeIngredients) // Создание таблицы ингредиентов
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_RECIPE_INGREDIENTS") // Удаляем таблицу ингредиентов при обновлении версии
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_RECIPE")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCT")
        onCreate(db)
    }

    fun addProduct(name: String, unit: String, proteins: Float, carbs: Float, fats: Float, calories: Float) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, name)
        values.put(COLUMN_UNIT, unit)
        values.put(COLUMN_PROTEINS, proteins)
        values.put(COLUMN_CARBS, carbs)
        values.put(COLUMN_FATS, fats)
        values.put(COLUMN_CALORIES, calories)

        db.insert(TABLE_PRODUCT, null, values)
        db.close()
    }

    // Новый метод для добавления рецепта с ингредиентами
    fun addRecipe(ingredients: List<ProductQuantity>, servings: Int, steps: String, image: ByteArray?) {
        val db = this.writableDatabase
        val recipeValues = ContentValues()
        recipeValues.put(COLUMN_SERVINGS, servings)
        recipeValues.put(COLUMN_STEPS, steps)
        recipeValues.put(COLUMN_IMAGE, image)

        // Вставляем рецепт и получаем его ID
        val recipeId = db.insert(TABLE_RECIPE, null, recipeValues)

        // Вставляем ингредиенты
        for (ingredient in ingredients) {
            val ingredientValues = ContentValues()
            ingredientValues.put(COLUMN_RECIPE_ID, recipeId) // Связываем с рецептом
            ingredientValues.put(COLUMN_PRODUCT_NAME, ingredient.product)
            ingredientValues.put(COLUMN_QUANTITY, ingredient.quantity)

            db.insert(TABLE_RECIPE_INGREDIENTS, null, ingredientValues)
        }

        db.close()
    }

    fun getAllProducts(): List<String> {
        val productList = mutableListOf<String>()
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            val query = "SELECT ${COLUMN_NAME} FROM ${TABLE_PRODUCT}"
            cursor = db.rawQuery(query, null)

            if (cursor.moveToFirst()) {
                do {
                    val productName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                    if (productName != null) { // Проверяем, чтобы избежать NullPointerException
                        productList.add(productName)
                    }
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            e.printStackTrace() // Логируем ошибки
        } finally {
            cursor?.close() // Закрываем курсор
            db.close() // Закрываем базу данных
        }
        return productList
    }

    // Класс для хранения информации о продукте и его количестве
    data class ProductQuantity(val product: String, val quantity: Int)
}