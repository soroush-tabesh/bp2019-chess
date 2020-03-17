package scratches;

import javax.swing.*;
import java.awt.*;

public class NBT extends JButton {
    public NBT(String text) {
        super(text);
    }

    @Override
    protected void paintComponent(Graphics g) {
        // g.fillRect(0,0,(int)g.getClipBounds().getWidth()+10,(int)g.getClipBounds().getHeight()+10);
        super.paintComponent(g);
    }
}
