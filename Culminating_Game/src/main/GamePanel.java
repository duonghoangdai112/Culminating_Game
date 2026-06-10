package main;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import absFrame.*;
import sprite.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.*;



public class GamePanel extends JPanel implements ActionListener {
    Timer timer; 
    
    int playerMapX;
    int playerMapY;
    
    
    boolean atNextDoor = false;
    boolean atPreviousDoor = false;
    
    //Time record variable 
    int TIMERSPEED =10; // speed
    int GAMETIME = 0; // time in ms
    int countSec =0; // time in s
    double FULLTIME =0; // time in s and ms 
    
    // Panel With and Height
    private int width = 1000;
    private int height = 1000;
    
    //Object Initialization
    Monster m1;
    Monster mDecoy,mDecoy2, mDecoy3;
    ArrayList<Monster> monsters = new ArrayList <Monster>();
    
    Room r;
    
    Archer archer = new Archer(100,5,100,10,10,10,"Archer");
    
	Map map = new Map();

	private Rectangle getDoorScreenRect(Rectangle door) {

	    BufferedImage roomImage =
	            map.getCurrentRoomImage();

	    double scaleX =
	            (double)getWidth() /
	            roomImage.getWidth();

	    double scaleY =
	            (double)getHeight() /
	            roomImage.getHeight();

	    return new Rectangle(
	            (int)(door.x * scaleX),
	            (int)(door.y * scaleY),
	            (int)(door.width * scaleX),
	            (int)(door.height * scaleY)
	    );
	}

	
    public GamePanel(HashMap<String,Integer> m1Stats){ //later on sep the hash into a new class
    	//Panel setup
        this.setPreferredSize(new Dimension(width, height));
        this.addKeyListener(new KeyLis());
        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);
        
        //Character Setup
        archer.setCharIMG(loadImage(archer.imgName));
        archer.weaponInit(1,1,1,0.1,10,"Sniper",300,300,loadImage("Sniper-animation.png"));
        //timer 
        timer = new Timer(TIMERSPEED, this);
		timer.start();
		timer.setInitialDelay(10);

		
		//init of stuff
			// this should later be move into room
        m1 = new RangeMonster(m1Stats,0,100,100,100,100);
//        mDecoy2 = new RangeMonster(m1Stats,0,100,100,100,800);
//        mDecoy3 = new RangeMonster(m1Stats,0,100,100,800,100);

