package test;

import common.GameServerSettings;
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

        GameServerSettings settings = new GameServerSettings();
        settings.name = args[0];
        settings.port = Integer.parseInt(args[1]);
        settings.tickRate = Integer.parseInt(args[2]);
        settings.amountOfTickBetweenUpdates = Integer.parseInt(args[3]);
        settings.playerSpeed = Integer.parseInt(args[4]);
        settings.mapPool = new String[]{args[5]};

        GameServer server = new GameServer(settings);
        server.start();
    }
}
