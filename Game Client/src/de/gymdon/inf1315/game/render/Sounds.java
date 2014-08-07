package de.gymdon.inf1315.game.render;

import java.io.File;

import javax.sound.sampled.*;

import de.gymdon.inf1315.game.Utils;

public class Sounds {

    public static void play(String filename) {
	try {
	    ClassLoader cl = Sounds.class.getClassLoader();
	    String fileEmpty = Utils.getResourceListing(cl, "/sound/" + filename).size() == 0 ? "/sound/" + filename : Utils.getResourceListing(cl, "/sound/" + filename).get(0);
	    File audioFile = new File(new File((cl.getResource("")).toURI()).getAbsolutePath().replace("\\", "/") + fileEmpty);
	    Clip clip = AudioSystem.getClip();
	    AudioInputStream inputStream = AudioSystem.getAudioInputStream(audioFile);
	    clip.open(inputStream);
	    clip.start();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
