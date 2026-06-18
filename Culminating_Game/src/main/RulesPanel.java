package main;



import javax.imageio.ImageIO;

import javax.swing.*;

import java.awt.*;

import java.awt.event.*;

import java.awt.image.BufferedImage;

import java.io.File;

import java.io.IOException;

import java.net.URL;



public class RulesPanel extends JPanel {



 private BufferedImage backgroundImage;



 private RulesListener listener;



 /*

 * These rectangles are recalculated whenever the panel is drawn.

 * Therefore, they remain centered when the window is resized.

 */

 private final Rectangle titleBounds = new Rectangle();

 private final Rectangle rulesBoxBounds = new Rectangle();

 private final Rectangle backButtonBounds = new Rectangle();



 private static final Color BG_DARK =

 new Color(18, 14, 22);



 private static final Color STONE_MID =

 new Color(68, 62, 76);



 private static final Color STONE_LIGHT =

 new Color(92, 86, 100);



 private static final Color STONE_EDGE =

 new Color(120, 112, 130);



 private static final Color TEXT_NORMAL =

 new Color(205, 195, 175);



 private static final Color TEXT_SELECTED =

 new Color(255, 238, 160);



 public interface RulesListener {

 void onBack();

 }



 public RulesPanel(

 JFrame frame,

 MainClass m,

 int windowWidth,

 int windowHeight

 ) {



 setFocusable(true);

 setBackground(BG_DARK);



 /*

 * Use the original window dimensions as the preferred size.

 * The actual drawing still uses getWidth() and getHeight(),

 * so it responds correctly when resized.

 */

 setPreferredSize(

 new Dimension(windowWidth, windowHeight)

 );



 backgroundImage = loadImage("bg.png");



 addKeyListener(new KeyAdapter() {



 @Override

 public void keyPressed(KeyEvent e) {



 int keyCode = e.getKeyCode();



 if (keyCode == KeyEvent.VK_L

 || keyCode == KeyEvent.VK_J

 || keyCode == KeyEvent.VK_ESCAPE) {



 if (listener != null) {

 listener.onBack();

 }

 }

 }

 });



 /*

 * Repaint the panel whenever its size changes.

 */

 addComponentListener(new ComponentAdapter() {



 @Override

 public void componentResized(ComponentEvent e) {

 repaint();

 }

 });

 }



 public void setRulesListener(RulesListener listener) {

 this.listener = listener;

 }



 /*

 * Calculates the position and size of every main component.

 *

 * The scale changes based on the current width and height.

 * The complete group is then centered vertically and horizontally.

 */

 private void updateLayoutBounds() {



 int panelWidth = getWidth();

 int panelHeight = getHeight();



 if (panelWidth <= 0 || panelHeight <= 0) {

 return;

 }



 /*

 * 1200 x 600 is treated as the normal design size.

 *

 * The scale becomes smaller when the panel becomes smaller,

 * but it will not grow above 1.0.

 */

 double widthScale = panelWidth / 1200.0;

 double heightScale = panelHeight / 600.0;



 double scale = Math.min(widthScale, heightScale);



 scale = Math.max(0.45, Math.min(1.0, scale));



 int margin = Math.max(

 10,

 (int) (20 * scale)

 );



 int titleHeight = Math.max(

 30,

 (int) (50 * scale)

 );



 int titleGap = Math.max(

 5,

 (int) (12 * scale)

 );



 int boxWidth = Math.min(

 (int) (1000 * scale),

 panelWidth - margin * 2

 );



 boxWidth = Math.max(180, boxWidth);



 int boxHeight = Math.max(

 150,

 (int) (300 * scale)

 );



 int buttonGap = Math.max(

 10,

 (int) (20 * scale)

 );



 int buttonWidth = Math.min(

 (int) (200 * scale),

 panelWidth - margin * 2

 );



 buttonWidth = Math.max(100, buttonWidth);



 int buttonHeight = Math.max(

 35,

 (int) (50 * scale)

 );



 int totalContentHeight =

 titleHeight

 + titleGap

 + boxHeight

 + buttonGap

 + buttonHeight;



 /*

 * Start the complete group in the vertical center.

 */

 int startY =

 (panelHeight - totalContentHeight) / 2;



 startY = Math.max(margin, startY);



 titleBounds.setBounds(

 0,

 startY,

 panelWidth,

 titleHeight

 );



 int boxX =

 (panelWidth - boxWidth) / 2;



 int boxY =

 titleBounds.y

 + titleBounds.height

 + titleGap;



 rulesBoxBounds.setBounds(

 boxX,

 boxY,

 boxWidth,

 boxHeight

 );



 int buttonX =

 (panelWidth - buttonWidth) / 2;



 int buttonY =

 rulesBoxBounds.y

 + rulesBoxBounds.height

 + buttonGap;



 backButtonBounds.setBounds(

 buttonX,

 buttonY,

 buttonWidth,

 buttonHeight

 );

 }



