package de.gymdon.inf1315.game;

public class Archer extends Unit {

    public Archer(Player owner, int x, int y) {
	this.owner = owner;
	this.x = x;
	this.y = y;
	this.speed = 8;
	this.range = 4;
	this.attack = 20;
	this.defense = 20;
	this.hp = 100;
	this.cost = 60;
	this.combined = 0.2;
	super.reset();
    }
}
