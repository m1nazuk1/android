package com.example.laba3_b

import android.os.Bundle
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class SetHorizontalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_horizontal)

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupHorizontal)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            AppData.horizontalAlignment = when (checkedId) {
                R.id.radioLeft -> "Left"
                R.id.radioCenter -> "Center"
                R.id.radioRight -> "Right"
                else -> "Center"
            }
            finish()
        }
    }
}