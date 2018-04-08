package gameserver;

import java.awt.*;
import java.util.*;

/**
 * @author Johannes Blüml
 * @author Dante Håkansson
 */
public class StartingPositions {
    private Random random = new Random();
    private LinkedList<Point> availablePositions = new LinkedList<>();

    public Point getOneRandom(Dimension grid) {
        int x = random.nextInt(grid.width);
        int y = random.nextInt(grid.height);
        return new Point(x, y);
    }

    synchronized public void generateRandom(Dimension grid, int amount) {
        Random random = new Random();

        availablePositions.clear();
        while (availablePositions.size() < amount) {
            Point point = getOneRandom(grid);
            if (!availablePositions.contains(point)) {
                availablePositions.add(point);
            }
        }
    }

    synchronized public void generateFair(Dimension grid, int amount) {
        if (amount > 5) {
            // Only 5 positions supported for now
            generateRandom(grid, amount);
            return;
        }

        int xLeft = (grid.width / 2) / 2;
        int xRight = (grid.width / 2) + xLeft;
        int yTop = (grid.height / 2) / 2;
        int yBottom = (grid.height / 2) + yTop;
        int xCenter = grid.width / 2;
        int yCenter = grid.height / 2;

        availablePositions.clear();
        availablePositions.add(new Point(xLeft, yTop));
        availablePositions.add(new Point(xRight, yBottom));
        availablePositions.add(new Point(xRight, yTop));
        availablePositions.add(new Point(xLeft, yBottom));
        availablePositions.add(new Point(xCenter, yCenter));
    }

    synchronized public Point getNext() throws NoSuchElementException {
        return availablePositions.removeFirst();
    }
}
