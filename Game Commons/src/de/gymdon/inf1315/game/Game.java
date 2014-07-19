package de.gymdon.inf1315.game;

import de.gymdon.inf1315.game.Player.Color;
import de.gymdon.inf1315.game.packet.Remote;

public class Game {
    private Remote clientA;
    private Remote clientB;
    public MapGenerator mapgen;
    public Tile[][] map;
    public Building[][] buildings;
    public Unit[][] units;
    public GameMechanics gm;
    public boolean[] options;
    public int phase = 0;
    public int round = 0;
    public Player red;
    public Player blue;

    public Game(Remote clientA) {
	options = new boolean[7];
	red = new Player();
	red.color = Color.RED;
	blue = new Player();
	blue.color = Color.BLUE;
	this.clientA = clientA;
	mapgen = new MapGenerator();
	gm = new GameMechanics();
	gm.game = this;
	units = new Unit[mapgen.getMapWidth()][mapgen.getMapHeight()];
    }

    public Game(Remote clientA, Remote clientB) {
	options = new boolean[7];
	red = new Player();
	red.color = Color.RED;
	blue = new Player();
	blue.color = Color.BLUE;
	this.clientA = clientA;
	this.clientB = clientB;
	mapgen = new MapGenerator();
	gm = new GameMechanics();
	gm.game = this;
	units = new Unit[mapgen.getMapWidth()][mapgen.getMapHeight()];
    }

    public boolean hasBothClients() {
	return clientA != null && clientB != null;
    }

    public Remote getClientA() {
	return clientA;
    }

    public Remote getClientB() {
	return clientA;
    }

    public int getNumClients() {
	return clientA != null ? clientB != null ? 2 : 1 : 0;
    }

    public void end(Remote leaver) {
	if (leaver == clientA)
	    clientA = null;
	if (leaver == clientB)
	    clientB = null;
	if (hasBothClients())
	    return;
	if (leaver.properties.containsKey("translation"))
	    System.out.println(((Translation) leaver.properties.get("translation")).translate("game.ended", this));
    }
}