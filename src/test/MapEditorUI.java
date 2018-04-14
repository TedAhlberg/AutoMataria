package test;

import common.GameMap;
import common.SpecialGameObject;
import gameclient.*;
import gameobjects.GameObject;
import gameobjects.Wall;
import common.Maps;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

/**
 * @author Johannes Blüml
 */
public class MapEditorUI {
    private final JColorChooser colorChooser;
    private Maps maps;
    private JPanel container;
    private JComboBox<String> mapsComboBox;
    private JButton loadMapButton;
    private JButton btnChangeWallColor;
    private JTextField tfPlayerSpeedTick;
    private JButton btnChangeWallBorderColor;
    private JTextField tfMapName;
    private JButton saveMapButton;
    private JButton deleteMapButton;
    private JComboBox<String> backgroundImageComboBox;
    private JComboBox<String> musicTrackComboBox;
    private JPanel gameGridPanel;
    private JRadioButton drawWallRadioButton;
    private JRadioButton eraseWallRadioButton;
    private JComboBox<String> gridSizeComboBox;
    private JComboBox<Integer> playersComboBox;

    private GamePanel gamePanel = new GamePanel();
    private GameMap currentMap = new GameMap();
    private PaintMode paintMode = PaintMode.DrawWall;
    private Wall wall;

    private HashMap<String, Dimension> gridSizes;

