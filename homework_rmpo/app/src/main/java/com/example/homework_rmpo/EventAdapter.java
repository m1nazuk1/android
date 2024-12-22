package com.example.homework_rmpo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends BaseAdapter {
    private Context context;
    private List<Event> events;

    public EventAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        }

        Event event = events.get(position);

        TextView idTextView = convertView.findViewById(R.id.idTextView);
        TextView titleTextView = convertView.findViewById(R.id.titleTextView);
        TextView dateTextView = convertView.findViewById(R.id.dateTextView);
        TextView reminderTextView = convertView.findViewById(R.id.reminderTextView); // Новое поле

        // Отображение ID
        idTextView.setText("№: " + event.getId());

        titleTextView.setText(event.getTitle());

        // Форматирование даты в удобочитаемый формат
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm (dd.MM.yyyy)", Locale.getDefault());
        String formattedDate = dateFormat.format(event.getDate());
        dateTextView.setText(formattedDate);

        // Отображение информации о напоминании, если оно установлено
        if (event.hasReminder()) {
            SimpleDateFormat reminderFormat = new SimpleDateFormat("HH:mm (dd.MM.yyyy)", Locale.getDefault());
            String reminderTime = reminderFormat.format(new Date(event.getReminderTime()));
            String reminderInfo = "Напоминание - " + reminderTime;
            reminderTextView.setText(reminderInfo);
            reminderTextView.setVisibility(View.VISIBLE); // Сделать видимым
        } else {
            reminderTextView.setVisibility(View.GONE); // Скрыть, если напоми-нание не установлено
        }

        return convertView;
    }

    public void updateData(List<Event> updatedEvents) {
        events.clear();
        events.addAll(updatedEvents);
        notifyDataSetChanged();
    }
}
