package common.messages;

import gameobjects.Player;

import java.util.HashMap;

/**
 * @author eriklundow
 */
public class GameOverMessage extends Message {
    private static final long serialVersionUID = 1L;

    private HashMap<Player, Integer> scores;
    private int timeUntilNextGame;

    public GameOverMessage(HashMap<Player, Integer> scores, int timeUntilNextGame) {
        this.scores = scores;
        this.timeUntilNextGame = timeUntilNextGame;
    }

    public HashMap<Player, Integer> getScores() {
        return scores;
    }

    public int getTimeUntilNextGame() {
        return timeUntilNextGame;
    }
}
