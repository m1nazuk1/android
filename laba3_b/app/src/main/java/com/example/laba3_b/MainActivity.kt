package com.example.laba3_b

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnSetText).setOnClickListener {
            startActivity(Intent(this, SetTextActivity::class.java))
        }
        findViewById<Button>(R.id.btnSetHorizontal).setOnClickListener {
            startActivity(Intent(this, SetHorizontalActivity::class.java))
        }
        findViewById<Button>(R.id.btnSetVertical).setOnClickListener {
            startActivity(Intent(this, SetVerticalActivity::class.java))
        }
        findViewById<Button>(R.id.btnSetColor).setOnClickListener {
            startActivity(Intent(this, SetColorActivity::class.java))
        }
        findViewById<Button>(R.id.btnShowResult).setOnClickListener {
            startActivity(Intent(this, ShowResultActivity::class.java))
        }
    }
}