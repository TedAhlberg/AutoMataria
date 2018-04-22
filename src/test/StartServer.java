package test;

import common.GameMap;
import common.Maps;
import gameserver.GameServer;

/**
 * @author Johannes Blüml
 */
public class StartServer {
    public static void main(String[] args) {
        if (args.length < 6) {
            System.out.println("Required parameters: <Server Name> <Server Port> <TickRate> <Amount of updates per tick> <Player speed> <Map name>");
            System.exit(1);
        }

        String serverName = args[0];
        int port = Integer.parseInt(args[1]);
        int tickRate = Integer.parseInt(args[2]);
        int amountOfUpdatesPerTick = Integer.parseInt(args[3]);
        int playerSpeed = Integer.parseInt(args[4]);
        GameMap map = Maps.getInstance().get(args[5]);

        new GameServer(serverName, port, tickRate, amountOfUpdatesPerTick, playerSpeed, map);
    }
}
