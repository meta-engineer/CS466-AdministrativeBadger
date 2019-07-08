package com.yys.cs446.es.castle_model;

import java.util.ArrayList;

public class troop extends unit{

	troop(double x, double y, player p) {
		super(x, y, p);
		totalHP = 100;
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
					// progress towards my next tile in path
					int effectiveX = (int)Math.round(location_x);
					int effectiveY = (int)Math.round(location_y);
					double newX = location_x + (path.get(0).get_x() - effectiveX) * movespeed * owner.get_grid().piece(effectiveX, effectiveY).getMovementFactor();
					double newY = location_y + (path.get(0).get_y() - effectiveY) * movespeed * owner.get_grid().piece(effectiveX, effectiveY).getMovementFactor();
					// if moved beyond range of current tile switch to new tile and advance in path
					if ((int)Math.round(newX) == path.get(0).get_x() && (int)Math.round(newY) == path.get(0).get_y()) {
						// if tile i WOULD move to is unaccessable then just stop
						// pathfinder should have done a better job
						if (path.get(0).getMovementFactor() <= 0.1) {
							command = Command.STAY;
							break;
						}
						// otherwise remove myself from current tile and place on next tile, then move my coords there too.
						owner.get_grid().piece(effectiveX, effectiveY).remove_unit(this);
						owner.get_grid().piece((int)Math.round(newX), (int)Math.round(newY)).add_unit(this);
						path.remove(0);
					}
					location_x = newX;
					location_y = newY;

					// detect if there are enemies to attack/capture
					ArrayList<unit> unitList = owner.get_grid().piece(effectiveX, effectiveY).get_units();
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
