package attendance;

import javax.swing.*;
import java.awt.*;

// Custom painted clipboard/checklist icon for the login screen
class AttendanceIcon extends JComponent {

    AttendanceIcon(int size) {
        setPreferredSize(new Dimension(size, size));
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        int w = getWidth(), h = getHeight();
        int pad = w / 8, arc = w / 5;

        // Clipboard body
        g2.setColor(new Color(0x4361EE));
        g2.fillRoundRect(pad, pad + h/10, w - pad*2, h - pad - h/10 - pad/2, arc, arc);

        // Clipboard header bar
        g2.setColor(new Color(0x3451CC));
        g2.fillRoundRect(pad, pad + h/10, w - pad*2, h/6, arc, arc);
        g2.fillRect(pad, pad + h/10 + h/12, w - pad*2, h/12);

        // Clip at top
        g2.setColor(new Color(0x2A3EAA));
        int clipW = w/3, clipH = h/8, clipX = (w - w/3)/2;
        g2.fillRoundRect(clipX, pad/2, clipW, clipH + pad/2, arc/2, arc/2);

        // Clip hole
        g2.setColor(new Color(0x4361EE));
        int holeW = clipW/2, holeH = clipH/2;
        g2.fillRoundRect(clipX + (clipW-holeW)/2, pad/2 + clipH/4, holeW, holeH, arc/4, arc/4);

        // Checklist lines
        g2.setStroke(new BasicStroke(Math.max(1.5f, w/28f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int lineLeft = pad+pad, lineRight = w-pad-pad;
        int lineStart = pad + h/10 + h/5 + pad/2, lineGap = h/7;
        int cx = lineLeft, cy = lineStart, chk = h/14;

        // Row 1 checkmark
        g2.setColor(new Color(0xC8FFE0));
        g2.drawLine(cx, cy+chk/2, cx+chk/2, cy+chk);
        g2.drawLine(cx+chk/2, cy+chk, cx+chk, cy);
        g2.setColor(Color.WHITE);
        g2.drawLine(cx+chk+pad/2, cy+chk/2, lineRight, cy+chk/2);

        // Row 2 checkmark
        cy += lineGap;
        g2.setColor(new Color(0xC8FFE0));
        g2.drawLine(cx, cy+chk/2, cx+chk/2, cy+chk);
        g2.drawLine(cx+chk/2, cy+chk, cx+chk, cy);
        g2.setColor(Color.WHITE);
        g2.drawLine(cx+chk+pad/2, cy+chk/2, lineRight, cy+chk/2);

        // Row 3 (empty line)
        cy += lineGap;
        g2.setColor(new Color(0x7B9FFF));
        g2.drawLine(lineLeft, cy+chk/2, lineRight-lineRight/4, cy+chk/2);

        g2.dispose();
    }
}