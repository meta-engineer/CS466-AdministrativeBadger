package castle_model;

public class Troop extends Unit{

	Troop(int x, int y, Player p) {
		super(x, y, p);
	}
	
	@Override
	public String toString() {
		return "Troop "+ super.toString();
	}
}
