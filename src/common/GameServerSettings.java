package common;

import java.io.Serializable;

/**
 * @author Johannes Blüml
 */
public class GameServerSettings implements Serializable {
    public static final long serialVersionUID = 1L;

    public String name = "Unknown";
    public String[] mapPool = {"Small Map 1"};
    public int port = 32000;
    public int tickRate = 75;
    public int amountOfTickBetweenUpdates = 2;
    public int playerSpeed = 50;
    public int newGameCountdown = 5000;
    public int roundOverCountdown = 5000;
    public int gameOverCountdown = 10000;
    public int scoreLimit = 2;
    public int roundLimit = 3;
}
