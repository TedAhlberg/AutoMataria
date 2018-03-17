package gameserver;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author Johannes Blüml
 */
public class Client {
    private ObjectOutputStream outputStream;

    public Client(Socket socket, LinkedList<ClientListener> listeners) throws IOException {
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

        listeners.forEach(listener -> listener.onConnect(this));

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Object nextObject = inputStream.readObject();
                    listeners.forEach(listener -> listener.onData(this, nextObject));
                } catch (IOException e) {
                    Thread.currentThread().interrupt();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            listeners.forEach(listener -> listener.onClose(this));
        }).start();
    }

    public void send(Object object) {
        if (Thread.currentThread().isInterrupted()) return;
        try {
            outputStream.writeObject(object);
            outputStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
