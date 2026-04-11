package attendance;

import javax.swing.border.AbstractBorder;
import java.awt.*;

// Custom border with rounded corners
class RoundedBorder extends AbstractBorder {
    private final int radius;
    private final Color color;

    RoundedBorder(int radius, Color color) {
        this.radius = radius;
        this.color = color;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(x, y, w-1, h-1, radius, radius);
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(4, 4, 4, 4);
    }
}