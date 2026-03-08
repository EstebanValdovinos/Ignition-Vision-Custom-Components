package com.inductiveautomation.ignition.examples.ce.components.display;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

public class ThermometerIndicatorComponent extends JComponent {

    public static final int UNIT_CELSIUS = 0;
    public static final int UNIT_FAHRENHEIT = 1;

    private static final int DEFAULT_SHADOW_SIZE = 8;

    private int bulbSize = 30;
    private int fineTicks = 3;
    private int majorTicks = 6;
    private int gap = 3;

    private Color highColor = new Color(240, 34, 11, 145);
    private Color lowColor = new Color(105, 162, 247);

    private double maxValue = 100.0;
    private double minValue = 0.0;
    private float shadowOpacity = 0.15f;
    private boolean showScale = true;
    private int unit = UNIT_CELSIUS;
    private boolean showTooltip = true;
    private double value = 65.0;

    public ThermometerIndicatorComponent() {
        setPreferredSize(new Dimension(130, 230));
        setMinimumSize(new Dimension(70, 140));
        setOpaque(false);
        setFont(new Font("SansSerif", Font.PLAIN, 9));
        setForeground(new Color(80, 80, 80));
        setBackground(new Color(0, 0, 0, 0));
    }

    // ------------------------
    // Properties
    // ------------------------

    public int getBulbSize() {
        return bulbSize;
    }

    public void setBulbSize(int bulbSize) {
        int old = this.bulbSize;
        int newValue = Math.max(10, bulbSize);
        if (old == newValue) {
            return;
        }
        this.bulbSize = newValue;
        firePropertyChange("bulbSize", old, this.bulbSize);
        repaint();
    }

    public int getFineTicks() {
        return fineTicks;
    }

    public void setFineTicks(int fineTicks) {
        int old = this.fineTicks;
        int newValue = Math.max(0, fineTicks);
        if (old == newValue) {
            return;
        }
        this.fineTicks = newValue;
        firePropertyChange("fineTicks", old, this.fineTicks);
        repaint();
    }

    public int getMajorTicks() {
        return majorTicks;
    }

    public void setMajorTicks(int majorTicks) {
        int old = this.majorTicks;
        int newValue = Math.max(2, majorTicks);
        if (old == newValue) {
            return;
        }
        this.majorTicks = newValue;
        firePropertyChange("majorTicks", old, this.majorTicks);
        repaint();
    }

    public int getGap() {
        return gap;
    }

    public void setGap(int gap) {
        int old = this.gap;
        int newValue = Math.max(0, gap);
        if (old == newValue) {
            return;
        }
        this.gap = newValue;
        firePropertyChange("gap", old, this.gap);
        repaint();
    }

    public Color getHighColor() {
        return highColor;
    }

    public void setHighColor(Color highColor) {
        Color old = this.highColor;
        if (old == null ? highColor == null : old.equals(highColor)) {
            return;
        }
        this.highColor = highColor;
        firePropertyChange("highColor", old, this.highColor);
        repaint();
    }

    public Color getLowColor() {
        return lowColor;
    }

    public void setLowColor(Color lowColor) {
        Color old = this.lowColor;
        if (old == null ? lowColor == null : old.equals(lowColor)) {
            return;
        }
        this.lowColor = lowColor;
        firePropertyChange("lowColor", old, this.lowColor);
        repaint();
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        double old = this.maxValue;
        if (Double.compare(old, maxValue) == 0) {
            return;
        }
        this.maxValue = maxValue;
        firePropertyChange("maxValue", old, this.maxValue);
        repaint();
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        double old = this.minValue;
        if (Double.compare(old, minValue) == 0) {
            return;
        }
        this.minValue = minValue;
        firePropertyChange("minValue", old, this.minValue);
        repaint();
    }

    public float getShadowOpacity() {
        return shadowOpacity;
    }

    public void setShadowOpacity(float shadowOpacity) {
        float old = this.shadowOpacity;
        float newValue = Math.max(0f, Math.min(1f, shadowOpacity));
        if (Float.compare(old, newValue) == 0) {
            return;
        }
        this.shadowOpacity = newValue;
        firePropertyChange("shadowOpacity", old, this.shadowOpacity);
        repaint();
    }

