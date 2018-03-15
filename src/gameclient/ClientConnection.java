package gameclient;

import java.io.*;
import java.net.*;
import java.util.function.*;

/**
 * @author Johannes Blüml
 */
public class ClientConnection {
    private ObjectOutputStream outputStream;

    public ClientConnection(Socket socket, Consumer<Object> listener) throws IOException {
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    listener.accept(inputStream.readObject());
                } catch (IOException e) {
                    Thread.currentThread().interrupt();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void send(Object object) {
        try {
            outputStream.writeObject(object);
        } catch (IOException e) {
            Thread.currentThread().interrupt();
        }
    }
}
