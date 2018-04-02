package common;

/**
 * @author Johannes Blüml
 */
public class ID {
    private static int counter = 0;
    private ID() {}
    synchronized public static int getNext() {
        return counter++;
    }
}
