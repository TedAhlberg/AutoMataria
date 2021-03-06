package gameclient.sound;

/**
 * Singleton class handling Backgroundmusic.
 * @author eriklundow
 *
 */
public class MusicManager {

    private static Audio music;
    private static boolean stop = false;
    private static MusicManager instance = null;
    private static Status status;
    
    protected MusicManager() {
    }

    public static Status getStatus() {
        return status;
    }


    public static void setStatus(Status s) {
        status = s;
    }

    public static enum Status {
        InGame, 
        InMenu;
    }

    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

    public void gameTrack1() {

        music = Audio.getTrack("AM-GameTrack.mp3");
        play(music);

    }

    public void menuTrack() {
        music = Audio.getTrack("AM-MenuTrack.mp3");

        play(music);

    }

    public void play(Audio music) {
        if (!stop && music != null) {
            music.play(99);
            music = null;
        }
    }

    public static void changeTrack() {
        stop = !stop;
        music.setVolume(0);
        music.stop();
        music.setVolume(1);
        stop = !stop;
    }

    public static void stop() {
        stop = !stop;
        music.setVolume(0);
        music.stop();
        music.setVolume(1);
    }

    
}
