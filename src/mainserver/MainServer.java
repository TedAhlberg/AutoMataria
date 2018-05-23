package mainserver;

import common.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * @author Henrik Olofsson
 * A class that will be used for writing and reading highscores.
 * And communicating with the main server.
 */
public class MainServer {
    private boolean running = false;
    private TCPServerThread thread = null;
    private GameServerList gameServerList;
    private FileStorage fileStorage;
    private HighscoreList highscoreList;

    public MainServer() {
        gameServerList = new GameServerList();
        fileStorage = new FileStorage();
        highscoreList = new HighscoreList(fileStorage.read());
    }

    public void started() {
        try {
            thread = new TCPServerThread(Game.MAIN_SERVER.getPort());
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

    private synchronized void cleanServerList() {
        int offlineServerLimit = 11000;
        Iterator<ServerInformation> iter = gameServerList.getServers().iterator();
        while (iter.hasNext()) {
            long elapsedTime = System.currentTimeMillis() - iter.next().getUpdateTime();

            if (elapsedTime > offlineServerLimit) {
                iter.remove();
            }
        }
    }

    private class TCPServerThread extends Thread {
        private ServerSocket serverSocket;

        public TCPServerThread(int port) throws IOException {
            serverSocket = new ServerSocket(port);
        }

        public void run() {
            while (running) {
                try (Socket socket = serverSocket.accept();
                     ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                     ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
                    socket.setSoTimeout(500);

                    String messageType = (String) inputStream.readObject();

                    if (messageType.equals("GET_HIGHSCORES")) {
                        ArrayList<Highscore> data = highscoreList.getSortedList();
                        outputStream.writeObject(data);
                    } else if (messageType.equals("SET_HIGHSCORES")) {
                        Object nextObject = inputStream.readObject();
                        Map<String, Integer> highscores = (Map<String, Integer>) nextObject;

                        highscores.forEach((userName, score) -> {
                            Highscore highscore = new Highscore(userName, score);
                            highscoreList.addAndReplace(highscore);
                        });

                        fileStorage.save(highscoreList.getSortedList());
                    } else if (messageType.equals("GET_GAMESERVERS")) {
                        outputStream.writeObject(gameServerList.getServers());
                        cleanServerList();
                    } else if (messageType.equals("SET_GAMESERVER")) {
                        Object object = inputStream.readObject();
                        if (object instanceof ServerInformation) {
                            ServerInformation information = (ServerInformation) object;
                            String ip = socket.getInetAddress().getHostAddress();
                            if (ip.equals("127.0.0.1")) {
                                ip = Game.MAIN_SERVER.getHostName();
                            }
                            information.setIp(ip);
                            gameServerList.addServer(information);
                        }
                    } else if (messageType.equals("CHANGE_USERNAME")) {
                        String oldUsername = inputStream.readUTF();
                        String newUsername = inputStream.readUTF();
                        highscoreList.replaceName(oldUsername, newUsername);
                        fileStorage.save(highscoreList.getSortedList());
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
