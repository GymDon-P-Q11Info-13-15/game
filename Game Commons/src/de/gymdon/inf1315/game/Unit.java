package de.gymdon.inf1315.game;

public abstract class Unit extends GameObject {
    int hp, speed, attack, defense, range, cost, act_speed;
    double combined;

    boolean[] options=new boolean[]{true,true,true,false,false,false,false};
    

    public void move(int x, int y) {
	if (this.x + x < 0)
	    throw new IllegalArgumentException("X - Coordinate must not be negative!");
	this.x = this.x + x;
	if (this.y + y < 0)
	    throw new IllegalArgumentException("Y - Coordinate must not be negative!");
	this.y = this.y + y;
    }

    public abstract void attack();

    public void setHP(int health) {
	hp = health;
    }

    public int getHP() {
	return hp;
    }
    
    public void resetSpeed(){
    	act_speed=speed;
    }

    public abstract int getSpeed();

    
    public boolean[] clicked(int phase){
    	if(act_speed==0||phase!=1){
    		options[1]=false;
    	}
    	else{
    	    options[1]=true;
    	}
    	if(hp<=100||phase!=1){
    		options[2]=false;
    	}
    	else{
    	    options[2]=true;
    	}
    	return options;
    }
    
   /* public boolean[][] attackableFoes(){
	Player attacker = this.owner;
	
	
	
	
    }
*/


}
