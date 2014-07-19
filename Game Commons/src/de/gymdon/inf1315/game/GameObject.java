package de.gymdon.inf1315.game;

public abstract class GameObject {
    public int x;
    public int y;
    public int cost;
    public int hp;
    public int defense;
    public Player owner;

    boolean[] options;
    
    public abstract boolean[] clicked(int phase);

    public int getSizeX() {
	return 1;
    }

    public int getSizeY() {
	return 1;
    }
    
    public void setHP(int health) {
	hp = health;
    }
    
    public int getHP()
    {
	return hp;
    }
}
