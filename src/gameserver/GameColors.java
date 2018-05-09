package gameserver;

import java.awt.*;
import java.util.LinkedList;
import java.util.Random;

/**
 * @author eriklundow
 */
public class GameColors {
    private LinkedList<Color> allColors = new LinkedList<Color>();
    private LinkedList<Color> availableColors = new LinkedList<>();
    private Random rand = new Random();
    private Color colorSelect;

    public GameColors() {
        allColors.add(new Color(0xff6400));
        allColors.add(new Color(0xD80211));
        allColors.add(new Color(0xff148c));
        allColors.add(new Color(0xAA88B4));
        allColors.add(new Color(0x8147ff));
        allColors.add(new Color(0x4147FF));
        allColors.add(new Color(0x1e96ff));
        allColors.add(new Color(0x5DBFCC));
        allColors.add(new Color(0xB4A76E));
        allColors.add(new Color(0xD4D215));
        allColors.add(new Color(0xc8ff32));
        allColors.add(new Color(0x85CC81));
        allColors.add(new Color(0x1cb874));
        allColors.add(new Color(0x23D820));
        allColors.add(new Color(0xFFFFFF));
        allColors.add(new Color(0x9A9A9A));

        availableColors.addAll(allColors);
    }

    synchronized Color exchangeColor(Color color) {
        colorSelect = availableColors.remove(rand.nextInt(availableColors.size()));
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
