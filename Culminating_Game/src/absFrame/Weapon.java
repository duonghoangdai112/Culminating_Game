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
	public double projRatio = 1;

	// Projectile pattern. Normal weapons keep the defaults: one projectile and no spread.
	private int projectileCount = 1;
	private double totalSpreadRadians = 0.0;

	// Muzzle position inside one unrotated sprite frame, stored as 0.0-1.0 ratios.
	// (1.0, 0.5) means the middle of the frame's right edge.
	private double muzzleXRatio = 1.0;
	private double muzzleYRatio = 0.5;
	
	
	
	public Weapon(int manaCost, int vx, int vy, double cooldown, int damage, 
			int angle,String name,int width,int height,BufferedImage wIMG,BufferedImage projImage,double projRatio) {
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
		this.projRatio = projRatio;
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
	 * Configures how many projectiles are fired and the total angle covered by them.
	 * For example, setProjectilePattern(5, 24) produces five pellets at
	 * -12, -6, 0, +6 and +12 degrees from the aiming direction.
	 */
	public void setProjectilePattern(int count, double totalSpreadDegrees) {
		projectileCount = Math.max(1, count);
		totalSpreadRadians = Math.toRadians(Math.max(0.0, totalSpreadDegrees));
	}

	/**
	 * Sets the muzzle location inside a single sprite frame.
	 * Values are ratios: x=0 is the left edge, x=1 is the right edge,
	 * y=0 is the top and y=1 is the bottom.
	 */
	public void setMuzzleOffset(double xRatio, double yRatio) {
		muzzleXRatio = Math.max(0.0, Math.min(1.0, xRatio));
		muzzleYRatio = Math.max(0.0, Math.min(1.0, yRatio));
	}

	/**
	 * create a projectile that head toward target
	 * @param target - the closet monster
	 * @return a projectile
	 */
	
	public Projectile createProjectile(Character character, Monster target,BufferedImage img) {
		return createProjectiles(character, target, img).get(0);
	}

	/**
	 * Creates all projectiles for one attack. A normal weapon returns one item;
	 * a shotgun can return several pellets with evenly spaced angles.
	 */
	public ArrayList<Projectile> createProjectiles(Character character, Monster target, BufferedImage img) {
		double targetX = target.x + target.width / 2.0;
		double targetY = target.y + target.height / 2.0;
		double[] muzzle = getMuzzleCenter(character);
		double baseAngle = Math.atan2(targetY - muzzle[1], targetX - muzzle[0]);
		return createProjectileBurst(character, baseAngle);
	}

	/**
	 * Create projectile(s) when there is no visible enemy.
	 */
	public Projectile createProjectile(Character character,BufferedImage img) {
		return createProjectiles(character, img).get(0);
	}

	public ArrayList<Projectile> createProjectiles(Character character, BufferedImage img) {
		return createProjectileBurst(character, angle);
	}

	private ArrayList<Projectile> createProjectileBurst(Character character, double baseAngle) {
		ArrayList<Projectile> shots = new ArrayList<Projectile>();
		int projW = 30;
		int projH = 30;
		double[] muzzle = getMuzzleCenter(character);
		double speed = projectileSpeed();

		for (int i = 0; i < projectileCount; i++) {
			double offset = 0.0;
			if (projectileCount > 1) {
				offset = -totalSpreadRadians / 2.0
						+ totalSpreadRadians * i / (projectileCount - 1.0);
			}

			double shotAngle = baseAngle + offset;
			double projVx = Math.cos(shotAngle) * speed;
			double projVy = Math.sin(shotAngle) * speed;

			shots.add(new Projectile(
					(int)Math.round(muzzle[0] - projW / 2.0),
					(int)Math.round(muzzle[1] - projH / 2.0),
					projW, projH, projVx, projVy, damage, projImg, projRatio));
		}

		return shots;
	}

	/**
	 * Finds the muzzle point in world coordinates. The muzzle location can be
	 * adjusted per sprite sheet, which is useful when frames contain padding.
	 */
	private double[] getMuzzleCenter(Character character) {
		double centerX = character.x + character.width / 2.0;
		double centerY = character.y + character.height / 2.0;

		double localX = (muzzleXRatio - 0.5) * width * ratio;
		double localY = (muzzleYRatio - 0.5) * height * ratio;
		if (yflip) {
			localY = -localY;
		}

		double muzzleX = centerX + Math.cos(angle) * localX - Math.sin(angle) * localY;
		double muzzleY = centerY + Math.sin(angle) * localX + Math.cos(angle) * localY;
		return new double[] {muzzleX, muzzleY};
	}

	private double projectileSpeed() {
		double speed = Math.hypot(vx, vy);
		return speed > 0 ? speed : 1;
	}
	
	public void draw(Graphics g,Character character) {
		Graphics2D g2 = (Graphics2D) g.create();
		
		  int drawW = (int)(width * ratio);
		  int drawH = (int)(height * ratio);

		  int centerX = character.x+6 + character.width / 2;
		  int centerY = character.y+6 + character.height / 2;

		  dx1 = centerX- drawW / 2;
		  dy1 = centerY - drawH / 2;
		  dx2 = dx1+drawW;
		  dy2 = dy1 +drawH;
		  
		  if(yflip) {
			  int temp = dy1;
			  dy1 = dy2;
			  dy2 = temp;
		  }
		  // Draw projectiles centered on their hitbox.
		  for (Projectile p: wProj) {
	    		p.move();

	    		int projDrawW = (int)Math.round(p.width * p.ratio);
	   		int projDrawH = (int)Math.round(p.height * p.ratio);
	   		int drawX = (int)Math.round(p.x + p.width / 2.0 - projDrawW / 2.0);
	   		int drawY = (int)Math.round(p.y + p.height / 2.0 - projDrawH / 2.0);

	    		Graphics2D projG = (Graphics2D) g2.create();
	    		projG.rotate(p.angle, p.x + p.width / 2.0, p.y + p.height / 2.0);
	    		projG.drawImage(p.bulletImg, drawX, drawY, projDrawW, projDrawH, null);
	    		projG.dispose();
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
		g2.dispose();
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
