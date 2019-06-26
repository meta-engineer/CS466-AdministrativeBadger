package com.yys.cs446.es.castle_model;

public class grid {
	public enum RESOURCES {
		GRAIN, WOOD
	}
	public static final int SIDE_LENGTH = 6;
	public static final int SIZE = SIDE_LENGTH * (SIDE_LENGTH - 1) * 3 + 1;
	tile tiles[];
	
	public grid() {
		tiles = new tile[SIZE];
		int count = 0;
		for (int x = 0; x < 2 * SIDE_LENGTH - 1; ++x) {
			for (int y = 0;  y < x + SIDE_LENGTH && y < 3 * SIDE_LENGTH - x - 2; ++y) {
				tiles[count] = new base_tile(x,y,0.5);
				++count;
			}
		}
		
	}
	
	//checked
	private int index(int x, int y) {
		final int OUTSIDE = SIDE_LENGTH * (SIDE_LENGTH - 1) / 2;
		if (x <= SIDE_LENGTH) {
			return (x + SIDE_LENGTH) * (x + SIDE_LENGTH  - 1) / 2 - OUTSIDE + y;   
		} else {
			return SIZE - index(2 * SIDE_LENGTH - x - 2, 3 * SIDE_LENGTH - x - y - 3) - 1;
		}
	}
	
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
		return x >= 0 && x < 2 * SIDE_LENGTH - 1 && y >= 0 && y < x + SIDE_LENGTH && y < 3 * SIDE_LENGTH - x - 2;
	}
	
	//check if an index is on the grid.
	public boolean valid(int index) {
		return index < SIZE;
	}
	
	public tile piece(int x, int y) {
		return tiles[index(x,y)];
	}
	
	public tile piece(int index) {
		return tiles[index];
	}
	
	
	
	private tile[] adjacent(int x, int y) {
		// TO DO? delete?
		return null;
	} 
}
