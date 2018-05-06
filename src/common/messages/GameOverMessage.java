package common.messages;

import gameobjects.Player;

import java.util.HashMap;

/**
 * @author eriklundow
 */
public class GameOverMessage extends Message {
    private static final long serialVersionUID = 1L;

    private HashMap<Player, Integer> roundScores;
    private HashMap<Player, Integer> accumulatedScores;
    private int timeUntilNextGame;

    public GameOverMessage(HashMap<Player, Integer> roundScores, HashMap<Player, Integer> accumulatedScores, int timeUntilNextGame) {
        this.roundScores = roundScores;
        this.accumulatedScores = accumulatedScores;
        this.timeUntilNextGame = timeUntilNextGame;
    }

    public HashMap<Player, Integer> getRoundScores() {
        return roundScores;
    }

    public HashMap<Player, Integer> getAccumulatedScores() {
        return accumulatedScores;
    }

    public int getTimeUntilNextGame() {
        return timeUntilNextGame;
    }
}
