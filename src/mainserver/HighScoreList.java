package mainserver;

import java.util.ArrayList;

public class HighScoreList {
    private ArrayList<HighScore2> highscoreList = new ArrayList<HighScore2>();
    
    public synchronized void addAndReplace(HighScore2 highScore) {
        int currentHighscore;
        String name;
        
        for(int index = 0; index < highscoreList.size(); index++) {
            if(highscoreList.get(index).getPlayerName().equals(highScore.getPlayerName())) {
               name = highscoreList.get(index).getPlayerName();
               currentHighscore = highscoreList.get(index).getHighscore();
               int newHighscore = currentHighscore + highScore.getHighscore();
               
               if(newHighscore <= 0) {
                   newHighscore = 0;
                   highscoreList.remove(index);
                   HighScore2 replaceHighScore = new HighScore2(name, newHighscore);
                   highscoreList.add(index, replaceHighScore);
               }
               else if(newHighscore > 0) {
                   HighScore2 replaceHighScore = new HighScore2(name, newHighscore);
                   highscoreList.remove(index);
                   highscoreList.add(index, replaceHighScore);
               }
               
            }
        }
    }
    
    public void replaceName(String oldUsername, String newUsername) {
        for(int index = 0; index < highscoreList.size(); index++) {
            if(highscoreList.get(index).getPlayerName().equals(oldUsername)) {
                highscoreList.get(index).setPlayerName(newUsername);
            }
        }
    }
    
    
    public synchronized ArrayList<HighScore2> getSortedList() {
        highscoreList.sort(new HighScoreComparator());
        return highscoreList;
    }

}
