package com.example.homework_rmpo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NextEventActivity extends AppCompatActivity {
    private EventDbHelper dbHelper;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView dateTextView;
    private TextView reminderTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_event);

        dbHelper = new EventDbHelper(this);

        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        dateTextView = findViewById(R.id.dateTextView);
        reminderTextView = findViewById(R.id.reminderTextView);

        long eventId = getIntent().getLongExtra("EVENT_ID", -1);
        if (eventId == -1) {
            Toast.makeText(this, "Событие не найдено", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Event event = dbHelper.getEventById(eventId);
        if (event != null) {
            titleTextView.setText(event.getTitle());
            descriptionTextView.setText(event.getDescription());

            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm (dd.MM.yyyy)", Locale.getDefault());
            dateTextView.setText("Дата: " + dateFormat.format(event.getDate()));

            if (event.hasReminder()) {
                reminderTextView.setVisibility(View.VISIBLE);
                reminderTextView.setText("Напоминание: " + dateFormat.format(new Date(event.getReminderTime())));
            } else {
                reminderTextView.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(this, "Событие не найдено", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}