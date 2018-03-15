package gameserver;

/**
 * @author Johannes Blüml
 */
public interface ClientListener {
    void onConnect(Client client);
    void onData(Client client, Object value);
    void onClose(Client client);
}
