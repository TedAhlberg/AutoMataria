package gameclient;

import java.awt.event.ActionEvent;

/**
 *
 * @author eriklundow
 *
 */
public class SoundFx {

    private Audio menuSelect;
    private Audio crash;
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
        menuSelect = Audio.getSFX("Menu_Select.mp3");
        play(menuSelect);
    }

    public void crash() {
        crash = Audio.getSFX("Crash.mp3");
        play(crash);

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
}
