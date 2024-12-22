package com.example.homework_rmpo;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EventAdapter eventAdapter;
    private EventDbHelper dbHelper;
    Event selectedEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new EventDbHelper(this);
        eventAdapter = new EventAdapter(this, dbHelper.getEvents());

        // Запрос разрешения на уведомления (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }

        Button addEventButton = findViewById(R.id.addEventButton);
        Button editEventButton = findViewById(R.id.editEventButton);
        Button deleteEventButton = findViewById(R.id.deleteEventButton);
        Button deleteAllButton = findViewById(R.id.deleteAllButton);

        Button startReminderService = findViewById(R.id.startServiceButton);
        Button stopReminderService = findViewById(R.id.stopServiceButton);

        ListView eventListView = findViewById(R.id.eventListView);
        eventListView.setAdapter(eventAdapter);
        eventAdapter.updateData(dbHelper.getEvents());

        startReminderService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(MainActivity.this, ReminderService.class));
                Toast.makeText(MainActivity.this, "Служба запущена", Toast.LENGTH_SHORT).show();
            }
        });

        stopReminderService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MainActivity.this, ReminderService.class));
                Toast.makeText(MainActivity.this, "Служба остановлена", Toast.LENGTH_SHORT).show();
            }
        });

        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // ...
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedEvent = (Event) parent.getItemAtPosition(position);
                Toast.makeText(MainActivity.this, "Выбрано событие: " + selectedEvent.getTitle(), Toast.LENGTH_SHORT).show();
                showEventDialog(selectedEvent);
            }
        });

        addEventButton.setOnClickListener(new View.OnClickListener() {
            // ...
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        editEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { showEditDialog(); }
        });

        deleteEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { deleteEvent(v); }
        });

        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { showDeleteAllConfirmationDialog(); }
        });

        Button nextEventButton = findViewById(R.id.nextEventButton);
        nextEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextEventDialog();
            }
        });
    }

    private void showNextEventDialog() {
        long currentTime = System.currentTimeMillis();
        List<Event> events = dbHelper.getEvents();

        Event nextEvent = null;
        for (Event e : events) {
            if (e.hasReminder() && e.getReminderTime() > currentTime) {
                if (nextEvent == null || e.getReminderTime() < nextEvent.getReminderTime()) {
                    nextEvent = e;
                }
            }
        }

        if (nextEvent != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Следующее событие");

            StringBuilder sb = new StringBuilder();
            sb.append("Название: ").append(nextEvent.getTitle()).append("\n");
            sb.append("Описание: ").append(nextEvent.getDescription()).append("\n");
            sb.append("Дата события: ").append(formatDate(nextEvent.getDate().getTime())).append("\n");
            sb.append("Напоминание: ").append(formatDate(nextEvent.getReminderTime()));

            builder.setMessage(sb.toString());
            builder.setPositiveButton("OK", null);
            builder.show();
        } else {
            Toast.makeText(this, "Нет предстоящих событий с напоминаниями", Toast.LENGTH_SHORT).show();
        }
    }

    private String formatDate(long timeInMillis) {
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("HH:mm (dd.MM.yyyy)", java.util.Locale.getDefault());
        return dateFormat.format(new java.util.Date(timeInMillis));
    }
    // Обновление данных, полученных из второй активности
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Обновление данных в адаптере
            eventAdapter.updateData(dbHelper.getEvents());
        }
    }

    private void showEventDialog(final Event event) {
        // Создаем AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Получаем макет диалога
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_event, null);
        builder.setView(dialogView);

        // Находим TextView для названия и описания события
        TextView titleTextView = dialogView.findViewById(R.id.titleTextViewDialog);
        TextView descriptionTextView = dialogView.findViewById(R.id.descriptionTextViewDialog);

        // Устанавливаем текст в TextView
        titleTextView.setText(event.getTitle());
        descriptionTextView.setText(event.getDescription());

        // Добавляем кнопку "Напоминание"
        builder.setNeutralButton("Напоминание", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Открываем диалог для установки даты и времени напоминания
                showReminderDialog(selectedEvent);
            }
        });

        // Устанавливаем кнопку "Закрыть"
        builder.setPositiveButton("Закрыть", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Создаем и отображаем AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showReminderDialog(final Event event) {
        // Получаем текущую дату и время
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Создаем TimePickerDialog для выбора времени
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                // Получаем выбранное время

                // Создаем DatePickerDialog для выбора даты
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // Создаем Calendar и устанавливаем выбранную дату и время
                        Calendar reminderTime = Calendar.getInstance();
                        reminderTime.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);
                        reminderTime.set(Calendar.SECOND, 0);
                        reminderTime.set(Calendar.MILLISECOND, 0); // Устанавливаем миллисекунды в 0

                        // Получаем название события
                        String eventTitle = event.getTitle();

                        // Устанавливаем напоминание с передачей названия события
                        setReminder(reminderTime.getTimeInMillis(), eventTitle, event);
                    }
                }, year, month, day);

                // Показываем диалог выбора даты
                datePickerDialog.show();
            }
        }, hour, minute, true);

        // Показываем диалог выбора времени
        timePickerDialog.show();
    }

    // Метод, который вызывается при установке напоминания
    private void setReminder(long timeInMillis, String eventTitle, Event event) {
        // Получаем доступ к ReminderService
        Intent reminderServiceIntent = new Intent(this, ReminderService.class);

        // Передаем данные для установки напоминания
        reminderServiceIntent.putExtra("REMINDER_TIME", timeInMillis);
        reminderServiceIntent.putExtra("EVENT_TITLE", eventTitle);
        reminderServiceIntent.putExtra("EVENT_ID", event.getId());
        reminderServiceIntent.putExtra("HAS_REMINDER", true);

        // Обновляем данные в базе данных
        event.setReminderTime(timeInMillis);
        event.setHasReminder(true);
        dbHelper.updateEvent(event);

        // Обновляем данные в адаптере
        eventAdapter.updateData(dbHelper.getEvents());

        Toast.makeText(this, "Напоминание установлено", Toast.LENGTH_SHORT).show();

        // Запускаем сервис для установки напоминания
        startService(reminderServiceIntent);
    }

    private void showEditDialog() {
        // Создаем AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Какое событие вы хотите изменить?");

        // Создаем поле ввода для ID события
        final EditText eventIdInput = new EditText(this);
        eventIdInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(eventIdInput);

        // Устанавливаем кнопку "Далее"
        builder.setPositiveButton("Далее", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Получаем введенный номер события
                String eventIdString = eventIdInput.getText().toString();

                try {
                    // Парсим введенный текст в число (ID события)
                    long eventId = Long.parseLong(eventIdString);

                    // Получаем событие по ID из базы данных
                    Event selectedEvent = dbHelper.getEventById(eventId);

                    if (selectedEvent != null) {
                        // Показываем диалог для изменения данных события
                        showEditEventDetailsDialog(selectedEvent);
                    } else {
                        Toast.makeText(MainActivity.this, "Событие не найдено", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Введите корректный номер события", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Устанавливаем кнопку "Отмена"
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Показываем AlertDialog
        builder.show();
    }

    private void showEditEventDetailsDialog(final Event event) {
        // Создаем AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Создаем контейнер LinearLayout
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        // Создаем EditText для названия события
        final EditText titleEditText = new EditText(this);
        titleEditText.setHint("Название");
        // Устанавливаем текущее значение названия события
        titleEditText.setText(event.getTitle());
        linearLayout.addView(titleEditText);

        // Создаем EditText для описания события
        final EditText descriptionEditText = new EditText(this);
        descriptionEditText.setHint("Описание");
        // Устанавливаем текущее значение описания события
        descriptionEditText.setText(event.getDescription());
        linearLayout.addView(descriptionEditText);

        // Сохраняем предыдущие значения
        //final long oldReminderTime = event.getReminderTime();
        //final boolean oldHasReminder = event.hasReminder();

        // Устанавливаем контейнер LinearLayout в AlertDialog
        builder.setView(linearLayout);

        // Устанавливаем кнопку "Сохранить"
        builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Получаем измененные значения из EditText
                String newTitle = titleEditText.getText().toString();
                String newDescription = descriptionEditText.getText().toString();

                //event.setReminderTime(oldReminderTime);
                //event.setHasReminder(oldHasReminder);

                // Проверяем, были ли введены новые значения
                if (!newTitle.equals(event.getTitle()) || !newDescription.equals(event.getDescription())) {
                    // Обновляем событие в базе данных только при изменении
                    event.setTitle(newTitle);
                    event.setDescription(newDescription);

                    // При изменении любого поля также обновляем дату на текущую
                    //event.setDate(new Date());

                    boolean eventUpdated = dbHelper.updateEvent(event);

                    if (eventUpdated) {
                        Toast.makeText(MainActivity.this, "Событие изменено", Toast.LENGTH_SHORT).show();
                        // Обновляем данные в адаптере
                        eventAdapter.updateData(dbHelper.getEvents());
                    } else {
                        Toast.makeText(MainActivity.this, "Ошибка при изменении события", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Если ничего не изменилось, просто закрываем диалог
                    dialog.dismiss();
                }
            }
        });

        // Устанавливаем кнопку "Отмена"
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Показываем AlertDialog
        builder.show();
    }

    public void deleteEvent(View view) {
        // Создаем AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Введите номер события для удаления:");

        // Создаем поле ввода внутри AlertDialog
        final EditText input = new EditText(this);
        builder.setView(input);

        // Устанавливаем кнопку "Удалить"
        builder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Получаем введенный номер события
                String eventIdString = input.getText().toString();

                try {
                    // Парсим введенный текст в число (ID события)
                    long eventId = Long.parseLong(eventIdString);

                    // Удаляем событие из базы данных
                    boolean eventDeleted = dbHelper.deleteEventById(eventId);

                    if (eventDeleted) {
                        Toast.makeText(MainActivity.this, "Событие удалено", Toast.LENGTH_SHORT).show();
                        // Обновляем данные в адаптере
                        eventAdapter.updateData(dbHelper.getEvents());
                    } else {
                        Toast.makeText(MainActivity.this, "Событие не найдено", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Введите корректный номер события", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Устанавливаем кнопку "Отмена"
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Показываем AlertDialog
        builder.show();
    }

    private void showDeleteAllConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удалить все события?");
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Вызываем метод для удаления всех записей из базы данных
                dbHelper.deleteAllEvents();

                // Обновляем данные в адаптере
                eventAdapter.updateData(dbHelper.getEvents());

                Toast.makeText(MainActivity.this, "Все события удалены", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}

