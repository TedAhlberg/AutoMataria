package gameserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/**
 * @author Johannes Blüml
 */
public class ServerConnection implements Runnable {
    private final LinkedList<ConnectionListener> listeners = new LinkedList<>();
    private LinkedList<Client> clients = new LinkedList<>();
    private int port;
    private ServerSocket serverSocket;
    private boolean running;

    public ServerConnection(int port) {
        this.port = port;
    }

    public void run() {
        running = true;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            this.serverSocket = serverSocket;
            listeners.forEach(client -> client.onServerConnectionStarted());
            while (running) {
                Socket socket = serverSocket.accept();
                clients.add(new Client(socket, listeners));
            }
        } catch (IOException e) {
        } finally {
            listeners.forEach(client -> client.onServerConnectionStopped());
        }
    }

    public void addListener(ConnectionListener l) {
        listeners.add(l);
    }

    public void stop() {
        if (serverSocket == null) return;
        running = false;
        clients.forEach(client -> client.disconnect());
        clients.clear();
        try {
            serverSocket.close();
        } catch (IOException e) {
        } finally {
            serverSocket = null;
        }
    }
}
