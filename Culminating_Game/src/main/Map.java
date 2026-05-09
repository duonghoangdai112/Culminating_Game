package main;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class Map {
	int maxW = 1000;
	int maxL = 1000;
	int screenW,screenH;
	double scaleFactor =1;
	private BufferedImage img;
	Image scaleImg;
	
	public int dx1,dx2,dy1,dy2;
	public int sx1,sx2,sy1,sy2;
	
	
	public Map(int panelW, int panelH,double scaleFactor) {
		
		screenW = panelW; 
		screenH = panelH;
		
		img = loadImage("testMap.png");

		maxW = (int) (img.getWidth()*scaleFactor);
		maxL = (int) (img.getHeight()*scaleFactor);
		
		scaleImg = img.getScaledInstance(maxW,maxL, Image.SCALE_SMOOTH);
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
    
    
    
	
}
