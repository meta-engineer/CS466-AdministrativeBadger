package castle_model;

public class Wtf {
	public static void main(String[] args) {
		// tested moving worker unit
		// tested worker production
		// tested moving troop unit
		// tested fighting between troop unit
		// tested moving settler unit
		// tested settler unit expand territory
		// tested interaction between worker unit and troop unit. need test again
		// tested interaction between settler unit and troop unit . need test again

		Grid g = new Grid();
		Player one = new Player(g, 0, 0, "one");
		Player two = new Player(g, 0, 2, "two");
		System.out.println("turn 0 start");
		one.build_unit(Unit.TYPE.WORKER);
		one.build_unit(Unit.TYPE.WORKER);
		one.build_unit(Unit.TYPE.SETTLER);
		two.build_unit(Unit.TYPE.TROOP);
		one.settlers.get(0).order_move(g.piece(0, 1));
		one.workers.get(0).order_move(g.piece(0, 1));
		g.resolve_all();
		one.print_resources();
		two.print_resources();
		System.out.println("turn 1 start");
		two.troops.get(0).order_move(g.piece(0, 1));
		for (int i = 0; i < 10; ++i) {
			System.out.println();
			System.out.println();
			g.resolve_all();
			System.out.println("turn " + (i + 2) + " start");
			System.out.println();
			one.print_resources();
			two.print_resources();
			one.expand_territory(g.piece(0, 1));
			two.expand_territory(g.piece(0, 1));
			System.out.println();
			System.out.println();
		}
	}
}
