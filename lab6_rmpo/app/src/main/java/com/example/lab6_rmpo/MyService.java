package com.example.lab6_rmpo;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class MyService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MyFactory(getApplicationContext(), intent);
    }
}