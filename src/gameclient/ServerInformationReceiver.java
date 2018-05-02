package gameclient;

import java.io.IOException;
import java.net.*;
import java.util.*;

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

    public ServerInformationReceiver() {
    }

    public void run() {
        running = true;
        runTimeThread();

        try (DatagramSocket socket = new DatagramSocket(Game.LOCAL_UDP_PORT)) {

        while (running) {
            byte[] data = new byte[128];

                DatagramPacket packet = new DatagramPacket(data, data.length);
                socket.receive(packet);
                String ip = packet.getAddress().getHostName();
                updateServerInfo(ip, new String(packet.getData()));
                
        }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
        serverList.add(serverInfo);
        for (ServerInformationListener listener : listeners) {
            listener.update(serverList);
        }
    }
    
    public void runTimeThread() {
        new Thread(new Runnable() {

            public void run() {
                while(running) {
                    cleanServerList();
                    try {
                        Thread.sleep(3000);
                    } catch(InterruptedException e) {
                        e.getStackTrace();
                    }
                } 
                
            } 
            
        })
        .start();
    }
    
    public synchronized void cleanServerList() {
        Iterator<ServerInformation> iter = serverList.iterator();
        while(iter.hasNext()) {
          long lastDateTime = iter.next().getUpdateTime();
          if(System.currentTimeMillis() - lastDateTime > 11000) {
              iter.remove();
              for (ServerInformationListener listener : listeners) {
                  listener.update(serverList);
              }
          }
        }
    }

    public Collection<ServerInformation> getServerList() {
        return serverList;
    }

    public void addListener(ServerInformationListener listener) {
        listeners.add(listener);
    }

}
