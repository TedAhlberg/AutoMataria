package mainserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author Henrik Olofsson & Johannes Blüml
 *
 */

public class FileStorage {
    String fileName = "HighScores/HighScore.ser";
    
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
        
        public synchronized HighScoreList read() {
            try (ObjectInputStream reader = new ObjectInputStream(
                    new BufferedInputStream(
                            new FileInputStream(fileName)))){
                Object object = reader.readObject();
                return (HighScoreList) object; 
            } catch (IOException e) {
                e.getStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } 
            return new HighScoreList();
        }
        
    }


