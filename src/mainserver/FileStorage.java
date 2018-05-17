package mainserver;

import java.io.*;
import java.util.ArrayList;

/**
 * 
 * @author Henrik Olofsson & Johannes Blüml
 *
 */

public class FileStorage {
    String fileName = "resources/HighScores/HighScore.ser";
    
    public synchronized void save(ArrayList<HighScore2> highscores) {
            try (ObjectOutputStream writer = new ObjectOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(fileName)))){
                writer.writeObject(highscores);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    public synchronized ArrayList<HighScore2> read() {
            try (ObjectInputStream reader = new ObjectInputStream(
                    new BufferedInputStream(
                            new FileInputStream(fileName)))){
                Object object = reader.readObject();
                return (ArrayList<HighScore2>) object;
            } catch (IOException e) {
                e.getStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        return new ArrayList<>();
        }
        
    }


