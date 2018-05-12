package common.messages;

import gameobjects.Player;

import java.util.HashMap;

/**
 * @author Johannes Blüml
 */
public class ScoreUpdateMessage extends Message {
    private final HashMap<Player, Integer> scores;
    private final int playedRounds;
    private final int highestScore;
    private final boolean gameOver;

    public ScoreUpdateMessage(HashMap<Player, Integer> scores, int playedRounds, int highestScore, boolean gameOver) {
        this.scores = scores;
        this.playedRounds = playedRounds;
        this.highestScore = highestScore;
        this.gameOver = gameOver;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public HashMap<Player, Integer> getScores() {
        return scores;
    }

    public int getPlayedRounds() {
        return playedRounds;
    }

    public int getHighestScore() {
        return highestScore;
    }
}
