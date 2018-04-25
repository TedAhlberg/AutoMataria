package gameclient;

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

    
    public void menuSelect() {
        sfx = Audio.getSFX("Menu_Select.mp3");
        play(sfx);
    }

    public void crash() {
        sfx = Audio.getSFX("Crash.mp3");
        play(sfx);

    }
    
    public void SelfSpeedPickup() {
        sfx=Audio.getSFX("SelfSpeedPickup.mp3");
        play(sfx);
    }

    public void play(Audio sfx) {
        sfx.playSfx();
        sfx = null;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this) {
            crash();
        }
    }

    public static void main(String[] args) {
        SoundFx.getInstance().crash();
    }

    public void selfSlowPickup() {
        sfx=Audio.getSFX("SelfSlowPickup.mp3");
        play(sfx);
        
    }
}
