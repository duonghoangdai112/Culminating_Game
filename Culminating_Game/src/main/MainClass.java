package main;

import java.util.HashMap;

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
        });
    }

    /**
     * Shows the main menu panel.
     */
    private static void showMainMenu(JFrame frame, MainClass m) {
        GameMenu menu = new GameMenu();

        frame.setContentPane(menu);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.revalidate();
        frame.repaint();

        menu.requestFocusInWindow();

        menu.setSelectionListener((idx, label) -> {
            if (label.equals("Play") || label.equals("Characters")) {
                showLoadoutScreen(frame, m);
            }
            else if (label.equals("Rules")) {
                System.out.println("Open rules screen here");
            }
        });
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

        frame.setContentPane(loadoutScreen);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.revalidate();
        frame.repaint();

        loadoutScreen.requestFocusInWindow();
    }

    /**
     * Shows the actual game panel.
     */
    private static void showGamePanel(JFrame frame, MainClass m, String characterName, String weaponName) {
        GamePanel panel = new GamePanel(m.rMonCons(), characterName, weaponName);

        // This is called when the player clicks X, then chooses YES.
        panel.setReturnToMenuListener(() -> showMainMenu(frame, m));

        frame.setContentPane(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.revalidate();
        frame.repaint();

        panel.requestFocusInWindow();
    }

    // stats holder for range Monster
    public HashMap<String,Integer> rMonCons() {
        HashMap<String,Integer> rangeMon = new HashMap<String,Integer>();
        rangeMon.put("health",100);
        rangeMon.put("damage",10);
        rangeMon.put("visionRange",5);

        rangeMon.put("speedX", 1);
        rangeMon.put("speedY", 1);

        rangeMon.put("width", 1);
        rangeMon.put("width", 1);
        rangeMon.put("cooldown",1);
        rangeMon.put("x",5);

        rangeMon.put("y",5);

        return rangeMon;
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
