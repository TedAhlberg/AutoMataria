package gameserver;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author Johannes Blüml
 */
public class ServerConnection {
    private final LinkedList<ClientListener> listeners = new LinkedList<>();
    private Thread thread;

    public ServerConnection() {

    }

    public void addListener(ClientListener l) {
        listeners.add(l);
    }

    public void start(int port) {
        if (thread != null) return;
        thread = new Thread(() -> startConnectionListener(port));
        thread.start();
    }

    private void startConnectionListener(int port) {
        ServerSocket server;
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            Thread.currentThread().interrupt();
            return;
        }
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket socket = server.accept();
                new Client(socket, listeners);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
