package absFrame;
public class Weapon {
	public int manaCost, vy, vx,damage, angel;// stats
	public int width,height,frameMax,frameCur; //img
	public double ratio,dir,cooldown;
	
	
	public int dx1,dx2,dy1,dy2;
	public int sx1,sx2,sy1=0,sy2;
	
	public String name,imgName;
	public int countSwtich=0;
	public final int maxSwitch =5; // this control the animation speed  
	public boolean attack;
	
	public int timeAttack;
	public boolean firstAttack=true;
	
	public double startTime=0;
	
	
	
	public Weapon(int manaCost, int vx, int vy, double cooldown, int damage, 
			int angel,String name,int width,int height) {
		this.manaCost = manaCost;
		this.vx = vx;
		this.vy = vy;
		this.cooldown = cooldown;// this is in secs
		this.damage = damage;
		this.angel = angel;
		this.name = name;
		this.imgName =name +"-animation.png";
		this.width = width;
		this.height = height;
		
		
		
		sy1=0;
		sy2 = height; 
		sx1 = 0;
		sx2 = sx1+width;
		
	}
	
	public void setImage(int frameMax,double ratio,int charX,int charY,int dir ) {
		
		
		this.frameMax= frameMax;
		this.ratio = ratio;
		this.dir = dir;
		dx1 = charX;
		dy1 = charY;
		dx2 = charX- ((int)(width*ratio));
		dx2 = dy1*dir+ (int)(height*ratio);
				
	}
	
	public void rotation(int angle) {
		this.angel = angle;
	}
	
	public void switchFrame() {
		if(countSwtich==5) { // 
			frameCur+=1;
			if(frameCur == frameMax){//max
				sy1=0;
				sy2 = height; 
				sx1 = 0;
				sx2 = sx1+width;
				frameCur =0;
				attack = false;
			}
			else{
				if(attack ==true) {
					sx1 = sx2;	
					sx2 = sx1+width;
				}

			}
			
			countSwtich=0;
		}
		countSwtich++;
	}
	
	public void logTime(double time) {
		this.startTime = time;
	}
	
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
	
	
	
	
	
	
	 
	
	 
	
	
	
	
}
