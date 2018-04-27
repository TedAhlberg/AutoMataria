package gameserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import common.Maps;

/**
 * Denna klassen ska via UDP skicka packet till klient-sidan
 * med information om GameServer-objekt
 * @author Johannes Blüml & Henrik Olofsson
 *
 */

public class ServerInformationSender implements Runnable {
    private boolean running;
    private GameServer gameServer;
    
    public ServerInformationSender(GameServer gameServer) {
        this.gameServer = gameServer;
    }
    
    public void run() {
        running = true;
        
        while(running) {
            byte[] data = gameServer.getServerAliveUpdateMessage();
            InetAddress ip;
            try {
                DatagramSocket socket = new DatagramSocket();
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

    public void stop() {
        running = false;
    }
    
    public static void main(String[] args) {
        GameServer gameServer = new GameServer("Best Server", 3000, 10, 10, 50, Maps.getInstance().get("Small Map 1"));
        ServerInformationSender sis = new ServerInformationSender(gameServer);
        new Thread(sis).start();
    }
}