    public boolean isShowTooltip() {
        return showTooltip;
    }

    public void setShowTooltip(boolean showTooltip) {
        boolean old = this.showTooltip;
        if (old == showTooltip) {
            return;
        }
        this.showTooltip = showTooltip;
        firePropertyChange("showTooltip", old, this.showTooltip);
        repaint();
    }

    public boolean isShowScale() {
        return showScale;
    }

    public void setShowScale(boolean showScale) {
        boolean old = this.showScale;
        if (old == showScale) {
            return;
        }
        this.showScale = showScale;
        firePropertyChange("showScale", old, this.showScale);
        repaint();
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        int old = this.unit;
        int newValue = (unit == UNIT_FAHRENHEIT) ? UNIT_FAHRENHEIT : UNIT_CELSIUS;
        if (old == newValue) {
            return;
        }
        this.unit = newValue;
        firePropertyChange("unit", old, this.unit);
        repaint();
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        double old = this.value;
        if (Double.compare(old, value) == 0) {
            return;
        }
        this.value = value;
        firePropertyChange("value", old, this.value);
        repaint();
    }

    // ------------------------
    // Standard appearance props
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

    @Override
    public void setBackground(Color bg) {
        Color old = getBackground();
        super.setBackground(bg);
        firePropertyChange("background", old, bg);
        repaint();
    }

    // ------------------------
    // Paint
    // ------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        float w = getWidth();
        float h = getHeight();

        double actualMin = minValue;
        double actualMax = maxValue;
        if (Double.compare(actualMin, actualMax) == 0) {
            actualMax = actualMin + 1.0;
        }

        double currentVal = Math.max(actualMin, Math.min(actualMax, value));
        double range = actualMax - actualMin;
        double pct = (currentVal - actualMin) / range;

        float paddingTop = 20f;
        float paddingBottom = 10f;

        float centerX = (w > 150f) ? (w * 0.45f) : (w / 2f);

        float actualBulbDia = Math.min(bulbSize, Math.min(w * 0.35f, h * 0.30f));
        actualBulbDia = Math.max(10f, actualBulbDia);

        float tubeW = actualBulbDia * 0.5f;
        float bulbCenterY = h - paddingBottom - (actualBulbDia / 2f);
        float topY = paddingTop;
        float tubeBottomY = bulbCenterY - (actualBulbDia / 2f) + 5f;

        float scaleTop = topY + (tubeW / 2f);
        float scaleBottom = bulbCenterY - (actualBulbDia / 2f);
        float scaleH = Math.max(1f, scaleBottom - scaleTop);

        // Background
        if (getBackground() != null && getBackground().getAlpha() > 0) {
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        paintShadow(g2, centerX, bulbCenterY, topY, tubeBottomY, actualBulbDia, tubeW);
        paintGlass(g2, centerX, bulbCenterY, topY, tubeBottomY, actualBulbDia, tubeW);
        float liquidTopY = paintLiquid(g2, centerX, bulbCenterY, topY, actualBulbDia, tubeW, scaleTop, scaleBottom, scaleH, pct);

        if (showScale) {
            paintScale(g2, centerX, tubeW, scaleTop, scaleBottom, scaleH, actualMin, actualMax);
        }

        if (showTooltip) {
            paintTooltip(g2, centerX, tubeW, liquidTopY, scaleTop, scaleBottom, currentVal);
        }

        g2.dispose();
    }

    private void paintShadow(Graphics2D g2,
                             float centerX,
                             float bulbCenterY,
                             float topY,
                             float tubeBottomY,
                             float bulbDia,
                             float tubeW) {

        int shadowSize = DEFAULT_SHADOW_SIZE;
        float alphaBase = Math.max(0f, Math.min(1f, shadowOpacity));

        for (int i = shadowSize; i > 0; i--) {
            float progress = 1f - ((float) i / (float) shadowSize);
            int alpha = (int) ((alphaBase * 255f) * (progress * progress));
            alpha = Math.max(0, Math.min(255, alpha));

            g2.setColor(new Color(0, 0, 0, alpha));

            float sBulb = bulbDia + (i * 2f);
            g2.fill(new Ellipse2D.Float(
                    centerX - (sBulb / 2f),
                    bulbCenterY - (sBulb / 2f),
                    sBulb,
                    sBulb
            ));

            float sTubeW = tubeW + (i * 2f);
            g2.fill(new RoundRectangle2D.Float(
                    centerX - (sTubeW / 2f),
                    topY - i,
                    sTubeW,
                    (tubeBottomY - topY) + i + 10f,
                    sTubeW,
                    sTubeW
            ));
        }
    }

