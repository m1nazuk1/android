package com.example.homework_rmpo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "event.db";
    private static final int DATABASE_VERSION = 2;

    public EventDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE events " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "description TEXT, " +
                "date INTEGER, " +
                "hasReminder INTEGER DEFAULT 0, " + // 0 - false, 1 - true
                "reminderTime INTEGER)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS events");
        onCreate(db);
    }

    public void addEvent(Event event) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", event.getTitle());
        values.put("description", event.getDescription());
        values.put("date", event.getDate().getTime());
        values.put("hasReminder", event.hasReminder() ? 1 : 0);
        values.put("reminderTime", event.getReminderTime());
        database.insert("events", null, values);
    }

    public List<Event> getEvents() {
        List<Event> events = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        String[] projection = {"id", "title", "description", "date", "hasReminder", "reminderTime"};
        Cursor cursor = database.query("events", projection, null, null, null, null, null);
        while (cursor.moveToNext()) {
            long eventId = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            long dateMillis = cursor.getLong(cursor.getColumnIndexOrThrow("date"));
            int hasReminder = cursor.getInt(cursor.getColumnIndexOrThrow("hasReminder"));
            long reminderTime = cursor.getLong(cursor.getColumnIndexOrThrow("reminderTime"));
            Event event = new Event();
            event.setId(eventId);
            event.setTitle(title);
            event.setDescription(description);
            event.setDate(new Date(dateMillis));
            event.setHasReminder(hasReminder == 1);
            event.setReminderTime(reminderTime);
            events.add(event);
        }
        cursor.close();
        return events;
    }

    public boolean updateEvent(Event event) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", event.getTitle());
        values.put("description", event.getDescription());
        values.put("date", event.getDate().getTime());
        values.put("hasReminder", event.hasReminder() ? 1 : 0);
        values.put("reminderTime", event.getReminderTime());
        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(event.getId())};
        int rowsUpdated = database.update("events", values, whereClause, whereArgs);
        return rowsUpdated > 0;
    }

    public boolean deleteEventById(long eventId) {
        SQLiteDatabase database = getWritableDatabase();
        String selection = "id=?";
        String[] selectionArgs = {String.valueOf(eventId)};
        int deletedRows = database.delete("events", selection, selectionArgs);
        return deletedRows > 0;
    }

    public void deleteAllEvents() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM events");
        db.execSQL("DELETE FROM sqlite_sequence WHERE name='events'");
        db.close();
    }

    public Event getEventById(long eventId) {
        SQLiteDatabase database = getReadableDatabase();
        String[] projection = {"id", "title", "description", "date", "hasReminder", "reminderTime"};
        String selection = "id=?";
        String[] selectionArgs = {String.valueOf(eventId)};
        Cursor cursor = database.query("events", projection, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            long dateMillis = cursor.getLong(cursor.getColumnIndexOrThrow("date"));
            int hasReminder = cursor.getInt(cursor.getColumnIndexOrThrow("hasReminder"));
            long reminderTime = cursor.getLong(cursor.getColumnIndexOrThrow("reminderTime"));
            Event event = new Event();
            event.setId(id);
            event.setTitle(title);
            event.setDescription(description);
            event.setDate(new Date(dateMillis));
            event.setHasReminder(hasReminder == 1);
            event.setReminderTime(reminderTime);
            cursor.close();
            return event;
        } else {
            cursor.close();
            return null;
        }
    }
}