package absFrame;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Monster {
	
	double health, damage, visionRange;
	double speedX, speedY, speed,hitBox;

	double cooldown; 
	int x; 
	int y; 
	ArrayList<Projectile> projectiles; 
	int startTime; 
	//image
	
	// initialize all attribute here 
	public Monster(HashMap<String,Integer> stats,int startTime){
		this.health = stats.get("health");
		this.damage = stats.get("damage");
		this.visionRange = stats.get("visionRange");
		
		this.speedX = stats.get("speedX");
		this.speedY = stats.get("speedY");
		this.hitBox = stats.get("hitBox");

		this.x = stats.get("x");
		this.y = stats.get("y");
		this.hitBox = stats.get("hitBox");

		this.startTime = startTime;
		speed =  Math.sqrt(speedX*speedX +speedY *speedY);
	}
	
	public double getHealth() {
		return health;
	}

	public void reduceHealth(double damage){
		health -= damage; 
	}

	public int getX(){return x;}

	public int getY(){return y;}
	
	public abstract void Attack() ;
	//when create a bullet just get the monster speedX and Y and times it by cons for the speed
	
	/**
	 * Move the position of the monster toward character
	 * @param dirX indicate x direction (1 right, 0 stationary , 2 left)
	 * @param dirY indicate y direction (1 up , 0 stationary , -1 down) 
	 */
	public void move() {
		x += speedX;
		y += speedY;
		
		//display image 
	}
	/**
	 * Turn and move the monster toward the character 
	 * @param charX character X position
	 * @param charY character Y position
	 */
	public void pathFinding(int charX, int charY) {
		int deltX = x- charX; 
		int deltY = y - charY; 
		double angelY = Math.atan(deltY/deltX);
		double angelX = 90-angelY;
		
		speedX = speed *Math.sin(angelX);
		speedY = speed* Math.sin(angelY);

		this.move();
		
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
