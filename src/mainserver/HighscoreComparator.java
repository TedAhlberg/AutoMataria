package mainserver;

import common.Highscore;

import java.util.Comparator;

public class HighscoreComparator implements Comparator<Highscore> {

    public int compare(Highscore highscore, Highscore highscore2) {
        return highscore2.getHighscore() - highscore.getHighscore();
    }
}
