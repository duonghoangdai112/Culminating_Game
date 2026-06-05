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
    
	Map map = new Map( width, height,1);


    public GamePanel(HashMap<String,Integer> m1Stats){ //later on sep the hash into a new class
    	//Panel setup
        this.setPreferredSize(new Dimension(width, height));
        this.addKeyListener(new KeyLis());
        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);
        
        //Character Setup
        archer.setCharIMG(loadImage(archer.imgName));
        archer.weaponInit(10,4,4,0.1,10,"Bow",loadImage("staff-animation.png"),4,0.7,loadImage("magic.png")); // to flip the img use negative value
        //timer 
        timer = new Timer(TIMERSPEED, this);
		timer.start();
		timer.setInitialDelay(10);

		
		//init of stuff
			// this should later be move into room
        m1 = new RangeMonster(m1Stats,0,100,100,50,50);
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
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//Map
//		g2.drawImage(map.scaleImg,0, 0,null);
		g2.drawImage(
				map.scaleImg,
				map.dx1, map.dy1, 
				map.dx2, map.dy2, 
				map.sx1, map.sy1, 
				map.sx2, map.sy2, 
				null);
		
		//Character
		g2.drawImage(archer.cIMG, (int)archer.getX(), archer.y, (int)archer.getWidth(), archer.height, null);
		archer.weapon.draw(g,archer);
		archer.drawCharacter(g);
		
		
		//Mons
		for(Monster m: monsters) {
			g2.draw(m);
		}
		
		
//		Projectile
		for (Projectile p: archer.projectile) {
    		p.move();
    		g2.drawImage(p.bulletImg, p.x, p.y, p.width*2, p.height*2, null);
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
       	
       	
        
        //MONSTER ACTION
        m1.move(archer.x, archer.y);
                
        
        //CHECK RESULT
  
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
		
		archer.checkCollision(width, height, map);
		
		//losing condtion
		if(archer.health <=0) {
			timer.stop();
			System.out.println("Game end");
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