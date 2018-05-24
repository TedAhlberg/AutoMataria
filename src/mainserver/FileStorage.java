package mainserver;

import common.Highscore;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;

/**
 * @author Henrik Olofsson & Johannes Blüml
 */
public class FileStorage {
    private Path filePath = Paths.get("Highscores");
    private File highscoreFile = filePath.resolve("Highscore.ser").toFile();

    public FileStorage() {
        if (!Files.exists(filePath)) {
            try {
                Files.createDirectories(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void save(ArrayList<Highscore> highscores) {
        try (ObjectOutputStream writer = new ObjectOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(highscoreFile)))) {
            writer.writeObject(highscores);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized ArrayList<Highscore> read() {
        try (ObjectInputStream reader = new ObjectInputStream(
                new BufferedInputStream(
                        new FileInputStream(highscoreFile)))) {
            Object object = reader.readObject();
            return (ArrayList<Highscore>) object;
        } catch (IOException e) {
            e.getStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

}


