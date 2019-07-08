package com.yys.cs446.es.castle_model;

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
	protected float totalHP;
	// attack points
	protected float AP;

	// each move unit moves towards next in path by their movespeed
	ArrayList<tile> path;
	// each gametick units can move 0.2 of an index * tile.movespeedFactor
	public static final double movespeed = 0.1;

	unit(double x, double y, player P) {
		location_x = x;
		location_y = y;
		owner = P;
		command = Command.STAY;

		//init empty path
		path = new ArrayList<tile>();
	}

	// progress self (finite state machine)
	// OVERRIDED implementation for each subclass
	public void act() {
		// anything to do for all units in general?
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

	public float getTotalHP() {
		return totalHP;
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
