package castle_model;

public class Wtf {
	public static void main(String[] args) {
		grid g = new grid();
		player one = new player(g,0,0);
		player two = new player(g,6,6);
		one.add_territory(0,1);

		System.out.println("player one owns " + one.owned);
		System.out.println("player one is adjacent to " + one.adjacent);
		System.out.println("player one can see " + one.visible);
		
		//untested.
		one.build_unit(unit.TYPE.WORKER);
		one.collect(grid.RESOURCES.GRAIN);
	}
}
