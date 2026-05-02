package absFrame;
import java.util.*;
public abstract class Character {
	public int health,mana,speed,visionRange,cooldown;
	public int x = 0;
	public int y = 0;
	public String name;
	public String imgName;
	public Weapon weapon;
	public Character(int health, int shield, int mana, int speed, int visionRange, int cooldown,String name) {
		this.health = health;
		this.mana = mana;
		this.speed = speed;
		this.visionRange = visionRange;
		this.cooldown = cooldown;
		this.name = name;
		this.imgName = name +".png";
	}
	
	public void Remove() {
		
	}
	
	public abstract void Ability();
	
	/**
	 * Change the character's x and y coordinates
	 * @param xchange change in the character's x coordinates
	 * @param ychange change in the character's y coordinates
	 */
	public void Move(int dirX, int dirY) {
		this.x += dirX*speed;
		this.y += dirY*speed;
	}
	
	public int Find() {
		return 1;
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

