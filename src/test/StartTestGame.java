package test;

import common.GameMap;
import common.SpecialGameObject;
import gameclient.Game;
import gameserver.GameServer;

import java.awt.*;
import java.util.Arrays;

import gameobjects.pickups.EraserPickup;
import gameobjects.pickups.SelfSlowPickup;
import gameobjects.pickups.SelfSpeedPickup;
import gameobjects.pickups.SlowEnemiesPickup;
import gameobjects.pickups.SpeedEnemiesPickup;
import common.Maps;

/**
 * @author Johannes Blüml
 */
public class StartTestGame {
    public static void main(String[] args) {
        // Create a map
        GameMap map = Maps.getInstance().get("Small Map 1");
        System.out.println(map);
        SpecialGameObject[] gameMapObjects = new SpecialGameObject[3];
        gameMapObjects[0] = new SpecialGameObject(new SlowEnemiesPickup(200, 200, 65), 10000, 0, true, 15000);
        gameMapObjects[1]= new SpecialGameObject(new SpeedEnemiesPickup(200,200,65),10000,0,true,15000);
        gameMapObjects[2] = new SpecialGameObject(new SelfSlowPickup(200,200,60), 10000,0,true,15000);
        map.setGameMapObjects(gameMapObjects);
        // Start a game server
        map.setPlayerSpeed(0.5);
        new GameServer("AM-test-server", 32000, 100, 150, map);

        // Start a game client
//        new Game("127.0.0.1", 32000, new Dimension(1000, 1000), 60);
        new Game("127.0.0.1", 32000, new Dimension(1000, 1000), 30);

//        new Game("127.0.0.1", 32000, 100);
       
        
    }
}
