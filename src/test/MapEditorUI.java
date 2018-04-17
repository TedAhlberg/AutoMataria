package test;

import common.*;
import gameclient.*;
import gameclient.Window;
import gameobjects.*;
import gameserver.StartingPositions;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author Johannes Blüml
 */
public class MapEditorUI {
    private final JColorChooser colorChooser;
    private JPanel container, gameGridPanel, editModePanel, wallPanel, gameObjectPanel;
    private JTextField tfPlayerSpeedTick, tfMapName, visibleTimeTextField, spawnLimitTextField, spawnIntervalTextField;
    private JButton loadMapButton, saveMapButton, deleteMapButton, btnChangeWallColor, btnChangeWallBorderColor, clearMapButton, generatePositionsButton, deleteGameObjectButton, addNewGameObjectButton;
    private JComboBox<String> mapsComboBox, backgroundImageComboBox, gridSizeComboBox, musicTrackComboBox, editModeComboBox;
    private JRadioButton drawWallRadioButton, eraseWallRadioButton, addStartingPositionRadioButton, removeStartingPositionRadioButton;
    private JComboBox<Integer> playersComboBox;
    private JComboBox<SpecialGameObject> specialGameObjectComboBox;
    private JCheckBox randomSpawnCheckBox;
    private JButton saveGameObjectButton;
    private JComboBox<String> gameObjectComboBox;
    private JPanel gameObjectSettingsPanel;
    private JPanel pickupPanel;
    private JRadioButton setFixedPositionRadioButton;

    private Maps maps;
    private StartingPositions startingPositions = new StartingPositions();
    private ArrayList<SpecialGameObject> specialGameObjectsToSave = new ArrayList<>();
    private HashSet<Point> startingPositionsToSave = new HashSet<>();
    private GamePanel gamePanel;
    private GameMap currentMap = new GameMap();
    private PaintMode paintMode = PaintMode.AddStartPosition;
    private SpecialGameObject selectedObject;

    private HashMap<String, Dimension> gridSizes;

