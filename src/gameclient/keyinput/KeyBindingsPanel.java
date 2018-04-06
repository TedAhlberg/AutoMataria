package gameclient.keyinput;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import common.Action;

/**
 * @author Ted Ahlberg
 */
public class KeyBindingsPanel extends JPanel {
	private KeyBindings keyBindings;
	private KeyListener keyListener = new KeyListener();
	private boolean listening = false;
	private Action[] actions = Action.values();
	private int actionIndex;
	private final int length = actions.length;

	private JPanel mainActionPanel = new JPanel();
	private JPanel[] actionPanels = new JPanel[length];
	private JLabel[] actionLabels = new JLabel[length];
	private JButton[] actionButtons = new JButton[length];

	public KeyBindingsPanel(KeyBindings keyBindings) {
		this.keyBindings = keyBindings;

		this.addKeyListener(keyListener);
		this.setLayout(new BorderLayout());
		this.add(mainActionPanel, BorderLayout.CENTER);
		mainActionPanel.setLayout(new GridLayout(actions.length, 1));
		drawList();
	}

	private void drawList() {
		ButtonListener l = new ButtonListener();
		for (int i = 0; i < actionPanels.length; i++) {
			actionPanels[i] = new JPanel();
			actionPanels[i].setLayout(new GridLayout(1, 3));
			actionPanels[i].add(new JLabel(actions[i].name()));
			actionPanels[i].add(actionLabels[i] = new JLabel(KeyEvent.getKeyText(keyBindings.getKeyFor(actions[i]))));
			actionPanels[i].add(actionButtons[i] = new JButton("Change Key"));
			actionButtons[i].addActionListener(l);
			actionButtons[i].addKeyListener(keyListener);
			mainActionPanel.add(actionPanels[i]);
		}
	}

	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < actionButtons.length; i++) {
				if (e.getSource() == actionButtons[i] && !listening) {
					actionIndex = i;
					System.out.println("Change key binding for action: " + actions[i]);
					System.out.println(
							"Current key binding is: " + KeyEvent.getKeyText(keyBindings.getKeyFor(actions[i])));
					System.out.println("Enter new key binding...");
					listening = true;
				}
			}
		}
	}

	private class KeyListener extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if(listening == true) {
				keyBindings.setKeyBindingFor(actions[actionIndex], e);
				System.out.println(KeyEvent.getKeyText(e.getKeyCode()));
				actionLabels[actionIndex].setText(KeyEvent.getKeyText(e.getKeyCode()));
				System.out.println(keyBindings.toString());
				listening = false;
			}
		}
	}

	public static void main(String[] args) {
		KeyBindings test = new KeyBindings();
		KeyBindingsPanel test1 = new KeyBindingsPanel(test);
		JFrame frame = new JFrame();

		frame.add(test1);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
