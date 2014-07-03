package de.gymdon.inf1315.game;

public abstract class Building extends GameObject {
    int hp, defense, cost;

    public abstract void occupy(Player p);

    public int getSizeX() {
	return 1;
    }

    public int getSizeY() {
	return 1;
    }

}
