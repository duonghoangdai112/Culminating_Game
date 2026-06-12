package main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Screen used before the game starts.
 * The player chooses a character and a weapon, then starts the game.
 */
public class LoadoutScreen extends JPanel {

    public interface LoadoutListener {
        void onStart(String characterName, String weaponName);
        void onBack();
    }

    private static class CharacterOption {
        String name;
        String description;
        String imageFile;

        CharacterOption(String name, String description, String imageFile) {
            this.name = name;
            this.description = description;
            this.imageFile = imageFile;
           
        }
    }

    private static class WeaponOption {
        String name;
        String description;
        String imageFile;

        WeaponOption(String name, String description, String imageFile) {
            this.name = name;
            this.description = description;
            this.imageFile = imageFile;
        }
    }

    private final CharacterOption[] characters = {
            new CharacterOption("Archer", "Fast ranged fighter", "Archer.png"),
            new CharacterOption("Mech", "Heavy armored fighter","Mech.png")
    };

    private final WeaponOption[] weapons = {
            new WeaponOption("Bow", "Balanced ranged weapon", "Bow-animation.png"),
            new WeaponOption("Staff", "Magic projectile weapon", "staff-animation.png"),
            new WeaponOption("Glock", "Fast short weapon", "glock-animation.png"),
            new WeaponOption("Sniper", "Slow but powerful", "Sniper-animation.png"),
            new WeaponOption("Rifle", "Steady automatic weapon", "47-animation.png")
    };

    private int selectedCharacter = 0;
    private int selectedWeapon = 0;

    // 0 = character card, 1 = weapon card, 2 = start button, 3 = back button
    private int selectedRow = 0;

    private LoadoutListener listener;
    private BufferedImage backgroundImage;
    private BufferedImage characterImage;
    private BufferedImage weaponImage;

    private Rectangle characterCardBounds = new Rectangle();
    private Rectangle weaponCardBounds = new Rectangle();
    private Rectangle characterPrevBounds = new Rectangle();
    private Rectangle characterNextBounds = new Rectangle();
    private Rectangle weaponPrevBounds = new Rectangle();
    private Rectangle weaponNextBounds = new Rectangle();
    private Rectangle startButtonBounds = new Rectangle();
    private Rectangle backButtonBounds = new Rectangle();

    private float glowPhase = 0f;

    private static final Color BG_DARK = new Color(18, 14, 22);
    private static final Color STONE_DARK = new Color(50, 46, 58);
    private static final Color STONE_MID = new Color(68, 62, 76);
    private static final Color STONE_LIGHT = new Color(92, 86, 100);
    private static final Color STONE_EDGE = new Color(120, 112, 130);
    private static final Color TEXT_NORMAL = new Color(205, 195, 175);
    private static final Color TEXT_SELECTED = new Color(255, 238, 160);
    private static final Color GLOW_CORE = new Color(255, 220, 80);

