package common;

import gameclient.Resources;

import java.io.*;
import java.nio.file.*;

/**
 * Singleton which is used to save and load GameMaps from the resources/maps directory
 *
 * @author Johannes Blüml
 */
public class Maps {
    public static Maps instance = new Maps();
    private Path directory = FileSystems.getDefault().getPath("resources", "maps");

    private Maps() {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Maps getInstance() {
        return instance;
    }

    synchronized public String[] getMapList() {
        return Resources.getFileList(directory);
    }

    synchronized public void remove(String name) {
        Path filePath = directory.resolve(name);
        try {
            Files.delete(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized public GameMap get(String name) {
        if (name == null) return null;
        Path filePath = directory.resolve(name);
        try (ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(filePath))) {
            return (GameMap) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    synchronized public void save(GameMap map) {
        Path filePath = directory.resolve(map.getName());
        try (ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            outputStream.writeObject(map);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
