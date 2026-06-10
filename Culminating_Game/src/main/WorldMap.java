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
import java.util.Random;

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
    private boolean[][] walkableMask;
    private ArrayList<Monster> monsters = new ArrayList<Monster>();

    // Collision colors are read from mainmap.png.
    // Peach/yellow pixels are walkable. Brown/orange wall pixels and large black
    // empty areas are blocked. Small black grid lines beside floor tiles are
    // treated as walkable so the player does not get stuck on tile outlines.
    private static final int FLOOR_R = 229;
    private static final int FLOOR_G = 170;
    private static final int FLOOR_B = 122;
    private static final int DOOR_R = 255;
    private static final int DOOR_G = 194;
    private static final int DOOR_B = 14;
    private static final int COLOR_TOLERANCE = 18;
    private static final int GRID_LINE_RADIUS = 2;

    // Optional room/area labels. They do not switch the map; they only describe
    // where the player currently is and help place monsters.
    private HashMap<String, Rectangle> areas = new HashMap<String, Rectangle>();

    // These are the actual rooms where enemies can spawn. Hallways are left out
    // so enemies do not appear in narrow transition spaces.
    private String[] combatRooms = {"room1", "room2", "room3", "room4"};

    public WorldMap() {
        mapImage = loadImage("mainmap.png");
        buildWalkableMask();
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

    /**
     * Builds a simple collision mask from mainmap.png.
     *
     * The artist only needs to keep the map colors consistent:
     * - peach floor tiles are walkable
     * - yellow door/portal pixels are walkable
     * - brown/orange wall pixels are blocked
     * - large black empty areas are blocked
     *
     * The code also allows tiny black grid lines if they are very close to a
     * floor tile. That lets the player walk across tile outlines without making
     * the big black void outside the map walkable.
     */
    private void buildWalkableMask() {
        if (mapImage == null) {
            walkableMask = null;
            return;
        }

        int mapW = mapImage.getWidth();
        int mapH = mapImage.getHeight();
        boolean[][] baseWalkable = new boolean[mapW][mapH];
        walkableMask = new boolean[mapW][mapH];

        for (int x = 0; x < mapW; x++) {
            for (int y = 0; y < mapH; y++) {
                int rgb = mapImage.getRGB(x, y);
                if (isFloorColor(rgb) || isDoorColor(rgb)) {
                    baseWalkable[x][y] = true;
                    walkableMask[x][y] = true;
                }
            }
        }

        for (int x = 0; x < mapW; x++) {
            for (int y = 0; y < mapH; y++) {
                if (walkableMask[x][y]) {
                    continue;
                }

                int rgb = mapImage.getRGB(x, y);
                if (isBlackColor(rgb) && hasFloorNearby(baseWalkable, x, y, GRID_LINE_RADIUS)) {
                    walkableMask[x][y] = true;
                }
            }
        }
    }

    /**
     * Checks whether the character's lower body can stand at its current world
     * position. This is used after movement to prevent crossing wall pixels.
     */
    public boolean canCharacterStand(Character character) {
        if (character == null) {
            return false;
        }

        Rectangle box = getCharacterCollisionBox(character);
        return isCollisionBoxWalkable(box);
    }

    /**
     * The visual sprite is tall, so using the whole rectangle would make the
     * player's head collide with walls. A smaller lower-body hitbox feels more
     * natural for a top-down game.
     */
    private Rectangle getCharacterCollisionBox(Character character) {
        int boxW = Math.max(10, character.width / 2);
        int boxH = Math.max(10, character.height / 4);
        int boxX = character.x + (character.width - boxW) / 2;
        int boxY = character.y + (int) Math.round(character.height * 0.68);

        if (boxY + boxH > character.y + character.height) {
            boxH = character.y + character.height - boxY;
        }

        return new Rectangle(boxX, boxY, boxW, boxH);
    }

    private boolean isCollisionBoxWalkable(Rectangle box) {
        if (box == null || box.width <= 0 || box.height <= 0) {
            return false;
        }

        int left = box.x;
        int right = box.x + box.width - 1;
        int top = box.y;
        int bottom = box.y + box.height - 1;
        int centerX = box.x + box.width / 2;
        int centerY = box.y + box.height / 2;

        return isWorldPointWalkable(left, top)
                && isWorldPointWalkable(centerX, top)
                && isWorldPointWalkable(right, top)
                && isWorldPointWalkable(left, centerY)
                && isWorldPointWalkable(centerX, centerY)
                && isWorldPointWalkable(right, centerY)
                && isWorldPointWalkable(left, bottom)
                && isWorldPointWalkable(centerX, bottom)
                && isWorldPointWalkable(right, bottom);
    }

    private boolean isWorldPointWalkable(int worldX, int worldY) {
        if (walkableMask == null || mapImage == null) {
            return true;
        }

        int mapX = worldX / MAP_SCALE;
        int mapY = worldY / MAP_SCALE;

        if (mapX < 0 || mapY < 0 || mapX >= mapImage.getWidth() || mapY >= mapImage.getHeight()) {
            return false;
        }

        return walkableMask[mapX][mapY];
    }

    private boolean hasFloorNearby(boolean[][] baseWalkable, int x, int y, int radius) {
        int mapW = baseWalkable.length;
        int mapH = baseWalkable[0].length;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int checkX = x + dx;
                int checkY = y + dy;

                if (checkX >= 0 && checkY >= 0 && checkX < mapW && checkY < mapH && baseWalkable[checkX][checkY]) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isFloorColor(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;
        return colorClose(r, g, b, FLOOR_R, FLOOR_G, FLOOR_B);
    }

    private boolean isDoorColor(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;
        return colorClose(r, g, b, DOOR_R, DOOR_G, DOOR_B);
    }

    private boolean isBlackColor(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;
        return r < 35 && g < 35 && b < 35;
    }

    private boolean colorClose(int r, int g, int b, int targetR, int targetG, int targetB) {
        return Math.abs(r - targetR) <= COLOR_TOLERANCE
                && Math.abs(g - targetG) <= COLOR_TOLERANCE
                && Math.abs(b - targetB) <= COLOR_TOLERANCE;
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
     * Returns only the room names where monsters should randomly spawn.
     */
    public String[] getCombatRoomNames() {
        return combatRooms.clone();
    }

    /**
     * Picks a random top-left world position inside a named area.
     * The object width/height are used so the monster stays fully inside the room.
     */
    public Point getRandomSpawnPointInArea(String areaName, Random random, int objectWidth, int objectHeight) {
        Rectangle area = getWorldArea(areaName);
        if (area == null) {
            return new Point(getWorldWidth() / 2, getWorldHeight() / 2);
        }

        int padding = 8 * MAP_SCALE;
        int minX = area.x + padding;
        int minY = area.y + padding;
        int maxX = area.x + area.width - objectWidth - padding;
        int maxY = area.y + area.height - objectHeight - padding;

        if (maxX < minX) {
            minX = area.x + Math.max(0, (area.width - objectWidth) / 2);
            maxX = minX;
        }
        if (maxY < minY) {
            minY = area.y + Math.max(0, (area.height - objectHeight) / 2);
            maxY = minY;
        }

        int x = minX + random.nextInt(maxX - minX + 1);
        int y = minY + random.nextInt(maxY - minY + 1);
        return new Point(x, y);
    }

    /**
     * Converts a map-area rectangle into world coordinates.
     */
    public Rectangle getWorldArea(String areaName) {
        Rectangle area = areas.get(areaName);
        if (area == null) {
            return null;
        }
        return new Rectangle(
                area.x * MAP_SCALE,
                area.y * MAP_SCALE,
                area.width * MAP_SCALE,
                area.height * MAP_SCALE);
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
