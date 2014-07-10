package de.gymdon.inf1315.game.packet;

import java.io.IOException;
import java.io.UTFDataFormatException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

import de.gymdon.inf1315.game.util.Self;
import de.gymdon.inf1315.game.util.Translation;

public abstract class Remote {

	protected SocketChannel socket;
	protected ByteBuffer in;
	protected ByteBuffer out;
	protected Packet openPacket;
	protected long lastPacket;
	protected boolean left = false;
	private Set<PacketListener> listeners = new HashSet<PacketListener>();
	protected boolean ping;

	public Remote(SocketChannel s) throws IOException {
		this.socket = s;
		in = ByteBuffer.allocateDirect(65540);
		out = ByteBuffer.allocateDirect(65540);
		notifyPacket();
	}

	public void readFromChannel() {
		in.compact();
		try {
			socket.read(in);
		} catch (IOException e) {
			e.printStackTrace();
			leave(e.toString());
		}
		in.flip();
	}

	public void writeToChannel() {
		out.flip();
		try {
			while (out.hasRemaining())
				socket.write(out);
		} catch (IOException e) {
			e.printStackTrace();
			leave(e.toString());
		}
		out.clear();
	}

	public boolean readBoolean() {
		return in.get() != (byte) 0;
	}

	public byte readByte() {
		return in.get();
	}

	public short readShort() {
		return in.getShort();
	}

	public char readChar() {
		return in.getChar();
	}

	public int readInt() {
		return in.getInt();
	}

	public float readFloat() {
		return in.getFloat();
	}

	public long readLong() {
		return in.getLong();
	}

	public double readDouble() {
		return in.getDouble();
	}

