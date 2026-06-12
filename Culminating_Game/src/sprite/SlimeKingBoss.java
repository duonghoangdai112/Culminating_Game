package sprite;

import absFrame.Character;
import absFrame.Monster;
import absFrame.Projectile;
import absFrame.Tiles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Final boss for the arena survival mode.
 *
 * The Slime King uses one 4-frame sprite row for both hopping and attacking:
 * 0 = squash, 1 = deep squash, 2 = stretch/airborne, 3 = land/impact.
 */
public class SlimeKingBoss extends Monster {
    private static final int PREPARE = 0;
    private static final int HOP = 1;
    private static final int LAND = 2;
    private static final int RECOVER = 3;

    private int state = PREPARE;
    private int stateCounter = 50;

    private final int prepareTicks = 50;
    private final int hopTicks = 34;
    private final int landTicks = 18;
    private final int recoverTicks = 34;

    private double hopStartX;
    private double hopStartY;
    private double hopEndX;
    private double hopEndY;

    private final double maxHopDistance = 330.0;
    private boolean circleAttackReleased = false;

    private final ArrayList<Projectile> slimeProjectiles = new ArrayList<Projectile>();
    private BufferedImage projectileImage;
    private final int projectileCount = 12;
    private final int projectileSize = 24;
    private final double projectileSpeed = 5.2;
    private final int projectileDamage = 8;

    private final double maxBossHealth;

    public SlimeKingBoss(HashMap<String, Integer> stats, int startTime, int width, int height, int x, int y, double speed) {
        super(stats, startTime, x, y, width, height, speed);
        maxBossHealth = health;
        walkFrameDelay = 10;
    }

    public void setProjectileImage(BufferedImage img) {
        projectileImage = img;
    }

    /**
     * Boss sprites should be much larger than normal monsters, so this method
     * lets GamePanel choose the final on-screen width instead of using
     * Monster.setWalkAnimation(), which scales enemies to 100 pixels wide.
     */
    public void setBossAnimation(BufferedImage sheet, int frameCount, int targetWidth) {
        if (sheet == null || frameCount <= 0 || targetWidth <= 0) {
            return;
        }

        int frameW = sheet.getWidth() / frameCount;
        int frameH = sheet.getHeight();
        if (frameW <= 0 || frameH <= 0) {
            return;
        }

        walkFrames = new BufferedImage[frameCount];
        for (int i = 0; i < frameCount; i++) {
            walkFrames[i] = sheet.getSubimage(i * frameW, 0, frameW, frameH);
        }

        double scale = (double) targetWidth / frameW;
        width = targetWidth;
        height = (int) Math.round(frameH * scale);
        monsterImage = walkFrames[0];
        walkFrameIndex = 0;
    }

    @Override
    public void Attack() {
        // The landing projectile burst is handled by move().
    }

    @Override
    public void move(int charX, int charY) {
        updateSlimeProjectiles();

        if (state == PREPARE) {
            if (stateCounter > prepareTicks / 2) {
                walkFrameIndex = 0;
            } else {
                walkFrameIndex = Math.min(1, getFrameCount() - 1);
            }

            stateCounter--;
            if (stateCounter <= 0) {
                startHop(charX, charY);
            }
            return;
        }

        if (state == HOP) {
            walkFrameIndex = Math.min(2, getFrameCount() - 1);

            double progress = 1.0 - ((double) stateCounter / hopTicks);
            progress = clamp(progress, 0.0, 1.0);
            double eased = easeInOut(progress);

            xx = hopStartX + (hopEndX - hopStartX) * eased;
            yy = hopStartY + (hopEndY - hopStartY) * eased;
            x = (int) Math.round(xx);
            y = (int) Math.round(yy);

            stateCounter--;
            if (stateCounter <= 0) {
                state = LAND;
                stateCounter = landTicks;
                circleAttackReleased = false;
            }
            return;
        }

        if (state == LAND) {
            walkFrameIndex = Math.min(3, getFrameCount() - 1);

            if (!circleAttackReleased) {
                releaseCircleAttack();
                circleAttackReleased = true;
            }

            stateCounter--;
            if (stateCounter <= 0) {
                state = RECOVER;
                stateCounter = recoverTicks;
            }
            return;
        }

        if (state == RECOVER) {
            walkFrameIndex = 0;
            stateCounter--;
            if (stateCounter <= 0) {
                state = PREPARE;
                stateCounter = prepareTicks;
            }
        }
    }

    private void startHop(int charX, int charY) {
        double bossCenterX = x + width / 2.0;
        double bossCenterY = y + height / 2.0;

        // GamePanel passes the player's top-left. Aim around the player's center.
        double targetX = charX + 50.0;
        double targetY = charY + 50.0;

        double diffX = targetX - bossCenterX;
        double diffY = targetY - bossCenterY;
        double distance = Math.hypot(diffX, diffY);

        if (distance < 1.0) {
            diffX = 1.0;
            diffY = 0.0;
            distance = 1.0;
        }

        setFacingFromDelta(diffX);

        double hopDistance = Math.min(maxHopDistance, distance);
        double dirX = diffX / distance;
        double dirY = diffY / distance;

        hopStartX = xx;
        hopStartY = yy;
        hopEndX = xx + dirX * hopDistance;
        hopEndY = yy + dirY * hopDistance;

        state = HOP;
        stateCounter = hopTicks;
    }

