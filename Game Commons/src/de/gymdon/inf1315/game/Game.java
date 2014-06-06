package de.gymdon.inf1315.game;

import de.gymdon.inf1315.game.packet.Remote;

public class Game {
    private Remote clientA;
    private Remote clientB;

    public Game(Remote clientA) {
	this.clientA = clientA;
    }

    public Game(Remote clientA, Remote clientB) {
	this.clientA = clientA;
	this.clientB = clientB;
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
	    System.out.println(((Translation) leaver.properties
		    .get("translation")).translate("game.ended", this));
    }
}