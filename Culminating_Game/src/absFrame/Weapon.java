package absFrame;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Weapon {
	//backend var
	public int manaCost, vy, vx,damage;// stats
	public double ratio,cooldown,angle;
	
	//img
	public int width,height,frameMax,frameCur; //img
	public BufferedImage wImg; //weapon img
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
			int angle,String name,int width,int height,BufferedImage wIMG) {
		this.manaCost = manaCost;
		this.vx = vx;
		this.vy = vy;
		this.cooldown = cooldown;// this is in secs
		this.damage = damage;
		this.angle = angle;
		this.name = name;
		this.imgName =name +"-animation.png";
		this.width = width;
		this.height = height;
		
		
		this.wImg = wIMG;
		sy1 = 0;
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
	public void setImage(int frameMax,double ratio,int charX,int charY ) {
		this.frameMax= frameMax;
		this.ratio = ratio;
		dx1 = charX;
		dy1 = charY;
		dx2 = dx1- ((int)(width*ratio));
		dx2 = dy1+ (int)(height*ratio);
				
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
	
	public Projectile createProjectile(Monster target) {
	    double startX = dx1;
	    double startY = dy1;

	    double targetX = target.x;
	    double targetY = target.y ;

	    double dx = targetX - startX;
	    double dy = targetY - startY;
	    
	    System.out.println("dx: "+dx);
	    System.out.println("dy: "+dy);

	    double angle = Math.atan2(dy, dx);

	    double projVx = Math.cos(angle) * 1;
	    double projVy = Math.sin(angle) * 1;
	    
	    System.out.println("projVx: "+projVx);
	    System.out.println("projVy: "+projVy);


	    int projW = 30;
	    int projH = 30;
	    return new Projectile((int)startX, (int)startY, projW,projH,projVx, projVy, damage);
	    
	}
	
	public void draw(Graphics g,Character character) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.drawString("Hello ", 100, 100);
		
		  int drawW = (int)(width * ratio);
		  int drawH = (int)(height * ratio);

		  int centerX = character.x + character.width / 2;
		  int centerY = character.y + character.height / 2;

		  dx1 = centerX;
		  dy1 = centerY - drawH / 2;
		  dx2 = dx1+drawW;
		  dy2 = dy1 +drawH;
		  
		  if(yflip) {
			  int temp = dy1;
			  dy1 = dy2;
			  dy2 = temp;
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
