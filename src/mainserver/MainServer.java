package mainserver;

import common.ServerInformation;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Henrik Olofsson
 * A class that will be used for writing and reading highscores.
 * And communicating with the main server.
 */

public class MainServer {
    private boolean running = false;
    private HighScoreServer thread = null;
    private Servers servers;
<<<<<<< HEAD
    private FileStorage fileStorage = new FileStorage();  
=======
    private FileStorage fileStorage = new FileStorage();

>>>>>>> bd80789e45f5348767642570ea2f41ada7a4526f

    public MainServer() {}

    public static void main(String[] args) {
        MainServer hsh = new MainServer();
        hsh.started();
    }

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
            while (running) {
                System.out.println("Server running: " + running);
                try (Socket socket = serverSocket.accept();
                     ObjectInputStream inputStream =
                             new ObjectInputStream(socket.getInputStream());
                     ObjectOutputStream outputStream =
                             new ObjectOutputStream(socket.getOutputStream())) {

                    System.out.println("Server running on port: " + socket.getPort());

                    String messageType = inputStream.readUTF();
<<<<<<< HEAD
                    
                    if(messageType.equals("GET_HIGHSCORES")) {
                        outputStream.writeObject(fileStorage.read());
                    }
                    else if(messageType.equals("SET_HIGHSCORE")) {
                        String userName = inputStream.readUTF();
                        int highScore = inputStream.readInt();
                        HighScore2 highscore = new HighScore2(userName, highScore);
                        fileStorage.save(highscore);
                    }
                    else if(messageType.equals("GET_GAMESERVERS")) {
=======

                    if (messageType.equals("GET_HIGHSCORES")) {
                        outputStream.writeObject(fileStorage.getHighScores());
                    } else if (messageType.equals("SET_HIGHSCORE")) {
                        String userName = inputStream.readUTF();
                        int highScore = inputStream.readInt();
                        fileStorage.saveToDisk(userName, highScore);
                    } else if (messageType.equals("GET_GAMESERVERS")) {
>>>>>>> bd80789e45f5348767642570ea2f41ada7a4526f
                        outputStream.writeObject(servers);
                    } else if (messageType.equals("SET_GAMESERVER")) {
                        try {
                            Object object = inputStream.readObject();
                            if (object instanceof ServerInformation) {
                                ServerInformation information = (ServerInformation) object;
                                servers.addServer(information);
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
<<<<<<< HEAD
                    }
                    else if(messageType.equals("CHANGE_USERNAME")) {
                       String oldUsername = inputStream.readUTF();
                       String newUsername = inputStream.readUTF();
                       HighScoreList highScores = fileStorage.read();
                        highScores.replaceName(oldUsername, newUsername);
                    }
                     } catch(IOException e) {
                         e.getStackTrace();
                     }
=======
                    } else if (messageType.equals("CHANGE_USERNAME")) {
                        String oldUsername = inputStream.readUTF();
                        String newUsername = inputStream.readUTF();
                        HighScore highScores = fileStorage.getHighScores();
                        highScores.replace(oldUsername, newUsername);
                    }

                } catch (IOException e) {
                    e.getStackTrace();
                }
>>>>>>> bd80789e45f5348767642570ea2f41ada7a4526f
            }
        }
    }
}