    public LoadoutScreen() {
        setFocusable(true);
        setBackground(BG_DARK);
        setPreferredSize(new Dimension(800, 500));

        backgroundImage = loadImage("bg.png");
        loadCurrentImages();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKey(e.getKeyCode());
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                handleMouse(e.getPoint());
            }
        });

        new Timer(16, e -> {
            glowPhase = (glowPhase + 0.07f) % (float)(Math.PI * 2);
            repaint();
        }).start();
    }

    public void setLoadoutListener(LoadoutListener listener) {
        this.listener = listener;
    }

    private void handleKey(int code) {
        switch (code) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                selectedRow = (selectedRow - 1 + 4) % 4;
                repaint();
                break;

            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                selectedRow = (selectedRow + 1) % 4;
                repaint();
                break;

            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                if (selectedRow == 0) {
                    changeCharacter(-1);
                } else if (selectedRow == 1) {
                    changeWeapon(-1);
                }
                break;

            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                if (selectedRow == 0) {
                    changeCharacter(1);
                } else if (selectedRow == 1) {
                    changeWeapon(1);
                }
                break;

            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_J:
                confirmSelectedRow();
                break;

            case KeyEvent.VK_ESCAPE:
            case KeyEvent.VK_X:
                goBack();
                break;
        }
    }

    private void handleMouse(Point p) {
        if (characterPrevBounds.contains(p)) {
            selectedRow = 0;
            changeCharacter(-1);
            return;
        }
        if (characterNextBounds.contains(p)) {
            selectedRow = 0;
            changeCharacter(1);
            return;
        }
        if (weaponPrevBounds.contains(p)) {
            selectedRow = 1;
            changeWeapon(-1);
            return;
        }
        if (weaponNextBounds.contains(p)) {
            selectedRow = 1;
            changeWeapon(1);
            return;
        }
        if (startButtonBounds.contains(p)) {
            selectedRow = 2;
            startGame();
            return;
        }
        if (backButtonBounds.contains(p)) {
            selectedRow = 3;
            goBack();
            return;
        }
        if (characterCardBounds.contains(p)) {
            selectedRow = 0;
            repaint();
            return;
        }
        if (weaponCardBounds.contains(p)) {
            selectedRow = 1;
            repaint();
        }
    }

    private void confirmSelectedRow() {
        if (selectedRow == 2) {
            startGame();
        } else if (selectedRow == 3) {
            goBack();
        }
    }

    private void startGame() {
        if (listener != null) {
            listener.onStart(characters[selectedCharacter].name, weapons[selectedWeapon].name);
        }
    }

    private void goBack() {
        if (listener != null) {
            listener.onBack();
        }
    }

    private void changeCharacter(int direction) {
        selectedCharacter = (selectedCharacter + direction + characters.length) % characters.length;
        loadCurrentImages();
        repaint();
    }

    private void changeWeapon(int direction) {
        selectedWeapon = (selectedWeapon + direction + weapons.length) % weapons.length;
        loadCurrentImages();
        repaint();
    }

    private void loadCurrentImages() {
        characterImage = loadImage(characters[selectedCharacter].imageFile);
        weaponImage = loadImage(weapons[selectedWeapon].imageFile);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int W = getWidth();
        int H = getHeight();

        paintBackground(g2, W, H);
        paintTitle(g2, W);
        paintCards(g2, W, H);
        paintBottomButtons(g2, W, H);
        paintHints(g2, W, H);

        g2.dispose();
    }

    private void paintBackground(Graphics2D g2, int W, int H) {
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, W, H, null);
            g2.setColor(new Color(0, 0, 0, 120));
            g2.fillRect(0, 0, W, H);
        }

        RadialGradientPaint vignette = new RadialGradientPaint(
                W / 2f, H / 2f, Math.max(W, H) * 0.6f,
                new float[]{0.2f, 1f},
                new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 190)}
        );
        g2.setPaint(vignette);
        g2.fillRect(0, 0, W, H);
    }

    private void paintTitle(Graphics2D g2, int W) {
        String title = "SELECT LOADOUT";
        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
        FontMetrics fm = g2.getFontMetrics();
        int plateW = 360;
        int plateH = 60;
        int x = (W - plateW) / 2;
        int y = 28;

        paintStonePlate(g2, x, y, plateW, plateH, false);

        int textX = x + (plateW - fm.stringWidth(title)) / 2;
        int textY = y + (plateH + fm.getAscent() - fm.getDescent()) / 2 - 1;
        g2.setColor(new Color(0, 0, 0, 220));
        g2.drawString(title, textX + 2, textY + 2);
        g2.setColor(TEXT_SELECTED);
        g2.drawString(title, textX, textY);
    }

    private void paintCards(Graphics2D g2, int W, int H) {
        int cardW = 270;
        int cardH = 285;
        int gap = 42;

        // Vertically centre the two cards in the screen. Clamp so they stay
        // clear of the title (top) and the START/BACK buttons (bottom) on
        // smaller windows, but sit in the true middle on large/maximised ones.
        int topLimit = 100;          // just below the title plate (title ends ~88)
        int bottomLimit = H - 95;    // just above the bottom buttons (buttons at H-82)
        int y = (H - cardH) / 2;
        if (y < topLimit) y = topLimit;
        if (y + cardH > bottomLimit) y = bottomLimit - cardH;

        int x1 = (W - cardW * 2 - gap) / 2;
        int x2 = x1 + cardW + gap;

        characterCardBounds.setBounds(x1, y, cardW, cardH);
        weaponCardBounds.setBounds(x2, y, cardW, cardH);

        paintLoadoutCard(g2, characterCardBounds, "CHARACTER", characters[selectedCharacter].name,
                characters[selectedCharacter].description, characterImage, selectedRow == 0, true);
        paintLoadoutCard(g2, weaponCardBounds, "WEAPON", weapons[selectedWeapon].name,
                weapons[selectedWeapon].description, weaponImage, selectedRow == 1, false);
    }

    private void paintLoadoutCard(Graphics2D g2, Rectangle r, String header, String name,
                                  String description, BufferedImage img, boolean selected, boolean isCharacter) {
        if (selected) {
            float pulse = 0.45f + 0.55f * (float)((Math.sin(glowPhase) + 1.0) / 2.0);
            g2.setColor(new Color(255, 190, 50, (int)(90 * pulse)));
            g2.fillRoundRect(r.x - 14, r.y - 14, r.width + 28, r.height + 28, 22, 22);
        }

        paintStonePlate(g2, r.x, r.y, r.width, r.height, selected);

        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 17));
        drawCenteredString(g2, header, r.x, r.y + 32, r.width, selected ? TEXT_SELECTED : TEXT_NORMAL);

        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 24));
        drawCenteredString(g2, name.toUpperCase(), r.x, r.y + 72, r.width, TEXT_SELECTED);

        Rectangle imgBox = new Rectangle(r.x + 35, r.y + 88, r.width - 70, 105);
        g2.setColor(new Color(0, 0, 0, 90));
        g2.fillRoundRect(imgBox.x, imgBox.y, imgBox.width, imgBox.height, 12, 12);
        g2.setColor(new Color(180, 170, 145, 90));
        g2.drawRoundRect(imgBox.x, imgBox.y, imgBox.width, imgBox.height, 12, 12);

        if (img != null) {
            drawImageInside(g2, img, imgBox, isCharacter);
        }

        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        drawCenteredString(g2, description, r.x, r.y + 218, r.width, TEXT_NORMAL);

        int arrowY = r.y + r.height - 45;
        int arrowW = 48;
        int arrowH = 32;
        Rectangle prev = isCharacter ? characterPrevBounds : weaponPrevBounds;
        Rectangle next = isCharacter ? characterNextBounds : weaponNextBounds;
        prev.setBounds(r.x + 55, arrowY, arrowW, arrowH);
        next.setBounds(r.x + r.width - 55 - arrowW, arrowY, arrowW, arrowH);

        paintSmallButton(g2, prev, "<", selected);
        paintSmallButton(g2, next, ">", selected);
    }

    private void paintBottomButtons(Graphics2D g2, int W, int H) {
        int buttonW = 190;
        int buttonH = 46;
        int gap = 28;
        int y = H - 82;
        int x1 = (W - buttonW * 2 - gap) / 2;
        int x2 = x1 + buttonW + gap;

        startButtonBounds.setBounds(x1, y, buttonW, buttonH);
        backButtonBounds.setBounds(x2, y, buttonW, buttonH);

        paintBigButton(g2, startButtonBounds, "START GAME", selectedRow == 2);
        paintBigButton(g2, backButtonBounds, "BACK", selectedRow == 3);
    }

    private void paintBigButton(Graphics2D g2, Rectangle r, String text, boolean selected) {
        if (selected) {
            float pulse = 0.45f + 0.55f * (float)((Math.sin(glowPhase) + 1.0) / 2.0);
            g2.setColor(new Color(255, 190, 50, (int)(100 * pulse)));
            g2.fillRoundRect(r.x - 10, r.y - 10, r.width + 20, r.height + 20, 16, 16);
        }

        paintStonePlate(g2, r.x, r.y, r.width, r.height, selected);
        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 17));
        FontMetrics fm = g2.getFontMetrics();
        int textX = r.x + (r.width - fm.stringWidth(text)) / 2;
        int textY = r.y + (r.height + fm.getAscent() - fm.getDescent()) / 2;
        g2.setColor(new Color(0, 0, 0, 220));
        g2.drawString(text, textX + 1, textY + 1);
        g2.setColor(selected ? TEXT_SELECTED : TEXT_NORMAL);
        g2.drawString(text, textX, textY);
    }

    private void paintSmallButton(Graphics2D g2, Rectangle r, String text, boolean selectedCard) {
        g2.setColor(selectedCard ? new Color(90, 78, 56) : new Color(60, 54, 68));
        g2.fillRoundRect(r.x, r.y, r.width, r.height, 8, 8);
        g2.setColor(selectedCard ? GLOW_CORE : STONE_EDGE);
        g2.drawRoundRect(r.x, r.y, r.width, r.height, 8, 8);

        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 22));
        FontMetrics fm = g2.getFontMetrics();
        int textX = r.x + (r.width - fm.stringWidth(text)) / 2;
        int textY = r.y + (r.height + fm.getAscent() - fm.getDescent()) / 2 - 1;
        g2.setColor(selectedCard ? TEXT_SELECTED : TEXT_NORMAL);
        g2.drawString(text, textX, textY);
    }

    private void paintStonePlate(Graphics2D g2, int x, int y, int w, int h, boolean selected) {
        Color top = selected ? new Color(86, 76, 58) : STONE_MID;
        Color bottom = selected ? new Color(58, 52, 42) : STONE_DARK;
        g2.setPaint(new GradientPaint(x, y, top, x, y + h, bottom));
        g2.fillRoundRect(x, y, w, h, 12, 12);

        g2.setColor(selected ? GLOW_CORE : STONE_EDGE);
        g2.setStroke(new BasicStroke(selected ? 3f : 2f));
        g2.drawRoundRect(x, y, w, h, 12, 12);

        g2.setColor(new Color(STONE_LIGHT.getRed(), STONE_LIGHT.getGreen(), STONE_LIGHT.getBlue(), 90));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(x + 5, y + 5, w - 10, h - 10, 8, 8);
    }

    private void drawCenteredString(Graphics2D g2, String text, int x, int baselineY, int width, Color color) {
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (width - fm.stringWidth(text)) / 2;
        g2.setColor(new Color(0, 0, 0, 210));
        g2.drawString(text, textX + 1, baselineY + 1);
        g2.setColor(color);
        g2.drawString(text, textX, baselineY);
    }

    private void drawImageInside(Graphics2D g2, BufferedImage img, Rectangle box, boolean isCharacter) {
        int sourceX = 0;
        int sourceY = 0;
        int sourceW = img.getWidth();
        int sourceH = img.getHeight();

        // Weapon images are animation sheets. Show only their first frame as the preview.
        if (!isCharacter) {
            int frames = getWeaponFrameCount(weapons[selectedWeapon].name);
            if (frames > 0) {
                sourceW = img.getWidth() / frames;
            }
        }

        double scale = Math.min((double)box.width / sourceW, (double)box.height / sourceH);
        int drawW = Math.max(1, (int)(sourceW * scale));
        int drawH = Math.max(1, (int)(sourceH * scale));
        int drawX = box.x + (box.width - drawW) / 2;
        int drawY = box.y + (box.height - drawH) / 2;

        g2.drawImage(img,
                drawX, drawY, drawX + drawW, drawY + drawH,
                sourceX, sourceY, sourceX + sourceW, sourceY + sourceH,
                null);
    }

    private int getWeaponFrameCount(String weaponName) {
        if (weaponName.equals("Bow")) {
            return 9;
        }
        if (weaponName.equals("Staff")) {
            return 4;
        }
        if (weaponName.equals("Glock")) {
            return 3;
        }
        if (weaponName.equals("Sniper")) {
            return 3;
        }
        if (weaponName.equals("Rifle")) {
            return 4;
        }
        return 1;
    }

    private void paintHints(Graphics2D g2, int W, int H) {
        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        g2.setColor(new Color(150, 140, 155));
        String hint = "W/S choose section     A/D change option     Enter/J select     X/Esc back";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(hint, (W - fm.stringWidth(hint)) / 2, H - 18);
    }

    private BufferedImage loadImage(String filename) {
        String[] resourceNames = {"/" + filename, "/assests/" + filename};
        for (String resourceName : resourceNames) {
            URL url = getClass().getResource(resourceName);
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
        return null;
    }
}