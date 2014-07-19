package de.gymdon.inf1315.game;

public abstract class Unit extends GameObject {
    int speed, attack, range, cost, act_speed;
    double combined;

    boolean[] options = new boolean[] { true, true, true, false, false, false, false };

    public void resetSpeed() {
	act_speed = speed;
    }

    public int getSpeed() {
	return speed;
    }

    public int getRange() {
	return range;
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

	return options;
    }
}
