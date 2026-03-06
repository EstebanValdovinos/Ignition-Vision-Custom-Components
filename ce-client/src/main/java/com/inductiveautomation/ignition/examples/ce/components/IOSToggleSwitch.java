package com.inductiveautomation.ignition.examples.ce.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class IOSToggleSwitch extends JComponent {

    private boolean selected = false;
    private float animationProgress = 0f;
    private final Timer animationTimer;

    private Color trackOnColor = new Color(52, 199, 89);
    private Color trackOffColor = new Color(209, 209, 214);

    private final Color KNOB_COLOR = Color.WHITE;

    public IOSToggleSwitch() {
        setPreferredSize(new Dimension(50, 28));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFocusable(true);

        animationTimer = new Timer(10, e -> animate());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isEnabled()) {
                    requestFocusInWindow();
                    toggle();
                }
            }
        });

        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE && isEnabled()) {
                    toggle();
                }
            }
        });
    }

    // ------------------------
    // Selected property
    // ------------------------

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        boolean oldValue = this.selected;
        if (oldValue == selected) {
            return;
        }

        this.selected = selected;

        // Vision bindings rely on Swing/JComponent property change events
        firePropertyChange("selected", oldValue, selected);

        if (!animationTimer.isRunning()) {
            animationTimer.start();
        }

        repaint();
    }

    private void toggle() {
        setSelected(!selected);
    }

    // ------------------------
    // Enabled property
    // ------------------------

    @Override
    public void setEnabled(boolean enabled) {
        boolean old = isEnabled();
        super.setEnabled(enabled);

        firePropertyChange("enabled", old, enabled);

        // Cursor behavior: hand when enabled, default when disabled
        if (enabled) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }

        repaint();
    }

    // ------------------------
    // Animation
    // ------------------------

    private void animate() {
        float speed = 0.1f;

        if (selected) {
            animationProgress = Math.min(1f, animationProgress + speed);
        } else {
            animationProgress = Math.max(0f, animationProgress - speed);
        }

        if (animationProgress == 0f || animationProgress == 1f) {
            animationTimer.stop();
        }

        repaint();
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

        // Track color based on animation (ON/OFF blend)
        Color baseTrackColor = blendColors(trackOffColor, trackOnColor, animationProgress);

        // If disabled: keep the same hue but make it translucent (muted/opaque look)
        if (!isEnabled()) {
            baseTrackColor = new Color(
                    baseTrackColor.getRed(),
                    baseTrackColor.getGreen(),
                    baseTrackColor.getBlue(),
                    120
            );
        }

        g2.setColor(baseTrackColor);
        g2.fillRoundRect(0, 0, width, height, height, height);

        // Focus ring
        if (isFocusOwner()) {
            g2.setColor(new Color(0, 120, 215));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(1, 1, width - 2, height - 2, height, height);
        }

        // Knob position
        int knobSize = height - 4;
        int x = (int) (2 + animationProgress * (width - knobSize - 4));

        // Knob color (slightly dim when disabled)
        if (!isEnabled()) {
            g2.setColor(new Color(230, 230, 230));
        } else {
            g2.setColor(KNOB_COLOR);
        }

        g2.fillOval(x, 2, knobSize, knobSize);

        g2.dispose();
    }

    // ------------------------
    // Size
    // ------------------------

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(50, 28);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(40, 22);
    }

    // ------------------------
    // Track colors
    // ------------------------

    public Color getTrackOnColor() {
        return trackOnColor;
    }

    public void setTrackOnColor(Color trackOnColor) {
        Color old = this.trackOnColor;
        if (old == null ? trackOnColor == null : old.equals(trackOnColor)) {
            return;
        }
        this.trackOnColor = trackOnColor;

        firePropertyChange("trackOnColor", old, trackOnColor);
        repaint();
    }

    public Color getTrackOffColor() {
        return trackOffColor;
    }

    public void setTrackOffColor(Color trackOffColor) {
        Color old = this.trackOffColor;
        if (old == null ? trackOffColor == null : old.equals(trackOffColor)) {
            return;
        }
        this.trackOffColor = trackOffColor;

        firePropertyChange("trackOffColor", old, trackOffColor);
        repaint();
    }

    // ------------------------
    // Utility
    // ------------------------

    private Color blendColors(Color c1, Color c2, float ratio) {
        int r = (int) (c1.getRed() + ratio * (c2.getRed() - c1.getRed()));
        int g = (int) (c1.getGreen() + ratio * (c2.getGreen() - c1.getGreen()));
        int b = (int) (c1.getBlue() + ratio * (c2.getBlue() - c1.getBlue()));
        return new Color(r, g, b);
    }
}