        mDecoy = new RangeMonster(m1Stats,0,100,100,800,800);
        monsters.add(m1);
        monsters.add(mDecoy);
//        monsters.add(mDecoy2);
//        monsters.add(mDecoy3);


//        ArrayList<Monster> m1A = new ArrayList<Monster>();
//        m1A.add(m1);
//
//
//        Room r = new Room(200,200,null,m1A);

    }
    
    // maybe move this to absFrame later
    BufferedImage loadImage(String filename) {
        URL url = this.getClass().getResource("/" + filename);
        BufferedImage img = null;

        if (url != null) {
            try {
                img = ImageIO.read(url);
            } catch (IOException e) {
                System.out.println(e.toString());
                JOptionPane.showMessageDialog(null, "An image failed to load: " + filename,
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.out.println("URL is null for: " + filename);
        }

        return img;
    }


    public void paintComponent(Graphics g) {
    	//setup
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        Random rd = new Random();
        g2.setColor(Color.BLACK);
        
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Draw room
	    BufferedImage roomImage = map.getCurrentRoomImage();

	    double scaleX =
	        (double)getWidth() / roomImage.getWidth();

	    double scaleY =
	        (double)getHeight() / roomImage.getHeight();

	    double scale =
	        Math.min(scaleX, scaleY);

	    int drawW =
	        (int)(roomImage.getWidth() * scale);

	    int drawH =
	        (int)(roomImage.getHeight() * scale);

	    g2.drawImage(roomImage,0,0,drawW,drawH,null);

	    // Draw doors (DEBUG)
	    Room room = map.getCurrentRoom();

	    g2.setColor(Color.RED);

	    if(room.nextDoor != null) {

	        g2.fillRect(
	            (int)(room.nextDoor.x * scaleX),
	            (int)(room.nextDoor.y * scaleY),
	            (int)(room.nextDoor.width * scale),
	            (int)(room.nextDoor.height * scale)
	        );
	    }
	    
	    if(room.previousDoor != null) {

	        g2.fillRect(
	            (int)(room.previousDoor.x * scale),
	            (int)(room.previousDoor.y * scale),
	            (int)(room.previousDoor.width * scale),
	            (int)(room.previousDoor.height * scale)
	        );
	    }
		
		//Character
		g2.drawImage(archer.cIMG, (int)archer.getX(), archer.y, (int)archer.getWidth(), archer.height, null);
		archer.weapon.draw(g,archer);
		archer.drawCharacter(g);
		
		
		//Mons
		for(Monster m: monsters) {
			g2.draw(m);
		}
		
		//Projectile
		for (Projectile p: archer.projectile) {
    		p.move();
    		g2.draw(p);
    		if(p.intersects(m1)) {
    			System.out.println("hit");
    			p.setVisibility(false);
    			}
    	}
		

    }

    @Override
    public void actionPerformed(ActionEvent e) {
    	//update timer
        if(countSec == 1000) {
        	GAMETIME++;
        	countSec =0;
        }
        else {
        	countSec += TIMERSPEED;
        }
        FULLTIME = ((double)(GAMETIME*1000+ countSec))/1000.0;
        
    	// weapon animation + rotation
    	if(archer.weapon.attack ==true) { 
	    	archer.weapon.switchFrame();	
    	}
       	archer.weapon.setImage(archer.maxImg,0.5,archer.x,archer.y);
       	
       	
        
        //Monster action 
        m1.move(archer.x, archer.y);
                
        
        //Check result
  
		archer.RemoveProj(); //remove bad projectile
	
		archer.checkProjectile(monsters); //check if projectile of character hit monster
		ArrayList<Monster> temp = (ArrayList<Monster>) monsters.clone();
		for(int i = 0;i<temp.size();i++) {
			if(temp.get(i).getHealth() <=0) {
				monsters.remove(i);
			}
			
		}
		
		//count down or grant immunity if got hit
		
		if(!archer.countDownImmunity()) {
			for(Monster m: monsters) { //check collision with character
				m.checkCollision(archer.x, archer.y, null, archer);
			}
			archer.resetHitTimer();
		}
		
		//archer.checkCollision(width, height, map);
		
		atNextDoor = false;
		atPreviousDoor = false;

		if(map.currentRoom.nextDoor != null) {

		    Rectangle nextDoorScreen =
		            getDoorScreenRect(
		                    map.currentRoom.nextDoor);

		    if(archer.intersects(nextDoorScreen)) {

		        atNextDoor = true;
		    }
		}

		if(map.currentRoom.previousDoor != null) {

		    Rectangle prevDoorScreen =
		            getDoorScreenRect(
		                    map.currentRoom.previousDoor);

		    if(archer.intersects(prevDoorScreen)) {

		        atPreviousDoor = true;
		    }
		}
		
		
		if(atNextDoor &&
				   map.currentRoom.next != null) {

				    map.currentRoom =
				        map.currentRoom.next;

				    archer.x = 50;
				}
		
		if(atPreviousDoor &&
				   map.currentRoom.previous != null) {

				    map.currentRoom =
				        map.currentRoom.previous;

				    archer.x = 700;
				}
		
	
		if(map.currentRoom.next != null &&
				map.currentRoom.nextDoor != null &&
				map.currentRoom.isClear() &&
			   archer.intersects(map.currentRoom.nextDoor))
		{
				map.currentRoom = map.currentRoom.next;	    
					    archer.x = 50;
					}

		if(map.currentRoom.previous != null &&
				map.currentRoom.previousDoor != null &&
						map.currentRoom.isClear() &&
				   archer.intersects(map.currentRoom.previousDoor))
				{
				    map.currentRoom = map.currentRoom.previous;

				    archer.x = 700;
				}
		
		//repaint
        this.repaint();
        
    }
    
    

	//Key input class
    private class KeyLis extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
        	String input = KeyEvent.getKeyText(e.getKeyCode()).toLowerCase();
        	switch(input) {
        		case "w": 
        			archer.Move(0,-1 );
        			break;
        		case "a":
        			archer.Move(-1,0 );
        			archer.flip(true);
        			break;
        		case "s": 
        			archer.Move(0,1);
        			break;
        		case "d": 
        			archer.Move(1,0 );
        			archer.flip(false);
        			break;
        		case "j":
        		    if (archer.weapon.Ready(FULLTIME)) {
        		        archer.Attack(monsters);
        		        archer.weapon.logTime(FULLTIME);
        		        // maybe later modified so the attack is invoke every second 
        		        // it will not only attack but only do aiming and rotation calling
        		        archer.weapon.attack =true;
//        		    }
        		    break;

        		}
        	}
        }
    }
    
}