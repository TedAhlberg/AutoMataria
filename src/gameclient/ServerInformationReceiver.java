package gameclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import gameserver.ServerInformation;
/**
 * Denna klassen ska ta emot UDP packet ifrån servern
 * och dela in dessa i en lista av hashmaps.
 * @author Henrik & Johannes
 *
 */


public class ServerInformationReceiver extends Thread {
    private DatagramSocket socket;
    private boolean running = false;
    private HashSet<ServerInformation> serverList = new HashSet<>();
    private ArrayList<ServerInformationListener> listeners = new ArrayList<>();
    
    public ServerInformationReceiver() throws SocketException {
        socket = new DatagramSocket(4445);
        
    }
    
    public void run() {
        running = true; 
        
        while(running) {
            byte[] data = new byte[76];
            try {
                DatagramPacket packet = new DatagramPacket(data, data.length);
                socket.receive(packet);
                String ip = packet.getAddress().toString();
                updateServerInfo(ip, new String(packet.getData()));
                
                System.out.println(serverList.size());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void updateServerInfo(String ip, String data) {
        String[] parts = data.split("\n");
        ServerInformation serverInfo = new ServerInformation(
                ip,
                parts[0],
                parts[1],
                parts[2],
                Integer.parseInt(parts[3]),
                Integer.parseInt(parts[4].trim()));
        serverList.add(serverInfo);
        for(ServerInformationListener listener : listeners) {
            listener.update(serverList);
        }
    }
    
    public Collection<ServerInformation> getServerList() {
        return serverList;
    }
    
//    public static void main(String[] args) throws SocketException {
//        ServerInformationReceiver sir = new ServerInformationReceiver();
//        sir.start();
////        String[] servInfo = new String[5];
////        servInfo = sir.getServerInformation();
////        System.out.println(servInfo[0]);
//    }

    public void addListener(ServerInformationListener listener) {
        listeners.add(listener);
    }

}
