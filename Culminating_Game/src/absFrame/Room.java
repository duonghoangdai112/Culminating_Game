package absFrame;

import java.awt.Rectangle;
import java.util.ArrayList;

public class Room {

    public Room next;
    public Room previous;

    public Rectangle nextDoor;
    public Rectangle previousDoor;

    public ArrayList<Monster> mons = new ArrayList<>();

    private int mapX;
    private int mapY;
    private int mapWidth;
    private int mapHeight;

    public Room(int mapX, int mapY,int mapWidth, int mapHeight) {
        this.mapX = mapX;
        this.mapY = mapY;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }

    public boolean isClear() {
        return mons.isEmpty();
    }

    // getters...
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



