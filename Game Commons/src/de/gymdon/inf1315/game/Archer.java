package de.gymdon.inf1315.game;

public class Archer extends Unit {

    public Archer(Player owner, int x, int y) {
	this.owner = owner;
	this.x = x;
	this.y = y;
	speed = 8;
	range = 4;
	attack = 20;
	defense = 20;
	hp = 100;
	cost = 60;
	combined = 0.2;
	super.resetSpeed();
    }
}
