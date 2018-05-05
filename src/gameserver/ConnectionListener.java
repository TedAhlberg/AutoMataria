package gameserver;

/**
 * @author Johannes Blüml
 */
public interface ConnectionListener {
    void onServerConnectionStarted();
    void onServerConnectionStopped();
    void onDataFromClient(Client client, Object value);
    void onClientDisconnect(Client client);
}
