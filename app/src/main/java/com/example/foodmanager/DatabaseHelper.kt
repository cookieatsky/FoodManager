package com.example.foodmanager

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "FoodManagerDB.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_PRODUCT = "Product"
        const val COLUMN_ID = "ID"
        const val COLUMN_NAME = "name"
        const val COLUMN_UNIT = "unit" // Единица измерения
        const val COLUMN_PROTEINS = "proteins"
        const val COLUMN_CARBS = "carbs"
        const val COLUMN_FATS = "fats"
        const val COLUMN_CALORIES = "calories"
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
                        ");"  // Убедитесь, что тут нет лишней скобки
                )
        db?.execSQL(createTableProduct)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
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
}