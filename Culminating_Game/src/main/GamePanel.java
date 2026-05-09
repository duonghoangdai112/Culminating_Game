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
    int TIMERSPEED =1;
    int GAMETIME = 0;
    int countSec =0;
    double FULLTIME =0;
    
    private int width = 500;
    private int height = 500;
    
    Monster m1;
    Room r;
    Archer archer = new Archer(100,5,100,4,10,10,"Archer");
	Map map = new Map( width, height,1);


    public GamePanel(HashMap<String,Integer> m1Stats){ //later on sep the hash into a new class
    	//setup
        this.setPreferredSize(new Dimension(width, height));
        this.addKeyListener(new KeyLis());
        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);

        //timer 
        timer = new Timer(TIMERSPEED, this);
		timer.start();
		timer.setInitialDelay(10);

		
		//init of stuff
//        Monster m1 = new RangeMonster(m1Stats,0);
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
		g2.drawImage(map.scaleImg,0, 0,null);
		//Character
		g2.drawImage(this.loadImage(archer.imgName), archer.x, archer.y, archer.width, archer.height, null);
		g2.drawImage( 
			    this.loadImage(archer.weapon.imgName),
			    (archer.weapon.dx1),
			    archer.weapon.dy1,
			    archer.x - ((int)(archer.weapon.width*archer.weapon.ratio)), // fix this later
			    archer.y + (int)(archer.weapon.height*archer.weapon.ratio), // fix this later
			    archer.weapon.sx1,
			    archer.weapon.sy1,
			    archer.weapon.sx2,
			    archer.weapon.sy2,
			    null
		);
		
		//Mons
		Rectangle mons = new Rectangle(50,50,100,100);
		g2.draw(mons);
		
		//Projectile
		for (Projectile p: archer.projectile) {
    		p.move();
    		g2.draw(p);
    		if(p.intersects(mons)) {
    			System.out.println("hit");
    			p.setVisibility(false);
    			}
    	}

    }

    @Override
    public void actionPerformed(ActionEvent e) {
    	// weapon animation
    	if(archer.weapon.attack ==true) {
	    	archer.weapon.switchFrame();	
    	}
       	archer.weapon.setImage(archer.maxImg,0.5,archer.x,archer.y,archer.Wdir);

       	//update timer
        if(countSec == 1000) {
        	GAMETIME++;
        	countSec =0;
        }
        else {
        	countSec += TIMERSPEED;
        }
        FULLTIME = ((double)(GAMETIME*1000+ countSec))/1000.0;
        
        //check result
		archer.RemoveProj();

		//repaint
        this.repaint();
        
    }

    private class KeyLis extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
        	String input = KeyEvent.getKeyText(e.getKeyCode()).toLowerCase();
        	switch(input) {
        		case "w": 
        			archer.Move(0,-1 );
        			break;
        		case "a":
        			archer.Move(-1,0 );
        			break;
        		case "s": 
        			archer.Move(0,1 );
        			break;
        		case "d": 
        			archer.Move(1,0 );
        			break;
        		case "j":
        		    if (archer.weapon.Ready(FULLTIME)) {
        		        archer.Attack();
        		        archer.weapon.logTime(FULLTIME);

        		        archer.weapon.attack =true;
//        		    }
        		    break;

        		}
        	}
        }
    }
    
}