package com.example.foodmanager

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Переход в меню
        val buttonMenu: Button = findViewById(R.id.MenuButton)
        buttonMenu.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        //Переход в список покупок на день
        val buttonShopList: Button = findViewById(R.id.ShopListButton)
        buttonShopList.setOnClickListener {
            val intent = Intent(this, ShopListActivity::class.java)
            startActivity(intent)
        }

        //Переход в генератор
        val buttonGenerator: Button = findViewById(R.id.GeneratorButton)
        buttonGenerator.setOnClickListener {
            val intent = Intent(this, GeneratorActivity::class.java)
            startActivity(intent)
        }

        //Переход в список продуктов питания созданных в системе
        val buttonProductList: Button = findViewById(R.id.ProductListButton)
        buttonProductList.setOnClickListener {
            val intent = Intent(this, ProductListActivity::class.java)
            startActivity(intent)
        }

        //Переход на карту рецептов
        val buttonRecipesMap: Button = findViewById(R.id.RecipesMapButton)
        buttonRecipesMap.setOnClickListener {
            val intent = Intent(this, RecipesMapActivity::class.java)
            startActivity(intent)
        }

    }
}