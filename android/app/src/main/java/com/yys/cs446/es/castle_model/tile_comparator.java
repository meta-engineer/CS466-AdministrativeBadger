package castle_model;

import java.util.Comparator;

public class tile_comparator implements Comparator<tile> {
	private grid.RESOURCES r = grid.RESOURCES.GRAIN;

	public tile_comparator(grid.RESOURCES res) {
		r = res;
	}

	@Override
	public int compare(tile t1, tile t2) {
		if (t1.efficiency(r) < t2.efficiency(r)) {
			return -1;
		} else if (t1.efficiency(r) > t2.efficiency(r)) {
			return 1;
		} else {
			return 0;
		}
	}

}
