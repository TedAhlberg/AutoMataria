package common;

import gameclient.Resources;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Singleton which is used to save and load GameMaps from the resources/systemMaps userMapsPath
 *
 * @author Johannes Blüml
 */
public class Maps {
    private final static Maps instance = new Maps();
    private final String systemMapsPath = "maps/";
    private final ArrayList<String> systemMaps = new ArrayList<>();
    private final Path userMapsPath = FileSystems.getDefault().getPath("resources", "maps");

    private Maps() {
        systemMaps.add("BASIC ONE");
        systemMaps.add("CLEAN");
        systemMaps.add("LABYRINTH");
        systemMaps.add("PICKUP MADNESS");
        systemMaps.add("X");

        if (!Files.exists(userMapsPath)) {
            try {
                Files.createDirectories(userMapsPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Maps getInstance() {
        return instance;
    }

    synchronized public Collection<String> getMapList() {
        HashSet<String> maps = new HashSet<>();
        maps.addAll(systemMaps);
        maps.addAll(Resources.getFileList(userMapsPath));
        return maps;
    }

    synchronized public ArrayList<String> getUserMapList() {
        return new ArrayList<>(Resources.getFileList(userMapsPath));
    }

    synchronized public void remove(String name) {
        Path filePath = userMapsPath.resolve(name);
        try {
            Files.delete(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized public GameMap get(String name) {
        if (name == null) return null;

        Path filePath = userMapsPath.resolve(name);
        if (Files.exists(filePath)) {
            try (ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(filePath))) {
                return (GameMap) inputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }

        if (systemMaps.contains(name)) {
            try (InputStream inputStream = Resources.class.getClassLoader().getResourceAsStream(systemMapsPath + name);
                 ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                return (GameMap) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }

        return null;
    }

    synchronized public void save(GameMap map) {
        Path filePath = userMapsPath.resolve(map.getName());
        try (ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            outputStream.writeObject(map);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
