package castle_model;

public class Worker extends Unit {

	Worker(int x, int y, Player p) {
		super(x, y, p);
	}

	@Override
	public String toString() {
		return "Worker " + super.toString();
	}

}
