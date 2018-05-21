package gameclient.interfaces.serverbrowserscreen;

import common.ServerInformation;
import gameclient.Game;

import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Denna klassen ska ta emot UDP packet ifrån lokala nätverket
 * Sedan spara dem i en lista och uppdatera lyssnare om att listan har ändrats
 *
 * @author Henrik Olofsson
 */
public class ServerInformationReceiver extends Thread {
    private boolean running = false;
    private HashSet<ServerInformation> serverList = new HashSet<>();
    private HashSet<ServerInformationListener> listeners = new HashSet<>();
    private DatagramSocket socket;

    public ServerInformationReceiver() {
        setName("ServerInformationReceiver");
    }

    public void addListener(ServerInformationListener listener) {
        listeners.add(listener);
    }

    /**
     * Stops listening for server information
     */
    public void close() {
        running = false;
        if (socket != null) socket.close();
    }

    public void run() {
        running = true;
        runTimeThread();

        while (running) {
            try (DatagramSocket socket = new DatagramSocket(null)) {
                socket.setReuseAddress(true);
                socket.bind(new InetSocketAddress(Game.LOCAL_UDP_PORT));
                this.socket = socket;

                while (running) {
                    byte[] data = new byte[128];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    socket.receive(packet);
                    String ip = packet.getAddress().getHostName();
                    updateServerInfo(ip, new String(packet.getData()));
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                this.socket = null;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Adds new or updates existing server in the serverList with the provided data
     *
     * @param ip   IP address or hostname of the server
     * @param data String with serverinformation data sent from ServerInformationSender on any gameserver
     * @see gameserver.ServerInformationSender
     */
    private synchronized void updateServerInfo(String ip, String data) {
        String[] parts = data.split("\n");
        ServerInformation serverInfo = new ServerInformation(
                ip,
                parts[0],
                parts[1],
                parts[2],
                Integer.parseInt(parts[3]),
                Integer.parseInt(parts[4]),
                Integer.parseInt(parts[5].trim()));

        serverList.remove(serverInfo);
        serverList.add(serverInfo);

        for (ServerInformationListener listener : listeners) {
            listener.update(serverList);
        }
    }

    /**
     * Starts a thread that checks for offline servers every 3 seconds
     *
     * @see ServerInformationReceiver#cleanServerList()
     */
    private void runTimeThread() {
        new Thread(new Runnable() {
            public void run() {
                while (running) {
                    cleanServerList();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.getStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * Removes servers from the serverList if they have not been updated in the last 11 seconds
     */
    private synchronized void cleanServerList() {
        int offlineServerLimit = 11000;
        Iterator<ServerInformation> iter = serverList.iterator();
        while (iter.hasNext()) {
            long elapsedTime = System.currentTimeMillis() - iter.next().getUpdateTime();

            if (elapsedTime > offlineServerLimit) {
                iter.remove();
                for (ServerInformationListener listener : listeners) {
                    listener.update(serverList);
                }
            }
        }
    }
}
