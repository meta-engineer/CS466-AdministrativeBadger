package com.yys.cs446.es.castle_model;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.PriorityQueue;
import java.util.Set;

import com.yys.cs446.es.castle_model.tile.TILETYPE;

public class player {

	public enum RESOURCES {
	    NONE,
		FOOD,
		LUMBER,
		STONE
	}

	// should be a collection of "home" tiles for each settlement?
	private int home_x, home_y;
	private float HPMax;
	private float HP;
	private grid g;
	//SIDE LENGTH is protected can this be restructured?
	//private double priority[][] = new double[grid.SIDE_LENGTH][grid.SIDE_LENGTH];

	// for debugging purpose, the following 6 arrays are set to public, please
	// change them to private after debugging.
	public ArrayList<tile> owned;
	public ArrayList<tile> adjacent;
	public ArrayList<tile> visible;

	// the point of having class hierarchies is to let them delegate themselves
    public ArrayList<unit> myUnits;
	//public ArrayList<worker> workers = new ArrayList<worker>();
	//public ArrayList<settler> settlers = new ArrayList<settler>();
	//public ArrayList<troop> troops = new ArrayList<troop>();

	private HashMap<RESOURCES, Double> resourceInventory;
	private RESOURCES targetResource;

	private ArrayList<tile> defendedTiles;

	private unit.TYPE producingUnitType;
	private float unitProgress;
	private final float unitProgressMax = 100;

	// constructor
	public player(grid G, int x, int y) {
		g = G;
        home_x = x;
        home_y = y;
		if (!g.add_player(this)) {
		    // could not add player to grid (controller will have to catch)
            throw new IllegalArgumentException("Could not add player to grid here");
        }
        HPMax = 1000;
		HP = 100;
        owned = new ArrayList<tile>();
        adjacent = new ArrayList<tile>();
        visible = new ArrayList<tile>();
		add_territory(x, y);

        myUnits = new ArrayList<unit>();

		// copy resources enum into array of player inventory
        resourceInventory = new HashMap<RESOURCES, Double>();
		for (RESOURCES r : RESOURCES.values()) {
			// for testing
			resourceInventory.put(r, 50.0);
		}
		targetResource = RESOURCES.NONE;

		producingUnitType = unit.TYPE.NONE;
		unitProgress = 0;

		defendedTiles = new ArrayList<tile>();
		// defended tiles are home and all 6 around it
		defendedTiles.add(g.piece(home_x, home_y));
		defendedTiles.add(g.piece(home_x - 1, home_y + 1));
		defendedTiles.add(g.piece(home_x - 1, home_y));
		defendedTiles.add(g.piece(home_x, home_y - 1));
		defendedTiles.add(g.piece(home_x, home_y + 1));
		defendedTiles.add(g.piece(home_x + 1, home_y));
		defendedTiles.add(g.piece(home_x + 1, home_y - 1));
	}

	// called every game tick to let units act (as finite state machines)
	public void act() {
		//cascade to units
	    for (unit q : myUnits) {
	        q.act();
        }

        // home tile producing units?
		if (producingUnitType != unit.TYPE.NONE) {
	    	// producing units costs food? stone?

			if (producingUnitType == unit.TYPE.WORKER) {
				if (resourceInventory.get(RESOURCES.FOOD) > 0) {
					// reduce food by 1 and increase progress (20 food to produce)
					resourceInventory.put(RESOURCES.FOOD, resourceInventory.get(RESOURCES.FOOD) - 0.2);
					unitProgress += 1;
				}
			} else if (producingUnitType == unit.TYPE.TROOP) {
				if (resourceInventory.get(RESOURCES.FOOD) > 0 && resourceInventory.get(RESOURCES.STONE) > 0) {
					// reduce food by 1 and increase progress (20 food to produce, 20 stone)
					resourceInventory.put(RESOURCES.FOOD, resourceInventory.get(RESOURCES.FOOD) - 0.2);
					resourceInventory.put(RESOURCES.STONE, resourceInventory.get(RESOURCES.STONE) - 0.2);
					unitProgress += 1;
				} else {
					// if not enough resources: send notification message?
				}
			} else if (producingUnitType == unit.TYPE.SETTLER) {
				if (resourceInventory.get(RESOURCES.FOOD) > 0 && resourceInventory.get(RESOURCES.LUMBER) > 0) {
					// reduce food by 1 and increase progress (20 food to produce, 20 stone)
					resourceInventory.put(RESOURCES.FOOD, resourceInventory.get(RESOURCES.FOOD) - 0.2);
					resourceInventory.put(RESOURCES.LUMBER, resourceInventory.get(RESOURCES.LUMBER) - 0.2);
					unitProgress += 1;
				}
			}

			// If progress reaches cap then ACTUALLY build a unit of requested type
			// and reset progress for next unit
			if (unitProgress >= unitProgressMax) {
				build_unit(producingUnitType);
				producingUnitType = unit.TYPE.NONE;
				unitProgress = 0;
			}
		}

		//recover HP
		HP += 1;
    }

