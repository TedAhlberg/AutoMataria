package mainserver;

import common.Highscore;

import java.util.ArrayList;

public class HighscoreList {
    private ArrayList<Highscore> highscoreList;

    public HighscoreList(ArrayList<Highscore> highscoreList) {
        this.highscoreList = highscoreList;
    }

    public void HighScoreList() {
        highscoreList = new ArrayList<>();
    }

    public synchronized void addAndReplace(Highscore highScore) {
        int currentHighscore;
        String name;
        
            if(!highscoreList.contains(highScore)) {
                highscoreList.add(highScore);
            } else {
               int index = highscoreList.indexOf(highScore);
               name = highScore.getPlayerName();
               currentHighscore = highscoreList.get(index).getHighscore();
               int newHighscore = currentHighscore + highScore.getHighscore();
               
               if(newHighscore <= 0) {
                   newHighscore = 0;
                   highscoreList.remove(index);
                   Highscore replaceHighScore = new Highscore(name, newHighscore);
                   highscoreList.add(index, replaceHighScore);
               }
               else if(newHighscore > 0) {
                   Highscore replaceHighScore = new Highscore(name, newHighscore);
                   highscoreList.remove(index);
                   highscoreList.add(index, replaceHighScore);
               }
        }
    }

    public void replaceName(String oldUsername, String newUsername) {
        for (int index = 0; index < highscoreList.size(); index++) {
            if (highscoreList.get(index).getPlayerName().equals(oldUsername)) {
                highscoreList.get(index).setPlayerName(newUsername);
            }
        }
    }

    public synchronized ArrayList<Highscore> getSortedList() {
        highscoreList.sort(new HighscoreComparator());
        return highscoreList;
    }
}