    private void paintGlass(Graphics2D g2,
                            float centerX,
                            float bulbCenterY,
                            float topY,
                            float tubeBottomY,
                            float bulbDia,
                            float tubeW) {

        Shape bulbShape = new Ellipse2D.Float(
                centerX - (bulbDia / 2f),
                bulbCenterY - (bulbDia / 2f),
                bulbDia,
                bulbDia
        );

        Shape tubeShape = new RoundRectangle2D.Float(
                centerX - (tubeW / 2f),
                topY,
                tubeW,
                tubeBottomY - topY + 10f,
                tubeW,
                tubeW
        );

        g2.setColor(Color.WHITE);
        g2.fill(tubeShape);
        g2.fill(bulbShape);

        g2.setColor(new Color(220, 220, 220));
        g2.setStroke(new BasicStroke(1f));
        g2.draw(tubeShape);
        g2.draw(bulbShape);
    }

    private float paintLiquid(Graphics2D g2,
                              float centerX,
                              float bulbCenterY,
                              float topY,
                              float bulbDia,
                              float tubeW,
                              float scaleTop,
                              float scaleBottom,
                              float scaleH,
                              double pct) {

        float liquidTopY = (float) (scaleBottom - (pct * scaleH));

        float innerGap = Math.max(0f, gap);
        float liqBulbDia = Math.max(2f, bulbDia - (innerGap * 2f));
        float liqTubeW = Math.max(2f, tubeW - (innerGap * 2f));

        Color actualLow = lowColor != null ? lowColor : new Color(105, 162, 247);
        Color actualHigh = highColor != null ? highColor : new Color(240, 34, 11, 145);

        GradientPaint gp = new GradientPaint(
                0f, bulbCenterY, actualLow,
                0f, topY, actualHigh
        );
        g2.setPaint(gp);

        g2.fill(new Ellipse2D.Float(
                centerX - (liqBulbDia / 2f),
                bulbCenterY - (liqBulbDia / 2f),
                liqBulbDia,
                liqBulbDia
        ));

        float bulbLiquidTop = bulbCenterY - (liqBulbDia / 2f);
        float drawH = (bulbLiquidTop - liquidTopY) + 5f;

        if (drawH > 0f) {
            g2.fill(new RoundRectangle2D.Float(
                    centerX - (liqTubeW / 2f),
                    liquidTopY,
                    liqTubeW,
                    drawH,
                    liqTubeW,
                    liqTubeW
            ));
        }

        return liquidTopY;
    }

