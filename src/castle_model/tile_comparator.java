package castle_model;

import java.util.Comparator;

public class Tile_comparator implements Comparator<Tile> {
	private Grid.RESOURCES r = Grid.RESOURCES.GRAIN;

	public Tile_comparator(Grid.RESOURCES res) {
		r = res;
	}

	@Override
	public int compare(Tile t1, Tile t2) {
		if (t1.efficiency(r) < t2.efficiency(r)) {
			return -1;
		} else if (t1.efficiency(r) > t2.efficiency(r)) {
			return 1;
		} else {
			return 0;
		}
	}

}
