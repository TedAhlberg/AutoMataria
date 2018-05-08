package common.messages;

import gameobjects.Player;

import java.util.HashMap;

/**
 * @author Johannes Blüml
 */
public class RoundOverMessage extends GameOverMessage {
    public RoundOverMessage(HashMap<Player, Integer> roundScores, HashMap<Player, Integer> accumulatedScores, int timeUntilNextGame) {
        super(roundScores, accumulatedScores, timeUntilNextGame);
    }
}
