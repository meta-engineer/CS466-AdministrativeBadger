package com.yys.cs446.es.castle_model;

import android.util.Log;

import java.util.ArrayList;

public class troop extends unit{

	troop(double x, double y, player p) {
		super(x, y, p);
		HPMax = 100;
		HP = 100;
		AP = 1;
	}


	@Override
	public void act() {
		super.act();

		switch (command) {
			case STAY:
				owner.getNewOrder(this);
				// after moving troop will detect enemies and switch to combat mode

				break;
			case MOVE:
				// super does movement work
                //check for enemies on this tile before leaving
                {
					// only if tile is in defended area interupt movement
					if (!(owner instanceof AI) && !owner.getDefendedTiles().contains(path.get(0))) {
						return;
					}

                    ArrayList<unit> unitList = path.get(0).get_units();
                    for (unit u : unitList) {
                        if (u.owner != owner) {
                            //reset to tile and attack
							location_x = path.get(0).get_x();
							location_y = path.get(0).get_y();
                            command = Command.COMBAT;
                            break;
                        }
                    }
                    if (path.get(0).getType() == tile.TILETYPE.CITY && path.get(0).get_owner() != owner) {
						location_x = path.get(0).get_x();
						location_y = path.get(0).get_y();
						command = Command.COMBAT;
						break;
					}
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

				// if defended tiles has changed get out (RETREAT)
				// AI troops cannot retreat (should subclass for AI troops then?)
				if (!(owner instanceof AI) && !owner.getDefendedTiles().contains(owner.get_grid().piece(effectiveX, effectiveY))) {
					owner.getNewOrder(this);
					return;
				}

				ArrayList<unit> unitList = owner.get_grid().piece(effectiveX, effectiveY).get_units();

				// FIRST check for enemy troops
				for (unit u : unitList) {
					if (u.owner != owner && u instanceof troop) {
						attack(u);
						// can only do attack on one unit
						return;
					}
				}
				// then check for other unit types
				for (unit u : unitList) {
					if (u.owner != owner) {
						attack(u);
						// can only do attack on one unit
						return;
					}
				}

				// LAST check for attackable tile
				tile myTile = owner.get_grid().piece(effectiveX, effectiveY);
				if (myTile.getType() == tile.TILETYPE.CITY && myTile.get_owner() != owner) {
					attack(myTile.get_owner());
					return;
				}
				// no units were attacked therefore just switch to STAY
				command = Command.MOVE;
				break;
		}
	}
	
}
