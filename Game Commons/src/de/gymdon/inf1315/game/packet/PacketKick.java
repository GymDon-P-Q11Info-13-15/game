package de.gymdon.inf1315.game.packet;

import com.google.gson.Gson;

public class PacketKick extends Packet {

	public static final short ID = 2;
	public String message;
	public Object[] args;
	
	public static final short MESSAGE_READ = 1;

	public PacketKick(Remote r) {
		super(r);
	}

	@Override
	public void handlePacket() {
		switch(actualStatus) {
		case NEW_PACKET:
			message = remote.readUTF();
			actualStatus = MESSAGE_READ;
		case MESSAGE_READ:
			args = new Gson().fromJson(remote.readUTF(), Object[].class);
			super.handlePacket();
		}
		
	}

	@Override
	public void send() {
		remote.writeShort(ID);
		remote.writeUTF(message);
		remote.writeUTF(new Gson().toJson(args));
		super.send();
	}
}
