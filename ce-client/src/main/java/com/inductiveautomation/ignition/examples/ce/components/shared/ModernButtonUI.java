package com.inductiveautomation.ignition.examples.ce.components.shared;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

public class ModernButtonUI {

    private boolean hover = false;
    private boolean pressed = false;

    public void install(JComponent c) {
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (c.isEnabled()) {
                    hover = true;
                    c.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                pressed = false;
                c.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (c.isEnabled() && SwingUtilities.isLeftMouseButton(e)) {
                    pressed = true;
                    c.requestFocusInWindow();
                    c.repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (pressed) {
                    pressed = false;
                    c.repaint();
                }
            }
        };

        c.addMouseListener(mouseHandler);

        c.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                c.repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                pressed = false;
                c.repaint();
            }
        });
    }

    public boolean isHover() {
        return hover;
    }

    public boolean isPressed() {
        return pressed;
    }

    public int getContentOffsetY() {
        return pressed ? 1 : 0;
    }

    public void paintEffects(Graphics2D g2,
                             JComponent c,
                             Shape shape,
                             Color strokeColor,
                             float strokeWidth,
                             boolean paintShadow,
                             boolean paintFocusRing) {

        AffineTransform oldTx = g2.getTransform();
        Stroke oldStroke = g2.getStroke();

        try {
            if (paintShadow && c.isEnabled() && !pressed) {
                int shadowOffset = hover ? 2 : 1;
                int shadowAlpha = hover ? 36 : 22;

                g2.translate(0, shadowOffset);
                g2.setColor(new Color(0, 0, 0, shadowAlpha));
                g2.fill(shape);
                g2.setTransform(oldTx);
            }

            if (hover && c.isEnabled() && !pressed) {
                g2.setColor(new Color(255, 255, 255, 18));
                g2.fill(shape);
            }

            if (pressed && c.isEnabled()) {
                g2.setColor(new Color(0, 0, 0, 28));
                g2.fill(shape);
            }

            if (strokeWidth > 0f && strokeColor != null) {
                g2.setStroke(new BasicStroke(strokeWidth));
                g2.setColor(strokeColor);
                g2.draw(shape);
            }

            if (paintFocusRing && c.isFocusOwner() && c.isEnabled()) {
                g2.setStroke(new BasicStroke(2f));
                g2.setColor(new Color(64, 156, 255, 180));
                g2.draw(shape);
            }
        } finally {
            g2.setTransform(oldTx);
            g2.setStroke(oldStroke);
        }
    }
}