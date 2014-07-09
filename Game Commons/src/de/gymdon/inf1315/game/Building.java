package de.gymdon.inf1315.game;

public abstract class Building extends GameObject {
    int hp, defense, cost;
    boolean[] options = new boolean[] { false, false, false, false, false, true, true };

    public abstract void occupy(Player p);

    public int getSizeX() {
	return 1;
    }

    public int getSizeY() {
	return 1;
    }

    
    
    public boolean[] clicked(int phase){
	if(phase%3==0){
	    options[5]=true;
	}
	else{
	    options[5]=false;
	}
	
	
	return options;
    }
}
