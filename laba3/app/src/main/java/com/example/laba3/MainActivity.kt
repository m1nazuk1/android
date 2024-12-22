package com.example.laba3

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.laba3.R

class MainActivity : AppCompatActivity() {
    private lateinit var switchButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        switchButton = findViewById(R.id.switchButton)
        switchButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://developer.android.com/index.html"))
                )
            }
        })
    }
}