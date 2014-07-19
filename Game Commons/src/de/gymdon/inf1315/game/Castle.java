package de.gymdon.inf1315.game;

public class Castle extends Building {

    public Castle(Player owner, int x, int y) {
	this.owner = owner;
	this.x = x;
	this.y = y;
	this.hp = 10000;
	this.defense = 80;
    }

    @Override
    public void occupy(Player p) {
	// cannot be occupied
	// sure can it be occupied, if occupied the game is over ;)
    }

    @Override
    public int getSizeX() {
	return 2;
    }

    @Override
    public int getSizeY() {
	return 2;
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
