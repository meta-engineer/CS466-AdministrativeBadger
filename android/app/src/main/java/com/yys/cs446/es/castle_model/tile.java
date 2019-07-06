package com.yys.cs446.es.castle_model;

import java.util.ArrayList;


public class tile {
    // change to tile TYPES
    // use RESOURCES in player for resource stock?
    public enum RESOURCES {
        NONE, GRASS, WATER, GRAIN, WOOD, MOUNTAIN, TOWN
    }
	ArrayList<unit> units = new ArrayList<unit>();
	double effi;
	int x;
	int y;
	RESOURCES type;

	public tile(int initX, int initY, double initE, RESOURCES initType) {
	    x = initX;
	    y = initY;
	    effi = initE;
	    type = initType;
    }

	public int get_x() {
        return x;
    }
	public int get_y() {
        return y;
    }
    public RESOURCES getType() {
        return type;
    }
    public double efficiency(RESOURCES r) {
        return effi;
    }
    public void add_unit(unit u) {
        units.add(u);
    }
	public void remove_unit(unit u) {
		units.remove(u);
	}
	
	// TO DO
	public void resolve() {
		if (units.isEmpty()) {
			return;
		}
	}

    public String toString() {
        return get_x() + " " + get_y();
    }
}
