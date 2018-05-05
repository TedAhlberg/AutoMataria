package gameclient.sound;

import gameclient.Resources;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * A class for playing a sound clip.
 * 
 * @Erik Lundow
 */
public class Audio implements Runnable {
    
    private SourceDataLine line;
    private AudioInputStream currentDecoded;
    private AudioInputStream encoded;
    private AudioFormat encodedFormat;
    private AudioFormat decodedFormat;
    private static final Object lock = new Object();
    private boolean started = false;
    private boolean stopped = false;
    private boolean paused = false;
    private static boolean on = true;
    private static boolean sfxOff = false;
    private static boolean musicStopped;
    private int times;
    private final int BUFFERSIZE = 4096;
    private URL url;

    /**
     * Constructs a Sound object specified by the url argument
     * 
     * @param url
     *            giving the location of the sound file
     */
    private Audio(URL url) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        this.url = url;
        encoded = AudioSystem.getAudioInputStream(url);
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
        Audio sound = null;
        try {
            URL url = Resources.musicPath.resolve(filename).toUri().toURL();
            sound = new Audio(url);
        } catch (Exception e) {
            System.out.println(e);
        }
        return sound;
    }

    public static Audio getSFX(String filename) {
        Audio sound = null;
        try {
            URL url = Resources.sfxPath.resolve(filename).toUri().toURL();
            sound = new Audio(url);
        } catch (Exception e) {
            System.out.println(e);
        }
        return sound;
    }

    /**
     * Begins to play the sound / resumes playback of a sound that is paused. A
     * sound can only be started once.
     */
    public synchronized void play(int times) {
       if(!on) {
           try {
            lock.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       }
        if (!started && on) {
            setLoop(times);
            new Thread(this).start();
            started = true;
        }
    }
    
    /**
     * Begins to play the sfx / resumes playback of a sound that is paused. A
     * sound can only be started once.
     */
    public synchronized void playSfx() {
        if(!started&&!sfxOff) {
            setLoop(1);
            new Thread(this).start();
            started = true;
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
    
    public static void setMusicStopped() {
        musicStopped=true;
        musicStopped=false;
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
                    if (times > 1) {
                        times--;
                        try {
                            encoded = AudioSystem.getAudioInputStream(url);
                            currentDecoded = AudioSystem.getAudioInputStream(decodedFormat, encoded);
                        } catch (UnsupportedAudioFileException e) {
                            setStopped(true);
                            e.printStackTrace();
                        }
                    } else {
                        setStopped(true);
                    }
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

    public static void sfxOff() {
        sfxOff = !sfxOff;
        
    }
    
    public static void on() {
        setMusicStopped();
        on=!on;
        try {
        lock.notifyAll();
        }catch (Exception e){}
        
    }

    public static void main(String[] args) {
        Audio audio = Audio.getTrack("AM-GameTrack.mp3");
        audio.play(1);
//        audio.setVolume(1);

    }

}
