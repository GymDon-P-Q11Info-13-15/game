package de.gymdon.inf1315.game.render.gui;

import de.gymdon.inf1315.game.client.Client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

public class GuiMainMenu extends GuiScreen {

    private GuiButton newGame = new GuiButton(this, 0, 20, 20, "gui.game.new");
    private GuiButton options = new GuiButton(this, 1, 20, 20, "gui.options");
    private GuiButton credits = new GuiButton(this, 2, 20, 20, "gui.credits");
    private GuiButton exit = new GuiButton(this, -1, 20, 20, "gui.exit");

    public GuiMainMenu() {
        controlList.add(newGame);
        controlList.add(options);
        controlList.add(credits);
        controlList.add(exit);
    }

    public void rebuild() {
        newGame = new GuiButton(this, 0, 20, 20, "gui.game.new");
        options = new GuiButton(this, 1, 20, 20, "gui.options");
        credits = new GuiButton(this, 2, 20, 20, "gui.credits");
        exit = new GuiButton(this, -1, 20, 20, "gui.exit");
        controlList.clear();
        controlList.add(newGame);
        controlList.add(options);
        controlList.add(credits);
        controlList.add(exit);
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
        newGame.setX(leftMargin);
        newGame.setY(topMargin);
        newGame.setWidth(buttonWidth);
        newGame.setHeight(buttonHeight);

        options.setX(leftMargin);
        options.setY(topMargin + (buttonHeight + buttonSpacing));
        options.setWidth(buttonWidth);
        options.setHeight(buttonHeight);

        credits.setX(leftMargin);
        credits.setY(topMargin + 2 * (buttonHeight + buttonSpacing));
        credits.setWidth(buttonWidth);
        credits.setHeight(buttonHeight);

        exit.setX(width - leftMargin - buttonWidthVerySmall);
        exit.setY(height - buttonSpacing - buttonHeight);
        exit.setWidth(buttonWidthVerySmall);
        exit.setHeight(buttonHeight);
        super.render(g2d, width, height);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getID() == ActionEvent.ACTION_PERFORMED) {
            // Buttons
            if (e.getSource() instanceof GuiButton) {
                GuiButton button = (GuiButton) e.getSource();
                if (button == newGame)
                    Client.instance.setGuiScreen(new GuiNewGameMenu(this));
                else if (button == options)
                    Client.instance.setGuiScreen(new GuiOptions(this));
                else if (button == credits)
                    Client.instance.setGuiScreen(new GuiCredits(this));
                else if (button == exit)
                    Client.instance.stop();
            }

            // Keys
            if (e.getSource() instanceof KeyEvent) {
                int key = ((KeyEvent) e.getSource()).getKeyCode();
                if (key == KeyEvent.VK_ESCAPE)
                    ;
            }
        }
    }
}
