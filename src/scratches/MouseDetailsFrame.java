package scratches;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseDetailsFrame extends JFrame {
    private final JLabel stat;
    private String det = "";

    public MouseDetailsFrame() {
        super("title");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 400);
        stat = new JLabel("CLick");
        JPanel jp = new JPanel();
        jp.setPreferredSize(new Dimension(100, 100));
        add(jp, BorderLayout.EAST);
        add(stat, BorderLayout.WEST);
        pack();
        jp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                stat.setText(e.getX() + " mv " + e.getY());
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                stat.setText(e.getX() + " cl " + e.getY());
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                stat.setText(e.getX() + " pr " + e.getY());
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                stat.setText(e.getX() + " entr " + e.getY());
                pack();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                stat.setText(e.getX() + " ex " + e.getY());
                pack();
            }
        });
    }

    public static void main(String[] args) {
        MouseDetailsFrame mdf = new MouseDetailsFrame();
        mdf.setVisible(true);
    }
}
