package de.gymdon.inf1315.game;

public class Mine extends Building {

    int income;
    public boolean superior = true;

    public Mine(int x, int y) {
	this.x = x;
	this.y = y;
	this.hp = 5000;
	defense = 20;
    }

    @Override
    public void occupy(Player p) {
	this.owner = p;
	this.hp = 5000;
    }

    @Override
    public boolean[] clicked(int phase) {
	options[5] = false;

	return options;
    }
}
