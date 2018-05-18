package gameclient.sound;

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
        effects.put("Crash.mp3", Audio.getSFX("Crash.mp3"));
        effects.put("Erase.mp3", Audio.getSFX("Erase.mp3"));
        effects.put("Ghost.mp3", Audio.getSFX("Ghost.mp3"));
        effects.put("Invincible.mp3", Audio.getSFX("Invincible.mp3"));
        effects.put("Menu_Select.mp3", Audio.getSFX("Menu_Select.mp3"));
        effects.put("Movement.mp3", Audio.getSFX("Movement.mp3"));
        effects.put("Reverse.mp3", Audio.getSFX("Reverse.mp3"));
        effects.put("SelfSlowPickup.mp3", Audio.getSFX("SelfSlowPickup.mp3"));
        effects.put("SelfSpeedPickup.mp3", Audio.getSFX("SelfSpeedPickup.mp3"));
        effects.put("SlowEnemies.mp3", Audio.getSFX("SlowEnemies.mp3"));
        effects.put("speedEnemies.mp3", Audio.getSFX("speedEnemies.mp3"));
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
