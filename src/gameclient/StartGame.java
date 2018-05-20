package gameclient;

/**
 * Starts the AutoMataria GameClient
 *
 * @author Johannes Blüml
 */
public class StartGame {
    public static void main(String[] args) {
        if (args.length == 1) {
            new Game(args[0]);
        } else {
            new Game();
        }
    }
}
