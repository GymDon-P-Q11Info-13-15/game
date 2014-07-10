package de.gymdon.inf1315.game.render.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.gymdon.inf1315.game.*;
import de.gymdon.inf1315.game.client.Client;

public class GuiGameMenu extends Gui {
    public GameObject object;
    public int guiWidth = 500;
    public int guiHeight = 500;
    public int spacing;
    private Font font = Client.instance.translation.font.deriveFont(75F);
    private boolean[] opt = Client.instance.game.options;

    public GuiGameMenu(GameObject go) {
	this.object = go;
    }

    public BufferedImage render() {
	// Buildings
	if (object instanceof Building) {
	    if (object instanceof Castle)
		return renderCastle(opt);
	}

	// Units
	if (object instanceof Unit)
	    return renderUnit(opt);
	return null;
    }

    private BufferedImage renderCastle(boolean[] opt) {
	BufferedImage image = new BufferedImage(guiWidth, guiWidth, BufferedImage.TYPE_INT_ARGB);
	Graphics2D g2d = image.createGraphics();
	g2d.setColor(new Color(object.owner.color == Player.Color.RED ? 0x6C4824 : 0x6C4824));
	g2d.fillRoundRect(0, 0, guiWidth, guiHeight, 10, 10);
	g2d.setColor(new Color(object.owner.color == Player.Color.RED ? 0x7B5C3D : 0x7B5C3D));
	g2d.fillRoundRect(5, 5, 490, 490, 5, 5);
	g2d.translate(10, 10);
	g2d.setColor(new Color(0x000000));
	g2d.setFont(font);
	//g2d.fillRoundRect(0, 0, 64, 64, 0, 0);
	g2d.drawString("Test", 0, font.getSize() - 20);
	return image;
    }

    private BufferedImage renderUnit(boolean[] opt) {
	BufferedImage image = new BufferedImage(guiWidth, guiWidth, BufferedImage.TYPE_INT_ARGB);
	Graphics2D g2d = image.createGraphics();
	g2d.setColor(new Color(0x6C4824));
	g2d.fillRoundRect(0, 0, guiWidth, guiHeight, 10, 10);
	g2d.setColor(new Color(0x7B5C3D));
	g2d.fillRoundRect(5, 5, 490, 490, 5, 5);
	return image;
    }

    @Override
    public void render(Graphics2D g2d, int width, int height) {
	g2d.drawImage(render(), 0, 0, null);
    }
}
