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
<<<<<<< Updated upstream
import java.util.*;


=======
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import absFrame.Character;
import sprite.Mech;
>>>>>>> Stashed changes

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

<<<<<<< Updated upstream
	private Rectangle getDoorScreenRect(Rectangle door) {
=======
    private Character player;
    private WorldMap worldMap = new WorldMap();
    private Camera camera = new Camera();
    private ArrayList<Monster> monsters = worldMap.getMonsters();
>>>>>>> Stashed changes

	    BufferedImage roomImage =
	            map.getCurrentRoomImage();

	    double scaleX =
	            (double)getWidth() /
	            roomImage.getWidth();

<<<<<<< Updated upstream
	    double scaleY =
	            (double)getHeight() /
	            roomImage.getHeight();
=======
        setupPlayerLoadout(characterName, weaponName);
        worldMap.placeCharacterInArea(player, "room1", 0.45, 0.50);
        setupWorldMonsters(hashMap);
>>>>>>> Stashed changes

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

<<<<<<< Updated upstream
        if (url != null) {
            try {
                img = ImageIO.read(url);
            } catch (IOException e) {
                System.out.println(e.toString());
                JOptionPane.showMessageDialog(null, "An image failed to load: " + filename,
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
=======
    /**
     * Sets up the chosen character and chosen weapon.
     * Right now Archer is the only character, but this keeps the game ready for more later.
     */
    private void setupPlayerLoadout(String characterName, String weaponName) {

        if ("Mech".equals(characterName)) {

            player = new Mech(
                    150,   // health
                    15,    // shield
                    50,    // mana
                    4,     // speed
                    10,
                    10,
                    "Mech"
            );

            // if you make a Mech sprite sheet later
            player.setFlyAnimation(loadImage("Mechfly.png"));

        } else {

            player = new Archer(
                    100,
                    5,
                    100,
                    5,
                    10,
                    10,
                    "Archer"
            );

            player.setWalkAnimation(loadImage("Archer_animation.png"), 5);
        }

        setupWeapon(weaponName);
    }

    /**
     * Applies the weapon that was selected on LoadoutScreen.
     */
    private void setupWeapon(String weaponName) {
        if (weaponName == null) {
            weaponName = "Bow";
        }

        switch (weaponName) {
            case "Staff":
                player.weaponInit(12, 5, 5, 0.7, 10, "Staff",
                        loadImage("staff-animation.png"), 4, 0.4, loadImage("magic.png"),2.0);
                break;

            case "Glock":
                player.weaponInit(8, 8, 8, 0.25, 8, "Glock",
                        loadImage("glock-animation.png"), 3, 0.25, loadImage("Bullet.png"),1.0);
                break;

            case "Sniper":
                player.weaponInit(16, 12, 12, 0.8, 25, "Sniper",
                        loadImage("Sniper-animation.png"), 3, 0.45, loadImage("Bullet.png"),1.1);
                break;

            case "Rifle":
                player.weaponInit(10, 8, 8, 0.3, 10, "Rifle",
                        loadImage("47-animation.png"), 4, 0.30, loadImage("Bullet.png"),0.8);
                break;

            case "Bow":
            default:
                player.weaponInit(10, 6, 6, 0.50, 10, "Bow",
                        loadImage("Bow-animation.png"), 9, -0.70, loadImage("Arrow.png"),-3);
                break;
        }
    }

    /**
     * Randomly spawns monsters inside each room on the full connected world map.
     * Hallways are skipped so monsters appear in rooms instead of narrow paths.
     */
    private void setupWorldMonsters(HashMap<String, Integer> monsterStats) {
        this.monsterStats = monsterStats;
        this.zombieWalkSheet = loadImage("Zombie.png");
        this.wolfWalkSheet = loadImage("Wolf.png");
        this.wolfDashEffect = loadImage("WolfDash.png");
        this.slimeKingSheet = loadImage("SlimeKing.png");
        this.slimeProjectileImage = loadImage("magic.png");

        worldMap.clearMonsters();

        // Start with a few monsters already present.
        for (String roomName : worldMap.getCombatRoomNames()) {
            spawnMonsterInRoom(roomName);
        }

        monsters = worldMap.getMonsters();
    }

    /**
     * Keeps spawning monsters until the survival timer reaches 100 seconds.
     */
    private void updateMonsterSpawning() {
        if (bossPhaseStarted || FULLTIME >= SURVIVAL_TIME_SECONDS || monsterStats == null) {
            return;
        }

        monsters = worldMap.getMonsters();
        if (monsters.size() >= MAX_ALIVE_MONSTERS) {
            return;
        }

        if (FULLTIME - lastMonsterSpawnTime < MONSTER_SPAWN_INTERVAL_SECONDS) {
            return;
        }

        lastMonsterSpawnTime = FULLTIME;

        // Spawn more enemies later in the round so the game ramps up.
        int spawnCount = FULLTIME >= 50 ? 2 : 1;
        for (int i = 0; i < spawnCount && worldMap.getMonsters().size() < MAX_ALIVE_MONSTERS; i++) {
            spawnMonsterAtRandomZone();
        }
    }

    private void spawnMonsterAtRandomZone() {
        String[] spawnZones = worldMap.getCombatRoomNames();
        if (spawnZones == null || spawnZones.length == 0) {
            return;
        }

        String roomName = spawnZones[spawnRandom.nextInt(spawnZones.length)];
        spawnMonsterInRoom(roomName);
    }

    private void spawnMonsterInRoom(String roomName) {
        if (monsterStats == null) {
            return;
        }

        Monster monster;

        // Randomly choose the enemy type every time something spawns.
        // Zombies are simple chasers. Wolves use their dash state machine.
        if (spawnRandom.nextBoolean()) {
            Zombie zombie = new Zombie(monsterStats, 0, 100, 100, 0, 0, 0.25);
            zombie.setWalkAnimation(zombieWalkSheet, 4);
            monster = zombie;
>>>>>>> Stashed changes
        } else {
            System.out.println("URL is null for: " + filename);
        }

        return img;
    }

<<<<<<< Updated upstream
=======
    private void placeMonsterRandomlyInRoom(String roomName, Monster monster, Random random) {
        int maxAttempts = 20;
        int safeDistanceFromPlayer = 180;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            java.awt.Point spawnPoint = worldMap.getRandomSpawnPointInArea(roomName, random, monster.width, monster.height);
            monster.setWorldPosition(spawnPoint.x, spawnPoint.y);

            if (!isTooCloseToPlayer(monster, safeDistanceFromPlayer)) {
                return;
            }
        }
    }

    private boolean isTooCloseToPlayer(Monster monster, int safeDistance) {
        int playerCenterX = player.x + player.width / 2;
        int playerCenterY = player.y + player.height / 2;
        int monsterCenterX = monster.x + monster.width / 2;
        int monsterCenterY = monster.y + monster.height / 2;

        int dx = playerCenterX - monsterCenterX;
        int dy = playerCenterY - monsterCenterY;
        return dx * dx + dy * dy < safeDistance * safeDistance;
    }

    private int randomBetween(Random random, int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    /**
     * Starts the boss phase after the player survives the normal 100-second wave.
     * Regular monsters are cleared so the final fight focuses on the Slime King.
     */
    private void startBossPhase() {
        bossPhaseStarted = true;
        worldMap.clearMonsters();

        HashMap<String, Integer> bossStats = new HashMap<String, Integer>();
        bossStats.put("health", 5000);
        bossStats.put("damage", 16);
        bossStats.put("visionRange", 1);
        bossStats.put("speedX", 1);
        bossStats.put("speedY", 1);

        slimeKingBoss = new SlimeKingBoss(bossStats, 0, 260, 260, 0, 0, 1.0);
        slimeKingBoss.setBossAnimation(slimeKingSheet, 4, 280);
        slimeKingBoss.setProjectileImage(slimeProjectileImage);

        worldMap.addMonsterToArea("arena", slimeKingBoss, 0.50, 0.50);
        monsters = worldMap.getMonsters();
    }

    private BufferedImage loadImage(String filename) {
        String[] resourceNames = {"/" + filename, "/assests/" + filename};
        for (String resourceName : resourceNames) {
            URL url = this.getClass().getResource(resourceName);
            if (url != null) {
                try {
                    return ImageIO.read(url);
                } catch (IOException e) {
                    System.out.println("Could not load image resource: " + resourceName);
                }
            }
        }

        String[] fileNames = {filename, "assests/" + filename};
        for (String fileName : fileNames) {
            File file = new File(fileName);
            if (file.exists()) {
                try {
                    return ImageIO.read(file);
                } catch (IOException e) {
                    System.out.println("Could not load image file: " + fileName);
                }
            }
        }

        System.out.println("Image not found: " + filename);
        JOptionPane.showMessageDialog(null, "An image failed to load: " + filename,
                "Error", JOptionPane.ERROR_MESSAGE);
        return null;
    }
>>>>>>> Stashed changes

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
		

<<<<<<< Updated upstream
=======
        Graphics2D worldG = (Graphics2D) g2.create();
        worldG.scale(camera.getZoom(), camera.getZoom());
        worldG.translate(-camera.getX(), -camera.getY());

        worldMap.draw(worldG);

        worldG.drawImage(player.getCurrentImage(), (int) player.getX(), player.y,
                (int) player.getWidth(), player.height, null);
        player.weapon.draw(worldG, player);

        monsters = worldMap.getMonsters();
        for (Monster monster : monsters) {
            monster.drawMonster(worldG);
        }
        worldG.dispose();

        // UI is drawn after the camera so it stays fixed to the screen.
        player.drawCharacter(g2);
        drawSurvivalTimer(g2);
        drawPlayerLevel(g2);

        if (!deathScreenOpen && !winScreenOpen) {
            drawCloseButton(g2);
        }

        if (winScreenOpen) {
            drawWinScreen(g2);
        } else if (deathScreenOpen) {
            drawDeathScreen(g2);
        } else if (levelUpScreenOpen) {
            drawLevelUpScreen(g2);
        } else if (returnDialogOpen) {
            drawReturnDialog(g2);
        }
    }

    private int getViewWidth() {
        return getWidth() > 0 ? getWidth() : width;
    }

    private int getViewHeight() {
        return getHeight() > 0 ? getHeight() : height;
    }

    private void updateCamera(int viewW, int viewH) {
        camera.follow(player, viewW, viewH, worldMap.getWorldWidth(), worldMap.getWorldHeight());
    }

    private void drawSurvivalTimer(Graphics2D g2) {
        int timeLeft = Math.max(0, SURVIVAL_TIME_SECONDS - (int) FULLTIME);
        String label;
        if (bossPhaseStarted) {
            label = "Boss: Slime King";
        } else {
            label = "Survive: " + timeLeft + "s";
        }

        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
        FontMetrics fm = g2.getFontMetrics();

        int x = 10;
        int y = 145;
        int boxW = fm.stringWidth(label) + 20;
        int boxH = 30;

        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(x, y - fm.getAscent(), boxW, boxH, 8, 8);

        g2.setColor(new Color(255, 235, 150));
        g2.drawString(label, x + 10, y);
    }

    private void drawPlayerLevel(Graphics2D g2) {
        String label = "LV " + player.level + "  EXP " + player.exp + "/" + player.expToNextLevel;

        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
        FontMetrics fm = g2.getFontMetrics();

        int x = 10;
        int y = 180;
        int boxW = fm.stringWidth(label) + 20;
        int boxH = 30;

        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(x, y - fm.getAscent(), boxW, boxH, 8, 8);

        g2.setColor(new Color(160, 230, 255));
        g2.drawString(label, x + 10, y);
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
        String controls = "Use joystic to choose, A to confirm";
        FontMetrics controlsFm = g2.getFontMetrics();
        int controlsX = boxX + (boxW - controlsFm.stringWidth(controls)) / 2;
        g2.drawString(controls, controlsX, boxY + boxH - 12);
    }

    private void drawLevelUpScreen(Graphics2D g2) {
        int W = getWidth();
        int H = getHeight();

        g2.setColor(new Color(0, 0, 0, 185));
        g2.fillRect(0, 0, W, H);

        int boxW = 540;
        int boxH = 360;
        int boxX = (W - boxW) / 2;
        int boxY = (H - boxH) / 2;

        g2.setColor(new Color(36, 42, 58));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 18, 18);

        g2.setColor(new Color(120, 210, 255));
        g2.setStroke(new BasicStroke(3f));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 18, 18);

        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 36));
        String title = "LEVEL UP!";
        FontMetrics titleFm = g2.getFontMetrics();
        int titleX = boxX + (boxW - titleFm.stringWidth(title)) / 2;
        g2.setColor(new Color(230, 250, 255));
        g2.drawString(title, titleX, boxY + 58);

        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        String subtitle = "Choose one upgrade";
        FontMetrics subFm = g2.getFontMetrics();
        int subX = boxX + (boxW - subFm.stringWidth(subtitle)) / 2;
        g2.drawString(subtitle, subX, boxY + 88);

        int btnW = 380;
        int btnH = 48;
        int btnX = boxX + (boxW - btnW) / 2;
        int firstY = boxY + 120;
        int gap = 18;

        healthUpgradeBounds.setBounds(btnX, firstY, btnW, btnH);
        damageUpgradeBounds.setBounds(btnX, firstY + btnH + gap, btnW, btnH);
        speedUpgradeBounds.setBounds(btnX, firstY + (btnH + gap) * 2, btnW, btnH);

        drawPopupButton(g2, healthUpgradeBounds, "1  HEALTH +20", levelUpSelection == 0);
        drawPopupButton(g2, damageUpgradeBounds, "2  DAMAGE +5", levelUpSelection == 1);
        drawPopupButton(g2, speedUpgradeBounds, "3  SPEED +1", levelUpSelection == 2);

        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        String controls = "Use W/S or arrows to choose, J/Enter to confirm";
        FontMetrics controlsFm = g2.getFontMetrics();
        int controlsX = boxX + (boxW - controlsFm.stringWidth(controls)) / 2;
        g2.setColor(new Color(230, 240, 255));
        g2.drawString(controls, controlsX, boxY + boxH - 20);
    }

    /**
     * Draws the game-over screen after the player dies.
     */
    private void drawDeathScreen(Graphics2D g2) {
        int W = getWidth();
        int H = getHeight();

        g2.setColor(new Color(0, 0, 0, 190));
        g2.fillRect(0, 0, W, H);

        int boxW = 470;
        int boxH = 240;
        int boxX = (W - boxW) / 2;
        int boxY = (H - boxH) / 2;

        g2.setColor(new Color(40, 28, 32));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 18, 18);

        g2.setColor(new Color(190, 80, 80));
        g2.setStroke(new BasicStroke(3f));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 18, 18);

        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 36));
        String title = "YOU DIED";
        FontMetrics titleFm = g2.getFontMetrics();
        int titleX = boxX + (boxW - titleFm.stringWidth(title)) / 2;
        g2.setColor(new Color(245, 215, 195));
        g2.drawString(title, titleX, boxY + 65);

        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        String subtitle = "Choose what you want to do next.";
        FontMetrics subFm = g2.getFontMetrics();
        int subX = boxX + (boxW - subFm.stringWidth(subtitle)) / 2;
        g2.drawString(subtitle, subX, boxY + 100);

        int btnW = 150;
        int btnH = 48;
        int gap = 35;
        int btnY = boxY + 135;
        int restartX = boxX + (boxW - btnW * 2 - gap) / 2;
        int menuX = restartX + btnW + gap;

        restartButtonBounds.setBounds(restartX, btnY, btnW, btnH);
        menuButtonBounds.setBounds(menuX, btnY, btnW, btnH);

        drawPopupButton(g2, restartButtonBounds, "RESTART", deathScreenSelection == 0);
        drawPopupButton(g2, menuButtonBounds, "MENU", deathScreenSelection == 1);

        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        String controls = "Use Joystick to choose, A to confirm";
        FontMetrics controlsFm = g2.getFontMetrics();
        int controlsX = boxX + (boxW - controlsFm.stringWidth(controls)) / 2;
        g2.drawString(controls, controlsX, boxY + boxH - 18);
    }

    /**
     * Draws the victory screen after surviving for 100 seconds.
     */
    private void drawWinScreen(Graphics2D g2) {
        int W = getWidth();
        int H = getHeight();

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, W, H);

        int boxW = 470;
        int boxH = 240;
        int boxX = (W - boxW) / 2;
        int boxY = (H - boxH) / 2;

        g2.setColor(new Color(32, 48, 38));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 18, 18);

        g2.setColor(new Color(120, 210, 120));
        g2.setStroke(new BasicStroke(3f));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 18, 18);

        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 36));
        String title = "YOU WIN!";
        FontMetrics titleFm = g2.getFontMetrics();
        int titleX = boxX + (boxW - titleFm.stringWidth(title)) / 2;
        g2.setColor(new Color(230, 255, 210));
        g2.drawString(title, titleX, boxY + 65);

        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        String subtitle = "You defeated the Slime King.";
        FontMetrics subFm = g2.getFontMetrics();
        int subX = boxX + (boxW - subFm.stringWidth(subtitle)) / 2;
        g2.drawString(subtitle, subX, boxY + 100);

        int btnW = 150;
        int btnH = 48;
        int gap = 35;
        int btnY = boxY + 135;
        int restartX = boxX + (boxW - btnW * 2 - gap) / 2;
        int menuX = restartX + btnW + gap;

        restartButtonBounds.setBounds(restartX, btnY, btnW, btnH);
        menuButtonBounds.setBounds(menuX, btnY, btnW, btnH);

        drawPopupButton(g2, restartButtonBounds, "RESTART", deathScreenSelection == 0);
        drawPopupButton(g2, menuButtonBounds, "MENU", deathScreenSelection == 1);

        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        String controls = "Use Joystick to choose, A to confirm";
        FontMetrics controlsFm = g2.getFontMetrics();
        int controlsX = boxX + (boxW - controlsFm.stringWidth(controls)) / 2;
        g2.drawString(controls, controlsX, boxY + boxH - 18);
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
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
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
        
