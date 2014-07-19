package de.gymdon.inf1315.game;

public class Miner extends Unit {

    public Miner(Player owner, int x, int y) {
	this.owner = owner;
	this.x = x;
	this.y = y;
	speed = 6;
	range = 4;
	attack = 10;
	defense = 5;
	hp = 100;
	cost = 20;
	combined = 0.01;
	super.resetSpeed();
    }
}
