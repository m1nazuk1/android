package com.example.laba4

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
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
        val dataToShow = ArrayList<String>()
        val db = dbHelper.writableDatabase

        dataToShow.add("Основные данные таблицы:")
        val cursor = db.query("appdb", null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            val idColIndex = cursor.getColumnIndex("id")
            val markColIndex = cursor.getColumnIndex("mark")
            val modelColIndex = cursor.getColumnIndex("model")
            val colorColIndex = cursor.getColumnIndex("color")
            val maxSpeedColIndex = cursor.getColumnIndex("maxSpeed")
            val engineColIndex = cursor.getColumnIndex("engine")
            do {
                dataToShow.add(
                    "ID = ${cursor.getInt(idColIndex)}, Марка: ${cursor.getString(markColIndex)}, " +
                            "Модель: ${cursor.getString(modelColIndex)}, Цвет: ${cursor.getInt(colorColIndex)}, " +
                            "Максимальная скорость: ${cursor.getInt(maxSpeedColIndex)}, Мощность двигателя: " +
                            "${cursor.getFloat(engineColIndex)}"
                )
            } while (cursor.moveToNext())
        }
        cursor.close()

        dataToShow.add("\nСортированные данные по скорости:")
        dataToShow.addAll(showSortedBySpeed(db))

        dataToShow.add("\nГруппировка данных по максимальной скорости:")
        dataToShow.addAll(showGroupedByMaxSpeed(db))

        dataToShow.add("\nСумма мощности двигателей:")
        dataToShow.add(showSumOfEnginePower(db))

        dataToShow.add("\nСредние значения мощности и скорости:")
        dataToShow.add(showAverageEnginePowerAndSpeed(db))

        dataToShow.add("\nМаксимальная мощность двигателя:")
        dataToShow.add(showMaxEnginePower(db))

        dataToShow.add("\nАвтомобили с максимальной скоростью выше 250:")
        dataToShow.addAll(showFieldsWithMaxSpeedGreaterThan(db, 250))

        dataToShow.add("\nАвтомобили с максимальной скоростью ниже средней:")
        dataToShow.addAll(showFieldsWithMaxSpeedLessThanAverage(db))

        dataToShow.add("\nМодели с мощностью двигателя более 700:")
        dataToShow.addAll(showModelsWithEnginePowerGreaterThan(db, 700))

        db.close()

        saveToFile("Nizami", dataToShow.toString());

        val intent = Intent(this, DisplayActivity::class.java)
        intent.putStringArrayListExtra("data", dataToShow)
        startActivity(intent)
    }
    private fun showSortedBySpeed(db: SQLiteDatabase): ArrayList<String> {
        val result = ArrayList<String>()
        val cursor = db.query("appdb", null, null, null, null, null, "maxSpeed ASC")
        if (cursor.moveToFirst()) {
            val idColIndex = cursor.getColumnIndex("id")
            val markColIndex = cursor.getColumnIndex("mark")
            val modelColIndex = cursor.getColumnIndex("model")
            val colorColIndex = cursor.getColumnIndex("color")
            val maxSpeedColIndex = cursor.getColumnIndex("maxSpeed")
            val engineColIndex = cursor.getColumnIndex("engine")
            do {
                result.add(
                    "ID = ${cursor.getInt(idColIndex)}, Марка: ${cursor.getString(markColIndex)}, " +
                            "Модель: ${cursor.getString(modelColIndex)}, Цвет: ${cursor.getInt(colorColIndex)}, " +
                            "Максимальная скорость: ${cursor.getInt(maxSpeedColIndex)}, Мощность двигателя: " +
                            "${cursor.getFloat(engineColIndex)}"
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return result
    }
    private fun showGroupedByMaxSpeed(db: SQLiteDatabase): ArrayList<String> {
        val results = ArrayList<String>()
        val cursor = db.rawQuery("SELECT maxSpeed, GROUP_CONCAT(mark) FROM appdb GROUP BY maxSpeed", null)
        if (cursor.moveToFirst()) {
            do {
                results.add("Скорость: ${cursor.getInt(0)}, Марки: ${cursor.getString(1)}")
            } while (cursor.moveToNext())
        }
        cursor.close()
        return results
    }

    private fun showSumOfEnginePower(db: SQLiteDatabase): String {
        val cursor = db.rawQuery("SELECT SUM(engine) FROM appdb", null)
        var sum = 0
        if (cursor.moveToFirst()) {
            sum = cursor.getInt(0)
        }
        cursor.close()
        return sum.toString()
    }

    private fun showAverageEnginePowerAndSpeed(db: SQLiteDatabase): String {
        val cursor = db.rawQuery("SELECT AVG(engine), AVG(maxSpeed) FROM appdb", null)
        var result = ""
        if (cursor.moveToFirst()) {
            result = "Средняя мощность двигателя: ${cursor.getFloat(0)}, Средняя скорость: ${cursor.getInt(1)}"
        }
        cursor.close()
        return result
    }

    private fun showMaxEnginePower(db: SQLiteDatabase): String {
        val cursor = db.rawQuery("SELECT MAX(engine) FROM appdb", null)
        var maxPower = 0
        if (cursor.moveToFirst()) {
            maxPower = cursor.getInt(0)
        }
        cursor.close()
        return maxPower.toString()
    }

    private fun showFieldsWithMaxSpeedGreaterThan(db: SQLiteDatabase, value: Int): ArrayList<String> {
        val results = ArrayList<String>()
        val cursor = db.rawQuery("SELECT * FROM appdb WHERE maxSpeed > $value", null)
        if (cursor.moveToFirst()) {
            val idColIndex = cursor.getColumnIndex("id")
            val markColIndex = cursor.getColumnIndex("mark")
            val modelColIndex = cursor.getColumnIndex("model")
            val colorColIndex = cursor.getColumnIndex("color")
            val maxSpeedColIndex = cursor.getColumnIndex("maxSpeed")
            val engineColIndex = cursor.getColumnIndex("engine")
            do {
                results.add(
                    "ID = ${cursor.getInt(idColIndex)}, Марка: ${cursor.getString(markColIndex)}, " +
                            "Модель: ${cursor.getString(modelColIndex)}, Цвет: ${cursor.getInt(colorColIndex)}, " +
                            "Максимальная скорость: ${cursor.getInt(maxSpeedColIndex)}, Мощность двигателя: " +
                            "${cursor.getFloat(engineColIndex)}"
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return results
    }

    private fun showFieldsWithMaxSpeedLessThanAverage(db: SQLiteDatabase): ArrayList<String> {
        val results = ArrayList<String>()
        val cursor = db.rawQuery("SELECT * FROM appdb WHERE maxSpeed < (SELECT AVG(maxSpeed) FROM appdb)", null)
        if (cursor.moveToFirst()) {
            val idColIndex = cursor.getColumnIndex("id")
            val markColIndex = cursor.getColumnIndex("mark")
            val modelColIndex = cursor.getColumnIndex("model")
            val colorColIndex = cursor.getColumnIndex("color")
            val maxSpeedColIndex = cursor.getColumnIndex("maxSpeed")
            val engineColIndex = cursor.getColumnIndex("engine")
            do {
                results.add(
                    "ID = ${cursor.getInt(idColIndex)}, Марка: ${cursor.getString(markColIndex)}, " +
                            "Модель: ${cursor.getString(modelColIndex)}, Цвет: ${cursor.getInt(colorColIndex)}, " +
                            "Максимальная скорость: ${cursor.getInt(maxSpeedColIndex)}, Мощность двигателя: " +
                            "${cursor.getFloat(engineColIndex)}"
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return results
    }

    private fun showModelsWithEnginePowerGreaterThan(db: SQLiteDatabase, value: Int): ArrayList<String> {
        val results = ArrayList<String>()
        val cursor = db.rawQuery("SELECT model FROM appdb WHERE engine > $value", null)
        if (cursor.moveToFirst()) {
            do {
                results.add("Модель: ${cursor.getString(0)}")
            } while (cursor.moveToNext())
        }
        cursor.close()
        return results
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