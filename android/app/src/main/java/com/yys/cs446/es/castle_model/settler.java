package com.yys.cs446.es.castle_model;

public class settler extends unit{

	private int buildProgress;
	private final int buildProgressMax = 100;

	settler(double x, double y, player p) {
		super(x, y, p);
		HPMax = 50;
		HP = 50;
		AP = 0;
	}

	@Override
	public double getProgress() {
		return (double)buildProgress / buildProgressMax;
	}

	@Override
	public void act() {

		super.act();
		switch (command) {
			case STAY:
				owner.getNewOrder(this);
				break;
			case MOVE:
				// super does most movement work
				// after last move switch to work command before staying
				// need to keep staying state empty for capturing enemy units
				if (path != null && path.size() <= 1) command = Command.WORK;

				break;
			case WORK:
				// build town progress if valid tile
				if (owner.get_grid().piece((int)location_x, (int)location_y).getType() != tile.TILETYPE.CITY &&
						owner.get_grid().piece((int)location_x, (int)location_y).getType() != tile.TILETYPE.TOWN) {
					buildProgress += 1;
					if (buildProgress >= buildProgressMax) {
						owner.get_grid().piece((int)location_x, (int)location_y).setType(tile.TILETYPE.TOWN, 0);
						owner.add_territory((int)location_x, (int)location_y);
						//I become the town
						owner.destroy_Unit(this);
					}
				}
				break;
			case COMBAT:
				// settlers cannot be put into this action?
				command = Command.STAY;
				break;
		}
	}
}