	public String readUTF() {
		in.mark();
		try {
			byte ch1 = in.get();
			byte ch2 = in.get();
			int utflen = (ch1 << 8) + (ch2 << 0);
			byte[] bytearr = new byte[utflen];
			char[] chararr = new char[utflen];

			int c, char2, char3;
			int count = 0;
			int chararr_count = 0;

			for (int i = 0; i < utflen; i++) {
				bytearr[i] = in.get();
			}

			while (count < utflen) {
				c = (int) bytearr[count] & 0xff;
				if (c > 127)
					break;
				count++;
				chararr[chararr_count++] = (char) c;
			}

			while (count < utflen) {
				c = (int) bytearr[count] & 0xff;
				switch (c >> 4) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
					/* 0xxxxxxx */
					count++;
					chararr[chararr_count++] = (char) c;
					break;
				case 12:
				case 13:
					/* 110x xxxx 10xx xxxx */
					count += 2;
					if (count > utflen)
						throw new UTFDataFormatException(
								"malformed input: partial character at end");
					char2 = (int) bytearr[count - 1];
					if ((char2 & 0xC0) != 0x80)
						throw new UTFDataFormatException(
								"malformed input around byte " + count);
					chararr[chararr_count++] = (char) (((c & 0x1F) << 6) | (char2 & 0x3F));
					break;
				case 14:
					/* 1110 xxxx 10xx xxxx 10xx xxxx */
					count += 3;
					if (count > utflen)
						throw new UTFDataFormatException(
								"malformed input: partial character at end");
					char2 = (int) bytearr[count - 2];
					char3 = (int) bytearr[count - 1];
					if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
						throw new UTFDataFormatException(
								"malformed input around byte " + (count - 1));
					chararr[chararr_count++] = (char) (((c & 0x0F) << 12)
							| ((char2 & 0x3F) << 6) | ((char3 & 0x3F) << 0));
					break;
				default:
					/* 10xx xxxx, 1111 xxxx */
					throw new UTFDataFormatException(
							"malformed input around byte " + count);
				}
			}
			// The number of chars produced may be less than utflen
			return new String(chararr, 0, chararr_count);
		} catch (BufferUnderflowException e) {
			in.reset();
			throw e;
		} catch (UTFDataFormatException e) {
			in.reset();
			throw new RuntimeException(e);
		}
	}

	public void writeBoolean(boolean b) {
		try {
			out.put(b ? (byte) 1 : (byte) 0);
		} catch (BufferOverflowException e) {
			this.writeToChannel();
			out.put(b ? (byte) 1 : (byte) 0);
		}
	}

	public void writeByte(byte b) {
		out.put(b);
	}

	public void writeShort(short s) {
		out.putShort(s);
	}

	public void writeChar(char c) {
		out.putChar(c);
	}

	public void writeInt(int i) {
		out.putInt(i);
	}

	public void writeFloat(float f) {
		out.putFloat(f);
	}

	public void writeLong(long l) {
		out.putLong(l);
	}

	public void writeDouble(double d) {
		out.putDouble(d);
	}

	public void writeUTF(String str) {
		out.mark();
		try {
			int strlen = str.length();
			int utflen = 0;
			int c, count = 0;

			/* use charAt instead of copying String to char array */
			for (int i = 0; i < strlen; i++) {
				c = str.charAt(i);
				if ((c >= 0x0001) && (c <= 0x007F)) {
					utflen++;
				} else if (c > 0x07FF) {
					utflen += 3;
				} else {
					utflen += 2;
				}
			}

			if (utflen > 65535)
				throw new UTFDataFormatException("encoded string too long: "
						+ utflen + " bytes");

			byte[] bytearr = new byte[utflen + 2];

			bytearr[count++] = (byte) ((utflen >>> 8) & 0xFF);
			bytearr[count++] = (byte) ((utflen >>> 0) & 0xFF);

			int i = 0;
			for (i = 0; i < strlen; i++) {
				c = str.charAt(i);
				if (!((c >= 0x0001) && (c <= 0x007F)))
					break;
				bytearr[count++] = (byte) c;
			}

			for (; i < strlen; i++) {
				c = str.charAt(i);
				if ((c >= 0x0001) && (c <= 0x007F)) {
					bytearr[count++] = (byte) c;

				} else if (c > 0x07FF) {
					bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
					bytearr[count++] = (byte) (0x80 | ((c >> 6) & 0x3F));
					bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
				} else {
					bytearr[count++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
					bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
				}
			}
			for (int j = 0; j < utflen + 2; j++) {
				out.put(bytearr[j]);
			}
		} catch (BufferOverflowException e) {
			out.reset();
			writeToChannel();
			writeUTF(str);
		} catch (UTFDataFormatException e) {
			out.reset();
			throw new RuntimeException(e);
		}
	}

	public boolean left() {
		return left;
	}

	public void leave(String message) {
		if (message == null)
			throw new NullPointerException();
		if (left)
			throw new RuntimeException("Remote already left");
		left = true;
		System.out.println(Self.instance.translation.translate("client.left",
				message));
		try {
			socket.close();
		} catch (IOException e) {
		}
	}

	public void kick(String message, Object... args) {
		if (left)
			throw new RuntimeException("Remote already left");
		PacketKick kick = new PacketKick(this);
		kick.message = message;
		kick.args = args;
		Translation t = Self.instance.translation;
		System.out.println(t.translate("client.kicked", socket.socket()
				.getInetAddress().getCanonicalHostName(),
				t.translate(message, args)));
		kick.send();
		try {
			socket.close();
		} catch (IOException e) {
		}
		left = true;
	}

	public void handlePacket() {
		this.readFromChannel();
		if (openPacket != null)
			openPacket.handlePacket();
		else {
			short id = this.readShort();
			openPacket = Packet.newPacket(id, this);
			openPacket.handlePacket();
		}
	}

	public void notifyPacket() {
		openPacket = null;
		lastPacket = System.currentTimeMillis();
		for (PacketListener pl : listeners)
			pl.handlePacket(this, null, true); // TODO ask
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

	public void setPing(boolean ping) {
		this.ping = ping;
	}

}
