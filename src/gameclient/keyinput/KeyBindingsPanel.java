package gameclient.keyinput;

import common.Action;
import gameclient.interfaces.AMButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

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
    private AMButton[] actionButtons;

    private JPanel btnsPanel = new JPanel();
    private AMButton defaultButton = new AMButton("DEFAULT");
    private AMButton saveButton = new AMButton("SAVE");

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

    public static void main(String[] args) {
        KeyBindings test = new KeyBindings();
        KeyBindingsPanel test1 = new KeyBindingsPanel(test);
        JFrame frame = new JFrame();

        frame.add(test1);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void drawList() {
        mainActionPanel.setLayout(new GridLayout(keyMap.size(), 1, 0, 5));
        length = keyMap.size();
        actionPanels = new JPanel[length];
        actionLabels = new JLabel[length];
        actionButtons = new AMButton[length];

        for (int i = 0; i < keyMap.size(); i++) {
            Action action = actions[i];
            System.out.println(action);
            int keyCode = keyMap.get(action);

            actionPanels[i] = new JPanel();
            actionPanels[i].setLayout(new GridLayout(1, 3, 15, 0));
            actionPanels[i].add(new JLabel(action.name()));
            actionPanels[i].add(actionLabels[i] = new JLabel(KeyEvent.getKeyText(keyCode)));
            actionPanels[i].add(actionButtons[i] = new AMButton("CHANGE"));
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
                labelText = "UNBOUND";
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

            if (e.getSource() == defaultButton) {
                keyBindings.setDefaultKeyBindings();
                keyMap = keyBindings.getKeyBindings();
                updateLabels();
            }

            if (e.getSource() == saveButton) {

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
}
