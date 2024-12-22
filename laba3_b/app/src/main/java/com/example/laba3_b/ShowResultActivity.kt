package com.example.laba3_b

import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ShowResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_result)

        val textViewResult = findViewById<TextView>(R.id.textViewResult).apply {
            text = AppData.text
            setTextColor(AppData.textColor)
        }

        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )

        layoutParams.gravity = (when (AppData.horizontalAlignment) {
            "Left" -> Gravity.START
            "Center" -> Gravity.CENTER_HORIZONTAL
            "Right" -> Gravity.END
            else -> Gravity.CENTER_HORIZONTAL
        } or when (AppData.verticalAlignment) {
            "Top" -> Gravity.TOP
            "Center" -> Gravity.CENTER_VERTICAL
            "Bottom" -> Gravity.BOTTOM
            else -> Gravity.CENTER_VERTICAL
        })

        textViewResult.layoutParams = layoutParams
    }
}