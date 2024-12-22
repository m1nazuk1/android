package com.example.laba3_b

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText

class SetTextActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_text)

        findViewById<Button>(R.id.btnSaveText).setOnClickListener {
            val text = findViewById<EditText>(R.id.editTextText).text.toString()
            AppData.text = text
            finish()
        }
    }
}