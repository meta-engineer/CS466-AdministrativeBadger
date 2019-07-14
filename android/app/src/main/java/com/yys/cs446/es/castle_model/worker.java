package com.yys.cs446.es.castle_model;

import android.util.Log;
import android.widget.PopupMenu;

import com.yys.cs446.es.castle_model.player.RESOURCES;

public class worker extends unit{

	private RESOURCES heldResource;
	private double heldAmount;
	private final double maxHeld = 5;

	worker(double x, double y, player p) {
		super(x, y, p);
		heldResource = null;
		heldAmount = 0;
		HPMax = 50;
		HP = 50;
		AP = 0;
	}

	@Override
	public double getProgress() {
		return heldAmount / maxHeld;
	}

	@Override
	public void act() {

		//Log.d("WORKER LOG", "act: " + command.toString() + " " + Double.toString(location_x) + ", " + Double.toString(location_y));
		super.act();
		switch (command) {
			case STAY:
				// if at home deposit
				if ((int)Math.round(location_x) == owner.getHomeCoord()[0] && (int)Math.round(location_y) == owner.getHomeCoord()[1]) {
					owner.add_resource(heldResource, heldAmount);
					heldResource = RESOURCES.NONE;
					heldAmount = 0;
					// get new order?
					owner.getNewOrder(this);
				} else {
					// if not at home (probably) finished move order to targetResource
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
					heldAmount = 0;
					heldResource = myTile.getResource();
				}
				// if not, or once reset;
				heldAmount += myTile.efficiency();

				// once holding enough return to home
				if (heldAmount >= maxHeld) {
					heldAmount = maxHeld;
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
