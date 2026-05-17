package main;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import absFrame.*;
import absFrame.Character;

public class Map {
	// full size of img 
	int maxW = 1000; 
	int maxL = 1000;
	
	int screenW,screenH; //panel size
	double scaleFactor =1; // Img scale factor
	
	private BufferedImage img;
	Image scaleImg;
	
	public int dx1,dx2,dy1,dy2; // destination coordinate
	public int sx1,sx2,sy1,sy2; // source coordinate
	
	
	public Map(int panelW, int panelH,double scaleFactor) {
		
		screenW = panelW; 
		screenH = panelH;
		
		dx1 = 0;
		dy1=0;
		dx2 =screenW;
		dy2 = screenH;
		
		sx1 = 0;
		sy1=0;
		sx2 =(int) (screenW);
		sy2 = (int) (screenH);
		
		img = loadImage("testMap.png");

		maxW = (int) (img.getWidth()*scaleFactor);
		maxL = (int) (img.getHeight()*scaleFactor);
		
		scaleImg = img.getScaledInstance(maxW,maxL, Image.SCALE_SMOOTH);
	}
	
	
	/**
	 * read img
	 * @param filename - name of the file
	 * @return an BufferedImg object
	 */
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
    
    /**
     * Change the background of the img
     * @param frameChange - contain 2 elements, the direction of change in x and y 
     * 					  - 1 is down and right, -1 is up and left
     * @return true if back background change, false if not
     */
    public boolean changeBackground(int[] frameChange) {
    	int oldSx1 = sx1;
        int oldSy1 = sy1;

        sx1 += screenW * frameChange[0];
        sy1 += screenH * frameChange[1];

        // keep camera inside map image
        if (sx1 < 0) sx1 = 0;
        if (sy1 < 0) sy1 = 0;

        if (sx1 > maxW - screenW) sx1 = maxW - screenW;
        if (sy1 > maxL - screenH) sy1 = maxL - screenH;

        sx2 = sx1 + screenW;
        sy2 = sy1 + screenH;

        // return true only if the camera actually moved
        return sx1/100 != oldSx1/100 || sy1/100 != oldSy1/100; // the division is just to account for the bad shape of the test map
    	
    	

	}
    
    
    
	
}