 @Override

 public void paintComponent(Graphics g) {



 super.paintComponent(g);



 Graphics2D g2 =

 (Graphics2D) g.create();



 g2.setRenderingHint(

 RenderingHints.KEY_ANTIALIASING,

 RenderingHints.VALUE_ANTIALIAS_ON

 );



 g2.setRenderingHint(

 RenderingHints.KEY_INTERPOLATION,

 RenderingHints.VALUE_INTERPOLATION_BILINEAR

 );



 int panelWidth = getWidth();

 int panelHeight = getHeight();



 /*

 * Recalculate all positions using the panel's current size.

 */

 updateLayoutBounds();



 // Draw the background image.

 if (backgroundImage != null) {



 g2.drawImage(

 backgroundImage,

 0,

 0,

 panelWidth,

 panelHeight,

 null

 );

 } else {



 g2.setColor(BG_DARK);



 g2.fillRect(

 0,

 0,

 panelWidth,

 panelHeight

 );

 }



 // Dark transparent layer over the background.

 g2.setColor(

 new Color(0, 0, 0, 120)

 );



 g2.fillRect(

 0,

 0,

 panelWidth,

 panelHeight

 );



 drawTitle(g2);

 drawRulesBox(g2);

 drawBackButton(g2);



 g2.dispose();

 }



 private void drawTitle(Graphics2D g2) {



 int titleFontSize = Math.max(

 22,

 Math.min(

 36,

 titleBounds.height

 )

 );



 Font titleFont = new Font(

 "Monospaced",

 Font.BOLD,

 titleFontSize

 );



 g2.setFont(titleFont);

 g2.setColor(TEXT_SELECTED);



 String title = "RULES";



 FontMetrics fm =

 g2.getFontMetrics();



 int titleX =

 titleBounds.x

 + (titleBounds.width

 - fm.stringWidth(title)) / 2;



 int titleY =

 titleBounds.y

 + (titleBounds.height

 - fm.getHeight()) / 2

 + fm.getAscent();



 g2.drawString(

 title,

 titleX,

 titleY

 );

 }



