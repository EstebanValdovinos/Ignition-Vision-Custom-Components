package com.inductiveautomation.ignition.examples.ce.components.display;

import com.inductiveautomation.ignition.client.images.ImageLoader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class TeslaLoadingSpinnerComponent extends JComponent {

    private boolean clockwise = true;
    private Color spinnerColor = new Color(170, 170, 170);
    private String iconPath = "";
    private float spinnerWidth = 5f;   // 0 = auto
    private String text = "";

    private float animationProgress = 0f;   // 0.0 to 1.0
    private final Timer animationTimer;

    private Image defaultBundledIcon;

    public TeslaLoadingSpinnerComponent() {
        setPreferredSize(new Dimension(65, 65));
        setMinimumSize(new Dimension(50, 50));
        setBackground(new Color(0, 0, 0, 0));
        setForeground(Color.GRAY);
        setFont(new Font("Dialog", Font.PLAIN, 12));
        setOpaque(false);

        defaultBundledIcon = loadBundledDefaultIcon();

        animationTimer = new Timer(16, e -> {
            animationProgress += 0.020f; //0.0125f;
            if (animationProgress >= 1f) {
                animationProgress -= 1f;
            }
            repaint();
        });
        animationTimer.start();
    }

    // ------------------------
    // Properties
    // ------------------------

    public boolean isClockwise() {
        return clockwise;
    }

    public void setClockwise(boolean clockwise) {
        boolean old = this.clockwise;
        if (old == clockwise) {
            return;
        }

        this.clockwise = clockwise;
        firePropertyChange("clockwise", old, clockwise);
        repaint();
    }

    public Color getSpinnerColor() {
        return spinnerColor;
    }

    public void setSpinnerColor(Color spinnerColor) {
        Color old = this.spinnerColor;
        if (old == null ? spinnerColor == null : old.equals(spinnerColor)) {
            return;
        }

        this.spinnerColor = spinnerColor;
        firePropertyChange("spinnerColor", old, spinnerColor);
        repaint();
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        String old = this.iconPath;
        if (old == null ? iconPath == null : old.equals(iconPath)) {
            return;
        }

        this.iconPath = iconPath;
        firePropertyChange("iconPath", old, iconPath);
        repaint();
    }

    public float getSpinnerWidth() {
        return spinnerWidth;
    }

    public void setSpinnerWidth(float spinnerWidth) {
        float old = this.spinnerWidth;
        float newValue = Math.max(0f, spinnerWidth);

        if (Float.compare(old, newValue) == 0) {
            return;
        }

        this.spinnerWidth = newValue;
        firePropertyChange("spinnerWidth", old, this.spinnerWidth);
        repaint();
    }

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
        revalidate();
        repaint();
    }

    // ------------------------
    // Standard appearance props
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

    @Override
    public void setEnabled(boolean enabled) {
        boolean old = isEnabled();
        super.setEnabled(enabled);

        firePropertyChange("enabled", old, enabled);

        if (enabled) {
            if (!animationTimer.isRunning()) {
                animationTimer.start();
            }
        } else {
            if (animationTimer.isRunning()) {
                animationTimer.stop();
            }
        }

        repaint();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (isEnabled() && !animationTimer.isRunning()) {
            animationTimer.start();
        }
    }

    @Override
    public void removeNotify() {
        if (animationTimer.isRunning()) {
            animationTimer.stop();
        }
        super.removeNotify();
    }

    // ------------------------
    // Helpers
    // ------------------------

    private Image loadBundledDefaultIcon() {
        try {
            InputStream is = TeslaLoadingSpinnerComponent.class.getResourceAsStream("/images/Web_TeslaT_Black.png");
            if (is != null) {
                try {
                    return ImageIO.read(is);
                } finally {
                    is.close();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private Image loadIconImage() {
        if (iconPath != null && !iconPath.trim().isEmpty()) {
            try {
                Image customImage = ImageLoader.getInstance().loadImage(iconPath);
                if (customImage != null) {
                    return customImage;
                }
            } catch (Exception ignored) {
            }
        }
        return defaultBundledIcon;
    }

    private Color withAlpha(Color c, int alpha) {
        if (c == null) {
            c = Color.GRAY;
        }
        alpha = Math.max(0, Math.min(255, alpha));
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    private LayoutMetrics getLayoutMetrics(Graphics2D g2) {
        float w = getWidth();
        float h = getHeight();
        float padding = 4f;

        float textHeightReservation = 0f;
        if (text != null && !text.trim().isEmpty()) {
            textHeightReservation = Math.min(30f, h * 0.20f);
        }

        float availableHeight = h - textHeightReservation;
        float diameter = Math.min(w, availableHeight) - (padding * 2f);
        diameter = Math.max(12f, diameter);

        float centerX = w / 2f;
        float centerY = availableHeight / 2f;

        float fontSize = Math.max(10f, Math.min(16f, diameter * 0.15f));

        return new LayoutMetrics(centerX, centerY, diameter, availableHeight, textHeightReservation, fontSize);
    }

    // ------------------------
    // Painting
    // ------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        LayoutMetrics layout = getLayoutMetrics(g2);

        paintSpinner(g2, layout);
        paintCenterIcon(g2, layout);
        paintText(g2, layout);

        g2.dispose();
    }

    private void paintSpinner(Graphics2D g2, LayoutMetrics layout) {
        float diameter = layout.diameter;
        float cx = layout.centerX;
        float cy = layout.centerY;

        int segments = (int) Math.max(120, Math.min(720, diameter * 2.5f));
        float tailLength = 300f;
        float step = tailLength / segments;

        float strokeW;
        if (spinnerWidth > 0f) {
            strokeW = spinnerWidth;
        } else {
            strokeW = Math.max(3f, diameter * 0.08f);
        }

        strokeW = Math.max(1f, Math.min(strokeW, diameter * 0.30f));

        g2.setStroke(new BasicStroke(strokeW, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        float headAngle;
        if (clockwise) {
            headAngle = animationProgress * -360f;
        } else {
            headAngle = animationProgress * 360f;
        }

        float arcExtent = (tailLength / segments) + 0.5f;

        Color ringColor = spinnerColor != null ? spinnerColor : new Color(170, 170, 170);

        if (!isEnabled()) {
            ringColor = withAlpha(ringColor, 120);
        }

        float arcX = cx - (diameter / 2f) + (strokeW / 2f);
        float arcY = cy - (diameter / 2f) + (strokeW / 2f);
        float arcSize = diameter - strokeW;

        for (int i = 0; i < segments; i++) {
            float progress = (float) i / (float) segments;
            int alpha = (int) (Math.pow(progress, 3) * 255);

            if (!isEnabled()) {
                alpha = Math.min(alpha, 120);
            }

            g2.setColor(withAlpha(ringColor, alpha));

            float segAngle;
            if (clockwise) {
                segAngle = (headAngle + tailLength) - (i * step);
            } else {
                segAngle = (headAngle - tailLength) + (i * step);
            }

            g2.draw(new Arc2D.Float(
                    arcX,
                    arcY,
                    arcSize,
                    arcSize,
                    segAngle,
                    arcExtent,
                    Arc2D.OPEN
            ));
        }
    }

    private void paintCenterIcon(Graphics2D g2, LayoutMetrics layout) {
        Image img = loadIconImage();
        if (img == null) {
            return;
        }

        float sineVal = (float) Math.sin(animationProgress * 2f * Math.PI);
        float baseAlpha = (sineVal + 1f) / 2f;
        float finalAlpha = Math.max(0.15f, Math.min(1f, baseAlpha));

        float pulseScale = 0.92f + (0.12f * baseAlpha);
        float currentIconSize = layout.diameter * 0.40f * pulseScale;
        currentIconSize = Math.max(8f, currentIconSize);

        int iconSize = Math.round(currentIconSize);
        int imgX = Math.round(layout.centerX - (currentIconSize / 2f));
        int imgY = Math.round(layout.centerY - (currentIconSize / 2f));

        Composite oldComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, isEnabled() ? finalAlpha : Math.min(finalAlpha, 0.45f)));

        try {
            Color tint = getForeground() != null ? getForeground() : Color.GRAY;

            BufferedImage bi = new BufferedImage(iconSize, iconSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D ig = bi.createGraphics();
            ig.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            ig.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            ig.drawImage(img, 0, 0, iconSize, iconSize, null);

            ig.setComposite(AlphaComposite.SrcIn);
            ig.setColor(tint);
            ig.fillRect(0, 0, iconSize, iconSize);
            ig.dispose();

            g2.drawImage(bi, imgX, imgY, null);
        } catch (Exception ignored) {
        }

        g2.setComposite(oldComposite);
    }

    private void paintText(Graphics2D g2, LayoutMetrics layout) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }

        Font baseFont = getFont() != null ? getFont() : new Font("Dialog", Font.PLAIN, 12);
        g2.setFont(baseFont.deriveFont(layout.fontSize));

        FontMetrics fm = g2.getFontMetrics();
        float strW = fm.stringWidth(text);
        float strH = fm.getAscent();

        float textX = layout.centerX - (strW / 2f);
        float textY = layout.availableHeight + (layout.textHeightReservation / 2f) + (strH / 3f);

        Color fg = getForeground() != null ? getForeground() : Color.GRAY;
        if (!isEnabled()) {
            fg = withAlpha(fg, 120);
        }

        g2.setColor(fg);
        g2.drawString(text, textX, textY);
    }

    // ------------------------
    // Size
    // ------------------------

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(65, 65);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(50, 50);
    }

    private static class LayoutMetrics {
        final float centerX;
        final float centerY;
        final float diameter;
        final float availableHeight;
        final float textHeightReservation;
        final float fontSize;

        LayoutMetrics(float centerX, float centerY, float diameter,
                      float availableHeight, float textHeightReservation, float fontSize) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.diameter = diameter;
            this.availableHeight = availableHeight;
            this.textHeightReservation = textHeightReservation;
            this.fontSize = fontSize;
        }
    }
}