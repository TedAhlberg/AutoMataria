package common;

import gameclient.*;
import gameserver.*;

/**
 * @author Johannes Blüml
 */
public class StartTestGame {
    public static void main(String[] args) {
        GameServer.main(args);
        Game.main(args);
        Game.main(args);
    }
}
