package test;

import common.*;
import gameclient.*;
import gameclient.Window;
import gameclient.interfaces.UserInterface;
import gameobjects.*;
import gameserver.StartingPositions;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author Johannes Blüml
 */
public class MapEditorUI {
    private final JColorChooser colorChooser;
    public JPanel container;
    private JPanel wallPanel, gameObjectPanel;
    private JTextField tfMapName, visibleTimeTextField, spawnLimitTextField, spawnIntervalTextField;
    private JButton saveMapButton, deleteMapButton, btnChangeWallColor, btnChangeWallBorderColor, clearMapButton, generatePositionsButton, deleteGameObjectButton, addNewGameObjectButton;
    private JComboBox<String> mapsComboBox, backgroundImageComboBox, gridSizeComboBox, musicTrackComboBox;
    private JRadioButton drawWallRadioButton, eraseWallRadioButton, addStartingPositionRadioButton, removeStartingPositionRadioButton;
    private JComboBox<Integer> playersComboBox;
    private JComboBox<SpecialGameObject> specialGameObjectComboBox;
    private JCheckBox randomSpawnCheckBox;
    private JButton saveGameObjectButton;
    private JComboBox<String> gameObjectComboBox;
    private JPanel gameObjectSettingsPanel;
    private JPanel pickupPanel;
    private JRadioButton setFixedPositionRadioButton;
    private JPanel gamePanelContainer;
    private JComboBox<Double> playerSpeedComboBox;
    private JButton exitButton;

    private Maps maps;
    private StartingPositions startingPositions = new StartingPositions();
    private ArrayList<SpecialGameObject> specialGameObjectsToSave = new ArrayList<>();
    private HashSet<Point> startingPositionsToSave = new HashSet<>();
    private GamePanel gamePanel;
    private GameMap currentMap;
    private PaintMode paintMode = PaintMode.AddStartPosition;
    private SpecialGameObject selectedObject;

    private HashMap<String, Dimension> gridSizes;
    private UserInterface userInterface;

