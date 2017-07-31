package de.gymdon.inf1315.game.packet;

import com.google.gson.Gson;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketKick extends Packet {

    public static final short ID = 2;
    public String message;
    public Object[] args;

    public PacketKick(Remote r) {
        super(r);
    }

    @Override
    public void handlePacket() throws IOException {
        super.handlePacket();
        DataInput in = remote.getInputStream();
        message = in.readUTF();
        args = new Gson().fromJson(in.readUTF(), Object[].class);
    }

    @Override
    public void send() throws IOException {
        super.send();
        DataOutputStream out = remote.getOutputStream();
        out.writeShort(ID);
        out.writeUTF(message);
        out.writeUTF(new Gson().toJson(args));
        super.send();
    }
}
