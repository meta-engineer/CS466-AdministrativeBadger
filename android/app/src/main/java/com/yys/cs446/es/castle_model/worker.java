package com.yys.cs446.es.castle_model;

import android.util.Log;
import android.widget.PopupMenu;

import com.yys.cs446.es.castle_model.player.RESOURCES;

public class worker extends unit{

	private RESOURCES heldResource;

	worker(double x, double y, player p) {
		super(x, y, p);
		heldResource = RESOURCES.NONE;
		progressMax = 5;
		HPMax = 50;
		HP = 50;
		AP = 0;
	}

	@Override
	public void act() {

		//Log.d("WORKER LOG", "act: " + command.toString() + " " + Double.toString(location_x) + ", " + Double.toString(location_y));
		super.act();
		switch (command) {
			case STAY:
				// if at home deposit
				if ((int)Math.round(location_x) == owner.getHomeCoord()[0] && (int)Math.round(location_y) == owner.getHomeCoord()[1]) {
					owner.add_resource(heldResource, progress);
					heldResource = RESOURCES.NONE;
					progress = 0;
					// get new order?
					owner.getNewOrder(this);
				} else {
					// if not at home (probably) finished move order to targetResource
					// check and wait for other unit to finish
					tile myTile = owner.get_grid().piece((int)Math.round(location_x), (int)Math.round(location_y));
					// if someone is already working this tile wait
					for (unit u : myTile.get_units()) {
						if (u instanceof worker && u.status() == Command.WORK) {
							// Just keep "staying"?
							// try to get another open tile?
							owner.getNewOrder(this);
							return;
						}
					}
					// no units found therefore i can work
					command = Command.WORK;
				}
				break;
			case MOVE:
			    // super does most movement work

				break;
			case WORK:
				// collect from current tile and if full/done switch to move towards home tile? nearest town tile?
				tile myTile = owner.get_grid().piece((int)Math.round(location_x), (int)Math.round(location_y));

				// if tile resource != held resource dump and reset
				if (myTile.getResource() != heldResource) {
					progress = 0;
					heldResource = myTile.getResource();
				}
				// if not, or once reset;
				progress += myTile.efficiency();

				// once holding enough return to home
				if (progress >= progressMax) {
					progress = progressMax;
					path = owner.getPath(myTile, owner.get_grid().piece(owner.getHomeCoord()[0], owner.getHomeCoord()[1]));
					command = Command.MOVE;
				}
				break;
			case COMBAT:
				// workers cannot be put into this action?
				command = Command.STAY;
				break;
		}
	}
	
}
