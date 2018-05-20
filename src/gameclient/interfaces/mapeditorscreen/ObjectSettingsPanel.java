package gameclient.interfaces.mapeditorscreen;

import common.SpecialGameObject;
import gameclient.Resources;
import gameobjects.*;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.*;

/**
 * @author Johannes Blüml
 */
public class ObjectSettingsPanel extends JComponent {
    private final MapEditorScreen mapEditorScreen;
    private final JColorChooser colorChooser;
    private GameObject gameObject;
    private SpecialGameObject specialGameObject;

    private JSlider spawnLimit, visibleTime, respawnInterval, activeTime;
    private JCheckBox randomSpawn;
    private JButton deleteButton;

    ObjectSettingsPanel(MapEditorScreen mapEditorScreen) {
        this.mapEditorScreen = mapEditorScreen;

        colorChooser = new JColorChooser();
        //colorChooser.setPreviewPanel(new JPanel());
        for (AbstractColorChooserPanel chooserPanel : colorChooser.getChooserPanels()) {
            if (!chooserPanel.getDisplayName().equals("RGB")) {
                //colorChooser.removeChooserPanel(chooserPanel);
            }
        }
    }

    GameObject getGameObject() {
        return gameObject;
    }

    private void createLayout() {
        removeAll();
        setLayout(new GridBagLayout());

        JLabel titleLabel = new JLabel(gameObject.getClass().getSimpleName().toUpperCase());
        titleLabel.setForeground(new Color(0x9b59b6));
        titleLabel.setFont(Resources.defaultFont.deriveFont(20.0f));
        addRow(titleLabel);

        if (gameObject instanceof Wall) {

            JButton wallColorButton = new JButton("CHANGE WALL COLOR");
            wallColorButton.setBackground(((Wall) gameObject).getColor());
            JButton wallBorderColorButton = new JButton("CHANGE BORDER COLOR");
            wallBorderColorButton.setBackground(((Wall) gameObject).getBorderColor());
            addRow(wallColorButton, wallBorderColorButton);

            wallColorButton.addActionListener(e -> {
                colorChooser.setColor(wallColorButton.getBackground());
                JColorChooser.createDialog(this, "Pick a color", true, colorChooser,
                        ok -> {
                            wallColorButton.setBackground(colorChooser.getColor());
                            ((Wall) gameObject).setColor(colorChooser.getColor());
                        }, null
                ).setVisible(true);
            });
            wallBorderColorButton.addActionListener(e -> {
                colorChooser.setColor(wallBorderColorButton.getBackground());
                JColorChooser.createDialog(this, "Pick a color", true, colorChooser,
                        ok -> {
                            wallBorderColorButton.setBackground(colorChooser.getColor());
                            ((Wall) gameObject).setBorderColor(colorChooser.getColor());
                        }, null
                ).setVisible(true);
            });

        } else if (gameObject instanceof Pickup) {

            JLabel activeTimeLabel = new JLabel("ACTIVE TIME: 0");
            activeTime = new JSlider(0, 30000, 0);
            activeTime.setSnapToTicks(true);
            activeTime.setMajorTickSpacing(500);
            activeTime.setPaintTicks(true);
            activeTime.addChangeListener(e -> {
                activeTimeLabel.setText("ACTIVE TIME: " + (activeTime.getValue() / 1000.0) + " SECONDS");
                ((Pickup) gameObject).setActiveTime(activeTime.getValue());
            });
            activeTime.setValue(((Pickup) gameObject).getActiveTime());
            addCombinedRows(activeTimeLabel, activeTime);
        }

        JLabel visibleTimeLabel = new JLabel("VISIBLE TIME: 0");
        visibleTime = new JSlider(0, 300000, 0);
        visibleTime.setSnapToTicks(true);
        visibleTime.setMajorTickSpacing(1000);
        visibleTime.addChangeListener(e -> {
            visibleTimeLabel.setText("VISIBLE TIME: " + (visibleTime.getValue() / 1000) + " SECONDS");
            specialGameObject.setVisibleTime(visibleTime.getValue());
        });
        visibleTime.setValue(specialGameObject.getVisibleTime());
        addCombinedRows(visibleTimeLabel, visibleTime);

        JLabel respawnIntervalLabel = new JLabel("RESPAWN TIME: 0");
        respawnInterval = new JSlider(0, 300000, 0);
        visibleTime.setSnapToTicks(true);
        visibleTime.setMajorTickSpacing(1000);
        respawnInterval.addChangeListener(e -> {
            respawnIntervalLabel.setText("RESPAWN TIME: " + (respawnInterval.getValue() / 1000) + " SECONDS");
            specialGameObject.setSpawnInterval(respawnInterval.getValue());
        });
        respawnInterval.setValue(specialGameObject.getSpawnInterval());
        addCombinedRows(respawnIntervalLabel, respawnInterval);

        if (gameObject instanceof Pickup) {
            randomSpawn = new JCheckBox("SPAWNS AT CHOSEN POSITION");
            randomSpawn.addChangeListener(e -> {
                if (randomSpawn.isSelected()) {
                    randomSpawn.setText("SPAWNS AT RANDOM POSITIONS");
                } else {
                    randomSpawn.setText("SPAWNS AT CHOSEN POSITION");
                }
                specialGameObject.setSpawnRandom(randomSpawn.isSelected());
            });
            randomSpawn.setSelected(specialGameObject.isSpawnRandom());
            addRow(randomSpawn);
        }

        JLabel spawnLimitLabel = new JLabel("SPAWN LIMIT: 0");
        spawnLimit = new JSlider(0, 50, 0);
        spawnLimit.setMajorTickSpacing(5);
        spawnLimit.setMinorTickSpacing(1);
        spawnLimit.setPaintTicks(true);
        spawnLimit.addChangeListener(e -> {
            spawnLimitLabel.setText("SPAWN LIMIT: " + spawnLimit.getValue());
            specialGameObject.setSpawnLimit(spawnLimit.getValue());
        });
        spawnLimit.setValue(specialGameObject.getSpawnLimit());
        addCombinedRows(spawnLimitLabel, spawnLimit);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridx = 0;
        gbc.weighty = 1;
        add(Box.createVerticalStrut(0), gbc);

        deleteButton = new JButton("DELETE OBJECT");
        deleteButton.addActionListener(e -> {
            mapEditorScreen.deleteObject(specialGameObject);
        });
        addRow(deleteButton);

        invalidate();
        repaint();
    }

    private void addRow(JComponent component) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipady = 10;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridx = 0;
        add(component, gbc);
    }

    private void addCombinedRows(JComponent component, JComponent component2) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipady = 10;
        gbc.insets = new Insets(10, 10, 0, 10);
        gbc.weightx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridx = 0;
        add(component, gbc);
        gbc.insets = new Insets(0, 10, 10, 10);
        add(component2, gbc);
    }

    private void addRow(JComponent component, JComponent component2) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipady = 10;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1;
        gbc.gridx = 0;
        add(component, gbc);
        gbc.gridx = 1;
        add(component2, gbc);
    }

    public void update(SpecialGameObject specialGameObject) {
        if (specialGameObject == null) {
            clear();
            return;
        }
        if (specialGameObject.equals(this.specialGameObject)) {
            return;
        }
        this.specialGameObject = specialGameObject;
        this.gameObject = specialGameObject.getGameObject();

        createLayout();
    }

    public void clear() {
        specialGameObject = null;
        gameObject = null;
        removeAll();
        invalidate();
        repaint();
    }
}
