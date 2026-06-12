package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import absFrame.Character;

public class GameHUD {

    public static void drawSurvivalTimer(Graphics2D g2, double fullTime,
                                         int survivalTimeSeconds,
                                         boolean bossPhaseStarted) {
        int timeLeft = Math.max(0, survivalTimeSeconds - (int) fullTime);

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

    public static void drawPlayerLevel(Graphics2D g2, Character archer) {
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
}
