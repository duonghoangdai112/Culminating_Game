package sprite;
import java.util.ArrayList;

import absFrame.*;
import absFrame.Character;

public class Archer extends Character {

	public Archer(int health, int shield, int mana, int speed, int visionRange, int cooldown,String name) {
		super(health,shield,mana,speed, visionRange,cooldown,name);
//		weapon = new Weapon(1,1,1,0.1,1,1,"Sniper",300,300,-1); //100 are for width and height only change stats before that 
//		weapon.setImage(maxImg,0.3,this.x,this.y); //setUp image
		this.ScalePX = 50;
	}

	@Override
	public void Ability() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	

	
	
	

}
