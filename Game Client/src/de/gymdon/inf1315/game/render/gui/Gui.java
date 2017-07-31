package de.gymdon.inf1315.game.render.gui;

import de.gymdon.inf1315.game.render.Renderable;

import java.awt.*;

public abstract class Gui implements Renderable {

    @Override
    public abstract void render(Graphics2D g2d, int width, int height);
}
