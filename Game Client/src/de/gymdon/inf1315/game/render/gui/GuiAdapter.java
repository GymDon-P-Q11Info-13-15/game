package de.gymdon.inf1315.game.render.gui;

public interface GuiAdapter {
    int getHeight(int index, GuiScrollList parent);

    int getWidth(int index, GuiScrollList parent);

    Gui get(int index, GuiScrollList parent);

    int getLength(GuiScrollList parent);
}
