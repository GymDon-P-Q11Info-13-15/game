package de.gymdon.inf1315.game;

import de.gymdon.inf1315.game.Player.PColor;
import de.gymdon.inf1315.game.packet.Remote;
import de.gymdon.inf1315.game.tile.Tile;
import de.gymdon.inf1315.game.tile.TileMap;

public class Game {
    public MapGenerator mapgen;
    public TileMap map;
    public Building[][] buildings;
    public Unit[][] units;
    public GameMechanics gm;
    public boolean[] options;
    public int phase = 0;
    public int round = 0;
    public Player player1;
    public Player player2;
    public Player activePlayer;
    public int GoldDif = 0;
    private Remote clientA;
    private Remote clientB;

    public Game(Remote clientA) {
        this.clientA = clientA;
        options = new boolean[7];
        player1 = new Player();
        player1.setColor(PColor.RED);
        player2 = new Player();
        player2.setColor(PColor.BLUE);
        mapgen = new MapGenerator();
        gm = new GameMechanics();
        gm.game = this;
        units = new Unit[mapgen.getMapWidth()][mapgen.getMapHeight()];
        if (getNumClients() == 0)
            activePlayer = player1;
    }

    public Game(Remote clientA, Remote clientB) {
        this.clientA = clientA;
        this.clientB = clientB;
        options = new boolean[7];
        player1 = new Player();
        player1.setColor(PColor.RED);
        player2 = new Player();
        player2.setColor(PColor.BLUE);
        mapgen = new MapGenerator();
        gm = new GameMechanics();
        gm.game = this;
        units = new Unit[mapgen.getMapWidth()][mapgen.getMapHeight()];
        if (getNumClients() == 0)
            activePlayer = player1;
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