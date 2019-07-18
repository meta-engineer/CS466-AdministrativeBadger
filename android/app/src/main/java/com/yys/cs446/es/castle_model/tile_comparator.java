package com.yys.cs446.es.castle_model;

import java.util.Comparator;
import com.yys.cs446.es.castle_model.tile.TILETYPE;

public class tile_comparator implements Comparator<tile> {
	private TILETYPE r = TILETYPE.GRAIN;

	// construct tile_comparator for 1 specific TILETYPE
	public tile_comparator(TILETYPE res) {
		r = res;
	}

	@Override
	public int compare(tile t1, tile t2) {
		// if types not equal considered "even"
		if (t1.getType() != t2.getType()) {
			return 0;
		}
		if (t1.efficiency() < t2.efficiency()) {
			return -1;
		} else if (t1.efficiency() > t2.efficiency()) {
			return 1;
		} else {
			return 0;
		}
	}

}
