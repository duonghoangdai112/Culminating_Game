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
    int TIMERSPEED = 25;

    private int width = 500; 
    private int length = 500;

    Monster m1;
    Room r;
    Archer archer = new Archer(100,5,100,10,10,10,"Archer");


    public GamePanel(HashMap<String,Integer> m1Stats){
        this.setPreferredSize(new Dimension(width, length));


        timer = new Timer(TIMERSPEED, this);
		timer.start();
		timer.setInitialDelay(0);

        Monster m1 = new RangeMonster(m1Stats,0);
        ArrayList<Monster> m1A = new ArrayList<Monster>();
        m1A.add(m1);


        Room r = new Room(200,200,null,m1A);

        System.out.println(m1.getX());
        

    }
    
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
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        Random rd = new Random();
			
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
		
		g2.drawImage(this.loadImage(archer.imgName), 50, 50, 100, 100, null);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.repaint();
       
    }

    private class KeyLis extends KeyAdapter implements KeyListener{
        public void keyPressed(KeyEvent e) {
            System.out.println("hello");
        }

    }

    


    
}