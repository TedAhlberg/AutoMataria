package common.messages;

/**
 * @author eriklundow
 */
public class NewGameMessage extends Message {
    private static final long serialVersionUID = 1L;

    private int timeUntilGameBegins;

    public NewGameMessage(int timeUntilGameBegins) {
        this.timeUntilGameBegins = timeUntilGameBegins;

    }

    public int getTimeUntileGameBegins() {
        return timeUntilGameBegins;
    }
}
