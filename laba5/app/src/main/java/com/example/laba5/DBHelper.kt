package com.example.laba5

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


internal class DBHelper(context: Context?) : SQLiteOpenHelper(context, "students.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE students (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "fio TEXT NOT NULL, " +
                    "faculty TEXT NOT NULL, " +
                    "group_name TEXT NOT NULL)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS students")
        onCreate(db)
    }

}