 private void drawRulesBox(Graphics2D g2) {



 // Draw the rules-box background.

 g2.setColor(STONE_MID);



 g2.fillRoundRect(

 rulesBoxBounds.x,

 rulesBoxBounds.y,

 rulesBoxBounds.width,

 rulesBoxBounds.height,

 20,

 20

 );



 // Draw the rules-box border.

 g2.setColor(STONE_EDGE);

 g2.setStroke(new BasicStroke(3));



 g2.drawRoundRect(

 rulesBoxBounds.x,

 rulesBoxBounds.y,

 rulesBoxBounds.width,

 rulesBoxBounds.height,

 20,

 20

 );



 String[] rules = {

 "JOYSTICK - Move",

 "X - Attack",

 "Z - Pause / menu",

 "Defeat enemies to gain EXP",

 "Survive, then defeat the boss"

 };



 int fontSize = Math.max(

 12,

 Math.min(

 18,

 rulesBoxBounds.width / 45

 )

 );



 Font rulesFont = new Font(

 "Monospaced",

 Font.BOLD,

 fontSize

 );



 g2.setFont(rulesFont);

 g2.setColor(TEXT_NORMAL);



 FontMetrics fm = g2.getFontMetrics();



 int lineSpacing = Math.max(

 4,

 fontSize / 3

 );



 /*

 * Calculate the height of the complete text group.

 */

 int textBlockHeight =

 rules.length * fm.getHeight()

 + (rules.length - 1) * lineSpacing;



 /*

 * Start drawing so the complete group is vertically centered.

 */

 int textY =

 rulesBoxBounds.y

 + (rulesBoxBounds.height - textBlockHeight) / 2

 + fm.getAscent();



 for (String rule : rules) {



 /*

 * Calculate a separate X-position for every line

 * so each line is horizontally centered.

 */

 int textX =

 rulesBoxBounds.x

 + (rulesBoxBounds.width

 - fm.stringWidth(rule)) / 2;



 g2.drawString(

 rule,

 textX,

 textY

 );



 textY += fm.getHeight() + lineSpacing;

 }

 }



 private void drawBackButton(Graphics2D g2) {



 // Draw the button background.

 g2.setColor(STONE_LIGHT);



 g2.fillRoundRect(

 backButtonBounds.x,

 backButtonBounds.y,

 backButtonBounds.width,

 backButtonBounds.height,

 15,

 15

 );



 // Draw the button border.

 g2.setColor(STONE_EDGE);

 g2.setStroke(new BasicStroke(3));



 g2.drawRoundRect(

 backButtonBounds.x,

 backButtonBounds.y,

 backButtonBounds.width,

 backButtonBounds.height,

 15,

 15

 );



 int fontSize = Math.max(

 14,

 Math.min(

 20,

 backButtonBounds.height / 2

 )

 );



 Font buttonFont = new Font(

 "Monospaced",

 Font.BOLD,

 fontSize

 );



 g2.setFont(buttonFont);

 g2.setColor(TEXT_SELECTED);



 String buttonText = "BACK";



 FontMetrics fm =

 g2.getFontMetrics();



 /*

 * Center the text horizontally and vertically

 * inside the button.

 */

 int textX =

 backButtonBounds.x

 + (backButtonBounds.width

 - fm.stringWidth(buttonText)) / 2;



 int textY =

 backButtonBounds.y

 + (backButtonBounds.height

 - fm.getHeight()) / 2

 + fm.getAscent();



 g2.drawString(

 buttonText,

 textX,

 textY

 );

 }



 @Override

 public void addNotify() {



 super.addNotify();



 /*

 * Request keyboard focus after the panel

 * has been added to the JFrame.

 */

 SwingUtilities.invokeLater(

 this::requestFocusInWindow

 );

 }



 private BufferedImage loadImage(String filename) {



 String[] resourceNames = {

 "/" + filename,

 "/assests/" + filename,

 "/assets/" + filename

 };



 /*

 * First try loading the image from the project resources.

 */

 for (String resourceName : resourceNames) {



 URL url =

 getClass().getResource(resourceName);



 if (url != null) {



 try {



 return ImageIO.read(url);



 } catch (IOException e) {



 e.printStackTrace();

 }

 }

 }



 String[] fileNames = {

 filename,

 "assests/" + filename,

 "assets/" + filename

 };



 /*

 * If the resource loading fails, try loading

 * the image directly from a file.

 */

 for (String fileName : fileNames) {



 File file =

 new File(fileName);



 if (file.exists()) {



 try {



 return ImageIO.read(file);



 } catch (IOException e) {



 e.printStackTrace();

 }

 }

 }



 System.out.println(

 "Unable to load image: " + filename

 );



 return null;

 }

}