package com.inductiveautomation.ignition.examples.ce.components.input;

import com.inductiveautomation.ignition.client.images.ImageLoader;
import com.inductiveautomation.ignition.common.BasicDataset;
import com.inductiveautomation.ignition.common.Dataset;

import javax.swing.*;
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
    // Popup (JWindow strategy)
    // ---------------------------------
    private PopupWindow popupWindow;
    private final PopupListPanel popupPanel;
    private AWTEventListener outsideClickListener;
    private long lastToggleTime = 0L;

    public DropdownButtonComponent() {
        popupPanel = new PopupListPanel();

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
        popupPanel.revalidate();
        popupPanel.repaint();
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

        if (hoverIndex >= getRowCount()) {
            setHoverIndex(-1);
        }

        refreshPopupPanel();
        repaint();

        if (isOpen()) {
            openPopup();
        }
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

        if (isOpen()) {
            openPopup();
        }
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

        if (isOpen()) {
            openPopup();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        boolean old = isEnabled();
        super.setEnabled(enabled);
        firePropertyChange("enabled", old, enabled);

        if (!enabled) {
            pressed = false;
            hidePopup();
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

    private Window getOwnerWindow() {
        return SwingUtilities.getWindowAncestor(this);
    }

    private void ensurePopupWindow() {
        Window owner = getOwnerWindow();
        if (owner == null) {
            return;
        }

        if (popupWindow == null || popupWindow.getOwner() != owner) {
            if (popupWindow != null) {
                popupWindow.dispose();
            }

            popupWindow = new PopupWindow(owner);
            popupWindow.getContentPane().setLayout(new BorderLayout());
            popupWindow.getContentPane().add(popupPanel, BorderLayout.CENTER);
        }
    }

    private Dimension getPopupPanelSize() {
        return popupPanel.getPreferredSize();
    }

    private void openPopup() {
        if (!isEnabled()) {
            return;
        }

        ensurePopupWindow();
        if (popupWindow == null) {
            return;
        }

        setOpen(true);

        Dimension popupSize = getPopupPanelSize();
        popupPanel.setPreferredSize(popupSize);
        popupPanel.setSize(popupSize);
        popupWindow.pack();

        Point screenPt;
        try {
            screenPt = getLocationOnScreen();
        } catch (IllegalComponentStateException ignored) {
            return;
        }

        int x = screenPt.x;
        int y = screenPt.y + getHeight() + 5;

        Rectangle screenBounds = getScreenBoundsFor(screenPt);

        int spaceBelow = screenBounds.y + screenBounds.height - (screenPt.y + getHeight());
        int spaceAbove = screenPt.y - screenBounds.y;

        if (spaceBelow < popupSize.height + 8 && spaceAbove > popupSize.height + 8) {
            y = screenPt.y - popupSize.height - 2;
        }

        if (x + popupSize.width > screenBounds.x + screenBounds.width) {
            x = screenBounds.x + screenBounds.width - popupSize.width - 2;
        }
        if (x < screenBounds.x) {
            x = screenBounds.x + 2;
        }
        if (y + popupSize.height > screenBounds.y + screenBounds.height) {
            y = screenBounds.y + screenBounds.height - popupSize.height - 2;
        }
        if (y < screenBounds.y) {
            y = screenBounds.y + 2;
        }

        popupWindow.setLocation(x, y);
        popupWindow.setVisible(true);
        installOutsideClickListener();
        popupPanel.repaint();
    }

    private void hidePopup() {
        if (popupWindow != null) {
            popupWindow.setVisible(false);
        }
        uninstallOutsideClickListener();
        setOpen(false);
        setHoverIndex(-1);
        lastToggleTime = System.currentTimeMillis();
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

    private void installOutsideClickListener() {
        if (outsideClickListener != null) {
            return;
        }

        outsideClickListener = new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (!(event instanceof MouseEvent)) {
                    return;
                }

                MouseEvent me = (MouseEvent) event;
                if (me.getID() != MouseEvent.MOUSE_PRESSED) {
                    return;
                }

                if (!isOpen() || popupWindow == null || !popupWindow.isVisible()) {
                    return;
                }

                Object src = me.getSource();
                if (!(src instanceof Component)) {
                    hidePopup();
                    return;
                }

                Component c = (Component) src;
                if (SwingUtilities.isDescendingFrom(c, DropdownButtonComponent.this)) {
                    return;
                }
                if (SwingUtilities.isDescendingFrom(c, popupPanel)) {
                    return;
                }
                if (SwingUtilities.isDescendingFrom(c, popupWindow)) {
                    return;
                }

                hidePopup();
            }
        };

        Toolkit.getDefaultToolkit().addAWTEventListener(
                outsideClickListener,
                AWTEvent.MOUSE_EVENT_MASK
        );
    }

    private void uninstallOutsideClickListener() {
        if (outsideClickListener != null) {
            Toolkit.getDefaultToolkit().removeAWTEventListener(outsideClickListener);
            outsideClickListener = null;
        }
    }

    private void togglePopup() {
        if (!isEnabled()) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastToggleTime < 150) {
            return;
        }

        requestFocusInWindow();

        if (isOpen()) {
            hidePopup();
        } else {
            openPopup();
        }

        lastToggleTime = now;
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

    private int getHeaderArrowCenterXLocal() {
        return getWidth() - 20;
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

        int size = Math.max(8, iconSize);
        int h = headerHeight;
        int padding = Math.max(10, h / 4);

        FontMetrics fm = g2.getFontMetrics(getFont());
        int textWidth = fm.stringWidth(label != null ? label : "");

        int centerX = getWidth() / 2;
        int textLeft = centerX - (textWidth / 2);
        int textRight = centerX + (textWidth / 2);

        int iconY = (h - size) / 2;
        int iconX;

        if (iconLocation == ICON_RIGHT) {
            iconX = textRight + iconGap;
            int maxX = getWidth() - padding - size - 18;
            if (iconX > maxX) {
                iconX = maxX;
            }
        } else {
            iconX = textLeft - iconGap - size;
            if (iconX < padding) {
                iconX = padding;
            }
        }

        return new Rectangle(iconX, iconY, size, size);
    }

    // ---------------------------------
    // Popup window/panel
    // ---------------------------------

    private class PopupWindow extends JWindow {
        PopupWindow(Window owner) {
            super(owner);
            setBackground(new Color(0, 0, 0, 0));
            setFocusableWindowState(false);
            setAlwaysOnTop(false);
        }
    }

    private class PopupListPanel extends JPanel implements MouseListener, MouseMotionListener {

        PopupListPanel() {
            setOpaque(false);
            setBorder(null);
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        private int getNotchSpace() {
            return showTopNotch ? Math.max(0, topNotchHeight) : 0;
        }

        @Override
        public Dimension getPreferredSize() {
            int width = Math.max(DropdownButtonComponent.this.getWidth(), 140);
            int notchSpace = getNotchSpace();
            int height = notchSpace + (getRowCount() * rowHeight);
            return new Dimension(width, Math.max(2, height));
        }

        private int getMenuStartY() {
            return getNotchSpace();
        }

        private int rowAt(Point p) {
            if (p == null) {
                return -1;
            }

            int y = p.y - getMenuStartY();
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
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                int w = getWidth();
                int menuY = getMenuStartY();
                int menuH = getRowCount() * rowHeight;

                if (menuH <= 0) {
                    return;
                }

                float stroke = Math.max(0f, strokeWidth);
                float halfStroke = stroke / 2f;

                int notchX = getHeaderArrowCenterXLocal();
                int notchW = Math.max(6, topNotchWidth);
                int notchH = getNotchSpace();

                float r = 4f;

                float x = halfStroke;
                float y = menuY + halfStroke;
                float ww = w - stroke - 1f;
                float hh = menuH - stroke - 1f;

                Path2D bubble = createBubblePath(x, y, ww, hh, r, notchX, notchW, notchH);

                // Fill popup background
                g2.setColor(listBackground);
                g2.fill(bubble);

                // Paint rows clipped INSIDE the border
                Shape oldClip = g2.getClip();
                g2.clip(bubble);
                paintRows(g2, w, menuY, stroke);
                g2.setClip(oldClip);

                // Draw border LAST so it stays consistent on all sides
                if (stroke > 0f) {
                    g2.setColor(strokeColor);
                    g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.draw(bubble);
                }

            } finally {
                g2.dispose();
            }
        }

        private Path2D createBubblePath(float x, float y, float w, float h, float r,
                                        int notchX, int notchW, int notchH) {
            Path2D bubble = new Path2D.Float();

            bubble.moveTo(x + r, y);

            if (notchH > 0) {
                float leftNotch = notchX - (notchW / 2f);
                float rightNotch = notchX + (notchW / 2f);

                leftNotch = Math.max(x + r + 2f, leftNotch);
                rightNotch = Math.min(x + w - r - 2f, rightNotch);

                if (leftNotch > x + r) {
                    bubble.lineTo(leftNotch, y);
                }
                bubble.lineTo(notchX, y - notchH);
                bubble.lineTo(rightNotch, y);
            }

            bubble.lineTo(x + w - r, y);
            bubble.quadTo(x + w, y, x + w, y + r);
            bubble.lineTo(x + w, y + h - r);
            bubble.quadTo(x + w, y + h, x + w - r, y + h);
            bubble.lineTo(x + r, y + h);
            bubble.quadTo(x, y + h, x, y + h - r);
            bubble.lineTo(x, y + r);
            bubble.quadTo(x, y, x + r, y);
            bubble.closePath();

            return bubble;
        }

        private void paintRows(Graphics2D g2, int w, int menuY, float stroke) {
            Font itemFont = DropdownButtonComponent.this.getFont() != null
                    ? DropdownButtonComponent.this.getFont()
                    : new Font("SansSerif", Font.PLAIN, 13);

            FontMetrics fm = g2.getFontMetrics(itemFont);

            int inset = Math.max(1, (int) Math.ceil(stroke));
            int contentLeft = inset;
            int contentRight = w - inset;

            for (int row = 0; row < getRowCount(); row++) {
                int y = menuY + (row * rowHeight);
                String label = getLabelAt(row);

                if (isDividerRow(row)) {
                    float lineY = y + (rowHeight / 2f);
                    g2.setColor(new Color(225, 225, 225));
                    g2.draw(new Line2D.Float(contentLeft, lineY, contentRight, lineY));
                    continue;
                }

                boolean hovered = (row == hoverIndex);
                boolean selected = (row == selectedIndex);

                if (hovered) {
                    g2.setColor(hoverBackground);
                    g2.fill(new Rectangle2D.Float(
                            contentLeft,
                            y,
                            contentRight - contentLeft,
                            rowHeight
                    ));
                }

                Color textColor = hovered ? hoverForeground : foreground;
                int textX = 12 + inset;

                String itemIconPath = getItemIconPathAt(row);
                Image itemIcon = loadIcon(itemIconPath);

                if (itemIcon != null) {
                    int iconSize = Math.max(8, itemIconSize);
                    int iconY = y + (rowHeight - iconSize) / 2;

                    if (itemIconPosition == ICON_LEFT) {
                        int iconX = 10 + inset;
                        paintTintedIcon(g2, itemIcon, iconX, iconY, iconSize, textColor);
                        textX = iconX + iconSize + 8;
                    } else {
                        int iconX = contentRight - 12 - iconSize;
                        paintTintedIcon(g2, itemIcon, iconX, iconY, iconSize, textColor);
                    }
                }

                if (selected) {
                    paintSelectedMarker(g2, y, w, inset);
                    if (selectedItemIconPosition == ICON_LEFT && selectedItemIconPath.isEmpty()) {
                        textX = Math.max(textX, 26 + inset);
                    }
                }

                g2.setFont(itemFont);
                g2.setColor(textColor);
                g2.drawString(label, textX, y + ((rowHeight - fm.getHeight()) / 2) + fm.getAscent());
            }
        }

        private void paintSelectedMarker(Graphics2D g2, int rowY, int width, int inset) {
            int size = Math.min(Math.max(8, itemIconSize), rowHeight - 8);
            int y = rowY + (rowHeight - size) / 2;

            int x;
            if (selectedItemIconPosition == ICON_RIGHT) {
                x = width - inset - 12 - size;
            } else {
                x = 10 + inset;
            }

            Image selectedMarker = loadIcon(selectedItemIconPath);
            if (selectedMarker != null) {
                paintTintedIcon(g2, selectedMarker, x, y, size, selectedItemIconColor);
                return;
            }

            float startX = x;
            float startY = rowY + (rowHeight / 2f);

            Path2D check = new Path2D.Float();
            check.moveTo(startX, startY);
            check.lineTo(startX + 4f, startY + 4f);
            check.lineTo(startX + 10f, startY - 4f);

            g2.setColor(selectedItemIconColor != null ? selectedItemIconColor : new Color(13, 110, 253));
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(check);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            int row = rowAt(e.getPoint());
            if (isSelectableRow(row)) {
                setSelectedIndex(row);
                hidePopup();
            }
        }

        @Override public void mousePressed(MouseEvent e) { }
        @Override public void mouseReleased(MouseEvent e) { }
        @Override public void mouseEntered(MouseEvent e) { }

        @Override
        public void mouseExited(MouseEvent e) {
            setHoverIndex(-1);
        }

        @Override public void mouseDragged(MouseEvent e) { }

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

    private void paintTintedIcon(Graphics2D g2, Image img, int x, int y, int size, Color tint) {
        if (img == null || size <= 0) {
            return;
        }

        BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ig = bi.createGraphics();
        try {
            ig.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            ig.drawImage(img, 0, 0, size, size, null);
            ig.setComposite(AlphaComposite.SrcAtop);
            ig.setColor(tint != null ? tint : Color.GRAY);
            ig.fillRect(0, 0, size, size);
        } finally {
            ig.dispose();
        }

        g2.drawImage(bi, x, y, null);
    }

    private Color withAlpha(Color c, int alpha) {
        if (c == null) {
            c = Color.GRAY;
        }
        alpha = Math.max(0, Math.min(255, alpha));
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    // ---------------------------------
    // Events
    // ---------------------------------

    @Override
    public void mousePressed(MouseEvent e) {
        if (!isEnabled()) {
            return;
        }

        if (e.getY() <= headerHeight) {
            pressed = true;
            repaint();
            togglePopup();
        }
    }

    @Override public void mouseClicked(MouseEvent e) { }

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
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_SPACE:
                togglePopup();
                e.consume();
                break;

            case KeyEvent.VK_ESCAPE:
                if (isOpen()) {
                    hidePopup();
                    e.consume();
                }
                break;

            case KeyEvent.VK_DOWN:
                if (!isOpen()) {
                    openPopup();
                } else {
                    moveSelection(1);
                }
                e.consume();
                break;

            case KeyEvent.VK_UP:
                if (isOpen()) {
                    moveSelection(-1);
                    e.consume();
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

        int current = hoverIndex >= 0 ? hoverIndex : selectedIndex;
        if (current < 0) {
            current = findFirstSelectableRow();
        }

        int idx = current;
        do {
            idx += delta;
        } while (idx >= 0 && idx < getRowCount() && isDividerRow(idx));

        if (idx >= 0 && idx < getRowCount()) {
            setHoverIndex(idx);
        }
    }
}