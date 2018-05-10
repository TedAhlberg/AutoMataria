package mainserver;

import java.util.ArrayList;

public class HighscoreList {
    private ArrayList<HighScore2> highscoreList = new ArrayList<>();
    private HighScore2 highScore;
    
    public HighscoreList() {
    }
    
    public void addAndReplace(HighScore2 highScore) {
        int currentHighscore;
        String playerName;
        for(int index = 0; index <= highscoreList.size(); index++) {
            if(highScore.getPlayer().getName().equals(highscoreList.get(index).getPlayer().getName())) {
//                currentHighscore = highscoreList.get(index).getHighscore();
//                playerName = highscoreList.get(index).getPlayer().getName();
//                highscoreList.remove(index);
                
            }
        }
    }

}
