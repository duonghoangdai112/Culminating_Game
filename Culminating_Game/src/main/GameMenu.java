package main;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import java.net.URL;
 
/**
 * Gun Mayhem - main menu panel.
 *
 * Navigation : W / Up     = move selection up
 *              S / Down   = move selection down
 *              Enter/Space = confirm selection
 *
 * Put your image at:
 * src/assets/background.png
 *
 * Then this class loads it with:
 * /assets/background.png
 */
public class GameMenu extends JPanel {
 
    // -- Menu state -----------------------------------------------------------
    private static final String[] OPTIONS = { "Play", "Rules", "Characters" };
    private int selectedIndex = 0;
 
    // -- Background image -----------------------------------------------------
    private BufferedImage backgroundImage;
 
    // -- Glow animation -------------------------------------------------------
    private float glowPhase = 0f;          // 0 -> 2 pi, drives sin-wave pulse
    private static final float GLOW_SPEED = 0.07f;
 
    // -- Colours (pixel-art palette) -----------------------------------------
    private static final Color BG_DARK        = new Color(18,  14,  22);
    private static final Color BG_MID         = new Color(28,  22,  36);
    private static final Color STONE_DARK     = new Color(50,  46,  58);
    private static final Color STONE_MID      = new Color(68,  62,  76);
    private static final Color STONE_LIGHT    = new Color(92,  86, 100);
    private static final Color STONE_EDGE     = new Color(110,104, 118);
    private static final Color TEXT_NORMAL    = new Color(180, 170, 158);
    private static final Color TEXT_SELECTED  = new Color(255, 238, 160);
    private static final Color GLOW_CORE      = new Color(255, 220,  80);
    private static final Color GLOW_MID       = new Color(255, 160,  30);
    private static final Color GLOW_OUTER     = new Color(200,  80,   0);
 
    // -- Fonts ----------------------------------------------------------------
    private static final Font TITLE_FONT  = new Font(Font.MONOSPACED, Font.BOLD, 26);
    private static final Font BUTTON_FONT = new Font(Font.MONOSPACED, Font.BOLD, 18);
    private static final Font HINT_FONT   = new Font(Font.MONOSPACED, Font.PLAIN, 11);
 
    // -- Layout constants -----------------------------------------------------
    private static final int BTN_W    = 230;
    private static final int BTN_H    =  46;
    private static final int BTN_GAP  =  62;   // centre-to-centre row spacing
    private static final int TITLE_W  = 290;
    private static final int TITLE_H  =  58;
    private static final int GAP_TITLE_BUTTONS = 40; // space between title and first button
    private static final int GROUP_PAD = 30;   // breathing room around the whole group

    // -- Responsive layout ----------------------------------------------------
    // The title + buttons are laid out once in a fixed "design space", then the
    // whole group is centred and scaled to fit whatever size the window is.
    // This keeps every button in the exact middle (horizontally and vertically)
    // at any window size, and prevents the title from clipping off the top.
    private int designW, designH;          // size of the design canvas
    private int titleDX, titleDY;          // title top-left, in design space
    private int[] btnDX, btnDY;            // each button top-left, in design space
    private AffineTransform menuTransform = new AffineTransform(); // design -> screen
 
    // -- Selection-changed callback ------------------------------------------
    public interface SelectionListener {
        void onSelect(int index, String label);
    }
 
    private SelectionListener selectionListener;
 
    public void setSelectionListener(SelectionListener l) {
        this.selectionListener = l;
    }
 
