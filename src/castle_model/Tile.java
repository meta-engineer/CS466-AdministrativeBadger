package castle_model;

import java.util.ArrayList;

public class Tile {
	public static final int TURNS_TO_CONCUR_TILE = 5;

	int x;
	int y;
	double[] effi;
	Player owner = null;
	Grid grid;
	int remained_concuring_turns = TURNS_TO_CONCUR_TILE;

	ArrayList<Settler> settlers = new ArrayList<Settler>();
	ArrayList<Worker> workers = new ArrayList<Worker>();
	ArrayList<Troop> troops = new ArrayList<Troop>();

	public Tile(int in_x, int in_y, double in_e, Grid g) {
		x = in_x;
		y = in_y;
		effi = new double[3];
		effi[0] = 2;
		effi[1] = 3;
		effi[2] = 5;
		grid = g;
	}

	public int get_x() {
		return x;
	}

	public int get_y() {
		return y;
	}

	public double efficiency(Grid.RESOURCES r) {
		return 0;
	}

	public boolean add_unit(Unit u) {
		if (u instanceof Settler && (settlers.isEmpty() || u.owner() == owner)) {
			settlers.add((Settler) u);
		} else if (u instanceof Worker && (workers.isEmpty() || u.owner() == owner)) {
			workers.add((Worker) u);
		} else if (u instanceof Troop && (troops.isEmpty())) {
			troops.add((Troop) u);
		} else {
			return false;
		}
		return true;
	}

	public void remove_unit(Unit u) {
		if (u instanceof Settler) {
			settlers.remove(u);
		} else if (u instanceof Worker) {
			workers.remove(u);
		} else if (u instanceof Troop) {
			troops.remove(u);
		} else {

		}
	}

	void consider_settler(Settler s) {
		--remained_concuring_turns;
		if (remained_concuring_turns == 0) {
			s.owner().add_territory(x, y);
			s.destroy();
		}
	}

	void consider_worker(Worker w) {
	}

	void consider_troop(Troop t) {
	}

	void act() {
	}

	public void resolve() {
		if (settlers.isEmpty() && workers.isEmpty() && troops.isEmpty()) {
			return;
		}
		for (Troop t : troops) {
			if (t.status() != Unit.Command.STAY) {
				consider_troop(t);
			}
		}
		for (Settler s : settlers) {
			if (s.status() != Unit.Command.STAY) {
				consider_settler(s);
			}
		}
		for (Worker w : workers) {
			if (w.status() != Unit.Command.STAY) {
				consider_worker(w);
			}
		}

		act();
	}

	public int get_index() {
		return grid.index(x, y);
	}

	public void set_owner(Player p) {
		owner = p;
	}

	public String toString() {
		return x + " " + y + " " + settlers + workers + troops;
	}
}
