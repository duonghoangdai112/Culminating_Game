package sprite;
import java.util.HashMap;

import absFrame.Monster;

public class RangeMonster extends Monster{
    public RangeMonster(HashMap<String,Integer> stats,int startTime){
        super(stats, startTime);
    }

    @Override
    public void Attack() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'Attack'");
    }
}