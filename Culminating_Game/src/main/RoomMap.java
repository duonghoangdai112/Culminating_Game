package main;

import absFrame.*;
import absFrame.Character;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Handles the room system for the game.
 *
 * This class keeps the room/map logic out of GamePanel:
 * - loads mainmap.png
 * - crops the current room from the big map image
 * - stores linked rooms and doors
 * - draws the current room
 * - changes rooms when the player touches a door
 */
public class RoomMap {
    private BufferedImage mapImage;
    private HashMap<String, Room> rooms = new HashMap<String, Room>();
    private Room currentRoom;

    // Keep this false for now so testing movement between rooms is easy.
    // Change to true later if you want doors locked until all monsters are defeated.
    private boolean lockDoorsUntilRoomClear = false;

    public RoomMap() {
        mapImage = loadImage("mainmap.png");
        createRooms();
        currentRoom = getRoom("room1");
    }

    private void createRooms() {
        Room room1 = addRoom("room1", 31, 252, 160, 90);
        Room hallway1 = addRoom("hallway1", 191, 162, 110, 161);
        Room room2 = addRoom("room2", 161, 12, 90, 160);
        Room hallway2 = addRoom("hallway2", 241, 32, 100, 50);
        Room room3 = addRoom("room3", 331, 12, 160, 90);
        Room room4 = addRoom("room4", 121, 362, 250, 130);

        link(room1, hallway1);
        link(hallway1, room2);
        link(room2, hallway2);
        link(hallway2, room3);
        link(room3, room4);

        // Door rectangles are local to each room, not global to the big map.
        room1.nextDoor = new Rectangle(150, 20, 10, 30);

        hallway1.previousDoor = new Rectangle(0, 110, 10, 30);
        hallway1.nextDoor = new Rectangle(0, 9, 30, 10);

        room2.previousDoor = new Rectangle(30, 150, 30, 10);
        room2.nextDoor = new Rectangle(80, 30, 10, 30);

        hallway2.previousDoor = new Rectangle(0, 10, 10, 30);
        hallway2.nextDoor = new Rectangle(90, 10, 10, 30);

        room3.previousDoor = new Rectangle(0, 30, 10, 30);

        // Room 4 is included now, but its exact door can be adjusted once the final map is done.
        room3.nextDoor = new Rectangle(75, 80, 30, 10);
        room4.previousDoor = new Rectangle(125, 0, 40, 10);
    }

    private Room addRoom(String name, int x, int y, int width, int height) {
        Room room = new Room(name, x, y, width, height);
        rooms.put(name, room);
        return room;
    }

    private void link(Room previous, Room next) {
        previous.next = next;
        next.previous = previous;
    }

    public Room getRoom(String name) {
        return rooms.get(name);
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public String getCurrentRoomName() {
        return currentRoom == null ? "" : currentRoom.getName();
    }

    public ArrayList<Monster> getCurrentMonsters() {
        if (currentRoom == null) {
            return new ArrayList<Monster>();
        }
        return currentRoom.getMonsters();
    }

    public void clearAllMonsters() {
        for (Room room : rooms.values()) {
            room.clearMonsters();
        }
    }

    public void addMonsterToRoom(String roomName, Monster monster) {
        Room room = getRoom(roomName);
        if (room != null) {
            room.addMonster(monster);
        }
    }

    public BufferedImage getCurrentRoomImage() {
        if (mapImage == null || currentRoom == null) {
            return null;
        }

        Rectangle src = currentRoom.getSourceRect();
        return mapImage.getSubimage(src.x, src.y, src.width, src.height);
    }

    public void drawCurrentRoom(Graphics2D g2, int panelW, int panelH) {
        BufferedImage roomImage = getCurrentRoomImage();
        if (roomImage == null) {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, panelW, panelH);
            return;
        }

        // The room is stretched to fill the game panel so player, monster, and door
        // hitboxes can all stay in the same 1000 x 1000 screen coordinate system.
        g2.drawImage(roomImage, 0, 0, panelW, panelH, null);
    }

    public void drawDebugDoors(Graphics2D g2, int panelW, int panelH) {
        if (currentRoom == null) {
            return;
        }

        g2.setColor(new Color(255, 0, 0, 120));
        if (currentRoom.nextDoor != null) {
            Rectangle next = toScreenRect(currentRoom.nextDoor, panelW, panelH);
            g2.fillRect(next.x, next.y, next.width, next.height);
        }

        g2.setColor(new Color(0, 120, 255, 120));
        if (currentRoom.previousDoor != null) {
            Rectangle previous = toScreenRect(currentRoom.previousDoor, panelW, panelH);
            g2.fillRect(previous.x, previous.y, previous.width, previous.height);
        }
    }

