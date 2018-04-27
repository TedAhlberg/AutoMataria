package gameclient;

import common.Utility;

import javax.imageio.ImageIO;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Handles all resources used by the Game like Images, Sounds, Music and Fonts.
 *
 * @author Dante Håkansson
 * @author Johannes Bluml
 * @author Erik Lundow
 */
public class Resources {
    private static Resources instance;
    public static Font defaultFont, titleFont;
    public static Path fontPath = FileSystems.getDefault().getPath("resources", "fonts");
    public static Path imagePath = FileSystems.getDefault().getPath("resources", "images");
    public static Path musicPath = FileSystems.getDefault().getPath("resources", "Music");
    public static Path buttonPath = FileSystems.getDefault().getPath("resources", "images", "Buttons");
    public static Path sfxPath = FileSystems.getDefault().getPath("resources", "SFX");
    private static HashMap<String, BufferedImage> images = new HashMap<>();

    private Resources() {
        try {
            File orbitronFile = fontPath.resolve("Orbitron Bold.ttf").toFile();
            Font orbitronFont = Font.createFont(Font.TRUETYPE_FONT, orbitronFile);
            defaultFont = orbitronFont.deriveFont(Font.BOLD, 12);
            titleFont = orbitronFont.deriveFont(Font.BOLD, 30);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(orbitronFont);
        } catch (IOException | FontFormatException e) {
            System.out.println("Failed to load font.");
            e.printStackTrace();
        }
    }

    public static Resources getInstance() {
        if (instance == null) {
             instance = new Resources();
        }
        return instance;
    }

    public static BufferedImage getImage(Path path, String name) {

        if (images.containsKey(name)) {
            return images.get(name);
        }
        Path filePath = path.resolve(name);
        try {
            BufferedImage image = Utility.convertToCompatibleImage(ImageIO.read(filePath.toFile()));
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
            BufferedImage image = Utility.convertToCompatibleImage(ImageIO.read(filePath.toFile()));
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

    public Font getDefaultFont() {
        return defaultFont;
    }

    public Font getTitleFont() {
        return titleFont;
    }
}
