import java.util.ArrayList;

public abstract class Room {
    private int width; 
    private int length; 
    ArrayList<Tiles> tiles; 
    ArrayList<Monster> mons;
    Boolean roomClear;

    public Room(int width, int length, ArrayList<Tiles> tiles, ArrayList<Monster> mons){
        // think of a way to add in tiles and monster to a room 
        this.width = width;
        this.length = length;
        this.tiles = tiles;
        this.mons = mons;

    }
    /**
     * check all monster is clear if yes change room clear to true
     */
    public void checkRoom(){
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
     * 
     * @param charX
     * @param charY
     * @param charProj
     * @return
     */

    public double checkResult(int charX, int charY, ArrayList<Projectile> charProj){
        int monDamage = 0;

        for (Monster m : mons ){
            monDamage += m.checkProjectile(charX, charY, tiles);

            for (Projectile p: charProj){
                if(m.getX() <= p.getX() + p.getHitBox() 
                    && m.getX() >= p.getX()- p.getX()){
                        if(m.getY() <= p.getY() + p.getHitBox() 
                        && m.getY() >= p.getY()- p.getHitBox()){
                            m.reduceHealth(p.getDamage());
                            p.setVisibility(false);
                            if(m.getHealth() ==0){mons.remove(m);}
                        }
                }
            }

        }
        return monDamage;
    }


}
