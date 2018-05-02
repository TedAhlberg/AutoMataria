package common.messages;

import gameobjects.Player;

import java.util.HashMap;

/**
 * @author eriklundow
 */
public class GameOverMessage extends Message {
    private static final long serialVersionUID = 1L;

    private HashMap<Player, Integer> scores;

    public GameOverMessage(HashMap<Player, Integer> scores) {
        this.scores = scores;
    }

    public HashMap<Player, Integer> getScores() {
        return scores;
    }
}
