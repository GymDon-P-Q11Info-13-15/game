package de.gymdon.inf1315.game.render.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.gymdon.inf1315.game.*;

public class GuiGameMenu extends Gui {
    public GameObject object;

    public GuiGameMenu(GameObject go) {
	this.object = go;
    }

    public BufferedImage render() {
	// Buildings
	if (object instanceof Building) {
	    if (object instanceof Castle)
		return renderCastle();
	}

	// Units
	if (object instanceof Unit)
	    return renderUnit();
	return null;
    }

    private BufferedImage renderCastle() {
	BufferedImage image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
	Graphics2D g2d = image.createGraphics();
	g2d.setColor(new Color(object.owner.color == Player.Color.RED ? 0x6C4824 : 0x6C4824));
	g2d.fillRoundRect(0, 0, 500, 500, 10, 10);
	g2d.setColor(new Color(object.owner.color == Player.Color.RED ? 0x7B5C3D : 0x7B5C3D));
	g2d.fillRoundRect(5, 5, 490, 490, 5, 5);
	return image;
    }

    private BufferedImage renderUnit() {
	BufferedImage image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
	Graphics2D g2d = image.createGraphics();
	g2d.setColor(new Color(object.owner.color == Player.Color.RED ? 0x6C4824 : 0x6C4824));
	g2d.fillRoundRect(0, 0, 500, 500, 10, 10);
	g2d.setColor(new Color(object.owner.color == Player.Color.RED ? 0x7B5C3D : 0x7B5C3D));
	g2d.fillRoundRect(5, 5, 490, 490, 5, 5);
	return image;
    }

    @Override
    public void render(Graphics2D g2d, int width, int height) {
	g2d.drawImage(render(), 0, 0, null);
    }
}
