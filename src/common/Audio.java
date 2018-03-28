package common;

import java.net.MalformedURLException;
import java.net.URL;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;

public class Audio {
	private String songName;
	private String pathToMp3;
	private BasicPlayer player = new BasicPlayer();

	public Audio(String songName) {
		pathToMp3 = System.getProperty("user.dir") + "/resources/Music/" + songName;
		try {
			player.open(new URL("file:///" + pathToMp3));
		} catch (BasicPlayerException | MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public void play() {
		try {
			player.play();
		} catch (BasicPlayerException e) {
			e.printStackTrace();
		}

	}
	

	public void stop() {
		try {
			player.stop();
		} catch (BasicPlayerException e) {
			e.printStackTrace();
		}
	}
	
	

}
