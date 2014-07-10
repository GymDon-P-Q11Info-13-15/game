package de.gymdon.inf1315.game;

public class Barracks extends Building {
    Player own;

    public Barracks(Player owner, int x, int y) {

	own = owner;
	this.x = x;
	this.y = y;
	this.hp = 10000;
	this.defense = 80;
    }

    public void buildUnit(Unit unit, int number) {

    }

    @Override
    public void occupy(Player p) {

    }

    public boolean[] clicked(int phase) {
	if(phase%3==0){
	    options[5]=true;
	}
	else{
	    options[5]=false;
	}
	
	
	return options;
    }
}
