package gameclient;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
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
    public static Path imagePath = FileSystems.getDefault().getPath("resources", "images");
    public static Path musicPath = FileSystems.getDefault().getPath("resources", "Music");

    private Resources() {
    }

    public static BufferedImage getImage(String name) {
        if (images.containsKey(name)) {
            return images.get(name);
        }
        Path filePath = imagePath.resolve(name);
        try {
            BufferedImage image = ImageIO.read(filePath.toFile());
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
}
