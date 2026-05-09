package absFrame;
import java.awt.*;
import java.util.*;
import main.Map;

public abstract class Character extends Rectangle {
	public int health,mana,speed,visionRange,cooldown;
	
	public int screenW, screenH;
	public String name;
	public String imgName;
	public Weapon weapon;
	public int maxImg;
	public int Wdir;
	
	public int startTime;
	
	public ArrayList<Projectile> projectile = new ArrayList<Projectile>();
	
	public Character(int health, int shield, int mana, int speed, int visionRange, int cooldown,String name) {
		this.health = health;
		this.mana = mana;
		this.speed = speed;
		this.visionRange = visionRange;
		this.cooldown = cooldown;
		this.name = name;
		this.imgName = name +".png";
		this.x = 0;
		this.y = 0;
		this.width = 100;
		this.height =100;
				
	}
	
	public void RemoveProj() {
		if(projectile.isEmpty()) {

		}
		
		else {
			ArrayList<Projectile> projTemp = (ArrayList<Projectile>) projectile.clone();
			for (Projectile p: projectile) {
				if(p.getVisibility() == false) {
					projTemp.remove(p);
				}
			}
			projectile = projTemp;
		}
		
		
	}
	
	public abstract void Ability();
	
	/**
	 * Change the character's x and y coordinates
	 * @param xchange change in the character's x coordinates
	 * @param ychange change in the character's y coordinates
	 * return 
	 */
	public void Move(int dirX, int dirY) {
				
		this.x += dirX*speed;
		this.y += dirY*speed;
		
	}
	
	public void checkCollision(int screenW,int screenH,Map map){
		this.screenW = screenW;
		this.screenH = screenH;
				
		//case left
		if(x <0) {
			boolean cameraMoved = map.changeBackground(new int[] {-1, 0});

			if (cameraMoved) {x = screenW - width;} 
			else {x = 0;}
		}
		
		//case up
		if(y <0) {
			boolean cameraMoved = map.changeBackground(new int[] {0, -1});

			if (cameraMoved) {
				y = screenH - height;} 
			else {y = 0;}
		}

		//case right
		if(x> screenW-this.width) { 
			boolean cameraMoved = map.changeBackground(new int[] {1, 0}); 

		    if (cameraMoved) { x = 0; } 
		    else {x = screenW - width;}
		}
		
		// case down
		if(y> screenH-this.height) {
			boolean cameraMoved = map.changeBackground(new int[] {0, 1});

			if (cameraMoved) {y = 0;} 
			else {y = screenH - height;}
		}		
	}
	
	
	public int Find() {
		return 1;
	}
	
	public void logTime(int time) {
		this.startTime = time;
	}
	
	public boolean AbilityReady(int time){
		if(time-startTime == cooldown) {return true;}
		return false;
	}
	
	public abstract void Attack();
	
	/**
	 * Add or subtract the character's hp
	 * @param change in the character's hp
	 */
	public void changeHealth(int change) {
		this.health += change;
	}
	
	/**
	 * Add or subtract the character's mana
	 * @param change in the character's mana
	 */
	public void changeMana(int change) {
		this.mana += change;
	}
	
	
}

