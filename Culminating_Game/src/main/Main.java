package main;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;


public class Main {
    Main(){}
	public static void main(String[] args) {
        // monster stats init and setUp 
        Main m = new Main();
		GamePanel panel = new GamePanel(m.rMonCons());

        // frame init and set up
        JFrame frame = new JFrame(); 
        
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        
        frame.add(panel,BorderLayout.CENTER);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

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
        guns.put("glock-animation",convertInt(7,4));

        
        return guns;
    }
    
    public Integer[] convertInt (int frame, int ratio){
    	Integer[] lis = {frame,ratio};
    	return lis;
    }


}