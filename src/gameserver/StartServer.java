package gameserver;

import common.GameServerSettings;
import common.Maps;

/**
 * @author Johannes Blüml
 */
public class StartServer {
    public static void main(String[] args) {
        if (args.length < 5) {
            System.out.println("Required parameters: <Server Name> <Server Port> <TickRate> <Amount of updates per tick> <Player speed> [<Map name>]");
            System.exit(1);
        }

        GameServerSettings settings = new GameServerSettings();
        settings.name = args[0];
        settings.port = Integer.parseInt(args[1]);
        settings.tickRate = Integer.parseInt(args[2]);
        settings.amountOfTickBetweenUpdates = Integer.parseInt(args[3]);
        settings.playerSpeed = Integer.parseInt(args[4]);
        if (args.length == 6) settings.mapPool = new String[]{args[5]};
        else settings.mapPool = Maps.getInstance().getMapList().toArray(new String[0]);

        settings.roundLimit = 10;
        settings.scoreLimit = 0;

        settings.forceMovePlayerCountdown = 1000;
        settings.gameOverCountdown = 15000;
        settings.roundOverCountdown = 5000;
        settings.newGameCountdown = 5000;

        GameServer server = new GameServer(settings);
        server.start();
    }
}