    private void releaseCircleAttack() {
        double centerX = x + width / 2.0;
        double centerY = y + height * 0.72;

        for (int i = 0; i < projectileCount; i++) {
            double angle = 2.0 * Math.PI * i / projectileCount;
            double vx = Math.cos(angle) * projectileSpeed;
            double vy = Math.sin(angle) * projectileSpeed;

            Projectile projectile = new Projectile(
                    (int) Math.round(centerX - projectileSize / 2.0),
                    (int) Math.round(centerY - projectileSize / 2.0),
                    projectileSize,
                    projectileSize,
                    vx,
                    vy,
                    projectileDamage,
                    projectileImage,
                    1.3);
            slimeProjectiles.add(projectile);
        }
    }

    private void updateSlimeProjectiles() {
        Iterator<Projectile> it = slimeProjectiles.iterator();
        while (it.hasNext()) {
            Projectile projectile = it.next();
            projectile.move();

            if (!projectile.getVisibility()
                    || projectile.x < -300
                    || projectile.y < -300
                    || projectile.x > 4000
                    || projectile.y > 4000) {
                it.remove();
            }
        }
    }

    @Override
    public void checkCollision(int charX, int charY, ArrayList<Tiles> Rtiles, Character c) {
        if (c == null) {
            return;
        }

        if (this.intersects(c)) {
            int contactDamage = (int) damage;
            if (state == LAND) {
                contactDamage *= 2;
            }
            c.takeDamage(contactDamage);
        }

        for (Projectile projectile : slimeProjectiles) {
            if (projectile.getVisibility() && projectile.intersects(c)) {
                c.takeDamage(projectileDamage);
                projectile.setVisibility(false);
            }
        }
    }

    @Override
    public void drawMonster(Graphics2D g2) {
        drawSlimeProjectiles(g2);
        super.drawMonster(g2);
        drawBossHealthBar(g2);
    }

    private void drawSlimeProjectiles(Graphics2D g2) {
        for (Projectile projectile : slimeProjectiles) {
            if (!projectile.getVisibility()) {
                continue;
            }

            int drawW = (int) Math.round(projectile.width * projectile.ratio);
            int drawH = (int) Math.round(projectile.height * projectile.ratio);
            int drawX = projectile.x - (drawW - projectile.width) / 2;
            int drawY = projectile.y - (drawH - projectile.height) / 2;

            if (projectile.bulletImg != null) {
                Graphics2D projG = (Graphics2D) g2.create();
                projG.rotate(projectile.angle,
                        projectile.x + projectile.width / 2.0,
                        projectile.y + projectile.height / 2.0);
                projG.drawImage(projectile.bulletImg, drawX, drawY, drawW*3, drawH*3, null);
                projG.dispose();
            } else {
                g2.setColor(new Color(80, 230, 70, 230));
                g2.fillOval(drawX, drawY, drawW, drawH);
                g2.setColor(new Color(20, 70, 20, 230));
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(drawX, drawY, drawW, drawH);
            }
        }
    }

    private void drawBossHealthBar(Graphics2D g2) {
        int barW = Math.max(width, 180);
        int barH = 14;
        int barX = x + (width - barW) / 2;
        int barY = y - 28;

        double ratio = maxBossHealth <= 0 ? 0.0 : health / maxBossHealth;
        ratio = clamp(ratio, 0.0, 1.0);

        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
        FontMetrics fm = g2.getFontMetrics();
        String label = "SLIME KING";
        int labelX = x + (width - fm.stringWidth(label)) / 2;

        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRoundRect(barX - 4, barY - fm.getAscent() - 2, barW + 8, barH + fm.getHeight() + 6, 8, 8);

        g2.setColor(new Color(255, 235, 130));
        g2.drawString(label, labelX, barY - 4);

        g2.setColor(new Color(85, 20, 35));
        g2.fillRect(barX, barY, barW, barH);
        g2.setColor(new Color(70, 220, 70));
        g2.fillRect(barX, barY, (int) Math.round(barW * ratio), barH);
        g2.setColor(Color.WHITE);
        g2.drawRect(barX, barY, barW, barH);
    }

    private int getFrameCount() {
        return walkFrames == null ? 0 : walkFrames.length;
    }

    private double easeInOut(double t) {
        return t * t * (3.0 - 2.0 * t);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public boolean isLanding() {
        return state == LAND;
    }

    public Rectangle getBoundsForProjectiles() {
        return getBounds();
    }
}
