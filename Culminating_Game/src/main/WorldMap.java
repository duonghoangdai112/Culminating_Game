package main;

import absFrame.Character;
import absFrame.Monster;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Draws and manages one full connected map.
 *
 * There is no room teleporting or cropped-room switching anymore.
 * Player, monsters, weapons, and projectiles use world coordinates.
 * Camera.java decides which part of this world is visible on screen.
 */
public class WorldMap {
    private static final int MAP_SCALE = 4;

    private BufferedImage mapImage;
    private ArrayList<Monster> monsters = new ArrayList<Monster>();

    // Optional room/area labels. They do not switch the map; they only describe
    // where the player currently is and help place monsters.
    private HashMap<String, Rectangle> areas = new HashMap<String, Rectangle>();

    public WorldMap() {
        mapImage = loadImage("mainmap.png");
        createAreas();
    }

    private void createAreas() {
        areas.put("room1", new Rectangle(31, 252, 160, 90));
        areas.put("hallway1", new Rectangle(191, 162, 110, 161));
        areas.put("room2", new Rectangle(161, 12, 90, 160));
        areas.put("hallway2", new Rectangle(241, 32, 100, 50));
        areas.put("room3", new Rectangle(331, 12, 160, 90));
        areas.put("room4", new Rectangle(121, 362, 250, 130));
    }

    public void draw(Graphics2D g2) {
        if (mapImage == null) {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, getWorldWidth(), getWorldHeight());
            return;
        }

        g2.drawImage(mapImage, 0, 0, getWorldWidth(), getWorldHeight(), null);
    }

    public int getWorldWidth() {
        if (mapImage == null) {
            return 1000;
        }
        return mapImage.getWidth() * MAP_SCALE;
    }

    public int getWorldHeight() {
        if (mapImage == null) {
            return 1000;
        }
        return mapImage.getHeight() * MAP_SCALE;
    }

    public int getMapScale() {
        return MAP_SCALE;
    }

    public ArrayList<Monster> getMonsters() {
        return monsters;
    }

    public void clearMonsters() {
        monsters.clear();
    }

    public void addMonster(Monster monster) {
        if (monster != null) {
            monsters.add(monster);
        }
    }

    /**
     * Places a monster inside a named area using percentages from 0.0 to 1.0.
     * Example: 0.5, 0.5 means center of that area.
     */
    public void addMonsterToArea(String areaName, Monster monster, double percentX, double percentY) {
        if (monster == null) {
            return;
        }

        Point center = getAreaPoint(areaName, percentX, percentY);
        monster.setWorldPosition(center.x - monster.width / 2, center.y - monster.height / 2);
        monsters.add(monster);
    }

    /**
     * Places the player inside a named area using percentages from 0.0 to 1.0.
     */
    public void placeCharacterInArea(Character character, String areaName, double percentX, double percentY) {
        if (character == null) {
            return;
        }

        Point center = getAreaPoint(areaName, percentX, percentY);
        character.x = center.x - character.width / 2;
        character.y = center.y - character.height / 2;
        keepInsideWorld(character);
    }

    private Point getAreaPoint(String areaName, double percentX, double percentY) {
        Rectangle area = areas.get(areaName);
        if (area == null) {
            return new Point(getWorldWidth() / 2, getWorldHeight() / 2);
        }

        percentX = clampDouble(percentX, 0.0, 1.0);
        percentY = clampDouble(percentY, 0.0, 1.0);

        int mapX = area.x + (int) Math.round(area.width * percentX);
        int mapY = area.y + (int) Math.round(area.height * percentY);
        return new Point(mapX * MAP_SCALE, mapY * MAP_SCALE);
    }

    public void removeDefeatedMonsters() {
        Iterator<Monster> it = monsters.iterator();
        while (it.hasNext()) {
            Monster monster = it.next();
            if (monster.getHealth() <= 0) {
                it.remove();
            }
        }
    }

    public void keepInsideWorld(Character character) {
        if (character == null) {
            return;
        }

        character.x = clamp(character.x, 0, Math.max(0, getWorldWidth() - character.width));
        character.y = clamp(character.y, 0, Math.max(0, getWorldHeight() - character.height));
    }

    public String getAreaNameAt(Rectangle target) {
        if (target == null) {
            return "World";
        }

        int mapX = (target.x + target.width / 2) / MAP_SCALE;
        int mapY = (target.y + target.height / 2) / MAP_SCALE;

        for (String name : areas.keySet()) {
            Rectangle area = areas.get(name);
            if (area.contains(mapX, mapY)) {
                return name;
            }
        }

        return "World";
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    private double clampDouble(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }

    private BufferedImage loadImage(String filename) {
        String[] resourceNames = {"/" + filename, "/assests/" + filename};
        for (String resourceName : resourceNames) {
            URL url = this.getClass().getResource(resourceName);
            if (url != null) {
                try {
                    return ImageIO.read(url);
                } catch (IOException e) {
                    System.out.println("Could not load image resource: " + resourceName);
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
                    System.out.println("Could not load image file: " + fileName);
                }
            }
        }

        System.out.println("Image not found: " + filename);
        JOptionPane.showMessageDialog(null, "An image failed to load: " + filename,
                "Error", JOptionPane.ERROR_MESSAGE);
        return null;
    }
}
