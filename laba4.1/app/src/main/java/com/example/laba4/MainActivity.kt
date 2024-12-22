package com.example.laba4

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var data: TextView
    private lateinit var add: Button
    private lateinit var show: Button
    private lateinit var delete: Button
    private var marks = arrayOf("BMW", "Tesla", "Subaru", "Hyundai", "Nissan",
        "Chevrolet", "Toyota", "Mercedes", "Bentley", "Ferrari",
        "Lamborghini", "Audi", "Porche", "Ford", "Honda")
    private var carModels = mapOf(
        "BMW" to arrayOf("X5", "M3", "M4", "Z4", "7 Series"),
        "Tesla" to arrayOf("Model S", "Model 3", "Model X", "Model Y"),
        "Subaru" to arrayOf("Outback", "Forester", "Impreza", "Legacy"),
        "Hyundai" to arrayOf("Sonata", "Elantra", "Kona", "Tucson", "Santa Fe"),
        "Nissan" to arrayOf("Altima", "Maxima", "370Z", "Juke", "Leaf"),
        "Chevrolet" to arrayOf("Malibu", "Camaro", "Impala", "Cruze", "Tahoe"),
        "Toyota" to arrayOf("Corolla", "Camry", "Hilux", "Prius", "Land Cruiser"),
        "Mercedes" to arrayOf("A-Class", "C-Class", "E-Class", "S-Class", "GLE"),
        "Bentley" to arrayOf("Continental GT", "Flying Spur", "Mulsanne"),
        "Ferrari" to arrayOf("488 GTB", "Portofino", "Roma", "LaFerrari"),
        "Lamborghini" to arrayOf("Aventador", "Huracán", "Urus"),
        "Audi" to arrayOf("A3", "A4", "A6", "Q5", "Q7"),
        "Porsche" to arrayOf("911", "Cayenne", "Macan", "Taycan"),
        "Ford" to arrayOf("Mustang", "Focus", "F-150", "Explorer", "Fiesta"),
        "Honda" to arrayOf("Civic", "Accord", "CR-V", "Pilot", "Fit")
    )
    val maxSpeeds = arrayOf(250, 261, 220, 210, 240, 210, 210, 250,
        320, 350, 350, 250, 300, 220, 220)

    val enginePowers = arrayOf(350.0F, 1020.0F, 275.0F, 290.0F, 400.0F, 355.0F, 300.0F, 550.0F,
        635.0F, 710.0F, 760.0F, 600.0F, 650.0F, 400.0F, 350.0F)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        setListeners()
    }

    private fun setListeners() {
        add.setOnClickListener { addData() }
        show.setOnClickListener { showData() }
        delete.setOnClickListener { deleteData() }
    }

    private fun addData() {
        for (i in 0 until marks.size) {
            dbHelper.insert(
                marks[i],
                carModels.get(marks[i])?.joinToString(",") ?: "Empty",
                (0xFF000000 and (Math.random() * 0x00FFFFFF).toLong()).toInt(),
                maxSpeeds[i],
                enginePowers[i]
            )
        }
        dbHelper.close()
    }

    private fun showData() {
        val db = dbHelper.writableDatabase
        data.text = ""
        val c = db.query("appdb", null, null, null, null, null, null)
        if (c.moveToFirst()) {
            val idColIndex = c.getColumnIndex("id")
            val markColIndex = c.getColumnIndex("mark")
            val modelColIndex = c.getColumnIndex("model")
            val colorColIndex = c.getColumnIndex("color")
            val maxSpeedColIndex = c.getColumnIndex("maxSpeed")
            val engineColIndex = c.getColumnIndex("engine")
            do {
                data.append(
                    "ID = " + c.getInt(idColIndex) + ", Марка: " +
                            c.getString(markColIndex) + ", Модель: " +
                            c.getString(modelColIndex) + ", Цвет: " +
                            c.getString(colorColIndex) + ", Максимальная скорость: " +
                            c.getString(maxSpeedColIndex) + ", Мощность двигателя: " +
                            c.getInt(engineColIndex) + "\n"
                )
            } while (c.moveToNext())
        } else {
            data.append("0 rows")
        }
        dbHelper.close()
        showSortedBySpeed()
        data.append("\n\nсгруппировать данные по нескольким одинаковым полям \n")
        showGroupedByMaxSpeed()
        showSumOfEnginePower()
        data.append("\n\nвычислить\n" +
                "средние значения по сгруппированным полям \n")
        showAverageEnginePowerAndSpeed()
        showMaxEnginePower()
        data.append("\n\nотобразить поля\n" +
                "таблицы, в которых числовые величины больше заданной \n")
        showFieldsWithMaxSpeedGreaterThan(250)
        data.append("\n\nотобразить поля таблицы, в которых числовые величины меньше\n" +
                "средней \n")
        showFieldsWithMaxSpeedLessThanAverage()
        showModelsWithEnginePowerGreaterThan(700)
    }
    private fun showSortedBySpeed() {
        val db = dbHelper.writableDatabase
        val c = db.query("appdb", null, null, null, null, null, "maxSpeed ASC")

        val fileName = "sortedCars.txt"
        val stringBuilder = StringBuilder()

        if (c.moveToFirst()) {
            val idColIndex = c.getColumnIndex("id")
            val markColIndex = c.getColumnIndex("mark")
            val modelColIndex = c.getColumnIndex("model")
            val colorColIndex = c.getColumnIndex("color")
            val maxSpeedColIndex = c.getColumnIndex("maxSpeed")
            val engineColIndex = c.getColumnIndex("engine")

            do {
                val result = "ID = " + c.getInt(idColIndex) + ", Марка: " +
                        c.getString(markColIndex) + ", Модель: " +
                        c.getString(modelColIndex) + ", Цвет: " +
                        c.getString(colorColIndex) + ", Максимальная скорость: " +
                        c.getString(maxSpeedColIndex) + ", Мощность двигателя: " +
                        c.getInt(engineColIndex) + "\n"

                stringBuilder.append(result)

                Log.d("SortedBySpeed", result)
            } while (c.moveToNext())
        } else {
            Log.d("SortedBySpeed", "0 rows")
        }

        saveToFile(fileName, stringBuilder.toString())
        dbHelper.close()
    }
    private fun showGroupedByMaxSpeed() {
        val db = dbHelper.writableDatabase
        val c = db.rawQuery("SELECT maxSpeed, GROUP_CONCAT(mark) FROM appdb GROUP BY maxSpeed", null)

        if (c.moveToFirst()) {
            do {
                val result = "Скорость: " + c.getInt(0) + ", Марки: " + c.getString(1) + "\n"
                data.append(result)
                Log.d("GroupedByMaxSpeed", result)
            } while (c.moveToNext())
        } else {
            Log.d("GroupedByMaxSpeed", "0 rows")
        }
        dbHelper.close()
    }

    private fun showSumOfEnginePower() {
        val db = dbHelper.writableDatabase
        val c = db.rawQuery("SELECT SUM(engine) FROM appdb", null)
        if (c.moveToFirst()) {
            val sum = c.getInt(0)
            saveToFile("SumOfEnginePower", "Сумма мощности двигателей: $sum")
            Log.d("SumOfEnginePower", "Сумма мощности двигателей: $sum")
        } else {
            Log.d("SumOfEnginePower", "0 rows")
        }
        dbHelper.close()
    }
    private fun showAverageEnginePowerAndSpeed() {
        val db = dbHelper.writableDatabase
        val c = db.rawQuery("SELECT AVG(engine), AVG(maxSpeed) FROM appdb", null)
        val stringBuilder = StringBuilder()
        if (c.moveToFirst()) {
            val avgEngine = c.getInt(0)
            val avgSpeed = c.getInt(1)
            val result = "Средняя мощность двигателя: $avgEngine, Средняя скорость: $avgSpeed"
            stringBuilder.append(result)
            Log.d("AverageEngineAndSpeed", result)
        } else {
            Log.d("AverageEngineAndSpeed", "0 rows")
        }
        saveToFile("AverageEngineAndSpeed", stringBuilder.toString())
        dbHelper.close()
    }
    private fun showMaxEnginePower() {
        val db = dbHelper.writableDatabase
        val c = db.rawQuery("SELECT MAX(engine) FROM appdb", null)
        if (c.moveToFirst()) {
            val maxEnginePower = c.getInt(0)
            data.append(maxEnginePower.toString())
            Log.d("MaxEnginePower", "Максимальная мощность двигателя: $maxEnginePower")
        } else {
            Log.d("MaxEnginePower", "0 rows")
        }
        dbHelper.close()
    }
    private fun showFieldsWithMaxSpeedGreaterThan(value: Int) {
        val db = dbHelper.writableDatabase
        val c = db.rawQuery("SELECT * FROM appdb WHERE maxSpeed > $value", null)
        if (c.moveToFirst()) {
            val idColIndex = c.getColumnIndex("id")
            val markColIndex = c.getColumnIndex("mark")
            val modelColIndex = c.getColumnIndex("model")
            val colorColIndex = c.getColumnIndex("color")
            val maxSpeedColIndex = c.getColumnIndex("maxSpeed")
            val engineColIndex = c.getColumnIndex("engine")
            do {
                val result = "ID = " + c.getInt(idColIndex) + ", Марка: " +
                        c.getString(markColIndex) + ", Модель: " +
                        c.getString(modelColIndex) + ", Цвет: " +
                        c.getString(colorColIndex) + ", Максимальная скорость: " +
                        c.getString(maxSpeedColIndex) + ", Мощность двигателя: " +
                        c.getInt(engineColIndex)
                data.append(result)
                Log.d("FieldsWithMaxSpeedGreaterThan", result)
            } while (c.moveToNext())
        } else {
            Log.d("FieldsWithMaxSpeedGreaterThan", "0 rows")
        }
        dbHelper.close()
    }
    private fun showFieldsWithMaxSpeedLessThanAverage() {
        val db = dbHelper.writableDatabase
        val c = db.rawQuery("SELECT * FROM appdb WHERE maxSpeed < (SELECT AVG(maxSpeed) FROM appdb)", null)
        if (c.moveToFirst()) {
            val idColIndex = c.getColumnIndex("id")
            val markColIndex = c.getColumnIndex("mark")
            val modelColIndex = c.getColumnIndex("model")
            val colorColIndex = c.getColumnIndex("color")
            val maxSpeedColIndex = c.getColumnIndex("maxSpeed")
            val engineColIndex = c.getColumnIndex("engine")
            do {
                val result = "ID = " + c.getInt(idColIndex) + ", Марка: " +
                        c.getString(markColIndex) + ", Модель: " +
                        c.getString(modelColIndex) + ", Цвет: " +
                        c.getString(colorColIndex) + ", Максимальная скорость: " +
                        c.getString(maxSpeedColIndex) + ", Мощность двигателя: " +
                        c.getInt(engineColIndex)
                data.append(result)
                Log.d("FieldsWithMaxSpeedLessThanAverage", result)
            } while (c.moveToNext())
        } else {
            Log.d("FieldsWithMaxSpeedLessThanAverage", "0 rows")
        }
        dbHelper.close()
    }
    private fun showModelsWithEnginePowerGreaterThan(value: Int) {
        val db = dbHelper.writableDatabase
        val c = db.rawQuery("SELECT model FROM appdb WHERE engine > $value", null)
        if (c.moveToFirst()) {
            do {
                val result = "Модель: " + c.getString(0)
                data.append(result)
                Log.d("ModelsWithEnginePowerGreaterThan", result)
            } while (c.moveToNext())
        } else {
            Log.d("ModelsWithEnginePowerGreaterThan", "0 rows")
        }
        dbHelper.close()
    }

    private fun deleteData() {
        val db = dbHelper.writableDatabase
        val clearCount = db.delete("appdb", null, null)
        data.text = "Удалено записей: $clearCount"
        dbHelper.close()
    }
    fun saveToFile(fileName: String, content: String) {
        try {
            val fileOutputStream: FileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE)
            fileOutputStream.write(content.toByteArray())
            fileOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка при сохранении файла", Toast.LENGTH_SHORT).show()
        }
    }
    private fun init() {
        dbHelper = DBHelper(this)
        data = findViewById(R.id.data)
        add = findViewById(R.id.add)
        show = findViewById(R.id.show)
        delete = findViewById(R.id.delete)
    }
}