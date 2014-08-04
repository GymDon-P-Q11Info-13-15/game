package de.gymdon.inf1315.game.render.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

import de.gymdon.inf1315.game.client.Client;

public class GuiNewGameMenu extends GuiScreen {

    private GuiScreen last;
    private GuiButton backButton = new GuiButton(this, 0, 300, 550, "gui.back");
    private GuiButton onlineGame = new GuiButton(this, 0, 20, 20, "gui.game.online");
    private GuiButton offlineGame = new GuiButton(this, 2, 20, 20, "gui.game.offline");
    private GuiButton tutorial = new GuiButton(this, 2, 20, 20, "gui.game.tutorial");

    public GuiNewGameMenu(GuiScreen last) {
	this.last = last;
	controlList.add(backButton);
	controlList.add(onlineGame);
	controlList.add(tutorial);
	controlList.add(offlineGame);
	controlList.add(backButton);
    }

    public void rebuild() {
	backButton = new GuiButton(this, 0, 300, 550, "gui.back");
	onlineGame = new GuiButton(this, 0, 20, 20, "gui.game.new");
	offlineGame = new GuiButton(this, 2, 20, 20, "gui.test");
	tutorial = new GuiButton(this, 2, 20, 20, "gui.game.tutorial");
	controlList.clear();
	controlList.add(onlineGame);
	controlList.add(tutorial);
	controlList.add(offlineGame);
	controlList.add(backButton);
    }

    @Override
    public void render(Graphics2D g2d, int width, int height) {
	drawBackground(g2d, width, height);
	// int ticksRunning = Client.instance.getTicksRunning(); //not needed
	// right now. Maybe later?

	Font f = Client.instance.translation.font.deriveFont(Font.BOLD, 120F);
	g2d.setFont(f);
	Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(Client.instance.translation.translate("game.title"), g2d);
	int titleX = (int) (width / 2 - bounds.getCenterX());
	int titleY = (int) (height / 3 - 50 + bounds.getCenterY());
	GlyphVector gv = f.createGlyphVector(g2d.getFontRenderContext(), Client.instance.translation.translate("game.title"));
	Shape outline = gv.getOutline();
	g2d.translate(titleX, titleY);
	{
	    g2d.setColor(new Color(0x6C4824));
	    g2d.setStroke(new BasicStroke(6F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
	    g2d.draw(outline);
	    g2d.setColor(new Color(0xc69c6d));
	    g2d.setStroke(new BasicStroke(1));
	    g2d.fill(outline);
	}
	g2d.translate(-titleX, -titleY);

	int topMargin = height / 3;
	int buttonWidth = width - width / 4;
	int buttonHeight = height / 10;
	int buttonSpacing = buttonHeight / 4;
	int leftMargin = width / 2 - buttonWidth / 2;
	int buttonWidthSmall = (buttonWidth - buttonSpacing) / 2;
	int buttonWidthVerySmall = (buttonWidthSmall - buttonSpacing) / 2;
	onlineGame.setX(leftMargin);
	onlineGame.setY(topMargin);
	onlineGame.setWidth(buttonWidth);
	onlineGame.setHeight(buttonHeight);

	offlineGame.setX(leftMargin);
	offlineGame.setY(topMargin + 1 * (buttonHeight + buttonSpacing));
	offlineGame.setWidth(buttonWidth);
	offlineGame.setHeight(buttonHeight);

	tutorial.setX(leftMargin);
	tutorial.setY(topMargin + 2 * (buttonHeight + buttonSpacing));
	tutorial.setWidth(buttonWidth);
	tutorial.setHeight(buttonHeight);

	backButton.setX(width - leftMargin - buttonWidthVerySmall);
	backButton.setY(height - buttonSpacing - buttonHeight);
	backButton.setWidth(buttonWidthVerySmall);
	backButton.setHeight(buttonHeight);
	super.render(g2d, width, height);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	if (e.getID() == ActionEvent.ACTION_PERFORMED) {
	    // Buttons
	    if (e.getSource() instanceof GuiButton) {
		GuiButton button = (GuiButton) e.getSource();
		if (button == onlineGame)
		    Client.instance.setGuiScreen(new GuiSelectServer(this));
		else if (button == offlineGame)
		    Client.instance.activateMap(true);
		else if (button == tutorial) {
		    Client.instance.activateMap(true);
		    Client.instance.mapren.tutorial();
		} else if (button == backButton)
		    Client.instance.setGuiScreen(last);
	    }

	    // Keys
	    if (e.getSource() instanceof KeyEvent) {
		int key = ((KeyEvent) e.getSource()).getKeyCode();
		if (key == KeyEvent.VK_ESCAPE)
		    actionPerformed(new ActionEvent(backButton, ActionEvent.ACTION_PERFORMED, null));
	    }
	}
    }
}
