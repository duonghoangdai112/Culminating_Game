package sprite;
import java.util.HashMap;

import absFrame.Monster;

public class RangeMonster extends Monster{
    public RangeMonster(HashMap<String,Integer> stats,int startTime,int width, int height, int x , int y){
        super(stats, startTime, x, y, width, height);
    }

    @Override
    public void Attack() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'Attack'");
    }
}