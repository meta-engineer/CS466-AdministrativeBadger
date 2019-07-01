package castle_model;

import java.util.ArrayList;
import java.util.List;

public class Grid {
	List<Player> players;
	public enum RESOURCES {
		GRAIN, WOOD
	}
	public static final int SIDE_LENGTH = 6;
	public static final int SIZE = SIDE_LENGTH * (SIDE_LENGTH - 1) * 3 + 1;
	Tile tiles[];
	
	public Grid() {
		tiles = new Tile[SIZE];
		int count = 0;
		for (int x = 0; x < 2 * SIDE_LENGTH - 1; ++x) {
			for (int y = 0;  y < x + SIDE_LENGTH && y < 3 * SIDE_LENGTH - x - 2; ++y) {
				tiles[count] = new Tile(x,y,0.5);
				++count;
			}
		}
		players = new ArrayList<Player>();
		
	}
	
	private int index(int x, int y) {
		final int OUTSIDE = SIDE_LENGTH * (SIDE_LENGTH - 1) / 2;
		if (x <= SIDE_LENGTH) {
			return (x + SIDE_LENGTH) * (x + SIDE_LENGTH  - 1) / 2 - OUTSIDE + y;   
		} else {
			return SIZE - index(2 * SIDE_LENGTH - x - 2, 3 * SIDE_LENGTH - x - y - 3) - 1;
		}
	}
	
	public int indexX(int index) {
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
	
	public int indexY(int index) {
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
	
	double printY(int in) {
		int x = indexX(in);
		int y = indexY(in);
		if (x < SIDE_LENGTH) {
			return SIDE_LENGTH - x - 1 + 2 * y;
		}
		return 2 * y - SIDE_LENGTH + 1 + x;
	}

	double printX(int in) {
		return indexX(in) * Math.sqrt(3);
	}

	//check if an coordinate is on the Grid.
	//uncheck
	public boolean valid(int x, int y) {
		return x >= 0 && x < 2 * SIDE_LENGTH - 1 && y >= 0 && y < x + SIDE_LENGTH && y < 3 * SIDE_LENGTH - x - 2;
	}
	
	//check if an index is on the Grid.
	public boolean valid(int index) {
		return index < SIZE;
	}
	
	public Tile piece(int x, int y) {
		return tiles[index(x,y)];
	}
	
	public Tile piece(int index) {
		return tiles[index];
	}
	
	public void resolve_all() {
		for (Tile t : tiles) {
			t.resolve();
		}
		for (Player p : players) {
			p.move_units();
		}
	}

	public void add_player(Player p) {
		players.add(p);
	}
	
	public Tile[] get_tiles()  {
		return tiles;
	}
}
