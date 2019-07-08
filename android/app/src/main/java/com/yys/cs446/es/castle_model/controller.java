package com.yys.cs446.es.castle_model;

import android.util.Log;

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

        //update spawn location to avoid throwing error
        int[] spawn;
        while (true) {
            spawn = new int[]{4,4}; // g.getValidSpawnLocation();
            try {
                playerOne = new player(g, spawn[0], spawn[1]);
                break;
            } catch (Error e) {
                Log.d("DEBUG:", "controller: Failed to spawn player, retrying");
            }
        }

        // init states
        view.update(g, playerOne);
        //view.setCamera(spawn[0], spawn[1]);

        // debug (should check for failed build)
        playerOne.build_unit(unit.TYPE.WORKER);
        playerOne.build_unit(unit.TYPE.TROOP);
        //playerOne.build_unit(unit.TYPE.SETTLER);

        playerOne.setTargetResource(player.RESOURCES.FOOD);

    }

    // called after constructor (in GameActivity), starts simulation
    public void start() {
        Timer timer = new Timer(true);
        TimerTask taskNew = new TimerTask() {
            @Override
            public void run() {
                // player (UI) actions will call methods below to change unit/tile states

                //step player/unit specific events
                playerOne.act();

                // GRID/TILE objects are only data structs, PLAYER/UNIT move simulation
                //step simulation events forward with current states
                //g.resolve_all();

                //pass updates structs to view
                view.update(g, playerOne);
            }
        };
        timer.scheduleAtFixedRate(taskNew, 0, (int)(0.1*1000));
    }

    // called by ui to give specific order to change playerOne's policies

    public void giveWorkersOrder(player.RESOURCES r) {
        playerOne.setTargetResource(r);
    }

    public void startBuildUnit(unit.TYPE t) {
        playerOne.start_build_unit(t);
    }
}
