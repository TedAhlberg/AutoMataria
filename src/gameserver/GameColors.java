package gameserver;

import java.awt.*;
import java.util.LinkedList;
import java.util.Random;

/**
 * @author eriklundow
 */
public class GameColors {
    private LinkedList<Color> availableColors = new LinkedList<>();
    private Random rand = new Random();

    public GameColors() {
        availableColors.add(new Color(0xff6400));
        availableColors.add(new Color(0xD80211));
        availableColors.add(new Color(0xff148c));
        availableColors.add(new Color(0xAA88B4));
        availableColors.add(new Color(0x8147ff));
        availableColors.add(new Color(0x4147FF));
        availableColors.add(new Color(0x1e96ff));
        availableColors.add(new Color(0x5DBFCC));
        availableColors.add(new Color(0xB4A76E));
        availableColors.add(new Color(0xD4D215));
        availableColors.add(new Color(0xc8ff32));
        availableColors.add(new Color(0x85CC81));
        availableColors.add(new Color(0x1cb874));
        availableColors.add(new Color(0x23D820));
        availableColors.add(new Color(0xFFFFFF));
        availableColors.add(new Color(0x9A9A9A));
    }

    synchronized Color exchangeColor(Color color) {
        Color colorSelect = availableColors.remove(rand.nextInt(availableColors.size()));
        availableColors.add(color);

        return colorSelect;
    }

    synchronized void giveBackColor(Color color) {
        availableColors.add(color);
    }

    synchronized public Color takeColor() {
        return availableColors.remove(rand.nextInt(availableColors.size()));
    }
}
