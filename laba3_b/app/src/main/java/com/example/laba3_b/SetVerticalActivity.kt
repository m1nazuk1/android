package com.example.laba3_b

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class SetVerticalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_vertical)

        findViewById<Button>(R.id.btnTop).setOnClickListener {
            AppData.verticalAlignment = "Top"
            finish()
        }
        findViewById<Button>(R.id.btnCenter).setOnClickListener {
            AppData.verticalAlignment = "Center"
            finish()
        }
        findViewById<Button>(R.id.btnBottom).setOnClickListener {
            AppData.verticalAlignment = "Bottom"
            finish()
        }
    }
}