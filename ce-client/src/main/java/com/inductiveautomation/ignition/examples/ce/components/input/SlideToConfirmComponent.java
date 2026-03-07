package com.inductiveautomation.ignition.examples.ce.components.input;

import com.inductiveautomation.ignition.client.images.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public class SlideToConfirmComponent extends JComponent {

    private String text = "Slide To Confirm";
    private String activeText = "Confirm";
    private String iconPath = "";
    private Image defaultBundledIcon;

    private Color knobColor = Color.WHITE;
    private Color iconColor = new Color(0, 122, 255);
    private Color pulseColor = Color.WHITE;

    private int cornerRadius = -1;
    private int pulsePadding = 15;

    private boolean confirmed = false;
    private boolean autoReset = true;
    private int resetDelay = 1500; // milliseconds
    private float confirmThreshold = 0.95f;

    // 0.0 to 1.0
    private float slidePos = 0f;

    private boolean dragging = false;
    private int dragOffsetX = 0;

    // one-ripple pulse
    private float pulseProgress = 0f;
    private boolean pulsing = false;
    private final Timer pulseTimer;

    private final Timer resetTimer;
    private final Timer resetAnimationTimer;

    public SlideToConfirmComponent() {
        setPreferredSize(new Dimension(260, 70));
        setMinimumSize(new Dimension(140, 40));
        setFont(new Font("Dialog", Font.BOLD, 12));
        setBackground(new Color(0, 122, 255));
        setForeground(Color.WHITE);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFocusable(true);
        setOpaque(false);

        defaultBundledIcon = loadBundledDefaultIcon();

        pulseTimer = new Timer(20, e -> {
            pulseProgress += 0.04f;

            if (pulseProgress >= 1f) {
                pulseProgress = 0f;

                boolean shouldKeepPulsing = confirmed || (dragging && slidePos >= confirmThreshold);

                if (!shouldKeepPulsing) {
                    pulsing = false;
                    ((Timer) e.getSource()).stop();
                }
            }

            repaint();
        });

        resetTimer = new Timer(resetDelay, e -> {
            ((Timer) e.getSource()).stop();
            animateReset();
        });
        resetTimer.setRepeats(false);

        resetAnimationTimer = new Timer(15, e -> {
            if (slidePos > 0f) {
                float old = slidePos;
                slidePos = Math.max(0f, slidePos - 0.06f);
                firePropertyChange("slidePos", old, slidePos);
                repaint();
            } else {
                ((Timer) e.getSource()).stop();
                boolean oldConfirmed = confirmed;
                confirmed = false;
                firePropertyChange("confirmed", oldConfirmed, confirmed);
            }
        });

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!isEnabled()) {
                    return;
                }

                Rectangle knobBounds = getKnobBounds();
                if (knobBounds.contains(e.getPoint())) {
                    dragging = true;
                    dragOffsetX = e.getX() - knobBounds.x;
                    requestFocusInWindow();

                    if (resetAnimationTimer.isRunning()) {
                        resetAnimationTimer.stop();
                    }
                    if (resetTimer.isRunning()) {
                        resetTimer.stop();
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (!isEnabled() || !dragging) {
                    return;
                }

                updateSlidePositionFromMouse(e.getX());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!isEnabled()) {
                    dragging = false;
                    return;
                }

                if (dragging) {
                    dragging = false;

                    if (slidePos >= confirmThreshold) {
                        setConfirmed(true);
                    } else {
                        animateReset();
                    }
                }
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    // ------------------------
    // Properties
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

    public String getActiveText() {
        return activeText;
    }

    public void setActiveText(String activeText) {
        String old = this.activeText;
        if (old == null ? activeText == null : old.equals(activeText)) {
            return;
        }
        this.activeText = activeText;
        firePropertyChange("activeText", old, activeText);
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

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {

        boolean old = this.confirmed;
        this.confirmed = confirmed;

        if (confirmed) {

            float oldSlide = slidePos;
            slidePos = 1f;
            firePropertyChange("slidePos", oldSlide, slidePos);

            startPulse();

            if (autoReset) {
                resetTimer.stop();
                resetTimer.setInitialDelay(resetDelay);
                resetTimer.restart();
            }

        } else {
            animateReset();
        }

        if (old != confirmed) {
            firePropertyChange("confirmed", old, confirmed);
        }

        repaint();
    }

    public float getSlidePos() {
        return slidePos;
    }

    public int getPulsePadding() {
        return pulsePadding;
    }

    public void setPulsePadding(int pulsePadding) {
        int old = this.pulsePadding;
        int newValue = Math.max(0, pulsePadding);

        if (old == newValue) {
            return;
        }

        this.pulsePadding = newValue;
        firePropertyChange("pulsePadding", old, this.pulsePadding);
        repaint();
    }

    public void setSlidePos(float slidePos) {
        float old = this.slidePos;
        float newValue = Math.max(0f, Math.min(1f, slidePos));
        if (old == newValue) {
            return;
        }

        this.slidePos = newValue;
        firePropertyChange("slidePos", old, this.slidePos);
        repaint();
    }

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

    public Color getKnobColor() {
        return knobColor;
    }

    public void setKnobColor(Color knobColor) {
        Color old = this.knobColor;
        if (old == null ? knobColor == null : old.equals(knobColor)) {
            return;
        }

        this.knobColor = knobColor;
        firePropertyChange("knobColor", old, knobColor);
        repaint();
    }

    public Color getIconColor() {
        return iconColor;
    }

    public void setIconColor(Color iconColor) {
        Color old = this.iconColor;
        if (old == null ? iconColor == null : old.equals(iconColor)) {
            return;
        }

        this.iconColor = iconColor;
        firePropertyChange("iconColor", old, iconColor);
        repaint();
    }

    public Color getPulseColor() {
        return pulseColor;
    }

    public void setPulseColor(Color pulseColor) {
        Color old = this.pulseColor;
        if (old == null ? pulseColor == null : old.equals(pulseColor)) {
            return;
        }

        this.pulseColor = pulseColor;
        firePropertyChange("pulseColor", old, pulseColor);
        repaint();
    }

    public boolean isAutoReset() {
        return autoReset;
    }

    public void setAutoReset(boolean autoReset) {
        boolean old = this.autoReset;
        if (old == autoReset) {
            return;
        }

        this.autoReset = autoReset;
        firePropertyChange("autoReset", old, autoReset);
    }

    public int getResetDelay() {
        return resetDelay;
    }

    public void setResetDelay(int resetDelay) {
        int old = this.resetDelay;
        int newValue = Math.max(0, resetDelay);

        if (old == newValue) {
            return;
        }

        this.resetDelay = newValue;
        resetTimer.setInitialDelay(newValue);

        firePropertyChange("resetDelay", old, this.resetDelay);
    }

    public float getConfirmThreshold() {
        return confirmThreshold;
    }

    public void setConfirmThreshold(float confirmThreshold) {
        float old = this.confirmThreshold;
        float newValue = Math.max(0.1f, Math.min(1.0f, confirmThreshold));

        if (old == newValue) {
            return;
        }

        this.confirmThreshold = newValue;
        firePropertyChange("confirmThreshold", old, this.confirmThreshold);
    }

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

        if (enabled) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
            dragging = false;
        }

        firePropertyChange("enabled", old, enabled);
        repaint();
    }

    // ------------------------
    // Helpers
    // ------------------------

    private void startPulse() {
        pulsing = true;

        if (!pulseTimer.isRunning()) {
            pulseProgress = 0f;
            pulseTimer.start();
        }
    }

    private void animateReset() {
        resetTimer.stop();

        pulsing = false;
        pulseTimer.stop();
        pulseProgress = 0f;

        if (!resetAnimationTimer.isRunning()) {
            resetAnimationTimer.start();
        }
    }

    private Image loadBundledDefaultIcon() {
        try {
            java.io.InputStream is = SlideToConfirmComponent.class.getResourceAsStream("/images/slide_default_icon.png");
            if (is != null) {
                try {
                    return javax.imageio.ImageIO.read(is);
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
        // First try user-specified Ignition image path
        if (iconPath != null && !iconPath.trim().isEmpty()) {
            try {
                Image customImage = ImageLoader.getInstance().loadImage(iconPath);
                if (customImage != null) {
                    return customImage;
                }
            } catch (Exception ignored) {
            }
        }

        // Fallback to bundled default icon
        return defaultBundledIcon;
    }

    private void updateSlidePositionFromMouse(int mouseX) {
        InsetsLayout layout = getLayoutMetrics();

        int newKnobX = mouseX - dragOffsetX;
        int minX = layout.knobMinX;
        int maxX = layout.knobMaxX;

        newKnobX = Math.max(minX, Math.min(maxX, newKnobX));

        float newSlide = (maxX == minX) ? 0f : (float) (newKnobX - minX) / (float) (maxX - minX);
        setSlidePos(newSlide);

        boolean thresholdReached = this.slidePos >= confirmThreshold;

        if (thresholdReached) {
            if (!pulsing) {
                startPulse();
            }
        } else {
            if (pulsing && !confirmed) {
                pulsing = false;
                pulseTimer.stop();
                pulseProgress = 0f;
                repaint();
            }
        }
    }

    private Rectangle getKnobBounds() {
        InsetsLayout layout = getLayoutMetrics();
        return new Rectangle(layout.knobX, layout.knobY, layout.knobSize, layout.knobSize);
    }

    private InsetsLayout getLayoutMetrics() {
        int w = getWidth();
        int h = getHeight();

        int outerPad = Math.max(6, pulsePadding);
        int trackX = outerPad;
        int trackY = outerPad;
        int trackW = Math.max(20, w - (outerPad * 2));
        int trackH = Math.max(20, h - (outerPad * 2));

        int innerPad = 4;
        int knobSize = Math.max(12, trackH - (innerPad * 2));
        int knobY = trackY + innerPad;

        int knobMinX = trackX + innerPad;
        int knobMaxX = trackX + trackW - innerPad - knobSize;
        int knobX = knobMinX + Math.round(slidePos * (knobMaxX - knobMinX));

        return new InsetsLayout(trackX, trackY, trackW, trackH, innerPad, knobSize, knobX, knobY, knobMinX, knobMaxX);
    }

    // ------------------------
    // Painting
    // ------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        InsetsLayout layout = getLayoutMetrics();

        int radius = (cornerRadius <= 0) ? layout.trackH : cornerRadius;

        Color trackColor = getBackground();
        Color textColor = getForeground();

        if (!isEnabled()) {
            trackColor = new Color(trackColor.getRed(), trackColor.getGreen(), trackColor.getBlue(), 120);
            textColor = new Color(200, 200, 200);
        }

        Shape trackShape = new RoundRectangle2D.Float(
                layout.trackX,
                layout.trackY,
                layout.trackW,
                layout.trackH,
                radius,
                radius
        );

        g2.setColor(trackColor);
        g2.fill(trackShape);

        if (pulsing || confirmed) {
            int maxGrowth = pulsePadding * 2;
            int growth = (int) (maxGrowth * pulseProgress);
            int pulseSize = layout.knobSize + growth;

            int pulseX = layout.knobX - (growth / 2);
            int pulseY = layout.knobY - (growth / 2);

            int alpha = (int) (130 * (1f - pulseProgress));
            alpha = Math.max(0, Math.min(255, alpha));

            Color pColor = pulseColor != null ? pulseColor : Color.WHITE;
            g2.setColor(new Color(pColor.getRed(), pColor.getGreen(), pColor.getBlue(), alpha));
            g2.fill(new Ellipse2D.Float(pulseX, pulseY, pulseSize, pulseSize));
        }

        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();

        Composite oldComposite = g2.getComposite();

        float alphaInactive = Math.max(0f, Math.min(1f, 1f - (slidePos * 1.5f)));
        if (alphaInactive > 0f && text != null) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaInactive));
            g2.setColor(textColor);

            int strW = fm.stringWidth(text);
            int txtX = layout.trackX + (layout.trackW - strW + layout.knobSize / 2) / 2;
            int txtY = layout.trackY + (layout.trackH / 2) + (fm.getAscent() * 35 / 100);

            g2.drawString(text, txtX, txtY);
        }

        if (slidePos > 0.5f && activeText != null) {
            float alphaActive = Math.max(0f, Math.min(1f, (slidePos - 0.5f) * 2f));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaActive));
            g2.setColor(textColor);

            int strW = fm.stringWidth(activeText);
            int txtX = layout.trackX + (layout.trackW - strW) / 2;
            int txtY = layout.trackY + (layout.trackH / 2) + (fm.getAscent() * 35 / 100);

            g2.drawString(activeText, txtX, txtY);
        }

        g2.setComposite(oldComposite);

        Shape knobShape = new Ellipse2D.Float(layout.knobX, layout.knobY, layout.knobSize, layout.knobSize);
        Color actualKnob = knobColor != null ? knobColor : Color.WHITE;
        if (!isEnabled()) {
            actualKnob = new Color(230, 230, 230);
        }

        g2.setColor(actualKnob);
        g2.fill(knobShape);

        paintTintedIcon(g2, layout);

        g2.dispose();
    }

    private void paintTintedIcon(Graphics2D g2, InsetsLayout layout) {
        try {
            Image img = loadIconImage();
            if (img == null) {
                return;
            }

            int iconDim = (int) (layout.knobSize * 0.75f);

            BufferedImage bi = new BufferedImage(iconDim, iconDim, BufferedImage.TYPE_INT_ARGB);
            Graphics2D ig = bi.createGraphics();
            ig.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            ig.drawImage(img, 0, 0, iconDim, iconDim, null);

            ig.setComposite(AlphaComposite.SrcIn);
            Color tint = iconColor != null ? iconColor : Color.BLACK;
            ig.setColor(tint);
            ig.fillRect(0, 0, iconDim, iconDim);
            ig.dispose();

            int ix = layout.knobX + (layout.knobSize - iconDim) / 2;
            int iy = layout.knobY + (layout.knobSize - iconDim) / 2;

            g2.drawImage(bi, ix, iy, null);
        } catch (Exception ignored) {
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(260, 70);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(140, 40);
    }

    private static class InsetsLayout {
        final int trackX;
        final int trackY;
        final int trackW;
        final int trackH;
        final int innerPad;
        final int knobSize;
        final int knobX;
        final int knobY;
        final int knobMinX;
        final int knobMaxX;

        InsetsLayout(int trackX, int trackY, int trackW, int trackH, int innerPad,
                     int knobSize, int knobX, int knobY, int knobMinX, int knobMaxX) {
            this.trackX = trackX;
            this.trackY = trackY;
            this.trackW = trackW;
            this.trackH = trackH;
            this.innerPad = innerPad;
            this.knobSize = knobSize;
            this.knobX = knobX;
            this.knobY = knobY;
            this.knobMinX = knobMinX;
            this.knobMaxX = knobMaxX;
        }
    }
}