    public MapEditorUI(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        $$$setupUI$$$();

        addNewGameObjectButton.addActionListener(e -> {
            String selected = (String) gameObjectComboBox.getSelectedItem();
            try {
                Object object = Class.forName(selected).getConstructor().newInstance();
                SpecialGameObject specialGameObject = new SpecialGameObject((GameObject) object);
                specialGameObjectsToSave.add(specialGameObject);
                specialGameObjectComboBox.addItem(specialGameObject);
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e1) {
                e1.printStackTrace();
            }
        });
        deleteGameObjectButton.addActionListener(e -> {
            selectedObject = (SpecialGameObject) specialGameObjectComboBox.getSelectedItem();
            specialGameObjectsToSave.remove(selectedObject);
            specialGameObjectComboBox.removeItem(selectedObject);
        });
        saveGameObjectButton.addActionListener(e -> {
            selectedObject = (SpecialGameObject) specialGameObjectComboBox.getSelectedItem();
            SpecialGameObject newObject = new SpecialGameObject(
                    selectedObject.getGameObject(),
                    Integer.parseInt(spawnIntervalTextField.getText()),
                    Integer.parseInt(spawnLimitTextField.getText()),
                    randomSpawnCheckBox.isSelected(),
                    Integer.parseInt(visibleTimeTextField.getText())
            );
            specialGameObjectsToSave.add(newObject);
            specialGameObjectComboBox.addItem(newObject);
            specialGameObjectsToSave.remove(selectedObject);
            specialGameObjectComboBox.removeItem(selectedObject);
        });
        specialGameObjectComboBox.addActionListener(e -> {
            selectedObject = (SpecialGameObject) specialGameObjectComboBox.getSelectedItem();
            spawnIntervalTextField.setText("" + selectedObject.getSpawnInterval());
            spawnLimitTextField.setText("" + selectedObject.getSpawnLimit());
            visibleTimeTextField.setText("" + selectedObject.getVisibleTime());
            randomSpawnCheckBox.setSelected(selectedObject.isSpawnRandom());
            if (selectedObject.getGameObject() instanceof Wall) {
                Wall wall = (Wall) selectedObject.getGameObject();
                ((CardLayout) gameObjectSettingsPanel.getLayout()).show(gameObjectSettingsPanel, "WallCard");
                btnChangeWallColor.setBackground(wall.getColor());
                btnChangeWallBorderColor.setBackground(wall.getBorderColor());
            } else if (selectedObject.getGameObject() instanceof Pickup) {
                ((CardLayout) gameObjectSettingsPanel.getLayout()).show(gameObjectSettingsPanel, "PickupCard");
                setFixedPositionRadioButton.setSelected(true);
            } else {
                ((CardLayout) gameObjectSettingsPanel.getLayout()).show(gameObjectSettingsPanel, "EmptyCard");
            }
        });

        btnChangeWallColor.setBackground(Color.CYAN);
        btnChangeWallBorderColor.setBackground(Color.WHITE);

        clearMapButton.addActionListener(e -> clearMap());

        generatePositionsButton.addActionListener(e -> {
            startingPositionsToSave.clear();
            for (Point point : startingPositions.generate(currentMap.getGrid(), currentMap.getPlayers())) {
                startingPositionsToSave.add(new Point(point.x, point.y));
            }
            updateGamePanel();
        });

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
            this.gamePanel.setBackground((String) backgroundImageComboBox.getSelectedItem());
        });

        gridSizeComboBox.addActionListener(e -> {
            Dimension newGrid = gridSizes.get(gridSizeComboBox.getSelectedItem());
            currentMap.setGrid(newGrid);
            this.gamePanel.setGrid(newGrid);
        });
        playersComboBox.addActionListener(e -> {
            currentMap.setPlayers((int) playersComboBox.getSelectedItem());
        });


        setFixedPositionRadioButton.addActionListener(e -> paintMode = PaintMode.ChangeGameObjectPosition);
        drawWallRadioButton.addActionListener(e -> paintMode = PaintMode.DrawWall);
        eraseWallRadioButton.addActionListener(e -> paintMode = PaintMode.EraseWall);
        addStartingPositionRadioButton.addActionListener(e -> paintMode = PaintMode.AddStartPosition);
        removeStartingPositionRadioButton.addActionListener(e -> paintMode = PaintMode.RemoveStartPosition);

        btnChangeWallColor.addActionListener(e -> {
            colorChooser.setColor(btnChangeWallColor.getBackground());
            JColorChooser.createDialog(container, "Pick a color", true, colorChooser,
                    ok -> {
                        btnChangeWallColor.setBackground(colorChooser.getColor());
                        updateWallColors();
                    }, null
            ).setVisible(true);
        });
        btnChangeWallBorderColor.addActionListener(e -> {
            colorChooser.setColor(btnChangeWallBorderColor.getBackground());
            JColorChooser.createDialog(container, "Pick a color", true, colorChooser,
                    ok -> {
                        btnChangeWallBorderColor.setBackground(colorChooser.getColor());
                        updateWallColors();
                    }, null
            ).setVisible(true);
        });

        updateCurrentMap();
        updateGamePanel();
    }

    public static void main(String[] args) {
        Window window = new Window("Auto-Mataria Map Editor", null);
        GamePanel gamePanel = new GamePanel(window.getSize());
        window.setContentPane(new MapEditorUI(gamePanel).container);
        window.pack();
    }

    private void updateWallColors() {
        if (selectedObject == null) return;
        if (selectedObject.getGameObject() instanceof Wall) {
            ((Wall) selectedObject.getGameObject()).setColor(btnChangeWallColor.getBackground());
            ((Wall) selectedObject.getGameObject()).setBorderColor(btnChangeWallBorderColor.getBackground());
        }
    }

    private void clearMap() {
        specialGameObjectsToSave.clear();
        startingPositionsToSave.clear();
        currentMap.setName("");
        updateUIFields();
        updateGamePanel();
    }

    private void loadMap() {
        String name = (String) mapsComboBox.getSelectedItem();
        GameMap map = maps.get(name);
        if (map == null) return;
        currentMap = map;

        startingPositionsToSave.clear();
        startingPositionsToSave.addAll(Arrays.asList(map.getStartingPositions()));

        specialGameObjectsToSave.clear();
        specialGameObjectsToSave.addAll(Arrays.asList(map.getGameMapObjects()));

        updateUIFields();
        updateGamePanel();
    }

    private void saveMap() {
        updateCurrentMap();
        for (SpecialGameObject specialGameObject : currentMap.getGameMapObjects()) {
            GameObject gameObject = specialGameObject.getGameObject();
            if (gameObject.getId() != 0) {
                gameObject.setId(0);
            }
        }
        try {
            maps.save(currentMap);
            reloadMapList();
        } catch (Exception error) {
            JOptionPane.showMessageDialog(container, "Failed to save map.");
        }
    }

    private void deleteMap() {
        int result = JOptionPane.showConfirmDialog(container, "Are you sure you want to delete the map " + currentMap.getName() + "?");
        if (result == 0) {
            maps.remove(currentMap.getName());
            reloadMapList();
        }
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
        specialGameObjectComboBox.removeAllItems();
        for (SpecialGameObject specialGameObject : specialGameObjectsToSave) {
            specialGameObjectComboBox.addItem(specialGameObject);
        }
    }

    private void updateCurrentMap() {
        try {
            currentMap.setName(tfMapName.getText());
            currentMap.setBackground((String) backgroundImageComboBox.getSelectedItem());
            currentMap.setMusicTrack((String) musicTrackComboBox.getSelectedItem());
            currentMap.setPlayers((int) playersComboBox.getSelectedItem());
            currentMap.setPlayerSpeed(Double.parseDouble(tfPlayerSpeedTick.getText()));
            currentMap.setGrid(gridSizes.get(gridSizeComboBox.getSelectedItem()));
            currentMap.setStartingPositions(startingPositionsToSave.toArray(new Point[0]));
            currentMap.setGameMapObjects(specialGameObjectsToSave.toArray(new SpecialGameObject[0]));

        } catch (NumberFormatException error) {
            JOptionPane.showMessageDialog(container, "Please enter only numbers.");
        }
    }

    private void updateGamePanel() {
        if (gamePanel == null) return;
        gamePanel.setGrid(currentMap.getGrid());

        ArrayList<GameObject> gameObjects = new ArrayList<>();

        for (SpecialGameObject specialGameObject : specialGameObjectsToSave) {
            GameObject gameObject = specialGameObject.getGameObject();
            if (gameObject.getId() == 0) {
                gameObject.setId(ID.getNext());
            }
            gameObjects.add(specialGameObject.getGameObject());
        }

        Wall startingPositionsMarker = new Wall(Color.RED, Color.WHITE);
        startingPositionsMarker.setId(ID.getNext());
        for (Point point : startingPositionsToSave) {
            Rectangle startingPosition = new Rectangle(point);
            startingPosition.setLocation(startingPosition.x * Game.GRID_PIXEL_SIZE, startingPosition.y * Game.GRID_PIXEL_SIZE);
            startingPosition.setSize(new Dimension(Game.GRID_PIXEL_SIZE, Game.GRID_PIXEL_SIZE));
            startingPositionsMarker.add(startingPosition);
        }
        gameObjects.add(startingPositionsMarker);

        gamePanel.updateGameObjects(gameObjects);
    }

    private void reloadMapList() {
        mapsComboBox.removeAllItems();
        for (String name : maps.getMapList()) {
            mapsComboBox.addItem(name);
        }
    }

    private void createUIComponents() {
        specialGameObjectComboBox = new JComboBox<>();

        maps = Maps.getInstance();
        mapsComboBox = new JComboBox<>(maps.getMapList());

        backgroundImageComboBox = new JComboBox<>(Resources.getImageList());
        musicTrackComboBox = new JComboBox<>(Resources.getMusicList());

        gridSizes = new HashMap<>();
        gridSizes.put("Huge", new Dimension(25, 25));
        gridSizes.put("Large", new Dimension(50, 50));
        gridSizes.put("Normal", new Dimension(75, 75));
        gridSizes.put("Small", new Dimension(100, 100));
        gridSizes.put("Tiny", new Dimension(150, 150));
        gridSizeComboBox = new JComboBox<>(new String[]{
                "Huge", "Large", "Normal", "Small", "Tiny"
        });

        playersComboBox = new JComboBox<>(new Integer[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16});

        gameGridPanel = new JPanel(new GridLayout(1, 1));
        gameGridPanel.add(gamePanel);
        gamePanel.toggleDebugInfo();
        gamePanel.start(6);
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
                if (selectedObject != null && selectedObject.getGameObject() instanceof Wall && (paintMode == PaintMode.DrawWall || paintMode == PaintMode.EraseWall)) {
                    int width = Game.GRID_PIXEL_SIZE + endPoint.x - startPoint.x;
                    int height = Game.GRID_PIXEL_SIZE + endPoint.y - startPoint.y;
                    Rectangle rectangle = new Rectangle(startPoint.x, startPoint.y, width, height);
                    Wall wall = (Wall) selectedObject.getGameObject();
                    if (paintMode == PaintMode.DrawWall) {
                        wall.add(rectangle);
                    } else if (paintMode == PaintMode.EraseWall) {
                        wall.remove(rectangle);
                    }
                } else if (paintMode == PaintMode.AddStartPosition) {
                    startingPositionsToSave.add(endPoint);
                } else if (paintMode == PaintMode.RemoveStartPosition) {
                    startingPositionsToSave.remove(endPoint);
                } else if (selectedObject != null && paintMode == PaintMode.ChangeGameObjectPosition) {
                    selectedObject.getGameObject().setX(endPoint.x);
                    selectedObject.getGameObject().setY(endPoint.y);
                }
                updateGamePanel();
            }
        });
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
        container.setPreferredSize(new Dimension(2000, 2000));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        container.add(panel1, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Max Players");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Player Speed (grids/tick)");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label2, gbc);
        tfPlayerSpeedTick = new JTextField();
        tfPlayerSpeedTick.setText("0.25");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(tfPlayerSpeedTick, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Map Name");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label3, gbc);
        tfMapName = new JTextField();
        tfMapName.setColumns(15);
        tfMapName.setText("New Map Name");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(tfMapName, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer2, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer3, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer4, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Background Image");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label4, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Music Track");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label5, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer5, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(backgroundImageComboBox, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(musicTrackComboBox, gbc);
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(spacer6, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("Grid Size");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label6, gbc);
        final JPanel spacer7 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer7, gbc);
        final JPanel spacer8 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer8, gbc);
        final JPanel spacer9 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 17;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 20;
        panel1.add(spacer9, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(gridSizeComboBox, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(playersComboBox, gbc);
        deleteMapButton = new JButton();
        deleteMapButton.setText("Delete Map");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 18;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(deleteMapButton, gbc);
        saveMapButton = new JButton();
        saveMapButton.setText("Save Map");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 18;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(saveMapButton, gbc);
        final JPanel spacer10 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer10, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel2, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(mapsComboBox, gbc);
        loadMapButton = new JButton();
        loadMapButton.setText("LOAD MAP");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(loadMapButton, gbc);
        final JPanel spacer11 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(spacer11, gbc);
        clearMapButton = new JButton();
        clearMapButton.setText("CLEAR MAP");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(clearMapButton, gbc);
        final JPanel spacer12 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(spacer12, gbc);
        generatePositionsButton = new JButton();
        generatePositionsButton.setText("Generate Positions");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 14;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(generatePositionsButton, gbc);
        removeStartingPositionRadioButton = new JRadioButton();
        removeStartingPositionRadioButton.setText("Remove");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 14;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(removeStartingPositionRadioButton, gbc);
        addStartingPositionRadioButton = new JRadioButton();
        addStartingPositionRadioButton.setEnabled(true);
        addStartingPositionRadioButton.setSelected(true);
        addStartingPositionRadioButton.setText("Add");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 14;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(addStartingPositionRadioButton, gbc);
        gameObjectPanel = new JPanel();
        gameObjectPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 16;
        gbc.gridwidth = 4;
        panel1.add(gameObjectPanel, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gameObjectPanel.add(panel3, gbc);
        addNewGameObjectButton = new JButton();
        addNewGameObjectButton.setText("Add new");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(addNewGameObjectButton, gbc);
        gameObjectComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("gameobjects.Wall");
        defaultComboBoxModel1.addElement("gameobjects.pickups.SelfSpeedPickup");
        defaultComboBoxModel1.addElement("gameobjects.pickups.SlowEnemiesPickup");
        defaultComboBoxModel1.addElement("gameobjects.pickups.EraserPickup");
        gameObjectComboBox.setModel(defaultComboBoxModel1);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(gameObjectComboBox, gbc);
        final JPanel spacer13 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(spacer13, gbc);
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        specialGameObjectComboBox.setModel(defaultComboBoxModel2);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(specialGameObjectComboBox, gbc);
        final JPanel spacer14 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel3.add(spacer14, gbc);
        deleteGameObjectButton = new JButton();
        deleteGameObjectButton.setText("Delete");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(deleteGameObjectButton, gbc);
        gameObjectSettingsPanel = new JPanel();
        gameObjectSettingsPanel.setLayout(new CardLayout(0, 0));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gameObjectPanel.add(gameObjectSettingsPanel, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        gameObjectSettingsPanel.add(panel4, "EmptyCard");
        wallPanel = new JPanel();
        wallPanel.setLayout(new GridBagLayout());
        gameObjectSettingsPanel.add(wallPanel, "WallCard");
        final JPanel spacer15 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        wallPanel.add(spacer15, gbc);
        eraseWallRadioButton = new JRadioButton();
        eraseWallRadioButton.setText("Erase");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        wallPanel.add(eraseWallRadioButton, gbc);
        final JLabel label7 = new JLabel();
        label7.setText("Wall Color");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        wallPanel.add(label7, gbc);
        final JLabel label8 = new JLabel();
        label8.setText("Wall Border Color");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        wallPanel.add(label8, gbc);
        btnChangeWallColor = new JButton();
        btnChangeWallColor.setContentAreaFilled(false);
        btnChangeWallColor.setOpaque(true);
        btnChangeWallColor.setText("Change");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        wallPanel.add(btnChangeWallColor, gbc);
        btnChangeWallBorderColor = new JButton();
        btnChangeWallBorderColor.setContentAreaFilled(false);
        btnChangeWallBorderColor.setOpaque(false);
        btnChangeWallBorderColor.setText("Change");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        wallPanel.add(btnChangeWallBorderColor, gbc);
        drawWallRadioButton = new JRadioButton();
        drawWallRadioButton.setSelected(false);
        drawWallRadioButton.setText("Draw");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        wallPanel.add(drawWallRadioButton, gbc);
        final JLabel label9 = new JLabel();
        label9.setText("Click and drag on map");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        wallPanel.add(label9, gbc);
        final JPanel spacer16 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        wallPanel.add(spacer16, gbc);
        final JPanel spacer17 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        wallPanel.add(spacer17, gbc);
        final JPanel spacer18 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        wallPanel.add(spacer18, gbc);
        pickupPanel = new JPanel();
        pickupPanel.setLayout(new GridBagLayout());
        gameObjectSettingsPanel.add(pickupPanel, "PickupCard");
        setFixedPositionRadioButton = new JRadioButton();
        setFixedPositionRadioButton.setText("Set Fixed Position");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        pickupPanel.add(setFixedPositionRadioButton, gbc);
        final JPanel spacer19 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        pickupPanel.add(spacer19, gbc);
        final JPanel spacer20 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        pickupPanel.add(spacer20, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gameObjectPanel.add(panel5, gbc);
        final JLabel label10 = new JLabel();
        label10.setText("Spawn Interval");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel5.add(label10, gbc);
        final JPanel spacer21 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(spacer21, gbc);
        final JPanel spacer22 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel5.add(spacer22, gbc);
        final JLabel label11 = new JLabel();
        label11.setText("Spawn Limit");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel5.add(label11, gbc);
        final JLabel label12 = new JLabel();
        label12.setText("Visible Time");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        panel5.add(label12, gbc);
        final JLabel label13 = new JLabel();
        label13.setText("Random Spawn");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        panel5.add(label13, gbc);
        randomSpawnCheckBox = new JCheckBox();
        randomSpawnCheckBox.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel5.add(randomSpawnCheckBox, gbc);
        visibleTimeTextField = new JTextField();
        visibleTimeTextField.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 7;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(visibleTimeTextField, gbc);
        spawnLimitTextField = new JTextField();
        spawnLimitTextField.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(spawnLimitTextField, gbc);
        spawnIntervalTextField = new JTextField();
        spawnIntervalTextField.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(spawnIntervalTextField, gbc);
        final JPanel spacer23 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel5.add(spacer23, gbc);
        final JPanel spacer24 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel5.add(spacer24, gbc);
        final JPanel spacer25 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel5.add(spacer25, gbc);
        final JPanel spacer26 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel5.add(spacer26, gbc);
        saveGameObjectButton = new JButton();
        saveGameObjectButton.setText("Save");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 9;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(saveGameObjectButton, gbc);
        final JPanel spacer27 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel5.add(spacer27, gbc);
        final JPanel spacer28 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 15;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer28, gbc);
        gameGridPanel.setMinimumSize(new Dimension(0, 0));
        gameGridPanel.setPreferredSize(new Dimension(0, 0));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        container.add(gameGridPanel, gbc);
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(drawWallRadioButton);
        buttonGroup.add(eraseWallRadioButton);
        buttonGroup.add(addStartingPositionRadioButton);
        buttonGroup.add(removeStartingPositionRadioButton);
        buttonGroup.add(setFixedPositionRadioButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() { return container; }

    private enum PaintMode {
        DrawWall,
        EraseWall,
        AddStartPosition,
        RemoveStartPosition,
        ChangeGameObjectPosition
    }
}
