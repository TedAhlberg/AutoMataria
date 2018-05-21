package mainserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class HighScoreList {
    private ArrayList<HighScore2> highscoreList;
    
    public void HighScoreList() {
        
    }

    public HighScoreList(ArrayList<HighScore2> highscoreList) {
        this.highscoreList = highscoreList;
    }

    public synchronized void addAndReplace(HighScore2 highScore) {
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

    public void replaceName(String oldUsername, String newUsername) {
        for (int index = 0; index < highscoreList.size(); index++) {
            if (highscoreList.get(index).getPlayerName().equals(oldUsername)) {
                highscoreList.get(index).setPlayerName(newUsername);
            }
        }
    }

    public synchronized ArrayList<HighScore2> getSortedList() {
        highscoreList.sort(new HighScoreComparator());
        return highscoreList;
    }
    
//    public static void main(String[] args) {
//        HighScoreList hsl = new HighScoreList();
//        HighScore2 highscore1 = new HighScore2("Henko", 200);
//        HighScore2 highscore2 = new HighScore2("Henkomannen", 250);
//        HighScore2 highscore3 = new HighScore2("erik", 400);
//        HighScore2 highscore4 = new HighScore2("Ted", 3000);
        
//        hsl.addAndReplace(highscore1);
//        hsl.addAndReplace(highscore2);
//        hsl.addAndReplace(highscore3);
//        hsl.addAndReplace(highscore4);
//        
//        ArrayList<HighScore2> hslSorted = hsl.getSortedList();
//        System.out.println(hslSorted.size());
//        for(HighScore2 hs2 : hslSorted) {
//            System.out.println(hs2.toString());
//        }
//        
//    }

//    public static void main(String[] args) {
//        HashMap<String, Integer> scores = new HashMap<>();
//        scores.put("asd", 3213);
//        scores.put("BRUM", 123);
//        new MainServerClient().sendGameScore(scores);
//
//        ArrayList<HighScore2> highscores = new gameclient.interfaces.highscorescreen.MainServerClient().getHighscores();
//        System.out.println(highscores);
//}

}
