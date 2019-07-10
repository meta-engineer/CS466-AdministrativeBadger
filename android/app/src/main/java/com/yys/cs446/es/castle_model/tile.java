package com.yys.cs446.es.castle_model;

import android.widget.PopupMenu;

import java.util.ArrayList;

import com.yys.cs446.es.castle_model.player.RESOURCES;


public class tile {
    // change to tile TYPES
    // use TILETYPE in player for resource stock?
    public enum TILETYPE {
        NONE, GRASS, WATER, GRAIN, WOOD, MOUNTAIN, TOWN
    }
    public static final int TURNS_TO_CONQUER_TILE = 5;

    private int x;
    private int y;
    private TILETYPE type;
    private double base_effi;
    private double real_effi;
    private double movementFactor;

    private player owner;
    private player tempted_owner;

    private grid parentGrid;

    private int remained_conquering_turns = TURNS_TO_CONQUER_TILE;
    // again, the point of abstract classes is to avoid this
    private ArrayList<unit> units;
    //private ArrayList<settler> settlers;
    //private ArrayList<worker> workers;
    //private ArrayList<troop> troops;

	public tile(int initX, int initY, double initE, TILETYPE initType, grid parent) {
	    x = initX;
	    y = initY;
        type = initType;
	    base_effi = initE;
	    real_effi = initE;

	    setType(initType);

	    parentGrid = parent;

        units = new ArrayList<unit>();
        owner = null;
        tempted_owner = null;

        units = new ArrayList<unit>();
        //settlers = new ArrayList<settler>();
        //workers = new ArrayList<worker>();
        //troops = new ArrayList<troop>();
    }

	public int get_x() {
        return x;
    }
	public int get_y() {
        return y;
    }

    // unsafely changes tile type
    public void setType(TILETYPE newType) {
        // should be logic for what changes can occur (settlers making towns, workers building on tiles?)
        // grid.add_player does some checks but not other actions
        type = newType;

        if (type == TILETYPE.NONE) {
            movementFactor = 0.0;
        } else if (type == TILETYPE.WATER || type == TILETYPE.MOUNTAIN) {
            movementFactor = 0;
        } else if (type == TILETYPE.TOWN) {
            movementFactor = 0.8;
        } else {
            movementFactor = 1.0;
        }
    }

    public TILETYPE getType() {
        return type;
    }

    // returns RESOURCE for this TILETYPE (STONE by default)
    public RESOURCES getResource() {
        if (type == TILETYPE.GRAIN) {
            return RESOURCES.FOOD;
        } else if (type == TILETYPE.WOOD) {
	        return RESOURCES.LUMBER;
        } else if (type == TILETYPE.MOUNTAIN) {
            return RESOURCES.STONE;
        } else {
            return RESOURCES.NONE;
        }
    }

    // improves efficiency by some set amount
    // how can the tileView know about/display this?
    public void improveEfficiency() {
	    // diminishing returns for high real_effi (starts at 1, then reduces)
	    real_effi += base_effi/real_effi;
    }

    public double efficiency() {
        return real_effi;
    }

    public double getMovementFactor() {
	    return movementFactor;
    }


    public boolean add_unit(unit u) {
	    units.add(u);
        return true;
    }
    public boolean remove_unit(unit u) {
        for (int i = 0; i < units.size(); i++) {
            // get particular instance of u, not just any u on this tile
            if (u.equals(units.get(i))) {
                units.remove(i);
                return true;
            }
        }
        // didn't find a unit to remove
        return false;
    }

    public ArrayList<unit> get_units() {
	    return units;
    }

    // resolve calls consider actions for each unit on this tile

    //troops add to army_power
    // settlers change owner if owner's power == 0 and opponent power > 0
    //          reduce conquering time of this tile
    // workers change owner if oner's power == 0 and opponent power > 0
    //          improve efficiency of tile?

    // ACT ADDS RESOURCE TO PLAYER
    //      resolves fights (remove units based on army power)


    public void set_owner(player p) {
        owner = p;
    }

    public player get_owner() {
	    return owner;
    }

    public void set_tempted_owner(player p) {
        tempted_owner = p;
    }

    public player get_tempted_owner() {
	    return tempted_owner;
    }

    public String toString() {
        return x + " " + y + " " + units;
    }
}
