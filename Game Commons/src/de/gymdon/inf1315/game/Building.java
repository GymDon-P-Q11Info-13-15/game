package de.gymdon.inf1315.game;

public abstract class Building extends GameObject {
    boolean[] options = new boolean[] { false, false, false, false, false, true, false };
    public int income;
    
    public abstract void occupy(Player p);
    
    @Override
    public boolean[] clicked(int phase) {
	if (phase != 0) {
	    options[5] = false;
	} else {
	    options[5] = true;
	}

	return options;
    }
    
    public int getIncome()
    {
	return income;
    }
}
