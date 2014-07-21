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
import de.gymdon.inf1315.game.render.BuildingRenderMap;
import de.gymdon.inf1315.game.render.StandardTexture;
import de.gymdon.inf1315.game.render.Texture;
import de.gymdon.inf1315.game.render.UnitRenderMap;

public class GuiGameMenu extends Gui implements MouseInputListener {
    public GameObject object;
    public int guiWidth = 500;
    public int guiHeight = 500;
    public int spacing = guiWidth / 10;
    private Font font = Client.instance.translation.font.deriveFont(75F);
    private boolean[] opt = Client.instance.game.options;
    private String[] act = new String[] { "attack", "move", "stack", "build", "", "spawn", "upgrade" };
    private AffineTransform affinetransform = new AffineTransform();
    private FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
    private int[] actionWidth = new int[7];
    private int[] actionHeight = new int[7];
    private boolean[] actionHover = new boolean[7];
    private int hoverXExtra = 4;
    private int hoverYExtra = 12;
    private int hoverSize = 3;
    private int hoverDifY = 12;
    private boolean newMenu = false;
    private int tileSize = Client.instance.mapren.tileSize;
    Class<?>[] minerClasses = new Class<?>[] { Barracks.class };
    Class<?>[] castleClasses = new Class<?>[] { Spearman.class, Swordsman.class, Miner.class };
    Class<?>[] classes = new Class<?>[] { Archer.class, Knight.class, Spearman.class, Swordsman.class, Miner.class };
    private boolean[] newMenuHover = new boolean[classes.length];
    private String newMenuCommand;

    public GuiGameMenu(GameObject go) {
	this.object = go;
    }

