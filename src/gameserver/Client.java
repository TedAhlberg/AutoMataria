package gameserver;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;

/**
 * @author Johannes Blüml
 */
public class Client {
    private ObjectOutputStream outputStream;
    private boolean connected;
    private LinkedList<ClientListener> listeners;

    public Client(Socket socket, LinkedList<ClientListener> listeners) {
        this.listeners = listeners;
        Thread thread = new Thread(() -> listenForDataAndUpdateListeners(socket));
        thread.start();
    }

    private void listenForDataAndUpdateListeners(Socket socket) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
            this.outputStream = outputStream;
            connected = true;
            while (connected) {
                try {
                    Object nextObject = inputStream.readObject();
                    listeners.forEach(listener -> listener.onData(this, nextObject));
                } catch (IOException e) {
                    e.printStackTrace();
                    connected = false;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            listeners.forEach(listener -> listener.onClose(this));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized public void send(Object object) {
        if (!connected) return;
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
}
