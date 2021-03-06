package gameclient.interfaces.gamescreen;

import java.io.*;
import java.net.Socket;

/**
 * @author Johannes Blüml
 */
public class GameServerConnection {
    private ObjectOutputStream outputStream;
    private boolean connected;
    private GameServerListener listener;
    private Thread thread;

    public GameServerConnection(GameServerListener listener) {
        this.listener = listener;
    }

    public void connect(String serverIP, int serverPort) {
        if (thread != null) disconnect();

        thread = new Thread(() -> startConnection(serverIP, serverPort), "ClientConnection");
        thread.start();
    }

    private void startConnection(String serverIP, int serverPort) {
        try (Socket socket = new Socket(serverIP, serverPort);
             ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {
            socket.setTcpNoDelay(true);
            socket.setKeepAlive(true);
            socket.setTrafficClass(0x18 | 0x08);
            this.outputStream = outputStream;
            connected = true;
            listener.onConnect();

            while (connected) {
                try {
                    Object nextObject = inputStream.readObject();
                    listener.onData(nextObject);
                } catch (IOException e) {
                    e.printStackTrace();
                    connected = false;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            listener.onDisconnect();
            thread = null;
        }
    }

    synchronized public void send(Object object) {
        try {
            outputStream.writeObject(object);
            outputStream.flush();
            outputStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
            connected = false;
        }
    }

    synchronized public void disconnect() {
        connected = false;
    }

    synchronized public boolean isConnected() {
        return connected;
    }
}
