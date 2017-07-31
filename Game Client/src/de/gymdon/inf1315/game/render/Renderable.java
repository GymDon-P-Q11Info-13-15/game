package de.gymdon.inf1315.game.render;

import java.awt.*;

@FunctionalInterface
public interface Renderable {
    void render(Graphics2D g2d, int width, int height);
}
