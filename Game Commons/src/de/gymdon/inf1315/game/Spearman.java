package de.gymdon.inf1315.game;

public class Spearman extends Unit {

    public Spearman(Player owner, int x, int y) {
	this.owner = owner;
	this.x = x;
	this.y = y;
	speed = 6;
	range = 1;
	attack = 40;
	defense = 20;
	hp = 100;
	cost = 80;
	combined = 0.1;
	super.resetSpeed();
    }
}
