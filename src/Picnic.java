/**
 * Vishal Nigam
 * 2/26/21
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

public class Picnic extends JPanel {

    private double seesawAngle = 0; // angle of seesaw
    private double seesawDelta = 0.01; // direction & num radians
    int alpha1 = 100; // sun alpha outer ring
    int alpha2 = 100; // sun alpha inner ring
    int changeAlpha1 = 2; // change factor for alpha1
    int changeAlpha2 = 1; // change factor for alpha2
    private int framenum = 0; // Set the initial frame number
    private boolean shouldChange = false; // A boolean to allow for value changes every few frames instead of every frame.
    private boolean invert = false;
    private double bezx1 = 0.5; // Bezel curve point values to facilitate the bird animation.
    private double  bezy1 = 1;
    private double bezx2 = 0.5;
    private double bezy2 = 1;
    private double bezx3 = 2;
    private double  bezy3 = 0.5;
    private double moveSun = 0;
    private int skyRed = 255;
    private int skyGreen = 125;
    private int skyBlue = 0;
    private boolean morning = true;
    private boolean day = false;
    private boolean night = false;
    private boolean evening = false;
    private int nightClock = 0;
    private float pixelSize;



    public static void main(String[] args){
        JFrame screen = new JFrame();
        Picnic panel = new Picnic();
        screen.setContentPane(panel);
        screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        screen.pack();
        screen.setResizable(true);
        Dimension viewsize = Toolkit.getDefaultToolkit().getScreenSize();
            //Create a timer to handle animations
        Timer animate = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.advanceScene(); //perform all necessary steps to change frames
                panel.repaint(); //redraw the canvas
            }
        });

        final long startTime = System.currentTimeMillis();

        screen.setLocation((viewsize.width - screen.getWidth())/2, (viewsize.height - screen.getHeight())/2);
        screen.setVisible(true);
        animate.start(); //start the animation

    }

    public Picnic() {
        setPreferredSize(new Dimension(1000,1000) );
    }

    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //Draw sky, grass, and lake
        drawBackground(g2);

        applyWindowToViewportTransformation(g2, -5, 10, -1, 14, true);

        drawScene(g2);
    }

    private void drawScene(Graphics2D g) {

        AffineTransform cs = g.getTransform();
        drawMainScene(g);
        g.setTransform(cs);
    }

    private void drawMainScene(Graphics2D g2) {

        //Draw the sun
        {
            AffineTransform save = g2.getTransform();
            g2.translate(2.2,1);
            g2.rotate(-moveSun);
            drawSun(g2);
            g2.setTransform(save);
        }

        //Draw the trees
        {
            AffineTransform cs = g2.getTransform();
            g2.setPaint(new Color(20, 50, 20)); //Dark green for leaves
            g2.translate(-8, 0);
            drawTree(g2);
            g2.setTransform(cs);
            g2.translate(6, 0);
            drawTree(g2);
            g2.setTransform(cs);
        }

        //Draw the Seesaw
        {
            AffineTransform save = g2.getTransform();
            g2.setStroke(new BasicStroke(4 * pixelSize));
            g2.setPaint(Color.WHITE);
            g2.translate(2, 0);
            g2.scale(-1,1);
            g2.rotate(-seesawAngle, 2, 1);
            drawSeesawLever(g2);
            g2.setTransform(save);
            g2.translate(-2, 0);
            g2.rotate(seesawAngle, 2, 1);
            drawSeesawLever(g2);
            g2.setTransform(save);
            g2.translate(-2, 0);
            drawSeesawBase(g2);
        }

        //Draw the Flock of birds
        {
            AffineTransform save = g2.getTransform();
            g2.setPaint(Color.BLACK);
            g2.translate(1, 8);
            double dx = ((framenum + 150) % 600) * 0.05;  // The mod helps it "wrap" around
            g2.translate(15 - dx, 0);  // Move it up and over with the framenumber used for animation...
            drawCritter(g2);
            g2.setTransform(save);
        }

        {
            AffineTransform save = g2.getTransform();
            g2.setPaint(Color.BLACK);
            g2.translate(0, 9);
            double dx = ((framenum + 150) % 600) * 0.05;  // The mod helps it "wrap" around
            g2.translate(15 - dx, 0);  // Move it up and over with the framenumber used for animation...
            drawCritter(g2);
            g2.setTransform(save);
        }

        {
            AffineTransform save = g2.getTransform();
            g2.setPaint(Color.BLACK);
            g2.translate(2, 9);
            double dx = ((framenum + 150) % 600) * 0.05;  // The mod helps it "wrap" around
            g2.translate(15 - dx, 0);  // Move it up and over with the framenumber used for animation...
            drawCritter(g2);
            g2.setTransform(save);
        }

        //Draw the picnic blanket and the figure on top of it.
        {
            AffineTransform save = g2.getTransform();
            g2.translate(5, 0);
            drawBlanket(g2);
            g2.setTransform(save);
        }
        //Draw the people playing on the seesaw
        {
            AffineTransform save = g2.getTransform();
            g2.setPaint(Color.WHITE);
            g2.translate(1, 0);
            g2.rotate(seesawAngle, 1, 1);
            drawPeople(g2);
            g2.setTransform(save);
        }
    }

    private void drawSun(Graphics2D g2){
        int sunHeight = 10;
        g2.translate(5,-1);
        g2.setPaint(new Color(225, 225, 100, alpha1)); //outer ring
        g2.fill(new Ellipse2D.Double(3, sunHeight - 1, 4, 4));
        g2.setPaint(new Color(225, 200, 0, alpha2)); // inner ring
        g2.fill(new Ellipse2D.Double(3.5, sunHeight - 0.5, 3, 3));
        g2.setPaint(Color.YELLOW); // Set color to yellow
        g2.fill(new Ellipse2D.Double(4, sunHeight, 2, 2));
    }
    private void drawTree(Graphics2D g){
        AffineTransform cs = g.getTransform();  // Save C.S. state
        g.setPaint(new Color(87, 35, 31)); // Brown
        g.drawArc(0,0,3,6, 0, 80);
        g.setTransform(cs);
        g.scale(-1,1); // Flip arc on y axis
        g.translate(-6.5,0); //Move flipped arc to connect to first arc
        g.drawArc(0,0,3,6, 0, 80);
        g.setTransform(cs);
        g.rotate(75,0,0); // Brown square to complete tree roots, rotated to have a corner facing down
        g.translate(2.3,1);
        g.scale(0.75,0.75); //shrink square to fit seamlessly into tree
        g.fillRect(0,0,2,2);
        g.setTransform(cs);
        g.setPaint(new Color(20, 50, 20)); // Green for leaves
        g.translate(0.75,0); // Move leaves into place
        g.fillOval(0,3,5,5);
        g.setTransform(cs); // Restore previous C.S. state
    }

    private  void drawBackground(Graphics2D g){

        AffineTransform cs = g.getTransform();  // Save C.S. state
        g.setPaint(Color.GREEN);
        g.fillRect(0,getHeight()/2,getWidth(),getHeight()); //Grass
        g.setPaint(Color.BLUE);
        g.fillOval(0,0,getWidth(),getHeight()/3 * 2); //Lake which is on top of the grass
        g.setPaint(new Color(skyRed, skyGreen, skyBlue)); //color which will change as the sun moves
        g.fillRect(0,0,getWidth(),getHeight()/2); //Sky which will cover circle to make the lake
        g.setTransform(cs);       // Restore previous C.S. state
    }

    private void drawSeesawLever(Graphics2D g){

        AffineTransform cs = g.getTransform();  // Save C.S. state
        Path2D frame = new Path2D.Double(); // One side of lever
        frame.moveTo(0, 1);
        frame.lineTo(2,1);
        g.draw(frame);
        g.setTransform(cs);       // Restore previous C.S. state
    }

    private void drawSeesawBase(Graphics2D g){
        AffineTransform cs = g.getTransform();
        Path2D frame = new Path2D.Double(); // triangular base of seesaw
        frame.moveTo(2, 1);
        frame.lineTo(1.5,0);
        frame.lineTo(2.5,0);
        frame.lineTo(2,1);
        g.draw(frame);
        g.setTransform(cs);
    }

    private void drawPeople(Graphics2D g){
        AffineTransform cs = g.getTransform();
        Path2D leftMan = new Path2D.Double(); //Parent
        Path2D rightMan = new Path2D.Double(); //Child
        //Parent body
        leftMan.moveTo(-0.5,3);
        leftMan.lineTo(-0.5,2.5);
        leftMan.lineTo(0+seesawAngle,1.75+seesawAngle);//Move arms in time with rotation of seesaw
        leftMan.moveTo(-0.5,2.5);
        leftMan.lineTo(-1-seesawAngle,1.75+seesawAngle);
        leftMan.moveTo(-0.5,2.5);
        leftMan.lineTo(-0.5,1.2);
        leftMan.lineTo(-0.25+seesawAngle,0+seesawAngle); //Move legs in time with the rotation of seesaw
        leftMan.moveTo(-0.5,1.2);
        leftMan.lineTo(-0.75-seesawAngle,0+seesawAngle);
        //Child body
        rightMan.moveTo(2.75,2);
        rightMan.lineTo(2.5+seesawAngle,2+seesawAngle);
        rightMan.moveTo(2.75,2);
        rightMan.lineTo(2.5-seesawAngle,2+seesawAngle);
        rightMan.moveTo(2.75,2);
        rightMan.lineTo(2.75,1.5);
        rightMan.lineTo(2.5,1);
        rightMan.moveTo(2.75,1.5);
        rightMan.lineTo(2.75,1);
        g.draw(leftMan);
        g.draw(rightMan);
        g.setTransform(cs);
        g.translate(0,-0.25);
        g.fillOval(-1,3,1,1); //Parent head
        g.setTransform(cs);
        g.scale(0.5,0.5);
        g.translate(2,1);
        g.fillOval(3,3,1,1); //Child head
        g.setTransform(cs);
    }

    private void drawCritter(Graphics2D g){
        AffineTransform cs = g.getTransform();
        g.scale(0.2,0.2);
        g.translate(30,0);
        Path2D birb = new Path2D.Double();
        birb.moveTo(0,0);
        birb.curveTo(bezx1,bezy1,bezx2,bezy2,bezx3,bezy3); //Bezel curves for the bird's right wing
        birb.moveTo(0,0);
        birb.curveTo(-bezx1,bezy1,-bezx2,bezy2,-bezx3,bezy3); //Bezel curves for the bird's left wing
        g.draw(birb);
        g.setTransform(cs);
    }

    private void drawBlanket(Graphics2D g){
        AffineTransform cs = g.getTransform();
        g.setPaint(new Color(144, 0, 255));
        g.scale(1,0.85); //Properly fit blanket into the scene
        g.shear(0.5,0.2); // Change the rectangle for perspective
        g.fillRect(0,0,2,4);
        g.setTransform(cs);
        g.setPaint(Color.WHITE); // White to draw the person
        g.translate(0,-0.5);
        g.fillOval(2,3,1,1); //Person's head
        Path2D man = new Path2D.Double(); //Person's body
        man.moveTo(2.3,2.9);
        man.lineTo(3.5,3.75);
        man.lineTo(2.5,3.8);
        man.moveTo(2.3,2.9);
        man.lineTo(1.9,3.75);
        man.lineTo(2.5,3.8);
        man.moveTo(2.5,3.5);
        man.lineTo(2,2);
        man.moveTo(2,2);
        man.lineTo(1,0.75);
        man.moveTo(2,2);
        man.lineTo(0.75,0.75);
        g.draw(man);
        g.setTransform(cs);
        g.setPaint(Color.MAGENTA);
        g.translate(2,1);
        g.scale(0.5,0.5);
        g.fillRect(3,2,1,1);
        g.rotate(Math.PI/2);
        g.translate(-0.5,-7);
        g.drawArc(3,3,1,1,-90,180);
        g.setTransform(cs);
    }

    private void advanceScene(){
        framenum++; //Advance 1 frame
        moveSun-=0.00045;//Move the sun across the sky
        alpha1+=changeAlpha1; //change transparency of outer ring of the sun
        alpha2+=changeAlpha2; //change transparency of inner ring of the sun
        seesawAngle+=seesawDelta; //change seesaw lever angle
        //Every fifth frame shouldChange is set to true
        if(framenum%5 == 0){
            shouldChange = true;
        }
        else shouldChange = false;
        //Check seesaw angle and reverse directions at a specific angle
        if(seesawAngle>=Math.PI/6){
            seesawAngle=Math.PI/6;
            seesawDelta*=-1;
        }
        else if (seesawAngle<=-Math.PI/6){
            seesawAngle=-Math.PI/6;
            seesawDelta*=-1;
        }
        //Increase sun ring transparency when below 200, and decrease sun ring transparency when below 50
        //Only activates every fifth frame
        if(shouldChange) {
            nightClock++;
            if (alpha1 >= 150 || alpha1 <= 50) {
                changeAlpha1 *= -1;
            }
            if (alpha2 >= 200 || alpha2 <= 150) {
                changeAlpha2 *= -1;
            }
            //Change wing direction
            if(bezx1>=0.5 || bezx2>=1.0 || bezy3>=2){
                invert = true;
            }
            else if(bezx1<=0.0 || bezx2<=0.0 || bezy3<=0.0){
                invert = false;
            }
            //Lower wings
            if(invert){
                bezx1-=0.1;
                bezx2-=0.1;
                bezy1-=0.1;
                bezy2-=0.2;
                bezy3-=0.1;
            }
            //Raise wings
            else{
                bezx1+=0.1;
                bezx2+=0.1;
                bezy1+=0.1;
                bezy2+=0.2;
                bezy3+=0.1;
            }
            //Sky color change from orange in morning and evening, to cyan in day, to dark blue in evening.
            //Once it is morning, where it starts, decrease red and increase blue and green until the sky is cyan.
                if(morning){
                    if(skyRed>0){
                        skyRed--;
                    }
                    if(skyGreen<255){
                        skyGreen++;
                    }
                    if(skyBlue<255){
                        skyBlue++;
                    }
                    if(skyGreen==255 && skyBlue==255 && skyRed==0){
                        morning = false;
                        day = true;
                        System.out.println("Day: "+day);
                    }
                }
                //Once it is day, decrease the blue and green and increase red until they sky is orange
                else if(day){
                    if(skyGreen>125){
                        skyGreen--;
                    }
                    if(skyBlue>0){
                        skyBlue--;
                    }
                    if(skyRed<255){
                        skyRed++;
                    }
                    if(skyRed==255 && skyGreen==125 && skyBlue==0){
                        day = false;
                        evening = true;
                        System.out.println("Evening: "+evening);
                    }

                }
                //Once it is evening, decrease the blue and green and red until the sky is nearly black
                else if(evening){
                    if(skyGreen>0){
                        skyGreen--;
                    }
                    if(skyBlue>30){
                        skyBlue--;
                    }
                    if(skyBlue<30){
                        skyBlue++;
                    }
                    if(skyRed>0){
                        skyRed--;
                    }
                    if(skyRed==0 && skyGreen==0 && skyBlue==30){
                        evening = false;
                        night = true;
                        System.out.println("Night: "+night);
                    }
                }
                //Once it hits night, increase red and green until the sky is orange
                //Because of the sun's movement night has to be 7.5 - 8 times longer than every other time.
                else if(night){
                    //Only change values 1/8 as much as the other times of day.
                    if (nightClock%8==0) {
                        if (skyGreen < 125) {
                            skyGreen++;
                        }
                        if (skyRed < 255) {
                            skyRed++;
                        }
                        if (skyBlue > 0) {
                            skyBlue--;
                        }
                        if (skyRed == 255 && skyGreen == 125 && skyBlue == 0) {
                            night = false;
                            morning = true;
                            System.out.println("Morning: " + morning);
                        }
                    }
                }

        }
    }


    private void applyWindowToViewportTransformation(Graphics2D g2,
                                                     double left, double right, double bottom, double top,
                                                     boolean preserveAspect) {
        int width = getWidth();   // The width of this drawing area, in pixels.
        int height = getHeight(); // The height of this drawing area, in pixels.
        if (preserveAspect) {
            // Adjust the limits to match the aspect ratio of the drawing area.
            double displayAspect = Math.abs((double)height / width);
            double requestedAspect = Math.abs(( bottom-top ) / ( right-left ));
            if (displayAspect > requestedAspect) {
                // Expand the viewport vertically.
                double excess = (bottom-top) * (displayAspect/requestedAspect - 1);
                bottom += excess/2;
                top -= excess/2;
            }
            else if (displayAspect < requestedAspect) {
                // Expand the viewport vertically.
                double excess = (right-left) * (requestedAspect/displayAspect - 1);
                right += excess/2;
                left -= excess/2;
            }
        }
        g2.scale( width / (right-left), height / (bottom-top) );
        g2.translate( -left, -top );
        double pixelWidth = Math.abs(( right - left ) / width);
        double pixelHeight = Math.abs(( bottom - top ) / height);
        pixelSize = (float)Math.max(pixelWidth,pixelHeight);
    }
}
