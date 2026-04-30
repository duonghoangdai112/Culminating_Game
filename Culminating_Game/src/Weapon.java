public abstract class Weapon {
	int mana, vy, vx, cooldown,damage, angle;
	
	public Weapon(int mana, int vx, int vy, int cooldown, int damage, int angle) {
		this.mana = mana;
		this.vx = vx;
		this.vy = vy;
		this.cooldown = cooldown;
		this.damage = damage;
		this.angle = angle;
	}
	
	public void rotation(int angle) {
		this.angle = angle;
	}
	
}
