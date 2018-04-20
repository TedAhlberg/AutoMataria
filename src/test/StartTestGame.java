package test;

import common.GameMap;
import common.SpecialGameObject;
import gameclient.Game;
import gameclient.ServerInformationReceiver;
import gameserver.GameServer;
import gameserver.ServerInformationSender;

import java.awt.*;
import java.net.SocketException;
import java.util.Arrays;

import gameobjects.pickups.EraserPickup;
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
        SpecialGameObject[] gameMapObjects = Arrays.copyOf(map.getGameMapObjects(), map.getGameMapObjects().length + 1);
        gameMapObjects[gameMapObjects.length - 1] = new SpecialGameObject(new SlowEnemiesPickup(200, 200), 10000, 0, true, 15000);
        gameMapObjects[gameMapObjects.length-1]= new SpecialGameObject(new SpeedEnemiesPickup(200,200),10000,0,true,15000);
        map.setGameMapObjects(gameMapObjects);
        // Start a game server
        GameServer gameServer = new GameServer("AM-TEST-SERVER", 32000, 50, 150, map);
//        try {
//            ServerInformationSender sis = new ServerInformationSender(gameServer);
//            ServerInformationReceiver sir = new ServerInformationReceiver();
//            System.out.println(sir.getServerInformation());
//        } catch (SocketException e) {
//            e.printStackTrace();
//        }

        // Start a game client
//        new Game("127.0.0.1", 32000, new Dimension(1000, 1000), 60);
        new Game("127.0.0.1", 32000, new Dimension(1000, 1000), 30);

//        new Game("127.0.0.1", 32000, 100);
       
        
    }
}
