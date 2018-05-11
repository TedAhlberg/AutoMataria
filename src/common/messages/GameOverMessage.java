package common.messages;

/**
 * @author eriklundow
 */
public class GameOverMessage extends Message {
    private static final long serialVersionUID = 1L;

    private int timeUntilNextGame;

    public GameOverMessage(int timeUntilNextGame) {
        this.timeUntilNextGame = timeUntilNextGame;
    }

    public int getTimeUntilNextGame() {
        return timeUntilNextGame;
    }
}
