package de.gymdon.inf1315.game.packet;

import de.gymdon.inf1315.game.util.Self;

public class PacketPing extends Packet {

	public static final short ID = 3;
	
	public PacketPing(Remote r) {
		super(r);
	}
	
	@Override
	public void handlePacket() {
		super.handlePacket();
		if (Self.instance.isServer())
			send();
	}
	
	@Override
	public void send() {
		remote.writeShort(ID);
		super.send();
	}

}
