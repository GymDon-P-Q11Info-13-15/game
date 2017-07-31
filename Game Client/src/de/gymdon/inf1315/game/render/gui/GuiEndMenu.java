package de.gymdon.inf1315.game.render.gui;

import de.gymdon.inf1315.game.client.Client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

public class GuiEndMenu extends GuiScreen {

    private GuiButton backButton = new GuiButton(this, -1, 20, 20, "gui.back");

    public GuiEndMenu() {
        controlList.add(backButton);
    }

    @Override
    public void render(Graphics2D g2d, int width, int height) {

        Client.instance.mapren.render(g2d, width, height);
        g2d.drawImage(Client.instance.mapren.getMapBackground(), 0, 0, null);
        String t = Client.instance.translation.translate("game.title");
        Font f = Client.instance.translation.font.deriveFont(Font.BOLD, 120F);
        g2d.setFont(f);
        Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(t, g2d);
        int titleX = (int) (width / 2 - bounds.getCenterX());
        int titleY = (int) (height / 3 - 50 + bounds.getCenterY());
        GlyphVector gv = f.createGlyphVector(g2d.getFontRenderContext(), t);
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

        t = Client.instance.translation.translate("game.ended", Client.instance.translation.translate("game.player") + " " + (Client.instance.game.activePlayer == Client.instance.game.player1 ? 1 : 2));
        f = Client.instance.translation.font.deriveFont(Font.BOLD, 50F);
        g2d.setFont(f);
        bounds = g2d.getFontMetrics().getStringBounds(t, g2d);
        titleX = (int) (width / 2 - bounds.getCenterX());
        titleY = (int) (height / 2 - bounds.getCenterY());
        gv = f.createGlyphVector(g2d.getFontRenderContext(), t);
        outline = gv.getOutline();
        g2d.translate(titleX, titleY);
        {
            g2d.setColor(Client.instance.game.activePlayer.getColor().getColor());
            g2d.setStroke(new BasicStroke(6F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
            g2d.draw(outline);
            g2d.setColor(new Color(0xc69c6d));
            g2d.setStroke(new BasicStroke(1));
            g2d.fill(outline);
        }
        g2d.translate(-titleX, -titleY);

        int botMargin = height / 8;
        int buttonWidth = width - width / 4;
        int buttonHeight = height / 10;
        int leftMargin = width / 2 - buttonWidth / 2;

        backButton.setX(leftMargin);
        backButton.setY(height - botMargin - buttonHeight);
        backButton.setWidth(buttonWidth);
        backButton.setHeight(buttonHeight);
        super.render(g2d, width, height);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getID() == ActionEvent.ACTION_PERFORMED) {
            // Buttons
            if (e.getSource() instanceof GuiButton) {
                GuiButton button = (GuiButton) e.getSource();
                if (button == backButton)
                    Client.instance.setGuiScreen(new GuiCredits(new GuiMainMenu()));
            }

            // Keys
            if (e.getSource() instanceof KeyEvent) {
                int key = ((KeyEvent) e.getSource()).getKeyCode();
                if (key == KeyEvent.VK_ESCAPE) {
                    actionPerformed(new ActionEvent(backButton, ActionEvent.ACTION_PERFORMED, null));
                    Client.instance.mapren.firstClick = false;
                }
            }
        }
    }
}
