package com.yys.cs446.es.castle_model;

import castle_model.grid.RESOURCES;

public class grain_tile extends special_tile{
	double multiplier = 2;
	double effi;
	public grain_tile(double x){
		effi = x;
	}
	

	public void change_multiplier(double change) {
		multiplier = change;
	}

	@Override
	public void resolve() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int get_x() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int get_y() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public double efficiency(RESOURCES r) {
		// TODO Auto-generated method stub
		return 0;
	}
}
