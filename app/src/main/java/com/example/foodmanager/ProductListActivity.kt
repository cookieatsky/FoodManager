package com.example.foodmanager

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProductListActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var products: List<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list) // Убедитесь, что имя совпадает с вашим XML

        // Инициализация базы данных и RecyclerView
        databaseHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerView)

        // Получаем все записи продуктов из базы данных
        products = getProductsFromDatabase()

        // Настраиваем RecyclerView
        productAdapter = ProductAdapter(products)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = productAdapter

        // Переход на форму создания продукта
        val buttonCreateProduct: Button = findViewById(R.id.CreateProductButton)
        buttonCreateProduct.setOnClickListener {
            val intent = Intent(this, CreateProductActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("Range")
    private fun getProductsFromDatabase(): List<Product> {
        val productList = mutableListOf<Product>()
        try {
            val db = databaseHelper.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_PRODUCT}", null)

            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID))
                    val name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME))
                    val unit = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_UNIT))
                    val proteins = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_PROTEINS))
                    val carbs = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_CARBS))
                    val fats = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_FATS))
                    val calories = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_CALORIES))

                    val product = Product(id, name, unit, proteins, carbs, fats, calories)
                    productList.add(product)
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()
        } catch (e: Exception) {
            e.printStackTrace()  // Печатаем стек вызовов при ошибке
        }
        return productList
    }
}