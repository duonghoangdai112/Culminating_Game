package main;

import java.awt.Rectangle;

/**
 * Simple camera that follows a target inside the full world map.
 *
 * Game objects keep their normal world positions. The camera only changes where
 * those objects are drawn on the screen. Zoom affects drawing only; world
 * movement and collisions stay in normal world coordinates.
 */
public class Camera {
    private int x;
    private int y;
    private double zoom = 1.5;

    public void follow(Rectangle target, int screenW, int screenH, int worldW, int worldH) {
        if (target == null) {
            x = 0;
            y = 0;
            return;
        }

        int viewW = Math.max(1, (int) (screenW / zoom));
        int viewH = Math.max(1, (int) (screenH / zoom));

        x = target.x + target.width / 2 - viewW / 2;
        y = target.y + target.height / 2 - viewH / 2;

        int maxX = Math.max(0, worldW - viewW);
        int maxY = Math.max(0, worldH - viewH);

        x = clamp(x, 0, maxX);
        y = clamp(y, 0, maxY);
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        if (zoom > 0) {
            this.zoom = zoom;
        }
    }
}
