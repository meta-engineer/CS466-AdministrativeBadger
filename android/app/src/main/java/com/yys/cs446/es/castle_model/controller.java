package com.yys.cs446.es.castle_model;

import android.util.Log;

import com.yys.cs446.es.views.tileView;

import java.util.Timer;
import java.util.TimerTask;

public class controller {

    public enum GAMESTATE {
        RUNNING, PAUSE, VICTORY, DEFEAT
    }

    private Timer timer;
    private TimerTask taskNew;
    private static final int timerRate = 200;

    private tileView view;
    private grid g;
    private GAMESTATE state;
    private player playerOne;
    private player playerTwo;

    public controller (tileView v) {
        view = v;
        g = new grid();
        state = GAMESTATE.PAUSE;

        initlevel();

        // init states
        view.update(g, playerOne, state);
        view.setCamera(playerOne.getHomeCoord()[0], playerOne.getHomeCoord()[1]);

    }

    private void initlevel() {
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

        // default resources
        playerOne.add_resource(player.RESOURCES.STONE, 30);
        playerOne.add_resource(player.RESOURCES.FOOD, 100);
        playerOne.add_resource(player.RESOURCES.LUMBER, 30);
        playerOne.build_unit(unit.TYPE.WORKER);

        //Enemy
        while (true) {
            spawn = g.getValidSpawnLocation();
            try {
                playerTwo = new AI(g, spawn[0], spawn[1], playerOne);
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
    }

    // called after constructor (in GameActivity), starts simulation
    public void start() {
        timer = new Timer(true);
        taskNew = new TimerTask() {
            @Override
            public void run() {
                // player (UI) actions will call methods below to change unit/tile states

                //step player/unit specific events
                playerOne.act();

                // playerOne has PID (randomize?)
                playerTwo.act();

                // gamestate changes
                // pause game and shift state?
                if (playerOne.getHP() <= 0) {
                    togglePause();
                    state = GAMESTATE.DEFEAT;
                } else if (playerTwo.getHP() <= 0) {
                    togglePause();
                    state = GAMESTATE.VICTORY;
                }

                // pass updated structs to view
                // update only needs to happen at start to pass objects by reference
                view.stepAnim();
                view.update(g, playerOne, state);
            }
        };
        timer.scheduleAtFixedRate(taskNew, 0, timerRate);
        state = GAMESTATE.RUNNING;
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
        view.update(g, playerOne, state);
    }

    // set tileView to accept touch for tile expantion
    public void selectExpandTile() {
        view.primeSelectTile(tileView.touchType.EXPAND_SELECT);
        view.update(g, playerOne, state); // update view if paused
    }

    public void toggleHeal() {
        playerOne.toggle_heal();
    }

    // method of "pausing" or only restarting and cancelling?
    // still want timer to run and let animations
    public void togglePause() {
        if (timer != null) {
            if (state == GAMESTATE.PAUSE) {
                start();
            } else {
                state = GAMESTATE.PAUSE;
                // update view last time before timer stops updating
                view.update(g, playerOne, state);
                timer.cancel();
            }
        }
    }

    public GAMESTATE getState() {
        return state;
    }
}
