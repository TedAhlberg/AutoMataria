package mainserver;

import common.Game;
import common.ServerInformation;
import gameclient.interfaces.serverbrowserscreen.ServerInformationListener;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Henrik Olofsson
 * A class that will be used for writing and reading highscores.
 * And communicating with the main server.
 */

public class MainServer {
    private boolean running = false;
    private HighScoreServer thread = null;
    private GameServers servers;
    private FileStorage fileStorage;
    private HighScoreList highscoreList;

    public MainServer() {
        servers = new GameServers();
        fileStorage = new FileStorage();
        highscoreList = new HighScoreList(fileStorage.read());
    }

    public static void main(String[] args) {
        MainServer hsh = new MainServer();
        hsh.started();
    }

    public void started() {
        try {
            thread = new HighScoreServer(Game.MAIN_SERVER.getPort());
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

    public GameServers getServers() {
        return servers;

    }
    
    private synchronized void cleanServerList() {
        int offlineServerLimit = 11000;
        Iterator<ServerInformation> iter = servers.getServers().iterator();
        while (iter.hasNext()) {
            long elapsedTime = System.currentTimeMillis() - iter.next().getUpdateTime();

            if (elapsedTime > offlineServerLimit) {
                iter.remove();
            }
        }
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
                     ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                     ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
                    socket.setSoTimeout(500);

                    System.out.println("Server running on port: " + socket.getPort());

                    String messageType = (String) inputStream.readObject();
                    System.out.println(messageType);

                    if (messageType.equals("GET_HIGHSCORES")) {
                        ArrayList<HighScore2> data = highscoreList.getSortedList();
                        outputStream.writeObject(data);
                    } else if (messageType.equals("SET_HIGHSCORES")) {
                        Object nextObject = inputStream.readObject();
                        Map<String, Integer> highscores = (Map<String, Integer>) nextObject;

                        highscores.forEach((userName, score) -> {
                            HighScore2 highscore = new HighScore2(userName, score);
                            highscoreList.addAndReplace(highscore);
                        });

                        fileStorage.save(highscoreList.getSortedList());
                    } else if (messageType.equals("GET_GAMESERVERS")) {
                        outputStream.writeObject(servers.getServers());
                        System.out.println(servers.getServers().size());
                        cleanServerList();
                    } else if (messageType.equals("SET_GAMESERVER")) {
                        Object object = inputStream.readObject();
                        if (object instanceof ServerInformation) {
                            ServerInformation information = (ServerInformation) object;
                            information.setIp(socket.getInetAddress().getHostAddress());
                            System.out.println(information.toString());
                            servers.addServer(information);
                        }
                    } else if (messageType.equals("CHANGE_USERNAME")) {
                        String oldUsername = inputStream.readUTF();
                        String newUsername = inputStream.readUTF();
                        highscoreList.replaceName(oldUsername, newUsername);
                        fileStorage.save(highscoreList.getSortedList());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
