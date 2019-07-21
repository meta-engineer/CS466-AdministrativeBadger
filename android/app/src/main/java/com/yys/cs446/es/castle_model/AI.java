package com.yys.cs446.es.castle_model;

import android.util.Log;

import com.yys.cs446.es.castle_model.grid;
import com.yys.cs446.es.castle_model.player;
import com.yys.cs446.es.castle_model.troop;
import com.yys.cs446.es.castle_model.unit;

public class AI extends player {

	private player myOpp;
	private int waveTime = 1000;
	private int elapsedTime = 0;

	public AI(grid G, int x, int y, player opponent) {
		super(G, x, y);
		myOpp = opponent;

	}
	
	// this function should look at the playground, make decisions and play the game.
	public void act () {
		super.act();

		//every once and a while send enemy to opp
		if (elapsedTime == waveTime/2) {
			sendWave();
		}
		// build more home defenses
		if (elapsedTime == waveTime) {
			build_unit(unit.TYPE.TROOP);
			elapsedTime = 0;
			waveTime -= 1; // speed up over time
		}
		elapsedTime += 1;
	}

	// build unit and give order (AI troops can fight outside of defended area)
	private void sendWave() {
		unit person = new troop(home_x, home_y, this);
		add_Unit(person);
		// move on path from home to opp home
		person.order_move(getPath(g.piece(home_x, home_y), g.piece(myOpp.getHomeCoord()[0], myOpp.getHomeCoord()[1])));
		return;
	}
}
