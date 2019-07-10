package com.yys.cs446.es.castle_model;

public class settler extends unit{

	settler(double x, double y, player p) {
		super(x, y, p);
		HPMax = 100;
		HP = 10;
		AP = 1;
	}
	
}
