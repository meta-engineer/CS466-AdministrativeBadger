package com.yys.cs446.es.castle_model;

import com.yys.cs446.es.castle_model.tile.TILETYPE;

import java.util.ArrayList;
import java.util.Random;

public class grid {
	// make square grid, map can be given boundary (mountain) tiles to make a hex
	// for tile [x,y]: [x-1,y] and [x+1,y] are adjacent (same row)
	//				   [x-1,y+1], [x,y-1], [x,y+1], [x+1,y-1]
	// meaning for lower rows, lower columns are closer, and for higher rows, higher columns are closer
	public final int SIDE_LENGTH;
	private tile tiles[][];

	// grid knows about players on it for view objects to draw (controller only gives playerOne)
	// why was this static?
	private ArrayList<player> players;

	public grid() {
		this(0, 0);
	}

	// mapversion is ignored atm, seed == 0 is random
	public grid(int mapVersion, int seed) {
		players = new ArrayList<player>();

		Random dice = null;
		if (seed == 0) {
			dice = new Random();
		} else {
			dice = new Random(seed);
		}

		// generate type 0 map
		switch(mapVersion) {
			default:
				SIDE_LENGTH = 21;
				tiles = new tile[SIDE_LENGTH][SIDE_LENGTH];

				for (int x = 0; x < SIDE_LENGTH; ++x) {
					for (int y = 0;  y < SIDE_LENGTH; ++y) {
						// create mountains on edges of hex
						if (x == 0 && y > SIDE_LENGTH/2 || x == SIDE_LENGTH - 1 && y < SIDE_LENGTH/2
								|| y == 0 && x > SIDE_LENGTH/2 || y == SIDE_LENGTH - 1 && x < SIDE_LENGTH/2
								|| x+y == Math.floor(SIDE_LENGTH/2) || x+y == Math.floor(SIDE_LENGTH*1.45)) {
							tiles[x][y] = new tile(x,y,0, TILETYPE.MOUNTAIN, this);
						} else if (x+y < Math.floor(SIDE_LENGTH/2) || x+y > Math.floor(SIDE_LENGTH * 1.45)) {
							// create empty tiles outside of mountains
							tiles[x][y] = new tile(x, y, 0, TILETYPE.NONE, this);
						} else {
							// randomly assign all inside tiles
							TILETYPE newType;
							double d100 = dice.nextDouble() * 100;
							if (d100 > 85) { // 15%
								newType = TILETYPE.WATER;
							} else if (d100 > 70) { // 15%
								newType = TILETYPE.GRAIN;
							} else if (d100 > 60) { // 10%
								newType = TILETYPE.WOOD;
							} else if (d100 > 50) { // 10%
								newType = TILETYPE.STONE;
							} else {				// 50%
								newType = TILETYPE.GRASS;
							}
							tiles[x][y] = new tile(x, y, 0.2, newType, this);
						}
					}
				}
				break;
		}
	}

	// just use x,y, no index functions needed...

	//check if an coordinate is on the grid.
	//uncheck
	public boolean valid(int x, int y) {
		// use 2d array to avoid tricky calculations
		return x >= 0 && x < SIDE_LENGTH  && y >= 0 && y < SIDE_LENGTH;
	}

	// this is getTile() ?
	// no validation?
	public tile piece(int x, int y) {
		return tiles[x][y];
	}

	// return distance on hex grid
	public int distance(tile s, tile f) {
		if (s.get_x() == f.get_x() && s.get_y() == f.get_y()) return 0; //base case same tile
		// otherwise return 1 + dist of closer tile
		int iX = s.get_x();
		int iY = s.get_y();
		if (iX < f.get_x()) {
			iX += 1;
			if (iY > f.get_y()) iY -= 1;
		} else if (iX > f.get_x()) {
			iX -= 1;
			if (iY < f.get_y()) iY += 1;
		} else {
			// if iX is in line then iY can move either way
			if (iY > f.get_y()) iY -= 1;
			else if (iY < f.get_y()) iY += 1;
		}
		//iX, iY is next tile (given hex constraints)
		return 1 + distance(this.piece(iX, iY), f);
	}

	public boolean add_player(player p) {
		// add player home, if not return false
		int[] newHome = p.getHomeCoord();
		// let player spawn on any tile (they should call getValidSpawnLocation if they want otherwise
		piece(newHome[0], newHome[1]).setType(TILETYPE.CITY, 0);
        piece(newHome[0], newHome[1]).set_owner(p);
		players.add(p);

		// assuming they've used getValidSpawnLocation they also need 3 starting resources

		return true;
	}

	// give grid state to view objects for drawing
	public tile[][] getTiles() {
		return tiles;
	}

	//give players to tiles
	// tileView needs access to all players on grid (controller only tells which one is self)
	public ArrayList<player> getPlayers() {
		return players;
	}

	// return iterable of tiles around tile[x][y] ??
	public tile[] adjacent(int x, int y) {
		ArrayList<tile> adj = new ArrayList<tile>();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (Math.abs(i+j) >= 2) continue; // hex constraints
				if (i==0 && j==0) continue; // dont add center
				try {
					adj.add(piece(x+i, y+j));
				} catch (Exception e) {
					// dont add, index out of range
				}
			}
		}

		return adj.toArray(new tile[adj.size()]);
	}

	public int[] getValidSpawnLocation() {
		int[] goodSpawn = new int[2];
		while (true) {
			goodSpawn[0] = (int)(Math.random() * SIDE_LENGTH);
			goodSpawn[1] = (int)(Math.random() * SIDE_LENGTH);
			tile checkTile = piece(goodSpawn[0], goodSpawn[1]);
			//check if spawn location is ok
			if (checkTile.getMovementFactor() != 0
					&& checkTile.getType() != TILETYPE.CITY
					&& checkTile.getType() != TILETYPE.TOWN) {
				// don't spawn too close to other players
				// throw exception for double loop break
				try {
					for (tile t : adjacent(goodSpawn[0], goodSpawn[1])) {
						if (t.getType() == TILETYPE.TOWN || t.getType() == TILETYPE.CITY) {
							throw new Exception("Bad spawn location");
						}
					}
				} catch (Exception e) {
					continue;
				}

				return goodSpawn;
			}
		}
	}
}
