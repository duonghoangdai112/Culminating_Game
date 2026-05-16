package absFrame;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Monster extends Rectangle {
	
	double health, damage, visionRange;	
	double cooldown; 
	
	int dx,dy;
	ArrayList<Projectile> projectiles; 
	int startTime; 
	//image
	
	// initialize all attribute here 
	public Monster(HashMap<String,Integer> stats,int startTime,int x, int y,int width,int height){
		super(x,y,width,height);
		
		this.health = stats.get("health");
		this.damage = stats.get("damage");
		this.visionRange = stats.get("visionRange");
		
		this.dx = stats.get("speedX");
		this.dy = stats.get("speedY");

		this.x = stats.get("x");
		this.y = stats.get("y");

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
	public void move(int charX, int charY) { 	// helper function 
		
		
		int deltX =  charX-x; 
		int deltY =  charY-y; 
		
		if(deltX ==0 ) {
			
		}

		int xCorrect =1;
		int yCorrect = 1; 
		
		int speedX = dx;
		int speedY = dy;
		
		// check x
		if(deltX>0) { speedX =dx ;}
		else if (deltX<0) {speedX = -dx;}
		else {yCorrect = 2; }
		
		// check y
		if(deltY>0) {speedY = dy;}
		else if (deltY<0) {speedY = -dy;}

		else {xCorrect = 2; }
		
		// velocity correction 
		x += (speedX*xCorrect);
		y += (speedY*yCorrect);
		System.out.println();
				
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
	public double checkProjectile (int charX, int charY, ArrayList<Tiles> Rtiles) {
		double monDamage = 0;
		for(Projectile p : projectiles){
			if(this.x == charX && this.y == charY) {
				//make bullet invisible
				// damage = p.damage
			}
			for(Tiles t : Rtiles) {
				// if (this.x == t.getX() && this.y == t.getY()){
				//		if(blockable) => delete projectile 
				// }
			}
		}
		
		return monDamage;
	}
	
	// draw method 
	
	
	
}
