package absFrame;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Monster extends Rectangle {
	
	double health, damage, visionRange;	
	double cooldown; 
	
	double dx,dy;
	double xx,yy;
	ArrayList<Projectile> projectiles; 
	int startTime; 
	//image
	
	// initialize all attribute here 
	public Monster(HashMap<String,Integer> stats,int startTime,int x, int y,int width,int height,double speed){
		super(x,y,width,height);
		
		this.health = stats.get("health");
		this.damage = stats.get("damage");
		this.visionRange = stats.get("visionRange");
		
		this.dx = stats.get("speedX")*speed;
		this.dy = stats.get("speedY")*speed;

		this.xx = x;
		this.yy=y;
		
		this.x = x;
		this.y = y;

		this.startTime = startTime;
	}
	
	public double getHealth() {
		return health;
	}

	public void reduceHealth(double damage){
		health -= damage; 
	}
	
	public abstract void Attack() ;
	//when create a bullet just get the monster speedX and Y and times it by cons for the speed
	
	
	/**
	 * Turn and move the monster toward the character 
	 * @param charX character X position
	 * @param charY character Y position
	 */
	public void move(int charX, int charY) {
		
		
		double deltX =  charX-x; 
		double deltY =  charY-y; 

		double xCorrect =1;
		double yCorrect = 1; 
		
		double speedX = 0;
		double speedY = 0;
		
		// check x
		if(deltX>0) { speedX =dx ;}
		else if (deltX<0) {speedX = -dx;}
		else {yCorrect = 2; }
		
		// check y
		if(deltY>0) {speedY = dy;}
		else if (deltY<0) {speedY = -dy;}

		else {xCorrect = 2; }
		
		System.out.println("charX: "+charX);
		System.out.println("charY: "+charY);
		System.out.println(speedX);
		System.out.println(speedY);
		System.out.println(xCorrect);
		System.out.println(yCorrect);

		
		// velocity correction 
		xx += (speedX*xCorrect);
		yy += (speedY*yCorrect);
		
		x = (int) xx;
		y = (int) yy;
				
	} 
	/**
	 * update bullet position and call the monster to attack if ready
	 * @param time the time from the game clock
	 */
	public void checkCD(int time ) {
		// if (time-startTime) % cooldown == 0 => this.attack()
		// for each projectile => p.move() 
	}
	
	/**
	 * Check if monster projectile hit block or character and do action accordingly
	 * @param charX - character X position
	 * @param charY - character Y position 
	 * @param Rtiles - collection of room tiles
	 * @return
	 */
	public void checkCollision (int charX, int charY, ArrayList<Tiles> Rtiles, Character c) {
		double monDamage = 0;
		if(charX == x && charY == y) {
			int collisionDamage = 5;
			monDamage += (double) collisionDamage;
		}

		c.health-= (int) monDamage;
	}
	
	// draw method 
	
	
	
}