    public MapEditorUI(UserInterface userInterface) {
        this.userInterface = userInterface;
        currentMap = new GameMap("NEW MAP", "", "", 4, 1.0, new Dimension(75, 75), null, null);
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
            if (selectedObject == null) return;
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

        mapsComboBox.addActionListener(e -> loadMap());
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

        exitButton.addActionListener(e -> {
            userInterface.changeToPreviousScreen();
        });

        updateCurrentMap();
        updateGamePanel();

        // Load first Map
        mapsComboBox.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        Window window = new Window("Auto-Mataria Map Editor", null);
        window.setContentPane(new MapEditorUI(null).container);
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
        playerSpeedComboBox.setSelectedItem(currentMap.getPlayerSpeedMultiplier());
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
            currentMap.setPlayerSpeedMultiplier((Double) playerSpeedComboBox.getSelectedItem());
            currentMap.setGrid(gridSizes.get(gridSizeComboBox.getSelectedItem()));
            currentMap.setStartingPositions(startingPositionsToSave.toArray(new Point[0]));
            currentMap.setGameMapObjects(specialGameObjectsToSave.toArray(new SpecialGameObject[0]));

        } catch (NumberFormatException error) {
            JOptionPane.showMessageDialog(container, "Please enter only numbers.");
        } catch (NullPointerException error) {
            error.printStackTrace();
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
            Rectangle startingPosition = new Rectangle(Utility.convertFromGrid(point));
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
        gridSizeComboBox.setSelectedItem(currentMap.getGrid());

        playersComboBox = new JComboBox<>(new Integer[]{
                2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16
        });
        playersComboBox.setSelectedItem(4);
        playerSpeedComboBox = new JComboBox<>(new Double[]{
                0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 2.0
        });
        playerSpeedComboBox.setSelectedItem(1.0);

        gamePanelContainer = new JPanel(new GridLayout(1, 1));
        gamePanel = new GamePanel();
        gamePanelContainer.add(gamePanel);
        gamePanel.toggleDebugInfo();
        gamePanel.start(15);
        gamePanel.addMouseListener(new MouseAdapter() {
            private MouseEvent start;

            public void mousePressed(MouseEvent e) {
                start = e;
            }

            public void mouseReleased(MouseEvent end) {
                int gamePanelSize = Math.min(gamePanel.getWidth(), gamePanel.getHeight());
                double spaceWidth = (double) gamePanelSize / currentMap.getGrid().width;
                double spaceHeight = (double) gamePanelSize / currentMap.getGrid().height;
                Point startGridPoint = new Point((int) (start.getX() / spaceWidth), (int) (start.getY() / spaceHeight));
                Point endGridPoint = new Point((int) (end.getX() / spaceWidth), (int) (end.getY() / spaceHeight));
                Point startPoint = Utility.convertFromGrid(startGridPoint);
                Point endPoint = Utility.convertFromGrid(endGridPoint);

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
                    startingPositionsToSave.add(endGridPoint);
                } else if (paintMode == PaintMode.RemoveStartPosition) {
                    startingPositionsToSave.remove(endGridPoint);
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
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        container.add(panel1, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Max Players");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Player Speed Multiplier");
        label2.setToolTipText("Increases (above 1.0) or decreases (below 1.0) all players speed on this map");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label2, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Map Name");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(label3, gbc);
        tfMapName = new JTextField();
        tfMapName.setColumns(15);
        tfMapName.setText("New Map Name");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.gridwidth = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(tfMapName, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(spacer1, gbc);
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
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(label5, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.gridwidth = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(backgroundImageComboBox, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.gridwidth = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(musicTrackComboBox, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("Grid Size");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(label6, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.gridwidth = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(gridSizeComboBox, gbc);
        deleteMapButton = new JButton();
        deleteMapButton.setText("Delete Map");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(deleteMapButton, gbc);
        saveMapButton = new JButton();
        saveMapButton.setText("Save Map");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 12;
        gbc.gridwidth = 7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(saveMapButton, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 9;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel2, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 3.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel2.add(mapsComboBox, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(spacer2, gbc);
        clearMapButton = new JButton();
        clearMapButton.setText("NEW MAP");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel2.add(clearMapButton, gbc);
        gameObjectPanel = new JPanel();
        gameObjectPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 9;
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
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel3.add(addNewGameObjectButton, gbc);
        gameObjectComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("gameobjects.Wall");
        defaultComboBoxModel1.addElement("gameobjects.pickups.EraserPickup");
        defaultComboBoxModel1.addElement("gameobjects.pickups.ReversePickup");
        defaultComboBoxModel1.addElement("gameobjects.pickups.SelfSlowPickup");
        defaultComboBoxModel1.addElement("gameobjects.pickups.SelfSpeedPickup");
        defaultComboBoxModel1.addElement("gameobjects.pickups.SlowEnemiesPickup");
        defaultComboBoxModel1.addElement("gameobjects.pickups.SpeedEnemiesPickup");
        gameObjectComboBox.setModel(defaultComboBoxModel1);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel3.add(gameObjectComboBox, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(spacer3, gbc);
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        specialGameObjectComboBox.setModel(defaultComboBoxModel2);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel3.add(specialGameObjectComboBox, gbc);
        deleteGameObjectButton = new JButton();
        deleteGameObjectButton.setText("Delete");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel3.add(deleteGameObjectButton, gbc);
        final JSeparator separator1 = new JSeparator();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(separator1, gbc);
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
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        wallPanel.add(spacer4, gbc);
        eraseWallRadioButton = new JRadioButton();
        eraseWallRadioButton.setText("Erase");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        wallPanel.add(eraseWallRadioButton, gbc);
        final JLabel label7 = new JLabel();
        label7.setText("Wall Color");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 2.0;
        gbc.anchor = GridBagConstraints.WEST;
        wallPanel.add(label7, gbc);
        final JLabel label8 = new JLabel();
        label8.setText("Wall Border Color");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 2.0;
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
        gbc.weightx = 2.0;
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
        gbc.weightx = 2.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        wallPanel.add(btnChangeWallBorderColor, gbc);
        drawWallRadioButton = new JRadioButton();
        drawWallRadioButton.setSelected(false);
        drawWallRadioButton.setText("Draw");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        wallPanel.add(drawWallRadioButton, gbc);
        final JLabel label9 = new JLabel();
        label9.setText("Click and drag on map");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 2.0;
        gbc.anchor = GridBagConstraints.WEST;
        wallPanel.add(label9, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        wallPanel.add(spacer5, gbc);
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        wallPanel.add(spacer6, gbc);
        final JPanel spacer7 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        wallPanel.add(spacer7, gbc);
        pickupPanel = new JPanel();
        pickupPanel.setLayout(new GridBagLayout());
        gameObjectSettingsPanel.add(pickupPanel, "PickupCard");
        setFixedPositionRadioButton = new JRadioButton();
        setFixedPositionRadioButton.setText("Set Fixed Position");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        pickupPanel.add(setFixedPositionRadioButton, gbc);
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
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel5.add(label10, gbc);
        final JPanel spacer8 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(spacer8, gbc);
        final JLabel label11 = new JLabel();
        label11.setText("Spawn Limit");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel5.add(label11, gbc);
        final JLabel label12 = new JLabel();
        label12.setText("Visible Time");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel5.add(label12, gbc);
        final JLabel label13 = new JLabel();
        label13.setText("Random Spawn");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel5.add(label13, gbc);
        randomSpawnCheckBox = new JCheckBox();
        randomSpawnCheckBox.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel5.add(randomSpawnCheckBox, gbc);
        visibleTimeTextField = new JTextField();
        visibleTimeTextField.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel5.add(visibleTimeTextField, gbc);
        spawnLimitTextField = new JTextField();
        spawnLimitTextField.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel5.add(spawnLimitTextField, gbc);
        spawnIntervalTextField = new JTextField();
        spawnIntervalTextField.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel5.add(spawnIntervalTextField, gbc);
        saveGameObjectButton = new JButton();
        saveGameObjectButton.setText("Save GameObject Settings");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel5.add(saveGameObjectButton, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 7;
        gbc.gridwidth = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(playersComboBox, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 8;
        gbc.gridwidth = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(playerSpeedComboBox, gbc);
        final JSeparator separator2 = new JSeparator();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 9;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(separator2, gbc);
        generatePositionsButton = new JButton();
        generatePositionsButton.setText("Generate");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 9;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(generatePositionsButton, gbc);
        removeStartingPositionRadioButton = new JRadioButton();
        removeStartingPositionRadioButton.setText("Remove");
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(removeStartingPositionRadioButton, gbc);
        addStartingPositionRadioButton = new JRadioButton();
        addStartingPositionRadioButton.setEnabled(true);
        addStartingPositionRadioButton.setSelected(true);
        addStartingPositionRadioButton.setText("Add");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(addStartingPositionRadioButton, gbc);
        final JPanel spacer9 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 9;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(spacer9, gbc);
        final JPanel spacer10 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 9;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(spacer10, gbc);
        final JLabel label14 = new JLabel();
        label14.setText("Starting Positions");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label14, gbc);
        final JSeparator separator3 = new JSeparator();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 9;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(separator3, gbc);
        final JPanel spacer11 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 10;
        panel1.add(spacer11, gbc);
        exitButton = new JButton();
        exitButton.setText("EXIT FROM MAP EDITOR");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 9;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(exitButton, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        container.add(gamePanelContainer, gbc);
        final JPanel spacer12 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 10;
        container.add(spacer12, gbc);
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
