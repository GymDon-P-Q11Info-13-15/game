package de.gymdon.inf1315.game;

public abstract class Self {
    public static Self instance;
    public Preferences preferences;
    public Translation translation;

    public abstract String getName();
}
