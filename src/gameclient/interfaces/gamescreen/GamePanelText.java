package gameclient.interfaces.gamescreen;

import common.Game;
import gameclient.Resources;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * @author Ted Ahlberg
 */

public class GamePanelText {
    private Color textColor = new Color(255, 255, 255, 200);
    private Color textColorTransparent = new Color(255, 255, 255, 100);

    public void drawText(Graphics2D g2, String text, FontSize fontSize, Location location) {
        int panelSize = g2.getClipBounds().height;
        g2.setColor(textColor);
        switch (fontSize) {
            case Small:
                g2.setFont(Resources.defaultFont.deriveFont((float) (panelSize / 50)));
                break;
            case Large:
                g2.setFont(Resources.defaultFont.deriveFont((float) (panelSize / 19)));
                break;
            default:
                g2.setFont(Resources.defaultFont.deriveFont((float) (panelSize / 27)));
        }
        FontMetrics fontMetrics = g2.getFontMetrics();

        Point startPoint = new Point(panelSize / 2, panelSize / 2);
        switch (location) {
            case Top:
                startPoint = new Point(panelSize / 2, panelSize / 4);
                break;
            case Bottom:
                startPoint = new Point(panelSize / 2, panelSize / 4 * 3);
                break;
        }

        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            int down = i * g2.getFont().getSize();
            Rectangle2D bounds = fontMetrics.getStringBounds(line, g2);

            int x = (int) (startPoint.x - (bounds.getWidth() / 2));
            int y = (startPoint.y - g2.getFont().getSize()) + down;

            g2.drawString(line, x, y);
        }
    }

    public void drawTopLeftText(Graphics2D g2, String text) {
        int panelSize = g2.getClipBounds().height;
        Font smallFont = Resources.defaultFont.deriveFont((float) (panelSize / 50));

        int x = Game.GRID_PIXEL_SIZE * 2;
        int y = x + smallFont.getSize();

        g2.setColor(textColorTransparent);
        g2.setFont(smallFont);
        g2.drawString(text, x, y);
    }

    public enum FontSize {
        Small, Normal, Large
    }

    public enum Location {
        Top, Center, Bottom
    }
}
