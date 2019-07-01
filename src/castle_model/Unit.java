package castle_model;

import java.util.ArrayList;

abstract public class Unit {
	public enum Command {
		MOVE, STAY
	}
	public enum TYPE {
		SETTLER, WORKER, TROOP
	}

	private int location_x, location_y;
	private int target_x, target_y;
	private Player owner;
	private Command command = Command.STAY;
	ArrayList<Tile> path;
	
	Unit(int x, int y, Player P) {
		location_x = x;
		location_y = y;
		owner = P;
	}

	// called by Player
	public void order_move(ArrayList<Tile> tiles) {
		command = Command.MOVE;
		path = tiles;
	}

	// called by Player
	public void order_stay() {
		command = Command.STAY;
		target_x = location_x;
		target_y = location_y;
		path.clear();
	}

	// need to fix this
	// for decimal locations
	public void move() {
		if (command == Command.MOVE) {
			int old_x = location_x;
			int old_y = location_y;
			if (target_x > location_x) {
				++location_x;
			} else if (target_x < location_x) {
				--location_x;
			}
			if (target_y > location_y) {
				++location_y;
			} else if (target_y < location_y) {
				--location_y;
			}
			//need to fix this
			owner.the_grid().piece(old_x,old_y).remove_unit(this);
			owner.the_grid().piece(location_x,location_y).add_unit(this);
			if ((target_x == location_x) && (target_y == location_y)) {
				command = Command.STAY;
			}
		}
	}

	public int get_location_x () {
		return location_x;
	}
	
	public int get_location_y () {
		return location_y;
	}
	
	public double print_location_x () {
		return 1;
	}
	
	public double print_location_y () {
		return 0;
	}
	public Player owner() {
		return owner;
	}
	
	public Command status() {
		return command;
	}
	
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
}
