package common;

/**
 * @author Johannes Blüml
 */
public class Trail extends Wall {

    public Trail(Player player) {
        super(player.getX(), player.getY(), player.getWidth(), player.getHeight(), player.getName() + "'s tail", player.getColor().darker().darker());

        if (player.getDirection() == Direction.Up) {
            y += height;
            height = player.getSpeed();
        } else if (player.getDirection() == Direction.Down) {
            y -= player.getSpeed();
            height = player.getSpeed();
        } else if (player.getDirection() == Direction.Left) {
            x += width;
            width = player.getSpeed();
        } else if (player.getDirection() == Direction.Right) {
            x -= player.getSpeed();
            width = player.getSpeed();
        }
    }

    public void grow(Direction direction, int size) {
        if (direction == Direction.Up) {
            y -= size;
            height += size;
        } else if (direction == Direction.Down) {
            height += size;
        } else if (direction == Direction.Left) {
            x -= size;
            width += size;
        } else if (direction == Direction.Right) {
            width += size;
        }
    }
}