    public boolean tryChangeRoom(Character player, int panelW, int panelH) {
        if (currentRoom == null || player == null) {
            return false;
        }

        if (!canLeaveCurrentRoom()) {
            return false;
        }

        if (currentRoom.next != null && currentRoom.nextDoor != null) {
            Rectangle nextDoor = toScreenRect(currentRoom.nextDoor, panelW, panelH);
            if (player.intersects(nextDoor)) {
                changeRoom(currentRoom.next, true, player, panelW, panelH);
                return true;
            }
        }

        if (currentRoom.previous != null && currentRoom.previousDoor != null) {
            Rectangle previousDoor = toScreenRect(currentRoom.previousDoor, panelW, panelH);
            if (player.intersects(previousDoor)) {
                changeRoom(currentRoom.previous, false, player, panelW, panelH);
                return true;
            }
        }

        return false;
    }

    private boolean canLeaveCurrentRoom() {
        return !lockDoorsUntilRoomClear || currentRoom.isClear();
    }

    private void changeRoom(Room targetRoom, boolean movedForward, Character player, int panelW, int panelH) {
        currentRoom = targetRoom;

        Rectangle spawnDoor = movedForward ? currentRoom.previousDoor : currentRoom.nextDoor;
        placePlayerNearDoor(player, spawnDoor, panelW, panelH, movedForward);
        keepPlayerInsideRoom(player, panelW, panelH);
    }

    private void placePlayerNearDoor(Character player, Rectangle door, int panelW, int panelH, boolean movedForward) {
        if (door == null || currentRoom == null) {
            player.x = movedForward ? 50 : panelW - player.width - 50;
            player.y = panelH / 2 - player.height / 2;
            return;
        }

        Rectangle screenDoor = toScreenRect(door, panelW, panelH);
        double doorCenterX = door.getCenterX();
        double doorCenterY = door.getCenterY();
        int roomW = currentRoom.getMapWidth();
        int roomH = currentRoom.getMapHeight();
        int padding = 25;

        player.x = screenDoor.x + screenDoor.width / 2 - player.width / 2;
        player.y = screenDoor.y + screenDoor.height / 2 - player.height / 2;

        if (doorCenterX < roomW * 0.25) {
            player.x = screenDoor.x + screenDoor.width + padding;
        } else if (doorCenterX > roomW * 0.75) {
            player.x = screenDoor.x - player.width - padding;
        } else if (doorCenterY < roomH * 0.25) {
            player.y = screenDoor.y + screenDoor.height + padding;
        } else if (doorCenterY > roomH * 0.75) {
            player.y = screenDoor.y - player.height - padding;
        }
    }

    public void keepPlayerInsideRoom(Character player, int panelW, int panelH) {
        if (player.x < 0) {
            player.x = 0;
        }
        if (player.y < 0) {
            player.y = 0;
        }
        if (player.x > panelW - player.width) {
            player.x = panelW - player.width;
        }
        if (player.y > panelH - player.height) {
            player.y = panelH - player.height;
        }
    }

    public Rectangle toScreenRect(Rectangle roomRect, int panelW, int panelH) {
        if (currentRoom == null || roomRect == null) {
            return new Rectangle();
        }

        double scaleX = (double) panelW / currentRoom.getMapWidth();
        double scaleY = (double) panelH / currentRoom.getMapHeight();

        return new Rectangle(
                (int) Math.round(roomRect.x * scaleX),
                (int) Math.round(roomRect.y * scaleY),
                Math.max(1, (int) Math.round(roomRect.width * scaleX)),
                Math.max(1, (int) Math.round(roomRect.height * scaleY))
        );
    }

    public void setLockDoorsUntilRoomClear(boolean lockDoorsUntilRoomClear) {
        this.lockDoorsUntilRoomClear = lockDoorsUntilRoomClear;
    }

    public Color getCurrentRoomPixelColor(int roomX, int roomY) {
        BufferedImage roomImage = getCurrentRoomImage();
        if (roomImage == null || roomX < 0 || roomY < 0 ||
                roomX >= roomImage.getWidth() || roomY >= roomImage.getHeight()) {
            return Color.BLACK;
        }

        return new Color(roomImage.getRGB(roomX, roomY));
    }

    public boolean isWall(Color color) {
        return color.getRed() > 100 &&
                color.getRed() < 170 &&
                color.getGreen() > 40 &&
                color.getGreen() < 100 &&
                color.getBlue() < 60;
    }

    private BufferedImage loadImage(String filename) {
        String[] resourceNames = {"/" + filename, "/assests/" + filename};
        for (String resourceName : resourceNames) {
            URL url = getClass().getResource(resourceName);
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
