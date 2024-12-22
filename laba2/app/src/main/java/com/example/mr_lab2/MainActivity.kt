package com.example.mr_lab2

import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.laba2.R

class MainActivity : AppCompatActivity() {

    private lateinit var layout: RelativeLayout
    private lateinit var shapeImage: ImageView
    private var selectedColor: Int = Color.BLACK // запоминаем выбранный цвет
    private var selectedShape: String? = null // текущая выбранная фигура

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        layout = findViewById(R.id.main_layout)
        shapeImage = findViewById(R.id.shape_image)

        logEvent("Application started")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_red -> {
                selectedColor = Color.RED
                Toast.makeText(this, "Красный цвет выбран", Toast.LENGTH_SHORT).show()
                logEvent("Красный цвет выбран")
            }
            R.id.action_green -> {
                selectedColor = Color.GREEN
                Toast.makeText(this, "Зеленый цвет выбран", Toast.LENGTH_SHORT).show()
                logEvent("Зеленый цвет выбран")
            }
            R.id.action_blue -> {
                selectedColor = Color.BLUE
                Toast.makeText(this, "Синий цвет выбран", Toast.LENGTH_SHORT).show()
                logEvent("Синий цвет выбран")
            }
            R.id.action_yellow -> {
                selectedColor = Color.YELLOW
                Toast.makeText(this, "Желтый цвет выбран", Toast.LENGTH_SHORT).show()
                logEvent("Желтый цвет выбран")
            }
            R.id.action_circle -> {
                selectedShape = "Circle"
                drawShape()
                logEvent("Круг выбран")
            }
            R.id.action_square -> {
                selectedShape = "Square"
                drawShape()
                logEvent("Квадрат выбран")
            }
            R.id.action_triangle -> {
                selectedShape = "Triangle"
                drawShape()
                logEvent("Треугольник выбран")
            }
            R.id.action_clear -> {
                shapeImage.visibility = ImageView.GONE // Скрываем фигуру
                shapeImage.clearColorFilter() // Сбрасываем цветовой фильтр
                layout.setBackgroundColor(Color.WHITE) // Сбрасываем цвет фона
                selectedShape = null // Сбрасываем фигуру, но сохраняем цвет
                Toast.makeText(this, "Экран очищен", Toast.LENGTH_SHORT).show()
                logEvent("Экран очищен")
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun drawShape() {
        shapeImage.clearColorFilter()

        // Если выбрана фигура "Треугольник", загружаем изображение из ресурсов
        if (selectedShape == "Triangle") {
            shapeImage.setImageResource(R.drawable.ic_triangle)
            shapeImage.setColorFilter(selectedColor) // Применяем сохраненный цвет к треугольнику
            shapeImage.visibility = ImageView.VISIBLE // Отображаем треугольник
            return
        }

        // Если выбрана другая фигура (Круг или Квадрат), создаем соответствующую форму
        val shapeDrawable = when (selectedShape) {
            "Circle" -> ShapeDrawable(OvalShape())
            "Square" -> ShapeDrawable(RectShape())
            else -> null
        }

        shapeDrawable?.let {
            it.intrinsicWidth = 200
            it.intrinsicHeight = 200
            it.paint.color = selectedColor // Применяем сохраненный цвет

            shapeImage.setImageDrawable(it)
            shapeImage.visibility = ImageView.VISIBLE // Отображаем фигуру
        }
    }

    private fun logEvent(message: String) {
        android.util.Log.d("MainActivityLog", message)
    }
}