    // -- Constructor ----------------------------------------------------------
    public GameMenu() {
        setFocusable(true);
        setBackground(BG_DARK);
        setPreferredSize(new Dimension(800, 500));

        // Build the fixed design-space layout for the title + buttons.
        computeDesignLayout();

        // Load the background image from src/assets/background.png
        backgroundImage = loadImage("bg.png");
 
        // Keyboard input
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKey(e.getKeyCode());
            }
        });

        // Mouse input for clicking the custom-drawn menu buttons.
        // The menu is drawn through a scale/translate transform, so map the
        // click back into design space before testing the button bounds.
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point2D p;
                try {
                    p = menuTransform.inverseTransform(e.getPoint(), null);
                } catch (Exception ex) {
                    return; // non-invertible transform (degenerate size) - ignore
                }
                for (int i = 0; i < OPTIONS.length; i++) {
                    if (getButtonBounds(i).contains(p)) {
                        selectedIndex = i;
                        repaint();

                        if (selectionListener != null) {
                            selectionListener.onSelect(selectedIndex, OPTIONS[selectedIndex]);
                        }
                        break;
                    }
                }
            }
        });
 
        // Glow animation loop (~60 fps)
        new Timer(16, e -> {
            glowPhase = (glowPhase + GLOW_SPEED) % (float)(Math.PI * 2);
            repaint();
        }).start();
    }
 
    // -- Image loading --------------------------------------------------------
    BufferedImage loadImage(String filename) {
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
 
    // -- Key handling ---------------------------------------------------------
    private void handleKey(int code) {
        switch (code) {
            case KeyEvent.VK_W:
                selectedIndex = (selectedIndex - 1 + OPTIONS.length) % OPTIONS.length;
                break;
            case KeyEvent.VK_S:
                selectedIndex = (selectedIndex + 1) % OPTIONS.length;
                break;
            case KeyEvent.VK_J:
                if (selectionListener != null) {
                    selectionListener.onSelect(selectedIndex, OPTIONS[selectedIndex]);
                } else {
                    System.out.println("Selected: " + OPTIONS[selectedIndex]);
                }
                break;
            case KeyEvent.VK_U:
            	System.exit(0);
        }
    }
 
    // -- Responsive layout helpers -------------------------------------------
    /**
     * Lays the title and buttons out once in a fixed "design space" whose origin
     * is the top-left of the whole menu group. computeMenuTransform() then maps
     * this design space onto the live panel, centred and scaled to fit.
     */
    private void computeDesignLayout() {
        int n = OPTIONS.length;
        int buttonsH = (n - 1) * BTN_GAP + BTN_H;
        int contentW = Math.max(TITLE_W, BTN_W);

        designW = contentW + GROUP_PAD * 2;
        designH = TITLE_H + GAP_TITLE_BUTTONS + buttonsH + GROUP_PAD * 2;

        // Title centred at the top of the group.
        titleDX = (designW - TITLE_W) / 2;
        titleDY = GROUP_PAD;

        // Button stack centred horizontally, directly under the title.
        int buttonsTop = GROUP_PAD + TITLE_H + GAP_TITLE_BUTTONS;
        btnDX = new int[n];
        btnDY = new int[n];
        for (int i = 0; i < n; i++) {
            btnDX[i] = (designW - BTN_W) / 2;
            btnDY[i] = buttonsTop + i * BTN_GAP;
        }
    }

    /**
     * Builds the design->screen transform: scale the whole group so it fits the
     * current window (with a small margin), then centre it both ways. Buttons
     * therefore stay in the exact middle at any window size, and the title never
     * clips off the top.
     */
    private AffineTransform computeMenuTransform() {
        int W = getWidth();
        int H = getHeight();

        // Fit within ~90% width and ~84% height so nothing touches the edges,
        // but allow the group to grow a little on very large windows.
        double scale = Math.min((W * 0.90) / designW, (H * 0.84) / designH);
        scale = Math.min(scale, 1.4);
        if (scale <= 0) scale = 0.01;

        double drawW = designW * scale;
        double drawH = designH * scale;

        AffineTransform at = new AffineTransform();
        at.translate((W - drawW) / 2.0, (H - drawH) / 2.0);
        at.scale(scale, scale);
        return at;
    }

    // -- Painting -------------------------------------------------------------
    @Override
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
 
        int W = getWidth();
        int H = getHeight();
 
        paintBackground(g2, W, H);

        // Centre + scale the title and buttons as one group.
        menuTransform = computeMenuTransform();
        AffineTransform saved = g2.getTransform();
        g2.transform(menuTransform);
        paintTitle(g2);
        paintButtons(g2);
        g2.setTransform(saved);

        paintHints(g2, W, H);
 
        g2.dispose();
    }
 
    // -- Background image + fallback -----------------------------------------
    private void paintBackground(Graphics2D g2, int W, int H) {
        if (backgroundImage != null) {
            // Draw the image stretched to fill the panel
            g2.drawImage(backgroundImage, 0, 0, W, H, null);
 
            // Dark overlay so the title and menu buttons stay readable
            g2.setColor(new Color(0, 0, 0, 90));
            g2.fillRect(0, 0, W, H);
 
        }
 
        //set transparency
        float opacity = 0.4f; 
        
       
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity);
        g2.setComposite(ac);
        
        // Fallback background if the image cannot be loaded
        RadialGradientPaint bg = new RadialGradientPaint(
            W / 2f, H / 2f, Math.max(W, H) * 0.6f,
            new float[]{ 0f, 1f },
            new Color[]{ BG_MID, BG_DARK }
        );
        g2.setPaint(bg);
        g2.fillRect(0, 0, W, H);
 
        // Subtle pixel-grid texture
        g2.setColor(new Color(255, 255, 255, 6));
        for (int x = 0; x < W; x += 8) {
            for (int y = 0; y < H; y += 8) {
                g2.fillRect(x, y, 1, 1);
            }
        }
 
        // Vignette
        RadialGradientPaint vignette = new RadialGradientPaint(
            W / 2f, H / 2f, Math.max(W, H) * 0.55f,
            new float[]{ 0.4f, 1f },
            new Color[]{ new Color(0, 0, 0, 0), new Color(0, 0, 0, 180) }
        );
        g2.setPaint(vignette);
        g2.fillRect(0, 0, W, H);
        
        //reset transparency
        opacity = 1f; 
        ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity);
        g2.setComposite(ac);
        
        
    }
 
    // -- Stone-plate title ----------------------------------------------------
    private void paintTitle(Graphics2D g2) {
        int tx = titleDX;
        int ty = titleDY;
 
        // Outer border shadow
        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRoundRect(tx + 3, ty + 3, TITLE_W, TITLE_H, 8, 8);
 
        // Stone plate fill
        GradientPaint stone = new GradientPaint(tx, ty, STONE_MID, tx, ty + TITLE_H, STONE_DARK);
        g2.setPaint(stone);
        g2.fillRoundRect(tx, ty, TITLE_W, TITLE_H, 8, 8);
 
        // Outer bevel highlight
        g2.setColor(STONE_EDGE);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(tx, ty, TITLE_W, TITLE_H, 8, 8);
 
        // Inner recessed border
        g2.setColor(STONE_LIGHT);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(tx + 5, ty + 5, TITLE_W - 10, TITLE_H - 10, 4, 4);
 
        // Diamond ornament top-centre
        g2.setColor(STONE_EDGE);
        drawDiamond(g2, tx + TITLE_W / 2, ty - 8, 7);
 
        // Title text
        g2.setFont(TITLE_FONT);
        FontMetrics fm = g2.getFontMetrics();
        String title = "Gun Mayhem";
        int textX = tx + (TITLE_W - fm.stringWidth(title)) / 2;
        int textY = ty + (TITLE_H + fm.getAscent() - fm.getDescent()) / 2 - 1;
 
        // Drop shadow
        g2.setColor(new Color(0, 0, 0, 200));
        g2.drawString(title, textX + 2, textY + 2);
 
        // Main text
        g2.setColor(new Color(210, 200, 180));
        g2.drawString(title, textX, textY);
    }
 
    // -- Menu buttons ---------------------------------------------------------
    private void paintButtons(Graphics2D g2) {
        for (int i = 0; i < OPTIONS.length; i++) {
            boolean active = (i == selectedIndex);
            paintButton(g2, btnDX[i], btnDY[i], OPTIONS[i], active);
        }
    }
 
    private void paintButton(Graphics2D g2, int bx, int by, String label, boolean active) {
        if (active) {
            // Pulsing glow intensity (0.4 -> 1.0)
            float pulse = 0.4f + 0.6f * (float)((Math.sin(glowPhase) + 1.0) / 2.0);
 
            // Outermost glow layer
            paintGlowLayer(g2, bx, by, 20, new Color(
                GLOW_OUTER.getRed(),
                GLOW_OUTER.getGreen(),
                GLOW_OUTER.getBlue(),
                (int)(60 * pulse)
            ));
 
            // Mid glow
            paintGlowLayer(g2, bx, by, 12, new Color(
                GLOW_MID.getRed(),
                GLOW_MID.getGreen(),
                GLOW_MID.getBlue(),
                (int)(90 * pulse)
            ));
 
            // Inner glow
            paintGlowLayer(g2, bx, by, 6, new Color(
                GLOW_CORE.getRed(),
                GLOW_CORE.getGreen(),
                GLOW_CORE.getBlue(),
                (int)(130 * pulse)
            ));
        }
 
        // Stone plate
        Color fillTop = active ? new Color(88, 80, 66) : STONE_MID;
        Color fillBot = active ? new Color(66, 60, 48) : STONE_DARK;
 
        GradientPaint stone = new GradientPaint(bx, by, fillTop, bx, by + BTN_H, fillBot);
        g2.setPaint(stone);
        g2.fillRoundRect(bx, by, BTN_W, BTN_H, 6, 6);
 
        // Bevel / border
        g2.setColor(STONE_EDGE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(bx, by, BTN_W, BTN_H, 6, 6);
 
        if (active) {
            float pulse = 0.4f + 0.6f * (float)((Math.sin(glowPhase) + 1.0) / 2.0);
            g2.setColor(new Color(
                GLOW_CORE.getRed(),
                GLOW_CORE.getGreen(),
                GLOW_CORE.getBlue(),
                (int)(200 * pulse)
            ));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(bx, by, BTN_W, BTN_H, 6, 6);
        }
 
        // Inner recessed line
        g2.setColor(new Color(
            STONE_LIGHT.getRed(),
            STONE_LIGHT.getGreen(),
            STONE_LIGHT.getBlue(),
            80
        ));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(bx + 3, by + 3, BTN_W - 6, BTN_H - 6, 3, 3);
 
        // Diamond ornaments
        Color diamondColor;
        if (active) {
            float pulse = 0.4f + 0.6f * (float)((Math.sin(glowPhase) + 1.0) / 2.0);
            diamondColor = new Color(
                GLOW_CORE.getRed(),
                GLOW_CORE.getGreen(),
                GLOW_CORE.getBlue(),
                (int)(255 * pulse)
            );
        } else {
            diamondColor = new Color(130, 122, 140);
        }
 
        g2.setColor(diamondColor);
        drawDiamond(g2, bx - 14, by + BTN_H / 2, 6);
        drawDiamond(g2, bx + BTN_W + 14, by + BTN_H / 2, 6);
 
        // Label
        g2.setFont(BUTTON_FONT);
        FontMetrics fm = g2.getFontMetrics();
        int textX = bx + (BTN_W - fm.stringWidth(label)) / 2;
        int textY = by + (BTN_H + fm.getAscent() - fm.getDescent()) / 2 - 1;
 
        // Drop shadow
        g2.setColor(new Color(0, 0, 0, 200));
        g2.drawString(label, textX + 1, textY + 1);
 
        // Text colour
        g2.setColor(active ? TEXT_SELECTED : TEXT_NORMAL);
        g2.drawString(label, textX, textY);
    }
 
    // -- Glow halo layer ------------------------------------------------------
    private void paintGlowLayer(Graphics2D g2, int bx, int by, int expand, Color c) {
        g2.setColor(c);
        g2.fillRoundRect(
            bx - expand,
            by - expand,
            BTN_W + expand * 2,
            BTN_H + expand * 2,
            10 + expand,
            10 + expand
        );
    }
 
    // -- Diamond shape --------------------------------------------------------
    private void drawDiamond(Graphics2D g2, int cx, int cy, int r) {
        int[] xs = { cx, cx + r, cx, cx - r };
        int[] ys = { cy - r, cy, cy + r, cy };
        g2.fillPolygon(xs, ys, 4);
        g2.setColor(new Color(255, 255, 255, 60));
        g2.drawPolygon(xs, ys, 4);
    }
 
    // -- Key-binding hint -----------------------------------------------------
    private void paintHints(Graphics2D g2, int W, int H) {
        g2.setFont(HINT_FONT);
        g2.setColor(new Color(120, 112, 130));
        String hint = "Joystick to navigate     X to select";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(hint, (W - fm.stringWidth(hint)) / 2, H - 18);
    }
 
    private Rectangle getButtonBounds(int index) {
        // Bounds are in design space; the mouse handler maps clicks into this
        // same space via the inverse of menuTransform before testing them.
        return new Rectangle(btnDX[index], btnDY[index], BTN_W, BTN_H);
    }
 
    // -- Getters --------------------------------------------------------------
    public int getSelectedIndex() {
        return selectedIndex;
    }
 
    public String getSelectedLabel() {
        return OPTIONS[selectedIndex];
    }
 
    // -- Demo entry point -----------------------------------------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Gun Mayhem");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
            GameMenu menu = new GameMenu();
            menu.setSelectionListener((idx, label) -> {
                JOptionPane.showMessageDialog(
                    null,
                    "Opening: " + label,
                    "Gun Mayhem",
                    JOptionPane.PLAIN_MESSAGE
                );
            });
 
            frame.add(menu);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            menu.requestFocusInWindow();
        });
    }
}