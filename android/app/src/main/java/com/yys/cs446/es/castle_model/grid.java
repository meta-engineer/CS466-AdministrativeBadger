package com.yys.cs446.es.castle_model;

import com.yys.cs446.es.castle_model.tile.RESOURCES;

import java.util.Random;

public class grid {
	// make square grid, map can be given boundary (mountain) tiles to make a hex
	// for tile [x,y]: [x-1,y] and [x+1,y] are adjacent (same row)
	//				   [x-1,y-1], [x,y-1], [x,y-1], [x+1,y-1]
	// meaning for lower rows, lower columns are closer, and for higher rows, higher columns are closer
	private final int SIDE_LENGTH;
	private tile tiles[][];
	
	public grid() {
		// generate type 0 map
		SIDE_LENGTH = 11;
		tiles = new tile[SIDE_LENGTH][SIDE_LENGTH];

		for (int x = 0; x < SIDE_LENGTH; ++x) {
			for (int y = 0;  y < SIDE_LENGTH; ++y) {
				// create mountains on edges of hex
				if (x == 0 && y > SIDE_LENGTH/2 || x == SIDE_LENGTH - 1 && y < SIDE_LENGTH/2
						|| y == 0 && x > SIDE_LENGTH/2 || y == SIDE_LENGTH - 1 && x < SIDE_LENGTH/2
						|| x+y == Math.floor(SIDE_LENGTH/2) || x+y == Math.ceil(SIDE_LENGTH * 1.3)) {
					tiles[x][y] = new tile(x,y,0, RESOURCES.MOUNTAIN);
				} else if (x+y < Math.floor(SIDE_LENGTH/2) || x+y > Math.ceil(SIDE_LENGTH * 1.3)) {
					tiles[x][y] = new tile(x, y, 0, RESOURCES.NONE);
				} else {
					RESOURCES newType = RESOURCES.NONE;
					double d100 = Math.random() * 100;
					if (d100 > 85) {
						newType = RESOURCES.WATER;
					} else if (d100 > 70) {
						newType = RESOURCES.GRAIN;
					} else if (d100 > 55) {
						newType = RESOURCES.WOOD;
					} else {
						newType = RESOURCES.GRASS;
					}
					tiles[x][y] = new tile(x, y, 0.5, newType);
				}
			}
		}
		
	}

	// what does this function do?
	//checked
	private int index(int x, int y) {
		final int OUTSIDE = SIDE_LENGTH * (SIDE_LENGTH - 1) / 2;
		if (x <= SIDE_LENGTH) {
			return (x + SIDE_LENGTH) * (x + SIDE_LENGTH  - 1) / 2 - OUTSIDE + y;   
		} else {
			return SIDE_LENGTH * SIDE_LENGTH - index(2 * SIDE_LENGTH - x - 2, 3 * SIDE_LENGTH - x - y - 3) - 1;
		}
	}

	// what does this function do?
	//uncheck
	private int indexX(int index) {
		int answer = 0;
		int length = SIDE_LENGTH;
		while (index >= length) {
			index -= length;
			++answer;
			if (answer < SIDE_LENGTH) {
				++length;
			} else {
				--length;
			}
		}
		return answer;
	}

	// what does this function do?
	//uncheck
	private int indexY(int index) {
		int answer = 0;
		int length = SIDE_LENGTH;
		while (index >= length) {
			index -= length;
			++answer;
			if (answer < SIDE_LENGTH) {
				++length;
			} else {
				--length;
			}
		}
		return index;
	}
	
	//check if an coordinate is on the grid.
	//uncheck
	public boolean valid(int x, int y) {
		// use 2d array to avoid tricky calculations
		return x >= 0 && x < SIDE_LENGTH  && y >= 0 && y < SIDE_LENGTH;
	}
	
	//check if an index is on the grid.
	// **should avoid using flattened indicies
	public boolean valid(int index) {
		return index < (SIDE_LENGTH * SIDE_LENGTH);
	}

	// getTile() ?
	public tile piece(int x, int y) {
		return tiles[x][y];
	}

	// give grid state to view objects
	public tile[][] getGrid() {
		return tiles;
	}

	// **should avoid using flattened indicies
	public tile piece(int index) {
		return tiles[(int)(index%SIDE_LENGTH)][(int)(index/SIDE_LENGTH)];
	}

	private tile[] adjacent(int x, int y) {
		// TO DO? delete?
		return null;
	} 
}
