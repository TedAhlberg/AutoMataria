package gameclient.sound;

import gameclient.Resources;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;

/**
 * @author eriklundow
 */
public class SoundFx {
    private static SoundFx instance;
    private HashMap<String, Audio> effects = new HashMap<>();

    private SoundFx() {
        loadSoundEffects();
    }

    public static SoundFx getInstance() {
        if (instance == null) {
            instance = new SoundFx();
        }
        return instance;
    }

    public static void main(String[] args) {
        SoundFx.getInstance().crash();
        SoundFx.getInstance().movement();
        SoundFx.getInstance().ReversePickup();
    }

    /**
     * Loads all sound effects into a HashMap for quick access
     */
    private void loadSoundEffects() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Resources.sfxPath)) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    String filename = path.getFileName().toString();
                    effects.put(filename, Audio.getSFX(filename));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play(String sound) {
        Audio audio = effects.get(sound);
        if (audio != null) {
            audio.playSfx();
        }
    }

    public void SelfGhostPickup() {
        play("Ghost.mp3");
    }

    public void menuSelect() {
        play("Menu_Select.mp3");
    }

    public void crash() {
        play("Crash.mp3");
    }

    public void SelfSpeedPickup() {
        play("SelfSpeedPickup.mp3");
    }

    public void movement() {
        play("Movement.mp3");
    }

    public void SelfSlowPickup() {
        play("SelfSlowPickup.mp3");

    }

    public void EraserPickup() {
        play("Erase.mp3");
    }

    public void InvinciblePickup() {
        play("Invincible.mp3");

    }

    public void SlowEnemiesPickup() {
        play("SlowEnemies.mp3");
    }

    public void ReversePickup() {
        play("Reverse.mp3");
    }

    public void SpeedEnemiesPickup() {
        play("speedEnemies.mp3");
    }
}
