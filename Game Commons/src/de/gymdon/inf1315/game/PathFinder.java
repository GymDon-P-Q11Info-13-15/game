package de.gymdon.inf1315.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PathFinder {

    private Tile[][] map;
    private Tile[][] mapA;
    private List<Tile> open = new ArrayList<Tile>();
    private List<Tile> closed = new ArrayList<Tile>();
    private List<Point> lastPath = new ArrayList<Point>();

    public PathFinder(Tile[][] map) {

	this.map = map;
	generateMapA();

    }

    public void setMap(Tile[][] map) {

	this.map = map;
	generateMapA();

    }

    private void generateMapA() {

	mapA = new Tile[map.length][map[0].length];

	for (int xP = 0; xP < map.length; xP++) {

	    for (int yP = 0; yP < map[0].length; yP++) {

		if (map[xP][yP] == Tile.grass || map[xP][yP] == Tile.grass2) {

		    mapA[xP][yP] = new Tile(0, "grass").setGroundFactor(1);
		    mapA[xP][yP].x = xP;
		    mapA[xP][yP].y = yP;

		}
		if (map[xP][yP] == Tile.sand || map[xP][yP] == Tile.sand2) {

		    mapA[xP][yP] = new Tile(1, "sand").setGroundFactor(1.2);
		    mapA[xP][yP].x = xP;
		    mapA[xP][yP].y = yP;

		}
		if (map[xP][yP] == Tile.water || map[xP][yP] == Tile.water2) {

		    mapA[xP][yP] = new Tile(2, "water").setGroundFactor(3);
		    mapA[xP][yP].x = xP;
		    mapA[xP][yP].y = yP;

		}

	    }

	}

    }

    public double generateH(int xStart, int yStart, int xEnd, int yEnd) {
	/**
	 * Method to generate the "H" value, returns double because it should be
	 * more precise. A more realistic value will be added later. TODO: Add
	 * more precise H value
	 */
	return 0;
	// return Math.abs(xEnd - xStart) + Math.abs(yEnd - yStart);

    }

    public List<Point> findPath(int xStart, int yStart, int xEnd, int yEnd) {

	if (xStart < 0 || yStart < 0 || xEnd < 0 || yEnd < 0)
	    return null;

	open.add(mapA[xStart][yStart]);

	if (!(xStart == 0)) {

	    if (!(mapA[xStart - 1][yStart].name == "water")) {
		open.add(mapA[xStart - 1][yStart]);
		mapA[xStart - 1][yStart].parent = mapA[xStart][yStart];
		mapA[xStart - 1][yStart].h = generateH(xStart - 1, yStart, xEnd, yEnd);
		mapA[xStart - 1][yStart].makeF();
	    }
	}

	if (!(xStart == mapA.length - 1)) {

	    if (!(mapA[xStart + 1][yStart].name == "water")) {
		open.add(mapA[xStart + 1][yStart]);
		mapA[xStart + 1][yStart].parent = mapA[xStart][yStart];
		mapA[xStart + 1][yStart].h = generateH(xStart + 1, yStart, xEnd, yEnd);
		mapA[xStart + 1][yStart].makeF();
	    }
	}

	if (!(yStart == 0)) {

	    if (!(mapA[xStart][yStart - 1].name == "water")) {
		open.add(mapA[xStart][yStart - 1]);
		mapA[xStart][yStart - 1].parent = mapA[xStart][yStart];
		mapA[xStart][yStart - 1].h = generateH(xStart, yStart - 1, xEnd, yEnd);
		mapA[xStart][yStart - 1].makeF();
	    }
	}

	if (!(yStart == mapA[0].length - 1)) {

	    if (!(mapA[xStart][yStart + 1].name == "water")) {
		open.add(mapA[xStart][yStart + 1]);
		mapA[xStart][yStart + 1].parent = mapA[xStart][yStart];
		mapA[xStart][yStart + 1].h = generateH(xStart, yStart + 1, xEnd, yEnd);
		mapA[xStart][yStart + 1].makeF();
	    }
	}
	open.remove(mapA[xStart][yStart]);
	closed.add(mapA[xStart][yStart]);
	Collections.sort(open);

	boolean ready = true;

	while (ready) {

	    int xF = open.get(0).x;
	    int yF = open.get(0).y;

	    closed.add(open.get(0));
	    open.remove(0);

	    if (!(xF == 0)) {

		if (!(mapA[xF - 1][yF].name == "water") && !(closed.contains(mapA[xF - 1][yF]))) {

		    if (open.contains(mapA[xF - 1][yF])) {

		    }

		    else {
			open.add(mapA[xF - 1][yF]);
			mapA[xF - 1][yF].parent = mapA[xF][yF];
			mapA[xF - 1][yF].h = generateH(xF - 1, yF, xEnd, yEnd);
			mapA[xF - 1][yF].makeF();
		    }
		}
	    }

	    if (!(xF == mapA.length - 1)) {

		if (!(mapA[xF + 1][yF].name == "water") && !(closed.contains(mapA[xF + 1][yF]))) {

		    if (open.contains(mapA[xF + 1][yF])) {

		    }

		    else {
			open.add(mapA[xF + 1][yF]);
			mapA[xF + 1][yF].parent = mapA[xF][yF];
			mapA[xF + 1][yF].h = generateH(xF + 1, yF, xEnd, yEnd);
			mapA[xF + 1][yF].makeF();
		    }
		}
	    }

	    if (!(yF == 0)) {

		if (!(mapA[xF][yF - 1].name == "water") && !(closed.contains(mapA[xF][yF - 1]))) {

		    if (open.contains(mapA[xF][yF - 1])) {

		    }

		    else {
			open.add(mapA[xF][yF - 1]);
			mapA[xF][yF - 1].parent = mapA[xF][yF];
			mapA[xF][yF - 1].h = generateH(xF, yF - 1, xEnd, yEnd);
			mapA[xF][yF - 1].makeF();
		    }
		}
	    }

	    if (!(yF == map[0].length - 1)) {

		if (!(mapA[xF][yF + 1].name == "water") && !(closed.contains(mapA[xF][yF + 1]))) {

		    if (open.contains(mapA[xF][yF + 1])) {

		    }

		    else {
			open.add(mapA[xF][yF + 1]);
			mapA[xF][yF + 1].parent = mapA[xF][yF];
			mapA[xF][yF + 1].h = generateH(xF, yF + 1, xEnd, yEnd);
			mapA[xF][yF + 1].makeF();
		    }
		}
	    }
	    open.remove(mapA[xF][yF]);
	    closed.add(mapA[xF][yF]);
	    Collections.sort(open);

	    if (closed.contains(mapA[xEnd][yEnd]))
		ready = false;

	}
	List<Point> list = new ArrayList<Point>();
	list = mapA[xEnd][yEnd].getParent(list);
	lastPath = list;
	return list;

    }

    public double lengthOfLastPath() {

	double length = 0;
	for (int i = 0; i < lastPath.size(); i++) {

	    length = length + mapA[lastPath.get(i).x][lastPath.get(i).y].getGroundFactor();

	}
	return length;

    }

    public static void main(String args[]) {

	MapGenerator mg = new MapGenerator();
	mg.generateAll();
	PathFinder p = new PathFinder(mg.getMap());
	List<Point> list = p.findPath(2, 2, 7, 11);
	for (int i = 0; i < list.size(); i++) {

	    System.out.println(list.get(i));

	}

    }

}
