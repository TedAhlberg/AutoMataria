package gameclient.keyinput;

import java.awt.event.KeyEvent;
import java.util.HashMap;

import common.Action;

/**
 * @author Ted Ahlberg
 */
public class KeyBindings {
	private DefaultKeyBindings defaultBindings = new DefaultKeyBindings();
	private HashMap<Action, Integer> keyBindings = defaultBindings.getHashMap();

	public KeyBindings() {
	}
	
	public void setKeyBindings(HashMap<Action, Integer> keyBindings) {
		this.keyBindings = keyBindings;
	}

	public void setDefaultKeyBindings() {
		defaultBindings = new DefaultKeyBindings();
		keyBindings = defaultBindings.getHashMap();
	}
	
	public HashMap<Action, Integer> getKeyBindings(){
		return keyBindings;
	}
	
	public int getKeyFor(Action action) {
		return keyBindings.get(action);
	}
	
	public String toString() {
		StringBuilder res = new StringBuilder();
		Action[] actions = Action.values();
		for (int i = 0; i < actions.length; i++) {
			res.append((actions[i] + " -> " + KeyEvent.getKeyText(keyBindings.get(actions[i])) + " \n"));
		}
		return res.toString();
	}
}