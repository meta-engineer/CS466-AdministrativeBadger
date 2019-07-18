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
				// detect if there are enemies to attack/capture?
				{
					ArrayList<unit> unitList = owner.get_grid().piece((int)location_x, (int)location_y).get_units();
					for (unit u : unitList) {
						if (u.owner != owner) {
							location_x = (int)Math.round(location_x);
							location_y = (int)Math.round(location_y);
							command = Command.COMBAT;
							break;
						}
					}
					tile myTile = owner.get_grid().piece((int)Math.round(location_x), (int)Math.round(location_y));
					if (myTile.getType() == tile.TILETYPE.CITY && myTile.get_owner() != owner) {
						location_x = (int)Math.round(location_x);
						location_y = (int)Math.round(location_y);
						command = Command.COMBAT;
						break;
					}
				}
				// else
				owner.getNewOrder(this);
				// after moving troop will detect enemies and switch to combat mode

				break;
			case MOVE:
				// super does movement work
                //check for enemies on this tile before leaving
                {
                    ArrayList<unit> unitList = owner.get_grid().piece((int)Math.round(location_x), (int)Math.round(location_y)).get_units();
                    for (unit u : unitList) {
                        if (u.owner != owner) {
                            //reset to tile and attack
                            location_x = (int)Math.round(location_x);
                            location_y = (int)Math.round(location_y);
                            command = Command.COMBAT;
                            break;
                        }
                    }
					tile myTile = owner.get_grid().piece((int)Math.round(location_x), (int)Math.round(location_y));
                    if (myTile.getType() == tile.TILETYPE.CITY && myTile.get_owner() != owner) {
						location_x = (int)Math.round(location_x);
						location_y = (int)Math.round(location_y);
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
				ArrayList<unit> unitList = owner.get_grid().piece(effectiveX, effectiveY).get_units();

				// FIRST check for enemy troops
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
				command = Command.STAY;
				break;
		}
	}
	
}
