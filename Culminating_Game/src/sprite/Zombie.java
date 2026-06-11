package sprite;
import java.util.HashMap;

import absFrame.Monster;

public class Zombie extends Monster{
    public Zombie(HashMap<String, Integer> hashMap,int startTime,int width, int height, int x , int y,double d){
        super(hashMap, startTime, x, y, width, height,d);
    }

    @Override
    public void Attack() {
        // Zombies do contact damage through Monster.checkCollision().
    }
}