package main;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import absFrame.Room;

public class Map {

    private BufferedImage mapImage;

    public Room room1;
    public Room hallway1;
    public Room room2;
    public Room hallway2;
    public Room room3;
    public Room hallway3;
    public Room room4;
    
    public Room currentRoom;
    
    public Map() {

        loadMap();

        createRooms();

        currentRoom = room1;
    }

    private void loadMap() {

        try {

            URL url =
                getClass().getResource("/mainmap.png");

            mapImage = ImageIO.read(url);

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void createRooms() {

        // x, y, width, height
        room1 =
            new Room(31, 252, 160, 90);

        hallway1 =
            new Room(191, 162, 110, 161);

        room2 =
            new Room(161, 12, 90, 160);
        
        hallway2 =
            new Room(241, 32, 100, 50);
        
        room3 =
            new Room(331, 12, 160, 90);
        
        room4 =
            new Room(121, 362, 250, 130);
        

        // Linked list
        room1.next = hallway1;

        hallway1.previous = room1;
        hallway1.next = room2;

        room2.previous = hallway1;
        room2.next = hallway2;

        hallway2.previous = room2;
        hallway2.next = room3;

        room3.previous = hallway2;
        room3.next = room4;

        room4.previous = room3;
        
        // Door rectangles
        room1.nextDoor =
            new java.awt.Rectangle(
                181-31, 272-252,
                10, 30);

        hallway1.previousDoor =
            new java.awt.Rectangle(
                191-191, 272-161,
                10, 30);

        hallway1.nextDoor =
            new java.awt.Rectangle(
                191-191, 171-162,
                30, 10);

        room2.previousDoor =
            new java.awt.Rectangle(
                0, 250,
                50, 100);
        
        room2.nextDoor =
        	new java.awt.Rectangle(
                    241, 42,
                    10, 30);

        hallway2.previousDoor =
        	new java.awt.Rectangle(
        			241, 42,
                    10, 30);

        hallway2.nextDoor =
        	new java.awt.Rectangle(
                    331, 42,
                    10, 30);

        //next door 
        room3.nextDoor =
        	new java.awt.Rectangle(
        			331, 42,
                    10, 30);
    }

    public BufferedImage getCurrentRoomImage() {

        return mapImage.getSubimage(
            currentRoom.getMapX(),
            currentRoom.getMapY(),
            currentRoom.getMapWidth(),
            currentRoom.getMapHeight()
        );
    }

	public Room getCurrentRoom(){
		return currentRoom;
		
	}
	
	public Color getPixelColor(int x, int y) {

	    BufferedImage roomImg = getCurrentRoomImage();

	    if(x < 0 || y < 0 ||
	       x >= roomImg.getWidth() ||
	       y >= roomImg.getHeight()) {

	        return Color.BLACK;
	    }

	    return new Color(
	        roomImg.getRGB(x, y)
	    );
	}
	
	public boolean isWall(Color c) {

	    return c.getRed() > 100 &&
	           c.getRed() < 170 &&
	           c.getGreen() > 40 &&
	           c.getGreen() < 100 &&
	           c.getBlue() < 60;
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
    
  
    
    
    
	
}
