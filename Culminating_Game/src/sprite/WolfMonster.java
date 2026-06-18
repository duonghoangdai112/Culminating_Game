package sprite;


import absFrame.Character;
import absFrame.Monster;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * A wolf monster with a simple dash attack.
 *
 * It works like RangeMonster because it extends Monster, but it has its own
 * movement state machine:
 * CHASE -> WARNING -> DASH -> COOLDOWN -> CHASE
 */
public class WolfMonster extends Monster {
    private static final int CHASE = 0;
    private static final int WARNING = 1;
    private static final int DASH = 2;
    private static final int COOLDOWN = 3;

    private int state = CHASE;

    private int dashCooldownCounter = 120;
    private final int dashCooldownTicks = 180;
    private int warningCounter = 0;
    private final int warningTicks = 55;
    private int dashCounter = 0;
    private final int dashTicks = 24;
    private int cooldownCounter = 0;
    private final int cooldownTicks = 45;

    private final double dashSpeed = 18.0;
    private final double dashTriggerRange = 900.0;
    private final int warningLength = 480;
    private final int warningWidth = 55;

    private double dashDirX = 1.0;
    private double dashDirY = 0.0;
    private double dashAngle = 0.0;

    private BufferedImage dashEffectImage;

    public WolfMonster(HashMap<String, Integer> stats, int startTime, int width, int height, int x, int y, double speed) {
        super(stats, startTime, x, y, width, height, speed);
        dashCooldownCounter = 90 + (int)(Math.random() * 80);
    }

    public void setDashEffectImage(BufferedImage img) {
        dashEffectImage = img;
    }

    @Override
    public void Attack() {
        // The dash attack is handled in move().
    }

    @Override
    public void move(int charX, int charY) {
        double wolfCenterX = x + width / 2.0;
        double wolfCenterY = y + height / 2.0;

        // GamePanel passes the player's top-left, so aim near the player's center.
        double targetX = charX + 50.0;
        double targetY = charY + 50.0;

        double diffX = targetX - wolfCenterX;
        double diffY = targetY - wolfCenterY;
        double distance = Math.hypot(diffX, diffY);

        setFacingFromDelta(diffX);

        if (state == CHASE) {
            super.move(charX, charY);

            dashCooldownCounter--;
            if (dashCooldownCounter <= 0 && distance <= dashTriggerRange && distance > 1) {
                startWarning(diffX, diffY, distance);
            }
            return;
        }

        if (state == WARNING) {
            updateWalkAnimation(false);
            warningCounter--;

            if (warningCounter <= 0) {
                state = DASH;
                dashCounter = dashTicks;
            }
            return;
        }

        if (state == DASH) {
            xx += dashDirX * dashSpeed;
            yy += dashDirY * dashSpeed;
            x = (int)(xx);
            y = (int)(yy);

            updateWalkAnimation(true);

            dashCounter--;
            if (dashCounter <= 0) {
                state = COOLDOWN;
                cooldownCounter = cooldownTicks;
            }
            return;
        }

        if (state == COOLDOWN) {
            super.move(charX, charY);
            cooldownCounter--;
            if (cooldownCounter <= 0) {
                state = CHASE;
                dashCooldownCounter = dashCooldownTicks;
            }
        }
    }

    private void startWarning(double diffX, double diffY, double distance) {
        dashDirX = diffX / distance;
        dashDirY = diffY / distance;
        dashAngle = Math.atan2(dashDirY, dashDirX);
        warningCounter = warningTicks;
        state = WARNING;
    }

    @Override
    public void drawMonster(Graphics2D g2) {
        if (state == WARNING) {
            drawWarningZone(g2);
        }

        if (state == DASH) {
            drawDashEffect(g2);
        }

        super.drawMonster(g2);
    }

    private void drawWarningZone(Graphics2D g2) {
        int centerX = x + width / 2;
        int centerY = y + height / 2;

        Graphics2D warnG = (Graphics2D) g2.create();
        warnG.translate(centerX, centerY);
        warnG.rotate(dashAngle);

        warnG.setColor(new Color(255, 0, 0, 85));
        warnG.fillRect(0, -warningWidth / 2, warningLength, warningWidth);

        warnG.setColor(new Color(255, 40, 40, 210));
        warnG.setStroke(new BasicStroke(3f));
        warnG.drawRect(0, -warningWidth / 2, warningLength, warningWidth);
        warnG.dispose();
    }

    private void drawDashEffect(Graphics2D g2) {
        if (dashEffectImage == null) {
            return;
        }

        int centerX = x + width / 2;
        int centerY = y + height / 2;
        int effectW = 250;
        int effectH = 90;

        Graphics2D effectG = (Graphics2D) g2.create();
        effectG.translate(centerX, centerY);
        effectG.rotate(dashAngle);
        effectG.drawImage(dashEffectImage, -effectW / 2, -effectH / 2, effectW, effectH, null);
        effectG.dispose();
    }

    @Override
    public void checkCollision(int charX, int charY, Character c) {
        if (c != null && this.intersects(c)) {
            int hitDamage = (int) damage;
            if (state == DASH) {
                hitDamage *= 2;
            }
            c.takeDamage(hitDamage);
        }
    }

    public boolean isDashing() {
        return state == DASH;
    }

    public boolean isWarning() {
        return state == WARNING;
    }
}
