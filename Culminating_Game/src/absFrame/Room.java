package absFrame;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Stores the data for one room in the big map image.
 *
 * sourceRect = the part of mainmap.png that belongs to this room.
 * nextDoor / previousDoor = door hitboxes inside this room, using room-local coordinates.
 * monsters = the monsters that belong to this room.
 */
public class Room {
    private String name;
    private Rectangle sourceRect;
    private ArrayList<Monster> monsters = new ArrayList<Monster>();

    public Room next;
    public Room previous;

    public Rectangle nextDoor;
    public Rectangle previousDoor;

    public Room(String name, int mapX, int mapY, int mapWidth, int mapHeight) {
        this.name = name;
        this.sourceRect = new Rectangle(mapX, mapY, mapWidth, mapHeight);
    }

    public String getName() {
        return name;
    }

    public Rectangle getSourceRect() {
        return sourceRect;
    }

    public int getMapX() {
        return sourceRect.x;
    }

    public int getMapY() {
        return sourceRect.y;
    }

    public int getMapWidth() {
        return sourceRect.width;
    }

    public int getMapHeight() {
        return sourceRect.height;
    }

    public ArrayList<Monster> getMonsters() {
        return monsters;
    }

    public void addMonster(Monster monster) {
        if (monster != null) {
            monsters.add(monster);
        }
    }

    public void clearMonsters() {
        monsters.clear();
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

    public boolean isClear() {
        removeDefeatedMonsters();
        return monsters.isEmpty();
    }
}
