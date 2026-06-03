package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;



public class Menu extends JPanel {


    private static final String[] OPTIONS = { "Play", "Rules", "Characters" };
    private int selectedIndex = 0;

    private float glowPhase = 0f;          // 0 → 2π, drives sin-wave pulse
    private static final float GLOW_SPEED = 0.07f;

    //colours
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

    //font
    private static final Font TITLE_FONT  = new Font(Font.MONOSPACED, Font.BOLD, 26);
    private static final Font BUTTON_FONT = new Font(Font.MONOSPACED, Font.BOLD, 18);
    private static final Font HINT_FONT   = new Font(Font.MONOSPACED, Font.PLAIN, 11);


    private static final int BTN_W    = 230;
    private static final int BTN_H    =  46;
    private static final int BTN_GAP  =  62;   // centre-to-centre row spacing
    private static final int TITLE_W  = 290;
    private static final int TITLE_H  =  58;

    public interface SelectionListener {
        void onSelect(int index, String label);
    }
    private SelectionListener selectionListener;

    public void setSelectionListener(SelectionListener l) {
        this.selectionListener = l;
    }

    public Menu() {
        setFocusable(true);
        setBackground(BG_DARK);
        setPreferredSize(new Dimension(800, 500));

        // Keyboard input
        addKeyListener(new KeyAdapter() {
            
            public void keyPressed(KeyEvent e) {
                handleKey(e.getKeyCode());
            }
        });

        new Timer(16, e -> {
            glowPhase = (glowPhase + GLOW_SPEED) % (float)(Math.PI * 2);
            repaint();
        }).start();
    }

    private void handleKey(int code) {
        switch (code) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                selectedIndex = (selectedIndex - 1 + OPTIONS.length) % OPTIONS.length;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                selectedIndex = (selectedIndex + 1) % OPTIONS.length;
                break;
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_SPACE:
                if (selectionListener != null) {
                    selectionListener.onSelect(selectedIndex, OPTIONS[selectedIndex]);
                } else {
                    System.out.println("Selected: " + OPTIONS[selectedIndex]);
                }
                break;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int W = getWidth();
        int H = getHeight();

        paintBackground(g2, W, H);
        paintTitle(g2, W, H);
        paintButtons(g2, W, H);
        paintHints(g2, W, H);

        g2.dispose();
    }

    private void paintBackground(Graphics2D g2, int W, int H) {
        RadialGradientPaint bg = new RadialGradientPaint(
            W / 2f, H / 2f, Math.max(W, H) * 0.6f,
            new float[]{ 0f, 1f },
            new Color[]{ BG_MID, BG_DARK }
        );
        g2.setPaint(bg);
        g2.fillRect(0, 0, W, H);

        g2.setColor(new Color(255, 255, 255, 6));
        for (int x = 0; x < W; x += 8) {
            for (int y = 0; y < H; y += 8) {
                g2.fillRect(x, y, 1, 1);
            }
        }

        RadialGradientPaint vignette = new RadialGradientPaint(
            W / 2f, H / 2f, Math.max(W, H) * 0.55f,
            new float[]{ 0.4f, 1f },
            new Color[]{ new Color(0,0,0,0), new Color(0,0,0,180) }
        );
        g2.setPaint(vignette);
        g2.fillRect(0, 0, W, H);
    }

    private void paintTitle(Graphics2D g2, int W, int H) {
        int tx = (W - TITLE_W) / 2;
        int ty = H / 2 - BTN_H / 2 - BTN_GAP * OPTIONS.length / 2 - TITLE_H - 30;

        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRoundRect(tx + 3, ty + 3, TITLE_W, TITLE_H, 8, 8);

        GradientPaint stone = new GradientPaint(tx, ty, STONE_MID, tx, ty + TITLE_H, STONE_DARK);
        g2.setPaint(stone);
        g2.fillRoundRect(tx, ty, TITLE_W, TITLE_H, 8, 8);

        g2.setColor(STONE_EDGE);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(tx, ty, TITLE_W, TITLE_H, 8, 8);

        g2.setColor(STONE_LIGHT);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(tx + 5, ty + 5, TITLE_W - 10, TITLE_H - 10, 4, 4);

        g2.setColor(STONE_EDGE);
        drawDiamond(g2, tx + TITLE_W / 2, ty - 8, 7);

        g2.setFont(TITLE_FONT);
        FontMetrics fm = g2.getFontMetrics();
        String title = "Gun Mayhem";
        int textX = tx + (TITLE_W - fm.stringWidth(title)) / 2;
        int textY = ty + (TITLE_H + fm.getAscent() - fm.getDescent()) / 2 - 1;

        g2.setColor(new Color(0, 0, 0, 200));
        g2.drawString(title, textX + 2, textY + 2);

        g2.setColor(new Color(210, 200, 180));
        g2.drawString(title, textX, textY);
    }

