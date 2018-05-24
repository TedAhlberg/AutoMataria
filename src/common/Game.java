package common;

import java.net.InetSocketAddress;

/**
 * Starts the Auto-Mataria Game
 *
 * @author Johannes Bluml
 */
public class Game {
    public static final String TITLE = "Auto-Mataria";
    public static final int GRID_PIXEL_SIZE = 100;
    public static final int LOCAL_UDP_PORT = 63211;
    public static final InetSocketAddress MAIN_SERVER = new InetSocketAddress("85.24.246.53", 30000);
}
