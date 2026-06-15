package main;

import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.Dimension;
import java.awt.Toolkit;

import absFrame.Character;
import absFrame.Monster;
import sprite.Archer;
import sprite.Mech;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class GamePanel extends JPanel implements ActionListener {
    private Timer timer;

    public interface ReturnToMenuListener {
        void onReturnToMenu();
    }

    private ReturnToMenuListener returnToMenuListener;

    public void setReturnToMenuListener(ReturnToMenuListener listener) {
        this.returnToMenuListener = listener;
    }

    public interface DeathScreenListener {
        void onRestart();
        void onReturnToMenu();
    }

    private DeathScreenListener deathScreenListener;

    public void setDeathScreenListener(DeathScreenListener listener) {
        this.deathScreenListener = listener;
    }

    private boolean moveUp = false;
    private boolean moveDown = false;
    private boolean moveLeft = false;
    private boolean moveRight = false;

    private boolean returnDialogOpen = false;
    private Rectangle closeButtonBounds = new Rectangle();
    private Rectangle yesButtonBounds = new Rectangle();
    private Rectangle noButtonBounds = new Rectangle();
    private int returnDialogSelection = 1;

    private boolean deathScreenOpen = false;
    private boolean winScreenOpen = false;
    private Rectangle restartButtonBounds = new Rectangle();
    private Rectangle menuButtonBounds = new Rectangle();
    private int deathScreenSelection = 0;

    private boolean levelUpScreenOpen = false;
    private int levelUpSelection = 0;
    private Rectangle healthUpgradeBounds = new Rectangle();
    private Rectangle damageUpgradeBounds = new Rectangle();
    private Rectangle speedUpgradeBounds = new Rectangle();

    private static final int SURVIVAL_TIME_SECONDS = 100;

    private final int TIMERSPEED = 10;
    private double FULLTIME = 0;
    
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int width = screenSize.width;
    int height = screenSize.height;

    private Character archer;
    private WorldMap worldMap = new WorldMap();
    private Camera camera = new Camera();
    private ArrayList<Monster> monsters = worldMap.getMonsters();
    private EnemySpawner enemySpawner;

    private MusicPlayer musicPlayer = new MusicPlayer();


    public GamePanel(HashMap<String, Integer> hashMap, String characterName, String weaponName) {
        setPreferredSize(new Dimension(width, height));
        addKeyListener(new KeyLis());
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        setupPlayerLoadout(characterName, weaponName);
        worldMap.placeCharacterInArea(archer, "room1", 0.45, 0.50);

        enemySpawner = new EnemySpawner(worldMap, archer, hashMap, getClass(), SURVIVAL_TIME_SECONDS);
        enemySpawner.setupWorldMonsters();
        monsters = worldMap.getMonsters();

        timer = new Timer(TIMERSPEED, this);
        timer.start();
        timer.setInitialDelay(10);

        musicPlayer.playLoop("src/assests/pixel_dungeon_soundtrack.wav");
    }

    private void setupPlayerLoadout(String characterName, String weaponName) {
        if (characterName == null) {
            characterName = "Archer";
        }

        if (characterName.equals("Mech")) {
            // The Mech reuses the same player controls and weapon system,
            // but has different stats and a different animation sheet.
            archer = new Mech(160, 5, 100, 4, 10, 10, "Mech");
            archer.setWalkAnimation(ImageLoader.loadImage(getClass(), "Mech_animation.png"), 5);
        } else {
            archer = new Archer(100, 5, 100, 5, 10, 10, "Archer");
            archer.setWalkAnimation(ImageLoader.loadImage(getClass(), "Archer_animation.png"), 5);
        }

        setupWeapon(weaponName);
    }

    private void setupWeapon(String weaponName) {
        if (weaponName == null) {
            weaponName = "Bow";
        }

        switch (weaponName) {
            case "Staff":
                archer.weaponInit(12, 5, 5, 0.7, 10, "Staff",
                        ImageLoader.loadImage(getClass(), "staff-animation.png"), 4, 0.4,
                        ImageLoader.loadImage(getClass(), "magic.png"), 0.5);
                break;

            case "Glock":
                archer.weaponInit(8, 8, 8, 0.25, 8, "Glock",
                        ImageLoader.loadImage(getClass(), "glock-animation.png"), 3, 0.25,
                        ImageLoader.loadImage(getClass(), "Bullet.png"), 1.0);
                break;

            case "Sniper":
                archer.weaponInit(16, 12, 12, 0.8, 25, "Sniper",
                        ImageLoader.loadImage(getClass(), "Sniper-animation.png"), 3, 0.45,
                        ImageLoader.loadImage(getClass(), "Bullet.png"), 1.1);
                break;

            case "Rifle":
                archer.weaponInit(10, 8, 8, 0.3, 10, "Rifle",
                        ImageLoader.loadImage(getClass(), "47-animation.png"), 4, 0.30,
                        ImageLoader.loadImage(getClass(), "Bullet.png"), 0.8);
                break;

            case "Bow":
            default:
                archer.weaponInit(10, 6, 6, 0.50, 10, "Bow",
                        ImageLoader.loadImage(getClass(), "Bow-animation.png"), 9, -0.70,
                        ImageLoader.loadImage(getClass(), "Arrow.png"), -3);
                break;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int viewW = getViewWidth();
        int viewH = getViewHeight();
        updateCamera(viewW, viewH);

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

        archer.drawCharacter(g2);
        GameHUD.drawSurvivalTimer(g2, FULLTIME, SURVIVAL_TIME_SECONDS, enemySpawner.isBossPhaseStarted());
        GameHUD.drawPlayerLevel(g2, archer);
        drawPopups(g2);
    }

    private void drawPopups(Graphics2D g2) {
        if (!deathScreenOpen && !winScreenOpen) {
            PopUp.drawCloseButton(g2, getWidth(), closeButtonBounds);
        }

        if (winScreenOpen) {
            PopUp.drawWinScreen(g2, getWidth(), getHeight(), deathScreenSelection,
                    restartButtonBounds, menuButtonBounds);
        } else if (deathScreenOpen) {
            PopUp.drawDeathScreen(g2, getWidth(), getHeight(), deathScreenSelection,
                    restartButtonBounds, menuButtonBounds);
        } else if (levelUpScreenOpen) {
            PopUp.drawLevelUpScreen(g2, getWidth(), getHeight(), levelUpSelection,
                    healthUpgradeBounds, damageUpgradeBounds, speedUpgradeBounds);
        } else if (returnDialogOpen) {
            PopUp.drawReturnDialog(g2, getWidth(), getHeight(), returnDialogSelection,
                    yesButtonBounds, noButtonBounds);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (returnDialogOpen || deathScreenOpen || winScreenOpen || levelUpScreenOpen) {
            return;
        }

        updateGameClock();
        enemySpawner.update(FULLTIME);
        updatePlayerMovement();
        updateWeaponAnimation();
        updateMonsters();
        checkCombatResults();
        checkGameEndConditions();

        repaint();
    }

    private void updateGameClock() {
        FULLTIME += TIMERSPEED / 1000.0;
    }

    private void updateWeaponAnimation() {
        if (archer.weapon.attack) {
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
        checkForLevelUp();
    }

    private void checkGameEndConditions() {
        if (archer.health <= 0 && !deathScreenOpen) {
            openDeathScreen();
            return;
        }

        if (enemySpawner.isBossDefeated() && archer.health > 0 && !winScreenOpen) {
            openWinScreen();
        }
    }

    private void openDeathScreen() {
        deathScreenOpen = true;
        deathScreenSelection = 0;
        archer.health = 0;
        stopPlayerMovement();
        repaint();
    }

    private void openWinScreen() {
        winScreenOpen = true;
        deathScreenSelection = 0;
        stopPlayerMovement();
        repaint();
    }

    private void confirmDeathScreenSelection() {
        if (deathScreenSelection == 0) {
            restartGame();
        } else {
            returnToMainMenuFromDeathScreen();
        }
    }

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

    private void restartGame() {
        timer.stop();
        if (deathScreenListener != null) {
            deathScreenListener.onRestart();
        }
    }

    private void returnToMainMenuFromDeathScreen() {
        timer.stop();
        if (deathScreenListener != null) {
            deathScreenListener.onReturnToMenu();
        } else {
            returnToMainMenu();
        }
    }

    private void stopPlayerMovement() {
        moveUp = false;
        moveDown = false;
        moveLeft = false;
        moveRight = false;
        archer.updateWalkAnimation(false);
    }

    private void openReturnDialog() {
        if (deathScreenOpen || winScreenOpen || levelUpScreenOpen) {
            return;
        }

        returnDialogOpen = true;
        returnDialogSelection = 1;
        stopPlayerMovement();
        repaint();
    }

    private void closeReturnDialog() {
        returnDialogOpen = false;
        requestFocusInWindow();
        repaint();
    }

    private void returnToMainMenu() {
        timer.stop();
        if (returnToMenuListener != null) {
            returnToMenuListener.onReturnToMenu();
        }
    }

    private void confirmReturnDialogSelection() {
        if (returnDialogSelection == 0) {
            returnToMainMenu();
        } else {
            closeReturnDialog();
        }
    }

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

   
    private class KeyLis extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (deathScreenOpen || winScreenOpen) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_UP:
                        moveDeathScreenSelection(-1);
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_DOWN:
                        moveDeathScreenSelection(1);
                        break;
                    
                    case KeyEvent.VK_K:
                        confirmDeathScreenSelection();
                        break;
                   
                   
                    case KeyEvent.VK_X:
                        deathScreenSelection = 1;
                        returnToMainMenuFromDeathScreen();
                        break;
                }
                return;
            }

            if (levelUpScreenOpen) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        moveLevelUpSelection(-1);
                        break;
                    case KeyEvent.VK_DOWN:
                        moveLevelUpSelection(1);
                        break;
                    case KeyEvent.VK_K:
                        confirmLevelUpSelection();
                        break;
                   
                }
                return;
            }

            if (returnDialogOpen) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_UP:
                        moveReturnDialogSelection(-1);
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_DOWN:
                        moveReturnDialogSelection(1);
                        break;
                    case KeyEvent.VK_K:
                        confirmReturnDialogSelection();
                        break;
                    case KeyEvent.VK_Y:
                        returnDialogSelection = 0;
                        returnToMainMenu();
                        break;
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
                case KeyEvent.VK_UP:
                    moveUp = true;
                    break;
                case KeyEvent.VK_LEFT:
                    moveLeft = true;
                    break;
                case KeyEvent.VK_DOWN:
                    moveDown = true;
                    break;
                case KeyEvent.VK_RIGHT:
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
                case KeyEvent.VK_UP:
                    moveUp = false;
                    break;
                    
                case KeyEvent.VK_LEFT:
                    moveLeft = false;
                    break;
                case KeyEvent.VK_DOWN:
                    moveDown = false;
                    break;
                case KeyEvent.VK_RIGHT:
                    moveRight = false;
                    break;
            }
        }
    }
}
