package com.inductiveautomation.ignition.examples.ce.components.input;

import com.inductiveautomation.ignition.client.images.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public class IOSButtonComponent extends JComponent {

    public static final int ICON_LEFT = 0;
    public static final int ICON_RIGHT = 1;

    private String text = "Submit";
    private boolean pressed = false;
    private int cornerRadius = -1;

    private String iconPath = "";
    private int iconLocation = ICON_LEFT;
    private Color iconColor = Color.WHITE;
    private int iconSize = 24;
    private int iconGap = 50;

    private Color strokeColor = new Color(0,0,0,0); // transparent default
    private float strokeWidth = 0f;

    public IOSButtonComponent() {
        setPreferredSize(new Dimension(230, 40));
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
                pressed = false;
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (isEnabled() && SwingUtilities.isLeftMouseButton(e)) {
                    firePropertyChange("buttonClicked", false, true);
                }
            }
        });
    }

    // ------------------------
    // Properties
    // ------------------------

    public String getText() {
        return text;
    }

    public void setText(String text) {
        String old = this.text;
        this.text = text;
        firePropertyChange("text", old, text);
        repaint();
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        String old = this.iconPath;
        this.iconPath = iconPath;
        firePropertyChange("iconPath", old, iconPath);
        repaint();
    }

    public int getIconLocation() {
        return iconLocation;
    }

    public void setIconLocation(int iconLocation) {
        int old = this.iconLocation;

        if (iconLocation != ICON_LEFT && iconLocation != ICON_RIGHT) {
            iconLocation = ICON_LEFT;
        }

        this.iconLocation = iconLocation;
        firePropertyChange("iconLocation", old, this.iconLocation);
        repaint();
    }

    public Color getIconColor() {
        return iconColor;
    }

    public void setIconColor(Color iconColor) {
        Color old = this.iconColor;
        this.iconColor = iconColor;
        firePropertyChange("iconColor", old, iconColor);
        repaint();
    }

    public int getIconSize() {
        return iconSize;
    }

    public void setIconSize(int iconSize) {
        int old = this.iconSize;
        this.iconSize = Math.max(0, iconSize);
        firePropertyChange("iconSize", old, this.iconSize);
        repaint();
    }

    public int getIconGap() {
        return iconGap;
    }

    public void setIconGap(int iconGap) {
        int old = this.iconGap;
        this.iconGap = Math.max(0, iconGap);
        firePropertyChange("iconGap", old, this.iconGap);
        repaint();
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(int cornerRadius) {
        int old = this.cornerRadius;
        this.cornerRadius = cornerRadius;
        firePropertyChange("cornerRadius", old, cornerRadius);
        repaint();
    }

    // ------------------------
    // Enabled
    // ------------------------

    @Override
    public void setEnabled(boolean enabled) {
        boolean old = isEnabled();
        super.setEnabled(enabled);
        firePropertyChange("enabled", old, enabled);

        if (!enabled) {
            pressed = false;
            setCursor(Cursor.getDefaultCursor());
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        Color bg = getBackground();
        Color fg = getForeground();

        if (!isEnabled()) {
            bg = new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 120);
            fg = new Color(180, 180, 180);
        } else if (pressed) {
            bg = bg.darker();
        }

        int radius = (cornerRadius <= 0) ? h : cornerRadius;

        float half = strokeWidth / 2f;

        Shape buttonShape = new RoundRectangle2D.Float(
                half,
                half,
                w - strokeWidth,
                h - strokeWidth,
                radius,
                radius
        );
        g2.setColor(bg);
        g2.fill(buttonShape);

        if (strokeWidth > 0f) {

            Color borderColor = strokeColor != null ? strokeColor : getForeground();

            if (!isEnabled()) {
                borderColor = new Color(
                        borderColor.getRed(),
                        borderColor.getGreen(),
                        borderColor.getBlue(),
                        120
                );
            }

            g2.setStroke(new BasicStroke(strokeWidth));
            g2.setColor(borderColor);
            g2.draw(buttonShape);
        }


        drawCenteredText(g2, new Rectangle(0, 0, w, h), fg);

        Rectangle iconRect = calculateIconRect(g2);
        if (iconRect != null) {
            paintIcon(g2, iconRect, isEnabled());
        }

        g2.dispose();
    }

    // ------------------------
    // Layout helpers
    // ------------------------

    private Rectangle calculateIconRect(Graphics2D g2) {
        Image icon = loadIconImage();
        if (icon == null) {
            return null;
        }

        int w = getWidth();
        int h = getHeight();

        int padding = Math.max(10, h / 4);
        int size = (iconSize > 0) ? iconSize : Math.max(12, (int) (h * 0.45f));
        size = Math.min(size, Math.max(8, h - 6));

        FontMetrics fm = g2.getFontMetrics(getFont());
        String safeText = text != null ? text : "";
        int textWidth = fm.stringWidth(safeText);

        int centerX = w / 2;
        int textLeft = centerX - (textWidth / 2);
        int textRight = centerX + (textWidth / 2);

        int iconY = (h - size) / 2;
        int iconX;

        if (iconLocation == ICON_RIGHT) {
            iconX = textRight + iconGap;
            int maxX = w - padding - size;
            if (iconX > maxX) {
                iconX = maxX;
            }
        } else {
            iconX = textLeft - iconGap - size;
            int minX = padding;
            if (iconX < minX) {
                iconX = minX;
            }
        }

        return new Rectangle(iconX, iconY, size, size);
    }

    // ------------------------
    // Drawing helpers
    // ------------------------

    public Color getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(Color strokeColor) {
        Color old = this.strokeColor;
        this.strokeColor = strokeColor;
        firePropertyChange("strokeColor", old, strokeColor);
        repaint();
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        float old = this.strokeWidth;
        this.strokeWidth = Math.max(0f, strokeWidth);
        firePropertyChange("strokeWidth", old, this.strokeWidth);
        repaint();
    }

    private void drawCenteredText(Graphics2D g2, Rectangle rect, Color textColor) {
        g2.setFont(getFont());
        g2.setColor(textColor);

        FontMetrics fm = g2.getFontMetrics();
        String safeText = text != null ? text : "";

        int textWidth = fm.stringWidth(safeText);
        int x = rect.x + (rect.width - textWidth) / 2;
        int y = rect.y + ((rect.height - fm.getHeight()) / 2) + fm.getAscent();

        g2.drawString(safeText, x, y);
    }

    private Image loadIconImage() {
        if (iconPath == null || iconPath.trim().isEmpty()) {
            return null;
        }

        try {
            return ImageLoader.getInstance().loadImage(iconPath);
        } catch (Exception ignored) {
        }

        return null;
    }

    private void paintIcon(Graphics2D g2, Rectangle rect, boolean enabled) {
        Image img = loadIconImage();
        if (img == null) {
            return;
        }

        BufferedImage bi = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ig = bi.createGraphics();
        ig.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        ig.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        ig.drawImage(img, 0, 0, rect.width, rect.height, null);
        ig.setComposite(AlphaComposite.SrcIn);

        Color tint = iconColor != null ? iconColor : getForeground();
        if (tint == null) {
            tint = Color.WHITE;
        }
        if (!enabled) {
            tint = new Color(180, 180, 180);
        }

        ig.setColor(tint);
        ig.fillRect(0, 0, rect.width, rect.height);
        ig.dispose();

        g2.drawImage(bi, rect.x, rect.y, null);
    }
}