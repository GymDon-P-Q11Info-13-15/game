package de.gymdon.inf1315.game.packet;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketStartGame extends Packet {

    public static final short ID = 3;
    public String opponent;

    public PacketStartGame(Remote r) {
        super(r);
    }

    @Override
    public void handlePacket() throws IOException {
        super.handlePacket();
        DataInput in = remote.getInputStream();
        opponent = in.readUTF();
    }

    @Override
    public void send() throws IOException {
        DataOutputStream out = remote.getOutputStream();
        out.writeShort(ID);
        out.writeUTF(opponent == null ? "" : opponent);
        super.send();
    }

}
