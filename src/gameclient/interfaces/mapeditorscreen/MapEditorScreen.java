package gameclient.interfaces.mapeditorscreen;

import common.*;
import gameclient.Game;
import gameclient.interfaces.UserInterface;
import gameclient.interfaces.UserInterfaceScreen;
import gameclient.interfaces.gamescreen.GamePanel;
import gameobjects.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author Johannes Blüml
 */
public class MapEditorScreen extends JComponent implements UserInterfaceScreen {
    private final MapTopBarPanel mapTopBarPanel;
    private final MapSettingsPanel mapSettingsPanel;
    private final ObjectSettingsPanel objectSettingsPanel;
    private final MapListPanel mapListPanel;
    private final GamePanel gamePanel;
    private final UserInterface userInterface;
    private final ArrayList<SpecialGameObject> specialGameObjects = new ArrayList<>();
    private final ArrayList<GameObject> gameObjects = new ArrayList<>();
    private Wall startPositionMarker;
    private JTabbedPane sidePanel;
    private GameMap currentMap;

    public MapEditorScreen(UserInterface userInterface) {
        this.userInterface = userInterface;
        setLayout(new GridBagLayout());
        gamePanel = new GamePanel();
        mapTopBarPanel = new MapTopBarPanel(this);
        mapListPanel = new MapListPanel(this::changeMap, this::deleteMap);
        mapSettingsPanel = new MapSettingsPanel(this);
        objectSettingsPanel = new ObjectSettingsPanel(this);

        createLayout();
        startGamePanelListener();
        changeMap(null);
    }

    private void deleteMap(String mapName) {
        int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the map " + mapName + "?");
        if (result == 0) {
            Maps.getInstance().remove(mapName);
            mapListPanel.reload();
        }
    }

    private void changeMap(String mapName) {
        if (mapName == null || mapName.equals("")) {
            currentMap = null;
        } else {
            currentMap = Maps.getInstance().get(mapName);
        }
        if (currentMap == null) {
            currentMap = new GameMap("NEW MAP", "Stars.png", null, 5, 1.0, Utility.getGridFromName("Large"), null, null);
        }

        mapSettingsPanel.setCurrentMap(currentMap);
        mapTopBarPanel.setMapName(currentMap.getName());
        gamePanel.setBackground(currentMap.getBackground());
        gamePanel.setGrid(currentMap.getGrid());
        objectSettingsPanel.clear();

        gameObjects.clear();
        specialGameObjects.clear();

        specialGameObjects.addAll(Arrays.asList(currentMap.getGameMapObjects()));

        specialGameObjects.forEach(specialGameObject -> {
            GameObject gameObject = specialGameObject.getGameObject();
            gameObject.setId(ID.getNext());
            gameObjects.add(gameObject);
        });

        if (currentMap.getStartingPositions().length > 0) {
            enableStartingPositions();
        } else {
            disableStartingPositions();
        }

        sidePanel.setSelectedComponent(mapSettingsPanel);
        updateGamePanel();
    }

    private void updateGamePanel() {
        gamePanel.updateGameObjects(gameObjects);
    }

