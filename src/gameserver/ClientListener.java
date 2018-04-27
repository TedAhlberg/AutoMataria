package gameserver;

/**
 * @author Johannes Blüml
 */
public interface ClientListener {
    void onData(Client client, Object value);
    void onClose(Client client);
}
