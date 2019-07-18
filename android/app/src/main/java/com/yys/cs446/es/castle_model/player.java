package com.yys.cs446.es.castle_model;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
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
	private boolean heal;
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
	private double resourceMax;
	private RESOURCES targetResource;

	private ArrayList<tile> defendedTiles;
	private ArrayList<tile> expandTiles;

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
		HP = 1000;
		heal = false;
        owned = new ArrayList<tile>();
        adjacent = new ArrayList<tile>();
        visible = new ArrayList<tile>();
		add_territory(x, y);

        myUnits = new ArrayList<unit>();

		// copy resources enum into array of player inventory
        resourceInventory = new HashMap<RESOURCES, Double>();
        for (RESOURCES r : RESOURCES.values()) {
        	resourceInventory.put(r, 0.0);
		}
		resourceMax = 999;
		targetResource = RESOURCES.NONE;

		producingUnitType = unit.TYPE.NONE;
		unitProgress = 0;

		ArrayList<tile> defaultTiles = new ArrayList<tile>();
		// defended tiles are home and all 6 around it
		// 5 tiles around center
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				// check validity of tiles in radius, add if valid
				if (i+j >= -1 && i+j <= 1
						&& home_x + i >= 0
						&& home_x + i < g.SIDE_LENGTH
						&& home_y + j >= 0
						&& home_y + j < g.SIDE_LENGTH
						&& g.piece(home_x + i, home_y + j).getMovementFactor() > 0) {
					defaultTiles.add(g.piece(home_x + i, home_y + j));
				}
			}
		}
		set_defended_tiles(defaultTiles);

		expandTiles = new ArrayList<tile>();
	}

	// called every game tick to let units act (as finite state machines)
	public void act() {
		//cascade to units
		if (myUnits != null && !myUnits.isEmpty()) {
			try {
				for (unit q : myUnits) {
					q.act();
				}
			} catch (Exception e) {
				Log.d("DEBUG", "act: " + e.getMessage());
			}
		}

        // home tile producing units?
		if (producingUnitType != unit.TYPE.NONE) {
	    	// producing units costs food? stone?

			if (producingUnitType == unit.TYPE.WORKER) {
				if (resourceInventory.get(RESOURCES.FOOD) > 5) {
					// reduce food by 1 and increase progress (20 food to produce)
					resourceInventory.put(RESOURCES.FOOD, resourceInventory.get(RESOURCES.FOOD) - 0.2);
					unitProgress += 1;
				}
			} else if (producingUnitType == unit.TYPE.TROOP) {
				if (resourceInventory.get(RESOURCES.FOOD) > 5 && resourceInventory.get(RESOURCES.STONE) > 5) {
					// reduce food by 1 and increase progress (20 food to produce, 20 stone)
					resourceInventory.put(RESOURCES.FOOD, resourceInventory.get(RESOURCES.FOOD) - 0.2);
					resourceInventory.put(RESOURCES.STONE, resourceInventory.get(RESOURCES.STONE) - 0.2);
					unitProgress += 1;
				} else {
					// if not enough resources: send notification message?
				}
			} else if (producingUnitType == unit.TYPE.SETTLER) {
				if (resourceInventory.get(RESOURCES.FOOD) > 5 && resourceInventory.get(RESOURCES.LUMBER) > 5) {
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
		if (HP < HPMax && !!heal && resourceInventory.get(RESOURCES.LUMBER) > 0.1 && resourceInventory.get(RESOURCES.STONE) > 0.1) {
			take_resource(RESOURCES.STONE, 0.1);
			take_resource(RESOURCES.LUMBER, 0.1);
			HP += 1;
		}

		// comeback mechanic
        if (resourceInventory.get(RESOURCES.FOOD) < 10) add_resource(RESOURCES.FOOD, 0.01);
        if (resourceInventory.get(RESOURCES.LUMBER) < 10) add_resource(RESOURCES.LUMBER, 0.01);

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
		//Log.d("Pathfindering", "getPath: " + Integer.toString(s.get_x()) + ", " + Integer.toString(s.get_y()) + " to " + Integer.toString(f.get_x()) + ", " + Integer.toString(f.get_y()) + " of size " + Integer.toString(answer.size()));
        for (tile t : answer) {
			//Log.d("Pathfindering", "madePath: " + "(" + t.get_x() + ", " + t.get_y() + ")");
		}
        return answer;
    }

    // called by units (workers) when they have finished a task and need to be updated with what to do
    public void getNewOrder(unit u) {
	    // delegate depending on what type of unit is asking for an order
        if (u instanceof worker) {
            // if there is no target resource, go home
            if (targetResource == RESOURCES.NONE) {
            	// move to stay if already home
				if (home_x == u.get_location_x() && home_y == u.get_location_y()) {
					u.order_stay();
				} else {
					u.order_move(getPath(g.piece((int) Math.round(u.get_location_x()), (int) Math.round(u.get_location_y())), g.piece(home_x, home_y)));
				}
			} else {
				// if there is a target resource, find path to it and give order
            	// get tile based on closest of type that is not being harvested?
				ArrayList<tile> availableTiles = new ArrayList<>( adjacent );
				// sort by ...? tiles closest to player.homeTile
				// Collections.shuffle(availableTiles);
				Collections.sort(availableTiles, new Comparator<tile>() {
					@Override
					public int compare(tile lt, tile rt) {
						// comparator is defined inside player, so has access to players class variables?
						double lt_dist_square = (home_x - lt.get_x())^2 + (home_y - lt.get_y())^2;
						double rt_dist_square = (home_x - rt.get_x())^2 + (home_y - rt.get_y())^2;
						return Double.compare(lt_dist_square, rt_dist_square);
					}
				});
                for (tile t : availableTiles) {
                	// if tile is desired resource
                    if (t.getResource() == targetResource) {
                    	// AND its not already being collected
						try {
							for (unit ut : t.get_units()) {
								if (ut instanceof worker && ut.status() == unit.Command.WORK) {
									// try next tile
									throw new Exception("Tile is busy");
								}
							}
						} catch (Exception e) {
							continue;
						}
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
		} else if (u instanceof settler) {
        	if (!expandTiles.isEmpty()) {
        		// order settler to move to expand tile, then stop other settlers from going there
        		u.order_move(getPath(g.piece((int)Math.round(u.get_location_x()), (int)Math.round(u.get_location_y())), expandTiles.get(0)));
        		expandTiles.remove(0);
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
		g.piece(x, y).set_owner(this);

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
		try {
			g.piece((int) Math.round(u.get_location_x()), (int) Math.round(u.get_location_y())).remove_unit(u);
			myUnits.remove(u);
		} catch (Exception e) {
			Log.d("DEBUG", "destroy_Unit: " + e.getMessage());
		}
	}

	// after unit has been created (has location) add to array
    // or when unit is captured (make sure they are not duplicated)
	public void add_Unit(unit u) {
		myUnits.add(u);
		// adds unit to tile on grid (if unit has decimal location puts on nearest tile)
		g.piece((int)Math.round(u.get_location_x()), (int)Math.round(u.get_location_y())).add_unit(u);
	}

    // builds unit type (time/resource cost is in start_build_unit
    // adds them to home tile
	public boolean build_unit(unit.TYPE type) {
	    unit person;
		switch (type) {
		case SETTLER:
			person = new settler(home_x, home_y, this);
            add_Unit(person);
            return true;
		case WORKER:
			person = new worker(home_x, home_y, this);
            add_Unit(person);
            return true;
		case TROOP:
			person = new troop(home_x, home_y, this);
            add_Unit(person);
			return true;
		}

		return false;
	}

	// called by workers when they want to deposit resources
    public boolean add_resource(RESOURCES r, double value) {
	    if (resourceInventory.get(r) != null) {
	    	if (resourceInventory.get(r) + value > resourceMax) {
	    		resourceInventory.put(r, resourceMax);
			} else {
				resourceInventory.put(r, resourceInventory.get(r) + value);
			}
            return true;
        }
        return false;
    }

    //called by units to be sustained
	public boolean take_resource(RESOURCES r, double value) {
		if (resourceInventory.get(r) == null || resourceInventory.get(r) - value < 0) {
			return false;
		}
		resourceInventory.put(r, resourceInventory.get(r) - value);
		return true;
	}

	public boolean set_defended_tiles(ArrayList<tile> dT) {
		// validity check for dT?
		defendedTiles = dT;
		return true;
	}

	public void set_heal(boolean h) {
		heal = h;
	}

	public void toggle_heal() {
		heal = !heal;
	}

	public void takeDamage(double dmg) {
		HP -= dmg;
		if (HP <= 0) {
			HP = 0;
		}
		Log.d("DEBUG", "takeDamage: " + Double.toString(HP));
	}

	public void add_expand_tile(tile t) {
		// is really a toggle
		if (expandTiles.contains(t)) expandTiles.remove(t);
		else expandTiles.add(t);
	}

	// ******* GETTERS ***********
    // ***************************

    // called from UI for tile overlay info
    public boolean isTileVisible(int x, int y) {
		try {
			for (tile t : visible) {
				if (t.get_x() == x && t.get_y() == y) {
					return true;
				}
			}
		} catch (Exception e) {
			Log.d("DEBUG", "isTileVisible: " + e.getMessage());
		}
		return false;
    }
    public boolean isTileOwned(int x, int y) {
		try {
			for (tile t : owned) {
				if (t.get_x() == x && t.get_y() == y) {
					return true;
				}
			}
		} catch (Exception e) {
			Log.d("DEBUG", "isTileOwned: " + e.getMessage());
		}
        return false;
    }

    public boolean isTileAdjacent(int x, int y) {
		try {
			for (tile t : adjacent) {
				if (t.get_x() == x && t.get_y() == y) {
					return true;
				}
			}
		} catch (Exception e) {
			Log.d("DEBUG", "isTileAdjacent: " + e.getMessage());
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

	public ArrayList<tile> getDefendedTiles() {
		return defendedTiles;
	}

	public ArrayList<tile> getExpandTiles() {
		return expandTiles;
	}

	public void print_self() {
		System.out.println(this + " has territories:  " + owned);
		System.out.println(this + " has resources: " + resourceInventory.get(RESOURCES.FOOD) + " " + resourceInventory.get(RESOURCES.LUMBER));
		System.out.println(this + " has units: " + myUnits);
	}
}
