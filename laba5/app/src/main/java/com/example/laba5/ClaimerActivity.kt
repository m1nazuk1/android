package com.example.laba5

import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ClaimerActivity : AppCompatActivity() {
    private lateinit var data: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_claimer)
        data = findViewById(R.id.data)
        val uri = Uri.parse("content://com.example.laba5.provider/students")
        val projection = arrayOf("_id", "fio", "faculty", "group_name")

        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow("_id"))
                val fio = it.getString(it.getColumnIndexOrThrow("fio"))
                val faculty = it.getString(it.getColumnIndexOrThrow("faculty"))
                val group = it.getString(it.getColumnIndexOrThrow("group_name"))

                data.append("ID: $id,\n Фамилия, имя, отчество: $fio,\nФакультет: $faculty,\nГруппа: $group\n\n")
            }
        }

    }
}