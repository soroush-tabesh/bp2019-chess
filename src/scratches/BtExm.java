package scratches;

import javax.swing.*;
import java.awt.*;

public class BtExm {
    //Declaration of all calculator's components.
    JPanel windowContent;
    JTextField displayField;
    JButton buttons[];
    JButton buttonPoint;
    JButton buttonAdd;
    JButton buttonEqual;
    JPanel pl;

    //Constructor creates the components in memory and adds the to the frame using combination of Borderlayout.
    BtExm() {
        windowContent = new JPanel();
        buttons = new JButton[10];

        // Set the layout manager for this panel
        BorderLayout bl = new BorderLayout();
        windowContent.setLayout(bl);

        //Create the display field and place it in the North area of the window
        displayField = new JTextField(30);
        windowContent.add("North", displayField);

        //Create button field and place it in the North area of the window
        for (int i = 0; i < 10; i++) {
            buttons[i] = new NBT(String.valueOf(i));
            buttons[i].setIcon(new ImageIcon("images/image.jpeg"));

        }

        buttonAdd = new JButton("+");
        buttonPoint = new JButton(".");
        buttonEqual = new JButton("=");

        //Create the panel with the GridLayout that will contain 12 buttons - 10 numeric ones, and button with the points
        //and the equal sign.
        pl = new JPanel();
        GridLayout gl = new GridLayout(4, 3);
        gl.setHgap(0);
        gl.setVgap(0);
        pl.setLayout(gl);
        //Add window controls to the panel pl.

        for (int i = 0; i < 10; i++) {
            pl.add(buttons[i]);
        }
        pl.add(buttonAdd);
        pl.add(buttonPoint);
        pl.add(buttonEqual);

        //Add the panel pl to the center area of the window
        windowContent.add("Center", pl);
        //Create the frame and set its content pane
        JFrame frame = new JFrame("Calculator");
        frame.setContentPane(windowContent);
        //set the size of the window to be big enough to accomodate all controls.
        frame.pack();
        //Finnaly, display the window
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            // handle exception
        }
        BtExm calc = new BtExm();
    }
}