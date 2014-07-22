package de.gymdon.inf1315.game;

public class Castle extends Building {

    public Castle(Player owner, int x, int y) {
	this.owner = owner;
	this.x = x;
	this.y = y;
	this.hp = 10000;
	this.defense = 80;
	this.cost = 0;
	this.income = 100;
    }

    @Override
    public void occupy(Player p) {

    }

    @Override
    public int getSizeX() {
	return 2;
    }

    @Override
    public int getSizeY() {
	return 2;
    }
}
