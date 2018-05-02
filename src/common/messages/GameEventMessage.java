package common.messages;

/**
 * @author eriklundow
 */
public class GameEventMessage extends Message {
    private static final long serialVersionUID = 1L;

    public String data;

    public GameEventMessage(String data) {
        this.data = data;

    }
}
