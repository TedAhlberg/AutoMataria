package common;

/**
 * Provides a unique identifier.
 * Mostly used to identify GameObjects so the same type does not equal each other.
 *
 * @author Johannes Blüml
 */
public class ID {
    private static int counter = 0;

    private ID() {}

    synchronized public static int getNext() {
        counter += 1;
        return counter;
    }
}
