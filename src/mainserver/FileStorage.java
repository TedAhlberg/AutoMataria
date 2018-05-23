package mainserver;

import common.Highscore;

import java.io.*;
import java.util.ArrayList;

/**
 * 
 * @author Henrik Olofsson & Johannes Blüml
 *
 */

public class FileStorage {
    String fileName = "resources/HighScores/HighScore.ser";

    public synchronized void save(ArrayList<Highscore> highscores) {
            try (ObjectOutputStream writer = new ObjectOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(fileName)))){
                writer.writeObject(highscores);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    public synchronized ArrayList<Highscore> read() {
            try (ObjectInputStream reader = new ObjectInputStream(
                    new BufferedInputStream(
                            new FileInputStream(fileName)))){
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


