package gameclient.keyinput;

import common.Action;

import java.awt.event.KeyEvent;
import java.util.HashMap;

/**
 * @author Ted Ahlberg
 */
public class DefaultKeyBindings {
	private HashMap<Action, Integer> bindings = new HashMap<Action, Integer>();

	public DefaultKeyBindings() {
		bindings.put(Action.InterfaceBack, KeyEvent.VK_ESCAPE);
		bindings.put(Action.ToggleInterpolation, KeyEvent.VK_F2);
		bindings.put(Action.ToggleNames, KeyEvent.VK_SHIFT);
		bindings.put(Action.ToggleDebugText, KeyEvent.VK_F1);
		bindings.put(Action.ToggleReady, KeyEvent.VK_R);
		bindings.put(Action.TogglePlayerColor, KeyEvent.VK_C);
		bindings.put(Action.SendChatMessage, KeyEvent.VK_ENTER);
		bindings.put(Action.OpenChatPrompt, KeyEvent.VK_Y);
		bindings.put(Action.UsePickup, KeyEvent.VK_SPACE);

		bindings.put(Action.GoLeft, KeyEvent.VK_LEFT);
		bindings.put(Action.GoRight, KeyEvent.VK_RIGHT);
		bindings.put(Action.GoUp, KeyEvent.VK_UP);
		bindings.put(Action.GoDown, KeyEvent.VK_DOWN);
	}

	public HashMap<Action, Integer> getHashMap() {
		return bindings;
	}
}
