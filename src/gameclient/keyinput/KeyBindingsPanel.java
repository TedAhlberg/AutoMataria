package gameclient.keyinput;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import common.Action;

/**
 * @author Ted Ahlberg
 */
public class KeyBindingsPanel extends JPanel {
	private static final long serialVersionUID = 1;
	private KeyBindings keyBindings;
	private HashMap<Action, Integer> keyMap;
	private KeyListener keyListener = new KeyListener();
	private ButtonListener buttonListener = new ButtonListener();
	private boolean listening = false;
	private Action[] actions = Action.values();
	private int actionIndex, length;

	private JScrollPane scrollPane;
	private JPanel mainActionPanel = new JPanel();
	private JPanel[] actionPanels;
	private JLabel[] actionLabels;
	private JButton[] actionButtons;

	private JPanel btnsPanel = new JPanel();
	private JButton defaultButton = new JButton("Default");
	private JButton saveButton = new JButton("Save");

	public KeyBindingsPanel(KeyBindings keyBindings) {
		this.keyBindings = keyBindings;
		keyMap = keyBindings.getKeyBindings();

		this.addKeyListener(keyListener);
		this.setLayout(new BorderLayout());
		drawList();
		scrollPane = new JScrollPane(mainActionPanel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(scrollPane, BorderLayout.CENTER);
		this.add(btnsPanel, BorderLayout.SOUTH);
		btnsPanel.setLayout(new GridLayout(1, 2));
		btnsPanel.add(defaultButton);
		defaultButton.addActionListener(buttonListener);
		btnsPanel.add(saveButton);
		saveButton.addActionListener(buttonListener);
	}

	private void drawList() {
		mainActionPanel.setLayout(new GridLayout(keyMap.size(), 1));
		length = keyMap.size();
		actionPanels = new JPanel[length];
		actionLabels = new JLabel[length];
		actionButtons = new JButton[length];

		for (int i = 0; i < keyMap.size(); i++) {
			Action action = actions[i];
			int keyCode = keyMap.get(action);

			actionPanels[i] = new JPanel();
			actionPanels[i].setLayout(new GridLayout(1, 3));
			actionPanels[i].add(new JLabel(action.name()));
			actionPanels[i].add(actionLabels[i] = new JLabel(KeyEvent.getKeyText(keyCode)));
			actionPanels[i].add(actionButtons[i] = new JButton("Change"));
			actionPanels[i].addKeyListener(keyListener);
			actionPanels[i].setFocusable(true);
			actionButtons[i].addActionListener(buttonListener);
			mainActionPanel.add(actionPanels[i]);
		}
	}

	private void updateLabels() {
		for (int i = 0; i < actionLabels.length; i++) {
			String labelText = KeyEvent.getKeyText(keyMap.get(actions[i]));
			int keyCode = keyMap.get(actions[i]);
			if (keyCode == -1) {
				labelText = "Unbinded";
			}
			actionLabels[i].setText(labelText);
		}
	}

	private void setEnableButtons(boolean b) {
		for (int i = 0; i < actionButtons.length; i++) {
			actionButtons[i].setEnabled(b);
		}
	}

	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < actionButtons.length; i++) {
				if (e.getSource() == actionButtons[i] && !listening) {
					actionIndex = i;
					actionPanels[i].requestFocus();
					listening = true;
					setEnableButtons(!listening);
				}
			}
			
			if(e.getSource() == defaultButton) {
				keyBindings.setDefaultKeyBindings();
				keyMap = keyBindings.getKeyBindings();
				updateLabels();
			}
			
			if(e.getSource() == saveButton) {
				
			}
		}
	}

	private class KeyListener extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if (listening == true) {
				int keyCode = e.getKeyCode();
				if (keyMap.containsValue(keyCode)) {
					for (int i = 0; i < keyMap.size(); i++) {
						if (keyCode == keyMap.get(actions[i])) {
							keyMap.put(actions[i], -1);
						}
					}
				}
				keyMap.replace(actions[actionIndex], keyCode);
				listening = false;
				setEnableButtons(!listening);
				keyBindings.setKeyBindings(keyMap);
				updateLabels();
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
