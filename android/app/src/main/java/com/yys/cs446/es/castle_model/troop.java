package com.yys.cs446.es.castle_model;

import android.util.Log;

import java.util.ArrayList;

public class troop extends unit{

	troop(double x, double y, player p) {
		super(x, y, p);
		HPMax = 100;
		HP = 10;
		AP = 1;
	}


	@Override
	public void act() {
		super.act();
		switch (command) {
			case STAY:
				// detect if there are enemies to attack/capture?
				// NO
				owner.getNewOrder(this);
				// after moving troop will detect enemies and switch to combat mode

				break;
			case MOVE:
				// check if there is path to follow
				if (path != null && !path.isEmpty()) {
					// after super moved
					// detect if there are enemies to attack/capture
					ArrayList<unit> unitList = path.get(0).get_units();
					for (unit u : unitList) {
						if (u.owner != owner) {
							command = Command.COMBAT;
						}
					}
				} else {
					command = Command.STAY;
				}
				break;
			case WORK:
				// troops dont do work
				break;
			case COMBAT:
				// check for other units on this tile
				// if other troop attack them
				// if settler/worker capture them
				int effectiveX = (int)Math.round(location_x);
				int effectiveY = (int)Math.round(location_y);
				ArrayList<unit> unitList = owner.get_grid().piece(effectiveX, effectiveY).get_units();

				// FIRST check for enemy troops
				for (unit u : unitList) {
					if (u.owner != owner && u instanceof troop) {
						attack(u);
						// can only do attack on one unit
						break;
					}
				}

				// SECOND check for enemy capturable units
				// if no capurable units (or troops) switch to STAY
				for (unit u : unitList) {
					if (u.owner != owner) {
						attack(u);
						// can only do attack on one unit
						break;
					}
				}
				// no units were attacked therefore just switch to STAY
				command = Command.STAY;
				break;
		}
	}
	
}
