package gameclient.interfaces.highscorescreen;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import common.ServerInformation;
import gameclient.Game;
import mainserver.HighScore2;

public class MainServerClient {

    public MainServerClient() {

    }

    public ArrayList<ServerInformation> getServers() {
        try (Socket socket = new Socket();
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {
            socket.connect(Game.MAIN_SERVER, 500);
            outputStream.writeObject("GET_SERVERS");
            return (ArrayList<ServerInformation>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<HighScore2> getHighscores() {
        try (Socket socket = new Socket()) {
            socket.connect(Game.MAIN_SERVER, 500);
            try(ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
                outputStream.writeObject("GET_HIGHSCORES");
                return (ArrayList<HighScore2>) inputStream.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