    // ****** UI CALLED ACTIONS TO CHANGE STATES *******
    // *************************************************

    public void setTargetResource(RESOURCES tar) {
	    targetResource = tar;
    }

	// called by UI
	public boolean start_build_unit(unit.TYPE type) {
		// can interrupt already producing unit?
		// sure but don't keep progress (unless selecting the same)
		if (type != producingUnitType) {
			producingUnitType = type;
			unitProgress = 0;
		}
		return true;
	}

    // why does the player check tiles, isn't this what grid/unit is supposed to do?
    public void expand_territory(tile t) {
	    /*
        if (t.get_owner() == this) {
            System.out.println(this + ", you have already owned this tile.");
            return;
        }

        if (t.get_tempted_owner() == this) {
            add_territory(t.get_x(), t.get_y());
            destroy_Unit(settlers.get(0));
            t.set_tempted_owner(null);
            System.out.println(this + ", You just concured the tile " + t.get_x() + " " + t.get_y() + " ");
        } else {
            System.out.println(this
                    + ", Please first put at least a settler on this tile for several turns, then you can concur this tile.");
        }
        */
    }

    // helper for pathfinding
    // implemented in player because context specific (avoiding enemies?)
    public ArrayList<tile> getPath(tile s, tile f) {
        ArrayList<tile> answer = new ArrayList<tile>();
        // build list of tiles INCLUDING START TILE, and INCLUDING FINISH TILE
        int iX = s.get_x();
        int iY = s.get_y();
		answer.add(g.piece(iX, iY));
        // keep adding closer tiles until we get there
        while (true) {
        	// if x++ then y or y--, if x-- then y or y++
            if (iX < f.get_x()) {
            	iX += 1;
				if (iY > f.get_y()) iY -= 1;
			} else if (iX > f.get_x()) {
            	iX -= 1;
				if (iY < f.get_y()) iY += 1;
			} else {
            	// if iX is in line then iY can move either way
				if (iY > f.get_y()) iY -= 1;
				else if (iY < f.get_y()) iY += 1;
			}

            answer.add(g.piece(iX, iY));
            if (iX == f.get_x() && iY == f.get_y()) break;
        }
		Log.d("Pathfindering", "getPath: " + Integer.toString(s.get_x()) + ", " + Integer.toString(s.get_y()) + " to " + Integer.toString(f.get_x()) + ", " + Integer.toString(f.get_y()) + " of size " + Integer.toString(answer.size()));
        for (tile t : answer) {
			Log.d("Pathfindering", "madePath: " + "(" + t.get_x() + ", " + t.get_y() + ")");
		}
        return answer;
    }

    // called by units (workers) when they have finished a task and need to be updated with what to do
    public void getNewOrder(unit u) {
	    // delegate depending on what type of unit is asking for an order
        if (u instanceof worker) {
            // if there is a target resource, find path to it and give order
            if (targetResource != RESOURCES.NONE) {
                for (tile t : adjacent) {
                    if (t.getResource() == targetResource) {
                        u.order_move(getPath(g.piece((int)Math.round(u.get_location_x()), (int)Math.round(u.get_location_y())), t));
                        return;
                    }
                }
            }
        } else if (u instanceof troop) {
        	// troops should be told to move randomly around defended area if they have nothing else to do
			if (!defendedTiles.isEmpty()) {
				int tileToDefend = (int)(Math.random() * defendedTiles.size());
				u.order_move(getPath(g.piece((int)Math.round(u.get_location_x()), (int)Math.round(u.get_location_y())), defendedTiles.get(tileToDefend)));
			}
		}

    }

    // gives all units of type t the Command c (may be unsafe)
	public void overrideOrders(unit.TYPE t, unit.Command c, tile target) {
		if (target == null) target = g.piece(home_x, home_y);
		switch (c) {
			case STAY:
				for (unit u : myUnits) {
					u.order_stay();
				}
				break;
			case MOVE:
				for (unit u : myUnits) {
					u.order_move(getPath(g.piece((int)Math.round(u.get_location_x()), (int)Math.round(u.get_location_y())), target));
				}
			default:
				// don't change orders unnessisarily
				break;

		}
	}

    // ******** SETTERS **********
    // ***************************

	// called by add_territory
	// add tile [x][y] from g to list
	private void attach(ArrayList<tile> list, int x, int y) {
		// System.out.println("check valid: "+ x + " "+ y);
		if (g.valid(x, y)) {
			// System.out.println(x + " "+ y + " is valid");
			list.add(g.piece(x, y));
		}
	}
	
