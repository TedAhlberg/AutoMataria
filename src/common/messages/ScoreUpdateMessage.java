package common.messages;

import gameobjects.Player;

import java.util.HashMap;

/**
 * @author Johannes Blüml
 */
public class ScoreUpdateMessage extends Message {
    private final HashMap<Player, Integer> scores;
    private final int scoreLimit;
    private final int roundLimit;
    private final int playedRounds;
    private boolean gameOver;

    public ScoreUpdateMessage(HashMap<Player, Integer> scores, int scoreLimit, int roundLimit, int playedRounds, boolean gameOver) {
        this.scores = scores;
        this.scoreLimit = scoreLimit;
        this.roundLimit = roundLimit;
        this.playedRounds = playedRounds;
        this.gameOver = gameOver;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public HashMap<Player, Integer> getScores() {
        return scores;
    }

    public int getScoreLimit() {
        return scoreLimit;
    }

    public int getRoundLimit() {
        return roundLimit;
    }

    public int getPlayedRounds() {
        return playedRounds;
    }
}
