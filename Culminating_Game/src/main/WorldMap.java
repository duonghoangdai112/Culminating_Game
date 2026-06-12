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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Random;

/**
 * Draws and manages one full arena map.
 *
 * The new mainmap.png is a single large room, so the game no longer needs room
 * switching. Player, monsters, weapons, and projectiles use world coordinates.
 * Camera.java decides which part of this world is visible on screen.
 */
public class WorldMap {
    // The new arena image is 1254 x 1254, so scale 2 keeps the world large
    // without making monsters take too long to reach the player.
    private static final int MAP_SCALE = 2;

    private BufferedImage mapImage;
    private ArrayList<Monster> monsters = new ArrayList<Monster>();

    // Areas are written in original image pixel coordinates, not scaled world
    // coordinates. They are converted using MAP_SCALE when needed.
    private LinkedHashMap<String, Rectangle> areas = new LinkedHashMap<String, Rectangle>();

    // Main walkable floor area inside the orange outer walls.
    private Rectangle arenaBounds;

    // Simple obstacle rectangles for raised pillars/blocks on the arena floor.
    // These stop the player from walking through the obvious raised objects.
    private ArrayList<Rectangle> blockedMapAreas = new ArrayList<Rectangle>();

    // Spawn zones around the arena. These are used by GamePanel's random monster
    // spawning code.
    private String[] combatRooms = {"spawnNW", "spawnNE", "spawnSW", "spawnSE"};

    public WorldMap() {
        mapImage = loadImage("mainmap.png");
        createAreas();
    }

    private void createAreas() {
        // This rectangle is the playable floor inside the orange walls. It is
        // intentionally a little smaller than the visible floor so the player
        // cannot clip into wall shadows or wall blocks.
        arenaBounds = new Rectangle(110, 130, 1035, 1000);
        areas.put("arena", arenaBounds);

        // Corner/edge spawn zones. They match the four decorative circular pads
        // in the new arena map.
        areas.put("spawnNW", new Rectangle(125, 150, 260, 260));
        areas.put("spawnNE", new Rectangle(870, 150, 260, 260));
        areas.put("spawnSW", new Rectangle(125, 850, 260, 260));
        areas.put("spawnSE", new Rectangle(870, 850, 260, 260));

        // Raised pillars/blocks. These are optional gameplay obstacles. The
        // center floor design is not blocked because it looks flat/walkable.
        addBlockedArea(370, 135, 80, 105);   // upper-left inner pillar
        addBlockedArea(805, 135, 80, 105);   // upper-right inner pillar
        addBlockedArea(105, 365, 90, 110);   // left middle pillar
        addBlockedArea(1060, 365, 90, 110);  // right middle pillar
        addBlockedArea(105, 750, 90, 110);   // left lower pillar
        addBlockedArea(1060, 750, 90, 110);  // right lower pillar
        addBlockedArea(370, 1040, 80, 95);   // lower-left inner pillar
        addBlockedArea(805, 1040, 80, 95);   // lower-right inner pillar
    }

    private void addBlockedArea(int x, int y, int width, int height) {
        blockedMapAreas.add(new Rectangle(x, y, width, height));
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
     * Checks whether the character's lower body can stand at its current world
     * position. This prevents the player from crossing the orange arena walls.
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

    private boolean isWorldRectangleWalkable(Rectangle box) {
        return isCollisionBoxWalkable(box);
    }

    private boolean isWorldPointWalkable(int worldX, int worldY) {
        int mapX = worldX / MAP_SCALE;
        int mapY = worldY / MAP_SCALE;

        if (mapImage == null) {
            return true;
        }

        if (mapX < 0 || mapY < 0 || mapX >= mapImage.getWidth() || mapY >= mapImage.getHeight()) {
            return false;
        }

        if (arenaBounds == null || !arenaBounds.contains(mapX, mapY)) {
            return false;
        }

        for (Rectangle blocked : blockedMapAreas) {
            if (blocked.contains(mapX, mapY)) {
                return false;
            }
        }

        return true;
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
     * Returns the arena zones where monsters should randomly spawn.
     */
    public String[] getCombatRoomNames() {
        return combatRooms.clone();
    }

    /**
     * Picks a random top-left world position inside a named spawn area.
     * The object width/height are used so the monster stays fully inside the map.
     */
    public Point getRandomSpawnPointInArea(String areaName, Random random, int objectWidth, int objectHeight) {
        Rectangle area = getWorldArea(areaName);
        if (area == null) {
            return new Point(getWorldWidth() / 2, getWorldHeight() / 2);
        }

        int padding = 12 * MAP_SCALE;
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

        for (int attempt = 0; attempt < 80; attempt++) {
            int x = minX + random.nextInt(maxX - minX + 1);
            int y = minY + random.nextInt(maxY - minY + 1);
            Rectangle spawnBox = new Rectangle(x, y, objectWidth, objectHeight);

            if (isWorldRectangleWalkable(spawnBox)) {
                return new Point(x, y);
            }
        }

        // Fallback: use the center of the zone if random attempts fail.
        int x = area.x + Math.max(0, (area.width - objectWidth) / 2);
        int y = area.y + Math.max(0, (area.height - objectHeight) / 2);
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

    public int removeDefeatedMonsters() {
        ArrayList<Monster> copy = new ArrayList<>(monsters);
        int monsKilled = 0;
        for (Monster monster : copy) {
            if (monster.getHealth() <= 0) {
                monsters.remove(monster);
                monsKilled +=1;
            }
        }
        return monsKilled; 
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

        // The new map is one big arena, so keep the label simple.
        if (arenaBounds != null && arenaBounds.contains(mapX, mapY)) {
            return "Arena";
        }

        return "Wall";
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
