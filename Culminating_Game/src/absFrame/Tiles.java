package absFrame;

public class Tiles {
	private boolean crossable;
	private int x; 
	private int y;
	private String name ; 
	private int size;

	Tiles(boolean crossable, int posX, int posY, String name, int size ){
		this.x = posX;
		this.y = posY;
		this.name = name ; 	
		this.size = size;
	}
	
	public String getName(){
		return this.name;
	}

	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public boolean getCrossable(){
		return crossable;
	}

	public void setCrossable(boolean a){
		crossable = a;
	}
	
	//draw method
}
