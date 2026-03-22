package com.inductiveautomation.ignition.examples.ce.components.display;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class CardComponent extends JComponent {

    private Color shadowColor = new Color(213,213,213);
    private int cornerRadius = 18;
    private double angle = 0.0;
    private int elevation = 20;
    private float shadowOpacity = 0.15f;

    private Color borderColor = new Color(187, 187, 187);
    private float borderWidth = 0.5f;
    private boolean showShadow = true;

    public CardComponent() {
        setPreferredSize(new Dimension(225, 300));
        setMinimumSize(new Dimension(80, 80));
        setBackground(new Color(255, 255, 255));
        setOpaque(false);
    }

    // ------------------------
    // Properties
    // ------------------------

    public Color getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(Color shadowColor) {
        Color old = this.shadowColor;
        this.shadowColor = shadowColor != null ? shadowColor : new Color(213,213,213);
        firePropertyChange("shadowColor", old, this.shadowColor);
        repaint();
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(int cornerRadius) {
        int old = this.cornerRadius;
        this.cornerRadius = Math.max(0, cornerRadius);
        firePropertyChange("cornerRadius", old, this.cornerRadius);
        repaint();
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        double old = this.angle;
        this.angle = angle;
        firePropertyChange("angle", old, this.angle);
        repaint();
    }

    public int getElevation() {
        return elevation;
    }

    public void setElevation(int elevation) {
        int old = this.elevation;
        this.elevation = Math.max(0, elevation);
        firePropertyChange("elevation", old, this.elevation);
        repaint();
    }

    public float getShadowOpacity() {
        return shadowOpacity;
    }

    public void setShadowOpacity(float shadowOpacity) {
        float old = this.shadowOpacity;
        this.shadowOpacity = clamp(shadowOpacity, 0f, 1f);
        firePropertyChange("shadowOpacity", old, this.shadowOpacity);
        repaint();
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        Color old = this.borderColor;
        this.borderColor = borderColor != null ? borderColor : new Color(187, 187, 187);
        firePropertyChange("borderColor", old, this.borderColor);
        repaint();
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(float borderWidth) {
        float old = this.borderWidth;
        this.borderWidth = Math.max(0f, borderWidth);
        firePropertyChange("borderWidth", old, this.borderWidth);
        repaint();
    }

    public boolean isShowShadow() {
        return showShadow;
    }

    public void setShowShadow(boolean showShadow) {
        boolean old = this.showShadow;
        this.showShadow = showShadow;
        firePropertyChange("showShadow", old, this.showShadow);
        repaint();
    }

    @Override
    public void setBackground(Color bg) {
        Color old = getBackground();
        super.setBackground(bg != null ? bg : new Color(255, 255, 255));
        firePropertyChange("background", old, getBackground());
        repaint();
    }

    // ------------------------
    // Painting
    // ------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

            float w = getWidth();
            float h = getHeight();

            if (w <= 1 || h <= 1) {
                return;
            }

            // Space reserved for shadow and border
            float shadowPad = showShadow ? Math.max(0, elevation) : 0;
            float outerPad = Math.max(shadowPad, borderWidth / 2f);

            // Main card box centered inside the component
            float boxX = outerPad;
            float boxY = outerPad;
            float boxW = w - (outerPad * 2f);
            float boxH = h - (outerPad * 2f);

            if (boxW < 1f) boxW = 1f;
            if (boxH < 1f) boxH = 1f;

            if (showShadow && elevation > 0 && shadowOpacity > 0f) {
                paintVanishingShadow(g2, boxX, boxY, boxW, boxH, cornerRadius);
            }

            float halfStroke = borderWidth > 0f ? borderWidth / 2f : 0f;

            Shape cardShape = new RoundRectangle2D.Float(
                    boxX + halfStroke,
                    boxY + halfStroke,
                    boxW - borderWidth,
                    boxH - borderWidth,
                    cornerRadius,
                    cornerRadius
            );

            g2.setColor(getBackground());
            g2.fill(cardShape);

            if (borderWidth > 0f) {
                g2.setStroke(new BasicStroke(borderWidth));
                g2.setColor(borderColor);
                g2.draw(cardShape);
            }

        } finally {
            g2.dispose();
        }
    }

    /**
     * Paints a true "vanishing" shadow using concentric stroked rounded rectangles,
     * based on the user's paintable-canvas approach.
     */
    private void paintVanishingShadow(Graphics2D g2, float boxX, float boxY, float boxW, float boxH, int radius) {
        int e = Math.max(1, elevation);

        // Convert shadowOpacity (0..1) to an edge intensity similar to your script.
        // Recommended useful range ends up around 15..90.
        float maxOpacity = 50f * (shadowOpacity / 0.12f);
        maxOpacity = clamp(maxOpacity, 0f, 120f);

        // Directional bias from angle.
        // 0 means centered shadow.
        double radians = Math.toRadians(angle);
        float dirX = (Math.abs(angle) < 0.0001) ? 0f : (float) Math.cos(radians);
        float dirY = (Math.abs(angle) < 0.0001) ? 0f : (float) Math.sin(radians);

        // Use stroked outlines like your canvas script.
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (int i = e; i >= 1; i--) {
            // progress: 0 at outermost, ~1 near card edge
            float progress = 1f - ((float) i / (float) e);

            // Quadratic falloff = vanishing effect
            int alpha = Math.round(maxOpacity * progress * progress);
            alpha = Math.max(0, Math.min(255, alpha));

            if (alpha <= 0) {
                continue;
            }

            // Directional offset grows slightly more on outer layers
            float offsetScale = ((float) i / (float) e);
            float offsetX = dirX * offsetScale * e * 0.6f;
            float offsetY = dirY * offsetScale * e * 0.6f;

            float layerX = boxX - i + offsetX;
            float layerY = boxY - i + offsetY;
            float layerW = boxW + (i * 2f);
            float layerH = boxH + (i * 2f);
            float layerRadius = radius + i;

            g2.setColor(new Color(
                    shadowColor.getRed(),
                    shadowColor.getGreen(),
                    shadowColor.getBlue(),
                    alpha
            ));

            g2.draw(new RoundRectangle2D.Float(
                    layerX,
                    layerY,
                    layerW,
                    layerH,
                    layerRadius,
                    layerRadius
            ));
        }
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}