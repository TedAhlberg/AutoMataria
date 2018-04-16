package gameserver;

import java.awt.*;
import java.util.*;

/**
 * @author Johannes Blüml
 * @author Dante Håkansson
 */
public class StartingPositions {
    private Random random = new Random();
    private LinkedList<Point> positions = new LinkedList<>();
    private LinkedList<Point> availablePositions = new LinkedList<>();

    public void set(Point[] startingPositions) {
        positions.clear();
        positions.addAll(Arrays.asList(startingPositions));
        reset();
    }

    public void reset() {
        availablePositions.clear();
        availablePositions.addAll(positions);
    }

    public Point getNext() throws NoSuchElementException {
        if (availablePositions.size() == 0) throw new NoSuchElementException();
        return availablePositions.remove(random.nextInt(availablePositions.size()));
    }

    public Point getOneRandom(Dimension grid) {
        int x = random.nextInt(grid.width);
        int y = random.nextInt(grid.height);
        return new Point(x, y);
    }

    public Point[] generate(Dimension grid, int amount) {
        positions.clear();
        if (amount <= 5) generate2to5(grid, amount);
        else if (amount <= 9) generate9(grid);
        else if (amount <= 16) generate16(grid);
        else generateRandom(grid, amount);
        reset();
        return positions.toArray(new Point[0]);
    }

    public void generateRandom(Dimension grid, int amount) {
        positions.clear();
        while (positions.size() < amount) {
            Point point = getOneRandom(grid);
            if (!positions.contains(point)) {
                positions.add(point);
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

        positions.add(new Point(xLeft, yTop));
        positions.add(new Point(xRight, yBottom));
        if (amount > 2) {
            positions.add(new Point(xRight, yTop));
            positions.add(new Point(xLeft, yBottom));
        }
        if (amount == 5) {
            positions.add(new Point(xCenter, yCenter));
        }
    }

    private void generate9(Dimension grid) {
        int xEvery = grid.width / 3;
        int xBegin = xEvery / 2;

        int yEvery = grid.height / 3;
        int yBegin = yEvery / 2;

        // First row positions
        positions.add(new Point(xBegin, yBegin));
        positions.add(new Point(xBegin + xEvery, yBegin));
        positions.add(new Point(xBegin + xEvery + xEvery, yBegin));
        // Center row positions
        positions.add(new Point(xBegin, yBegin + yEvery));
        positions.add(new Point(xBegin + xEvery, yBegin + yEvery));
        positions.add(new Point(xBegin + xEvery + xEvery, yBegin + yEvery));
        // Last row positions
        positions.add(new Point(xBegin, yBegin + yEvery + yEvery));
        positions.add(new Point(xBegin + xEvery, yBegin + yEvery + yEvery));
        positions.add(new Point(xBegin + xEvery + xEvery, yBegin + yEvery + yEvery));
    }

    private void generate16(Dimension grid) {
        int xEvery = grid.width / 4;
        int xBegin = xEvery / 2;

        int yEvery = grid.height / 4;
        int yBegin = yEvery / 2;

        // First row positions
        positions.add(new Point(xBegin, yBegin));
        positions.add(new Point(xBegin + yEvery, yBegin));
        positions.add(new Point(xBegin + yEvery * 2, yBegin));
        positions.add(new Point(xBegin + yEvery * 3, yBegin));
        // Second row positions
        positions.add(new Point(xBegin, yBegin + yEvery));
        positions.add(new Point(xBegin + yEvery, yBegin + yEvery));
        positions.add(new Point(xBegin + yEvery * 2, yBegin + yEvery));
        positions.add(new Point(xBegin + yEvery * 3, yBegin + yEvery));
        // Third row positions
        positions.add(new Point(xBegin, yBegin + yEvery * 2));
        positions.add(new Point(xBegin + yEvery, yBegin + yEvery * 2));
        positions.add(new Point(xBegin + yEvery * 2, yBegin + yEvery * 2));
        positions.add(new Point(xBegin + yEvery * 3, yBegin + yEvery * 2));
        // Forth row positions
        positions.add(new Point(xBegin, yBegin + yEvery * 3));
        positions.add(new Point(xBegin + yEvery, yBegin + yEvery * 3));
        positions.add(new Point(xBegin + yEvery * 2, yBegin + yEvery * 3));
        positions.add(new Point(xBegin + yEvery * 3, yBegin + yEvery * 3));
    }
}
