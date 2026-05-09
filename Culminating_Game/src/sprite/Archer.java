package sprite;
import absFrame.*;
import absFrame.Character;

public class Archer extends Character {

	public Archer(int health, int shield, int mana, int speed, int visionRange, int cooldown,String name) {
		super(health,shield,mana,speed, visionRange,cooldown,name);
		weapon = new Weapon(1,1,1,0.1,1,1,"Sniper",300,300); //100 are for width and height only change stats before that 
		this.maxImg = 3;
		this.Wdir = -1;
		weapon.setImage(maxImg,0.3,this.x,this.y,Wdir);
	}

	@Override
	public void Ability() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	

	@Override
	public void Attack() {
		if(weapon.attack ==false) {
			weapon.attack = true;
			int sizeP = 30; 
			
			int wid = (int)(weapon.width *weapon.ratio);// size of weapon after resize
			System.out.println(weapon.dir);
			projectile.add(new Projectile(weapon.dx1-wid,weapon.dy1,sizeP,sizeP, weapon.dir, 0, 10));
		}
	}
	

}
