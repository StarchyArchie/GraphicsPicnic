/********************
 * HierarchyScene
 * Author: Christian Duncan
 * Spring 21: CSC345
 *
 * Based off the GraphicsStarter2D.java file from Eck's text.
 * I left several of the comments in place to indicate what each step is doing.
 * 
 * This class illustrates transformations on a scene using Java's
 * Graphics2D class
 ********************/

import java.awt.*;        // import statements to make necessary classes available
import java.awt.geom.*;
import javax.swing.*;

public class HierarchyScene extends JPanel {
    /**
     * This main() routine makes it possible to run the class HierarchyScene
     * as an application.  It simply creates a window that contains a panel
     * of type HierarchyScene.  The program ends when the user closed the
     * window by clicking its close box.
     */
    public static void main(String[] args) {
        JFrame window;
        window = new JFrame("Transformers: Graphics in Disguise!");  // The parameter shows in the window title bar.
        window.setContentPane(new HierarchyScene()); // Show an instance of this class in main window pane
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // End program when window closes.
        window.pack();  // Set window size based on the preferred sizes of its contents.
        window.setResizable(false); // Don't let user resize window.
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        // Center window on screen.
        window.setLocation((screen.width - window.getWidth())/2, (screen.height - window.getHeight())/2);
        window.setVisible(true); // Open the window, making it visible on the screen.
    }
    
    private float pixelSize;  // This is the measure of a pixel in the coordinate system
                              // set up by calling the applyLimits method.  It can be used
                              // for setting line widths, for example.
    
    /**
     * This constructor sets up a HierarchyScene when it is created.  Here, it
     * sets the size of the drawing area.  (The size is set as a "preferred size,"
     * which will be used by the pack() command in the main() routine.)
     */
    public HierarchyScene() {
        setPreferredSize(new Dimension(1000,1000) ); // Set size of drawing area, in pixels.
    }
    
    /**
     * The paintComponent method draws the content of the JPanel.  The parameter
     * is a graphics context that can be used for drawing on the panel.  Note that
     * it is declared to be of type Graphics but is actually of type Graphics2D,
     * which is a subclass of Graphics.
     */
    protected void paintComponent(Graphics g) {
        /* First, create a Graphics2D drawing context for drawing on the panel.
         * (g.create() makes a copy of g, which will draw to the same place as g,
         * but changes to the returned copy will not affect the original.)
         */
        Graphics2D g2 = (Graphics2D) g.create();
        
        /* Turn on antialiasing in this graphics context, for better drawing.
         */
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        /* Fill in the entire drawing area with a black background.
         */
        g2.setPaint(Color.BLACK);
        g2.fillRect(0,0,getWidth(),getHeight()); // From the old graphics API!
        
        /* Here, I set up a new coordinate system on the drawing area, by calling
         * the applyLimits() method that is defined below.  Without this call, I
         * would be using regular pixel coordinates.  This function sets the global
         * variable pixelSize, which I need for stroke widths in the transformed
         * coordinate system.
         */
        
        applyapplyWindowToViewportTransformation(g2, -15, 15, -15, 15, true);
        
        /* Finish by drawing a few shapes as an example.  You can erase the rest of 
         * this subroutine and substitute your own drawing.
         */

        drawScene(g2);
    }

    /**
     * Draw the scene
     */
    private void drawScene(Graphics2D g2) {
        drawCoordinateFrame(g2, 10);  // 10 is the number of "ticks" to show

        // Scene version 1
        AffineTransform cs = g2.getTransform();   // Save current "coordinate system" transform
        g2.scale(1,1);  // No scaling yet, but setting it up to make a tad bigger
        drawMainScene(g2);
    }

    private void drawMainScene(Graphics2D g2) {
        drawHouseWithChimney(g2);

        AffineTransform cs = g2.getTransform();
        g2.setPaint(Color.RED);
        g2.translate(-1, 0);
        g2.scale(0.05, 0.05);
        drawMailbox(g2);
        g2.setTransform(cs);
        
        // A little "bench"
        g2.setPaint(new Color(0, 255, 0));  // Set color to green (or Color.GREEN)
        g2.fill(new Rectangle2D.Double(2, 0.1, 1, 0.4)); // x, y, width, height

        // A "sun"
        g2.setPaint(Color.YELLOW); // Set color to yellow
        g2.fill(new Ellipse2D.Double(4, 4, 2, 2));   // lower left X, lower left Y, width, height
    }

    /**
     * Draw a coordinate frame
     */
    private void drawCoordinateFrame(Graphics2D g2, int length) {
        g2.setPaint(Color.WHITE);
        g2.setStroke( new BasicStroke(2*pixelSize) );
        // Draw y-axis and its tick marks
        g2.draw( new Line2D.Double( 0, 0, 0, length) );   // Y-axis
        for (int i = 1; i <= length; i++)
            g2.draw( new Line2D.Double(-0.2, i, 0.2, i));

        // Draw x-axis and its tick marks
        g2.draw( new Line2D.Double( 0, 0, length, 0) );
        for (int i = 1; i <= length; i++)
            g2.draw( new Line2D.Double(i, -0.2, i, 0.2));
    }

    private void drawMailbox(Graphics2D g2) {
        AffineTransform cs = g2.getTransform();  // Save C.S. state
        g2.fill(new Rectangle2D.Double(0, 0, 1, 5));
        g2.fill(new Rectangle2D.Double(0, 5, 4, 2));
        g2.setTransform(cs); // Restore previous C.S. state
    }
    
    private void drawHouseWithChimney(Graphics2D g2) {
        AffineTransform cs = g2.getTransform();  // Save C.S. state
        drawHouse(g2);
        g2.translate(0.8, 0.8);
        g2.scale(0.1, 0.4);
        drawHouse(g2);
        g2.setTransform(cs); // Restore previous C.S. state
    }
    
    /**
     * Draw a simple house as a closed polygonal path
     */
    private void drawHouse(Graphics2D g2) {
        Path2D poly = new Path2D.Double();
        poly.moveTo(0,0);
        poly.lineTo(0,1);
        poly.lineTo(0.5, 1.5);
        poly.lineTo(1,1);
        poly.lineTo(1,0);
        poly.closePath();
        
        g2.setPaint(new Color(255, 0, 0) ); // red
        g2.fill(poly);
    }
    
    /**
     * Applies a coordinate transform to a Graphics2D graphics context.  The upper
     * left corner of the viewport where the graphics context draws is assumed to
     * be (0,0).  The coordinate transform will make a requested view window visible
     * in the drawing area.  The requested limits might be adjusted to preserve the
     * aspect ratio.
     *     This method sets the value of the global variable pixelSize, which is defined as the
     * maximum of the width of a pixel and the height of a pixel as measured in the
     * coordinate system.  (If the aspect ratio is preserved, then the width and 
     * height will agree.
     * @param g2 The drawing context whose transform will be set.
     * @param left requested x-value at left of drawing area.
     * @param right requested x-value at right of drawing area.
     * @param bottom requested y-value at bottom of drawing area; can be less than
     *     top, which will reverse the orientation of the y-axis to make the positive
     *     direction point upwards.
     * @param top requested y-value at top of drawing area.
     * @param preserveAspect if preserveAspect is false, then the requested view window
     *     rectangle will exactly fill the viewport; if it is true, then the limits will be
     *     expanded in one direction, horizontally or vertically, if necessary, to make the
     *     aspect ratio of the view window match the aspect ratio of the viewport.
     *     Note that when preserveAspect is false, the units of measure in the horizontal 
     *     and vertical directions will be different.
     */
    private void applyapplyWindowToViewportTransformation(Graphics2D g2,
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
