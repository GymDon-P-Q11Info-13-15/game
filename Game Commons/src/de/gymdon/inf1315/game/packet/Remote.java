package de.gymdon.inf1315.game.packet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

import de.gymdon.inf1315.game.Translation;

public abstract class Remote {

    public static boolean isServer;
    protected SocketChannel socketChannel;
    protected ByteBuffer inBuffer;
    protected ByteBuffer outBuffer;
    protected long lastPacket;
    protected boolean left = false;
    public Map<String,Object> properties = new HashMap<String,Object>();
    private Set<PacketListener> listeners = new HashSet<PacketListener>();
    protected boolean ping;

    public Remote(SocketChannel s) throws IOException {
	this.socketChannel = s;
	inBuffer = ByteBuffer.allocate(65536);
    }

    public SocketChannel getSocketChannel() {
	return socketChannel;
    }

    public ByteBuffer getInBuffer() {
	if (left())
	    throw new RuntimeException("Client left");
	return inBuffer;
    }
    
    public ByteBuffer getOutBuffer() {
	if (left())
	    throw new RuntimeException("Client left");
	return outBuffer;
    }

    public boolean left() {
	return left;
    }

    public void leave(String message) {
	if (left)
	    return;
	left = true;
	if(properties.containsKey("translation")) {
	    Translation t = (Translation)properties.get("translation");
	    System.out.println(t.translate("client.left", message));
	}
	if(message == null)
	    throw new NullPointerException();
	try {
	    socketChannel.close();
	} catch (IOException e) {
	}
    }
    
    public void kick(String message, Object... args) {
	if (left)
	    return;
	PacketKick kick = new PacketKick(this);
	kick.message = message;
	kick.args = args;
	if(properties.containsKey("translation")) {
	    Translation t = (Translation)properties.get("translation");
	    System.out.println(t.translate("client.kicked", socketChannel.socket().getInetAddress().getCanonicalHostName(), t.translate(message, args)));
	}
	try {
	    kick.send();
	} catch (IOException e) {
	}
	try {
	    socketChannel.close();
	} catch (IOException e) {
	}
	left = true;
    }
    
    public void notifyPacket() {
	lastPacket = System.currentTimeMillis();
    }
    
    public long getLastPacketTime() {
	return lastPacket;
    }
    
    public void addPacketListener(PacketListener l) {
	listeners.add(l);
    }

    public void removePacketListener(PacketListener l) {
	listeners.remove(l);
    }

    public abstract boolean isServer();

    public abstract boolean isClient();

    public void setPing(boolean ping) {
	this.ping = ping;
    }
}