    private void paintButtons(Graphics2D g2, int W, int H) {
        int totalH = (OPTIONS.length - 1) * BTN_GAP + BTN_H;
        int startY  = (H - totalH) / 2 + 30;   // slight downward offset for the title
        int bx      = (W - BTN_W) / 2;

        for (int i = 0; i < OPTIONS.length; i++) {
            int by = startY + i * BTN_GAP;
            boolean active = (i == selectedIndex);
            paintButton(g2, bx, by, OPTIONS[i], active);
        }
    }

    private void paintButton(Graphics2D g2, int bx, int by, String label, boolean active) {
        if (active) {
            float pulse = 0.4f + 0.6f * (float)((Math.sin(glowPhase) + 1.0) / 2.0);

            paintGlowLayer(g2, bx, by, 20, new Color(GLOW_OUTER.getRed(),
                GLOW_OUTER.getGreen(), GLOW_OUTER.getBlue(), (int)(60 * pulse)));
            paintGlowLayer(g2, bx, by, 12, new Color(GLOW_MID.getRed(),
                GLOW_MID.getGreen(), GLOW_MID.getBlue(), (int)(90 * pulse)));
            paintGlowLayer(g2, bx, by,  6, new Color(GLOW_CORE.getRed(),
                GLOW_CORE.getGreen(), GLOW_CORE.getBlue(), (int)(130 * pulse)));
        }

        Color fillTop = active ? new Color(88, 80, 66) : STONE_MID;
        Color fillBot = active ? new Color(66, 60, 48) : STONE_DARK;

        GradientPaint stone = new GradientPaint(bx, by, fillTop, bx, by + BTN_H, fillBot);
        g2.setPaint(stone);
        g2.fillRoundRect(bx, by, BTN_W, BTN_H, 6, 6);

        g2.setColor(STONE_EDGE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(bx, by, BTN_W, BTN_H, 6, 6);

        if (active) {
            float pulse = 0.4f + 0.6f * (float)((Math.sin(glowPhase) + 1.0) / 2.0);
            g2.setColor(new Color(GLOW_CORE.getRed(), GLOW_CORE.getGreen(),
                GLOW_CORE.getBlue(), (int)(200 * pulse)));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(bx, by, BTN_W, BTN_H, 6, 6);
        }

        g2.setColor(new Color(STONE_LIGHT.getRed(), STONE_LIGHT.getGreen(),
            STONE_LIGHT.getBlue(), 80));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(bx + 3, by + 3, BTN_W - 6, BTN_H - 6, 3, 3);

        Color diamondColor;
        if (active) {
            float pulse = 0.4f + 0.6f * (float)((Math.sin(glowPhase) + 1.0) / 2.0);
            diamondColor = new Color(GLOW_CORE.getRed(), GLOW_CORE.getGreen(),
                GLOW_CORE.getBlue(), (int)(255 * pulse));
        } else {
            diamondColor = new Color(130, 122, 140);
        }
        g2.setColor(diamondColor);
        drawDiamond(g2, bx - 14, by + BTN_H / 2, 6);
        drawDiamond(g2, bx + BTN_W + 14, by + BTN_H / 2, 6);

        g2.setFont(BUTTON_FONT);
        FontMetrics fm = g2.getFontMetrics();
        int textX = bx + (BTN_W - fm.stringWidth(label)) / 2;
        int textY = by + (BTN_H + fm.getAscent() - fm.getDescent()) / 2 - 1;

        g2.setColor(new Color(0, 0, 0, 200));
        g2.drawString(label, textX + 1, textY + 1);

        g2.setColor(active ? TEXT_SELECTED : TEXT_NORMAL);
        g2.drawString(label, textX, textY);
    }

    private void paintGlowLayer(Graphics2D g2, int bx, int by, int expand, Color c) {
        g2.setColor(c);
        g2.fillRoundRect(bx - expand, by - expand,
            BTN_W + expand * 2, BTN_H + expand * 2, 10 + expand, 10 + expand);
    }

    private void drawDiamond(Graphics2D g2, int cx, int cy, int r) {
        int[] xs = { cx, cx + r, cx, cx - r };
        int[] ys = { cy - r, cy, cy + r, cy };
        g2.fillPolygon(xs, ys, 4);
        g2.setColor(new Color(255, 255, 255, 60));
        g2.drawPolygon(xs, ys, 4);
    }

    private void paintHints(Graphics2D g2, int W, int H) {
        g2.setFont(HINT_FONT);
        g2.setColor(new Color(120, 112, 130));
        String hint = "W / S  to navigate     Enter to select";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(hint, (W - fm.stringWidth(hint)) / 2, H - 18);
    }

    public int getSelectedIndex()  { return selectedIndex; }
    public String getSelectedLabel() { return OPTIONS[selectedIndex]; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Gun Mayhem");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            Menu menu = new Menu();
            menu.setSelectionListener((idx, label) -> {
                JOptionPane.showMessageDialog(null, "Opening: " + label,
                    "Gun Mayhem", JOptionPane.PLAIN_MESSAGE);
            });

            frame.add(menu);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            menu.requestFocusInWindow();
        }
        );
    }
}
