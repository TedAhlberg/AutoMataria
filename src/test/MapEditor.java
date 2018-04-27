package test;

import common.*;
import gameclient.GamePanel;
import gameobjects.GameObject;
import gameserver.StartingPositions;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Johannes Blüml
 */
public class MapEditor {
    private StartingPositions startingPositions;
    private GamePanel gamePanel;
    private ArrayList<GameObject> gamePanelGameObjects;
    private GameMap currentMap;
    private GameObject currentGameObject;
    private SpecialGameObject currentSpecialGameObject;

    private String mapName, mapBackgroundImage, mapMusicTrack;
    private int mapMaxPlayers;
    private double mapPlayerSpeedMultiplier;
    private Dimension mapGrid;
    private ArrayList<Point> mapStartingPositions;
    private ArrayList<SpecialGameObject> mapSpecialGameObjects;

    public MapEditor() {
    }

    public void selectSpecialGameObject(int index) {
        currentSpecialGameObject = mapSpecialGameObjects.get(index);
    }

    public void selectGameObject(String type) {

    }

    public void saveSpecialGameObject(int spawnInterval, int spawnLimit, boolean spawnRandom, int visibletime) {
    }

    public void loadMap(String name) {
        GameMap map = Maps.getInstance().get(name);

        if (map == null) {
            clearMap();
            return;
        }

        mapName = map.getName();
        mapBackgroundImage = map.getBackground();
        mapMusicTrack = map.getMusicTrack();
        mapMaxPlayers = map.getPlayers();
        mapPlayerSpeedMultiplier = map.getPlayerSpeedMultiplier();
        mapGrid = map.getGrid();
        mapStartingPositions = new ArrayList<>(Arrays.asList(map.getStartingPositions()));
        mapSpecialGameObjects = new ArrayList<>(Arrays.asList(map.getGameMapObjects()));

        gamePanelGameObjects = new ArrayList<>();
        for (SpecialGameObject mapSpecialGameObject : mapSpecialGameObjects) {
            gamePanelGameObjects.add(mapSpecialGameObject.getGameObject());
        }

        updateUI();
    }

    private void clearMap() {
        mapName = "New Map Name";
        mapBackgroundImage = "";
        mapMusicTrack = "";
        mapMaxPlayers = 4;
        mapPlayerSpeedMultiplier = 250;
        mapGrid = new Dimension(75, 75);
        mapStartingPositions = new ArrayList<>();
        mapSpecialGameObjects = new ArrayList<>();

        gamePanelGameObjects = new ArrayList<>();
    }

    public void saveMap() {
        GameMap map = new GameMap(
                mapName,
                mapBackgroundImage,
                mapMusicTrack,
                mapMaxPlayers,
                mapPlayerSpeedMultiplier,
                mapGrid,
                mapStartingPositions.toArray(new Point[0]),
                mapSpecialGameObjects.toArray(new SpecialGameObject[0])
        );
        Maps.getInstance().save(map);
    }

    public void removeMap() {
        Maps.getInstance().remove(mapName);
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public void setMapBackgroundImage(String mapBackgroundImage) {
        this.mapBackgroundImage = mapBackgroundImage;
    }

    public void setMapMusicTrack(String mapMusicTrack) {
        this.mapMusicTrack = mapMusicTrack;
    }

    public void setMapMaxPlayers(int mapMaxPlayers) {
        this.mapMaxPlayers = mapMaxPlayers;
    }

    public void setMapPlayerSpeedMultiplier(int mapPlayerSpeedMultiplier) {
        this.mapPlayerSpeedMultiplier = mapPlayerSpeedMultiplier;
    }

    public void setMapGrid(Dimension mapGrid) {
        this.mapGrid = mapGrid;
    }

    public void addStartingPosition(Point point) {
        mapStartingPositions.add(point);
    }

    public void removeStartingPosition(Point point) {
        mapStartingPositions.remove(point);
    }

    public void generateStartingPositions() {
        mapStartingPositions.clear();
        mapStartingPositions.addAll(Arrays.asList(startingPositions.generate(mapGrid,mapMaxPlayers)));
    }

    private void updateUI() {
    }
}
