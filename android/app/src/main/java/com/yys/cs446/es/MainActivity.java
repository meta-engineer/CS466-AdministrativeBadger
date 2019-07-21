package com.yys.cs446.es;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer titleTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleTheme = MediaPlayer.create(MainActivity.this, R.raw.es_title_theme);
        titleTheme.setLooping(true);
        titleTheme.start();
    }

    public void launchGameActivity(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        titleTheme.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        titleTheme.seekTo(0);
        titleTheme.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        titleTheme.pause();
    }
}
