package gameclient.sound;

import java.awt.event.ActionEvent;

/**
 *
 * @author eriklundow
 *
 */
public class SoundFx {

    private Audio sfx;
    private static SoundFx instance = null;

    protected SoundFx() {

    }

    public static SoundFx getInstance() {
        if (instance == null) {
            instance = new SoundFx();
        }
        return instance;
    }

    public void play(Audio sfx) {
        if (sfx != null) {
            sfx.playSfx();
            sfx = null;
        }
    }

    public void menuSelect() {
        sfx = Audio.getSFX("Menu_Select.mp3");
        play(sfx);
    }

    public void crash() {
        sfx = Audio.getSFX("Crash.mp3");
        play(sfx);

    }

    public void SelfSpeedPickup() {
        sfx = Audio.getSFX("SelfSpeedPickup.mp3");
        play(sfx);
    }

    public void movement() {
        sfx = Audio.getSFX("Movement.mp3");
        play(sfx);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this) {
            crash();
        }
    }

    public static void main(String[] args) {
        SoundFx.getInstance().crash();
    }

    public void SelfSlowPickup() {
        sfx = Audio.getSFX("SelfSlowPickup.mp3");
        play(sfx);

    }

    public void EraserPickup() {
        sfx = Audio.getSFX("Erase.mp3");
        play(sfx);
    }

    public void InvinciblePickup() {
        sfx = Audio.getSFX("Invincible.mp3");
        play(sfx);

    }

    public void SlowEnemiesPickup() {
        sfx = Audio.getSFX("SlowEnemies.mp3");
        play(sfx);
    }

    public void ReversePickup() {
        sfx = Audio.getSFX("Reverse.mp3");
        play(sfx);
    }

    public void SpeedEnemiesPickup() {
        sfx = Audio.getSFX("speedEnemies.mp3");
        play(sfx);
    }
}
