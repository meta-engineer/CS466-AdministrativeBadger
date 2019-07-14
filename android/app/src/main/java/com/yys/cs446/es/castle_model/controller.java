package com.yys.cs446.es.castle_model;

import android.util.Log;

import com.yys.cs446.es.views.tileView;

import java.util.Timer;
import java.util.TimerTask;

public class controller {

    private tileView view;
    private grid g;
    private player playerOne;
    private player playerTwo;

    public controller (tileView v) {
        view = v;
        g = new grid();

        //update spawn location to avoid throwing error
        int[] spawn;
        while (true) {
            spawn = g.getValidSpawnLocation();
            try {
                playerOne = new player(g, spawn[0], spawn[1]);
                break;
            } catch (Error e) {
                Log.d("DEBUG:", "controller: Failed to spawn player, retrying");
            }
        }

        playerOne.add_resource(player.RESOURCES.STONE, 100);
        playerOne.add_resource(player.RESOURCES.FOOD, 100);
        playerOne.add_resource(player.RESOURCES.LUMBER, 100);

        //Enemy
        while (true) {
            spawn = g.getValidSpawnLocation();
            try {
                playerTwo = new player(g, spawn[0], spawn[1]);
                break;
            } catch (Error e) {
                Log.d("DEBUG:", "controller: Failed to spawn enemy, retrying");
            }
        }

        playerTwo.add_resource(player.RESOURCES.STONE, 10000);
        playerTwo.add_resource(player.RESOURCES.FOOD, 10000);
        playerTwo.add_resource(player.RESOURCES.LUMBER, 10000);
        playerTwo.build_unit(unit.TYPE.TROOP);
        playerTwo.build_unit(unit.TYPE.TROOP);
        playerTwo.build_unit(unit.TYPE.TROOP);

        // init states
        view.update(g, playerOne);
        view.setCamera(playerOne.getHomeCoord()[0], playerOne.getHomeCoord()[1]);
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

                // playerOne has PID
                playerTwo.act();

                //pass updates structs to view
                // update only needs to happen at start to pass objects by reference
                view.update(g, playerOne);
            }
        };
        timer.scheduleAtFixedRate(taskNew, 0, (int)(0.2*1000));
    }

    // called by ui to give specific order to change playerOne's policies

    public void giveWorkersOrder(player.RESOURCES r) {
        playerOne.setTargetResource(r);
    }

    // begins process for unit.Type t
    public void startBuildUnit(unit.TYPE t) {
        playerOne.start_build_unit(t);
    }

    // set tileView to accept touch for worker improvement
    public void startImproveTile() {
        //view.primeSelectTile(tileView.touchType.IMPROVE_SELECT);
    }

    // set tileView to accept touch for defining defendedTiles
    public void selectDefendArea() {
        view.primeSelectTile(tileView.touchType.DEFEND_SELECT);
    }

    // set tileView to accept touch for tile expantion
    public void selectExpandTile() {
        view.primeSelectTile(tileView.touchType.EXPAND_SELECT);
    }

    public void toggleHeal() {
        playerOne.toggle_heal();
    }
}
