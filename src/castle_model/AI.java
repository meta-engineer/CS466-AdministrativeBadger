package castle_model;

import java.util.ArrayList;

public class AI extends Player {
	public AI(Grid G, int x, int y) {
		super(G, 0, 0, "AI Player");
	}

	// this function should look at the playground, make decisions and play the
	// game.
	public void act() {
		/*
		 * 
		 * public void collect(Grid.RESOURCES type); 
		 * 		type == GRAIN || WOOD.
		 * 		assign units so that they prioritize collecting GRAIN or WOOD. 
		 * public void build_unit(Unit.TYPE type)
		 * 		produce a unit at its home tile.
		 * 		type == SETTLER || WORKER || TROOP.
		 * public void build_tile(Tile t)
		 * 		develop a tile so that it produces faster.
		 * 		for now, except the cost of resources, there is no restrictions on developing a tile.
		 * public void add_territory(int x, int y); 
		 * 		claims the tile(x,y) as its territory.
		 * 		with the cost of resources[0] and resources[1] and one Settler unit. 
		 * 
		 * information access: 
		 * 		ArrayList<Tile> owned;
		 * 		ArrayList<Tile> adjacent;
		 * 		ArrayList<Tile> visible;
		 * 		ArrayList<Settler> settlers 
		 * 		ArrayList<Worker> workers 
		 * 		ArrayList<Troop> troops 
		 * 
		 * you might want to override "public void collect(Grid.RESOURCES type)" by calling
		 * 		for each unit unit_a in settlers/workers/troops{
		 * 			some decisions -> "tile_A is the best for that unit".
		 * 			unit_A.order_move(path_to("tile_A"));
		 * 		}
		 * 
		 * 
		 */
	}
}
