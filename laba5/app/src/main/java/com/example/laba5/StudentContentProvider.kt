package com.example.laba5

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

class StudentContentProvider : ContentProvider() {
    companion object {
        const val AUTHORITY = "com.example.laba5.provider"
        const val PATH = "students"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$PATH")

        const val STUDENTS = 1
        const val STUDENT_ID = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, PATH, STUDENTS)
            addURI(AUTHORITY, "$PATH/#", STUDENT_ID)
        }
    }

    private lateinit var dbHelper: DBHelper

    override fun onCreate(): Boolean {
        dbHelper = DBHelper(context!!)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val db = dbHelper.readableDatabase
        return when (uriMatcher.match(uri)) {
            STUDENTS -> db.query("students", projection, selection, selectionArgs, null, null, sortOrder)
            STUDENT_ID -> {
                val id = ContentUris.parseId(uri)
                db.query(
                    "students",
                    projection,
                    "_id = ?",
                    arrayOf(id.toString()),
                    null,
                    null,
                    sortOrder
                )
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = dbHelper.writableDatabase
        val id = db.insert("students", null, values)
        context?.contentResolver?.notifyChange(uri, null)
        return ContentUris.withAppendedId(CONTENT_URI, id)
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val db = dbHelper.writableDatabase
        val count: Int

        when (uriMatcher.match(uri)) {
            STUDENTS -> {
                count = db.update("students", values, selection, selectionArgs)
            }
            STUDENT_ID -> {
                val id = uri.lastPathSegment
                count = db.update(
                    "students",
                    values,
                    "_id = ?",
                    arrayOf(id)
                )
            }
            else -> throw IllegalArgumentException("Unknown URI $uri")
        }

        context?.contentResolver?.notifyChange(uri, null)
        return count
    }


    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db = dbHelper.writableDatabase
        val count = db.delete("students", selection, selectionArgs)
        context?.contentResolver?.notifyChange(uri, null)
        return count
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            STUDENTS -> "vnd.android.cursor.dir/$AUTHORITY.$PATH"
            STUDENT_ID -> "vnd.android.cursor.item/$AUTHORITY.$PATH"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }
}
