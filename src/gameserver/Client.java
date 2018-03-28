package gameserver;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;

/**
 * @author Johannes Blüml
 */
public class Client {
    private boolean connected;
    private ObjectOutputStream outputStream;
    private LinkedList<ClientListener> listeners;

    public Client(Socket socket, LinkedList<ClientListener> listeners) throws IOException {
        this.listeners = listeners;
        connected = true;
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

        listeners.forEach(listener -> listener.onConnect(this));

        new Thread(() -> {
            while (connected) {
                try {
                    Object nextObject = inputStream.readObject();
                    listeners.forEach(listener -> listener.onData(this, nextObject));
                } catch (IOException e) {
                    connected = false;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            listeners.forEach(listener -> listener.onClose(this));
        }).start();
    }

    public void send(Object object) {
        if (!connected) return;
        try {
            outputStream.writeObject(object);
            outputStream.reset();
        } catch (IOException e) {
            connected = false;
        }
    }
}
