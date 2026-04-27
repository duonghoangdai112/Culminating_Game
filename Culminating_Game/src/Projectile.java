
public class Projectile {
	private int x;
	private int y; 
	private double hitBox; // maybe set it the same as img size?
	private boolean visibility = true;
	private double speedX; 
	private double speedY;
	//image
	
	Projectile(int wX,int wY,double speedX,double speedY){
		x = wX;
		y= wY;
		
		this.speedX = speedX; 
		this.speedY = speedY;
		
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setVisibility(boolean visbile) {
		visibility =  visbile;
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
