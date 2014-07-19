package de.gymdon.inf1315.game;

public abstract class GameObject {
    public int x, y, cost;
    public Player owner;

    boolean[] options;

    public int getSizeX() {
	return 1;
    }

    public int getSizeY() {
	return 1;
    }
}
