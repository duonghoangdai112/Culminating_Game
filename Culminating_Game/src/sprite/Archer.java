package sprite;
import absFrame.*;
import absFrame.Character;

public class Archer extends Character {

	public Archer(int health, int shield, int mana, int speed, int visionRange, int cooldown,String name) {
		super(health,shield,mana,speed, visionRange,cooldown,name);
		weapon = new Weapon(1,1,1,1,1,1,"Bow",100,100); //100 are for width and height only change stats before that 
	}

	@Override
	public void Ability() {
		// TODO Auto-generated method stub
		
	}

}
