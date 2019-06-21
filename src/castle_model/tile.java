package castle_model;

import java.util.ArrayList;

public interface tile {
	ArrayList<unit> units = new ArrayList<unit>();
	int x = -1, y = -1;
	public int get_x();
	public int get_y();
	public double efficiency(grid.RESOURCES r);
	public default void add_unit(unit u) {
		units.add(u);
	}
	public default void remove_unit(unit u) {
		units.remove(u);
	}
	
	// TO DO
	public default void resolve() {
		if (units.isEmpty()) {
			return;
		}
	}
	
	public String toString();
}
