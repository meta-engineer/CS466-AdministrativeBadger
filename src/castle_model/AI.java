package castle_model;

import java.util.ArrayList;

public class AI extends Player {
	private static int num_AIs = 0;

	public AI(Grid G, int x, int y){
		String AI_name = "AI Player" + (++ num_AIs);
		super(G, x, y, AI_name);
	}

	public AI(Grid G) {
		int x, y;
		/* 	for(Tile t : G.get_tiles()){
		 *		Inpsect all tiles in Grid and pick the optimal location to 
		 *		spawn.
		 *	} 
		 */
		this(G, x, y);
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
		/* LOOK AT THE PLAYGOUND:
		 
		 *
		 * Decision Tree/ Bayes / CSP/ A* making action decisions based on
		 * game dynamics
		 */
        for(Tile t : the_grid().get_tiles()){
        }
        
        for(Player p : the_grid().players){
        }
    
		build_unit(Unit.TYPE.WORKER);
		build_unit(Unit.TYPE.WORKER);
		build_unit(Unit.TYPE.SETTLER);
		expand_territory(g.piece(0,1));
	}
}

