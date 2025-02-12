package com.example.foodmanager

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "FoodManagerDB.db"
        private const val DATABASE_VERSION = 2 // Изменим версию базы данных

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
        const val COLUMN_PRODUCT = "product"
        const val COLUMN_SERVINGS = "servings"
        const val COLUMN_STEPS = "steps"
        const val COLUMN_IMAGE = "image" // Если вы планируете хранить изображения
    }

    override fun onCreate(db: SQLiteDatabase?) {
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

        // Определение новой таблицы для рецептов
        val createTableRecipe = (
                "CREATE TABLE IF NOT EXISTS ${TABLE_RECIPE} (" +
                        "${COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "${COLUMN_PRODUCT} TEXT, " +
                        "${COLUMN_SERVINGS} INTEGER, " +
                        "${COLUMN_STEPS} TEXT, " +
                        "${COLUMN_IMAGE} BLOB" + // Тип для хранения изображений
                        ");"
                )

        db?.execSQL(createTableProduct)
        db?.execSQL(createTableRecipe) // Создаем таблицу рецептов
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCT")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_RECIPE") // Удаляем таблицу рецептов при обновлении версии
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

    // Новый метод для добавления рецепта
    fun addRecipe(product: String, servings: Int, steps: String, image: ByteArray?) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_PRODUCT, product)
        values.put(COLUMN_SERVINGS, servings)
        values.put(COLUMN_STEPS, steps)
        values.put(COLUMN_IMAGE, image)

        db.insert(TABLE_RECIPE, null, values)
        db.close()
    }
    fun getAllProducts(): List<String> {
        val productList = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor: Cursor? = null
        try {
            val query = "SELECT ${COLUMN_NAME} FROM ${TABLE_PRODUCT}"
            val cursor = db.rawQuery(query, null)

            if (cursor.moveToFirst()) {
                do {
                    val productName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                    if (productName != null) { // Проверяем, чтобы избежать NullPointerException
                        productList.add(productName)
                    }
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            e.printStackTrace() // Логируем, что произошло
        } finally {
            cursor?.close() // Закрываем курсор
            // db будет закрыт в месте, где вы завершите работу с DatabaseHelper
        }
        return productList
    }
}