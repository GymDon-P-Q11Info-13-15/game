package de.gymdon.inf1315.game.packet;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class Packet {

    public static final short ID = -1;
    public static final Map<Short, Class<? extends Packet>> packetTypes = new HashMap<Short, Class<? extends Packet>>();
    public static final int PROTOCOL_VERSION = 3;

    static {
        register(PacketHello.class);
        register(PacketHeartbeat.class);
        register(PacketKick.class);
        register(PacketStartGame.class);
        register(PacketGameAction.class);
        register(PacketGameInfo.class);
    }

    protected Remote remote;

    public Packet(Remote r) {
        this.remote = r;
    }

    @Nullable
    public static Packet newPacket(short id, Remote r) {
        if (packetTypes.containsKey(id))
            try {
                return packetTypes.get(id).getConstructor(Remote.class).newInstance(r);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return null;
    }

    private static void register(Class<? extends Packet> packet) {
        try {
            short id = packet.getField("ID").getShort(null);
            if (id < 0)
                System.err.println("Packet ID undefined: " + packet.getSimpleName());
            else if (packetTypes.containsKey(id))
                System.err.println("Duplicate Packet ID: " + packet.getSimpleName() + " (0x" + Integer.toHexString(id) + " = " + packetTypes.get(id).getSimpleName() + ")");
            else
                packetTypes.put(id, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handlePacket() throws IOException {
        remote.notifyPacket(this, true);
    }

    public void send() throws IOException {
        remote.getOutputStream().flush();
        remote.notifyPacket(this, false);
    }
}
