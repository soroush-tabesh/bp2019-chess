package scratches;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Asghar {
    private JPanel panel;
    private JButton button1;
    private JRadioButton radioButton1;

    public Asghar() {
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public static void main(String[] args) {
        JFrame jf = new JFrame("KEKE");
        jf.setContentPane(new Asghar().panel);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.pack();
        jf.setVisible(true);
    }

}
