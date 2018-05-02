package gameserver;

import common.Maps;
import gameclient.Game;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * Denna klassen ska via UDP skicka packet till klient-sidan
 * med information om GameServer-objekt
 *
 * @author Henrik Olofsson
 * @author Johannes Bluml
 */

public class ServerInformationSender implements Runnable {
    private boolean running;
    private GameServer gameServer;

    public ServerInformationSender(GameServer gameServer) {
        this.gameServer = gameServer;
    }

    public static void main(String[] args) {
        GameServer gameServer = new GameServer("Best Server", 3000, 10, 10, 50, Maps.getInstance().get("Small Map 1"));
        gameServer.start();
    }

    public void run() {
        running = true;

        HashSet<InetAddress> localBroadcastAddresses = getBroadcastAddresses();

        while (running) {
            byte[] data = gameServer.getServerAliveUpdateMessage();

            try (DatagramSocket socket = new DatagramSocket()) {
                for (InetAddress localBroadcastAddress : localBroadcastAddresses) {
                    DatagramPacket packet = new DatagramPacket(data, data.length, localBroadcastAddress, Game.LOCAL_UDP_PORT);
                    socket.send(packet);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        running = false;
    }

    /**
     * @return A list of all local broadcast addresses
     */
    private HashSet<InetAddress> getBroadcastAddresses() {
        HashSet<InetAddress> result = new HashSet<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface == null || (networkInterface.isLoopback() && !networkInterface.isUp())) continue;
                List<InterfaceAddress> addresses = networkInterface.getInterfaceAddresses();
                for (InterfaceAddress address : addresses) {
                    if (address == null) continue;
                    InetAddress broadcast = address.getBroadcast();
                    if (broadcast == null) continue;
                    result.add(address.getBroadcast());
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            result.add(InetAddress.getByName("255.255.255.255"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return result;
    }
}
