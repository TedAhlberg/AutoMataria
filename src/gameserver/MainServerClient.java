package gameserver;

import common.ServerInformation;
import gameclient.Game;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class MainServerClient {
    private Socket socket;
    private String ip;
    private int port;
    private boolean connected = false;

    public void sendServerInformation(ServerInformation serverInformation) {
        try (Socket socket = new Socket()) {
            socket.connect(Game.MAIN_SERVER, 500);
            try(ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {
                outputStream.writeObject("SET_GAMESERVER");
                outputStream.writeObject(serverInformation);
            }
        } catch(IOException e) {
            e.getStackTrace();
        }
    }

    public void sendGameScore(HashMap<String, Integer> scores) {
        try (Socket socket = new Socket()) {
            socket.connect(Game.MAIN_SERVER, 500);
            try(ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {
                outputStream.writeObject("SET_HIGHSCORES");
                outputStream.writeObject(scores);
            }
        } catch(IOException e) {
            e.getStackTrace();
        }
    }
}
