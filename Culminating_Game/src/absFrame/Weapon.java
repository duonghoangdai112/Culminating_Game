package absFrame;
public class Weapon {
	public int manaCost, vy, vx, cooldown,damage, angel;// stats
	public int width,height,frameMax,frameCur; //img
	public int sx1,sx2,sy1=0,sy2;
	boolean draw;
	
	public String name,imgName;
	public int count;
	
	
	
	public Weapon(int manaCost, int vx, int vy, int cooldown, int damage, int angel,String name,int width,int height) {
		this.manaCost = manaCost;
		this.vx = vx;
		this.vy = vy;
		this.cooldown = cooldown;
		this.damage = damage;
		this.angel = angel;
		this.name = name;
		this.imgName =name +"-animation.png";
		this.width = width;
		this.height = height;
		frameCur =0;
		sy2= height;
		sx1=100;
		sx2 =width+100;
		count=0;
	}
	
	public void rotation(int angle) {
		this.angel = angle;
	}
	
	public void switchFrame() {
		if(count==5) {
			
			frameCur+=1;
			if(frameCur == 8){//max
				System.out.println("hello");
				sx1=100;
				sx2 =width+100;
				frameCur=1;
			}
			else{
				sx1 = sx2;	
				sx2 = sx1+width;
			}
			System.out.println(sx1);
			System.out.println(sx2);
			count=0;
		}
		count++;
	}
	
}
