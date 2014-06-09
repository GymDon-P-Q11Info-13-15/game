package de.gymdon.inf1315.game;

public class Tile implements Comparable<Tile>{
    public double groundFactor;
    private boolean walkable;
    public double g = 1;
    public double h;
    public double f;
    public int x;
    public int y;
    public Tile parent;
    public String name;

    public static final Tile grass = new Tile(0, "grass").setGroundFactor(1);
    public static final Tile grass2 = new Tile(0, "grass").setGroundFactor(1);
    public static final Tile sand = new Tile(1, "sand").setGroundFactor(3);
    public static final Tile sand2 = new Tile(1, "sand").setGroundFactor(3);
    public static final Tile water = new Tile(2, "water").setGroundFactor(2);
    public static final Tile water2 = new Tile(2, "water").setGroundFactor(2);
    
    public Tile(int id, String name) {
    	this.name = name;
    }
    
    public void makeF() {
    	
    	f = g + h;
    	
    }
    
    public Tile setGroundFactor(int groundFactor) {
	this.groundFactor = groundFactor;
	g = groundFactor;
	return this;
    }
    
    public double getGroundFactor() {
	return groundFactor;
    }
    
    /* Not needed right now
    private Tile setWalkable(boolean walkable) {
	this.walkable = walkable;
	return this;
    }*/
    
    public boolean isWalkable() {
	return walkable;
    }


	@Override
	public int compareTo(Tile arg0) {
	
		if(this.h < arg0.h) return -1;
		if(this.h > arg0.h) return 1; 
		if(this.h == arg0.h) return 0;
		return -2;
	}
}
