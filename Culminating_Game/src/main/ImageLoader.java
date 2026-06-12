package main;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ImageLoader {

    public static BufferedImage loadImage(Class<?> owner, String filename) {
        String[] resourceNames = {"/" + filename, "/assests/" + filename};

        for (String resourceName : resourceNames) {
            URL url = owner.getResource(resourceName);
            if (url != null) {
                try {
                    return ImageIO.read(url);
                } catch (IOException e) {
                    System.out.println(e.toString());
                }
            }
        }

        String[] fileNames = {filename, "assests/" + filename};
        for (String fileName : fileNames) {
            File file = new File(fileName);
            if (file.exists()) {
                try {
                    return ImageIO.read(file);
                } catch (IOException e) {
                    System.out.println(e.toString());
                }
            }
        }

        System.out.println("URL is null for: " + filename);
        JOptionPane.showMessageDialog(null, "An image failed to load: " + filename,
                "Error", JOptionPane.ERROR_MESSAGE);
        return null;
    }

    public static BufferedImage loadOptionalImage(Class<?> owner, String filename) {
        String[] resourceNames = {"/" + filename, "/assests/" + filename};

        for (String resourceName : resourceNames) {
            URL url = owner.getResource(resourceName);
            if (url != null) {
                try {
                    return ImageIO.read(url);
                } catch (IOException e) {
                    System.out.println(e.toString());
                    return null;
                }
            }
        }

        String[] fileNames = {filename, "assests/" + filename};
        for (String fileName : fileNames) {
            File file = new File(fileName);
            if (file.exists()) {
                try {
                    return ImageIO.read(file);
                } catch (IOException e) {
                    System.out.println(e.toString());
                    return null;
                }
            }
        }

        System.out.println("Optional image not found: " + filename);
        return null;
    }
}
