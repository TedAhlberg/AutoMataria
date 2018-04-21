package gameclient;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class SoundFx extends JButton implements ActionListener {
    private static final long serialVersionUID = 1L;
    
    private Audio menuSelect;
    
    public SoundFx() {
        this.addActionListener(this);
        
    }

    public void menuSelect() {
            menuSelect = Audio.getSFX("Menu_Select.mp3");
            play(menuSelect);

        
    }
    public void play(Audio sfx) {
        sfx.playSfx();
        sfx=null;
    }
    
    
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this) {
            menuSelect();
        }
    }

    public static void main(String[] args) {
        Frame frame = new Frame();
        frame.setVisible(true);
        
        SoundFx audio = new SoundFx();
        frame.add(audio);
        frame.pack();
        

    }

}
