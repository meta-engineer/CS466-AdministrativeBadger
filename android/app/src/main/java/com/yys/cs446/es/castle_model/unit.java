package castle_model;

abstract public class unit {
	public enum Command {
		MOVE, STAY
	}
	public enum TYPE {
		SETTLER, WORKER, TROOP
	}

	private int location_x, location_y;
	private player owner;
	private Command command = Command.STAY;
	private int target_x, target_y;

	unit(int x, int y, player P) {
		location_x = x;
		location_y = y;
		owner = P;
	}

	// called by player
	public void order_move(tile t) {
		command = Command.MOVE;
		target_x = t.get_x();
		target_y = t.get_y();
	}

	// called by player
	public void order_move(int x, int y) {
		command = Command.MOVE;
		target_x = x;
		target_y = y;
	}

	// called by player
	public void order_stay() {
		command = Command.STAY;
		target_x = location_x;
		target_y = location_y;
	}

	//need to fix this
	// maybe called by grid, but definitely not the player
	// maybe this should be override by child classes since unit actions are
	// different according to types.
	// called by the grid to actually move the unit.
	public void move(int x, int y) {
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
	
	public player owner() {
		return owner;
	}
}
