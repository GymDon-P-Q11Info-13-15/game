package de.gymdon.inf1315.game;

import de.gymdon.inf1315.game.packet.Remote;
import de.gymdon.inf1315.game.util.Translation;

public class Game {
	private Remote clientA;
	private Remote clientB;
	private boolean ended;

	public Game(Remote clientA) {
		this.clientA = clientA;
	}

	public Game(Remote clientA, Remote clientB) {
		this.clientA = clientA;
		this.clientB = clientB;
	}

	public void join(Remote client) {
		if(hasBothClients())
			throw new RuntimeException("Game has already two players");
		if (ended)
			throw new RuntimeException("Game already ended");
		clientB = client;
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
		return (clientA != null ? 1 : 0) + (clientB != null ? 1 : 0);
	}

	public void end(Remote leaver) {
		if (ended)
			throw new RuntimeException("Game already ended");
		if (leaver == clientA)
			clientB = null;
		else if (leaver == clientB)
			clientB = null;
		else
			throw new RuntimeException("leaver should be a player of this Game");
		ended = true;
		System.out.println(Translation.instance.translate("game.ended", this));
	}
}