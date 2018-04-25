package gameclient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

/**
 * Singleton class handling Backgroundmusic.
 * @author eriklundow
 *
 */
public class MusicManager extends JButton implements ActionListener{

    private static Audio music;
    private static boolean stop=false;

    private static  MusicManager instance = null;
    

    protected MusicManager() {
        this.addActionListener(this);
    }
    
    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }
    
    public void gameTrack1() {
        if(!stop) {
        music=Audio.getTrack("AM-GameTrack.mp3");
        play(music);
        }
                
    }
    
    public void play(Audio music) {
         music.play(99);
         music=null;

    }
    
    public static void stop() {
        stop=!stop;
        music.setVolume(0);
        music.stop();
        music.setVolume(1);
        
    }


    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==this) {
            stop();

        }
        
        
        
    }
    public static void main(String[] args) {
       
        JFrame frame = new JFrame();
        frame.setVisible(true);
        frame.add( MusicManager.getInstance());
        frame.pack();
    }
}
