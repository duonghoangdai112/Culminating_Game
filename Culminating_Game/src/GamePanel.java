import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

public class GamePanel extends JPanel implements ActionListener {
    Timer timer; 
    int TIMERSPEED = 25;

    private int width = 500; 
    private int length = 500;

    public GamePanel(){
        this.setPreferredSize(new Dimension(width, length));
        timer = new Timer(TIMERSPEED, this);
		timer.start();
		timer.setInitialDelay(0);

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.fillRect(ALLBITS, ABORT, WIDTH, HEIGHT);

        Random rd = new Random();
			
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
		for (int i =0;i<30;i++){
				//1
			g2.setPaint(new Color(rd.nextInt(256),rd.nextInt(256),rd.nextInt(256)));
			g2.setStroke(new BasicStroke(rd.nextInt(20)));
			g2.drawOval(i*10, i*10, this.getHeight()-i*20, this.getHeight()-i*20);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.repaint();
       
    }

    private class KeyLis extends KeyAdapter implements KeyListener{
        public void keyPressed(KeyEvent e) {
            System.out.println("hello");
        }

    }

    


    
}