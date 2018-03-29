package gameserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/**
 * @author Johannes Blüml
 */
public class ServerConnection implements Runnable {
    private final LinkedList<ClientListener> listeners = new LinkedList<>();
    private LinkedList<Client> clients = new LinkedList<>();
    private int port;
    private ServerSocket serverSocket;

    public ServerConnection(int port) {
        this.port = port;
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            this.serverSocket = serverSocket;
            System.out.println("Server started on port " + port + ".");
            while (true) {
                Socket socket = serverSocket.accept();
                new Client(socket, listeners);
            }
        } catch (IOException e) {
        } finally {
            System.out.println("Server stopped.");
        }
    }

    public void addListener(ClientListener l) {
        listeners.add(l);
    }

    public void stop() {
        if (serverSocket == null) return;
        clients.forEach(client -> client.disconnect());
        try {
            serverSocket.close();
        } catch (IOException e) {
        } finally {
            serverSocket = null;
        }
    }
}
