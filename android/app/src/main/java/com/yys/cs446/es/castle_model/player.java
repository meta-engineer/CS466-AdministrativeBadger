package com.yys.cs446.es.castle_model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.PriorityQueue;
import java.util.Set;

import com.yys.cs446.es.castle_model.tile.RESOURCES;

public class player {
	private int home_x, home_y;
	private grid g;
	//SIDE LENGTH is protected can this be restructured?
	//private double priority[][] = new double[grid.SIDE_LENGTH][grid.SIDE_LENGTH];

	// for debugging purpose, the following 6 arrays are set to public, please
	// change them to private after debugging.
	public ArrayList<tile> owned = new ArrayList<tile>();
	public ArrayList<tile> adjacent = new ArrayList<tile>();
	public ArrayList<tile> visible = new ArrayList<tile>();

	public ArrayList<worker> workers = new ArrayList<worker>();
	public ArrayList<settler> settlers = new ArrayList<settler>();
	public ArrayList<troop> troops = new ArrayList<troop>();


	// constructor
	public player(grid G, int x, int y) {
		g = G;
		home_x = x;
		home_y = y;
		add_territory(x, y);
	}

	// called from UI for tile overlay info
	public boolean isTileVisible(int x, int y) {
		for (tile t : visible) {
			if (t.get_x() == x && t.get_y() == y) {
				return true;
			}
		}
		return false;
	}
	public boolean isTileOwned(int x, int y) {
		for (tile t : owned) {
			if (t.get_x() == x && t.get_y() == y) {
				return true;
			}
		}
		return false;
	}

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
		attach(adjacent, x - 1, y + 1);
		attach(adjacent, x - 1, y);
		attach(adjacent, x, y - 1);
		attach(adjacent, x, y + 1);
		attach(adjacent, x + 1, y);
		attach(adjacent, x + 1, y - 1);

		// 2 tile radius
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				if (i+j >= -2 && i+j <= 2) {
					attach(visible, x + i, y + j);
				}
			}
		}
		// visible is superset DO NOT remove adj/owned
		//visible.removeAll(adjacent);
		//visible.removeAll(owned);
		// adjacent is not superset remove Owned
		adjacent.removeAll(owned);
		remove_duplicate();
	}

	// called by UI (through controller)
	public void collect(RESOURCES type) {
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
