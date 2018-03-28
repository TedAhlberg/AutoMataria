package common;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class AudioPlayer implements Runnable {
	private SourceDataLine line;
	private AudioInputStream currentDecoded;
	private AudioInputStream encoded;
	private AudioFormat encodedFormat;
	private AudioFormat decodedFormat;
	private boolean started = false;
	private boolean stopped = false;
	private boolean paused = false;
	private final int BUFFERSIZE = 4096;

	private AudioPlayer(URL url) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
		encoded = AudioSystem.getAudioInputStream(url);
		encodedFormat = encoded.getFormat();
		decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, encodedFormat.getSampleRate(), 16,
				encodedFormat.getChannels(), encodedFormat.getChannels() * 2, encodedFormat.getSampleRate(), false);
		currentDecoded = AudioSystem.getAudioInputStream(decodedFormat, encoded);
		line = AudioSystem.getSourceDataLine(decodedFormat);
		line.open(decodedFormat);
		setPaused(true);
		setStopped(false);
	}

	public static AudioPlayer getSound(String filename) {
		AudioPlayer sound = null;
		try {
			URL url = new File(filename).toURI().toURL();
			sound = new AudioPlayer(url);
		} catch (Exception e) {
			System.out.println(e);
		}
		return sound;
	}

	public synchronized void play() {
		if (!started) {
			new Thread(this).start();
			started = true;
		}
		setPaused(false);
		notify();
	}

	public synchronized void stop() {
		setStopped(true);
		setPaused(false);
		notify();
	}

	public synchronized void pause() {
		setPaused(true);
	}

	private boolean getStopped() {
		return stopped;
	}

	private void setStopped(boolean stopped) {
		this.stopped = stopped;
	}

	private boolean getPaused() {
		return paused;
	}

	private void setPaused(boolean paused) {
		this.paused = paused;
	}

	public void run() {
		line.start();
		byte[] b = new byte[BUFFERSIZE];
		int i = 0;
		setStopped(false);
		setPaused(false);
		while (!getStopped()) {
			while (getPaused()) {
				synchronized (this) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						setStopped(true);
						setPaused(false);
					}
				}
			}
			try {
				i = currentDecoded.read(b, 0, b.length);
				if (i == -1) {
					setStopped(true);
				} else
					line.write(b, 0, i);
			} catch (IOException e) {
				System.err.println(e);
			}
		}
		line.drain();
		line.stop();
		line.close();
		try {
			currentDecoded.close();
			encoded.close();
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

}
