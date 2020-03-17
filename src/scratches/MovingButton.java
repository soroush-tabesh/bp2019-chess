package scratches;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Chet
 */
public class MovingButton extends JButton implements ActionListener {

    static final int MAX_Y = 100;
    static JFrame f;
    static JPanel checkerboard;
    static JPanel glass;
    Timer timer;                        // for later start/stop actions
    int animationDuration = 2000;   // each animation will take 2 seconds
    long animStartTime;     // start time for each animation
    int translateY = 0;                 // y location of the button

    /**
     * Creates a new instance of TranslucentButton
     */
    public MovingButton(String label) {
        super(label);
        setOpaque(false);
        timer = new Timer(30, this);
        addActionListener(this);
    }

    private static void createAndShowGUI() {
        f = new JFrame("Moving Button");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(300, 300);
        f.setGlassPane(glass = new JPanel());
        checkerboard = new Checkerboard();
        checkerboard.add(new MovingButton("Start Animation"));
        f.add(checkerboard);
        f.setVisible(true);
    }

    public static void main(String args[]) {
        Runnable doCreateAndShowGUI = new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        };
        SwingUtilities.invokeLater(doCreateAndShowGUI);
    }

    @Override
    protected void paintComponent(Graphics g) {
        glass.revalidate();
        g = glass.getGraphics();
        //g.translate(100,100);
        g.translate(0, translateY);
        super.paintComponent(g);
        //g.translate(0,-translateY);
        //g.translate(-100,-100);
    }

    /**
     * Displays our component in the location (0, translateY). Note that
     * this changes only the rendering location of the button, not the
     * physical location of it. Note, also, that rendering into g will
     * be clipped to the physical location of the button, so the button will
     * disappear as it moves away from that location.
     */

    public void paint(Graphics g) {
        glass.revalidate();
        g = glass.getGraphics();
        //g.translate(100,100);
        g.translate(0, translateY);
        super.paint(g);
        //g.translate(0,-translateY);
        //g.translate(-100,-100);
    }

    /**
     * This method handles both button clicks, which start/stop the animation,
     * and Swing Timer events.
     */
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource().equals(this)) {
            // button click
            if (!timer.isRunning()) {
                animStartTime = System.nanoTime() / 1000000;
                this.setText("Stop Animation");
                timer.start();
            } else {
                timer.stop();
                this.setText("Start Animation");
                // reset translation to 0
                translateY = 0;
            }
        } else {
            // Timer event
            // calculate the elapsed fraction
            long currentTime = System.nanoTime() / 1000000;
            long totalTime = currentTime - animStartTime;
            if (totalTime > animationDuration) {
                animStartTime = currentTime;
            }
            float fraction = (float) totalTime / animationDuration;
            fraction = Math.min(1.0f, fraction);
            // This calculation will cause translateY to go from 0 to MAX_Y
            // as the fraction goes from 0 to 1
            if (fraction < .5f) {
                translateY = (int) (MAX_Y * (2 * fraction));
            } else {
                translateY = (int) (MAX_Y * (2 * (1 - fraction)));
            }
            // redisplay our component with the new location
            repaint();
        }
    }

    /**
     * Paints a checkerboard background
     */
    private static class Checkerboard extends JPanel {
        static final int CHECKER_SIZE = 60;
        private static final int DIVISIONS = 10;

        public void paintComponent(Graphics g) {
            g.setColor(Color.white);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.BLACK);
            for (int stripeX = 0; stripeX < getWidth(); stripeX += CHECKER_SIZE) {
                for (int y = 0, row = 0; y < getHeight(); y += CHECKER_SIZE / 2, ++row) {
                    int x = (row % 2 == 0) ? stripeX : (stripeX + CHECKER_SIZE / 2);
                    g.fillRect(x, y, CHECKER_SIZE / 2, CHECKER_SIZE / 2);
                }
            }
        }
    }
}
