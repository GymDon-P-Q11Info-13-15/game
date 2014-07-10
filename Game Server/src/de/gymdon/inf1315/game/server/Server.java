package de.gymdon.inf1315.game.server;

import java.net.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.io.*;
import java.util.ArrayList;

import de.gymdon.inf1315.game.packet.Remote;
import de.gymdon.inf1315.game.util.Self;
import de.gymdon.inf1315.game.util.Translation;

public class Server implements Runnable {

    public static Server instance;

    List<Remote> clientList = new ArrayList<Remote>();
    private boolean running = false;
    private Selector selector;
    private Timer timer;
    public Preferences preferences;
    public Translation translation;

    public static void main(String[] args) {
	new Server();
    }

    public Server() {
	if (instance != null)
	    throw new RuntimeException("Already running");
	instance = this;
	Self self = new Self() {

	    @Override
	    public boolean isServer() {
		return true;
	    }

	    @Override
	    public String getName() {
		return "Game Server";
	    }
	};
	Self.instance = self;
	translation = new Translation("en");
	this.readPreferences();
	if (!preferences.language.equals("en"))
	    translation.load(preferences.language);
	this.run();
    }

    private void readPreferences() {
	File f = new File("preferences.json");
	try {
	    if (f.exists())
		preferences = Preferences.readNew(new FileReader(f));
	    else {
		preferences = new Preferences();
		f.createNewFile();
		preferences.write(new FileWriter(f));
		System.out.println(translation.translate("file.created",
			"preferences.json"));
	    }
	    if (preferences.version != Preferences.CURRENT_VERSION) {
		preferences.version = Preferences.CURRENT_VERSION;
		preferences = new Preferences();
		f.createNewFile();
		preferences.write(new FileWriter(f));
		System.out.println(translation.translate("updated.version",
			"preferences.json", preferences.version));
	    }
	} catch (IOException e) {
	    throw new RuntimeException("Preferences couldn't be loaded/saved",
		    e);
	}
    }

    public void run() {
	running = true;
	ServerSocketChannel ssc;
	Selector selector;

	ssc = ServerSocketChannel.open();
	ssc.configureBlocking(false);
	ssc.socket().bind(new InetSocketAddress(preferences.port));

	selector = Selector.open();
	ssc.register(selector, SelectionKey.OP_ACCEPT);
	
	  try { if(!(preferences.hostname.equals("0.0.0.0") ||
	  preferences.hostname.equals("*") || preferences.hostname.equals("")))
	  ssc.socket().bind(new InetSocketAddress(preferences.hostname,
	  preferences.port));
	  System.out.println(Self.instance.translation.translate("server.started",
	  preferences.hostname, preferences.port)); } catch (IOException e) {
	 e.printStackTrace(); return; }
	 
	timer.start();
	while (running) {
	    int newEvents = selector.select();
	    Set<SelectionKey> events = selector.selectedKeys();
	    Iterator<SelectionKey> it = events.iterator();
	    while(it.hasNext()) {
		SelectionKey sk = it.next();
		if ((sk.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
		    SocketChannel sc = ((ServerSocketChannel) sk.channel()).accept();
		    sc.configureBlocking(false);
		    SelectionKey tempSelectionKey = sc.register(selector, SelectionKey.OP_READ);
		    Client c = new Client(sc);
		    tempSelectionKey.attach(c);
		    clientList.add(c);
		}
		if ((sk.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
		    Client c = (Client) sk.attachment();
		    c.handlePacket();
		}
		it.remove();
	}
	timer.stopTimer();
	try {
	    timer.join();
	} catch (InterruptedException e1) {
	}
	try {
	    ssc.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public void stop() {
	running = false;
    }
}