    public MapEditorUI() {
        $$$setupUI$$$();

        colorChooser = new JColorChooser();
        colorChooser.setPreviewPanel(new JPanel());
        for (AbstractColorChooserPanel chooserPanel : colorChooser.getChooserPanels()) {
            if (!chooserPanel.getDisplayName().equals("RGB")) {
                colorChooser.removeChooserPanel(chooserPanel);
            }
        }

        loadMapButton.addActionListener(e -> loadMap());
        saveMapButton.addActionListener(e -> saveMap());
        deleteMapButton.addActionListener(e -> deleteMap());

        backgroundImageComboBox.addActionListener(e -> {
            gamePanel.setBackground((String) backgroundImageComboBox.getSelectedItem());
        });

        gridSizeComboBox.addActionListener(e -> {
            updateCurrentMap();
            updateGamePanel();
        });

        drawWallRadioButton.addActionListener(e -> paintMode = PaintMode.DrawWall);
        eraseWallRadioButton.addActionListener(e -> paintMode = PaintMode.EraseWall);

        btnChangeWallColor.addActionListener(e -> {
            colorChooser.setColor(btnChangeWallColor.getForeground());
            JColorChooser.createDialog(container, "Pick a color", true, colorChooser,
                    ok -> {
                        btnChangeWallColor.setForeground(colorChooser.getColor());
                        updateWallColors();
                    }, null
            ).setVisible(true);
        });
        btnChangeWallBorderColor.addActionListener(e -> {
            colorChooser.setColor(btnChangeWallBorderColor.getForeground());
            JColorChooser.createDialog(container, "Pick a color", true, colorChooser,
                    ok -> {
                        btnChangeWallBorderColor.setForeground(colorChooser.getColor());
                        updateWallColors();
                    }, null
            ).setVisible(true);
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MapEditorUI");
        frame.setContentPane(new MapEditorUI().container);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void loadMap() {
        String name = (String) mapsComboBox.getSelectedItem();
        GameMap map = maps.get(name);
        if (map == null) return;
        currentMap = map;
        updateUIFields();
        updateGamePanel();
    }

    private void saveMap() {
        updateCurrentMap();
        try {
            maps.save(currentMap);
            reloadMapList();
        } catch (Exception error) {
            JOptionPane.showMessageDialog(null, "Failed to save map.");
        }
    }

    private void deleteMap() {
        int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the map " + currentMap.getName() + "?");
        if (result == 0) {
            maps.remove(currentMap.getName());
            reloadMapList();
        }
    }

    private void updateWallColors() {
        wall.setColor(btnChangeWallColor.getForeground());
        wall.setBorderColor(btnChangeWallBorderColor.getForeground());
    }

    private void updateUIFields() {
        tfMapName.setText(currentMap.getName());
        backgroundImageComboBox.setSelectedItem(currentMap.getBackground());
        musicTrackComboBox.setSelectedItem(currentMap.getMusicTrack());
        playersComboBox.setSelectedItem(currentMap.getPlayers());
        gridSizes.forEach((key, value) -> {
            if (value.equals(currentMap.getGrid())) {
                gridSizeComboBox.setSelectedItem(key);
            }
        });
        tfPlayerSpeedTick.setText(Double.toString(currentMap.getPlayerSpeed() / (double) Game.GRID_PIXEL_SIZE));
    }

    private void updateCurrentMap() {
        try {
            currentMap.setName(tfMapName.getText());
            currentMap.setBackground((String) backgroundImageComboBox.getSelectedItem());
            currentMap.setMusicTrack((String) musicTrackComboBox.getSelectedItem());
            currentMap.setPlayers((int) playersComboBox.getSelectedItem());
            currentMap.setPlayerSpeed(Double.parseDouble(tfPlayerSpeedTick.getText()));
            currentMap.setGrid(gridSizes.get(gridSizeComboBox.getSelectedItem()));

        } catch (NumberFormatException error) {
            JOptionPane.showMessageDialog(null, "Please enter only numbers.");
        }
    }

    private void updateGamePanel() {
        int width = currentMap.getGrid().width * Game.GRID_PIXEL_SIZE;
        int height = currentMap.getGrid().height * Game.GRID_PIXEL_SIZE;
        double scale = Math.min((double) gamePanel.getWidth() / width, (double) gamePanel.getHeight() / height);
        gamePanel.setScale(scale);
        gamePanel.setGrid(currentMap.getGrid());
        if (currentMap.getGameMapObjects() != null) {
            ArrayList<GameObject> gameObjects = new ArrayList<>();
            for (SpecialGameObject specialGameObject : currentMap.getGameMapObjects()) {
                gameObjects.add(specialGameObject.getGameObject());
                if (specialGameObject.getGameObject() instanceof Wall) {
                    wall = (Wall) specialGameObject.getGameObject();
                }
            }
            gamePanel.updateGameObjects(gameObjects);
        }
    }

    private void reloadMapList() {
        mapsComboBox.removeAllItems();
        for (String name : maps.getMapList()) {
            mapsComboBox.addItem(name);
        }
    }

    private void createUIComponents() {
        maps = Maps.getInstance();
        mapsComboBox = new JComboBox<>(maps.getMapList());
        backgroundImageComboBox = new JComboBox<>(Resources.getImageList());
        musicTrackComboBox = new JComboBox<>(Resources.getMusicList());

        gridSizes = new HashMap<>();
        gridSizes.put("Tiny", new Dimension(25, 25));
        gridSizes.put("Small", new Dimension(50, 50));
        gridSizes.put("Normal", new Dimension(75, 75));
        gridSizes.put("Large", new Dimension(100, 100));
        gridSizes.put("Huge", new Dimension(150, 150));
        gridSizeComboBox = new JComboBox<>(new String[]{
                "Tiny", "Small", "Normal", "Large", "Huge"
        });

        playersComboBox = new JComboBox<>(new Integer[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16});

        gameGridPanel = new JPanel();
        gamePanel.toggleDebugInfo();
        gamePanel.setPreferredSize(new Dimension(800, 800));
        gamePanel.setSize(new Dimension(800, 800));
        gamePanel.start(1.0, 6);
        gamePanel.addMouseListener(new MouseAdapter() {
            private MouseEvent start;

            public void mousePressed(MouseEvent e) {
                start = e;
            }

            public void mouseReleased(MouseEvent end) {
                int spaceWidth = gamePanel.getSize().width / currentMap.getGrid().width;
                int spaceHeight = gamePanel.getSize().height / currentMap.getGrid().height;
                Point startPoint = new Point(start.getX() / spaceWidth, start.getY() / spaceHeight);
                Point endPoint = new Point(end.getX() / spaceWidth, end.getY() / spaceHeight);
                startPoint.setLocation(startPoint.x * Game.GRID_PIXEL_SIZE, startPoint.y * Game.GRID_PIXEL_SIZE);
                endPoint.setLocation(endPoint.x * Game.GRID_PIXEL_SIZE, endPoint.y * Game.GRID_PIXEL_SIZE);
                int width = Game.GRID_PIXEL_SIZE + endPoint.x - startPoint.x;
                int height = Game.GRID_PIXEL_SIZE + endPoint.y - startPoint.y;
                Rectangle rectangle = new Rectangle(startPoint.x, startPoint.y, width, height);
                if (wall == null) {
                    wall = new Wall();
                    SpecialGameObject[] current = currentMap.getGameMapObjects();
                    if (current == null) {
                        currentMap.setGameMapObjects(new SpecialGameObject[]{new SpecialGameObject(wall, 0, 0, false, 0)});
                    } else {
                        SpecialGameObject[] changed = Arrays.copyOf(current, current.length + 1);
                        changed[changed.length - 1] = new SpecialGameObject(wall, 0, 0, false, 0);
                        currentMap.setGameMapObjects(changed);
                    }
                }
                if (paintMode == PaintMode.DrawWall) {
                    ArrayList<GameObject> gameObjects = new ArrayList<>();
                    wall.add(rectangle);
                    gameObjects.add(wall);
                    gamePanel.updateGameObjects(gameObjects);
                } else if (paintMode == PaintMode.EraseWall) {
                    ArrayList<GameObject> gameObjects = new ArrayList<>();
                    wall.remove(rectangle);
                    gameObjects.add(wall);
                    gamePanel.updateGameObjects(gameObjects);
                }
            }
        });
        gameGridPanel.add(gamePanel);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        container = new JPanel();
        container.setLayout(new GridBagLayout());
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        container.add(panel1, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(mapsComboBox, gbc);
        loadMapButton = new JButton();
        loadMapButton.setText("Load Map");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(loadMapButton, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(spacer1, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        container.add(panel2, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Max Players");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        panel2.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Player Speed (grids/tick)");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.anchor = GridBagConstraints.WEST;
        panel2.add(label2, gbc);
        tfPlayerSpeedTick = new JTextField();
        tfPlayerSpeedTick.setText("0.25");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 11;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(tfPlayerSpeedTick, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Wall Color");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 17;
        gbc.anchor = GridBagConstraints.WEST;
        panel2.add(label3, gbc);
        btnChangeWallColor = new JButton();
        btnChangeWallColor.setText("Change");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 17;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(btnChangeWallColor, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Wall Border Color");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 19;
        gbc.anchor = GridBagConstraints.WEST;
        panel2.add(label4, gbc);
        btnChangeWallBorderColor = new JButton();
        btnChangeWallBorderColor.setText("Change");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 19;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(btnChangeWallBorderColor, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Map Name");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel2.add(label5, gbc);
        tfMapName = new JTextField();
        tfMapName.setColumns(15);
        tfMapName.setText("New Map Name");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(tfMapName, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(spacer2, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel2.add(spacer3, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel2.add(spacer4, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel2.add(spacer5, gbc);
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel2.add(spacer6, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("Background Image");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel2.add(label6, gbc);
        final JLabel label7 = new JLabel();
        label7.setText("Music Track");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        panel2.add(label7, gbc);
        final JPanel spacer7 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel2.add(spacer7, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(backgroundImageComboBox, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(musicTrackComboBox, gbc);
        final JPanel spacer8 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(spacer8, gbc);
        saveMapButton = new JButton();
        saveMapButton.setText("Save Map");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 21;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(saveMapButton, gbc);
        drawWallRadioButton = new JRadioButton();
        drawWallRadioButton.setSelected(true);
        drawWallRadioButton.setText("Draw Wall");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 13;
        gbc.anchor = GridBagConstraints.WEST;
        panel2.add(drawWallRadioButton, gbc);
        eraseWallRadioButton = new JRadioButton();
        eraseWallRadioButton.setText("Erase Wall");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 15;
        gbc.anchor = GridBagConstraints.WEST;
        panel2.add(eraseWallRadioButton, gbc);
        final JLabel label8 = new JLabel();
        label8.setText("Grid Size");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        panel2.add(label8, gbc);
        deleteMapButton = new JButton();
        deleteMapButton.setText("Delete Map");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 23;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(deleteMapButton, gbc);
        final JPanel spacer9 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 22;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel2.add(spacer9, gbc);
        final JPanel spacer10 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel2.add(spacer10, gbc);
        final JPanel spacer11 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel2.add(spacer11, gbc);
        final JPanel spacer12 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 14;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel2.add(spacer12, gbc);
        final JPanel spacer13 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 16;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel2.add(spacer13, gbc);
        final JPanel spacer14 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 18;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel2.add(spacer14, gbc);
        final JPanel spacer15 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 20;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel2.add(spacer15, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(gridSizeComboBox, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(playersComboBox, gbc);
        gameGridPanel.setMinimumSize(new Dimension(800, 800));
        gameGridPanel.setPreferredSize(new Dimension(800, 800));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        container.add(gameGridPanel, gbc);
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(drawWallRadioButton);
        buttonGroup.add(eraseWallRadioButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() { return container; }

    private enum PaintMode {
        DrawWall,
        EraseWall
    }
}
