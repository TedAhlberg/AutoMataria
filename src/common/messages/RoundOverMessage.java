package common.messages;

/**
 * @author Johannes Blüml
 */
public class RoundOverMessage extends GameOverMessage {
    public RoundOverMessage(int timeUntilNextGame) {
        super(timeUntilNextGame);
    }
}
