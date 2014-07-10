package de.gymdon.inf1315.game.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketHeartbeat extends Packet {

	public static final short ID = 1;
	public boolean response;
	public byte[] payload;

	public PacketHeartbeat(Remote r) {
		super(r);
	}

	@Override
	public void handlePacket() throws IOException {
		response = remote.readBoolean();
		payload = new byte[remote.readShort()];
		remote.read(payload);
		if (!response) {
			PacketHeartbeat resp = new PacketHeartbeat(remote);
			resp.response = true;
			resp.payload = payload;
			resp.send();
		}
		super.handlePacket();
	}

	@Override
	public void send() throws IOException {
		DataOutputStream out = remote.getOutputStream();
		out.writeShort(ID);
		out.writeBoolean(response);
		out.writeShort(payload.length);
		out.write(payload);
		super.send();
	}
}
