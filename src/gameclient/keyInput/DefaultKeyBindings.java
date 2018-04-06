package gameclient.keyInput;

import java.awt.event.KeyEvent;
import java.util.HashMap;

import common.Action;

public class DefaultKeyBindings {
	private HashMap<Action, Integer> bindings = new HashMap();

	public DefaultKeyBindings() {
		bindings.put(Action.ExitGame, KeyEvent.VK_ESCAPE);
		bindings.put(Action.ToggleInterpolation, KeyEvent.VK_I);
		bindings.put(Action.ToggleNames, KeyEvent.VK_SHIFT);
		bindings.put(Action.ToggleDebugText, KeyEvent.VK_F1);

		bindings.put(Action.GoLeft, KeyEvent.VK_LEFT);
		bindings.put(Action.GoRight, KeyEvent.VK_RIGHT);
		bindings.put(Action.GoUp, KeyEvent.VK_UP);
		bindings.put(Action.GoDown, KeyEvent.VK_DOWN);
	}
	
	public HashMap getHashMap() {
		return bindings;
	}
}