    private void createLayout() {
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = .9;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(gamePanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = .1;
        gbc.fill = GridBagConstraints.BOTH;
        add(mapTopBarPanel, gbc);

        mapListPanel.reload();
        sidePanel = new JTabbedPane();
        sidePanel.add("MAP LIST", mapListPanel);
        sidePanel.add("MAP SETTINGS", mapSettingsPanel);
        sidePanel.add("OBJECT SETTINGS", objectSettingsPanel);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = .1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(sidePanel, gbc);
    }

    public void onScreenActive() {
        gamePanel.setGameState(GameState.Running);
        gamePanel.setBackground(currentMap.getBackground());
        gamePanel.setGrid(currentMap.getGrid());
        gamePanel.start(30);
    }

    public void onScreenInactive() {
        gamePanel.stop();
    }

    private void startGamePanelListener() {
        gamePanel.addMouseListener(new MouseAdapter() {
            private MouseEvent start;

            public void mousePressed(MouseEvent start) {
                super.mousePressed(start);
                this.start = start;
            }

            public void mouseReleased(MouseEvent end) {
                super.mouseReleased(end);
                int gamePanelSize = Math.min(gamePanel.getWidth(), gamePanel.getHeight());
                double spaceWidth = (double) gamePanelSize / currentMap.getGrid().width;
                double spaceHeight = (double) gamePanelSize / currentMap.getGrid().height;
                Point startGridPoint = new Point((int) (start.getX() / spaceWidth), (int) (start.getY() / spaceHeight));
                Point endGridPoint = new Point((int) (end.getX() / spaceWidth), (int) (end.getY() / spaceHeight));

                handleMouseEvent(startGridPoint, endGridPoint, (end.getButton() == MouseEvent.BUTTON1));
            }
        });
    }

    private void handleMouseEvent(Point startGridPoint, Point endGridPoint, boolean leftClick) {
        boolean dragged = !startGridPoint.equals(endGridPoint);
        GameObject gameObjectToSelect = null;
        Rectangle clickedStartGrid = new Rectangle();
        clickedStartGrid.setLocation(Utility.convertFromGrid(startGridPoint));
        clickedStartGrid.setSize(new Dimension(Game.GRID_PIXEL_SIZE, Game.GRID_PIXEL_SIZE));

        for (GameObject object : gameObjects) {
            if (object instanceof Pickup && clickedStartGrid.intersects(object.getBounds())) {
                gameObjectToSelect = object;
            } else if (object instanceof Wall && ((Wall) object).intersects(clickedStartGrid)) {
                gameObjectToSelect = object;
            }
        }

        if (gameObjectToSelect != null) {
            showObjectSettings(gameObjectToSelect);
        }

        if (leftClick && gameObjectToSelect == null && objectSettingsPanel.getGameObject() instanceof Wall) {
            drawWall((Wall) objectSettingsPanel.getGameObject(), startGridPoint, endGridPoint, leftClick);
        }

        if (!leftClick && !dragged && gameObjectToSelect == null) {
            String selected = (String) JOptionPane.showInputDialog(
                    gamePanel,
                    "ADD A NEW OBJECT:",
                    "ADD A NEW OBJECT",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    new String[]{"Wall",
                            "StartingPosition",
                            "EraserPickup",
                            "ReversePickup",
                            "SelfSlowPickup",
                            "SelfSpeedPickup",
                            "SlowEnemiesPickup",
                            "SpeedEnemiesPickup",
                            "InvinciblePickup",
                            "SelfGhostPickup",
                            "SwissCheesePickup"},
                    "Wall");
            if (selected != null && selected.length() > 0) {
                try {
                    if (selected.equals("StartingPosition")) {
                        if (startPositionMarker == null) {
                            JOptionPane.showMessageDialog(null, "CAN'T ADD STARTING POSITIONS BECAUSE THEY ARE SET TO AUTOMATICALLY BE GENERATED DURING MATCH.");
                        } else {
                            startPositionMarker.addGridPoint(endGridPoint);
                        }
                        return;
                    }
                    selected = selected.contains("Pickup")
                            ? "gameobjects.pickups." + selected
                            : "gameobjects." + selected;

                    GameObject gameObject = (GameObject) Class.forName(selected).getConstructor().newInstance();
                    gameObject.setId(ID.getNext());
                    if (gameObject instanceof Wall) {
                        ((Wall) gameObject).addGridPoint(endGridPoint);
                    } else {
                        Point p = Utility.convertFromGrid(endGridPoint);
                        int centering = Game.GRID_PIXEL_SIZE / 4;
                        p.x -= centering;
                        p.y -= centering;
                        gameObject.setPoint(p);
                    }
                    gameObjects.add(gameObject);

                    SpecialGameObject specialGameObject = new SpecialGameObject(gameObject);
                    specialGameObjects.add(specialGameObject);

                    objectSettingsPanel.update(specialGameObject);
                    sidePanel.setSelectedComponent(objectSettingsPanel);

                    updateGamePanel();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }

        if (gameObjectToSelect != null) {
            if (gameObjectToSelect instanceof Pickup) {
                Point p = Utility.convertFromGrid(endGridPoint);
                int centering = Game.GRID_PIXEL_SIZE / 4;
                p.x -= centering;
                p.y -= centering;
                gameObjectToSelect.setPoint(p);
                gameObjects.remove(gameObjectToSelect);
                gameObjects.add(gameObjectToSelect);
            } else {
                drawWall((Wall) gameObjectToSelect, startGridPoint, endGridPoint, leftClick);
            }

            updateGamePanel();
        }
    }

    private void showObjectSettings(GameObject gameObject) {
        for (SpecialGameObject specialGameObject : specialGameObjects) {
            if (gameObject.equals(specialGameObject.getGameObject())) {
                objectSettingsPanel.update(specialGameObject);
                sidePanel.setSelectedComponent(objectSettingsPanel);
                break;
            }
        }
    }

    private int linearInterpolate(double start, double end, double t) {
        return (int) Math.round(start + t * (end - start));
    }

    private void drawWall(Wall wall, Point startGridPoint, Point endGridPoint, boolean draw) {
        HashSet<Point> points = new HashSet<>();
        if (startGridPoint.x == endGridPoint.x) {
            // vertical line
            for (int i = startGridPoint.y; i <= endGridPoint.y; i++) {
                points.add(new Point(startGridPoint.x, i));
            }
        } else if (startGridPoint.y == endGridPoint.y) {
            // horizontal line
            for (int i = startGridPoint.x; i <= endGridPoint.x; i++) {
                points.add(new Point(i, startGridPoint.y));
            }
        } else {
            // diagonal line
            for (int t = 0; t < 100; t++) {
                points.add(new Point(
                        linearInterpolate(startGridPoint.x, endGridPoint.x, t / 100.0),
                        linearInterpolate(startGridPoint.y, endGridPoint.y, t / 100.0)
                ));
            }
        }

        if (draw) wall.addGridPoints(points);
        else wall.removeGridPoints(points);
    }

    void deleteObject(SpecialGameObject specialGameObject) {
        specialGameObjects.remove(specialGameObject);
        gameObjects.remove(specialGameObject.getGameObject());
        objectSettingsPanel.clear();
        updateGamePanel();
    }

    void setMapName(String name) {
        currentMap.setName(name);
        mapTopBarPanel.setMapName(name);
    }

    void setMapGrid(String gridName) {
        Dimension grid = Utility.getGridFromName(gridName);
        currentMap.setGrid(grid);
        gamePanel.setGrid(grid);
    }

    void setMapPlayers(int players) {
        currentMap.setPlayers(players);
    }

    void setMapPlayerSpeedMultiplier(double playerSpeedMultiplier) {
        currentMap.setPlayerSpeedMultiplier(playerSpeedMultiplier);
    }

    void setMapBackgroundImage(String image) {
        currentMap.setBackground(image);
        gamePanel.setBackground(image);
    }

    void setMapMusicTrack(String musicTrack) {
        currentMap.setMusicTrack(musicTrack);
    }

    void saveMap() {
        try {
            if (startPositionMarker == null || startPositionMarker.getGridPoints().size() == 0) {
                currentMap.setStartingPositions(null);
            } else {
                currentMap.setStartingPositions(startPositionMarker.getGridPoints().toArray(new Point[0]));
                if (startPositionMarker.getGridPoints().size() < currentMap.getPlayers()) {
                    JOptionPane.showMessageDialog(this, "NOTE: YOU HAVE NOT SET STARTING POSITION FOR ALL PLAYERS\nMAP WILL BE SAVED ANYWAY");
                }
            }
            currentMap.setGameMapObjects(specialGameObjects.toArray(new SpecialGameObject[0]));
            Maps.getInstance().save(currentMap);
            mapListPanel.reload();
        } catch (Exception error) {
            error.printStackTrace();
            JOptionPane.showMessageDialog(this, "There was an error. The map has NOT been saved. Please try again.");
        }
    }

    void newMap() {
        changeMap(null);
    }

    void exit() {
        userInterface.changeToPreviousScreen();
    }

    void disableStartingPositions() {
        if (startPositionMarker == null) return;
        gameObjects.remove(startPositionMarker);
        startPositionMarker.clear();
        startPositionMarker = null;
        updateGamePanel();
    }

    void enableStartingPositions() {
        if (startPositionMarker != null) {
            gameObjects.remove(startPositionMarker);
            startPositionMarker.clear();
            startPositionMarker = null;
        }
        startPositionMarker = new Wall(Color.RED, Color.WHITE);
        startPositionMarker.setId(ID.getNext());
        startPositionMarker.addGridPoints(Arrays.asList(currentMap.getStartingPositions()));
        gameObjects.add(startPositionMarker);
        updateGamePanel();
    }
}
