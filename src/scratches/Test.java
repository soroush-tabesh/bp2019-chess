package scratches;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class Test extends JFrame {
    static int W, H;

    public Test(String title) throws HeadlessException {
        super(title);
    }

    public static void main(String[] args) {
        Test frame = new Test("Chess");
        //frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        //frame.setUndecorated(true);
        frame.setBackground(new Color(238f / 255f, 238f / 255f, 238f / 255f, 1));
        //frame.setOpacity(0.5f);
        //frame.add(new JLabel("Hello"),BorderLayout.WEST);
        frame.add(new AboutComponent());
        frame.pack();
        //size the window
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                Rectangle b = e.getComponent().getBounds();
                e.getComponent().setBounds(b.x, b.y, b.width, b.width * H / W);
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {
                Rectangle b = e.getComponent().getBounds();
                H = b.height;
                W = b.width;
            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });
    }

    private static class AboutComponent extends JComponent {
        public AboutComponent() {
            super();
            setPreferredSize(new Dimension(getFrames()[0].getWidth(), getFrames()[0].getHeight()));
        }

        public void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics;
            //draw chess square
            g.setPaint(Color.white);
            g.fillRect(0, 0, Math.min(getSize().width, getSize().height), Math.min(getSize().width, getSize().height));
            //assuming the square is 800*800 px in size
            g.scale(Math.min(getSize().width, getSize().height) / 800f, Math.min(getSize().width, getSize().height) / 800f);
            //draw chess cells
            g.setPaint(new Color(108f / 255f, 107f / 255f, 106f / 255f, 1f));
            g.fillRect(100, 100, 100, 100);
            g.setPaint(new Color(243f / 255f, 163f / 255f, 60f / 255f, 1f));
            g.fillRect(200, 100, 100, 100);
            //chess piece
            g.setPaint(Color.white);
            g.setFont(new Font("TimesRoman", Font.PLAIN, 72));
            g.drawString("♕", 120, 170);
            //borderline
            g.setPaint(new Color(86f / 255f, 84f / 255f, 85f / 255f, 1f));
            g.fillRect(200, 100, 1, 100);
            //set back the scale
            g.scale(1, 1);
        }
    }

}
