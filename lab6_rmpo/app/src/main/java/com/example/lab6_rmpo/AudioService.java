package com.example.lab6_rmpo;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class AudioService extends Service {

    private MediaPlayer mediaPlayer;
    private String currentFilePath;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String filePath = intent.getStringExtra("file_path");
        if (filePath != null) {
            if (filePath.equals(currentFilePath) && mediaPlayer != null && mediaPlayer.isPlaying()) {
                stopPlaying();
            } else {
                playAudio(filePath);
            }
        } else {
            Log.e("AudioService", "File path is null!");
        }
        return START_NOT_STICKY;
    }

    private void playAudio(String filePath) {
        try {
            stopPlaying();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, android.net.Uri.parse(filePath));
            mediaPlayer.prepare();
            mediaPlayer.start();
            currentFilePath = filePath;
            mediaPlayer.setOnCompletionListener(mp -> {
                Log.i("AudioService", "Playback completed for file: " + filePath);
                stopSelf();
            });
        } catch (IOException e) {
            Log.e("AudioService", "Error playing file: " + filePath, e);
        } catch (IllegalArgumentException e) {
            Log.e("AudioService", "Invalid file path: " + filePath, e);
        }
    }

    private void stopPlaying() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
            currentFilePath = null;
        }
    }

    @Override
    public void onDestroy() {
        stopPlaying();
        super.onDestroy();
    }
}