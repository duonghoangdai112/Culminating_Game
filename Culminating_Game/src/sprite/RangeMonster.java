package sprite;
import java.util.HashMap;

import absFrame.Monster;

public class RangeMonster extends Monster{
    public RangeMonster(HashMap<String, Integer> hashMap,int startTime,int width, int height, int x , int y,double d){
        super(hashMap, startTime, x, y, width, height,d);
    }

    @Override
    public void Attack() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'Attack'");
    }
}