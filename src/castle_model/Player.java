package castle_model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class Player {
	private String name = "";
	private int home_x, home_y;
	private Grid g;
	private double resources[] = new double[2];
	private double priority[] = new double[Grid.SIZE];

	// for debugging purpose, the following 6 arrays are set to public, please
	// change them to private after debugging.
	public ArrayList<Tile> owned = new ArrayList<Tile>();
	public ArrayList<Tile> adjacent = new ArrayList<Tile>();
	public ArrayList<Tile> visible = new ArrayList<Tile>();

	public ArrayList<Worker> workers = new ArrayList<Worker>();
	public ArrayList<Settler> settlers = new ArrayList<Settler>();
	public ArrayList<Troop> troops = new ArrayList<Troop>();

	// called by Grid
	public void remove_territory(int x, int y) {
		// TO DO
	}

	// called by add_territory
	private void attach(ArrayList<Tile> list, int x, int y) {
		if (g.valid(x, y)) {
			list.add(g.piece(x, y));
		}
	}

	// called by add_territory
	private void remove_duplicate() {
		Set<Tile> set = new LinkedHashSet<>();
		set.addAll(owned);
		owned.clear();
		owned.addAll(set);
		set.clear();

		set.addAll(adjacent);
		adjacent.clear();
		adjacent.addAll(set);
		set.clear();

		set.addAll(visible);
		visible.clear();
		visible.addAll(set);
		set.clear();

	}

	// called by Grid and constructor
	public void add_territory(int x, int y) {
		g.piece(x, y).set_owner(this);
		attach(owned, x, y);
		attach(adjacent, x - 1, y - 1);
		attach(adjacent, x - 1, y);
		attach(adjacent, x, y - 1);
		attach(adjacent, x, y + 1);
		attach(adjacent, x + 1, y);
		attach(adjacent, x + 1, y + 1);
		attach(visible, x - 2, y);
		attach(visible, x - 2, y - 1);
		attach(visible, x - 2, y - 2);
		attach(visible, x - 1, y + 1);
		attach(visible, x - 1, y - 2);
		attach(visible, x, y - 2);
		attach(visible, x, y + 2);
		attach(visible, x + 1, y + 2);
		attach(visible, x + 1, y - 1);
		attach(visible, x + 2, y + 2);
		attach(visible, x + 2, y + 1);
		attach(visible, x + 2, y);
		visible.removeAll(adjacent);
		visible.removeAll(owned);
		adjacent.removeAll(owned);
		remove_duplicate();
	}

	// checked

	public void expand_territory(Tile t) {
		if (t.owner == this) {
			System.out.println(this + ", you have already owned this tile.");
			return;
		}

		if (t.tempted_owner == this) {
			add_territory(t.get_x(), t.get_y());
			destroy_Unit(settlers.get(0));
			t.tempted_owner = null;
			System.out.println(this + ", You just concured the tile " + t.get_x() + " " + t.get_y() + " ");
		} else {
			System.out.println(this
					+ ", Please first put at least a settler on this tile for several turns, then you can concur this tile.");
		}
	}

	public Player(Grid G, int x, int y, String s) {
		g = G;
		home_x = x;
		home_y = y;
		add_territory(x, y);
		name = s;
		g.add_player(this);
	}

	private ArrayList<Tile> path_to(Tile t) {
		ArrayList<Tile> answer = new ArrayList<Tile>();
		return answer;
	}

	// called by UI
	// get path
	public void collect(Grid.RESOURCES type) {
		Comparator<Tile> comp = new Tile_comparator(type);
		PriorityQueue<Tile> queue = new PriorityQueue<Tile>(comp);
		queue.addAll(owned);
		// TO DO : better way of arranging workers, or better style
		for (int i = 0; i < workers.size(); ++i) {
			workers.get(i).order_move(path_to(queue.poll()));
			// this ensures that the program wont crash. subject to change.
			if (queue.isEmpty()) {
				queue.addAll(owned);
			}
		}
	}

	// not a good choice, need to be fixed.
	// called by Unit so that the Unit knows to change the whether Tiles have them.
	public Grid the_grid() {
		return g;
	}

	public void destroy_Unit(Unit u) {
		if (u instanceof Settler) {
			settlers.remove(u);
		} else if (u instanceof Worker) {
			workers.remove(u);
		} else if (u instanceof Troop) {
			troops.remove(u);
		} else {
			System.out.println("destroy failed");
		}
		g.piece(u.get_location_x(), u.get_location_y()).remove_unit(u);
	}

	public void add_Unit(Unit u) {
		if (u instanceof Settler) {
			settlers.add((Settler) u);
		} else if (u instanceof Worker) {
			workers.add((Worker) u);
		} else if (u instanceof Troop) {
			troops.add((Troop) u);
		} else {
			System.out.println("adding unit failed");
		}
		g.piece(u.get_location_x(), u.get_location_y()).add_unit(u);
	}

	// called by UI
	public void build_unit(Unit.TYPE type) {
		switch (type) {
		case SETTLER:
			Settler person_s = new Settler(home_x, home_y, this);
			settlers.add(person_s);
			g.piece(home_x, home_y).add_unit(person_s);
			break;
		case WORKER:
			Worker person_w = new Worker(home_x, home_y, this);
			workers.add(person_w);
			g.piece(home_x, home_y).add_unit(person_w);
			break;
		case TROOP:
			Troop person_t = new Troop(home_x, home_y, this);
			troops.add(person_t);
			g.piece(home_x, home_y).add_unit(person_t);
			break;
		}
	}

	public void move_units() {
		for (Settler s : settlers) {
			s.move();
		}
		for (Worker w : workers) {
			w.move();
		}
		for (Troop t : troops) {
			t.move();
		}
	}

	public void add_resources(double[] effi) {
		for (int i = 0; i < Grid.RESOURCES_TYPES; ++i) {
			resources[i] += effi[i];
		}
	}

	public void print_resources() {
		System.out.println(this + " has territories:  " + owned);
		System.out.println(this + " has resources: " + resources[0] + " " + resources[1]);
		System.out.println(this + " has settlers: " + settlers);
		System.out.println(this + " has workers: " + workers);
		System.out.println(this + " has troops:  " + troops);
	}

	public String toString() {
		return name;

	}
}