    private void paintScale(Graphics2D g2,
                            float centerX,
                            float tubeW,
                            float scaleTop,
                            float scaleBottom,
                            float scaleH,
                            double minV,
                            double maxV) {
        Color fg = getForeground() != null ? getForeground() : new Color(80,80,80);

        int safeMajorTicks = Math.max(2, majorTicks);
        int safeFineTicks = Math.max(0, fineTicks);

        float tickStartX = centerX + (tubeW / 2f) + 3f;
        float tickEndMajor = tickStartX + 6f;
        float tickEndMinor = tickStartX + 3f;

        g2.setFont(getFont() != null ? getFont() : new Font("SansSerif", Font.PLAIN, 9));
        FontMetrics fm = g2.getFontMetrics();

        double range = maxV - minV;

        for (int i = 0; i < safeMajorTicks; i++) {
            float tickPct = (safeMajorTicks == 1) ? 0f : ((float) i / (float) (safeMajorTicks - 1));
            float tickY = scaleBottom - (tickPct * scaleH);

            g2.setColor(fg);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new Line2D.Float(tickStartX, tickY, tickEndMajor, tickY));

            double tickVal = minV + (tickPct * range);
            String label = formatScaleValue(tickVal) + getUnitSuffix();

            g2.setColor(fg);
            g2.drawString(label, tickEndMajor + 3f, tickY + (fm.getAscent() * 0.35f));

            if (i < safeMajorTicks - 1 && safeFineTicks > 0) {
                float stepY = (scaleH / (safeMajorTicks - 1)) / (safeFineTicks + 1f);
                for (int j = 1; j <= safeFineTicks; j++) {
                    float fineY = tickY - (j * stepY);
                    g2.setColor(fg);
                    g2.draw(new Line2D.Float(tickStartX, fineY, tickEndMinor, fineY));
                }
            }
        }
    }

    private void paintTooltip(Graphics2D g2,
                              float centerX,
                              float tubeW,
                              float liquidTopY,
                              float scaleTop,
                              float scaleBottom,
                              double currentVal) {

        float tipY = Math.max(scaleTop, Math.min(scaleBottom, liquidTopY));
        String tipText = formatScaleValue(currentVal) + getUnitSuffix();

        Font tooltipFont = (getFont() != null ? getFont() : new Font("SansSerif", Font.PLAIN, 9)).deriveFont(Font.BOLD, 12f);
        g2.setFont(tooltipFont);
        FontMetrics fm = g2.getFontMetrics();

        int textWidth = fm.stringWidth(tipText);
        int textHeight = fm.getAscent();

        int boxPad = 6;
        float boxW = textWidth + (boxPad * 2f);
        float boxH = textHeight + (boxPad * 2f);

        float tipPointX = centerX - (tubeW / 2f) - gap;
        float boxRightX = tipPointX - 8f;
        float boxLeftX = boxRightX - boxW;
        float boxTopY = tipY - (boxH / 2f);

        Path2D.Float bubble = new Path2D.Float();
        bubble.moveTo(tipPointX, tipY);
        bubble.lineTo(boxRightX, tipY - 5f);
        bubble.lineTo(boxRightX, boxTopY + 4f);
        bubble.curveTo(boxRightX, boxTopY, boxRightX, boxTopY, boxRightX - 4f, boxTopY);
        bubble.lineTo(boxLeftX + 4f, boxTopY);
        bubble.curveTo(boxLeftX, boxTopY, boxLeftX, boxTopY, boxLeftX, boxTopY + 4f);
        bubble.lineTo(boxLeftX, boxTopY + boxH - 4f);
        bubble.curveTo(boxLeftX, boxTopY + boxH, boxLeftX, boxTopY + boxH, boxLeftX + 4f, boxTopY + boxH);
        bubble.lineTo(boxRightX - 4f, boxTopY + boxH);
        bubble.curveTo(boxRightX, boxTopY + boxH, boxRightX, boxTopY + boxH, boxRightX, boxTopY + boxH - 4f);
        bubble.lineTo(boxRightX, tipY + 5f);
        bubble.closePath();

        g2.translate(2, 2);
        g2.setColor(new Color(0, 0, 0, 40));
        g2.fill(bubble);
        g2.translate(-2, -2);

        g2.setColor(Color.WHITE);
        g2.fill(bubble);

        g2.setColor(new Color(200, 200, 200));
        g2.setStroke(new BasicStroke(1f));
        g2.draw(bubble);

        Color textColor = getForeground() != null ? getForeground() : new Color(80, 80, 80);
        g2.setColor(textColor);
        g2.drawString(tipText, boxLeftX + boxPad, boxTopY + boxPad + textHeight - 2f);
    }

    private String formatScaleValue(double val) {
        if (Math.abs(val - Math.rint(val)) < 0.000001) {
            return Integer.toString((int) Math.rint(val));
        }
        return String.format("%.1f", val);
    }

    private String getUnitSuffix() {
        return unit == UNIT_FAHRENHEIT ? "°F" : "°C";
    }

    // ------------------------
    // Size
    // ------------------------

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(130, 230);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(70, 140);
    }
}