    public BufferedImage render() {
	// Buildings
	if (object instanceof Building) {
	    if (object instanceof Castle)
		return renderCastle();
	    if (object instanceof Barracks)
		return renderBarracks();
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
	g2d.setColor(new Color(0x6C4824));
	g2d.fillRoundRect(0, 0, guiWidth, guiHeight, 10, 10);
	g2d.setColor(new Color(0x7B5C3D));
	g2d.fillRoundRect(5, 5, 490, 490, 5, 5);
	g2d.translate(20, 20);
	g2d.setColor(new Color(0xFFFFFF));
	g2d.setFont(font);
	int c = 0;
	if (!newMenu) {
	    for (int i = 0; i < opt.length; i++) {
		if (opt[i]) {
		    g2d.drawString(Client.instance.translation.translate("game.option." + act[i], new Object[0]), 0, actionHeight[i] + (spacing + actionHeight[i]) * c);
		    if (actionHover[i]) {
			g2d.fillRect(-hoverXExtra, (spacing + actionHeight[i]) * c - hoverYExtra + hoverDifY, hoverSize, actionHeight[i] + hoverYExtra * 2);
			g2d.fillRect(-hoverXExtra, (spacing + actionHeight[i]) * c - hoverYExtra + hoverDifY, actionWidth[i] + hoverXExtra * 2, hoverSize);
			g2d.fillRect(actionWidth[i] + hoverXExtra, (spacing + actionHeight[i]) * c - hoverYExtra + hoverDifY, hoverSize, actionHeight[i] + hoverYExtra * 2);
			g2d.fillRect(-hoverXExtra, actionHeight[i] + (spacing + actionHeight[i]) * c + hoverYExtra + hoverDifY, actionWidth[i] + hoverXExtra * 2, hoverSize);
		    }
		    c++;
		}
	    }
	} else {
	    newMenuCommand = "castle";
	    for (int i = 0; i < castleClasses.length; i++) {
		@SuppressWarnings("unchecked")
		Class<? extends Unit> clazz = (Class<? extends Unit>) castleClasses[i];
		try {
		    Unit u = clazz.getConstructor(Player.class, Integer.TYPE, Integer.TYPE).newInstance(null, 0, 0);
		    Texture tex = UnitRenderMap.getTexture(u);
		    g2d.drawImage(tex.getImage(), (tileSize + spacing) * (i % 3), (tileSize + spacing) * (i / 3), tileSize, tileSize, tex);

		    Font f = Client.instance.translation.font.deriveFont(20F);
		    int cWidth = (int) (f.getStringBounds("" + u.getCost(), frc).getWidth());
		    int cHeight = (int) (f.getStringBounds("" + u.getCost(), frc).getHeight());
		    g2d.setFont(f);
		    g2d.setColor(new Color(0xEDE275));
		    g2d.drawString("" + u.getCost(), (tileSize + spacing) * (i % 3) + tileSize / 2 - cWidth / 2, (tileSize + spacing) * (i / 3) + tileSize + cHeight);
		} catch (Exception e1) {
		    e1.printStackTrace();
		}
		if (newMenuHover[i]) {
		    Texture hTex = StandardTexture.get("hover");
		    g2d.drawImage(hTex.getImage(), (tileSize + spacing) * (i % 3), (tileSize + spacing) * (i / 3), tileSize, tileSize, hTex);
		}
	    }
	}
	return image;
    }

    private BufferedImage renderBarracks() {
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
	if (!newMenu) {
	    for (int i = 0; i < opt.length; i++) {
		if (opt[i]) {
		    g2d.drawString(Client.instance.translation.translate("game.option." + act[i], new Object[0]), 0, actionHeight[i] + (spacing + actionHeight[i]) * c);
		    if (actionHover[i]) {
			g2d.fillRect(-hoverXExtra, (spacing + actionHeight[i]) * c - hoverYExtra + hoverDifY, hoverSize, actionHeight[i] + hoverYExtra * 2);
			g2d.fillRect(-hoverXExtra, (spacing + actionHeight[i]) * c - hoverYExtra + hoverDifY, actionWidth[i] + hoverXExtra * 2, hoverSize);
			g2d.fillRect(actionWidth[i] + hoverXExtra, (spacing + actionHeight[i]) * c - hoverYExtra + hoverDifY, hoverSize, actionHeight[i] + hoverYExtra * 2);
			g2d.fillRect(-hoverXExtra, actionHeight[i] + (spacing + actionHeight[i]) * c + hoverYExtra + hoverDifY, actionWidth[i] + hoverXExtra * 2, hoverSize);
		    }
		    c++;
		}
	    }
	} else {
	    newMenuCommand = "all";
	    for (int i = 0; i < classes.length; i++) {
		@SuppressWarnings("unchecked")
		Class<? extends Unit> clazz = (Class<? extends Unit>) classes[i];
		try {
		    Unit u = clazz.getConstructor(Player.class, Integer.TYPE, Integer.TYPE).newInstance(null, 0, 0);
		    Texture tex = UnitRenderMap.getTexture(u);
		    g2d.drawImage(tex.getImage(), (tileSize + spacing) * (i % 3), (tileSize + spacing) * (i / 3), tileSize, tileSize, tex);
		    Font f = Client.instance.translation.font.deriveFont(20F);
		    int cWidth = (int) (f.getStringBounds("" + u.getCost(), frc).getWidth());
		    int cHeight = (int) (f.getStringBounds("" + u.getCost(), frc).getHeight());
		    g2d.setFont(f);
		    g2d.setColor(new Color(0xEDE275));
		    g2d.drawString("" + u.getCost(), (tileSize + spacing) * (i % 3) + tileSize / 2 - cWidth / 2, (tileSize + spacing) * (i / 3) + tileSize + cHeight);
		} catch (Exception e1) {
		    e1.printStackTrace();
		}
		if (newMenuHover[i]) {
		    Texture hTex = StandardTexture.get("hover");
		    g2d.drawImage(hTex.getImage(), (tileSize + spacing) * (i % 3), (tileSize + spacing) * (i / 3), tileSize, tileSize, hTex);
		}
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
	if (!newMenu) {
	    for (int i = 0; i < opt.length; i++) {
		if (opt[i]) {
		    g2d.drawString(Client.instance.translation.translate("game.option." + act[i], new Object[0]), 0, actionHeight[i] + (spacing + actionHeight[i]) * c);
		    if (actionHover[i]) {
			g2d.fillRect(-hoverXExtra, (spacing + actionHeight[i]) * c - hoverYExtra + hoverDifY, hoverSize, actionHeight[i] + hoverYExtra * 2);
			g2d.fillRect(-hoverXExtra, (spacing + actionHeight[i]) * c - hoverYExtra + hoverDifY, actionWidth[i] + hoverXExtra * 2, hoverSize);
			g2d.fillRect(actionWidth[i] + hoverXExtra, (spacing + actionHeight[i]) * c - hoverYExtra + hoverDifY, hoverSize, actionHeight[i] + hoverYExtra * 2);
			g2d.fillRect(-hoverXExtra, actionHeight[i] + (spacing + actionHeight[i]) * c + hoverYExtra + hoverDifY, actionWidth[i] + hoverXExtra * 2, hoverSize);
		    }
		    c++;
		}
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
	if (!newMenu) {
	    for (int i = 0; i < opt.length; i++) {
		if (opt[i]) {
		    g2d.drawString(Client.instance.translation.translate("game.option." + act[i], new Object[0]), 0, actionHeight[i] + (spacing + actionHeight[i]) * c);
		    if (actionHover[i]) {
			g2d.fillRect(-hoverXExtra, (spacing + actionHeight[i]) * c - hoverYExtra + hoverDifY, hoverSize, actionHeight[i] + hoverYExtra * 2);
			g2d.fillRect(-hoverXExtra, (spacing + actionHeight[i]) * c - hoverYExtra + hoverDifY, actionWidth[i] + hoverXExtra * 2, hoverSize);
			g2d.fillRect(actionWidth[i] + hoverXExtra, (spacing + actionHeight[i]) * c - hoverYExtra + hoverDifY, hoverSize, actionHeight[i] + hoverYExtra * 2);
			g2d.fillRect(-hoverXExtra, actionHeight[i] + (spacing + actionHeight[i]) * c + hoverYExtra + hoverDifY, actionWidth[i] + hoverXExtra * 2, hoverSize);
		    }
		    c++;
		}
	    }
	} else {
	    newMenuCommand = "miner";
	    for (int i = 0; i < minerClasses.length; i++) {
		@SuppressWarnings("unchecked")
		Class<? extends Building> clazz = (Class<? extends Building>) minerClasses[i];
		try {
		    Building b = clazz.getConstructor(Player.class, Integer.TYPE, Integer.TYPE).newInstance(null, 0, 0);
		    Texture tex = BuildingRenderMap.getTexture(b);
		    g2d.drawImage(tex.getImage(), (tileSize + spacing) * (i % 3), (tileSize + spacing) * (i / 3), tileSize, tileSize, tex);
		    Font f = Client.instance.translation.font.deriveFont(20F);
		    int cWidth = (int) (f.getStringBounds("" + b.getCost(), frc).getWidth());
		    int cHeight = (int) (f.getStringBounds("" + b.getCost(), frc).getHeight());
		    g2d.setFont(f);
		    g2d.setColor(new Color(0xEDE275));
		    g2d.drawString("" + b.getCost(), (tileSize + spacing) * (i % 3) + tileSize / 2 - cWidth / 2, (tileSize + spacing) * (i / 3) + tileSize + cHeight);
		} catch (Exception e1) {
		    e1.printStackTrace();
		}
		if (newMenuHover[i]) {
		    Texture hTex = StandardTexture.get("hover");
		    g2d.drawImage(hTex.getImage(), (tileSize + spacing) * (i % 3), (tileSize + spacing) * (i / 3), tileSize, tileSize, hTex);
		}
	    }
	}
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
	    if (!newMenu) {
		for (int i = 0; i < opt.length; i++) {
		    if (opt[i]) {
			if (x >= 0 && x <= actionWidth[i] && y >= (actionHeight[i] + spacing) * c && y <= actionHeight[i] + (spacing + actionHeight[i]) * c + hoverDifY + hoverYExtra) {
			    if (act[i] == "attack" && object instanceof Unit)
				Client.instance.mapren.attack = true;
			    if (act[i] == "move" && object instanceof Unit)
				Client.instance.mapren.move = true;
			    if (act[i] == "stack" && object instanceof Unit)
				Client.instance.mapren.stack = true;
			    if (act[i] == "build" && object instanceof Miner) {
				newMenu = true;
			    }
			    if (act[i] == "spawn" && object instanceof Building) {
				newMenu = true;
			    }
			    if (act[i] == "upgrade" && object instanceof Building)
				Client.instance.mapren.upgrade = true;
			}
			c++;
		    }
		}
	    } else {
		Class<?>[] clas = newMenuCommand == "all" ? classes : newMenuCommand == "castle" ? castleClasses : newMenuCommand == "miner" ? minerClasses : null;
		for (int i = 0; i < clas.length; i++) {
		    if (newMenuCommand != "miner") {
			@SuppressWarnings("unchecked")
			Class<? extends Unit> clazz = (Class<? extends Unit>) clas[i];
			if (x >= (tileSize + spacing) * (i % 3) && x <= tileSize + (tileSize + spacing) * (i % 3) && y >= (tileSize + spacing) * (i / 3) && y <= tileSize + (tileSize + spacing) * (i / 3)) {
			    Client.instance.mapren.spawnClass = clazz;
			    Client.instance.mapren.spawn = true;
			}
		    }
		    else
		    {
			@SuppressWarnings("unchecked")
			Class<? extends Building> clazz = (Class<? extends Building>) clas[i];
			if (x >= (tileSize + spacing) * (i % 3) && x <= tileSize + (tileSize + spacing) * (i % 3) && y >= (tileSize + spacing) * (i / 3) && y <= tileSize + (tileSize + spacing) * (i / 3)) {
			    Client.instance.mapren.buildClass = clazz;
			    Client.instance.mapren.build = true;
			}
		    }
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
	if (!newMenu) {
	    for (int i = 0; i < opt.length; i++) {
		if (opt[i]) {
		    if (x >= 0 && x <= actionWidth[i] && y >= (actionHeight[i] + spacing) * c && y <= actionHeight[i] + (spacing + actionHeight[i]) * c + hoverDifY + hoverYExtra)
			actionHover[i] = true;
		    else
			actionHover[i] = false;
		    c++;
		}
	    }
	} else {
	    Class<?>[] clas = newMenuCommand == "all" ? classes : newMenuCommand == "castle" ? castleClasses : newMenuCommand == "miner" ? minerClasses : null;
	    newMenuHover = new boolean[clas.length];
	    for (int i = 0; i < clas.length; i++) {
		if (x >= (tileSize + spacing) * (i % 3) && x <= tileSize + (tileSize + spacing) * (i % 3) && y >= (tileSize + spacing) * (i / 3) && y <= tileSize + (tileSize + spacing) * (i / 3))
		    newMenuHover[i] = true;
	    }
	}
    }
}
