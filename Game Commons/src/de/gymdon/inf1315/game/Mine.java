package de.gymdon.inf1315.game;

public class Mine extends Building {

    private int income;
    public boolean superior = true;

    public Mine(int x, int y) {
	this.x = x;
	this.y = y;
	this.hp = 1;
	this.cost = 0;
	this.defense = 0;
    }

    @Override
    public void occupy(Player p) {
	this.owner = p;
	this.hp = 5000;
	this.defense = 20;
	if(this.superior)
	    this.income = 150;
	else
	    this.income = 50;
    }

    @Override
    public boolean[] clicked(int phase) {
	options[5] = false;

	return options;
    }
    
    public int getIncome()
    {
	return income;
    }
}
