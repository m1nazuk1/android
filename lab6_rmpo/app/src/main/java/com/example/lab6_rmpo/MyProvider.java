package com.example.lab6_rmpo;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class MyProvider extends AppWidgetProvider {

    private static final String ACTION_ON_CLICK = "com.example.lab6_rmpo.itemonclick";
    public static final String ITEM_POSITION = "item_position";
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int id : appWidgetIds) {
            updateWidget(context, appWidgetManager, id);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_ON_CLICK.equals(intent.getAction())) {
            String filePath = intent.getStringExtra("file_path");
            Log.i("MyProvider", "Clicked file: " + filePath);
            if (filePath != null) {
                Intent serviceIntent = new Intent(context, AudioService.class);
                serviceIntent.putExtra("file_path", filePath);
                context.startService(serviceIntent);
            }
        }
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
        setUpdateTV(rv, context, appWidgetId);
        setList(rv, context, appWidgetId);
        setListClick(rv, context, appWidgetId);
        appWidgetManager.updateAppWidget(appWidgetId, rv);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lvList);
    }

    private void setUpdateTV(RemoteViews rv, Context context, int appWidgetId) {
        rv.setTextViewText(R.id.tvUpdate, "Update\n" + sdf.format(new Date(System.currentTimeMillis())));
        rv.setTextViewTextSize(R.id.tvUpdate, TypedValue.COMPLEX_UNIT_SP, 16);
        Intent intent = new Intent(context, MyProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{appWidgetId});
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_MUTABLE);
        rv.setOnClickPendingIntent(R.id.tvUpdate, pendingIntent);
    }

    private void setList(RemoteViews rv, Context context, int appWidgetId) {
        Intent intent = new Intent(context, MyService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        rv.setRemoteAdapter(R.id.lvList, intent);
    }

    private void setListClick(RemoteViews rv, Context context, int appWidgetId) {
        Intent intent = new Intent(context, MyProvider.class);
        intent.setAction(ACTION_ON_CLICK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE);
        rv.setPendingIntentTemplate(R.id.lvList, pendingIntent);
    }
}