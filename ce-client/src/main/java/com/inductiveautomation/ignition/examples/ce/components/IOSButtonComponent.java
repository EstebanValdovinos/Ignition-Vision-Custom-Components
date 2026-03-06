package com.inductiveautomation.ignition.examples.ce.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class IOSButtonComponent extends JComponent {

    private String text = "Submit";
    private boolean pressed = false;
    private int cornerRadius = -1;

    public IOSButtonComponent() {
        setPreferredSize(new Dimension(140, 36));
        setMinimumSize(new Dimension(80, 28));
        setBackground(new Color(0, 122, 255));
        setForeground(Color.WHITE);
        setFont(new Font("Dialog", Font.BOLD, 14));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFocusable(true);
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled() && SwingUtilities.isLeftMouseButton(e)) {
                    pressed = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (pressed) {
                    pressed = false;
                    repaint();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (isEnabled() && SwingUtilities.isLeftMouseButton(e)) {
                    requestFocusInWindow();
                    firePropertyChange("buttonClicked", false, true);
                }
            }
        });
    }

    // ------------------------
    // Text property
    // ------------------------

    public String getText() {
        return text;
    }

    public void setText(String text) {
        String old = this.text;
        if (old == null ? text == null : old.equals(text)) {
            return;
        }

        this.text = text;
        firePropertyChange("text", old, text);
        repaint();
    }

    // ------------------------
    // Enabled property
    // ------------------------

    @Override
    public void setEnabled(boolean enabled) {
        boolean old = isEnabled();
        super.setEnabled(enabled);

        firePropertyChange("enabled", old, enabled);

        if (enabled) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
            pressed = false;
        }

        repaint();
    }

    // ------------------------
    // Appearance
    // ------------------------

    @Override
    public void setBackground(Color bg) {
        Color old = getBackground();
        super.setBackground(bg);
        firePropertyChange("background", old, bg);
        repaint();
    }

    @Override
    public void setForeground(Color fg) {
        Color old = getForeground();
        super.setForeground(fg);
        firePropertyChange("foreground", old, fg);
        repaint();
    }

    @Override
    public void setFont(Font font) {
        Font old = getFont();
        super.setFont(font);
        firePropertyChange("font", old, font);
        revalidate();
        repaint();
    }

    // ------------------------
    // Corner radius
    // ------------------------

    public int getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(int cornerRadius) {
        int old = this.cornerRadius;
        if (old == cornerRadius) {
            return;
        }

        this.cornerRadius = Math.max(-1, cornerRadius);
        firePropertyChange("cornerRadius", old, this.cornerRadius);
        repaint();
    }

    // ------------------------
    // Size
    // ------------------------

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(140, 36);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(80, 28);
    }

    // ------------------------
    // Painting
    // ------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        Color bg = getBackground();
        Color fg = getForeground();

        if (!isEnabled()) {
            bg = new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 120);
            fg = new Color(180, 180, 180);
        } else if (pressed) {
            bg = bg.darker();
        }

        g2.setColor(bg);
        int radius = (cornerRadius <= 0) ? height : cornerRadius;
        g2.fillRoundRect(0, 0, width, height, radius, radius);

        if (isFocusOwner()) {
            g2.setColor(new Color(0, 120, 215));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(1, 1, width - 2, height - 2, radius, radius);
        }

        g2.setColor(fg);
        g2.setFont(getFont());

        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textX = (width - textWidth) / 2;
        int textY = (height - fm.getHeight()) / 2 + fm.getAscent();

        g2.drawString(text, textX, textY);

        g2.dispose();
    }
}