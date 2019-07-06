package com.yys.cs446.es.castle_model;

import com.yys.cs446.es.views.tileView;

import java.util.Timer;
import java.util.TimerTask;

public class controller {

    private tileView view;
    private grid g;
    private player playerOne;

    public controller (tileView v) {
        view = v;
        g = new grid();

        int spawnX = 5;
        int spawnY = 5;
        playerOne = new player(g, spawnX, spawnY);

        // init states
        view.update(g, playerOne);
    }

    public void start() {
        Timer timer = new Timer(true);
        TimerTask taskNew = new TimerTask() {
            @Override
            public void run() {
                //step internal player actions
                //playerOne.collectTiles();
                //playerOne.expandTerritory();

                //step general grid actions
                //g.getGrid()[0][0].resolve();

                view.update(g, playerOne);
            }
        };
        timer.scheduleAtFixedRate(taskNew, 0, (int)(0.5*1000));
    }
}
