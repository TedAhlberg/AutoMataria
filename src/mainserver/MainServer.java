package mainserver;

import java.io.BufferedInputStream;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import common.ServerInformation;

/**
 * 
 * @author Henrik Olofsson
 *  A class that will be used for writing and reading highscores.
 *  And communicating with the main server.
 *
 */

public class MainServer {
    private boolean running = false;
    private HighScoreServer thread = null;
    private Servers servers;
    private FileStorage fileStorage = new FileStorage();
    

    public MainServer() {}

    public void started() {
        try {
            thread = new HighScoreServer(10500);
        } catch (IOException e) {
            e.printStackTrace();
        }
        running = true;
        thread.start();
    }

    public void stopped() {
        running = false;
        thread = null;
    }
    
    public Servers getServers() {
        return servers;
        
    }

    private class HighScoreServer extends Thread {
        private ServerSocket serverSocket;
        
        public HighScoreServer(int port) throws IOException {
            serverSocket = new ServerSocket(port);
        }

        public void run() {
            while(running) {
                System.out.println("Server running: " + running);
                try (Socket socket = serverSocket.accept();
                     ObjectInputStream inputStream =
                             new ObjectInputStream(socket.getInputStream());
                     ObjectOutputStream outputStream =
                             new ObjectOutputStream(socket.getOutputStream())){
                    
                    System.out.println("Server running on port: " + socket.getPort());
                    
                    String messageType = inputStream.readUTF();
                    
                    if(messageType.equals("GET_HIGHSCORES")) {
                        outputStream.writeObject(fileStorage.getHighScores());
                    }
                    else if(messageType.equals("SET_HIGHSCORE")) {
                        String userName = inputStream.readUTF();
                        int highScore = inputStream.readInt();
                        fileStorage.saveToDisk(userName,highScore);
                    }
                    else if(messageType.equals("GET_GAMESERVERS")) {
                        outputStream.writeObject(servers);
                    }
                    else if(messageType.equals("SET_GAMESERVER")) {
                        try {
                            Object object = inputStream.readObject();
                            if(object instanceof ServerInformation) {
                              ServerInformation information = (ServerInformation)object;
                              servers.addServer(information);
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(messageType.equals("CHANGE_USERNAME")) {
                       String oldUsername = inputStream.readUTF();
                       String newUsername = inputStream.readUTF();
                       HighScore highScores = fileStorage.getHighScores();
                        highScores.replace(oldUsername, newUsername);
                    }
      
                     } catch(IOException e) {
                         e.getStackTrace();
                     }
            }
        }
    }
    public static void main(String[] args) {
        MainServer hsh = new MainServer();
        hsh.started();
    }
}