	// called by add_territory
	//checked
	private void remove_duplicate() {
		Set<tile> set = new LinkedHashSet<>();
		set.addAll(owned);
		owned.clear();
		owned.addAll(set);
		set.clear();

		set.addAll(adjacent);
		adjacent.clear();
		adjacent.addAll(set);
		set.clear();

		set.addAll(visible);
		visible.clear();
		visible.addAll(set);
		set.clear();

	}

	
	// called by grid and constructor
	//checked
	public void add_territory(int x, int y) {
		attach(owned, x, y);
		attach(adjacent, x - 1, y + 1);
		attach(adjacent, x - 1, y);
		attach(adjacent, x, y - 1);
		attach(adjacent, x, y + 1);
		attach(adjacent, x + 1, y);
		attach(adjacent, x + 1, y - 1);

		// 2 tile radius
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				if (i+j >= -2 && i+j <= 2) {
					attach(visible, x + i, y + j);
				}
			}
		}
		// visible is superset DO NOT remove adj/owned
		//visible.removeAll(adjacent);
		//visible.removeAll(owned);
		// adjacent is not superset remove Owned
		adjacent.removeAll(owned);
		remove_duplicate();
	}

	// for given index add all tiles in 2 tile radius to visible (remove duplicates)
	public void setVisible2TileRadius(int x, int y) {
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				if (i+j >= -2 && i+j <= 2) {
					attach(visible, x + i, y + j);
				}
			}
		}
		remove_duplicate();
	}

    // called by grid
    public void remove_territory(int x, int y) {
        // TO DO
    }

    // does this destroy a specific unit, or any unit of the same type?
	public void destroy_Unit(unit u) {
	    for (int i = 0; i < myUnits.size(); i++) {
	        if (u.equals(myUnits.get(i))) {
	            // removes unit from self
	            myUnits.remove(i);
	            // removes unit from grid knowledge (assuming grid also removes same instance)
	            g.piece((int)Math.round(u.get_location_x()), (int)Math.round(u.get_location_y())).remove_unit(u);
	            return;
            }
        }
        System.out.println("destroy failed: could not find that unit for this player");
	}

	// after unit has been created (has location) add to array
    // or when unit is captured (make sure they are not duplicated)
	public void add_Unit(unit u) {
		myUnits.add(u);
		// adds unit to tile on grid (if unit has decimal location puts on nearest tile)
		g.piece((int)Math.round(u.get_location_x()), (int)Math.round(u.get_location_y())).add_unit(u);
	}

    // builds unit type (should be a time/resource cost?)
    // adds them to home tile
	public boolean build_unit(unit.TYPE type) {
	    unit person;
	    // (-0.4, 0.3, ..., 0.4)
	    double randDist = 0; //units actually need to be on cener of tiles to avoid clipping on diagonals
		switch (type) {
		case SETTLER:
			person = new settler(home_x + randDist, home_y + randDist, this);
            add_Unit(person);
            return true;
		case WORKER:
			person = new worker(home_x + randDist, home_y + randDist, this);
            add_Unit(person);
            return true;
		case TROOP:
			person = new troop(home_x + randDist, home_y + randDist, this);
            add_Unit(person);
			return true;
		}

		return false;
	}

	// called by workers when they want to deposit resources
    public boolean add_resource(RESOURCES r, double value) {
	    if (resourceInventory.get(r) != null) {
            resourceInventory.put(r, resourceInventory.get(r) + value);
            return true;
        }
        return false;
    }

    //called by units to be sustained
	public boolean take_resource(RESOURCES r, double value) {
		if (resourceInventory.get(r) == null || resourceInventory.get(r) <= 0) {
			return false;
		}
		resourceInventory.put(r, resourceInventory.get(r) - value);
		return true;
	}

	// ******* GETTERS ***********
    // ***************************

    // called from UI for tile overlay info
    public boolean isTileVisible(int x, int y) {
        for (tile t : visible) {
            if (t.get_x() == x && t.get_y() == y) {
                return true;
            }
        }
        return false;
    }
    public boolean isTileOwned(int x, int y) {
        for (tile t : owned) {
            if (t.get_x() == x && t.get_y() == y) {
                return true;
            }
        }
        return false;
    }
    public int[] getHomeCoord() {
        int[] home = {home_x, home_y};
        return home;
    }

    // passes grid to unit to check or do work/combat
    public grid get_grid() {
	    return g;
    }

    public HashMap<RESOURCES, Double> getResourceInventory() {
	    return resourceInventory;
    };

	public float getUnitProgress() {
		return unitProgress;
	}

	public float getUnitProgressMax() {
		return unitProgressMax;
	}

	public float getHPMax() {
	    return HPMax;
    }

    public float getHP() {
	    return HP;
    }

	public void print_self() {
		System.out.println(this + " has territories:  " + owned);
		System.out.println(this + " has resources: " + resourceInventory.get(RESOURCES.FOOD) + " " + resourceInventory.get(RESOURCES.LUMBER));
		System.out.println(this + " has units: " + myUnits);
	}
}
