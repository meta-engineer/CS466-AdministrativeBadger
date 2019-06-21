package castle_model;

import castle_model.grid.RESOURCES;

public class base_tile implements tile{
	double effi;
	int x;
	int y;
	
	public base_tile(int in_x, int in_y, double in_e){
		x = in_x;
		y = in_y;
		effi = in_e;
	}

	@Override
	public void resolve() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int get_x() {
		// TODO Auto-generated method stub
		return x;
	}

	@Override
	public int get_y() {
		// TODO Auto-generated method stub
		return y;
	}

	@Override
	public double efficiency(RESOURCES r) {
		// TODO Auto-generated method stub
		return effi;
	}
	
	public String toString() {
		return get_x() + " " + get_y();
	}
}
