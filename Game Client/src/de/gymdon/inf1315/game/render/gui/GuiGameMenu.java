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
    public int spacing = guiWidth / 10;
    private Font font = Client.instance.translation.font.deriveFont(75F);
    private boolean[] opt = Client.instance.game.options;
    private String[] act = new String[] {"", "", "", "", "", "", ""};

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
	BufferedImage image = new BufferedImage(guiWidth, guiWidth, BufferedImage.TYPE_INT_ARGB);
	Graphics2D g2d = image.createGraphics();
	g2d.setColor(new Color(object.owner.color == Player.Color.RED ? 0x6C4824 : 0x6C4824));
	g2d.fillRoundRect(0, 0, guiWidth, guiHeight, 10, 10);
	g2d.setColor(new Color(object.owner.color == Player.Color.RED ? 0x7B5C3D : 0x7B5C3D));
	g2d.fillRoundRect(5, 5, 490, 490, 5, 5);
	g2d.translate(20, 20);
	g2d.setColor(new Color(0xFFFFFF));
	g2d.setFont(font);
	int c = 0;
	for(int i = 0; i < opt.length; i++)
	{
	    if(opt[i])
	    {
		g2d.drawString(Client.instance.translation.translate("game.option." + act[i], new Object[0]), 0, font.getSize() - 20 + (spacing + font.getSize() - 20) * c);
		c++;
	    }
	}
	return image;
    }

    private BufferedImage renderUnit() {
	BufferedImage image = new BufferedImage(guiWidth, guiWidth, BufferedImage.TYPE_INT_ARGB);
	Graphics2D g2d = image.createGraphics();
	g2d.setColor(new Color(0x6C4824));
	g2d.fillRoundRect(0, 0, guiWidth, guiHeight, 10, 10);
	g2d.setColor(new Color(0x7B5C3D));
	g2d.fillRoundRect(5, 5, 490, 490, 5, 5);
	g2d.translate(20, 20);
	g2d.setColor(new Color(0xFFFFFF));
	g2d.setFont(font);
	int c = 0;
	for(int i = 0; i < opt.length; i++)
	{
	    if(opt[i])
	    {
		g2d.drawString(Client.instance.translation.translate("game.option." + act[i], new Object[0]), 0, font.getSize() - 20 + (spacing + font.getSize() - 20) * c);
		c++;
	    }
	}
	return image;
    }

    @Override
    public void render(Graphics2D g2d, int width, int height) {
	g2d.drawImage(render(), 0, 0, null);
    }
}
