package com.example.lab6_rmpo;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MyFactory implements RemoteViewsFactory {

    private ArrayList<String> data;
    private ArrayList<String> filePaths;
    private final Context context;
    private final SimpleDateFormat sdf;
    private final int widgetID;

    public MyFactory(Context ctx, Intent intent) {
        this.context = ctx;
        this.sdf = new SimpleDateFormat("HH:mm:ss");
        this.widgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        this.data = new ArrayList<>();
        this.filePaths = new ArrayList<>();
    }

    @Override
    public void onCreate() {
        data = new ArrayList<>();
        filePaths = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.item);
        rv.setTextViewText(R.id.tvItemText, data.get(position));
        Intent clickIntent = new Intent();
        clickIntent.putExtra("file_path", filePaths.get(position));
        rv.setOnClickFillInIntent(R.id.tvItemText, clickIntent);
        return rv;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onDataSetChanged() {
        data.clear();
        filePaths.clear();
        getMP3FilesInfo();
    }

    @Override
    public void onDestroy() {
        data.clear();
        filePaths.clear();
    }

    private void getMP3FilesInfo() {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST
        };

        try (Cursor cursor = contentResolver.query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);

                do {
                    long id = cursor.getLong(idColumn);
                    String title = cursor.getString(titleColumn);
                    String artist = cursor.getString(artistColumn);

                    Uri contentUri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
                    File file = new File(contentUri.getPath());

                    if (title == null || title.isEmpty()) title = file.getName();
                    String songInfo = (artist != null && !artist.isEmpty()) ? (artist + " - " + title) : title;

                    data.add(songInfo);
                    filePaths.add(contentUri.toString());
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("MyFactory", "Error retrieving MP3 files", e);
        }
    }
}