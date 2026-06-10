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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class GamePanel extends JPanel implements ActionListener {
    Timer timer;

    // Lets MainClass decide what happens when the player confirms returning to menu.
    public interface ReturnToMenuListener {
        void onReturnToMenu();
    }

    private ReturnToMenuListener returnToMenuListener;

    public void setReturnToMenuListener(ReturnToMenuListener listener) {
        this.returnToMenuListener = listener;
    }

    // Track held movement keys so multiple keys can move the player diagonally.
    private boolean moveUp = false;
    private boolean moveDown = false;
    private boolean moveLeft = false;
    private boolean moveRight = false;

    // Pause / return-to-menu confirmation state.
    private boolean returnDialogOpen = false;
    private Rectangle closeButtonBounds = new Rectangle();
    private Rectangle yesButtonBounds = new Rectangle();
    private Rectangle noButtonBounds = new Rectangle();
    // 0 = YES, 1 = NO. Start on NO so Enter does not accidentally leave the game.
    private int returnDialogSelection = 1;

    //Time record variable
    int TIMERSPEED =10; // speed
    int GAMETIME = 0; // time in ms
    int countSec =0; // time in s
    double FULLTIME =0; // time in s and ms

    // Panel Width and Height
    private int width = 1000;
    private int height = 1000;

    //Object Initialization
    Monster m1;
    Monster mDecoy,mDecoy2, mDecoy3;
    ArrayList<Monster> monsters = new ArrayList <Monster>();

    Room r;

    Archer archer = new Archer(100,5,100,5,10,10,"Archer");

    Map map = new Map(width, height, 1);

    public GamePanel(HashMap<String, Integer> hashMap){ //later on sep the hash into a new class
        //Panel setup
        this.setPreferredSize(new Dimension(width, height));
        this.addKeyListener(new KeyLis());
        this.addMouseListener(new MouseLis());
        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);

        // Character setup
        // Archer_animation.png is the gameplay sprite sheet.
        // Frame 0 is the idle/resting image, and the other frames create walking movement.
        archer.setWalkAnimation(loadImage("Archer_animation.png"), 5);
        archer.weaponInit(10,4,4,0.1,10,"Bow",loadImage("staff-animation.png"),4,0.7,loadImage("Bullet.png")); // to flip the img use negative value

        //timer
        timer = new Timer(TIMERSPEED, this);
        timer.start();
        timer.setInitialDelay(10);

        //init of stuff
        // this should later be move into room
        m1 = new RangeMonster(hashMap,0,100,100,50,50,0.3);
//        mDecoy2 = new RangeMonster(m1Stats,0,100,100,100,800);
//        mDecoy3 = new RangeMonster(m1Stats,0,100,100,800,100);

        mDecoy = new RangeMonster(hashMap,0,100,100,800,800,0.3);
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
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //Map
//      g2.drawImage(map.scaleImg,0, 0,null);
        g2.drawImage(
                map.scaleImg,
                map.dx1, map.dy1,
                map.dx2, map.dy2,
                map.sx1, map.sy1,
                map.sx2, map.sy2,
                null);

        //Character
        g2.drawImage(archer.getCurrentImage(), (int)archer.getX(), archer.y, (int)archer.getWidth(), archer.height, null);
        archer.weapon.draw(g,archer);
        archer.drawCharacter(g);

        //Mons
        for(Monster m: monsters) {
            g2.draw(m);
        }

        drawCloseButton(g2);

        if (returnDialogOpen) {
            drawReturnDialog(g2);
        }
    }

    /**
     * Draws the small X button in the top-right corner of the game screen.
     */
    private void drawCloseButton(Graphics2D g2) {
        int size = 40;
        int margin = 20;
        int x = getWidth() - size - margin;
        int y = margin;
        closeButtonBounds.setBounds(x, y, size, size);

        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(x, y, size, size, 8, 8);

        g2.setColor(new Color(255, 255, 255, 220));
        g2.setStroke(new BasicStroke(3f));
        g2.drawLine(x + 11, y + 11, x + size - 11, y + size - 11);
        g2.drawLine(x + size - 11, y + 11, x + 11, y + size - 11);

        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(x, y, size, size, 8, 8);
    }

    /**
     * Draws the pause confirmation popup.
     */
    private void drawReturnDialog(Graphics2D g2) {
        int W = getWidth();
        int H = getHeight();

        // Dark transparent overlay.
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, 0, W, H);

        int boxW = 430;
        int boxH = 200;
        int boxX = (W - boxW) / 2;
        int boxY = (H - boxH) / 2;

        g2.setColor(new Color(45, 40, 55));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 18, 18);

        g2.setColor(new Color(220, 210, 180));
        g2.setStroke(new BasicStroke(3f));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 18, 18);

        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 24));
        String title = "Return to main screen?";
        FontMetrics titleFm = g2.getFontMetrics();
        int titleX = boxX + (boxW - titleFm.stringWidth(title)) / 2;
        g2.drawString(title, titleX, boxY + 65);

        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        String subtitle = "The game is paused while this menu is open.";
        FontMetrics subFm = g2.getFontMetrics();
        int subX = boxX + (boxW - subFm.stringWidth(subtitle)) / 2;
        g2.drawString(subtitle, subX, boxY + 95);

        int btnW = 130;
        int btnH = 45;
        int gap = 35;
        int btnY = boxY + 130;
        int yesX = boxX + (boxW - btnW * 2 - gap) / 2;
        int noX = yesX + btnW + gap;

        yesButtonBounds.setBounds(yesX, btnY, btnW, btnH);
        noButtonBounds.setBounds(noX, btnY, btnW, btnH);

        drawPopupButton(g2, yesButtonBounds, "YES", returnDialogSelection == 0);
        drawPopupButton(g2, noButtonBounds, "NO", returnDialogSelection == 1);

        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        String controls = "Use W/A/S/D to choose, Enter/J/Space to confirm";
        FontMetrics controlsFm = g2.getFontMetrics();
        int controlsX = boxX + (boxW - controlsFm.stringWidth(controls)) / 2;
        g2.drawString(controls, controlsX, boxY + boxH - 12);
    }

    private void drawPopupButton(Graphics2D g2, Rectangle bounds, String text, boolean selected) {
        if (selected) {
            g2.setColor(new Color(135, 112, 70));
        } else {
            g2.setColor(new Color(80, 72, 92));
        }
        g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 10, 10);

        if (selected) {
            g2.setColor(new Color(255, 235, 145));
            g2.setStroke(new BasicStroke(4f));
        } else {
            g2.setColor(new Color(230, 220, 190));
            g2.setStroke(new BasicStroke(2f));
        }
        g2.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 10, 10);

        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
        FontMetrics fm = g2.getFontMetrics();
        int textX = bounds.x + (bounds.width - fm.stringWidth(text)) / 2;
        int textY = bounds.y + (bounds.height + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(text, textX, textY);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (returnDialogOpen) {
            // Pause the game updates while the confirmation popup is open.
            return;
        }

        //update timer
        if(countSec == 1000) {
            GAMETIME++;
            countSec =0;
        }
        else {
            countSec += TIMERSPEED;
        }
        FULLTIME = ((double)(GAMETIME*1000+ countSec))/1000.0;

        updatePlayerMovement();

        // weapon animation + rotation
        if(archer.weapon.attack ==true) {
            archer.weapon.switchFrame();
        }

        //MONSTER ACTION
        m1.move(archer.x, archer.y);
        for(Monster m : monsters) {
            m.move(archer.x, archer.y);
        }

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

        //losing condition
        if(archer.health <=0) {
            timer.stop();
            System.out.println("Game end");
        }

        //repaint
        this.repaint();
    }

    /**
     * Opens the return-to-menu confirmation and freezes player movement.
     */
    private void openReturnDialog() {
        returnDialogOpen = true;
        returnDialogSelection = 1;
        moveUp = false;
        moveDown = false;
        moveLeft = false;
        moveRight = false;
        archer.updateWalkAnimation(false);
        repaint();
    }

    /**
     * Closes the return-to-menu confirmation and resumes the game.
     */
    private void closeReturnDialog() {
        returnDialogOpen = false;
        requestFocusInWindow();
        repaint();
    }

    /**
     * Stops this game panel and asks MainClass to show the main menu again.
     */
    private void returnToMainMenu() {
        timer.stop();
        if (returnToMenuListener != null) {
            returnToMenuListener.onReturnToMenu();
        }
    }

    /**
     * Confirms whichever pause-popup option is currently selected.
     */
    private void confirmReturnDialogSelection() {
        if (returnDialogSelection == 0) {
            returnToMainMenu();
        } else {
            closeReturnDialog();
        }
    }

    /**
     * Moves the pause-popup selection between YES and NO.
     */
    private void moveReturnDialogSelection(int direction) {
        if (direction == 0) {
            return;
        }
        returnDialogSelection += direction;
        if (returnDialogSelection < 0) {
            returnDialogSelection = 1;
        } else if (returnDialogSelection > 1) {
            returnDialogSelection = 0;
        }
        repaint();
    }

    /**
     * Moves the player based on all movement keys that are currently held.
     * This allows diagonal movement, for example W + D or S + A.
     */
    private void updatePlayerMovement() {
        int dirX = 0;
        int dirY = 0;

        if (moveLeft) {
            dirX--;
        }
        if (moveRight) {
            dirX++;
        }
        if (moveUp) {
            dirY--;
        }
        if (moveDown) {
            dirY++;
        }

        boolean moving = dirX != 0 || dirY != 0;

        if (moving) {
            archer.Move(dirX, dirY);

            if (dirX < 0) {
                archer.flip(false);
            } else if (dirX > 0) {
                archer.flip(true);
            }
        }

        archer.updateWalkAnimation(moving);
    }

    //Mouse input class
    private class MouseLis extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            requestFocusInWindow();

            if (returnDialogOpen) {
                if (yesButtonBounds.contains(e.getPoint())) {
                    returnDialogSelection = 0;
                    returnToMainMenu();
                }
                else if (noButtonBounds.contains(e.getPoint())) {
                    returnDialogSelection = 1;
                    closeReturnDialog();
                }
                return;
            }

            if (closeButtonBounds.contains(e.getPoint())) {
                openReturnDialog();
            }
        }
    }

    //Key input class
    private class KeyLis extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (returnDialogOpen) {
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_A:
                    case KeyEvent.VK_W:
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_UP:
                        moveReturnDialogSelection(-1);
                        break;
                    case KeyEvent.VK_D:
                    case KeyEvent.VK_S:
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_DOWN:
                        moveReturnDialogSelection(1);
                        break;
                    case KeyEvent.VK_ENTER:
                    case KeyEvent.VK_SPACE:
                    case KeyEvent.VK_J:
                        confirmReturnDialogSelection();
                        break;
                    case KeyEvent.VK_Y:
                        returnDialogSelection = 0;
                        returnToMainMenu();
                        break;
                    case KeyEvent.VK_N:
                    case KeyEvent.VK_ESCAPE:
                    case KeyEvent.VK_X:
                        returnDialogSelection = 1;
                        closeReturnDialog();
                        break;
                }
                return;
            }

            switch(e.getKeyCode()) {
                case KeyEvent.VK_X:
                    openReturnDialog();
                    break;
                case KeyEvent.VK_W:
                    moveUp = true;
                    break;
                case KeyEvent.VK_A:
                    moveLeft = true;
                    archer.flip(true);
                    break;
                case KeyEvent.VK_S:
                    moveDown = true;
                    break;
                case KeyEvent.VK_D:
                    moveRight = true;
                    archer.flip(false);
                    break;
                case KeyEvent.VK_J:
                    if (archer.weapon.Ready(FULLTIME)) {
                        archer.Attack(monsters);
                        archer.weapon.logTime(FULLTIME);
                        // maybe later modified so the attack is invoke every second
                        // it will not only attack but only do aiming and rotation calling
                        archer.weapon.attack = true;
                    }
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_W:
                    moveUp = false;
                    break;
                case KeyEvent.VK_A:
                    moveLeft = false;
                    break;
                case KeyEvent.VK_S:
                    moveDown = false;
                    break;
                case KeyEvent.VK_D:
                    moveRight = false;
                    break;
            }
        }
    }
}
