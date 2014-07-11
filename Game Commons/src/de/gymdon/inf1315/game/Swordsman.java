package de.gymdon.inf1315.game;

public class Swordsman extends Unit {

    public Swordsman(Player owner, int x, int y) {
	this.owner = owner;
	this.x = x;
	this.y = y;
	speed = 5;
	range = -1;
	attack = 40;
	defense = 70;
	hp = 110;
	cost = 130;
	combined = 0.1;
    }

    @Override
    public void move(int x, int y) {
	super.move(x, y);
    }

    @Override
    public void attack() {

    }

    @Override
    public void setHP(int hp) {

	this.hp = hp;

    }

    @Override
    public int getSpeed() {

	return speed;
    }
}
