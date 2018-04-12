package gameclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Denna klassen ska ta emot UDP packet ifrån servern
 * och dela in dessa i en lista av hashmaps.
 * @author Henrik & Johannes
 *
 */


public class ServerInformationReceiver extends Thread {
    private DatagramSocket socket;
    private boolean running = false;
    private DatagramPacket packet;
    private ArrayList<HashMap<String,String>> hashList = new ArrayList<>();
    
    public ServerInformationReceiver() throws SocketException {
        socket = new DatagramSocket(4445);
        
    }
    
    public void run() {
        running = true; 
        
        while(running) {
            byte[] data = new byte[76];
            try {
                packet = new DatagramPacket(data, data.length);
                socket.receive(packet);
                System.out.println(packet.toString());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public String[] getServerInformation() {
        HashMap<String, String> serverHash = new HashMap<>();
        String information = new String(packet.getData());
        String[] parts = information.split("\n");
        serverHash.put("ServerName", parts[0]);
        serverHash.put("MapName", parts[1]);
        serverHash.put("GameState", parts[2]);
        serverHash.put("ServerPort", parts[3]);
        serverHash.put("ConnectedClients", parts[4]);
        return parts;
    }
    
    public static void main(String[] args) throws SocketException {
        ServerInformationReceiver sir = new ServerInformationReceiver();
        sir.start();
    }

}
