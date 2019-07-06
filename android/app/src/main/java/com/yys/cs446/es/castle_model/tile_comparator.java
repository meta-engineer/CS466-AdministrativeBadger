package com.yys.cs446.es.castle_model;

import java.util.Comparator;
import com.yys.cs446.es.castle_model.tile.RESOURCES;

public class tile_comparator implements Comparator<tile> {
	private RESOURCES r = RESOURCES.GRAIN;

	public tile_comparator(RESOURCES res) {
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
