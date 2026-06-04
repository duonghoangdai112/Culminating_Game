package absFrame;

import java.awt.Rectangle;
import java.util.ArrayList;

public class Room {

    public Room next;
    public Room previous;

    // Section of the large map image
    private int mapX;
    private int mapY;
    private int mapWidth;
    private int mapHeight;

    // Door hitboxes
    public Rectangle nextDoor;
    public Rectangle previousDoor;

    ArrayList<Tiles> tiles = new ArrayList<>();
    ArrayList<Monster> mons = new ArrayList<>();

    public Room(int mapX, int mapY,
                int mapWidth, int mapHeight) {

        this.mapX = mapX;
        this.mapY = mapY;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }

    public int getMapX() {
        return mapX;
    }

    public int getMapY() {
        return mapY;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }


 /**
     * check all monster is clear if yes change room clear to true
     * to do later 
     */
    public void checkRoom(){
        boolean roomClear;
		if (mons.isEmpty() && roomClear == false) {
            roomClear = true;
            for (Tiles t: tiles){
                if (t.getName().equals("door")){
                    t.setCrossable(true);
                }
            }
        }
    }

    /**
     * to do later 
     * @param charX
     * @param charY
     * @param charProj
     * @return
     */

    public double checkResult(int charX, int charY, ArrayList<Projectile> charProj){
        int monDamage = 0;

        for (Monster m : mons ){
//            monDamage += m.checkProjectile(charX, charY, tiles);

            for (Projectile p: charProj){
//                if(m.getX() <= p.getX() + p.getHitBox() 
//                    && m.getX() >= p.getX()- p.getX()){
//                        if(m.getY() <= p.getY() + p.getHitBox() 
//                        && m.getY() >= p.getY()- p.getHitBox()){
//                            m.reduceHealth(p.getDamage());
//                            p.setVisibility(false);
//                            if(m.getHealth() ==0){mons.remove(m);}
//                        }
//                }
            }

        }
        return monDamage;
    }


}



