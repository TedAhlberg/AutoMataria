package gameclient.interfaces.gamescreen;

/**
 * @author Johannes Blüml
 */
public interface GameServerListener {
    void onConnect();

    void onDisconnect();

    void onData(Object data);
}
