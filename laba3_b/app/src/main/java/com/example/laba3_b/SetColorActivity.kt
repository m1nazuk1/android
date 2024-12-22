package com.example.laba3_b

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText

class SetColorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_color)

        findViewById<Button>(R.id.btnSaveColor).setOnClickListener {
            val red = findViewById<EditText>(R.id.editTextRed).text.toString().toInt()
            val green = findViewById<EditText>(R.id.editTextGreen).text.toString().toInt()
            val blue = findViewById<EditText>(R.id.editTextBlue).text.toString().toInt()
            AppData.textColor = Color.rgb(red, green, blue)
            finish()
        }
    }
}