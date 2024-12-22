package com.example.homework_rmpo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

public class AddEventActivity extends AppCompatActivity {
    private EventDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        dbHelper = new EventDbHelper(this);
    }

    public void onSaveButtonClick(View view) {
        // Получаем данные из текстовых полей
        EditText titleEditText = findViewById(R.id.titleEditText);
        EditText descriptionEditText = findViewById(R.id.descriptionEditText);

        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        // Создаем новое событие
        Event newEvent = createEvent(title, description);

        // Добавляем новое событие в базу данных
        dbHelper.addEvent(newEvent);

        // Перейдите обратно к главной активности
        navigateBackToMainActivity();
    }

    private Event createEvent(String title, String description) {
        Event newEvent = new Event();
        newEvent.setTitle(title);
        newEvent.setDescription(description);
        newEvent.setDate(new Date());
        return newEvent;
    }

    private void navigateBackToMainActivity() {
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
