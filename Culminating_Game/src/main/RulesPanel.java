package main;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class RulesPanel extends JPanel {

    private BufferedImage backgroundImage;
    private RulesListener listener;

    // Its position is updated inside paintComponent.
    private final Rectangle backButtonBounds = new Rectangle();

    public interface RulesListener {
        void onBack();
    }

    private static final Color BG_DARK = new Color(18, 14, 22);
    private static final Color STONE_MID = new Color(68, 62, 76);
    private static final Color STONE_LIGHT = new Color(92, 86, 100);
    private static final Color STONE_EDGE = new Color(120, 112, 130);
    private static final Color TEXT_NORMAL = new Color(205, 195, 175);
    private static final Color TEXT_SELECTED = new Color(255, 238, 160);

    public RulesPanel(JFrame frame, MainClass mainClass, int windowWidth, int windowHeight) {
        setFocusable(true);
        setBackground(BG_DARK);
        setPreferredSize(new Dimension(1000, 700));

        backgroundImage = loadImage("bg.png");

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouseClick(e.getPoint());
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();

                if (key == KeyEvent.VK_X
                        || key == KeyEvent.VK_K
                        || key == KeyEvent.VK_ESCAPE) {
                    goBack();
                }
            }
        });
    }

    public void setRulesListener(RulesListener listener) {
        this.listener = listener;
    }

    private void handleMouseClick(Point point) {
        requestFocusInWindow();

        if (backButtonBounds.contains(point)) {
            goBack();
        }
    }

    private void goBack() {
        if (listener != null) {
            listener.onBack();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        drawBackground(g2, panelWidth, panelHeight);
        drawCenteredContent(g2, panelWidth, panelHeight);

        g2.dispose();
    }

    private void drawBackground(Graphics2D g2, int panelWidth, int panelHeight) {
        if (backgroundImage != null) {
            g2.drawImage(
                    backgroundImage,
                    0,
                    0,
                    panelWidth,
                    panelHeight,
                    null
            );
        } else {
            g2.setColor(BG_DARK);
            g2.fillRect(0, 0, panelWidth, panelHeight);
        }

        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRect(0, 0, panelWidth, panelHeight);
    }

    private void drawCenteredContent(Graphics2D g2, int panelWidth, int panelHeight) {
        int titleHeight = 50;
        int titleToBoxGap = 25;
        int boxToButtonGap = 25;

        int boxWidth = Math.min(1000, panelWidth - 80);
        int boxHeight = Math.min(300, Math.max(220, panelHeight - 240));

        int buttonWidth = 200;
        int buttonHeight = 50;

        int totalHeight =
                titleHeight
                + titleToBoxGap
                + boxHeight
                + boxToButtonGap
                + buttonHeight;

        // Centers the complete title, box, and button group vertically.
        int contentY = Math.max(20, (panelHeight - totalHeight) / 2);

        int titleY = contentY + 38;

        int boxX = (panelWidth - boxWidth) / 2;
        int boxY = contentY + titleHeight + titleToBoxGap;

        int buttonX = (panelWidth - buttonWidth) / 2;
        int buttonY = boxY + boxHeight + boxToButtonGap;

        backButtonBounds.setBounds(
                buttonX,
                buttonY,
                buttonWidth,
                buttonHeight
        );

        drawTitle(g2, panelWidth, titleY);
        drawRulesBox(g2, boxX, boxY, boxWidth, boxHeight);
        drawBackButton(g2);
    }

    private void drawTitle(Graphics2D g2, int panelWidth, int titleY) {
        String title = "RULES";

        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 36));
        FontMetrics metrics = g2.getFontMetrics();

        int titleX = (panelWidth - metrics.stringWidth(title)) / 2;

        g2.setColor(new Color(0, 0, 0, 200));
        g2.drawString(title, titleX + 2, titleY + 2);

        g2.setColor(TEXT_SELECTED);
        g2.drawString(title, titleX, titleY);
    }

    private void drawRulesBox(
            Graphics2D g2,
            int boxX,
            int boxY,
            int boxWidth,
            int boxHeight
    ) {
        g2.setColor(STONE_MID);
        g2.fillRoundRect(
                boxX,
                boxY,
                boxWidth,
                boxHeight,
                20,
                20
        );

        g2.setColor(STONE_EDGE);
        g2.setStroke(new BasicStroke(3f));
        g2.drawRoundRect(
                boxX,
                boxY,
                boxWidth,
                boxHeight,
                20,
                20
        );

        String[] rules = {
                "WASD - Move",
                "J - Attack",
                "X - Pause / menu",
                "Defeat enemies to gain EXP",
                "Survive, then defeat the boss"
        };

        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
        g2.setColor(TEXT_NORMAL);

        FontMetrics metrics = g2.getFontMetrics();

        int lineGap = 35;
        int textBlockHeight = (rules.length - 1) * lineGap;
        int firstLineY =
                boxY
                + (boxHeight - textBlockHeight) / 2
                + metrics.getAscent() / 2;

        for (int i = 0; i < rules.length; i++) {
            String rule = rules[i];

            // Centers each rule horizontally inside the box.
            int textX =
                    boxX
                    + (boxWidth - metrics.stringWidth(rule)) / 2;

            int textY = firstLineY + i * lineGap;

            g2.drawString(rule, textX, textY);
        }
    }

    private void drawBackButton(Graphics2D g2) {
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
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(
                backButtonBounds.x,
                backButtonBounds.y,
                backButtonBounds.width,
                backButtonBounds.height,
                15,
                15
        );

        String text = "BACK";

        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        FontMetrics metrics = g2.getFontMetrics();

        int textX =
                backButtonBounds.x
                + (backButtonBounds.width - metrics.stringWidth(text)) / 2;

        int textY =
                backButtonBounds.y
                + (backButtonBounds.height
                + metrics.getAscent()
                - metrics.getDescent()) / 2;

        g2.setColor(TEXT_SELECTED);
        g2.drawString(text, textX, textY);
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
                    System.out.println(
                            "Could not load image resource: " + resourceName
                    );
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
                    System.out.println(
                            "Could not load image file: " + fileName
                    );
                }
            }
        }

        System.out.println("Image not found: " + filename);
        return null;
    }
}
