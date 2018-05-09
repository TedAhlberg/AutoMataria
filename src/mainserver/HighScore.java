package mainserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class HighScore {
    private HashMap<String, Integer> highScores = new HashMap<>();
    private static boolean ASC = true;
    private static boolean DESC = false;
    
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
    
    public synchronized void sort() {
//    	ArrayList<String> usernames = new ArrayList<String>(highScores.keySet());
    	ArrayList<Integer> highscores = new ArrayList<Integer>(highScores.values());
    	Collections.sort(highscores);
    	
    }
  
}
