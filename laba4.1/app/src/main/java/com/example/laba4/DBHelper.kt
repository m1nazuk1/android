package com.example.laba4

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

internal class DBHelper(context: Context?) : SQLiteOpenHelper(context, "appDB", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE appdb (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "mark TEXT, " +
                    "model TEXT, " +
                    "color INTEGER, " +
                    "maxSpeed INTEGER, " +
                    "engine REAL" + ");"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS appdb")
        onCreate(db)
    }
    fun insert(mark: String, model: String, color: Int, maxSpeed: Int, engine: Float) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("mark", mark)
            put("model", model)
            put("color", color)
            put("maxSpeed", maxSpeed)
            put("engine", engine)
        }
        db.insert("appdb", null, values)
        db.close()
    }
}
