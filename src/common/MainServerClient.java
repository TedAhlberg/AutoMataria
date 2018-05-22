package common;

import mainserver.HighScore2;

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
            try(ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {
                outputStream.writeObject("SET_GAMESERVER");
                outputStream.writeObject(serverInformation);
            }
        } catch(IOException e) {
            e.getStackTrace();
        }
    }

    public static void sendGameScore(HashMap<String, Integer> scores) {
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
    
    public static ArrayList<HighScore2> getHighscores() {
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
    
    public static ArrayList<ServerInformation> getServers() {
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
}
