package castle_model;

import java.util.ArrayList;
import java.util.List;

public class Unit {
	public enum Command {
		MOVE, STAY
	}

	public enum TYPE {
		SETTLER, WORKER, TROOP
	}

	public static final int TURNS_TO_MOVE_UNIT = 3;

	protected int location_x, location_y;

	protected Player owner;
	Command command = Command.STAY;
	List<Tile> path;
	int remained_moving_turns = TURNS_TO_MOVE_UNIT;

	Unit(int x, int y, Player P) {
		location_x = x;
		location_y = y;
		owner = P;
		path = new ArrayList<Tile>();
	}

	// called by Player
	public void order_move(ArrayList<Tile> tiles) {
		command = Command.MOVE;
		path = tiles;
	}

	// for debugging use
	// need to be adjacent tiles
	public void order_move(Tile tile) {
		command = Command.MOVE;
		path.add(tile);
	}

	// called by Player
	public void order_stay() {
		command = Command.STAY;
		path.clear();
	}

	// need to fix this
	// for decimal locations
	public void move() {
		if (command == Command.MOVE) {
			--remained_moving_turns;
			if (remained_moving_turns == 0) {
				remained_moving_turns = TURNS_TO_MOVE_UNIT;
				owner.the_grid().piece(location_x, location_y).remove_unit(this);
				location_x = path.get(0).get_x();
				location_y = path.get(0).get_y();
				path.remove(0).add_unit(this);
				if (path.isEmpty()) {
					command = Command.STAY;
				}
			}
		}
	}

	public int get_location_x() {
		return location_x;
	}

	public int get_location_y() {
		return location_y;
	}

	public double print_location_x() {
		double answer_x = owner.the_grid().printX(owner.the_grid().index(location_x, location_y));
		switch (command) {
		case STAY:
			return answer_x;
		case MOVE:
			return answer_x + (owner.the_grid().printX(path.get(0).get_index()) - answer_x)
					* (1 - 1.0 * remained_moving_turns / TURNS_TO_MOVE_UNIT);
		default:
			return answer_x;
		}
	}

	public double print_location_y() {
		double answer_y = owner.the_grid().printY(owner.the_grid().index(location_x, location_y));
		switch (command) {
		case STAY:
			return answer_y;
		case MOVE:
			return answer_y + (owner.the_grid().printY(path.get(0).get_index()) - answer_y)
					* (1 - 1.0 * remained_moving_turns / TURNS_TO_MOVE_UNIT);
		default:
			return answer_y;
		}
	}

	public Player get_owner() {
		return owner;
	}

	public void set_owner(Player p) {
		this.get_owner().destroy_Unit(this);
		p.add_Unit(this);
		owner = p;
		order_stay();
	}

	public Command status() {
		return command;
	}

	public String toString() {
		return location_x + " " + location_y + " " + command;
	}

	public void destroy() {
		// TODO Auto-generated method stub

	}
}
