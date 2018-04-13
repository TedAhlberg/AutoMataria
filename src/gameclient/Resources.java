package gameclient;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

/**
 * 
 * @author Dante Håkansson
 * @author Johannes Bluml
 *
 */
public class Resources {
    private static HashMap<String, BufferedImage> images = new HashMap<>();
    private static String prefix = "resources/images/";

    private Resources() {
    }

    public static BufferedImage getImage(String name) {
        if (images.containsKey(name)) {
            return images.get(name);
        }
        try {
            BufferedImage image = ImageIO.read(new File(prefix + name));
            images.put(name, image);
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
