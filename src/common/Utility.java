package common;

import gameclient.Game;
import gameobjects.Player;

import java.awt.*;
import java.util.Collection;

/**
 * @author Johannes Blüml
 */
public class Utility {
    public static int clamp(int var, int min, int max) {
        if (var >= max) return max;
        if (var <= min) return min;
        return var;
    }

    public static double getReadyPlayerPercentage(Collection<Player> players) {
        if (players == null) {
            return 0;
        }
        int readyPlayers = 0;
        for (Player player : players) {
            if (player.isReady()) {
                readyPlayers += 1;
            }
        }
        double readyPlayerPercentage = ((double) readyPlayers / (double) players.size());
        readyPlayerPercentage = Math.round(readyPlayerPercentage * 100);
        return readyPlayerPercentage;
    }

    public static Point canChangeDirection(Direction direction, Point point, int speed) {
        switch (direction) {
            case Up:
                for (int i = 0; i < speed; i++) {
                    if ((point.y - i) % Game.GRID_PIXEL_SIZE == 0) {
                        return new Point(point.x, point.y - i);
                    }
                }
                break;
            case Down:
                for (int i = 0; i < speed; i++) {
                    if ((point.y + i) % Game.GRID_PIXEL_SIZE == 0) {
                        return new Point(point.x, point.y + i);
                    }
                }
                break;
            case Left:
                for (int i = 0; i < speed; i++) {
                    if ((point.x - i) % Game.GRID_PIXEL_SIZE == 0) {
                        return new Point(point.x - i, point.y);
                    }
                }
                break;
            case Right:
                for (int i = 0; i < speed; i++) {
                    if ((point.x + i) % Game.GRID_PIXEL_SIZE == 0) {
                        return new Point(point.x + i, point.y);
                    }
                }
                break;
        }
        return null;
    }
}