=======
        repaint();
    }

    private void updateGameClock() {
        if (countSec == 1000) {
            GAMETIME++;
            countSec = 0;
        } else {
            countSec += TIMERSPEED;
        }
        FULLTIME = ((double) (GAMETIME * 1000 + countSec)) / 1000.0;
    }

    private void updateWeaponAnimation() {
        if (player.weapon.attack == true) {
            player.weapon.switchFrame();
        }
    }

    private void updateMonsters() {
        monsters = worldMap.getMonsters();
        for (Monster monster : monsters) {
            monster.move(player.x, player.y);
        }
    }

    private void checkCombatResults() {
        monsters = worldMap.getMonsters();

        player.RemoveProj();
        player.checkProjectile(monsters);
        int monsKilled = worldMap.removeDefeatedMonsters();
        if (monsKilled > 0) {
            gainExp(monsKilled);
        }
        monsters = worldMap.getMonsters();

        // Count down immunity every tick, then let each monster test collision.
        // Character.takeDamage(...) decides whether damage should actually happen,
        // so multiple monsters touching the player cannot one-shot them in one tick.
        player.countDownImmunity();
        for (Monster monster : monsters) {
            monster.checkCollision(player.x, player.y, null, player);
        }
    }

    private void gainExp(int amount) {
        player.exp += amount;
        checkForLevelUp();
    }

    private void checkForLevelUp() {
        if (!levelUpScreenOpen && player.levelUp()) {
            openLevelUpScreen();
        }
    }

    private void openLevelUpScreen() {
        levelUpScreenOpen = true;
        levelUpSelection = 0;
        stopPlayerMovement();
        repaint();
    }

    private void moveLevelUpSelection(int direction) {
        levelUpSelection += direction;
        if (levelUpSelection < 0) {
            levelUpSelection = 2;
        } else if (levelUpSelection > 2) {
            levelUpSelection = 0;
        }
        repaint();
    }

    private void confirmLevelUpSelection() {
        applyLevelUpChoice(levelUpSelection);
    }

    private void applyLevelUpChoice(int choice) {
        if (choice == 0) {
            player.maxHealth += 20;
            player.health += 20;
        } else if (choice == 1) {
            if (player.weapon != null) {
                player.weapon.damage += 5;
            }
        } else if (choice == 2) {
            player.speed += 1;
        }

        levelUpScreenOpen = false;
        requestFocusInWindow();
        repaint();

        // If the player already has enough EXP for another level, open the
        // screen again after applying this upgrade.
        checkForLevelUp();
    }

    private void checkGameEndConditions() {
        if (player.health <= 0 && !deathScreenOpen) {
            openDeathScreen();
            return;
        }

        // At 100 seconds, survival mode changes into the boss fight instead of
        // instantly winning. The player wins only after the Slime King dies.
        if (FULLTIME >= SURVIVAL_TIME_SECONDS && player.health > 0 && !bossPhaseStarted) {
            startBossPhase();
            return;
        }

        if (bossPhaseStarted
                && slimeKingBoss != null
                && slimeKingBoss.getHealth() <= 0
                && player.health > 0
                && !winScreenOpen) {
            openWinScreen();
        }
    }

    /**
     * Opens the death screen and freezes player movement.
     */
    private void openDeathScreen() {
        deathScreenOpen = true;
        deathScreenSelection = 0;
        player.health = 0;
        stopPlayerMovement();
        repaint();
    }

    /**
     * Opens the win screen and freezes player movement.
     */
    private void openWinScreen() {
        winScreenOpen = true;
        deathScreenSelection = 0;
        stopPlayerMovement();
        repaint();
    }

    /**
     * Confirms whichever death-screen or win-screen option is currently selected.
     */
    private void confirmDeathScreenSelection() {
        if (deathScreenSelection == 0) {
            restartGame();
        } else {
            returnToMainMenuFromDeathScreen();
        }
    }

    /**
     * Moves the death-screen selection between RESTART and MENU.
     */
    private void moveDeathScreenSelection(int direction) {
        if (direction == 0) {
            return;
        }

        deathScreenSelection += direction;
        if (deathScreenSelection < 0) {
            deathScreenSelection = 1;
        } else if (deathScreenSelection > 1) {
            deathScreenSelection = 0;
        }
        repaint();
    }

    /**
     * Restarts the game by asking MainClass to create a fresh GamePanel.
     */
    private void restartGame() {
        timer.stop();
        if (deathScreenListener != null) {
            deathScreenListener.onRestart();
        }
    }

    /**
     * Returns to the main menu from the death screen.
     */
    private void returnToMainMenuFromDeathScreen() {
        timer.stop();
        if (deathScreenListener != null) {
            deathScreenListener.onReturnToMenu();
        } else {
            returnToMainMenu();
        }
    }

    /**
     * Stops all held movement keys and resets the walking animation.
     */
    private void stopPlayerMovement() {
        moveUp = false;
        moveDown = false;
        moveLeft = false;
        moveRight = false;
        player.updateWalkAnimation(false);
    }

    /**
     * Opens the return-to-menu confirmation and freezes player movement.
     */
    private void openReturnDialog() {
        if (deathScreenOpen || winScreenOpen || levelUpScreenOpen) {
            return;
        }

        returnDialogOpen = true;
        returnDialogSelection = 1;
        stopPlayerMovement();
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

        boolean tryingToMove = dirX != 0 || dirY != 0;
        boolean actuallyMoved = false;

        if (tryingToMove) {
            int oldX = player.x;
            int oldY = player.y;

            // Move on X and Y separately. This lets the player slide along a
            // wall instead of getting completely stuck when moving diagonally.
            if (dirX != 0) {
                player.x += dirX * player.speed;
                worldMap.keepInsideWorld(player);
                if (!worldMap.canCharacterStand(player)) {
                    player.x = oldX;
                }
            }

            if (dirY != 0) {
                player.y += dirY * player.speed;
                worldMap.keepInsideWorld(player);
                if (!worldMap.canCharacterStand(player)) {
                    player.y = oldY;
                }
            }

            actuallyMoved = player.x != oldX || player.y != oldY;

            if (dirX < 0) {
                player.flip(false);
            } else if (dirX > 0) {
                player.flip(true);
            }
        }

        worldMap.keepInsideWorld(player);
        if(player.name.equals("Mech")){
            player.updateFlyAnimation(actuallyMoved);
        } else {
            player.updateWalkAnimation(actuallyMoved);
        }
    }

    private class MouseLis extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            requestFocusInWindow();

            if (deathScreenOpen || winScreenOpen) {
                if (restartButtonBounds.contains(e.getPoint())) {
                    deathScreenSelection = 0;
                    restartGame();
                } else if (menuButtonBounds.contains(e.getPoint())) {
                    deathScreenSelection = 1;
                    returnToMainMenuFromDeathScreen();
                }
                return;
            }

            if (levelUpScreenOpen) {
                if (healthUpgradeBounds.contains(e.getPoint())) {
                    levelUpSelection = 0;
                    applyLevelUpChoice(0);
                } else if (damageUpgradeBounds.contains(e.getPoint())) {
                    levelUpSelection = 1;
                    applyLevelUpChoice(1);
                } else if (speedUpgradeBounds.contains(e.getPoint())) {
                    levelUpSelection = 2;
                    applyLevelUpChoice(2);
                }
                return;
            }

            if (returnDialogOpen) {
                if (yesButtonBounds.contains(e.getPoint())) {
                    returnDialogSelection = 0;
                    returnToMainMenu();
                } else if (noButtonBounds.contains(e.getPoint())) {
                    returnDialogSelection = 1;
                    closeReturnDialog();
                }
                return;
            }

            if (closeButtonBounds.contains(e.getPoint())) {
                openReturnDialog();
            }
        }
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
        		}
        	}
