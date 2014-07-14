package de.gymdon.inf1315.game.render.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.event.MouseInputListener;

import de.gymdon.inf1315.game.*;
import de.gymdon.inf1315.game.client.Client;

public class GuiGameMenu extends Gui implements MouseInputListener {
    public GameObject object;
    public int guiWidth = 500;
    public int guiHeight = 500;
    public int spacing = guiWidth / 10;
    private Font font = Client.instance.translation.font.deriveFont(75F);
    private boolean[] opt = Client.instance.game.options;
    private String[] act = new String[] { "attack", "move", "stack", "", "", "spawn", "upgrade" };
    private AffineTransform affinetransform = new AffineTransform();
    private FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
    private int[] actionWidth = new int[7];
    private int[] actionHeight = new int[7];
    private boolean[] actionHover = new boolean[7];

    public GuiGameMenu(GameObject go) {
	this.object = go;
    }

    public BufferedImage render() {
	// Buildings
	if (object instanceof Building) {
	    if (object instanceof Castle)
		return renderCastle();
	    if (object instanceof Mine)
		return renderMine();
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
	for (int i = 0; i < opt.length; i++) {
	    if (opt[i]) {
		g2d.drawString(act[i] != "" ? Client.instance.translation.translate("game.option." + act[i], new Object[0]) : "", 0, actionHeight[i] + (spacing + actionHeight[i]) * c);
		if (actionHover[i]) {
		    g2d.fillRect(-4, (spacing + actionHeight[i]) * c + 12, 3, actionHeight[i]);
		    g2d.fillRect(-4, (spacing + actionHeight[i]) * c + 12, actionWidth[i] + 8, 3);
		    g2d.fillRect(actionWidth[i] + 4, (spacing + actionHeight[i]) * c + 12, 3, actionHeight[i]);
		    g2d.fillRect(-4, actionHeight[i] + (spacing + actionHeight[i]) * c + 12, actionWidth[i] + 8, 3);
		}
		c++;
	    }
	}
	return image;
    }

    private BufferedImage renderMine() {
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
	for (int i = 0; i < opt.length; i++) {
	    if (opt[i]) {
		g2d.drawString(act[i] != "" ? Client.instance.translation.translate("game.option." + act[i], new Object[0]) : "", 0, actionHeight[i] + (spacing + actionHeight[i]) * c);
		if (actionHover[i]) {
		    g2d.fillRect(-4, (spacing + actionHeight[i]) * c + 12, 3, actionHeight[i]);
		    g2d.fillRect(-4, (spacing + actionHeight[i]) * c + 12, actionWidth[i] + 8, 3);
		    g2d.fillRect(actionWidth[i] + 4, (spacing + actionHeight[i]) * c + 12, 3, actionHeight[i]);
		    g2d.fillRect(-4, actionHeight[i] + (spacing + actionHeight[i]) * c + 12, actionWidth[i] + 8, 3);
		}
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
	for (int i = 0; i < opt.length; i++) {
	    if (opt[i]) {
		g2d.drawString(act[i] != "" ? Client.instance.translation.translate("game.option." + act[i], new Object[0]) : "", 0, actionHeight[i] + (spacing + actionHeight[i]) * c);
		if (actionHover[i]) {
		    g2d.fillRect(-4, (spacing + actionHeight[i]) * c + 12, 3, actionHeight[i]);
		    g2d.fillRect(-4, (spacing + actionHeight[i]) * c + 12, actionWidth[i] + 8, 3);
		    g2d.fillRect(actionWidth[i] + 4, (spacing + actionHeight[i]) * c + 12, 3, actionHeight[i]);
		    g2d.fillRect(-4, actionHeight[i] + (spacing + actionHeight[i]) * c + 12, actionWidth[i] + 8, 3);
		}
		c++;
	    }
	}
	if(Client.instance.mapren.activeAction)
	    return null;
	else
	    return image;
    }

    @Override
    public void render(Graphics2D g2d, int width, int height) {
	g2d.drawImage(render(), 0, 0, null);
    }

    public void keepSizesUpToDate() {
	for (int i = 0; i < opt.length; i++) {
	    String text = act[i] != "" ? Client.instance.translation.translate("game.option." + act[i], new Object[0]) : "";
	    actionWidth[i] = (int) (font.getStringBounds(text, frc).getWidth());
	    actionHeight[i] = (int) (font.getStringBounds(text, frc).getHeight()) - 21;
	}
    }

    @Override
    public void mouseClicked(MouseEvent e) {
	if (e.getButton() == MouseEvent.BUTTON1) {
	    int x = e.getX() - 20;
	    int y = e.getY() - 20;

	    int c = 0;
	    for (int i = 0; i < opt.length; i++) {
		if (opt[i]) {
		    if (x >= 0 && x <= actionWidth[i] && y >= (actionHeight[i] + spacing) * c && y <= actionHeight[i] + (spacing + actionHeight[i]) * c) {
			System.out.println(act[i] != "" ? Client.instance.translation.translate("game.option." + act[i], new Object[0]) : "");
			if (act[i] == "attack") {
			    Client.instance.mapren.attack = true;
			}
			if (act[i] == "move") {
			    Client.instance.mapren.move = true;
			}
			if (act[i] == "stack") {
			    Client.instance.mapren.stack = true;
			}
			if (act[i] == "spawn") {
			    Client.instance.mapren.spawn = true;
			}
			if (act[i] == "upgrade") {
			}
		    }
		    c++;
		}
	    }
	}
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
	int x = e.getX() - 20;
	int y = e.getY() - 20;

	int c = 0;
	for (int i = 0; i < opt.length; i++) {
	    if (opt[i]) {
		if (x >= 0 && x <= actionWidth[i] && y >= (actionHeight[i] + spacing) * c && y <= actionHeight[i] + (spacing + actionHeight[i]) * c)
		    actionHover[i] = true;
		else
		    actionHover[i] = false;
		c++;
	    }
	}
    }
}
