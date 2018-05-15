package gameserver;

import java.net.Socket;

import common.ServerInformation;

public class MainServerClient {
    private Socket socket;
    private String ip;
    private int port;
    private boolean connected = false;
    private ClientHandler clientHandler = null;
    
    
    public MainServerClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
    
    public void connectClient() {
        clientHandler = new ClientHandler();
        connected = true;
        clientHandler.start();
    }
    
    public void disconnectClient() {
        clientHandler = null;
        connected = false;
    }
    
    //skicka serverinformation
    //skicka gamescore
    
    public void sendServerInformation(ServerInformation serverInformation) {
        ServerInformation serverInformation = gameServer.getServerInformation();
        
    }
    
    
    private class ClientHandler extends Thread {
        
        public void run() {
            while(connected) {
                
            }
        }
        
    }
    

}
