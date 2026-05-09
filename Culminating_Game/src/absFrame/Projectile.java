package absFrame;

import java.awt.Rectangle;

public class Projectile extends Rectangle {

	boolean visibility = true;
	double speedX; 
	double speedY;
	double damage; 
	//image
	
	public Projectile(int x,int y,int width, int height,
			double speedX,double speedY , double damage){
		super(x,y,width,height);
		this.damage = damage;
		this.speedX = speedX; 
		this.speedY = speedY;
		
	}

	public double getDamage(){
		return damage;
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
