package com.yys.cs446.es;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yys.cs446.es.castle_model.controller;
import com.yys.cs446.es.views.tileView;

public class GameActivity extends AppCompatActivity {

    private tileView customView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        customView = (tileView) findViewById(R.id.tileView);

        //controller gameController = new controller(customView);
        //gameController.start();
    }
}
