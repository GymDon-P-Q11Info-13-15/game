package de.gymdon.inf1315.game.render.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.gymdon.inf1315.game.Building;
import de.gymdon.inf1315.game.Castle;
import de.gymdon.inf1315.game.Player;

public class GuiBuildingMenu extends Gui {
    public Building building;
    
    public GuiBuildingMenu(Building building) {
	this.building = building;
    }
    
    public BufferedImage render() {
	if(building instanceof Castle)
	    return renderCastle();
	return null;
    }
    
    private BufferedImage renderCastle() {
	BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
	Graphics2D g2d = image.createGraphics();
	g2d.setColor(building.owner.color == Player.Color.RED ? Color.RED : Color.BLUE);
	g2d.fillOval(0, 0, 100, 100);
	return image;
    }

    @Override
    public void render(Graphics2D g2d, int width, int height) {
	g2d.drawImage(render(), 0, 0, null);
    }
}
