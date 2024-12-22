package com.example.laba5

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var id: EditText
    private lateinit var fio: EditText
    private lateinit var faculty: EditText
    private lateinit var group: EditText
    private lateinit var add: Button
    private lateinit var update: Button
    private lateinit var show: Button
    private lateinit var delete: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        id = findViewById(R.id.id)
        fio = findViewById(R.id.fio)
        faculty = findViewById(R.id.faculty)
        group = findViewById(R.id.group)
        add = findViewById(R.id.add)
        update = findViewById(R.id.update)
        show = findViewById(R.id.show)
        delete = findViewById(R.id.delete)
        add.setOnClickListener {
            val fio = this.fio.text.toString()
            val faculty = this.faculty.text.toString()
            val group = this.group.text.toString()

            if (fio.isBlank() || faculty.isBlank() || group.isBlank()) {
                Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val values = ContentValues().apply {
                put("fio", fio)
                put("faculty", faculty)
                put("group_name", group)
            }
            val uri: Uri? = contentResolver.insert(StudentContentProvider.CONTENT_URI, values)
            Toast.makeText(this, "Студент добавлен: $uri", Toast.LENGTH_SHORT).show()
        }

        update.setOnClickListener {
            val id = this.id.text.toString()
            val fio = this.fio.text.toString()
            val faculty = this.faculty.text.toString()
            val group = this.group.text.toString()

            if (id.isBlank() || fio.isBlank() || faculty.isBlank() || group.isBlank()) {
                Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val values = ContentValues().apply {
                put("fio", fio)
                put("faculty", faculty)
                put("group_name", group)
            }

            val uri = Uri.withAppendedPath(StudentContentProvider.CONTENT_URI, id)
            val updatedRows = contentResolver.update(uri, values, null, null)
            Toast.makeText(this, "Обновлена запись для: $uri", Toast.LENGTH_SHORT).show()
        }

        show.setOnClickListener {
            startActivity(Intent(this@MainActivity, ClaimerActivity::class.java))
        }

        delete.setOnClickListener {
            val id = this.id.text.toString()

            if (id.isBlank()) {
                Toast.makeText(this, "Укажите ID для удаления", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val uri = Uri.withAppendedPath(StudentContentProvider.CONTENT_URI, id)
            val count = contentResolver.delete(uri, null, null)
            Toast.makeText(this, "Удалено записей: $count", Toast.LENGTH_SHORT).show()
        }
    }
}