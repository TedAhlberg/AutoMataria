package mainserver;

import java.util.HashMap;

public class HighScore {
    private HashMap<String, Integer> highScores = new HashMap<>();
    
    public synchronized void put(String key, Integer value) {
        highScores.put(key, value);
    }
    
    public synchronized Integer get(String key) {
        Integer value = highScores.get(key);
        return value;
    }
    
    public synchronized boolean containsKey(String key) {
      return highScores.containsKey(key);
    }
    
    public synchronized void replace(String key, String newUserName) {
        int highScore = highScores.get(key);
        highScores.remove(key);
        highScores.put(newUserName, highScore);
    }
}
