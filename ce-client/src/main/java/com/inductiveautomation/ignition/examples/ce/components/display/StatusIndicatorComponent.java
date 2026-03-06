package com.inductiveautomation.ignition.examples.ce.components.display;

import javax.swing.*;
import java.awt.*;

public class StatusIndicatorComponent extends JComponent {

    public static final int EFFECT_NONE = 0;
    public static final int EFFECT_BLINK = 1;
    public static final int EFFECT_PULSE = 2;

    public static final int TEXT_RIGHT = 0;
    public static final int TEXT_LEFT = 1;

    private static final int RIPPLE_COUNT = 1;

    private String text = "Running";
    private boolean on = true;

    private Color statusColor = new Color(56, 174, 78);
    private Color offColor = new Color(180, 180, 180);

    private int effectMode = EFFECT_NONE;
    private int textSide = TEXT_RIGHT;

    // milliseconds
    private int effectSpeed = 900;

    private boolean blinkState = true;
    private float pulseProgress = 0f;

    private final Timer effectTimer;

    public StatusIndicatorComponent() {
        setPreferredSize(new Dimension(180, 40));
        setMinimumSize(new Dimension(120, 30));
        setForeground(Color.BLACK);
        setFont(new Font("Dialog", Font.PLAIN, 12));
        setOpaque(false);

        effectTimer = new Timer(Math.max(20, effectSpeed / 30), e -> {
            if (effectMode == EFFECT_BLINK) {
                blinkState = !blinkState;
            } else if (effectMode == EFFECT_PULSE) {
                pulseProgress += 0.025f;
                if (pulseProgress > 1f) {
                    pulseProgress -= 1f;
                }
            }
            repaint();
        });

        updateEffectTimer();
    }

    // ------------------------
    // Text
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
    // On
    // ------------------------

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        boolean old = this.on;
        if (old == on) {
            return;
        }

        this.on = on;
        firePropertyChange("on", old, on);

        updateEffectTimer();
        repaint();
    }

    // ------------------------
    // Effect Mode
    // ------------------------

    public int getEffectMode() {
        return effectMode;
    }

    public void setEffectMode(int effectMode) {
        int old = this.effectMode;
        if (old == effectMode) {
            return;
        }

        this.effectMode = effectMode;
        firePropertyChange("effectMode", old, effectMode);

        blinkState = true;
        pulseProgress = 0f;
        updateEffectTimer();
        repaint();
    }

    // ------------------------
    // Effect Speed
    // ------------------------

    public int getEffectSpeed() {
        return effectSpeed;
    }

    public void setEffectSpeed(int effectSpeed) {
        int old = this.effectSpeed;
        int newValue = Math.max(100, effectSpeed);
        if (old == newValue) {
            return;
        }

        this.effectSpeed = newValue;
        firePropertyChange("effectSpeed", old, this.effectSpeed);

        updateEffectTimer();
        repaint();
    }

    private void updateEffectTimer() {
        int delay;

        if (effectMode == EFFECT_BLINK) {
            delay = Math.max(80, effectSpeed);
        } else {
            delay = Math.max(20, effectSpeed / 30);
        }

        effectTimer.setDelay(delay);

        if (on && (effectMode == EFFECT_BLINK || effectMode == EFFECT_PULSE)) {
            if (!effectTimer.isRunning()) {
                effectTimer.start();
            }
        } else {
            effectTimer.stop();
            blinkState = true;
            pulseProgress = 0f;
        }
    }

    // ------------------------
    // Status Color
    // ------------------------

    public Color getStatusColor() {
        return statusColor;
    }

    public void setStatusColor(Color statusColor) {
        Color old = this.statusColor;
        if (old == null ? statusColor == null : old.equals(statusColor)) {
            return;
        }

        this.statusColor = statusColor;
        firePropertyChange("statusColor", old, statusColor);
        repaint();
    }

    // ------------------------
    // Off Color
    // ------------------------

    public Color getOffColor() {
        return offColor;
    }

    public void setOffColor(Color offColor) {
        Color old = this.offColor;
        if (old == null ? offColor == null : old.equals(offColor)) {
            return;
        }

        this.offColor = offColor;
        firePropertyChange("offColor", old, offColor);
        repaint();
    }

    // ------------------------
    // Text Side
    // ------------------------

    public int getTextSide() {
        return textSide;
    }

    public void setTextSide(int textSide) {
        int old = this.textSide;
        if (old == textSide) {
            return;
        }

        this.textSide = textSide;
        firePropertyChange("textSide", old, textSide);
        repaint();
    }

    // ------------------------
    // Appearance
    // ------------------------

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
    // Size
    // ------------------------

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(180, 40);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(120, 30);
    }

    // ------------------------
    // Paint
    // ------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        FontMetrics fm = g2.getFontMetrics(getFont());
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();

        // Layout rules
        int verticalPadding = 5; // 10 total
        int remainingHeight = Math.max(4, height - (verticalPadding * 2));

        int indicatorDiameter = Math.max(4, remainingHeight / 2);
        int pulseOffset = Math.max(2, remainingHeight / 2);

        int pulseBoundsSize = indicatorDiameter + (pulseOffset * 2);
        int gapToText = 5;

        int pulseY = (height - pulseBoundsSize) / 2;
        int indicatorY = (height - indicatorDiameter) / 2;

        int pulseX;
        int indicatorX;
        int textX;

        if (textSide == TEXT_LEFT) {
            textX = 4;
            pulseX = textX + textWidth + gapToText;
            indicatorX = pulseX + pulseOffset;
        } else {
            pulseX = 4;
            indicatorX = pulseX + pulseOffset;
            textX = pulseX + pulseBoundsSize + gapToText;
        }

        int indicatorCenterX = indicatorX + indicatorDiameter / 2;
        int indicatorCenterY = indicatorY + indicatorDiameter / 2;

        // Multiple ripple pulse effect
        if (on && effectMode == EFFECT_PULSE) {
            for (int i = 0; i < RIPPLE_COUNT; i++) {
                float phaseOffset = (float) i / RIPPLE_COUNT;
                float rippleProgress = pulseProgress - phaseOffset;

                if (rippleProgress < 0f) {
                    rippleProgress += 1f;
                }

                int rippleDiameter = (int) (
                        indicatorDiameter + ((pulseBoundsSize - indicatorDiameter) * rippleProgress)
                );

                int rippleX = indicatorCenterX - rippleDiameter / 2;
                int rippleY = indicatorCenterY - rippleDiameter / 2;

                int alpha = (int) (90 * (1f - rippleProgress));
                alpha = Math.max(0, Math.min(255, alpha));

                if (alpha > 0) {
                    g2.setColor(new Color(
                            statusColor.getRed(),
                            statusColor.getGreen(),
                            statusColor.getBlue(),
                            alpha
                    ));
                    g2.fillOval(rippleX, rippleY, rippleDiameter, rippleDiameter);
                }
            }
        }

        // Main indicator
        Color drawColor;
        if (on) {
            if (effectMode == EFFECT_BLINK && !blinkState) {
                drawColor = offColor;
            } else {
                drawColor = statusColor;
            }
        } else {
            drawColor = offColor;
        }

        g2.setColor(drawColor);
        g2.fillOval(indicatorX, indicatorY, indicatorDiameter, indicatorDiameter);

        // Text
        g2.setColor(getForeground());
        g2.setFont(getFont());

        int textY = (height - textHeight) / 2 + fm.getAscent();
        g2.drawString(text, textX, textY);

        g2.dispose();
    }
}