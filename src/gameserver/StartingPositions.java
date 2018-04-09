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

    public Point getNext() throws NoSuchElementException {
        if (availablePositions.size() == 0) throw new NoSuchElementException();
        return availablePositions.remove(random.nextInt(availablePositions.size()));
    }

    public Point getOneRandom(Dimension grid) {
        int x = random.nextInt(grid.width);
        int y = random.nextInt(grid.height);
        return new Point(x, y);
    }

    public void generate(Dimension grid, int amount) {
        if (amount <= 5) generate2to5(grid, amount);
        else if (amount <= 9) generate9(grid);
        else if (amount <= 16) generate16(grid);
        else generateRandom(grid, amount);
    }

    private void generateRandom(Dimension grid, int amount) {
        availablePositions.clear();
        while (availablePositions.size() < amount) {
            Point point = getOneRandom(grid);
            if (!availablePositions.contains(point)) {
                availablePositions.add(point);
            }
        }
    }

    private void generate2to5(Dimension grid, int amount) {
        int xCenter = grid.width / 2;
        int xLeft = grid.width / 4;
        int xRight = xCenter + xLeft;

        int yCenter = grid.height / 2;
        int yTop = grid.height / 4;
        int yBottom = yCenter + yTop;

        availablePositions.clear();
        availablePositions.add(new Point(xLeft, yTop));
        availablePositions.add(new Point(xRight, yBottom));
        if (amount > 2) {
            availablePositions.add(new Point(xRight, yTop));
            availablePositions.add(new Point(xLeft, yBottom));
        }
        if (amount == 5) {
            availablePositions.add(new Point(xCenter, yCenter));
        }
    }

    private void generate9(Dimension grid) {
        int xEvery = grid.width / 3;
        int xBegin = xEvery / 2;

        int yEvery = grid.height / 3;
        int yBegin = yEvery / 2;

        availablePositions.clear();
        // First row positions
        availablePositions.add(new Point(xBegin, yBegin));
        availablePositions.add(new Point(xBegin + xEvery, yBegin));
        availablePositions.add(new Point(xBegin + xEvery + xEvery, yBegin));
        // Center row positions
        availablePositions.add(new Point(xBegin, yBegin + yEvery));
        availablePositions.add(new Point(xBegin + xEvery, yBegin + yEvery));
        availablePositions.add(new Point(xBegin + xEvery + xEvery, yBegin + yEvery));
        // Last row positions
        availablePositions.add(new Point(xBegin, yBegin + yEvery + yEvery));
        availablePositions.add(new Point(xBegin + xEvery, yBegin + yEvery + yEvery));
        availablePositions.add(new Point(xBegin + xEvery + xEvery, yBegin + yEvery + yEvery));
    }

    private void generate16(Dimension grid) {
        int xEvery = grid.width / 4;
        int xBegin = xEvery / 2;

        int yEvery = grid.height / 4;
        int yBegin = yEvery / 2;

        availablePositions.clear();
        // First row positions
        availablePositions.add(new Point(xBegin, yBegin));
        availablePositions.add(new Point(xBegin + yEvery, yBegin));
        availablePositions.add(new Point(xBegin + yEvery * 2, yBegin));
        availablePositions.add(new Point(xBegin + yEvery * 3, yBegin));
        // Second row positions
        availablePositions.add(new Point(xBegin, yBegin + yEvery));
        availablePositions.add(new Point(xBegin + yEvery, yBegin + yEvery));
        availablePositions.add(new Point(xBegin + yEvery * 2, yBegin + yEvery));
        availablePositions.add(new Point(xBegin + yEvery * 3, yBegin + yEvery));
        // Third row positions
        availablePositions.add(new Point(xBegin, yBegin + yEvery * 2));
        availablePositions.add(new Point(xBegin + yEvery, yBegin + yEvery * 2));
        availablePositions.add(new Point(xBegin + yEvery * 2, yBegin + yEvery * 2));
        availablePositions.add(new Point(xBegin + yEvery * 3, yBegin + yEvery * 2));
        // Forth row positions
        availablePositions.add(new Point(xBegin, yBegin + yEvery * 3));
        availablePositions.add(new Point(xBegin + yEvery, yBegin + yEvery * 3));
        availablePositions.add(new Point(xBegin + yEvery * 2, yBegin + yEvery * 3));
        availablePositions.add(new Point(xBegin + yEvery * 3, yBegin + yEvery * 3));
    }
}
