package de.gymdon.inf1315.game.server;

import java.io.IOException;
import java.net.Socket;

import de.gymdon.inf1315.game.Game;
import de.gymdon.inf1315.game.packet.Remote;

public class Client extends Remote {

    private Game game;

    public Client(Socket s) throws IOException {
	super(s);
    }

    public Client getOtherClient() {
	if (game == null)
	    return null;
	if (game.hasBothClients())
	    return game.getClientA() == this ? game.getClientB() : game
		    .getClientA();
	return null;
    }

    public Game getGame() {
	return game;
    }

    @Override
    public void leave(String message) {
	super.leave(message);
	if (game != null)
	    game.end(this);
    }

    @Override
    public boolean isServer() {
	return false;
    }

    @Override
    public boolean isClient() {
	return true;
    }
}
