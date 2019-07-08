package com.yys.cs446.es;

import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.lang.reflect.Type;

public class GameActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private tileView customView;
    private controller gameController;
    private MediaPlayer gameTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Typeface faFont = Typeface.createFromAsset(getAssets(), "fa_fr.otf");
        //Button testbutton = (Button)findViewById(R.id.button2);
        //testbutton.setTypeface(faFont);

        customView = (tileView) findViewById(R.id.tileView);

        gameController = new controller(customView);
        gameController.start();

        gameTheme = MediaPlayer.create(GameActivity.this, R.raw.es_game_theme);
        gameTheme.setLooping(true);
        gameTheme.start();
    }

    public void popupMenuOptions(View v) {
        Toast.makeText(getApplicationContext(), "Options menu", Toast.LENGTH_SHORT).show();
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
            case R.id.build_4:
                Toast.makeText(getApplicationContext(), "Start improving tile", Toast.LENGTH_SHORT).show();
                break;
            case R.id.expand_1:
                Toast.makeText(getApplicationContext(), "Expand menu 1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.defend_1:
                Toast.makeText(getApplicationContext(), "Defend menu 1", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}
