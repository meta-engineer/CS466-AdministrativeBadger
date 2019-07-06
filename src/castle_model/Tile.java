package castle_model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Tile {
	public static final int TURNS_TO_CONCUR_TILE = 5;

	int x;
	int y;
	double[] base_effi = new double[Grid.RESOURCES_TYPES];
	double[] real_effi = new double[Grid.RESOURCES_TYPES];
	double[] army_power = null;
	Player owner = null;
	Player tempted_owner = null;
	Grid grid;
	int remained_concuring_turns = TURNS_TO_CONCUR_TILE;

	ArrayList<Settler> settlers = new ArrayList<Settler>();
	ArrayList<Worker> workers = new ArrayList<Worker>();
	ArrayList<Troop> troops = new ArrayList<Troop>();

	public Tile(int in_x, int in_y, double in_e, Grid g) {
		x = in_x;
		y = in_y;
		base_effi[0] = in_e;
		base_effi[1] = in_e + 2;
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
		if (u instanceof Settler && (settlers.isEmpty() || u.get_owner() == owner)) {
			settlers.add((Settler) u);
		} else if (u instanceof Worker && (workers.isEmpty() || u.get_owner() == owner)) {
			workers.add((Worker) u);
		} else if (u instanceof Troop) {
			troops.add((Troop) u);
		} else {
			return false;
		}
		return true;
	}

	public boolean remove_unit(Unit u) {
		if (u instanceof Settler) {
			settlers.remove(u);
		} else if (u instanceof Worker) {
			workers.remove(u);
		} else if (u instanceof Troop) {
			troops.remove(u);
		} else {
			return false;
		}
		return true;
	}

	void consider_troop(Troop t) {
		army_power[Grid.players.indexOf(t.get_owner())] += 1;
	}

	void consider_settler(Settler s) {
		if (army_power[Grid.players.indexOf(s.get_owner())] == 0) {
			for (int i = 0; i < Grid.players.size(); ++i) {
				if (army_power[i] > 0) {
					System.out.println("settler owner changed from " + s.get_owner() + " to " + Grid.players.get(i));
					s.set_owner(Grid.players.get(i));
				}
			}
		}
		--remained_concuring_turns;
		if (remained_concuring_turns == 0) {
			remained_concuring_turns = TURNS_TO_CONCUR_TILE;
			tempted_owner = s.get_owner();
			/*
			 * s.owner().add_territory(x, y); s.destroy();
			 */
		}
	}

	void consider_worker(Worker w) {
		if (army_power[Grid.players.indexOf(w.get_owner())] == 0) {
			for (int i = 0; i < Grid.players.size(); ++i) {
				if (army_power[i] > 0) {
					System.out.println("worker owner changed from " + w.get_owner() + " to " + Grid.players.get(i));
					w.set_owner(Grid.players.get(i));
				}
			}
		}

		for (int i = 0; i < Grid.RESOURCES_TYPES; ++i) {
			if (w.command == Unit.Command.STAY) {
				real_effi[i] += base_effi[i];
			}
		}
	}

	void act() {
		// produce resources
		System.out.println(x + " " + y + " is trying to act");
		System.out.println(" tempted owner is " + tempted_owner);
		if (owner != null) {
			owner.add_resources(real_effi);
		}
		// resolve fight
		double sum = 0;
		for (int i = 0; i < Grid.players.size(); ++i) {
			sum += army_power[i];
		}
		for (Player p : Grid.players) {
			double my_power = army_power[Grid.players.indexOf(p)];
			int number_of_units_to_be_killed = (int) my_power - (int) (my_power * my_power / sum);
			for (int i = 0; number_of_units_to_be_killed > 0;) {
				if (troops.get(i).get_owner() == p) {
					p.destroy_Unit(troops.get(i));
					--number_of_units_to_be_killed;
				} else {
					++i;
				}
			}

		}
	}

	public void resolve() {
		real_effi = new double[Grid.RESOURCES_TYPES];
		army_power = new double[Grid.players.size()];
		if (settlers.isEmpty() && workers.isEmpty() && troops.isEmpty()) {
			return;
		}
		for (Troop t : troops) {
			consider_troop(t);

		}
		for (Settler s : settlers) {
			if (s.status() == Unit.Command.STAY) {
				consider_settler(s);
			}
		}
		for (Worker w : workers) {
			if (w.status() == Unit.Command.STAY) {
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
