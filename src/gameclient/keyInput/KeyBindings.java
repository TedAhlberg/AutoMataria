package gameclient.keyInput;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import common.Action;

/**
 * @author Ted Ahlberg
 */
public class KeyBindings extends KeyAdapter implements Runnable{
	private DefaultKeyBindings defaultBindings;
	private HashMap<Action, Integer> bindings = new HashMap<Action, Integer>();
	private boolean running;
	private Thread thread;
	
	public KeyBindings() {
		
	}
	
	public void setDefaultKeyBindings() {
		defaultBindings = new DefaultKeyBindings();
		bindings = defaultBindings.getHashMap();
	}
	
	public void changeKeyFor(Action action) {
		
	}
	
	public void keyPressed(KeyEvent e) {
		System.out.println(KeyEvent.getKeyText(e.getKeyCode()));
	}
		
	public static void main(String[] args) {
		KeyBindings test = new KeyBindings();
		JFrame test2 = new JFrame();
		test2.addKeyListener(test);
		test2.setVisible(true);
		test2.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	public void run() {
		// TODO Auto-generated method stub
		
	}
}