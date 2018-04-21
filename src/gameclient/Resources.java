package gameclient;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Dante Håkansson
 * @author Johannes Bluml
 * @author Erik Lundow
 */
public class Resources {
    public static Path imagePath = FileSystems.getDefault().getPath("resources", "images");
    public static Path musicPath = FileSystems.getDefault().getPath("resources", "Music");
    public static Path buttonPath = FileSystems.getDefault().getPath("resources", "images", "Buttons");
    public static Path sfxPath = FileSystems.getDefault().getPath("resources", "SFX");
    private static HashMap<String, BufferedImage> images = new HashMap<>();

    private Resources() {
    }

    public static BufferedImage getImage(Path path, String name) {

        if (images.containsKey(name)) {
            return images.get(name);
        }
        Path filePath = path.resolve(name);
        try {
            BufferedImage image = convertToCompatibleImage(ImageIO.read(filePath.toFile()));
            images.put(name, image);
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static BufferedImage getButtonImage(String name) {
        return getImage(buttonPath, name);

    }

    public static BufferedImage getImage(String name) {
        if (images.containsKey(name)) {
            return images.get(name);
        }
        Path filePath = imagePath.resolve(name);
        try {
            BufferedImage image = convertToCompatibleImage(ImageIO.read(filePath.toFile()));
            images.put(name, image);
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String[] getImageList() {
        return getFileList(imagePath);
    }

    public static String[] getMusicList() {
        return getFileList(musicPath);
    }

    public static String[] getFileList(Path directory) {
        ArrayList<String> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    result.add(path.getFileName().toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toArray(new String[0]);
    }

    private static BufferedImage convertToCompatibleImage(BufferedImage image) {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        GraphicsConfiguration config = device.getDefaultConfiguration();

        // Image is already compatible
        if (image.getColorModel().equals(config.getColorModel())) {
            return image;
        }

        // Create a compatible image
        BufferedImage compatibleImage = config.createCompatibleImage(image.getWidth(), image.getHeight(),
                image.getTransparency());
        Graphics2D g2d = (Graphics2D) compatibleImage.getGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return compatibleImage;
    }
}
