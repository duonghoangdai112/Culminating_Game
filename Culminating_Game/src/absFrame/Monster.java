package absFrame;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Monster extends Rectangle {
    
    public double health, damage, visionRange;    
    public double cooldown; 
    
    public double dx,dy;
    public double xx,yy;
    ArrayList<Projectile> projectiles; 
    int startTime; 

    // Enemy image / animation.
    public BufferedImage monsterImage;
    public BufferedImage[] walkFrames;
    public int walkFrameIndex = 0;
    public int walkFrameCounter = 0;
    public int walkFrameDelay = 8;
    public boolean faceLeft = true;
    
    // initialize all attribute here 
    public Monster(HashMap<String,Integer> stats,int startTime,int x, int y,int width,int height,double speed){
        super(x,y,width,height);
        
        this.health = stats.get("health");
        this.damage = stats.get("damage");
        this.visionRange = stats.get("visionRange");
        
        this.dx = stats.get("speedX")*speed;
        this.dy = stats.get("speedY")*speed;

        this.xx = x;
        this.yy=y;
        
        this.x = x;
        this.y = y;

        this.startTime = startTime;
    }
    
    public void setWorldPosition(int x, int y) {
        this.x = x;
        this.y = y;
        this.xx = x;
        this.yy = y;
    }

    /**
     * Sets the still image used by this monster.
     * This is used as the fallback image if no animation sheet is loaded.
     */
    public void setMonsterImage(BufferedImage img) {
        monsterImage = img;
        if (img != null && width <= 0 && height <= 0) {
            width = img.getWidth();
            height = img.getHeight();
        }
    }

    /**
     * Gives the monster a walking sprite sheet with equal-width frames.
     * This works the same way as the player's walking animation.
     */
    public void setWalkAnimation(BufferedImage sheet, int frameCount) {
        if (sheet == null || frameCount <= 0) {
            return;
        }

        int frameW = sheet.getWidth() / frameCount;
        int frameH = sheet.getHeight();
        if (frameW <= 0 || frameH <= 0) {
            return;
        }

        walkFrames = new BufferedImage[frameCount];
        for (int i = 0; i < frameCount; i++) {
            walkFrames[i] = sheet.getSubimage(i * frameW, 0, frameW, frameH);
        }

        // Keep enemies about the same size as before, but preserve sprite aspect ratio.
        double scale = 100.0 / frameW;
        width = (int) (frameW * scale);
        height = (int) (frameH * scale);

        if (monsterImage == null) {
            monsterImage = walkFrames[0];
        }
    }

    public void updateWalkAnimation(boolean moving) {
        if (walkFrames == null || walkFrames.length == 0) {
            walkFrameIndex = 0;
            walkFrameCounter = 0;
            return;
        }

        if (!moving) {
            walkFrameIndex = 0;
            walkFrameCounter = 0;
            return;
        }

        // Frame 0 is the idle frame. While walking, cycle through frames 1+
        // so the monster does not keep flashing back to the standing pose.
        if (walkFrames.length > 1 && walkFrameIndex == 0) {
            walkFrameIndex = 1;
        }

        walkFrameCounter++;
        if (walkFrameCounter >= walkFrameDelay) {
            walkFrameCounter = 0;

            if (walkFrames.length > 1) {
                walkFrameIndex++;
                if (walkFrameIndex >= walkFrames.length) {
                    walkFrameIndex = 1;
                }
            } else {
                walkFrameIndex = 0;
            }
        }
    }

    public BufferedImage getCurrentImage() {
        if (walkFrames != null && walkFrames.length > 0) {
            return walkFrames[walkFrameIndex];
        }
        return monsterImage;
    }

    /**
     * Draws the monster as an image instead of just drawing its rectangle hitbox.
     */
    public void drawMonster(Graphics2D g2) {
        BufferedImage img = getCurrentImage();
        if (img == null) {
            g2.draw(this);
            return;
        }

        if (faceLeft) {
            g2.drawImage(img, x, y, width, height, null);
        } else {
            // Flip horizontally when the monster is facing right.
            g2.drawImage(img, x + width, y, -width, height, null);
        }
    }

    public void setFacingFromDelta(double deltaX) {
        if (deltaX < 0) {
            faceLeft = true;
        } else if (deltaX > 0) {
            faceLeft = false;
        }
    }

    public double getHealth() {
        return health;
    }

    public void reduceHealth(double damage){
        health -= damage; 
    }
    
    public abstract void Attack() ;
    //when create a bullet just get the monster speedX and Y and times it by cons for the speed
    
    
    /**
     * Turn and move the monster toward the character 
     * @param charX character X position
     * @param charY character Y position
     */
    public void move(int charX, int charY) {
        int oldX = x;
        int oldY = y;
        
        double deltX =  charX-x; 
        double deltY =  charY-y; 

        if (deltX < 0) {
            faceLeft = true;
        } else if (deltX > 0) {
            faceLeft = false;
        }

        double xCorrect =1;
        double yCorrect = 1; 
        
        double speedX = 0;
        double speedY = 0;
        
        // check x
        if(deltX>0) { speedX =dx ;}
        else if (deltX<0) {speedX = -dx;}
        else {yCorrect = 2; }
        
        // check y
        if(deltY>0) {speedY = dy;}
        else if (deltY<0) {speedY = -dy;}

        else {xCorrect = 2; }
        
        
        // velocity correction 
        xx += (speedX*xCorrect);
        yy += (speedY*yCorrect);
        
        x = (int) xx;
        y=(int) yy;

        // Use the intended movement direction for animation instead of integer
        // position change. This matters because the monster moves with decimal
        // speed, so x/y may not change every timer tick.
        boolean moving = Math.abs(deltX) > 1 || Math.abs(deltY) > 1;
        updateWalkAnimation(moving);
                
    } 
    /**
     * update bullet position and call the monster to attack if ready
     * @param time the time from the game clock
     */
    public void checkCD(int time ) {
        // if (time-startTime) % cooldown == 0 => this.attack()
        // for each projectile => p.move() 
    }
    
    /**
     * Check if monster projectile hit block or character and do action accordingly
     * @param charX - character X position
     * @param charY - character Y position 
     * @param Rtiles - collection of room tiles
     * @return
     */
    public void checkCollision (int charX, int charY, Character c) {
        if (c != null && this.intersects(c)) {
            c.takeDamage((int) damage);
        }
    }
}
