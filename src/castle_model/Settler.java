package castle_model;

public class Settler extends Unit {

	Settler(int x, int y, Player p) {
		super(x, y, p);
	}

	@Override
	public String toString() {
		return "Settler " + super.toString();
	}
}
