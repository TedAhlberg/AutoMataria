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

/**
 * 
 * @author Henrik Olofsson
 *  A class that will be used for writing and reading highscores.
 *  And communicating with the main server.
 *
 */

public class HighScoreHandler {
    String fileName = "HighScores/HighScore.ser";
    private Object object;
    private HighScore highScores;
    private boolean running = false;
    private HighScoreServer thread = null;

    public HighScoreHandler() {}

    public synchronized void writeToFile(String userName, int highScore) {
        highScores = readFromFile();

        if(highScore > highScores.get(userName)) {
            try (ObjectOutputStream writer = new ObjectOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(fileName)))){
                highScores.put(userName, highScore);
                writer.writeObject(highScores);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }        

    }

    public HighScore readFromFile() {

        try (ObjectInputStream reader = new ObjectInputStream(
                new BufferedInputStream(
                        new FileInputStream(fileName)))){
            object = reader.readObject();
            object = (HighScore) highScores; 
        } catch (IOException e) {
            e.getStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return highScores;
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

    private class HighScoreServer extends Thread {
        private ServerSocket serverSocket;
        
        public HighScoreServer(int port) throws IOException {
            serverSocket = new ServerSocket(port);
        }

        public void run() {
            while(running) {
                System.out.println("Server running: " + running);
                try (Socket socket = serverSocket.accept();
                     DataInputStream fromMainServer =
                             new DataInputStream(socket.getInputStream());
                     DataOutputStream toMainServer =
                             new DataOutputStream(socket.getOutputStream())){
                    
                    System.out.println("Server running on port: " + socket.getPort());
                    
                    String userName = fromMainServer.readUTF();
                    int highScore = fromMainServer.readInt();
                    
                    writeToFile(userName,highScore);
                         
                     } catch(IOException e) {
                         e.getStackTrace();
                     }
            }

        }
    }
    
    public static void main(String[] args) {
        HighScoreHandler hsh = new HighScoreHandler();
        hsh.started();
    }
}
