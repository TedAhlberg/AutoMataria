package mainserver;

import java.util.Comparator;

public class HighScoreComparator implements Comparator<HighScore2> {

    public int compare(HighScore2 highscore, HighScore2 highscore2) {
        return highscore.getHighscore() - highscore2.getHighscore();
    }

}
