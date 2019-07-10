package com.yys.cs446.es.castle_model;

import android.util.Log;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.List;

public class unit {
	public enum Command {
		MOVE, STAY, WORK, COMBAT
	}
	public enum TYPE {
		NONE, SETTLER, WORKER, TROOP
	}

	protected double location_x, location_y;
	protected player owner;
	protected Command command;
	// hit points
	protected float HP;
	protected float HPMax;
	// attack points
	protected float AP;
	protected float foodUpkeep;

	// used my view objects to track animations
	// no dependencies on it in unit object so coupling isn't horrendous.
	public int animState = 0;
	// starting north, then CW.
	public int moveDirection = 0;

	// each move unit moves towards next in path by their movespeed
	ArrayList<tile> path;
	// each gametick units can move 0.2 of an index * tile.movespeedFactor
	public final double movespeed = 0.1;

	unit(double x, double y, player P) {
		location_x = x;
		location_y = y;
		owner = P;
		command = Command.STAY;

		foodUpkeep = 0.02;

		//init empty path
		path = new ArrayList<tile>();
	}

	// progress self (finite state machine)
	// OVERRIDED implementation for each subclass
	public void act() {
		// anything to do for all units in general?
		// all units need food to be alive
		owner.take_resource(player.RESOURCES.FOOD, foodUpkeep);

		switch (command) {
			case STAY:
				// may want regeneration methods to handle specific classes
				if (HP < HPMax) {
					HP += 1;
					if (HP > HPMax) HP = HPMax; //floats might overflow
				}
				//handled by subclass
				break;
			case MOVE:
				// check if there is path to follow (path.get(0) is always current tile) and path does not lead to bad tile
				if (path != null && path.size() > 1 && path.get(1).getMovementFactor() > 0) {
					// progress towards my next tile in path
					if (path.get(0) == path.get(1)) {
						path.remove(0); // already there
						break; // keep validity (this loses a gametick, but is fault of pathfinder)
					}

					double moveX = (path.get(1).get_x() - path.get(0).get_x()) * movespeed * path.get(0).getMovementFactor();
					double moveY = (path.get(1).get_y() - path.get(0).get_y()) * movespeed * path.get(0).getMovementFactor();
					double newX = location_x + moveX;
					double newY = location_y + moveY;
					// animation direction
					// 6 directions, let left/right directions be the same
					if (moveY > 0) {
						moveDirection = 1;
					} else if (moveY < 0) {
						moveDirection = 3;
					} else {
						if (moveX < 0) {
							moveDirection = 0;
						} else {
							moveDirection = 2;
						}
					}
					location_x = newX;
					location_y = newY;
					// if moved beyond range of current tile switch to new tile and advance in path
					if (Math.abs(newX - path.get(0).get_x()) > 0.99 || Math.abs(newY - path.get(0).get_y()) > 0.99) {
						// remove myself from current tile and place on next tile, then move my coords there too.
						path.get(0).remove_unit(this);
						path.get(1).add_unit(this);
						owner.setVisible2TileRadius(path.get(1).get_x(), path.get(1).get_y());
						//set on tile locations (integers) to prevent rouding errors in movement
						location_x = path.get(1).get_x();
						location_y = path.get(1).get_y();
						//keep moving toward centre of tile, once within range of tile center then advance path
						path.remove(0);
					}
				} else {
					command = Command.STAY;
				}
				break;
			case WORK:
				// handled by subclass
				break;
			case COMBAT:
				// handled by subclass
				break;

		}
	}

	// called by player
	public void order_move(ArrayList<tile> p) {
		// check if path is valid (adjacent?, starts from current location?)
		command = Command.MOVE;
		path = p;
	}

	public void attack(unit u) {
		float enemyAttack = u.getAP();
		// if attack would kill unit check for capturing
		if (AP >= u.getHP() && !(u instanceof troop)) {
			u.set_owner(owner);
		} else {
			u.takeDamage(AP);
			takeDamage(enemyAttack);
		}
	}

	public float getHPMax() {
		return HPMax;
	}

	public float getHP() {
		return HP;
	}

	public void takeDamage(float dmg) {
		HP -= dmg;
		if (HP <= 0) owner.destroy_Unit(this);
	}

	public float getAP() {
		return AP;
	}

	public void order_stay() {
		command = Command.STAY;
	}

	public double get_location_x () {
		return location_x;
	}
	
	public double get_location_y () {
		return location_y;
	}

	public player get_owner() {
		return owner;
	}

	public void set_owner(player p) {
		this.get_owner().destroy_Unit(this);
		p.add_Unit(this);
		owner = p;
		order_stay();
	}

	public Command status() {
		return command;
	}
}
