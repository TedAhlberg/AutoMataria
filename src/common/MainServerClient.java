package common;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class MainServerClient {

    private MainServerClient() {

    }

    public static void sendServerInformation(ServerInformation serverInformation) {
        try (Socket socket = new Socket()) {
            socket.connect(Game.MAIN_SERVER, 500);
            try (ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
                outputStream.writeObject("SET_GAMESERVER");
                outputStream.writeObject(serverInformation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendGameScore(HashMap<String, Integer> scores) {
        try (Socket socket = new Socket()) {
            socket.connect(Game.MAIN_SERVER, 500);
            try (ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
                outputStream.writeObject("SET_HIGHSCORES");
                outputStream.writeObject(scores);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Highscore> getHighscores() {
        try (Socket socket = new Socket()) {
            socket.connect(Game.MAIN_SERVER, 500);
            try (ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
                outputStream.writeObject("GET_HIGHSCORES");
                return (ArrayList<Highscore>) inputStream.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<ServerInformation> getServers() {
        try (Socket socket = new Socket()) {
            socket.connect(Game.MAIN_SERVER, 500);
            try (ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
                outputStream.writeObject("GET_GAMESERVERS");
                return (ArrayList<ServerInformation>) inputStream.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
