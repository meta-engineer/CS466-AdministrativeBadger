package castle_model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class player {
	private int home_x, home_y;
	private grid g;
	private double priority[] = new double[grid.SIZE];

	// for debugging purpose, the following 6 arrays are set to public, please
	// change them to private after debugging.
	public ArrayList<tile> owned = new ArrayList<tile>();
	public ArrayList<tile> adjacent = new ArrayList<tile>();
	public ArrayList<tile> visible = new ArrayList<tile>();

	public ArrayList<worker> workers = new ArrayList<worker>();
	public ArrayList<settler> settlers = new ArrayList<settler>();
	public ArrayList<troop> troops = new ArrayList<troop>();

	// called by grid
	public void remove_territory(int x, int y) {
		// TO DO
	}

	// called by add_territory
	//checked
	private void attach(ArrayList list, int x, int y) {
		// System.out.println("check valid: "+ x + " "+ y);
		if (g.valid(x, y)) {
			// System.out.println(x + " "+ y + " is valid");
			list.add(g.piece(x, y));
		}
	}
	
	// called by add_territory
	//checked
	private void remove_duplicate() {
		Set<tile> set = new LinkedHashSet<>();
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

	
	// called by grid and constructor
	//checked
	public void add_territory(int x, int y) {
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

	//checked
	public player(grid G, int x, int y) {
		g = G;
		home_x = x;
		home_y = y;
		add_territory(x, y);
	}

	// called by UI
	public void collect(grid.RESOURCES type) {
		Comparator<tile> comp = new tile_comparator(type);
		PriorityQueue<tile> queue = new PriorityQueue<tile>(comp);
		queue.addAll(owned);
		// TO DO : better way of arranging workers, or better style
		for (int i = 0; i < workers.size(); ++i) {
			workers.get(i).order_move(queue.poll());
			// this ensures that the program wont crash. subject to change.
			if (queue.isEmpty()) {
				queue.addAll(owned);
			}
		}
	}

	// not a good choice, need to be fixed.
	// called by unit so that the unit knows to change the whether tiles have them.
	public grid the_grid() {
		return g;
	}

	// TO DO
	public void destroy_unit(unit u) {
		if (u instanceof settler) {
			settlers.remove(u);
		} else if (u instanceof worker) {
			workers.remove(u);
		} else if (u instanceof troop) {
			troops.remove(u);
		} else {

		}
		g.piece(u.get_location_x(), u.get_location_y()).remove_unit(u);
	}

	// called by UI
	public void build_unit(unit.TYPE type) {
		switch (type) {
		case SETTLER:
			settler person_s = new settler(home_x, home_y, this);
			settlers.add(person_s);
			g.piece(home_x, home_y).add_unit(person_s);
			break;
		case WORKER:
			worker person_w = new worker(home_x, home_y, this);
			workers.add(person_w);
			g.piece(home_x, home_y).add_unit(person_w);
			break;
		case TROOP:
			troop person_t = new troop(home_x, home_y, this);
			troops.add(person_t);
			g.piece(home_x, home_y).add_unit(person_t);
			break;
		}
	}

}
