package de.gymdon.inf1315.game;

public class Barracks extends Building {

    public Barracks(Player owner, int x, int y) {
	this.owner = owner;
	this.x = x;
	this.y = y;
	this.hp = 10000;
	this.defense = 80;
	this.cost = 300;
    }

    @Override
    public void occupy(Player p) {

    }
}
