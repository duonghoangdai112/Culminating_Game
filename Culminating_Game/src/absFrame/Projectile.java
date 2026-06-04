package absFrame;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;


public class Projectile extends Rectangle {

	boolean visibility = true; // if visibility is false it will get remove
	double speedX; 
	double speedY;
	double damage;
	double xx,yy;
	//image
	public BufferedImage bulletImg;
	
	
	public Projectile(int x,int y,int width, int height,
			double speedX,double speedY , double damage, BufferedImage img){
		super(x,y,width,height);
		xx =x;
		yy= y;
		this.damage = damage;
		this.speedX = speedX; 
		this.speedY = speedY;
		bulletImg = img;
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
		xx+= speedX;
		yy+= speedY; //since speed is pretty small xx and yy is use to ensure accuracy
		x= (int)(xx);
		y=(int)(yy);
	}
	
	// draw method
}
