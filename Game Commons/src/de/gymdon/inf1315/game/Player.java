package de.gymdon.inf1315.game;

import java.awt.Color;

public class Player {
    public int gold = 500;
    public PColor color;
    
    public enum PColor {
	RED (Color.RED), BLUE (Color.BLUE);
	
	private Color col;
	
	private PColor(Color c)
	{
	    col = c;
	}
	
	public Color getColor()
	{
	    return col;
	}
    }
}
