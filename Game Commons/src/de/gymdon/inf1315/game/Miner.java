package de.gymdon.inf1315.game;

public class Miner extends Unit {

    public Miner(Player owner, int x, int y) {
	this.owner = owner;
	this.x = x;
	this.y = y;
	speed = 6;
	range = 1;
	attack = 10;
	defense = 5;
	hp = 100;
	cost = 20;
	combined = 0.01;
	super.reset();
    }
    
    @Override
    public boolean[] clicked(int phase) {
	if (phase != 2) {
	    options[0] = false;
	} else {
	    options[0] = true;
	}
	if (act_speed == 0 || phase != 1) {
	    options[1] = false;
	} else {
	    options[1] = true;
	}
	if (hp <= 100 || phase != 1) {
	    options[2] = false;
	} else {
	    options[2] = true;
	}
	if (phase != 1) {
	    options[3] = false;
	} else {
	    options[3] = true;
	}

	return options;
    }
}
