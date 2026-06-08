package absFrame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Weapon {
	//backend VAR
	public int manaCost, vy, vx,damage;// stats
	public int width,height,frameMax,frameCur; //img
	public double ratio,cooldown,angle;
	public ArrayList<Projectile> wProj = new ArrayList<Projectile> ();
	
	//img
	public BufferedImage wImg; //weapon img
	public BufferedImage projImg;
	public int dx1,dx2,dy1,dy2; //img destination location 
	public int sx1,sx2,sy1=0,sy2;  // img source location
	public String name,imgName;
	public boolean yflip = false;
	
	//animation
	public int countSwtich=0; // for animation delay
	public final int maxSwitch =5; // this control the animation speed  
	public boolean attack; // true if attack, false if not 
	
	public int timeAttack; 
	public boolean firstAttack=true;
	
	public double startTime=0; // time of the last attack 
	
	
	
	public Weapon(int manaCost, int vx, int vy, double cooldown, int damage, 
			int angle,String name,int width,int height,BufferedImage wIMG,BufferedImage projImage) {
		this.manaCost = manaCost;
		this.vx = vx;
		this.vy = vy;
		this.cooldown = cooldown;// this is in secs
		this.damage = damage;
		this.angle = angle;
		this.name = name;
		this.imgName =name +"-animation.png";
		this.projImg = projImage;
		this.width = width;
		this.height = height;
		
		
		this.wImg = wIMG;
		sy1=0;
		sy2 = height; 
		sx1 = 0;
		sx2 = sx1+width;
		
	}
	/**
	 * set the status for the weapon img 
	 * @param frameMax - the total amount of frame
	 * @param ratio - the scaling ratio
	 * @param charX - Character X
	 * @param charY - Character Y
	 */
	public void setImage(int frameMax,double ratio) {
		this.frameMax= frameMax;
		this.ratio = ratio;
				
	}
	
	/**
	 * change the image frame for animation
	 */
	public void switchFrame() {
		if(countSwtich==5) { // the countSwitch is to delay the animation 
			frameCur+=1; // move to the next frame
			if(frameCur == frameMax){//max case
				sy1=0;
				sy2 = height; 
				sx1 = 0;
				sx2 = sx1+width;
				frameCur =0; 
				attack = false;
			}
			else{
				if(attack ==true) { // change source position when it is attacking
					sx1 = sx2;	
					sx2 = sx1+width;
				}

			}
			
			countSwtich=0;
		}
		countSwtich++;
	}
	/**
	 * log the current attacking time 
	 * @param time - time during the attack in secs 
	 */
	public void logTime(double time) {
		this.startTime = time;
	}
	
	/**
	 * cooldown funciton
	 * @param time - the current time 
	 * @return true if the time diff exceed cooldown or if it the first attack. Otherwise return false
	 */
	public boolean Ready(double time){
		
		if(time-startTime >= cooldown) {return true;}

		if(time<cooldown && firstAttack) {
			firstAttack = false;
			System.out.println("succes");
			return true;
		}
		System.out.println("fail");

		return false;
	}
	
	/**
	 * create a projectile that head toward target
	 * @param target - the closet monster
	 * @return a projectile
	 */
	
	public Projectile createProjectile(Character character, Monster target,BufferedImage img) {
		int projW = 30;
		int projH = 30;

		double[] muzzle = getMuzzleCenter(character);
		double startX = muzzle[0];
		double startY = muzzle[1];

		// Aim at the centre of the monster, not its top-left corner.
		double targetX = target.x + target.width / 2.0;
		double targetY = target.y + target.height / 2.0;

		double dx = targetX - startX;
		double dy = targetY - startY;
		double projectileAngle = Math.atan2(dy, dx);
		double speed = projectileSpeed();

		double projVx = Math.cos(projectileAngle) * speed;
		double projVy = Math.sin(projectileAngle) * speed;

		// Rectangle x/y is top-left, so subtract half the projectile size.
		return new Projectile(
				(int)Math.round(startX - projW / 2.0),
				(int)Math.round(startY - projH / 2.0),
				projW, projH, projVx, projVy, damage,projImg);
	}

	/**
	 * Create a projectile when there is no visible enemy.
	 * The projectile still starts from the muzzle/end of the weapon.
	 * @return a projectile
	 */
	public Projectile createProjectile(Character character,BufferedImage img) {
		int projW = 30;
		int projH = 30;
		double[] muzzle = getMuzzleCenter(character);
		double speed = projectileSpeed();
		double projVx = Math.cos(angle) * speed;
		double projVy = Math.sin(angle) * speed;

		return new Projectile(
				(int)Math.round(muzzle[0] - projW / 2.0),
				(int)Math.round(muzzle[1] - projH / 2.0),
				projW, projH, projVx, projVy, damage,projImg);
	}

	/**
	 * Finds the muzzle/end point of the weapon in screen coordinates.
	 * The weapon sprite points right before rotation and is drawn starting at
	 * the character centre, so the muzzle is one scaled weapon-width away
	 * in the current weapon angle.
	 */
	private double[] getMuzzleCenter(Character character) {
		double drawW = width * ratio/2;
		double centerX = character.x + character.width / 2.0;
		double centerY = character.y + character.height / 2.0;

		double muzzleX = centerX + Math.cos(angle) * drawW;
		double muzzleY = centerY +  Math.sin(angle) * drawW;
		return new double[] {muzzleX, muzzleY};
	}

	private double projectileSpeed() {
		double speed = Math.hypot(vx, vy);
		return speed > 0 ? speed : 1;
	}
	
	public void draw(Graphics g,Character character) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.drawString("Hello ", 100, 100);
		
		  int drawW = (int)(width * ratio);
		  int drawH = (int)(height * ratio);

		  int centerX = character.x + character.width / 2;
		  int centerY = character.y + character.height / 2;

		  dx1 = centerX- drawW / 2;
		  dy1 = centerY - drawH / 2;
		  dx2 = dx1+drawW;
		  dy2 = dy1 +drawH;
		  
		  if(yflip) {
			  int temp = dy1;
			  dy1 = dy2;
			  dy2 = temp;
		  }
		  for (Projectile p: wProj) {
	    		p.move();
	    		g2.drawImage(p.bulletImg, p.x, p.y, p.width, p.height, null);
//	    		if(p.intersects(m1)) {
//	    			System.out.println("hit");
//	    			p.setVisibility(false);
//	    			}
		  }
	 
		  g2.rotate(angle,centerX,centerY);
	 g2.drawImage(
	        wImg,
	        dx1,
	        dy1,
	        dx2,
	         dy2,
	        sx1,
	        sy1,
	        sx2,
	        sy2,
	        null
	    );
	}
	
	public void setAngle(Character character, Monster target) {
        int charCenterX = character.x + character.width / 2;
        int charCenterY = character.y + character.height / 2;

        int targetCenterX = target.x + target.width / 2;
        int targetCenterY = target.y + target.height / 2;

        double dx = targetCenterX - charCenterX;
        double dy = targetCenterY - charCenterY;
        
        if(dx<0) {yflip = true;}
        else {yflip = false;}
        angle = Math.atan2(dy, dx);
    }
	
	
	
	
	
	
	 
	
	 
	
	
	
	
}
