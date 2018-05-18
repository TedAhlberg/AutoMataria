package gameclient;

import common.Utility;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
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
    public static Font defaultFont, titleFont;
    public static String imagePath = "images/";
    public static String musicPath = "Music/";
    public static String buttonPath = "images/Buttons/";
    public static String sfxPath = "SFX/";
    private static Resources instance;
    private static HashMap<String, BufferedImage> images = new HashMap<>();

    private Resources() {
        try {
            InputStream inputStream = Resources.class.getClassLoader().getResourceAsStream("fonts/Orbitron Bold.ttf");
            Font orbitronFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
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

    public static BufferedImage getImage(String path, String name) {
        if (images.containsKey(name)) {
            return images.get(name);
        }
        System.out.println("LOADING IMAGE FROM DISK: " + path + name);
        try {
            InputStream inputStream = Resources.class.getClassLoader().getResourceAsStream(path + name);
            BufferedImage image = Utility.convertToCompatibleImage(ImageIO.read(inputStream));
            images.put(name, image);
            return image;
        } catch (IOException e) {
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
        try {
            InputStream inputStream = Resources.class.getClassLoader().getResourceAsStream(imagePath + name);
            BufferedImage image = Utility.convertToCompatibleImage(ImageIO.read(inputStream));
            images.put(name, image);
            return image;
        } catch (IOException e) {
            return null;
        }
    }

    public static String[] getImageList() {
        return new String[]{
                "Stars.png"
        };
    }

    public static String[] getMusicList() {
        return new String[]{
                "AM-GameTrack.mp3",
                "AM-MenuTrack.mp3",
                "AM-trck1.mp3"
        };
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
