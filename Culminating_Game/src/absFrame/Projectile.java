package absFrame;

public class Projectile {
	int x;
	int y; 
	double hitBox; // maybe set it the same as img size?
	boolean visibility = true;
	double speedX; 
	double speedY;
	double damage; 
	//image
	
	Projectile(int wX,int wY,double speedX,double speedY , double hitBox, double damage){
		x = wX;
		y= wY;
		this.hitBox = hitBox;
		this.damage = damage;
		this.speedX = speedX; 
		this.speedY = speedY;
		
	}
	public double getHitBox(){return hitBox;}
	public int getX() {
		return x;
	}

	public double getDamage(){
		return damage;
	}
	
	public int getY() {
		return y;
	}
	
	public void setVisibility(boolean visible) {
		visibility =  visible;
	}
	
	public boolean getVisibility() {
		return visibility;
	}
	
	public void move() {
		x += speedX;
		y += speedY;
	}
	
	// draw method
}
