package gameclient.sound;

import gameclient.Resources;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * A class for playing a sound clip.
 *
 * @Erik Lundow
 */
public class Audio implements Runnable {
    private static final Object lock = new Object();
    private static boolean on = true;
    private static boolean sfxOff = false;
    private static boolean musicStopped;
    private final int BUFFERSIZE = 4096;
    private SourceDataLine line;
    private AudioInputStream currentDecoded;
    private AudioInputStream encoded;
    private AudioFormat encodedFormat;
    private AudioFormat decodedFormat;
    private boolean started = false;
    private boolean stopped = false;
    private boolean paused = false;
    private int times;
    private File file;
    private Thread soundThread;

    /**
     * Constructs a Sound object specified by the file argument
     *
     * @param file giving the location of the sound file
     */
    private Audio(File file) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        this.file = file;
        encoded = AudioSystem.getAudioInputStream(file);
        encodedFormat = encoded.getFormat();
        decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, // Encoding to use
                encodedFormat.getSampleRate(), // sample rate (same as base format)
                16, // sample size in bits (thx to Javazoom)
                encodedFormat.getChannels(), // # of Channels
                encodedFormat.getChannels() * 2, // Frame Size
                encodedFormat.getSampleRate(), // Frame Rate
                false // Big Endian
        );
        currentDecoded = AudioSystem.getAudioInputStream(decodedFormat, encoded);
        line = AudioSystem.getSourceDataLine(decodedFormat);
        line.open(decodedFormat);
        setPaused(true);
        setStopped(false);
    }

    public static Audio getTrack(String filename) {
        try {
            return new Audio(Resources.musicPath.resolve(filename).toFile());
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static Audio getSFX(String filename) {
        try {
            return new Audio(Resources.sfxPath.resolve(filename).toFile());
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static void setMusicStopped() {
        musicStopped = true;
        musicStopped = false;
    }

    public static void sfxOff() {
        sfxOff = !sfxOff;
    }

    public static void on() {
        setMusicStopped();
        on = !on;
        try {
            lock.notifyAll();
        } catch (Exception e) {}

    }

    public static void main(String[] args) {
        Audio audio = Audio.getTrack("AM-GameTrack.mp3");
        audio.play(1);
//        audio.setVolume(1);
    }

    /**
     * Begins to play the sound / resumes playback of a sound that is paused. A
     * sound can only be started once.
     */
    public synchronized void play(int times) {
        if (!on) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!started && on) {
            setLoop(times);
            new Thread(this, "ClientAudio").start();
            started = true;
        }
    }

    /**
     * Begins to play the sfx / resumes playback of a sound that is paused.
     */
    public synchronized void playSfx() {
        if (!sfxOff) {
            setLoop(1);
            if (soundThread == null) {
                started = true;
                soundThread = new Thread(this, "ClientAudio");
                soundThread.start();
            } else {
                resume();
            }
        }
    }

    /**
     * Stops playback of the sound.
     */
    public synchronized void stop() {
        setStopped(true);
        setPaused(false);
        notify();
    }

    /**
     * Pauses playback of the sound.
     */
    public synchronized void pause() {
        setPaused(true);
    }

    public synchronized void resume() {
        setPaused(false);
        notify();
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

    private void setLoop(int times) {
        this.times = times;
    }

    public void setVolume(double volume) {
        if (line.isOpen()) {
            volume = (volume <= 0.0) ? 0.0001 : ((volume > 1.0) ? 1.0 : volume);
            try {
                float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
                ((FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN)).setValue(dB);
            } catch (Exception ex) {

            }
        }
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
                    if (times >= 1) {
                        times--;
                        try {
                            encoded = AudioSystem.getAudioInputStream(file);
                            currentDecoded = AudioSystem.getAudioInputStream(decodedFormat, encoded);
                        } catch (UnsupportedAudioFileException e) {
                            setStopped(true);
                            e.printStackTrace();
                        }
                    }
                } else {
                    line.write(b, 0, i);
                }
                if (times == 0) {
                    setPaused(true);
                }
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
}
