package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class PopUp {

    public static void drawCloseButton(Graphics2D g2, int panelWidth, Rectangle closeButtonBounds) {
        int size = 40;
        int margin = 20;
        int x = panelWidth - size - margin;
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

    public static void drawReturnDialog(Graphics2D g2, int panelWidth, int panelHeight,
                                        int selection, Rectangle yesButtonBounds,
                                        Rectangle noButtonBounds) {
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, 0, panelWidth, panelHeight);

        int boxW = 430;
        int boxH = 200;
        int boxX = (panelWidth - boxW) / 2;
        int boxY = (panelHeight - boxH) / 2;

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

        drawPopupButton(g2, yesButtonBounds, "YES", selection == 0);
        drawPopupButton(g2, noButtonBounds, "NO", selection == 1);

        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        String controls = "Use joystick to choose, B to confirm";
        FontMetrics controlsFm = g2.getFontMetrics();
        int controlsX = boxX + (boxW - controlsFm.stringWidth(controls)) / 2;
        g2.drawString(controls, controlsX, boxY + boxH - 12);
    }

    public static void drawLevelUpScreen(Graphics2D g2, int panelWidth, int panelHeight,
                                         int selection, Rectangle healthBounds,
                                         Rectangle damageBounds, Rectangle speedBounds) {
        g2.setColor(new Color(0, 0, 0, 185));
        g2.fillRect(0, 0, panelWidth, panelHeight);

        int boxW = 540;
        int boxH = 360;
        int boxX = (panelWidth - boxW) / 2;
        int boxY = (panelHeight - boxH) / 2;

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

        healthBounds.setBounds(btnX, firstY, btnW, btnH);
        damageBounds.setBounds(btnX, firstY + btnH + gap, btnW, btnH);
        speedBounds.setBounds(btnX, firstY + (btnH + gap) * 2, btnW, btnH);

        drawPopupButton(g2, healthBounds, "1  HEALTH +20", selection == 0);
        drawPopupButton(g2, damageBounds, "2  DAMAGE +5", selection == 1);
        drawPopupButton(g2, speedBounds, "3  SPEED +1", selection == 2);

        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        String controls = "Use W/S to choose, B to confirm";
        FontMetrics controlsFm = g2.getFontMetrics();
        int controlsX = boxX + (boxW - controlsFm.stringWidth(controls)) / 2;
        g2.setColor(new Color(230, 240, 255));
        g2.drawString(controls, controlsX, boxY + boxH - 20);
    }

    public static void drawDeathScreen(Graphics2D g2, int panelWidth, int panelHeight,
                                       int selection, Rectangle restartBounds,
                                       Rectangle menuBounds) {
        drawEndScreen(g2, panelWidth, panelHeight,
                "YOU DIED",
                "Choose what you want to do next.",
                new Color(40, 28, 32),
                new Color(190, 80, 80),
                new Color(245, 215, 195),
                selection, restartBounds, menuBounds);
    }

    public static void drawWinScreen(Graphics2D g2, int panelWidth, int panelHeight,
                                     int selection, Rectangle restartBounds,
                                     Rectangle menuBounds) {
        drawEndScreen(g2, panelWidth, panelHeight,
                "YOU WIN!",
                "You defeated the Slime King.",
                new Color(32, 48, 38),
                new Color(120, 210, 120),
                new Color(230, 255, 210),
                selection, restartBounds, menuBounds);
    }

    private static void drawEndScreen(Graphics2D g2, int panelWidth, int panelHeight,
                                      String title, String subtitle,
                                      Color boxColor, Color borderColor, Color textColor,
                                      int selection, Rectangle restartBounds,
                                      Rectangle menuBounds) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, panelWidth, panelHeight);

        int boxW = 470;
        int boxH = 240;
        int boxX = (panelWidth - boxW) / 2;
        int boxY = (panelHeight - boxH) / 2;

        g2.setColor(boxColor);
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 18, 18);

        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(3f));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 18, 18);

        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 36));
        FontMetrics titleFm = g2.getFontMetrics();
        int titleX = boxX + (boxW - titleFm.stringWidth(title)) / 2;

        g2.setColor(textColor);
        g2.drawString(title, titleX, boxY + 65);

        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        FontMetrics subFm = g2.getFontMetrics();
        int subX = boxX + (boxW - subFm.stringWidth(subtitle)) / 2;
        g2.drawString(subtitle, subX, boxY + 100);

        int btnW = 150;
        int btnH = 48;
        int gap = 35;
        int btnY = boxY + 135;
        int restartX = boxX + (boxW - btnW * 2 - gap) / 2;
        int menuX = restartX + btnW + gap;

        restartBounds.setBounds(restartX, btnY, btnW, btnH);
        menuBounds.setBounds(menuX, btnY, btnW, btnH);

        drawPopupButton(g2, restartBounds, "RESTART", selection == 0);
        drawPopupButton(g2, menuBounds, "MENU", selection == 1);

        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        String controls = "Use joystick to choose, B to confirm";
        FontMetrics controlsFm = g2.getFontMetrics();
        int controlsX = boxX + (boxW - controlsFm.stringWidth(controls)) / 2;
        g2.drawString(controls, controlsX, boxY + boxH - 18);
    }

    private static void drawPopupButton(Graphics2D g2, Rectangle bounds, String text, boolean selected) {
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
}
