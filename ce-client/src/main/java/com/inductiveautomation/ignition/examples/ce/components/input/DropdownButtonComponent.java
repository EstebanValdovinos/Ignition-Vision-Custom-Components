package com.inductiveautomation.ignition.examples.ce.components.input;

import com.inductiveautomation.ignition.client.images.ImageLoader;
import com.inductiveautomation.ignition.common.BasicDataset;
import com.inductiveautomation.ignition.common.Dataset;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public class DropdownButtonComponent extends JComponent
        implements MouseListener, MouseMotionListener, KeyListener {

    public static final int ICON_LEFT = 0;
    public static final int ICON_RIGHT = 1;

    // ---------------------------------
    // Header / same family as IOSButton
    // ---------------------------------
    private String text = "";
    private boolean pressed = false;
    private int cornerRadius = 10;

    private String iconPath = "";
    private int iconLocation = ICON_LEFT;
    private Color iconColor = new Color(33, 37, 41);
    private int iconSize = 16;
    private int iconGap = 8;

    private Color strokeColor = new Color(200, 200, 200);
    private float strokeWidth = 1f;

    // ---------------------------------
    // Dropdown data
    // ---------------------------------
    private Dataset data = createDefaultDataset();

    private int selectedIndex = -1;
    private int hoverIndex = -1;
    private boolean open = false;

    // ---------------------------------
    // Appearance
    // ---------------------------------
    private int headerHeight = 40;
    private int rowHeight = 30;

    private Color headerBackground = new Color(255, 255, 255);
    private Color listBackground = Color.WHITE;
    private Color foreground = new Color(73, 80, 87);
    private Color hoverBackground = new Color(248, 249, 250);
    private Color hoverForeground = new Color(22, 24, 27);
    private Color placeholderColor = new Color(180, 180, 180);

    private String placeholderText = "< Select Option >";

    private String selectedItemIconPath = "";
    private Color selectedItemIconColor = new Color(13, 110, 253);
    private int selectedItemIconPosition = ICON_LEFT;

    private int itemIconPosition = ICON_RIGHT;
    private int itemIconSize = 16;

    private boolean showTopNotch = true;
    private int topNotchWidth = 12;
    private int topNotchHeight = 6;

    // ---------------------------------
    // Popup
    // ---------------------------------
    private JPopupMenu popupMenu;
    private PopupListPanel popupPanel;
    private long lastToggleTime = 0L;

    public DropdownButtonComponent() {
        popupPanel = new PopupListPanel();

        popupMenu = new JPopupMenu();
        popupMenu.setOpaque(false);
        popupMenu.setBorder(BorderFactory.createEmptyBorder());
        popupMenu.setLayout(new BorderLayout());
        popupMenu.add(popupPanel, BorderLayout.CENTER);

        setPreferredSize(new Dimension(335, headerHeight));
        setMinimumSize(new Dimension(140, headerHeight));

        super.setForeground(this.foreground);
        super.setBackground(headerBackground);

        setFont(new Font("SansSerif", Font.PLAIN, 13));
        setFocusable(true);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
    }

    private void refreshPopupPanel() {
        if (popupPanel != null) {
            popupPanel.revalidate();
            popupPanel.repaint();
        }
    }

    // ---------------------------------
    // Defaults
    // ---------------------------------

    private static Dataset createDefaultDataset() {
        String[] columnNames = new String[]{"label", "iconPath", "value"};
        Class<?>[] columnTypes = new Class<?>[]{String.class, String.class, Integer.class};

        Object[][] data = new Object[][]{
                {
                        "Option 1",
                        "Option 2",
                        "Another Option",
                        "One More Option",
                        "-",
                        "Separate Link"
                },
                {
                        "",
                        "",
                        "",
                        "Builtin/icons/16/lightbulb.png",
                        "",
                        ""
                },
                {
                        1,
                        2,
                        3,
                        4,
                        5,
                        6
                }
        };

        return new BasicDataset(columnNames, columnTypes, data);
    }

    // ---------------------------------
    // Properties
    // ---------------------------------

    public String getText() {
        return text;
    }

    public void setText(String text) {
        String old = this.text;
        this.text = text != null ? text : "";
        firePropertyChange("text", old, this.text);
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
        refreshPopupPanel();
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        String old = this.iconPath;
        this.iconPath = iconPath != null ? iconPath : "";
        firePropertyChange("iconPath", old, this.iconPath);
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

    public Color getIconColor() {
        return iconColor;
    }

    public void setIconColor(Color iconColor) {
        Color old = this.iconColor;
        this.iconColor = (iconColor != null) ? iconColor : new Color(33, 37, 41);
        firePropertyChange("iconColor", old, this.iconColor);
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
        this.iconGap = Math.max(0, iconGap);
        firePropertyChange("iconGap", old, this.iconGap);
        repaint();
    }

    public Color getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(Color strokeColor) {
        Color old = this.strokeColor;
        this.strokeColor = (strokeColor != null) ? strokeColor : new Color(200, 200, 200);
        firePropertyChange("strokeColor", old, this.strokeColor);
        repaint();
        refreshPopupPanel();
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        float old = this.strokeWidth;
        this.strokeWidth = Math.max(0f, strokeWidth);
        firePropertyChange("strokeWidth", old, this.strokeWidth);
        repaint();
        refreshPopupPanel();
    }

    public Dataset getData() {
        return data;
    }

    public void setData(Dataset data) {
        Dataset old = this.data;
        this.data = (data != null) ? data : createDefaultDataset();
        clampSelectedIndex();
        firePropertyChange("data", old, this.data);
        refreshPopupPanel();
        repaint();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        int old = this.selectedIndex;

        if (selectedIndex < 0) {
            this.selectedIndex = -1;
        } else {
            this.selectedIndex = normalizeSelectableIndex(selectedIndex);
        }

        firePropertyChange("selectedIndex", old, this.selectedIndex);
        firePropertyChange("selectedLabel", null, getSelectedLabel());
        refreshPopupPanel();
        repaint();
    }

    public String getSelectedLabel() {
        if (isSelectableRow(selectedIndex)) {
            return getLabelAt(selectedIndex);
        }
        return "";
    }

    public void setSelectedLabel(String selectedLabel) {
        String old = getSelectedLabel();
        int idx = findRowByLabel(selectedLabel);
        setSelectedIndex(idx);
        firePropertyChange("selectedLabel", old, getSelectedLabel());
    }

    public int getHoverIndex() {
        return hoverIndex;
    }

    public void setHoverIndex(int hoverIndex) {
        int old = this.hoverIndex;
        if (hoverIndex < 0 || hoverIndex >= getRowCount() || isDividerRow(hoverIndex)) {
            hoverIndex = -1;
        }
        this.hoverIndex = hoverIndex;
        firePropertyChange("hoverIndex", old, this.hoverIndex);
        refreshPopupPanel();
        repaint();
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        boolean old = this.open;
        this.open = open;
        firePropertyChange("open", old, this.open);
        repaint();
    }

    public int getHeaderHeight() {
        return headerHeight;
    }

    public void setHeaderHeight(int headerHeight) {
        int old = this.headerHeight;
        this.headerHeight = Math.max(28, headerHeight);
        firePropertyChange("headerHeight", old, this.headerHeight);

        setPreferredSize(new Dimension(getPreferredSize().width, this.headerHeight));
        setMinimumSize(new Dimension(getMinimumSize().width, this.headerHeight));
        revalidate();
        repaint();
    }

    public int getRowHeight() {
        return rowHeight;
    }

    public void setRowHeight(int rowHeight) {
        int old = this.rowHeight;
        this.rowHeight = Math.max(22, rowHeight);
        firePropertyChange("rowHeight", old, this.rowHeight);
        refreshPopupPanel();
    }

    public Color getHeaderBackground() {
        return headerBackground;
    }

    public void setHeaderBackground(Color headerBackground) {
        Color old = this.headerBackground;
        this.headerBackground = (headerBackground != null) ? headerBackground : new Color(233, 236, 239);
        firePropertyChange("headerBackground", old, this.headerBackground);
        repaint();
    }

    public Color getListBackground() {
        return listBackground;
    }

    public void setListBackground(Color listBackground) {
        Color old = this.listBackground;
        this.listBackground = (listBackground != null) ? listBackground : Color.WHITE;
        firePropertyChange("listBackground", old, this.listBackground);
        refreshPopupPanel();
    }

    @Override
    public Color getForeground() {
        return foreground;
    }

    @Override
    public void setForeground(Color foreground) {
        Color old = this.foreground;
        this.foreground = (foreground != null) ? foreground : new Color(73, 80, 87);
        super.setForeground(this.foreground);
        firePropertyChange("foreground", old, this.foreground);
        refreshPopupPanel();
        repaint();
    }

    public Color getHoverBackground() {
        return hoverBackground;
    }

    public void setHoverBackground(Color hoverBackground) {
        Color old = this.hoverBackground;
        this.hoverBackground = (hoverBackground != null) ? hoverBackground : new Color(248, 249, 250);
        firePropertyChange("hoverBackground", old, this.hoverBackground);
        refreshPopupPanel();
    }

    public Color getHoverForeground() {
        return hoverForeground;
    }

    public void setHoverForeground(Color hoverForeground) {
        Color old = this.hoverForeground;
        this.hoverForeground = (hoverForeground != null) ? hoverForeground : new Color(22, 24, 27);
        firePropertyChange("hoverForeground", old, this.hoverForeground);
        refreshPopupPanel();
    }

    public String getPlaceholderText() {
        return placeholderText;
    }

    public void setPlaceholderText(String placeholderText) {
        String old = this.placeholderText;
        this.placeholderText = (placeholderText != null) ? placeholderText : "< Select Option >";
        firePropertyChange("placeholderText", old, this.placeholderText);
        repaint();
    }

    public Color getPlaceholderColor() {
        return placeholderColor;
    }

    public void setPlaceholderColor(Color placeholderColor) {
        Color old = this.placeholderColor;
        this.placeholderColor = (placeholderColor != null) ? placeholderColor : new Color(180, 180, 180);
        firePropertyChange("placeholderColor", old, this.placeholderColor);
        repaint();
    }

    public String getSelectedItemIconPath() {
        return selectedItemIconPath;
    }

    public void setSelectedItemIconPath(String selectedItemIconPath) {
        String old = this.selectedItemIconPath;
        this.selectedItemIconPath = (selectedItemIconPath != null) ? selectedItemIconPath : "";
        refreshPopupPanel();
        firePropertyChange("selectedItemIconPath", old, this.selectedItemIconPath);
    }

    public Color getSelectedItemIconColor() {
        return selectedItemIconColor;
    }

    public void setSelectedItemIconColor(Color selectedItemIconColor) {
        Color old = this.selectedItemIconColor;
        this.selectedItemIconColor = (selectedItemIconColor != null) ? selectedItemIconColor : new Color(13, 110, 253);
        refreshPopupPanel();
        firePropertyChange("selectedItemIconColor", old, this.selectedItemIconColor);
    }

    public int getSelectedItemIconPosition() {
        return selectedItemIconPosition;
    }

    public void setSelectedItemIconPosition(int selectedItemIconPosition) {
        int old = this.selectedItemIconPosition;
        this.selectedItemIconPosition = (selectedItemIconPosition == ICON_RIGHT) ? ICON_RIGHT : ICON_LEFT;
        refreshPopupPanel();
        firePropertyChange("selectedItemIconPosition", old, this.selectedItemIconPosition);
    }

    public int getItemIconPosition() {
        return itemIconPosition;
    }

    public void setItemIconPosition(int itemIconPosition) {
        int old = this.itemIconPosition;
        this.itemIconPosition = (itemIconPosition == ICON_LEFT) ? ICON_LEFT : ICON_RIGHT;
        refreshPopupPanel();
        firePropertyChange("itemIconPosition", old, this.itemIconPosition);
    }

    public int getItemIconSize() {
        return itemIconSize;
    }

    public void setItemIconSize(int itemIconSize) {
        int old = this.itemIconSize;
        this.itemIconSize = Math.max(8, itemIconSize);
        refreshPopupPanel();
        firePropertyChange("itemIconSize", old, this.itemIconSize);
    }

    public boolean isShowTopNotch() {
        return showTopNotch;
    }

    public void setShowTopNotch(boolean showTopNotch) {
        boolean old = this.showTopNotch;
        this.showTopNotch = showTopNotch;
        refreshPopupPanel();
        firePropertyChange("showTopNotch", old, this.showTopNotch);
    }

    public int getTopNotchWidth() {
        return topNotchWidth;
    }

    public void setTopNotchWidth(int topNotchWidth) {
        int old = this.topNotchWidth;
        this.topNotchWidth = Math.max(6, topNotchWidth);
        refreshPopupPanel();
        firePropertyChange("topNotchWidth", old, this.topNotchWidth);
    }

    public int getTopNotchHeight() {
        return topNotchHeight;
    }

    public void setTopNotchHeight(int topNotchHeight) {
        int old = this.topNotchHeight;
        this.topNotchHeight = Math.max(0, topNotchHeight);
        refreshPopupPanel();
        firePropertyChange("topNotchHeight", old, this.topNotchHeight);
    }

    @Override
    public void setFont(Font font) {
        Font old = getFont();
        super.setFont(font);
        refreshPopupPanel();
        firePropertyChange("font", old, font);
        repaint();
    }

    @Override
    public void setEnabled(boolean enabled) {
        boolean old = isEnabled();
        super.setEnabled(enabled);
        firePropertyChange("enabled", old, enabled);

        if (!enabled) {
            pressed = false;
            if (popupMenu != null && popupMenu.isVisible()) {
                popupMenu.setVisible(false);
            }
            setCursor(Cursor.getDefaultCursor());
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        repaint();
    }

    // ---------------------------------
    // Dataset helpers
    // ---------------------------------

    private int getRowCount() {
        return data != null ? data.getRowCount() : 0;
    }

    private int findColumnIgnoreCase(String name, int fallbackIndex) {
        if (data == null) {
            return -1;
        }

        for (int i = 0; i < data.getColumnCount(); i++) {
            String col = data.getColumnName(i);
            if (col != null && col.trim().equalsIgnoreCase(name)) {
                return i;
            }
        }

        return Math.min(fallbackIndex, Math.max(0, data.getColumnCount() - 1));
    }

    private Object getValueAt(int row, int col) {
        if (data == null || row < 0 || row >= data.getRowCount() || col < 0 || col >= data.getColumnCount()) {
            return null;
        }
        try {
            return data.getValueAt(row, col);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String getLabelAt(int row) {
        int col = findColumnIgnoreCase("label", 0);
        Object value = getValueAt(row, col);
        return value != null ? String.valueOf(value).trim() : "";
    }

    private String getItemIconPathAt(int row) {
        int col = findColumnIgnoreCase("iconPath", 1);
        Object value = getValueAt(row, col);
        return value != null ? String.valueOf(value).trim() : "";
    }

    private boolean isDividerRow(int row) {
        if (row < 0 || row >= getRowCount()) {
            return false;
        }
        String label = getLabelAt(row);
        return "-".equals(label) || "--".equals(label);
    }

    private boolean isSelectableRow(int row) {
        return row >= 0 && row < getRowCount() && !isDividerRow(row);
    }

    private int normalizeSelectableIndex(int index) {
        if (index < 0 || index >= getRowCount() || isDividerRow(index)) {
            return findFirstSelectableRow();
        }
        return index;
    }

    private int findFirstSelectableRow() {
        for (int i = 0; i < getRowCount(); i++) {
            if (isSelectableRow(i)) {
                return i;
            }
        }
        return -1;
    }

    private int findRowByLabel(String label) {
        if (label == null) {
            return -1;
        }

        for (int i = 0; i < getRowCount(); i++) {
            if (!isDividerRow(i) && label.trim().equals(getLabelAt(i))) {
                return i;
            }
        }
        return -1;
    }

    private void clampSelectedIndex() {
        if (selectedIndex >= 0) {
            selectedIndex = normalizeSelectableIndex(selectedIndex);
        }
    }

    // ---------------------------------
    // Popup helpers
    // ---------------------------------

    private void openPopup() {
        if (!isEnabled() || popupMenu == null || popupPanel == null) {
            return;
        }

        popupPanel.revalidate();
        popupPanel.repaint();

        Dimension popupSize = popupPanel.getPreferredSize();
        int popupW = popupSize.width;
        int popupH = popupSize.height;

        int x = 0;
        int y = getHeight() + 5;

        try {
            Point screenPt = getLocationOnScreen();
            Rectangle screenBounds = getScreenBoundsFor(screenPt);

            int spaceBelow = screenBounds.y + screenBounds.height - (screenPt.y + getHeight());
            int spaceAbove = screenPt.y - screenBounds.y;

            if (spaceBelow < popupH + 8 && spaceAbove > popupH + 8) {
                y = -popupH - 2;
            }

            int desiredRight = screenPt.x + popupW;
            int screenRight = screenBounds.x + screenBounds.width;
            if (desiredRight > screenRight) {
                x -= (desiredRight - screenRight) + 4;
            }

            int desiredLeft = screenPt.x + x;
            if (desiredLeft < screenBounds.x) {
                x += (screenBounds.x - desiredLeft) + 4;
            }
        } catch (IllegalComponentStateException ignored) {
        }

        popupMenu.show(this, x, y);
        setOpen(true);

        popupMenu.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) { }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                setOpen(false);
                setHoverIndex(-1);
                lastToggleTime = System.currentTimeMillis();
                popupMenu.removePopupMenuListener(this);
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                setOpen(false);
                setHoverIndex(-1);
                lastToggleTime = System.currentTimeMillis();
                popupMenu.removePopupMenuListener(this);
            }
        });
    }

    private Rectangle getScreenBoundsFor(Point pointOnScreen) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = ge.getScreenDevices();

        for (GraphicsDevice device : devices) {
            GraphicsConfiguration gc = device.getDefaultConfiguration();
            Rectangle bounds = gc.getBounds();
            if (bounds.contains(pointOnScreen)) {
                Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
                return new Rectangle(
                        bounds.x + insets.left,
                        bounds.y + insets.top,
                        bounds.width - insets.left - insets.right,
                        bounds.height - insets.top - insets.bottom
                );
            }
        }

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        return new Rectangle(0, 0, screen.width, screen.height);
    }

    // ---------------------------------
    // Paint header
    // ---------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        paintHeader(g2);

        g2.dispose();
    }

    private void paintHeader(Graphics2D g2) {
        int w = getWidth();
        int h = headerHeight;

        Color bg = headerBackground;
        Color textColor = foreground;

        if (!isEnabled()) {
            bg = withAlpha(bg, 140);
            textColor = new Color(180, 180, 180);
        } else if (pressed) {
            bg = bg.darker();
        }

        g2.setColor(bg);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));

        if (strokeWidth > 0f) {
            g2.setColor(strokeColor);
            g2.setStroke(new BasicStroke(strokeWidth));
            g2.draw(new RoundRectangle2D.Float(
                    strokeWidth / 2f,
                    strokeWidth / 2f,
                    w - strokeWidth,
                    h - strokeWidth,
                    cornerRadius,
                    cornerRadius
            ));
        }

        String headerText = resolveHeaderText();
        boolean placeholder = (selectedIndex < 0 && (text == null || text.trim().isEmpty()));

        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(placeholder ? placeholderColor : textColor);
        g2.drawString(headerText, 10, (int) (h / 2.0 + fm.getAscent() * 0.35));

        Rectangle iconRect = calculateHeaderIconRect(g2, headerText);
        if (iconRect != null) {
            paintTintedIcon(g2, loadIcon(iconPath), iconRect.x, iconRect.y, iconRect.width, iconColor);
        }

        paintHeaderArrow(g2, w, h, placeholder ? placeholderColor : textColor);
    }

    private void paintHeaderArrow(Graphics2D g2, int w, int h, Color color) {
        int arrowX = w - 20;
        int arrowY = h / 2;
        int arrowSize = 4;

        Polygon poly;
        g2.setColor(color);

        if (open) {
            poly = new Polygon(
                    new int[]{arrowX, arrowX + arrowSize, arrowX - arrowSize},
                    new int[]{arrowY - 2, arrowY + 3, arrowY + 3},
                    3
            );
        } else {
            poly = new Polygon(
                    new int[]{arrowX, arrowX + arrowSize, arrowX - arrowSize},
                    new int[]{arrowY + 3, arrowY - 2, arrowY - 2},
                    3
            );
        }

        g2.fill(poly);
    }

    private String resolveHeaderText() {
        if (text != null && !text.trim().isEmpty()) {
            return text;
        }
        if (selectedIndex >= 0 && isSelectableRow(selectedIndex)) {
            return getSelectedLabel();
        }
        return placeholderText != null ? placeholderText : "< Select Option >";
    }

    private Rectangle calculateHeaderIconRect(Graphics2D g2, String label) {
        Image icon = loadIcon(iconPath);
        if (icon == null) {
            return null;
        }

        FontMetrics fm = g2.getFontMetrics(getFont());
        int textW = fm.stringWidth(label != null ? label : "");
        int centerX = getWidth() / 2;
        int textLeft = centerX - (textW / 2);
        int textRight = centerX + (textW / 2);

        int y = (headerHeight - iconSize) / 2;
        int x;

        if (iconLocation == ICON_RIGHT) {
            x = textRight + iconGap;
            x = Math.min(x, getWidth() - 30 - iconSize);
        } else {
            x = textLeft - iconGap - iconSize;
            x = Math.max(x, 8);
        }

        return new Rectangle(x, y, iconSize, iconSize);
    }

    // ---------------------------------
    // Popup list panel
    // ---------------------------------

    private class PopupListPanel extends JPanel implements MouseListener, MouseMotionListener {

        PopupListPanel() {
            setOpaque(false);
            setBorder(null);
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        @Override
        public Dimension getPreferredSize() {
            int w = Math.max(DropdownButtonComponent.this.getWidth(), 140);
            int listH = getRowCount() * rowHeight;
            int extraTop = (showTopNotch ? topNotchHeight : 0);
            return new Dimension(w, extraTop + listH + 2);
        }

        private int getRowsStartY() {
            return showTopNotch ? topNotchHeight : 0;
        }

        private int rowAt(Point p) {
            if (p == null) {
                return -1;
            }
            int y = p.y - getRowsStartY();
            if (y < 0) {
                return -1;
            }
            int idx = y / rowHeight;
            if (idx < 0 || idx >= getRowCount()) {
                return -1;
            }
            return idx;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int w = getWidth();
            int notchH = showTopNotch ? topNotchHeight : 0;
            int startRowsY = notchH;
            int listH = getRowCount() * rowHeight;

            if (listH <= 0) {
                g2.dispose();
                return;
            }

            float triBaseX = w - 20f;
            float triBaseY = startRowsY;

            if (showTopNotch && topNotchHeight > 0) {
                Path2D tri = new Path2D.Float();
                tri.moveTo(triBaseX - (topNotchWidth / 2f), triBaseY);
                tri.lineTo(triBaseX, triBaseY - topNotchHeight);
                tri.lineTo(triBaseX + (topNotchWidth / 2f), triBaseY);
                tri.closePath();

                g2.setColor(listBackground);
                g2.fill(tri);

                if (strokeWidth > 0f) {
                    g2.setColor(strokeColor);
                    g2.setStroke(new BasicStroke(strokeWidth));
                    g2.draw(tri);

                    g2.setColor(listBackground);
                    g2.setStroke(new BasicStroke(Math.max(2f, strokeWidth + 1f)));
                    g2.drawLine(
                            (int) (triBaseX - (topNotchWidth / 2f) + 1),
                            (int) triBaseY,
                            (int) (triBaseX + (topNotchWidth / 2f) - 1),
                            (int) triBaseY
                    );
                }
            }

            for (int i = 6; i >= 1; i--) {
                int alpha = (int) (20f * (i / 6f));
                g2.setColor(new Color(0, 0, 0, alpha));
                g2.drawRoundRect(-i, startRowsY + 2 - i, w + (i * 2), listH + (i * 2), 6, 6);
            }

            g2.setColor(listBackground);
            g2.fillRoundRect(0, startRowsY, w - 1, listH, 4, 4);

            if (strokeWidth > 0f) {
                g2.setColor(strokeColor);
                g2.setStroke(new BasicStroke(strokeWidth));
                g2.drawRoundRect(0, startRowsY, w - 1, listH, 4, 4);
            }

            paintRows(g2, startRowsY, w);
            g2.dispose();
        }

        private void paintRows(Graphics2D g2, int startRowsY, int w) {
            FontMetrics fm = g2.getFontMetrics(getFont());

            for (int i = 0; i < getRowCount(); i++) {
                int iy = startRowsY + (i * rowHeight);
                String label = getLabelAt(i);

                if (isDividerRow(i)) {
                    float lineY = iy + (rowHeight / 2f);
                    g2.setColor(new Color(233, 236, 239));
                    g2.draw(new Line2D.Float(1, lineY, w - 2, lineY));
                    continue;
                }

                boolean hovered = (i == hoverIndex);
                boolean selected = (i == selectedIndex);

                if (hovered) {
                    g2.setColor(hoverBackground);
                    g2.fill(new Rectangle2D.Float(1, iy, w - 2, rowHeight));
                }

                Color textColor = hovered ? hoverForeground : new Color(33, 37, 41);

                int textX;
                int markerX;

                if (itemIconPosition == ICON_RIGHT) {
                    markerX = 10;
                    textX = 30;
                } else {
                    markerX = w - 25;
                    textX = 15;
                }

                if (selected) {
                    paintSelectedMarker(g2, markerX, iy, rowHeight, textColor, hovered, w);
                }

                String itemIconPath = getItemIconPathAt(i);
                if (itemIconPath != null && !itemIconPath.isEmpty()) {
                    Image img = loadIcon(itemIconPath);
                    if (img != null) {
                        int iconY = iy + (rowHeight - itemIconSize) / 2;

                        if (itemIconPosition == ICON_LEFT) {
                            paintTintedIcon(g2, img, 10, iconY, itemIconSize, textColor);
                            textX = 10 + itemIconSize + 8;
                        } else {
                            int iconX = w - 15 - itemIconSize;
                            paintTintedIcon(g2, img, iconX, iconY, itemIconSize, textColor);
                        }
                    }
                }

                g2.setColor(textColor);
                g2.setFont(getFont());
                g2.drawString(label, textX, (int) (iy + (rowHeight / 2.0) + fm.getAscent() * 0.35));
            }
        }

        private void paintSelectedMarker(Graphics2D g2, int markerX, int rowY, int rowH, Color fallbackTextColor, boolean hovered, int width) {
            Image markerImage = loadIcon(selectedItemIconPath);

            if (markerImage != null) {
                int size = Math.min(itemIconSize, rowH - 6);
                int y = rowY + (rowH - size) / 2;

                int x = (selectedItemIconPosition == ICON_RIGHT)
                        ? width - 15 - size
                        : markerX;

                paintTintedIcon(g2, markerImage, x, y, size, selectedItemIconColor);
                return;
            }

            float checkScale = 0.7f;
            float midY = rowY + (rowH / 2f);
            float offsetY = (9f * checkScale) / 2f;
            float startY = midY - offsetY;

            Path2D check = new Path2D.Float();
            check.moveTo(markerX, startY + (5 * checkScale));
            check.lineTo(markerX + (4 * checkScale), startY + (9 * checkScale));
            check.lineTo(markerX + (10 * checkScale), startY);

            g2.setColor(selectedItemIconColor != null ? selectedItemIconColor : new Color(13, 110, 253));
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(check);
            g2.setStroke(new BasicStroke(1f));

            g2.setColor(hovered ? hoverForeground : fallbackTextColor);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            int row = rowAt(e.getPoint());
            if (isSelectableRow(row)) {
                setSelectedIndex(row);
                popupMenu.setVisible(false);
            }
        }

        @Override public void mousePressed(MouseEvent e) { }
        @Override public void mouseReleased(MouseEvent e) { }
        @Override public void mouseEntered(MouseEvent e) { }

        @Override
        public void mouseExited(MouseEvent e) {
            setHoverIndex(-1);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            int row = rowAt(e.getPoint());
            setHoverIndex(isSelectableRow(row) ? row : -1);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            int row = rowAt(e.getPoint());
            setHoverIndex(isSelectableRow(row) ? row : -1);
        }
    }

    // ---------------------------------
    // Image helpers
    // ---------------------------------

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

    private void paintTintedIcon(Graphics2D g2, Image image, int x, int y, int size, Color tint) {
        if (image == null || size <= 0) {
            return;
        }

        BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ig = bi.createGraphics();
        ig.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        ig.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        ig.drawImage(image, 0, 0, size, size, null);
        ig.setComposite(AlphaComposite.SrcIn);
        ig.setColor(tint != null ? tint : Color.GRAY);
        ig.fillRect(0, 0, size, size);
        ig.dispose();

        g2.drawImage(bi, x, y, null);
    }

    private Color withAlpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, Math.min(255, alpha)));
    }

    // ---------------------------------
    // Events
    // ---------------------------------

    @Override
    public void mousePressed(MouseEvent e) {
        if (!isEnabled()) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastToggleTime < 150) {
            return;
        }

        if (e.getY() >= headerHeight) {
            return;
        }

        pressed = true;
        repaint();

        if (popupMenu.isVisible()) {
            popupMenu.setVisible(false);
            lastToggleTime = now;
        } else {
            openPopup();
            lastToggleTime = now;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (pressed) {
            pressed = false;
            repaint();
        }
    }

    @Override public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) {
        if (pressed) {
            pressed = false;
            repaint();
        }
    }

    @Override public void mouseDragged(MouseEvent e) { }
    @Override public void mouseMoved(MouseEvent e) { }
    @Override public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!isEnabled()) {
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_ENTER: {
                long now = System.currentTimeMillis();
                if (now - lastToggleTime < 150) {
                    return;
                }

                if (popupMenu.isVisible()) {
                    popupMenu.setVisible(false);
                } else {
                    openPopup();
                }
                lastToggleTime = now;
                break;
            }

            case KeyEvent.VK_ESCAPE:
                if (popupMenu.isVisible()) {
                    popupMenu.setVisible(false);
                }
                break;

            case KeyEvent.VK_DOWN:
                if (!popupMenu.isVisible()) {
                    openPopup();
                    lastToggleTime = System.currentTimeMillis();
                } else {
                    moveSelection(1);
                }
                break;

            case KeyEvent.VK_UP:
                if (popupMenu.isVisible()) {
                    moveSelection(-1);
                }
                break;

            default:
                break;
        }
    }

    @Override public void keyReleased(KeyEvent e) { }

    private void moveSelection(int delta) {
        if (getRowCount() <= 0) {
            return;
        }

        int idx = (selectedIndex >= 0) ? selectedIndex : findFirstSelectableRow();
        int next = idx;

        do {
            next += delta;
            if (next < 0 || next >= getRowCount()) {
                return;
            }
        } while (isDividerRow(next));

        setSelectedIndex(next);
        setHoverIndex(next);
    }
}