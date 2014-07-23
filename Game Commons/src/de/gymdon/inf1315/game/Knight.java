package de.gymdon.inf1315.game;

public class Knight extends Unit {

    public Knight(Player owner, int x, int y) {
	this.owner = owner;
	this.x = x;
	this.y = y;
	speed = 7;
	range = 1;
	attack = 70;
	defense = 50;
	hp = 100;
	cost = 200;
	combined = 0.25;
	super.reset();
    }
}
