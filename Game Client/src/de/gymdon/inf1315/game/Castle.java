package de.gymdon.inf1315.game;

public class Castle extends Building {
 Player own;
    
    public Castle(Player owner, int x, int y){
	
	own=owner;
	this.x=x;
	this.y=y;
	this.hp=10000;
	this.defense=80;
	
    }
    
    

    @Override
    public void occupy(Player p) {
	// cannot be occupied
	
    }
    

}