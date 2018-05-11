package mainserver;

import java.util.ArrayList;

import gameobjects.Player;

public class HighScoreList {
    private ArrayList<HighScore2> highscoreList = new ArrayList<HighScore2>();
    private Player player;
    
    public HighScoreList() {
    }
    
    
    public void addAndReplace(HighScore2 highScore) {
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
                   highscoreList.add(replaceHighScore);
               }
               
            }
        }
    }

}
