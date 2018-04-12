package gameserver;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import common.GameMap;
import gameclient.Game;
import gameobjects.GameObject;
import gameobjects.Wall;

/**
 * Denna klassen ska via UDP skicka packet till klient-sidan
 * med information om GameServer-objekt
 * @author Johannes Blüml & Henrik Olofsson
 *
 */

public class ServerInformationSender extends Thread {
    private DatagramSocket socket;
    private boolean running;
    private GameServer gameServer;

    
    public ServerInformationSender(GameServer gameServer) throws SocketException {
        socket = new DatagramSocket();
        this.gameServer = gameServer;
    }
    
    public void run() {
        running = true;
        
        while(running) {
            byte[] data = gameServer.getServerAliveUpdateMessage();
            InetAddress ip;
            try {
                ip = InetAddress.getByName("255.255.255.255");
                DatagramPacket packet = new DatagramPacket(data, data.length, ip, 4445);
                socket.send(packet);
                Thread.sleep(3000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) throws SocketException {
        GameMap map = new GameMap();
        map.setBackground("resources/Stars.png");
        map.setMusicTrack("resources/Music/AM-trck1.mp3");
        map.setPlayerSpeed(0.25);
        map.setPlayers(5);
        map.setGrid(new Dimension(50, 50));
        Wall wall = new Wall();
        int width = map.getGrid().width * Game.GRID_PIXEL_SIZE;
        int height = map.getGrid().height * Game.GRID_PIXEL_SIZE;
        wall.add(new Rectangle(0, 0, Game.GRID_PIXEL_SIZE, width));
        wall.add(new Rectangle(height - Game.GRID_PIXEL_SIZE, 0, Game.GRID_PIXEL_SIZE, width));
        wall.add(new Rectangle(0, 0, height, Game.GRID_PIXEL_SIZE));
        wall.add(new Rectangle(0, height - Game.GRID_PIXEL_SIZE, height, Game.GRID_PIXEL_SIZE));
        GameObject[] startingObjects = {wall};
        map.setStartingGameObjects(startingObjects);
        GameServer gameServer = new GameServer("Best Server", 3000, 10, 10, map);
        ServerInformationSender sis = new ServerInformationSender(gameServer);
        sis.start();
    }

}
