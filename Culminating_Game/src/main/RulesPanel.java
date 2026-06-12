package main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class RulesPanel extends JPanel {

    BufferedImage backgroundImage;

    private RulesListener listener;

    private Rectangle backButtonBounds =
            new Rectangle(300, 380, 200, 50);

    public interface RulesListener {
        void onBack();
    }

    private static final Color BG_DARK = new Color(18, 14, 22);
    private static final Color STONE_DARK = new Color(50, 46, 58);
    private static final Color STONE_MID = new Color(68, 62, 76);
    private static final Color STONE_LIGHT = new Color(92, 86, 100);
    private static final Color STONE_EDGE = new Color(120, 112, 130);
    private static final Color TEXT_NORMAL = new Color(205, 195, 175);
    private static final Color TEXT_SELECTED = new Color(255, 238, 160);

    public RulesPanel(JFrame frame, MainClass m) {

        setFocusable(true);
        setBackground(BG_DARK);
        setPreferredSize(new Dimension(800, 500));

        backgroundImage = loadImage("bg.png");

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                if (backButtonBounds.contains(e.getPoint())) {

                    if (listener != null) {
                        listener.onBack();
                    }
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_X || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    if (listener != null) {
                        listener.onBack();
                    }
                }
            }
        });
    }

    public void setRulesListener(RulesListener listener) {
        this.listener = listener;
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        int W = getWidth();
        int H = getHeight();

        // Background
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, W, H, null);
        }

        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRect(0, 0, W, H);

        // Title
        g2.setFont(new Font("Monospaced", Font.BOLD, 36));
        g2.setColor(TEXT_SELECTED);

        String title = "RULES";

        FontMetrics fm = g2.getFontMetrics();

        g2.drawString(
                title,
                (W - fm.stringWidth(title)) / 2,
                80
        );

        // Rules Box
        int boxX = 150;
        int boxY = 120;
        int boxW = 500;
        int boxH = 220;

        g2.setColor(STONE_MID);
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 20, 20);

        g2.setColor(STONE_EDGE);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 20, 20);

        // Rules Text
        g2.setFont(new Font("Monospaced", Font.BOLD, 18));
        g2.setColor(TEXT_NORMAL);

        int textX = boxX + 30;
        int y = boxY + 50;

        g2.drawString("WASD - Move", textX, y);
        y += 35;

        g2.drawString("J - Attack", textX, y);
        y += 35;

        g2.drawString("X - Pause / menu", textX, y);
        y += 35;

        g2.drawString("Defeat enemies to gain EXP", textX, y);
        y += 35;

        g2.drawString("Survive, then defeat the boss", textX, y);

        // Back Button
        g2.setColor(STONE_LIGHT);
        g2.fillRoundRect(
                backButtonBounds.x,
                backButtonBounds.y,
                backButtonBounds.width,
                backButtonBounds.height,
                15,
                15
        );

        g2.setColor(STONE_EDGE);
        g2.drawRoundRect(
                backButtonBounds.x,
                backButtonBounds.y,
                backButtonBounds.width,
                backButtonBounds.height,
                15,
                15
        );

        g2.setColor(TEXT_SELECTED);
        g2.setFont(new Font("Monospaced", Font.BOLD, 20));

        g2.drawString(
                "BACK",
                backButtonBounds.x + 65,
                backButtonBounds.y + 32
        );
    }

    private BufferedImage loadImage(String filename) {

        String[] resourceNames = {
                "/" + filename,
                "/assests/" + filename
        };

        for (String resourceName : resourceNames) {

            URL url = getClass().getResource(resourceName);

            if (url != null) {

                try {
                    return ImageIO.read(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String[] fileNames = {
                filename,
                "assests/" + filename
        };

        for (String fileName : fileNames) {

            File file = new File(fileName);

            if (file.exists()) {

                try {
                    return ImageIO.read(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}