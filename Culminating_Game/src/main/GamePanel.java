package main;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import absFrame.Monster;
import sprite.Archer;
import sprite.Zombie;
import sprite.WolfMonster;
import sprite.SlimeKingBoss;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    private Timer timer;

    // Lets MainClass decide what happens when the player confirms returning to menu.
    public interface ReturnToMenuListener {
        void onReturnToMenu();
    }

    private ReturnToMenuListener returnToMenuListener;

    public void setReturnToMenuListener(ReturnToMenuListener listener) {
        this.returnToMenuListener = listener;
    }

    // Lets MainClass decide what happens when the player chooses from the death screen.
    public interface DeathScreenListener {
        void onRestart();
        void onReturnToMenu();
    }

    private DeathScreenListener deathScreenListener;

    public void setDeathScreenListener(DeathScreenListener listener) {
        this.deathScreenListener = listener;
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

    // Death screen state.
    private boolean deathScreenOpen = false;
    private boolean winScreenOpen = false;
    private Rectangle restartButtonBounds = new Rectangle();
    private Rectangle menuButtonBounds = new Rectangle();
    // 0 = RESTART, 1 = MENU.
    private int deathScreenSelection = 0;

    // Level-up screen state.
    private boolean levelUpScreenOpen = false;
    private int levelUpSelection = 0;
    private Rectangle healthUpgradeBounds = new Rectangle();
    private Rectangle damageUpgradeBounds = new Rectangle();
    private Rectangle speedUpgradeBounds = new Rectangle();

    // Survival / wave settings.
    private static final int SURVIVAL_TIME_SECONDS = 30;
    private static final double MONSTER_SPAWN_INTERVAL_SECONDS = 2.0;
    private static final int MAX_ALIVE_MONSTERS = 35;
    private double lastMonsterSpawnTime = 0.0;
    private Random spawnRandom = new Random();
    private HashMap<String, Integer> monsterStats;
    private BufferedImage zombieWalkSheet;
    private BufferedImage wolfWalkSheet;
    private BufferedImage wolfDashEffect;
    private BufferedImage slimeKingSheet;
    private BufferedImage slimeProjectileImage;
    private SlimeKingBoss slimeKingBoss;
    private boolean bossPhaseStarted = false;

    // Time record variable.
    private int TIMERSPEED = 10;
    private int GAMETIME = 0;
    private int countSec = 0;
    private double FULLTIME = 0;

    // Panel width and height.
    private int width = 1000;
    private int height = 1000;

    private Archer archer = new Archer(100, 5, 100, 5, 10, 10, "Archer");
    private WorldMap worldMap = new WorldMap();
    private Camera camera = new Camera();
    private ArrayList<Monster> monsters = worldMap.getMonsters();


    public GamePanel(HashMap<String, Integer> hashMap, String characterName, String weaponName) {
        setPreferredSize(new Dimension(width, height));
        addKeyListener(new KeyLis());
        addMouseListener(new MouseLis());
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        setupPlayerLoadout(characterName, weaponName);
        worldMap.placeCharacterInArea(archer, "room1", 0.45, 0.50);
        setupWorldMonsters(hashMap);

        timer = new Timer(TIMERSPEED, this);
        timer.start();
        timer.setInitialDelay(10);
    }

    /**
     * Sets up the chosen character and chosen weapon.
     * Right now Archer is the only character, but this keeps the game ready for more later.
     */
    private void setupPlayerLoadout(String characterName, String weaponName) {
        // Archer_animation.png is the gameplay sprite sheet.
        // Frame 0 is the idle/resting image, and the other frames create walking movement.
        archer.setWalkAnimation(loadImage("Archer_animation.png"), 5);
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
                archer.weaponInit(12, 5, 5, 0.7, 10, "Staff",
                        loadImage("staff-animation.png"), 4, 0.4, loadImage("magic.png"),2.0);
                break;

            case "Glock":
                archer.weaponInit(8, 8, 8, 0.25, 8, "Glock",
                        loadImage("glock-animation.png"), 3, 0.25, loadImage("Bullet.png"),1.0);
                break;

            case "Sniper":
                archer.weaponInit(16, 12, 12, 0.8, 25, "Sniper",
                        loadImage("Sniper-animation.png"), 3, 0.45, loadImage("Bullet.png"),1.1);
                break;

            case "Rifle":
                archer.weaponInit(10, 8, 8, 0.3, 10, "Rifle",
                        loadImage("47-animation.png"), 4, 0.30, loadImage("Bullet.png"),0.8);
                break;

            case "Bow":
            default:
                archer.weaponInit(10, 6, 6, 0.50, 10, "Bow",
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
        } else {
            WolfMonster wolf = new WolfMonster(monsterStats, 0, 100, 100, 0, 0, 0.25);
            wolf.setWalkAnimation(wolfWalkSheet, 4);
            wolf.setDashEffectImage(wolfDashEffect);
            monster = wolf;
        }

        placeMonsterRandomlyInRoom(roomName, monster, spawnRandom);
        worldMap.addMonster(monster);
    }

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
        int playerCenterX = archer.x + archer.width / 2;
        int playerCenterY = archer.y + archer.height / 2;
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

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int viewW = getViewWidth();
        int viewH = getViewHeight();
        updateCamera(viewW, viewH);

        // Draw the game world through the camera. Everything inside worldG uses
        // world coordinates; the camera translation decides where it appears on screen.
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, viewW, viewH);

        Graphics2D worldG = (Graphics2D) g2.create();
        worldG.scale(camera.getZoom(), camera.getZoom());
        worldG.translate(-camera.getX(), -camera.getY());

        worldMap.draw(worldG);

        worldG.drawImage(archer.getCurrentImage(), (int) archer.getX(), archer.y,
                (int) archer.getWidth(), archer.height, null);
        archer.weapon.draw(worldG, archer);

        monsters = worldMap.getMonsters();
        for (Monster monster : monsters) {
            monster.drawMonster(worldG);
        }
        worldG.dispose();

        // UI is drawn after the camera so it stays fixed to the screen.
        archer.drawCharacter(g2);
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
        camera.follow(archer, viewW, viewH, worldMap.getWorldWidth(), worldMap.getWorldHeight());
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
        String label = "LV " + archer.level + "  EXP " + archer.exp + "/" + archer.expToNextLevel;

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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (returnDialogOpen || deathScreenOpen || winScreenOpen || levelUpScreenOpen) {
            // Pause the game updates while a popup screen is open.
            return;
        }

        updateGameClock();
        updateMonsterSpawning();
        updatePlayerMovement();
        updateWeaponAnimation();
        updateMonsters();
        checkCombatResults();
        checkGameEndConditions();

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
        if (archer.weapon.attack == true) {
            archer.weapon.switchFrame();
        }
    }

    private void updateMonsters() {
        monsters = worldMap.getMonsters();
        for (Monster monster : monsters) {
            monster.move(archer.x, archer.y);
        }
    }

    private void checkCombatResults() {
        monsters = worldMap.getMonsters();

        archer.RemoveProj();
        archer.checkProjectile(monsters);
        int monsKilled = worldMap.removeDefeatedMonsters();
        if (monsKilled > 0) {
            gainExp(monsKilled);
        }
        monsters = worldMap.getMonsters();

        // Count down immunity every tick, then let each monster test collision.
        // Character.takeDamage(...) decides whether damage should actually happen,
        // so multiple monsters touching the player cannot one-shot them in one tick.
        archer.countDownImmunity();
        for (Monster monster : monsters) {
            monster.checkCollision(archer.x, archer.y, null, archer);
        }
    }

    private void gainExp(int amount) {
        archer.exp += amount;
        checkForLevelUp();
    }

    private void checkForLevelUp() {
        if (!levelUpScreenOpen && archer.levelUp()) {
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
            archer.maxHealth += 20;
            archer.health += 20;
        } else if (choice == 1) {
            if (archer.weapon != null) {
                archer.weapon.damage += 5;
            }
        } else if (choice == 2) {
            archer.speed += 1;
        }

        levelUpScreenOpen = false;
        requestFocusInWindow();
        repaint();

        // If the player already has enough EXP for another level, open the
        // screen again after applying this upgrade.
        checkForLevelUp();
    }

    private void checkGameEndConditions() {
        if (archer.health <= 0 && !deathScreenOpen) {
            openDeathScreen();
            return;
        }

        // At 100 seconds, survival mode changes into the boss fight instead of
        // instantly winning. The player wins only after the Slime King dies.
        if (FULLTIME >= SURVIVAL_TIME_SECONDS && archer.health > 0 && !bossPhaseStarted) {
            startBossPhase();
            return;
        }

        if (bossPhaseStarted
                && slimeKingBoss != null
                && slimeKingBoss.getHealth() <= 0
                && archer.health > 0
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
        archer.health = 0;
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
        archer.updateWalkAnimation(false);
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
            int oldX = archer.x;
            int oldY = archer.y;

            // Move on X and Y separately. This lets the player slide along a
            // wall instead of getting completely stuck when moving diagonally.
            if (dirX != 0) {
                archer.x += dirX * archer.speed;
                worldMap.keepInsideWorld(archer);
                if (!worldMap.canCharacterStand(archer)) {
                    archer.x = oldX;
                }
            }

            if (dirY != 0) {
                archer.y += dirY * archer.speed;
                worldMap.keepInsideWorld(archer);
                if (!worldMap.canCharacterStand(archer)) {
                    archer.y = oldY;
                }
            }

            actuallyMoved = archer.x != oldX || archer.y != oldY;

            if (dirX < 0) {
                archer.flip(false);
            } else if (dirX > 0) {
                archer.flip(true);
            }
        }

        worldMap.keepInsideWorld(archer);
        archer.updateWalkAnimation(actuallyMoved);
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
    }

    private class KeyLis extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (deathScreenOpen || winScreenOpen) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A:
                    case KeyEvent.VK_W:
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_UP:
                        moveDeathScreenSelection(-1);
                        break;
                    case KeyEvent.VK_D:
                    case KeyEvent.VK_S:
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_DOWN:
                        moveDeathScreenSelection(1);
                        break;
                    case KeyEvent.VK_ENTER:
                    case KeyEvent.VK_SPACE:
                    case KeyEvent.VK_J:
                        confirmDeathScreenSelection();
                        break;
                    case KeyEvent.VK_R:
                        deathScreenSelection = 0;
                        restartGame();
                        break;
                    case KeyEvent.VK_M:
                    case KeyEvent.VK_ESCAPE:
                    case KeyEvent.VK_X:
                        deathScreenSelection = 1;
                        returnToMainMenuFromDeathScreen();
                        break;
                }
                return;
            }

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
                    if (archer.weapon.Ready(FULLTIME)) {
                        archer.Attack(monsters);
                        archer.weapon.logTime(FULLTIME);
                        archer.weapon.attack = true;
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
        }
    }
}
