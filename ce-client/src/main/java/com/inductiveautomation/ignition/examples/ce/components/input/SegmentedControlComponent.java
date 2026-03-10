package com.inductiveautomation.ignition.examples.ce.components.input;

import com.inductiveautomation.ignition.client.images.ImageLoader;
import com.inductiveautomation.ignition.common.BasicDataset;
import com.inductiveautomation.ignition.common.Dataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public class SegmentedControlComponent extends JComponent
        implements MouseListener, MouseMotionListener, KeyListener, ComponentListener {

    public static final int ORIENTATION_HORIZONTAL = 0;
    public static final int ORIENTATION_VERTICAL = 1;

    public static final int ICON_LEFT = 0;
    public static final int ICON_RIGHT = 1;

    private Dataset items = createDefaultDataset();
    private int selectedIndex = 0;
    private int orientation = ORIENTATION_HORIZONTAL;

    private Color selectedBackground = Color.WHITE;
    private Color selectedForeground = new Color(60, 60, 60);
    private Color selectedIconColor = new Color(60, 60, 60);

    private Color hoverBackground = new Color(0, 0, 0, 16);
    private Color borderColor = new Color(170, 170, 170);
    private Color dividerColor = new Color(190, 190, 190);

    private Color iconColor = new Color(105, 105, 110);

    private Font selectedFont = new Font("Dialog", Font.BOLD, 12);

    private float borderWidth = 1f;
    private int cornerRadius = 10; // -1 = pill
    private int segmentGap = 0;
    private int padding = 2;
    private int iconSize = 14;
    private int iconGap = 6;
    private int iconLocation = ICON_LEFT;

    private boolean showDividers = true;
    private boolean animateSelection = true;

    private int hoveredIndex = -1;
    private int pressedIndex = -1;

    private float animX = -1f;
    private float animY = -1f;
    private float animW = 0f;
    private float animH = 0f;
    private Rectangle animationTarget = null;

    private final Timer animationTimer;

    public SegmentedControlComponent() {
        setPreferredSize(new Dimension(250, 40));
        setMinimumSize(new Dimension(90, 32));

        setBackground(new Color(235, 235, 238));
        setForeground(new Color(95, 95, 100));
        setFont(new Font("Dialog", Font.BOLD, 12));

        setFocusable(true);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        addComponentListener(this);

        animationTimer = new Timer(15, e -> stepAnimation());
    }

    // ------------------------
    // Properties
    // ------------------------

    public Dataset getItems() {
        return items;
    }

    public void setItems(Dataset items) {
        Dataset old = this.items;
        this.items = (items != null) ? items : createDefaultDataset();
        clampSelectedIndex();

        firePropertyChange("items", old, this.items);
        firePropertyChange("selectedValue", null, getSelectedValue());

        hoveredIndex = -1;
        pressedIndex = -1;
        updateAnimationTarget(false);
        revalidate();
        repaint();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        int maxIndex = getItemCount() - 1;

        int old = this.selectedIndex;
        String oldValue = getSelectedValue();

        if (selectedIndex < -1) {
            selectedIndex = -1;
        }
        if (selectedIndex > maxIndex) {
            selectedIndex = maxIndex;
        }

        if (old == selectedIndex) {
            return;
        }

        this.selectedIndex = selectedIndex;

        firePropertyChange("selectedIndex", old, this.selectedIndex);
        firePropertyChange("selectedValue", oldValue, getSelectedValue());

        updateAnimationTarget(true);
        repaint();
    }

    public String getSelectedValue() {
        if (selectedIndex >= 0 && selectedIndex < getItemCount()) {
            String label = getLabelAt(selectedIndex);
            return label != null ? label : "";
        }
        return "";
    }

    public void setSelectedValue(String selectedValue) {
        String old = getSelectedValue();
        int newIndex = -1;

        if (selectedValue != null) {
            String target = selectedValue.trim();
            for (int i = 0; i < getItemCount(); i++) {
                String label = getLabelAt(i);
                if (label != null && label.equals(target)) {
                    newIndex = i;
                    break;
                }
            }
        }

        setSelectedIndex(newIndex);

        if (!old.equals(getSelectedValue())) {
            firePropertyChange("selectedValue", old, getSelectedValue());
        }
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        int old = this.orientation;
        this.orientation = (orientation == ORIENTATION_VERTICAL) ? ORIENTATION_VERTICAL : ORIENTATION_HORIZONTAL;
        firePropertyChange("orientation", old, this.orientation);
        updateAnimationTarget(false);
        revalidate();
        repaint();
    }

    public Color getHoverBackground() {
        return hoverBackground;
    }

    public void setHoverBackground(Color hoverBackground) {
        Color old = this.hoverBackground;
        this.hoverBackground = (hoverBackground != null) ? hoverBackground : new Color(0, 0, 0, 16);
        firePropertyChange("hoverBackground", old, this.hoverBackground);
        repaint();
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        Color old = this.borderColor;
        this.borderColor = (borderColor != null) ? borderColor : new Color(170, 170, 170);
        firePropertyChange("borderColor", old, this.borderColor);
        repaint();
    }

    public Color getDividerColor() {
        return dividerColor;
    }

    public void setDividerColor(Color dividerColor) {
        Color old = this.dividerColor;
        this.dividerColor = (dividerColor != null) ? dividerColor : new Color(190, 190, 190);
        firePropertyChange("dividerColor", old, this.dividerColor);
        repaint();
    }

    public Color getIconColor() {
        return iconColor;
    }

    public void setIconColor(Color iconColor) {
        Color old = this.iconColor;
        this.iconColor = (iconColor != null) ? iconColor : new Color(105, 105, 110);
        firePropertyChange("iconColor", old, this.iconColor);
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

    public int getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(int cornerRadius) {
        int old = this.cornerRadius;
        this.cornerRadius = Math.max(-1, cornerRadius);
        firePropertyChange("cornerRadius", old, this.cornerRadius);
        repaint();
    }

    public int getSegmentGap() {
        return segmentGap;
    }

    public void setSegmentGap(int segmentGap) {
        int old = this.segmentGap;
        this.segmentGap = Math.max(0, segmentGap);
        firePropertyChange("segmentGap", old, this.segmentGap);
        repaint();
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        int old = this.padding;
        this.padding = Math.max(0, padding);
        firePropertyChange("padding", old, this.padding);
        revalidate();
        repaint();
    }

    public int getIconSize() {
        return iconSize;
    }

    public void setIconSize(int iconSize) {
        int old = this.iconSize;
        this.iconSize = Math.max(8, iconSize);
        firePropertyChange("iconSize", old, this.iconSize);
        repaint();
    }

    public int getIconGap() {
        return iconGap;
    }

    public void setIconGap(int iconGap) {
        int old = this.iconGap;
        this.iconGap = Math.max(-1, iconGap);
        firePropertyChange("iconGap", old, this.iconGap);
        repaint();
    }

    public int getIconLocation() {
        return iconLocation;
    }

    public void setIconLocation(int iconLocation) {
        int old = this.iconLocation;
        this.iconLocation = (iconLocation == ICON_RIGHT) ? ICON_RIGHT : ICON_LEFT;
        firePropertyChange("iconLocation", old, this.iconLocation);
        repaint();
    }

    public boolean isShowDividers() {
        return showDividers;
    }

    public void setShowDividers(boolean showDividers) {
        boolean old = this.showDividers;
        this.showDividers = showDividers;
        firePropertyChange("showDividers", old, this.showDividers);
        repaint();
    }

    public boolean isAnimateSelection() {
        return animateSelection;
    }

    public void setAnimateSelection(boolean animateSelection) {
        boolean old = this.animateSelection;
        this.animateSelection = animateSelection;
        firePropertyChange("animateSelection", old, this.animateSelection);
        repaint();
    }

    public Color getSelectedBackground() {
        return selectedBackground;
    }

    public void setSelectedBackground(Color selectedBackground) {
        Color old = this.selectedBackground;
        this.selectedBackground = (selectedBackground != null) ? selectedBackground : Color.WHITE;
        firePropertyChange("selectedBackground", old, this.selectedBackground);
        repaint();
    }

    public Color getSelectedForeground() {
        return selectedForeground;
    }

    public void setSelectedForeground(Color selectedForeground) {
        Color old = this.selectedForeground;
        this.selectedForeground = (selectedForeground != null) ? selectedForeground : new Color(60, 60, 60);
        firePropertyChange("selectedForeground", old, this.selectedForeground);
        repaint();
    }

    public Color getSelectedIconColor() {
        return selectedIconColor;
    }

    public void setSelectedIconColor(Color selectedIconColor) {
        Color old = this.selectedIconColor;
        this.selectedIconColor = (selectedIconColor != null) ? selectedIconColor : new Color(60, 60, 60);
        firePropertyChange("selectedIconColor", old, this.selectedIconColor);
        repaint();
    }

    public Font getSelectedFont() {
        return selectedFont;
    }

    public void setSelectedFont(Font selectedFont) {
        Font old = this.selectedFont;
        this.selectedFont = (selectedFont != null) ? selectedFont : new Font("Dialog", Font.BOLD, 12);
        firePropertyChange("selectedFont", old, this.selectedFont);
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

        if (enabled) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
            hoveredIndex = -1;
            pressedIndex = -1;
        }

        firePropertyChange("enabled", old, enabled);
        repaint();
    }

    // ------------------------
    // Dataset helpers
    // ------------------------

    private int getItemCount() {
        return items != null ? items.getRowCount() : 0;
    }

    private int findColumnIgnoreCase(String name) {
        if (items == null || name == null) {
            return -1;
        }

        for (int i = 0; i < items.getColumnCount(); i++) {
            String col = items.getColumnName(i);
            if (col != null && col.trim().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    private Object getValueAt(int row, String columnName) {
        if (items == null || row < 0 || row >= items.getRowCount()) {
            return null;
        }

        int col = findColumnIgnoreCase(columnName);
        if (col < 0 || col >= items.getColumnCount()) {
            return null;
        }

        try {
            return items.getValueAt(row, col);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String getLabelAt(int row) {
        Object value = getValueAt(row, "label");
        if (value == null) {
            return "";
        }
        return String.valueOf(value).trim();
    }

    private String getIconPathAt(int row) {
        Object value = getValueAt(row, "icon");
        if (value == null) {
            return "";
        }
        return String.valueOf(value).trim();
    }

    private static Dataset createDefaultDataset() {
        String[] columnNames = new String[]{"label", "icon"};
        Class<?>[] columnTypes = new Class<?>[]{String.class, String.class};

        Object[][] data = new Object[][]{
                {"Calendar", ""},
                {"Date Range", ""}
        };

        return new BasicDataset(columnNames, columnTypes, data);
    }

    // ------------------------
    // Helpers
    // ------------------------

    private void clampSelectedIndex() {
        int max = getItemCount() - 1;
        if (max < 0) {
            selectedIndex = -1;
        } else if (selectedIndex > max) {
            selectedIndex = max;
        } else if (selectedIndex < -1) {
            selectedIndex = -1;
        }
    }

    private Rectangle getInnerBounds() {
        float half = borderWidth / 2f;
        int x = Math.round(half) + padding;
        int y = Math.round(half) + padding;
        int w = getWidth() - Math.round(borderWidth) - (padding * 2);
        int h = getHeight() - Math.round(borderWidth) - (padding * 2);
        return new Rectangle(x, y, Math.max(0, w), Math.max(0, h));
    }

    private Rectangle getSegmentBounds(int index) {
        int count = getItemCount();
        if (index < 0 || index >= count) {
            return new Rectangle();
        }

        Rectangle inner = getInnerBounds();
        if (count <= 0) {
            return new Rectangle();
        }

        if (orientation == ORIENTATION_VERTICAL) {
            int totalGap = Math.max(0, count - 1) * segmentGap;
            int availableH = inner.height - totalGap;
            int cellH = availableH / count;
            int rem = availableH % count;

            int y = inner.y;
            for (int i = 0; i < index; i++) {
                y += cellH + (i < rem ? 1 : 0) + segmentGap;
            }

            int thisH = cellH + (index < rem ? 1 : 0);
            return new Rectangle(inner.x, y, inner.width, thisH);
        } else {
            int totalGap = Math.max(0, count - 1) * segmentGap;
            int availableW = inner.width - totalGap;
            int cellW = availableW / count;
            int rem = availableW % count;

            int x = inner.x;
            for (int i = 0; i < index; i++) {
                x += cellW + (i < rem ? 1 : 0) + segmentGap;
            }

            int thisW = cellW + (index < rem ? 1 : 0);
            return new Rectangle(x, inner.y, thisW, inner.height);
        }
    }

    private int getSegmentIndexAt(Point p) {
        if (p == null) {
            return -1;
        }

        for (int i = 0; i < getItemCount(); i++) {
            if (getSegmentBounds(i).contains(p)) {
                return i;
            }
        }
        return -1;
    }

    private int getEffectiveRadius(Rectangle r) {
        if (cornerRadius == -1) {
            return Math.max(4, Math.min(r.width, r.height));
        }
        return Math.max(0, Math.min(cornerRadius, Math.min(r.width, r.height)));
    }

    private void selectIndexFromInteraction(int index) {
        if (index >= 0) {
            setSelectedIndex(index);
        }
    }

    private void moveSelection(int delta) {
        int count = getItemCount();
        if (count == 0) {
            return;
        }

        int idx = selectedIndex;
        if (idx < 0) {
            idx = 0;
        } else {
            idx += delta;
        }

        idx = Math.max(0, Math.min(count - 1, idx));
        setSelectedIndex(idx);
    }

    private void updateAnimationTarget(boolean animate) {
        if (selectedIndex < 0) {
            animationTarget = null;
            animationTimer.stop();
            animX = -1f;
            animY = -1f;
            animW = 0f;
            animH = 0f;
            return;
        }

        Rectangle r = getSegmentBounds(selectedIndex);
        animationTarget = new Rectangle(r);

        if (!animate || !animateSelection) {
            animX = r.x;
            animY = r.y;
            animW = r.width;
            animH = r.height;
            animationTimer.stop();
            return;
        }

        if (animX < 0f || animY < 0f || animW <= 0f || animH <= 0f) {
            animX = r.x;
            animY = r.y;
            animW = r.width;
            animH = r.height;
            animationTimer.stop();
            return;
        }

        if (!animationTimer.isRunning()) {
            animationTimer.start();
        }
    }

    private void stepAnimation() {
        if (animationTarget == null) {
            animationTimer.stop();
            return;
        }

        float ease = 0.23f;

        animX += (animationTarget.x - animX) * ease;
        animY += (animationTarget.y - animY) * ease;
        animW += (animationTarget.width - animW) * ease;
        animH += (animationTarget.height - animH) * ease;

        boolean done =
                Math.abs(animationTarget.x - animX) < 0.5f &&
                        Math.abs(animationTarget.y - animY) < 0.5f &&
                        Math.abs(animationTarget.width - animW) < 0.5f &&
                        Math.abs(animationTarget.height - animH) < 0.5f;

        if (done) {
            animX = animationTarget.x;
            animY = animationTarget.y;
            animW = animationTarget.width;
            animH = animationTarget.height;
            animationTimer.stop();
        }

        repaint();
    }

    private Color withAlpha(Color color, int alpha) {
        if (color == null) {
            color = Color.GRAY;
        }
        alpha = Math.max(0, Math.min(255, alpha));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    private Image loadIcon(String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }

        try {
            return ImageLoader.getInstance().loadImage(path.trim());
        } catch (Exception ignored) {
            return null;
        }
    }

    private void paintTintedIcon(Graphics2D g2, Image img, int x, int y, int size, Color tint) {
        if (img == null || size <= 0) {
            return;
        }

        BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ig = bi.createGraphics();
        ig.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        ig.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        ig.drawImage(img, 0, 0, size, size, null);
        ig.setComposite(AlphaComposite.SrcIn);
        ig.setColor(tint != null ? tint : Color.GRAY);
        ig.fillRect(0, 0, size, size);
        ig.dispose();

        g2.drawImage(bi, x, y, null);
    }

    private void paintSegmentContent(Graphics2D g2, Rectangle r, int index, boolean selected, Color textColor, Color iconTint) {
        String text = getLabelAt(index);
        String iconPath = getIconPathAt(index);
        Image icon = loadIcon(iconPath);

        boolean hasText = text != null && text.trim().length() > 0;
        boolean hasIcon = icon != null;

        Font baseFont = getFont() != null ? getFont() : new Font("Dialog", Font.BOLD, 12);
        Font fontToUse = selected
                ? (selectedFont != null ? selectedFont : baseFont)
                : baseFont;

        g2.setFont(fontToUse);
        FontMetrics fm = g2.getFontMetrics(fontToUse);

        if (hasIcon && !hasText) {
            int ix = r.x + ((r.width - iconSize) / 2);
            int iy = r.y + ((r.height - iconSize) / 2);
            paintTintedIcon(g2, icon, ix, iy, iconSize, iconTint);
            return;
        }

        if (!hasIcon && hasText) {
            g2.setColor(textColor);
            int tx = r.x + ((r.width - fm.stringWidth(text)) / 2);
            int ty = r.y + ((r.height - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(text, tx, ty);
            return;
        }

        if (hasIcon && hasText) {
            int textW = fm.stringWidth(text);
            int textX = r.x + ((r.width - textW) / 2);
            int textY = r.y + ((r.height - fm.getHeight()) / 2) + fm.getAscent();

            int edgePadding = Math.max(4, padding + 4);
            int iconY = r.y + ((r.height - iconSize) / 2);
            int iconX;

            if (iconGap == -1) {
                if (iconLocation == ICON_RIGHT) {
                    iconX = r.x + r.width - edgePadding - iconSize;
                } else {
                    iconX = r.x + edgePadding;
                }
            } else {
                if (iconLocation == ICON_RIGHT) {
                    iconX = textX + textW + iconGap;
                    int maxX = r.x + r.width - edgePadding - iconSize;
                    if (iconX > maxX) {
                        iconX = maxX;
                    }
                } else {
                    iconX = textX - iconGap - iconSize;
                    int minX = r.x + edgePadding;
                    if (iconX < minX) {
                        iconX = minX;
                    }
                }
            }

            paintTintedIcon(g2, icon, iconX, iconY, iconSize, iconTint);

            g2.setColor(textColor);
            g2.drawString(text, textX, textY);
            return;
        }

        g2.setColor(textColor);
        int dashW = 8;
        int dashH = 1;
        int dx = r.x + ((r.width - dashW) / 2);
        int dy = r.y + (r.height / 2);
        g2.fillRect(dx, dy, dashW, dashH);
    }

    // ------------------------
    // Paint
    // ------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (selectedIndex >= 0 && !animationTimer.isRunning()) {
            updateAnimationTarget(false);
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        int w = getWidth();
        int h = getHeight();

        Color bg = getBackground() != null ? getBackground() : new Color(235, 235, 238);
        Color fg = getForeground() != null ? getForeground() : new Color(95, 95, 100);
        Color selBg = selectedBackground != null ? selectedBackground : Color.WHITE;
        Color selFg = selectedForeground != null ? selectedForeground : new Color(60, 60, 60);
        Color selIcon = selectedIconColor != null ? selectedIconColor : selFg;
        Color hovBg = hoverBackground != null ? hoverBackground : new Color(0, 0, 0, 16);
        Color bdr = borderColor != null ? borderColor : new Color(170, 170, 170);
        Color div = dividerColor != null ? dividerColor : new Color(190, 190, 190);
        Color unselIcon = iconColor != null ? iconColor : fg;

        if (!isEnabled()) {
            bg = withAlpha(bg, 150);
            fg = new Color(165, 165, 170);
            selBg = withAlpha(selBg, 150);
            selFg = new Color(185, 185, 190);
            selIcon = new Color(185, 185, 190);
            hovBg = withAlpha(hovBg, 50);
            bdr = withAlpha(bdr, 120);
            div = withAlpha(div, 100);
            unselIcon = new Color(165, 165, 170);
        }

        int outerRadius = getEffectiveRadius(new Rectangle(0, 0, w, h));

        Shape outer = new RoundRectangle2D.Float(
                borderWidth / 2f,
                borderWidth / 2f,
                w - borderWidth,
                h - borderWidth,
                outerRadius,
                outerRadius
        );

        g2.setColor(bg);
        g2.fill(outer);

        if (selectedIndex >= 0 && selectedIndex < getItemCount()) {
            Rectangle rr = new Rectangle(Math.round(animX), Math.round(animY), Math.round(animW), Math.round(animH));
            int rrRadius = getEffectiveRadius(rr);

            Shape selectedShape = new RoundRectangle2D.Float(
                    rr.x,
                    rr.y,
                    rr.width,
                    rr.height,
                    rrRadius,
                    rrRadius
            );

            g2.setColor(selBg);
            g2.fill(selectedShape);
        }

        if (hoveredIndex >= 0 && hoveredIndex < getItemCount() && hoveredIndex != selectedIndex) {
            Rectangle r = getSegmentBounds(hoveredIndex);
            int rRadius = getEffectiveRadius(r);

            g2.setColor(hovBg);
            g2.fill(new RoundRectangle2D.Float(r.x, r.y, r.width, r.height, rRadius, rRadius));
        }

        if (pressedIndex >= 0 && pressedIndex < getItemCount()) {
            Rectangle r = getSegmentBounds(pressedIndex);
            int rRadius = getEffectiveRadius(r);

            g2.setColor(withAlpha(Color.BLACK, isEnabled() ? 18 : 8));
            g2.fill(new RoundRectangle2D.Float(r.x, r.y, r.width, r.height, rRadius, rRadius));
        }

        if (showDividers && getItemCount() > 1) {
            g2.setColor(div);
            g2.setStroke(new BasicStroke(1f));

            for (int i = 1; i < getItemCount(); i++) {
                Rectangle prev = getSegmentBounds(i - 1);
                Rectangle curr = getSegmentBounds(i);

                if (orientation == ORIENTATION_VERTICAL) {
                    int y = (prev.y + prev.height + curr.y) / 2;
                    int x1 = getInnerBounds().x + 5;
                    int x2 = getInnerBounds().x + getInnerBounds().width - 5;
                    g2.drawLine(x1, y, x2, y);
                } else {
                    int x = (prev.x + prev.width + curr.x) / 2;
                    int y1 = getInnerBounds().y + 5;
                    int y2 = getInnerBounds().y + getInnerBounds().height - 5;
                    g2.drawLine(x, y1, x, y2);
                }
            }
        }

        for (int i = 0; i < getItemCount(); i++) {
            Rectangle r = getSegmentBounds(i);
            boolean selected = (i == selectedIndex);

            Color textColor = selected ? selFg : fg;
            Color thisIconTint = selected ? selIcon : unselIcon;

            paintSegmentContent(g2, r, i, selected, textColor, thisIconTint);
        }

        if (borderWidth > 0f) {
            g2.setColor(bdr);
            g2.setStroke(new BasicStroke(borderWidth));
            g2.draw(outer);
        }

        g2.dispose();
    }

    // ------------------------
    // Size
    // ------------------------

    @Override
    public Dimension getPreferredSize() {
        return orientation == ORIENTATION_VERTICAL
                ? new Dimension(120, 110)
                : new Dimension(250, 40);
    }

    @Override
    public Dimension getMinimumSize() {
        return orientation == ORIENTATION_VERTICAL
                ? new Dimension(70, 70)
                : new Dimension(90, 32);
    }

    // ------------------------
    // Mouse events
    // ------------------------

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!isEnabled() || !SwingUtilities.isLeftMouseButton(e)) {
            return;
        }

        requestFocusInWindow();
        pressedIndex = getSegmentIndexAt(e.getPoint());
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!isEnabled() || !SwingUtilities.isLeftMouseButton(e)) {
            pressedIndex = -1;
            repaint();
            return;
        }

        int releasedIndex = getSegmentIndexAt(e.getPoint());
        if (pressedIndex >= 0 && pressedIndex == releasedIndex) {
            selectIndexFromInteraction(releasedIndex);
        }

        pressedIndex = -1;
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (!isEnabled()) {
            return;
        }
        hoveredIndex = getSegmentIndexAt(e.getPoint());
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (hoveredIndex != -1 || pressedIndex != -1) {
            hoveredIndex = -1;
            pressedIndex = -1;
            repaint();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!isEnabled()) {
            return;
        }
        int old = hoveredIndex;
        hoveredIndex = getSegmentIndexAt(e.getPoint());
        if (old != hoveredIndex) {
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!isEnabled()) {
            return;
        }
        int old = hoveredIndex;
        hoveredIndex = getSegmentIndexAt(e.getPoint());
        if (old != hoveredIndex) {
            repaint();
        }
    }

    // ------------------------
    // Keyboard events
    // ------------------------

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!isEnabled()) {
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (orientation == ORIENTATION_HORIZONTAL) {
                    moveSelection(-1);
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (orientation == ORIENTATION_HORIZONTAL) {
                    moveSelection(1);
                }
                break;
            case KeyEvent.VK_UP:
                if (orientation == ORIENTATION_VERTICAL) {
                    moveSelection(-1);
                }
                break;
            case KeyEvent.VK_DOWN:
                if (orientation == ORIENTATION_VERTICAL) {
                    moveSelection(1);
                }
                break;
            case KeyEvent.VK_HOME:
                if (getItemCount() > 0) {
                    setSelectedIndex(0);
                }
                break;
            case KeyEvent.VK_END:
                if (getItemCount() > 0) {
                    setSelectedIndex(getItemCount() - 1);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { }

    // ------------------------
    // Resize handling
    // ------------------------

    @Override
    public void componentResized(ComponentEvent e) {
        updateAnimationTarget(false);
        repaint();
    }

    @Override
    public void componentMoved(ComponentEvent e) { }

    @Override
    public void componentShown(ComponentEvent e) {
        updateAnimationTarget(false);
        repaint();
    }

    @Override
    public void componentHidden(ComponentEvent e) { }
}