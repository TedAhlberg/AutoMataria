package mainserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileStorage {
    String fileName = "HighScores/HighScore.ser";
    private HighScore highScores;
    private Object object;
    
    
    public synchronized void saveToDisk(String userName, int highScore) {
        highScores = getHighScores();

        if(highScore > highScores.get(userName)) {
            try (ObjectOutputStream writer = new ObjectOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(fileName)))){
                highScores.put(userName, highScore);
                writer.writeObject(highScores);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
        
        public HighScore getHighScores() {
            try (ObjectInputStream reader = new ObjectInputStream(
                    new BufferedInputStream(
                            new FileInputStream(fileName)))){
                object = reader.readObject();
                object = (HighScore) highScores; 
            } catch (IOException e) {
                e.getStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return highScores;
        }
    }

