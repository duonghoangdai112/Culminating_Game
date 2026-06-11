package main;

import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MainClass {
    MainClass(){}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainClass m = new MainClass();

            JFrame frame = new JFrame("Gun Mayhem");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(true);

            showMainMenu(frame, m);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            // Give the menu keyboard focus now that the window is on screen.
            frame.getContentPane().requestFocusInWindow();
        });
    }

    /**
     * Swaps the visible panel while keeping the window's current size and
     * position. The window is only packed and centred on the very first show
     * (before it becomes visible); after that, whatever size the player has
     * resized the window to is preserved across every screen change.
     */
    private static void swapContent(JFrame frame, JComponent panel) {
        if (frame.isShowing()) {
            // Already on screen: keep the player's current size/position and
            // just re-lay-out the new panel to fill it. No pack() => no snap-back.
            frame.setContentPane(panel);
            frame.revalidate();
            frame.repaint();
        } else {
            // First launch: size to the panel's preferred size and centre it.
            frame.setContentPane(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
        }
        panel.requestFocusInWindow();
    }

    /**
     * Shows the main menu panel.
     */
    private static void showMainMenu(JFrame frame, MainClass m) {
        GameMenu menu = new GameMenu();

        menu.setSelectionListener((idx, label) -> {
            if (label.equals("Play") || label.equals("Characters")) {
                showLoadoutScreen(frame, m);
            }
            else if (label.equals("Rules")) {
                System.out.println("Open rules screen here");
            }
        });

        swapContent(frame, menu);
    }

    /**
     * Shows the character + weapon selection screen.
     */
    private static void showLoadoutScreen(JFrame frame, MainClass m) {
        LoadoutScreen loadoutScreen = new LoadoutScreen();

        loadoutScreen.setLoadoutListener(new LoadoutScreen.LoadoutListener() {
            @Override
            public void onStart(String characterName, String weaponName) {
                showGamePanel(frame, m, characterName, weaponName);
            }

            @Override
            public void onBack() {
                showMainMenu(frame, m);
            }
        });

        swapContent(frame, loadoutScreen);
    }

    /**
     * Shows the actual game panel.
     */
    private static void showGamePanel(JFrame frame, MainClass m, String characterName, String weaponName) {
        GamePanel panel = new GamePanel(m.rZomCons(), characterName, weaponName);

        // This is called when the player clicks X, then chooses YES.
        panel.setReturnToMenuListener(() -> showMainMenu(frame, m));

        // This is called from the death screen.
        panel.setDeathScreenListener(new GamePanel.DeathScreenListener() {
            @Override
            public void onRestart() {
                showGamePanel(frame, m, characterName, weaponName);
            }

            @Override
            public void onReturnToMenu() {
                showMainMenu(frame, m);
            }
        });

        swapContent(frame, panel);
    }

    // stats holder for range Monster
    public HashMap<String,Integer> rZomCons() {
        HashMap<String,Integer> ZomMon = new HashMap<String,Integer>();
        ZomMon.put("health",500);
        ZomMon.put("damage",10);
        ZomMon.put("visionRange",1);

        ZomMon.put("speedX", 4);
        ZomMon.put("speedY", 4);

        ZomMon.put("width", 1);
        ZomMon.put("width", 1);
        ZomMon.put("cooldown",10);
        ZomMon.put("x",5);

        ZomMon.put("y",5);

        return ZomMon;
    }

    public HashMap<String, Integer[]> rWeaponCons(){
        // name of the weapon : [ratio * 10, number of frame]
        HashMap<String,Integer[]> guns = new HashMap<String,Integer[]>();
        guns.put("Sniper-animation",convertInt(5,3));
        guns.put("47-animation",convertInt(6,4));
        guns.put("glock-animation",convertInt(3,3));

        return guns;
    }

    public Integer[] convertInt (int frame, int ratio){
        Integer[] lis = {frame,ratio};
        return lis;
    }
}