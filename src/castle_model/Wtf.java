package castle_model;

public class Wtf {
	public static void main(String[] args) {
		Grid g = new Grid();
		Player one = new Player(g, 0, 0);
		Player two = new Player(g, 6, 6);

		System.out.println("world players are         " + g.players + '\n');
		
		System.out.println("Player one owns           " + one.owned);
		System.out.println("Player one is adjacent to " + one.adjacent);
		System.out.println("Player one can see        " + one.visible);

		System.out.println("player one has workers:" + one.workers);
		System.out.println("whats on " + g.piece(0));
		one.build_unit(Unit.TYPE.WORKER);
		System.out.println("player one has workers:" + one.workers);
		System.out.println("whats on " + g.piece(0));
		one.build_unit(Unit.TYPE.WORKER);
		System.out.println("player one has workers:" + one.workers);
		System.out.println("whats on " + g.piece(0));

		one.build_unit(Unit.TYPE.SETTLER);
		one.build_unit(Unit.TYPE.TROOP);
		System.out.println("whats on " + g.piece(0));

		one.collect(Grid.RESOURCES.GRAIN);
		for (int i = 0; i < 91; ++i) {
			System.out.println(g.printX(i) + " " + g.printY(i));
		}
	}
}
