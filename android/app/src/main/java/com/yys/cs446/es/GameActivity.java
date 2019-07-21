package com.yys.cs446.es;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.yys.cs446.es.castle_model.controller;
import com.yys.cs446.es.castle_model.player;
import com.yys.cs446.es.castle_model.unit;
import com.yys.cs446.es.views.tileView;

import java.io.File;

import java.lang.reflect.Type;

public class GameActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private tileView customView;
    private controller gameController;
    private MediaPlayer gameTheme;

    private boolean recordState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        customView = (tileView) findViewById(R.id.tileView);

        customView.buildDrawingCache();

        gameController = new controller(customView);
        gameController.start();

        gameTheme = MediaPlayer.create(GameActivity.this, R.raw.es_game_theme);
        gameTheme.setLooping(true);
        //gameTheme.start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        gameTheme.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        gameTheme.pause();
    }

    public void popupMenuOptions(View v) {
        PopupMenu pu = new PopupMenu(this, v);
        pu.setOnMenuItemClickListener(this);
        pu.inflate(R.menu.options_menu);
        // must edit menu instance just before showing
        if (recordState) {
            pu.getMenu().findItem(R.id.options_2).setTitle("Stop current recording");
        } else {
            pu.getMenu().findItem(R.id.options_2).setTitle("Start new recording");
        }
        pu.show();
    }

    public void popupMenuGather(View v) {
        popupMenuType(v, R.menu.gather_menu);
    }

    public void popupMenuBuild(View v) {
        popupMenuType(v, R.menu.build_menu);
    }

    public void popupMenuExpand(View v) {
        popupMenuType(v, R.menu.expand_menu);
    }

    public void popupMenuDefend(View v) {
        popupMenuType(v, R.menu.defend_menu);
    }

    public void popupMenuType(View v, int menures) {
        PopupMenu pu = new PopupMenu(this, v);
        pu.setOnMenuItemClickListener(this);
        pu.inflate(menures);
        pu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.gather_1:
                Toast.makeText(getApplicationContext(), "Gathering lumber", Toast.LENGTH_SHORT).show();
                gameController.giveWorkersOrder(player.RESOURCES.LUMBER);
                break;
            case R.id.gather_2:
                Toast.makeText(getApplicationContext(), "Gathering food", Toast.LENGTH_SHORT).show();
                gameController.giveWorkersOrder(player.RESOURCES.FOOD);
                break;
            case R.id.gather_3:
                Toast.makeText(getApplicationContext(), "Gathering stone", Toast.LENGTH_SHORT).show();
                gameController.giveWorkersOrder(player.RESOURCES.STONE);
                break;
            case R.id.gather_4:
                Toast.makeText(getApplicationContext(), "Retreating Workers", Toast.LENGTH_SHORT).show();
                gameController.giveWorkersOrder(player.RESOURCES.NONE);
                break;
            case R.id.build_1:
                Toast.makeText(getApplicationContext(), "Start producing worker", Toast.LENGTH_SHORT).show();
                gameController.startBuildUnit(unit.TYPE.WORKER);
                break;
            case R.id.build_2:
                Toast.makeText(getApplicationContext(), "Start producing troop", Toast.LENGTH_SHORT).show();
                gameController.startBuildUnit(unit.TYPE.TROOP);
                break;
            case R.id.build_3:
                Toast.makeText(getApplicationContext(), "Start producing settler", Toast.LENGTH_SHORT).show();
                gameController.startBuildUnit(unit.TYPE.SETTLER);
                break;
            case R.id.expand_1:
                Toast.makeText(getApplicationContext(), "Select tile to expand to", Toast.LENGTH_SHORT).show();
                gameController.selectExpandTile();
                break;
            case R.id.expand_2:
                Toast.makeText(getApplicationContext(), "Toggle stronghold heal", Toast.LENGTH_SHORT).show();
                gameController.toggleHeal();
                break;
            case R.id.defend_1:
                Toast.makeText(getApplicationContext(), "Advancing troops", Toast.LENGTH_SHORT).show();
                gameController.selectDefendArea();
                break;
            case R.id.options_1:
                Toast.makeText(getApplicationContext(), "Toggle Pause", Toast.LENGTH_SHORT).show();
                gameController.togglePause();
                break;
            case R.id.options_2:

                break;
            case R.id.options_3:

                break;
        }
        return true;
    }
}
