package absFrame;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import main.Map;

public abstract class Character extends Rectangle {
	public int health,mana,speed,visionRange,cooldown,maxHealth,maxMana;
	
	public BufferedImage cIMG; // character IMG
	public int screenW, screenH; // panel size
	public String name; // char  name 
	public String imgName; // char picture name 
	public Weapon weapon; 
	
	public boolean faceLeft = true; 
	
	public int startTime;
	
	
	public boolean isAleardyHit = false;
	public int tImmune = 100;
	
	public ArrayList<Projectile> projectile = new ArrayList<Projectile>();
	
	public Character(int health, int shield, int mana, int speed, int visionRange, int cooldown,String name) {
		this.maxHealth = health;
		this.maxMana = mana; 
		
		this.health = health;
		this.mana = mana;
		this.speed = speed;
		this.visionRange = visionRange;
		this.cooldown = cooldown;
		this.name = name;
		this.imgName = name +".png";
		this.x = 200;
		this.y = 200;
		this.width = 100;
		this.height =100;
		tImmune = 3;
	}
	
	/**
	 * grant immune for tImmune time after the character got hit 
	 * Countdown tImmune if immune is already granted
	 * @param hit
	 */
	public boolean countDownImmunity() {
		if(tImmune<=0) {
			isAleardyHit = false;
		}
		else {
			isAleardyHit = true; 
			tImmune --;
		}
		return isAleardyHit;
	}
	
	/**
	 * reset tImunity 
	 */
	public void resetHitTimer() {
		tImmune = 3;
	}
	
	
	public void drawCharacter(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
//		g2.drawImage(archer.cIMG, (int)archer.getX(), archer.y, (int)archer.getWidth(), archer.height, null);
//		archer.weapon.draw(g,archer);	
		
		//drawing health bar
		// TO DO : change barW and barL to make it better 
		int numbMaxBar = maxHealth/10;
		int numbBar = health/10;
		int barW = 30; int barL = 50;
		
		
		for(int i=0;i<numbMaxBar;i++) {
			if(numbBar>0 ) {
				g2.setColor(Color.RED);
				g2.fillRect(10+i*barW, 30, barW, barL);
				numbBar--;
			}
			
			g2.setColor(Color.WHITE);
			g2.drawRect(10+i*barW, 30, barW, barL);
			
		}
		
		int maxManaBar = maxMana/10;
		int curManaBar = mana/10;
		for(int i=0;i<maxManaBar;i++) {
			if(curManaBar>0 ) {
				g2.setColor(Color.BLUE);
				g2.fillRect(10+i*barW, 70, barW, barL);
				numbBar--;
			}
			
			g2.setColor(Color.WHITE);
			g2.drawRect(10+i*barW, 70, barW, barL);
			
		}
	}
	
	
	/**
	 * Give the image to the character
	 * @param img
	 */
	public void setCharIMG(BufferedImage img) {cIMG = img;}
	/**
	 * create the weapon
	 */
	public void weaponInit(int manaCost,int vx,int vy,double cd,int damage,
			String name, BufferedImage wIMG,int maxFrameW,double d) {
		int angel = 0;
		int width = wIMG.getWidth()/maxFrameW;
		int height = wIMG.getHeight();
		System.out.println(d);
		weapon = new Weapon(manaCost,vx,vy,cd,damage,angel,name,width,height,wIMG); 
		weapon.setImage(maxFrameW,d);
	}
	/**
	 * change facing state 
	 * @param b- false = left, true = right
	 */
	public void flip(boolean b) {faceLeft = b;}
	
	/**
	 * this function change x-coordinate for image flipping
	 */
	public double getX() {
		if(faceLeft) {return x;}
		else {return x+width;}
	}
	/**
	 * this function change width for image flipping
	 */
	public double getWidth() {
		if(faceLeft) {return width;}
		else {return -width;}
	}
	
	/**
	 * check if Chacter projectile has hit the monster and minus the HP of that 
	 * @param mons
	 */
	public void checkProjectile(ArrayList<Monster> mons) {
		for(Projectile p: this.projectile) {
			for(Monster m : mons) {
				if(m.intersects(p)) {
					m.reduceHealth(p.damage);
//					System.out.println(m.getHealth());
				}
			}
		}
	}
	
	public void RemoveProj() {
		if(projectile.isEmpty()) {

		}
		
		else {
			ArrayList<Projectile> projTemp = (ArrayList<Projectile>) projectile.clone();
			for (Projectile p: projectile) {
				if(p.getVisibility() == false) {
					projTemp.remove(p);
				}
			}
			projectile = projTemp;
		}
		
		
	}
	
	public abstract void Ability();
	
	/**
	 * Change the character's x and y coordinates
	 * @param xchange change in the character's x coordinates
	 * @param ychange change in the character's y coordinates
	 * return 
	 */
	public void Move(int dirX, int dirY) {
				
		this.x += dirX*speed;
		this.y += dirY*speed;
		
	}
	
	public void checkCollision(int screenW,int screenH,Map map){
		this.screenW = screenW;
		this.screenH = screenH;
				
		//case left
		if(x <0) {
			boolean cameraMoved = map.changeBackground(new int[] {-1, 0});

			if (cameraMoved) {x = screenW - width;} 
			else {x = 0;}
		}
		
		//case up
		if(y <0) {
			boolean cameraMoved = map.changeBackground(new int[] {0, -1});

			if (cameraMoved) {
				y = screenH - height;} 
			else {y = 0;}
		}

		//case right
		if(x> screenW-this.width) { 
			boolean cameraMoved = map.changeBackground(new int[] {1, 0}); 

		    if (cameraMoved) { x = 0; } 
		    else {x = screenW - width;}
		}
		
		// case down
		if(y> screenH-this.height) {
			boolean cameraMoved = map.changeBackground(new int[] {0, 1});

			if (cameraMoved) {y = 0;} 
			else {y = screenH - height;}
		}		
	}
	
	
	public int Find() {
		return 1;
	}
	/**
	 * log time for ability 
	 * @param time
	 */
	public void logTime(int time) {
		this.startTime = time;
	}
	
	public boolean AbilityReady(int time){
		if(time-startTime == cooldown) {return true;}
		return false;
	}
	
	/**
	 * character attack function 
	 * @param monsters - list of monster 
	 */
	public void Attack(ArrayList<Monster> monsters) {
		if(weapon.attack ==false) {
			weapon.attack = true;
			
			Monster target = findClosetEnemy(monsters);
			if(target != null) {
				Projectile p = weapon.createProjectile(this, target);
				projectile.add(p);
			}
			else {
				Projectile p = weapon.createProjectile(this);
				projectile.add(p);

			}
			
		}
	}
	
	public Monster findClosetEnemy(ArrayList<Monster> monsters) {
		Monster target = null;
		int dx = 0;
		int dy = 0;
		int hyp = 100000000; //just a very large number to avoid null in first check 
		if(monsters == null||monsters.size() ==0) {
			return null;}
		else {
			for(Monster m: monsters) {
				
				dx = m.x-x;
				dy = m.y-y;
				int temp = dx*dx +dy*dy;
				if(temp<hyp) {
					hyp = temp;
					target = m;
				}
			}
			weapon.setAngle(this, target);
			return target;
		}
	}
	
	
	
	/**
	 * Add or subtract the character's hp
	 * @param change in the character's hp
	 */
	public void changeHealth(int change) {
		this.health += change;
	}
	
	/**
	 * Add or subtract the character's mana
	 * @param change in the character's mana
	 */
	public void changeMana(int change) {
		this.mana += change;
	}
	
	
}

