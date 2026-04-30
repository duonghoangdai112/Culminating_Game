import java.util.*;
public abstract class Character {
	private int health,mana,speed,visionRange,cooldown;
	private int x = 0;
	private int y = 0;
	
	public Character(int health, int shield, int mana, int speed, int visionRange, int cooldown) {
		this.health = health;
		this.mana = mana;
		this.speed = speed;
		this.visionRange = visionRange;
		this.cooldown = cooldown;
	}
	
	public void Remove() {
		
	}
	
	public abstract void Ability();
	
	/**
	 * Change the character's x and y coordinates
	 * @param xchange change in the character's x coordinates
	 * @param ychange change in the character's y coordinates
	 */
	public void Move(int xchange, int ychange) {
		this.x += xchange;
		this.y += ychange;
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