=======
            if (levelUpScreenOpen) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W:
                    case KeyEvent.VK_UP:
                        moveLevelUpSelection(-1);
                        break;
                    case KeyEvent.VK_S:
                    case KeyEvent.VK_DOWN:
                        moveLevelUpSelection(1);
                        break;
                    case KeyEvent.VK_ENTER:
                    case KeyEvent.VK_SPACE:
                    case KeyEvent.VK_J:
                        confirmLevelUpSelection();
                        break;
                    case KeyEvent.VK_1:
                        levelUpSelection = 0;
                        applyLevelUpChoice(0);
                        break;
                    case KeyEvent.VK_2:
                        levelUpSelection = 1;
                        applyLevelUpChoice(1);
                        break;
                    case KeyEvent.VK_3:
                        levelUpSelection = 2;
                        applyLevelUpChoice(2);
                        break;
                }
                return;
            }

            if (returnDialogOpen) {
                switch (e.getKeyCode()) {
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

            switch (e.getKeyCode()) {
                case KeyEvent.VK_X:
                    openReturnDialog();
                    break;
                case KeyEvent.VK_W:
                    moveUp = true;
                    break;
                case KeyEvent.VK_A:
                    moveLeft = true;
                    break;
                case KeyEvent.VK_S:
                    moveDown = true;
                    break;
                case KeyEvent.VK_D:
                    moveRight = true;
                    break;
                case KeyEvent.VK_J:
                    monsters = worldMap.getMonsters();
                    if (player.weapon.Ready(FULLTIME)) {
                        player.Attack(monsters);
                        player.weapon.logTime(FULLTIME);
                        player.weapon.attack = true;
                    }
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
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
>>>>>>> Stashed changes
        }
    }
    
}