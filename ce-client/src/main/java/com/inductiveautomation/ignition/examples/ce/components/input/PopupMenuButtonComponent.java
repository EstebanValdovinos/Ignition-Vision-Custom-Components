package com.inductiveautomation.ignition.examples.ce.components.input;

import com.inductiveautomation.ignition.client.images.ImageLoader;
import com.inductiveautomation.ignition.common.BasicDataset;
import com.inductiveautomation.ignition.common.Dataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class PopupMenuButtonComponent extends JComponent implements MouseListener, KeyListener {

    public static final int ORIENTATION_VERTICAL = 0;
    public static final int ORIENTATION_HORIZONTAL = 1;

    public static final int POPUP_TOP = 0;
    public static final int POPUP_BOTTOM = 1;
    public static final int POPUP_LEFT = 2;
    public static final int POPUP_RIGHT = 3;

    private static final int POINTER_WIDTH = 14;
    private static final int POINTER_DEPTH = 5;
    private static final int MENU_RADIUS = 8;
    private static final int ITEM_PAD = 4;
    private static final int ITEM_RADIUS = 8;
    private static final int GRID_LINE_PAD = 8;

    private String text = "Options";
    private Color btnColor = new Color(13, 110, 253);
    private Color btnTextColor = Color.WHITE;
    private Font buttonTextFont = new Font("SansSerif", Font.BOLD, 12);

    private Dataset options = createDefaultOptionsDataset();
    private String selectedItem = "";
    private boolean isOpen = false;
    private int hoverIndex = -1;

    private int orientation = ORIENTATION_VERTICAL;
    private int popupLocation = POPUP_RIGHT;

    private int cellWidth = 70;
    private int cellHeight = 75;
    private int iconSize = 20;
    private int popupGap = 3;

    private Color menuBgColor = new Color(213, 213, 213);
    private Color hoverColor = new Color(170, 170, 170);
    private Color selectedColor = new Color(185, 185, 185);
    private Color gridLineColor = new Color(170, 170, 170);

    private PopupWindow popupWindow;
    private final PopupPanel popupPanel;

    private AWTEventListener outsideClickListener;
    private long lastToggleTime = 0L;

    public PopupMenuButtonComponent() {
        setOpaque(false);
        setFocusable(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        super.setFont(new Font("SansSerif", Font.PLAIN, 12));
        super.setForeground(new Color(85, 85, 85));

        popupPanel = new PopupPanel();

        addMouseListener(this);
        addKeyListener(this);
    }

    private static Dataset createDefaultOptionsDataset() {
        String[] columnNames = new String[]{"option", "iconPath"};
        Class<?>[] columnTypes = new Class<?>[]{String.class, String.class};

        Object[][] data = new Object[][]{
                {
                        "Copy",
                        "Home",
                        "Mail",
                        "Power",
                        "Setting",
                        "Menu"
                },
                {
                        "Builtin/icons/16/copy.png",
                        "Builtin/icons/16/home.png",
                        "Builtin/icons/16/document.png",
                        "Builtin/icons/16/lightbulb_on.png",
                        "Builtin/icons/16/wrench.png",
                        "Builtin/icons/16/oszillograph.png"
                }
        };

        return new BasicDataset(columnNames, columnTypes, data);
    }

    private static class MenuItemData {
        final String option;
        final String iconPath;

        MenuItemData(String option, String iconPath) {
            this.option = option != null ? option : "";
            this.iconPath = iconPath != null ? iconPath : "";
        }
    }

    private List<MenuItemData> getMenuItems() {
        List<MenuItemData> items = new ArrayList<MenuItemData>();
        if (options == null) {
            return items;
        }

        int optionCol = findColumnIgnoreCase(options, "option");
        int iconCol = findColumnIgnoreCase(options, "iconPath");

        if (optionCol < 0) {
            optionCol = 0;
        }
        if (iconCol < 0 && options.getColumnCount() > 1) {
            iconCol = 1;
        }

        for (int row = 0; row < options.getRowCount(); row++) {
            String option = "";
            String iconPath = "";

            try {
                if (optionCol >= 0 && optionCol < options.getColumnCount()) {
                    Object v = options.getValueAt(row, optionCol);
                    option = v != null ? String.valueOf(v) : "";
                }

                if (iconCol >= 0 && iconCol < options.getColumnCount()) {
                    Object v = options.getValueAt(row, iconCol);
                    iconPath = v != null ? String.valueOf(v) : "";
                }
            } catch (Exception ignored) {
            }

            items.add(new MenuItemData(option, iconPath));
        }

        return items;
    }

    private int findColumnIgnoreCase(Dataset ds, String name) {
        if (ds == null || name == null) {
            return -1;
        }

        for (int i = 0; i < ds.getColumnCount(); i++) {
            String col = ds.getColumnName(i);
            if (col != null && col.trim().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
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

    private int getItemCount() {
        return getMenuItems().size();
    }

    private int getEffectiveRows() {
        return orientation == ORIENTATION_VERTICAL ? Math.max(1, getItemCount()) : 1;
    }

    private int getEffectiveCols() {
        return orientation == ORIENTATION_HORIZONTAL ? Math.max(1, getItemCount()) : 1;
    }

    private Rectangle getMenuRect() {
        int cols = getEffectiveCols();
        int rows = getEffectiveRows();

        int menuW = cols * cellWidth;
        int menuH = rows * cellHeight;

        switch (popupLocation) {
            case POPUP_TOP:
                return new Rectangle(0, 0, menuW, menuH);
            case POPUP_BOTTOM:
                return new Rectangle(0, POINTER_DEPTH + popupGap, menuW, menuH);
            case POPUP_LEFT:
                return new Rectangle(0, 0, menuW, menuH);
            case POPUP_RIGHT:
            default:
                return new Rectangle(POINTER_DEPTH + popupGap, 0, menuW, menuH);
        }
    }

    private Dimension getPopupPanelSize() {
        Rectangle r = getMenuRect();
        switch (popupLocation) {
            case POPUP_TOP:
            case POPUP_BOTTOM:
                return new Dimension(r.width, r.height + POINTER_DEPTH + popupGap);
            case POPUP_LEFT:
            case POPUP_RIGHT:
            default:
                return new Dimension(r.width + POINTER_DEPTH + popupGap, r.height);
        }
    }

    private void refreshPopupPanel() {
        if (popupPanel == null) {
            return;
        }
        popupPanel.revalidate();
        popupPanel.repaint();
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

                if (!isIsOpen() || popupWindow == null || !popupWindow.isVisible()) {
                    return;
                }

                Object src = me.getSource();
                if (!(src instanceof Component)) {
                    hidePopup();
                    return;
                }

                Component c = (Component) src;
                if (SwingUtilities.isDescendingFrom(c, PopupMenuButtonComponent.this)) {
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

        if (isIsOpen()) {
            hidePopup();
        } else {
            openPopup();
        }

        lastToggleTime = now;
    }

    private void openPopup() {
        ensurePopupWindow();
        if (popupWindow == null) {
            return;
        }

        Dimension popupSize = getPopupPanelSize();
        popupPanel.setPreferredSize(popupSize);
        popupWindow.pack();

        Point screenPt;
        try {
            screenPt = getLocationOnScreen();
        } catch (IllegalComponentStateException ignored) {
            return;
        }

        int x;
        int y;

        switch (popupLocation) {
            case POPUP_TOP:
                x = screenPt.x + (getWidth() - popupSize.width) / 2;
                y = screenPt.y - popupSize.height - popupGap;
                break;
            case POPUP_BOTTOM:
                x = screenPt.x + (getWidth() - popupSize.width) / 2;
                y = screenPt.y + getHeight() + popupGap;
                break;
            case POPUP_LEFT:
                x = screenPt.x - popupSize.width - popupGap;
                y = screenPt.y + (getHeight() - popupSize.height) / 2;
                break;
            case POPUP_RIGHT:
            default:
                x = screenPt.x + getWidth() + popupGap;
                y = screenPt.y + (getHeight() - popupSize.height) / 2;
                break;
        }

        Rectangle screenBounds = getScreenBoundsFor(screenPt);

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
        setIsOpen(true);
    }

    private void hidePopup() {
        if (popupWindow != null) {
            popupWindow.setVisible(false);
        }
        uninstallOutsideClickListener();
        setIsOpen(false);
        setHoverIndex(-1);
        lastToggleTime = System.currentTimeMillis();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        String old = this.text;
        this.text = text != null ? text : "";
        firePropertyChange("text", old, this.text);
        repaint();
    }

    public Color getBtnColor() {
        return btnColor;
    }

    public void setBtnColor(Color btnColor) {
        Color old = this.btnColor;
        this.btnColor = btnColor != null ? btnColor : new Color(13, 110, 253);
        firePropertyChange("btnColor", old, this.btnColor);
        repaint();
    }

    public Color getBtnTextColor() {
        return btnTextColor;
    }

    public void setBtnTextColor(Color btnTextColor) {
        Color old = this.btnTextColor;
        this.btnTextColor = btnTextColor != null ? btnTextColor : Color.WHITE;
        firePropertyChange("btnTextColor", old, this.btnTextColor);
        repaint();
    }

    public Font getButtonTextFont() {
        return buttonTextFont;
    }

    public void setButtonTextFont(Font buttonTextFont) {
        Font old = this.buttonTextFont;
        this.buttonTextFont = buttonTextFont != null ? buttonTextFont : new Font("SansSerif", Font.BOLD, 12);
        firePropertyChange("buttonTextFont", old, this.buttonTextFont);
        repaint();
    }

    public Dataset getOptions() {
        return options;
    }

    public void setOptions(Dataset options) {
        Dataset old = this.options;
        this.options = options != null ? options : createDefaultOptionsDataset();
        firePropertyChange("options", old, this.options);

        boolean exists = false;
        for (MenuItemData item : getMenuItems()) {
            if (item.option.equals(selectedItem)) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            String oldSel = this.selectedItem;
            this.selectedItem = "";
            firePropertyChange("selectedItem", oldSel, this.selectedItem);
        }

        if (hoverIndex >= getItemCount()) {
            setHoverIndex(-1);
        }

        refreshPopupPanel();
    }

    public String getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(String selectedItem) {
        String old = this.selectedItem;
        this.selectedItem = selectedItem != null ? selectedItem : "";
        firePropertyChange("selectedItem", old, this.selectedItem);
        refreshPopupPanel();
        repaint();
    }

    public boolean isIsOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        boolean old = this.isOpen;
        this.isOpen = isOpen;
        firePropertyChange("isOpen", old, this.isOpen);
        repaint();
    }

    public int getHoverIndex() {
        return hoverIndex;
    }

    public void setHoverIndex(int hoverIndex) {
        int old = this.hoverIndex;
        int count = getItemCount();
        if (hoverIndex < -1 || hoverIndex >= count) {
            hoverIndex = -1;
        }
        this.hoverIndex = hoverIndex;
        firePropertyChange("hoverIndex", old, this.hoverIndex);
        refreshPopupPanel();
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        int old = this.orientation;
        if (orientation != ORIENTATION_VERTICAL && orientation != ORIENTATION_HORIZONTAL) {
            orientation = ORIENTATION_VERTICAL;
        }
        this.orientation = orientation;
        firePropertyChange("orientation", old, this.orientation);
        refreshPopupPanel();
        if (isIsOpen()) {
            openPopup();
        }
    }

    public int getPopupLocation() {
        return popupLocation;
    }

    public void setPopupLocation(int popupLocation) {
        int old = this.popupLocation;
        if (popupLocation != POPUP_TOP && popupLocation != POPUP_BOTTOM
                && popupLocation != POPUP_LEFT && popupLocation != POPUP_RIGHT) {
            popupLocation = POPUP_RIGHT;
        }
        this.popupLocation = popupLocation;
        firePropertyChange("popupLocation", old, this.popupLocation);
        refreshPopupPanel();
        if (isIsOpen()) {
            openPopup();
        }
    }

    public int getCellWidth() {
        return cellWidth;
    }

    public void setCellWidth(int cellWidth) {
        int old = this.cellWidth;
        this.cellWidth = Math.max(24, cellWidth);
        firePropertyChange("cellWidth", old, this.cellWidth);
        refreshPopupPanel();
        if (isIsOpen()) {
            openPopup();
        }
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public void setCellHeight(int cellHeight) {
        int old = this.cellHeight;
        this.cellHeight = Math.max(24, cellHeight);
        firePropertyChange("cellHeight", old, this.cellHeight);
        refreshPopupPanel();
        if (isIsOpen()) {
            openPopup();
        }
    }

    public int getIconSize() {
        return iconSize;
    }

    public void setIconSize(int iconSize) {
        int old = this.iconSize;
        this.iconSize = Math.max(8, iconSize);
        firePropertyChange("iconSize", old, this.iconSize);
        refreshPopupPanel();
    }

    public int getPopupGap() {
        return popupGap;
    }

    public void setPopupGap(int popupGap) {
        int old = this.popupGap;
        this.popupGap = Math.max(0, popupGap);
        firePropertyChange("popupGap", old, this.popupGap);
        refreshPopupPanel();
        if (isIsOpen()) {
            openPopup();
        }
    }

    public Color getMenuBgColor() {
        return menuBgColor;
    }

    public void setMenuBgColor(Color menuBgColor) {
        Color old = this.menuBgColor;
        this.menuBgColor = menuBgColor != null ? menuBgColor : new Color(213, 213, 213);
        firePropertyChange("menuBgColor", old, this.menuBgColor);
        refreshPopupPanel();
    }

    public Color getHoverColor() {
        return hoverColor;
    }

    public void setHoverColor(Color hoverColor) {
        Color old = this.hoverColor;
        this.hoverColor = hoverColor != null ? hoverColor : new Color(170, 170, 170);
        firePropertyChange("hoverColor", old, this.hoverColor);
        refreshPopupPanel();
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(Color selectedColor) {
        Color old = this.selectedColor;
        this.selectedColor = selectedColor != null ? selectedColor : new Color(185, 185, 185);
        firePropertyChange("selectedColor", old, this.selectedColor);
        refreshPopupPanel();
    }

    public Color getGridLineColor() {
        return gridLineColor;
    }

    public void setGridLineColor(Color gridLineColor) {
        Color old = this.gridLineColor;
        this.gridLineColor = gridLineColor != null ? gridLineColor : new Color(170, 170, 170);
        firePropertyChange("gridLineColor", old, this.gridLineColor);
        refreshPopupPanel();
    }

    @Override
    public void setFont(Font font) {
        Font old = super.getFont();
        super.setFont(font != null ? font : new Font("SansSerif", Font.PLAIN, 12));
        firePropertyChange("font", old, super.getFont());
        refreshPopupPanel();
        repaint();
    }

    @Override
    public void setForeground(Color fg) {
        Color old = super.getForeground();
        super.setForeground(fg != null ? fg : new Color(85, 85, 85));
        firePropertyChange("foreground", old, super.getForeground());
        refreshPopupPanel();
        repaint();
    }

    @Override
    public void setEnabled(boolean enabled) {
        boolean old = isEnabled();
        super.setEnabled(enabled);
        firePropertyChange("enabled", old, enabled);

        if (!enabled) {
            hidePopup();
            setCursor(Cursor.getDefaultCursor());
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        repaint();
    }

    @Override
    public void removeNotify() {
        hidePopup();
        if (popupWindow != null) {
            popupWindow.dispose();
            popupWindow = null;
        }
        super.removeNotify();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            Color bg = btnColor != null ? btnColor : new Color(13, 110, 253);
            Color fg = btnTextColor != null ? btnTextColor : Color.WHITE;

            if (!isEnabled()) {
                bg = new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 140);
                fg = new Color(220, 220, 220);
            }

            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 8, 8));

            g2.setColor(fg);
            g2.setFont(buttonTextFont != null ? buttonTextFont : new Font("SansSerif", Font.BOLD, 12));

            FontMetrics fm = g2.getFontMetrics();
            String safeText = text != null ? text : "";
            int sx = (w - fm.stringWidth(safeText)) / 2;
            int sy = (h - fm.getHeight()) / 2 + fm.getAscent();

            g2.drawString(safeText, sx, sy);
        } finally {
            g2.dispose();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(120, 40);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(60, 28);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        togglePopup();
    }

    @Override public void mouseClicked(MouseEvent e) { }
    @Override public void mouseReleased(MouseEvent e) { }
    @Override public void mouseEntered(MouseEvent e) { }
    @Override public void mouseExited(MouseEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!isEnabled()) {
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
            togglePopup();
            e.consume();
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE && isIsOpen()) {
            hidePopup();
            e.consume();
        }
    }

    @Override public void keyTyped(KeyEvent e) { }
    @Override public void keyReleased(KeyEvent e) { }

    private class PopupWindow extends JWindow {
        PopupWindow(Window owner) {
            super(owner);
            setBackground(new Color(0, 0, 0, 0));
            setFocusableWindowState(false);
            setAlwaysOnTop(false);
        }
    }

    private class PopupPanel extends JPanel implements MouseListener, MouseMotionListener {

        PopupPanel() {
            setOpaque(false);
            setBorder(null);
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        @Override
        public Dimension getPreferredSize() {
            return getPopupPanelSize();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                List<MenuItemData> itemList = getMenuItems();
                Rectangle menuRect = getMenuRect();
                Shape bubble = createBubbleShape(menuRect);

                g2.setColor(menuBgColor != null ? menuBgColor : new Color(213, 213, 213));
                g2.fill(bubble);

                int cols = getEffectiveCols();
                int rows = getEffectiveRows();

                g2.setColor(gridLineColor != null ? gridLineColor : new Color(170, 170, 170));
                g2.setStroke(new BasicStroke(1f));

                for (int c = 1; c < cols; c++) {
                    float x = menuRect.x + (c * cellWidth);
                    g2.draw(new Line2D.Float(x, menuRect.y + GRID_LINE_PAD, x, menuRect.y + menuRect.height - GRID_LINE_PAD));
                }

                for (int r = 1; r < rows; r++) {
                    float y = menuRect.y + (r * cellHeight);
                    g2.draw(new Line2D.Float(menuRect.x + GRID_LINE_PAD, y, menuRect.x + menuRect.width - GRID_LINE_PAD, y));
                }

                Font optionFont = PopupMenuButtonComponent.this.getFont() != null
                        ? PopupMenuButtonComponent.this.getFont()
                        : new Font("SansSerif", Font.PLAIN, 12);

                Color optionColor = PopupMenuButtonComponent.this.getForeground() != null
                        ? PopupMenuButtonComponent.this.getForeground()
                        : new Color(85, 85, 85);

                for (int i = 0; i < itemList.size(); i++) {
                    int row = i / cols;
                    int col = i % cols;

                    int cellX = menuRect.x + (col * cellWidth);
                    int cellY = menuRect.y + (row * cellHeight);
                    Rectangle cell = new Rectangle(cellX, cellY, cellWidth, cellHeight);

                    Rectangle fillRect = new Rectangle(
                            cell.x + ITEM_PAD,
                            cell.y + ITEM_PAD,
                            cell.width - (ITEM_PAD * 2),
                            cell.height - (ITEM_PAD * 2)
                    );

                    MenuItemData item = itemList.get(i);
                    boolean selected = item.option.equals(selectedItem);
                    boolean hovered = (i == hoverIndex);

                    if (selected || hovered) {
                        g2.setColor(selected
                                ? (selectedColor != null ? selectedColor : new Color(185, 185, 185))
                                : (hoverColor != null ? hoverColor : new Color(170, 170, 170)));
                        g2.fill(new RoundRectangle2D.Float(
                                fillRect.x,
                                fillRect.y,
                                fillRect.width,
                                fillRect.height,
                                ITEM_RADIUS * 2,
                                ITEM_RADIUS * 2
                        ));
                    }

                    Image icon = loadIcon(item.iconPath);
                    if (icon != null) {
                        int iconX = cell.x + (cell.width - iconSize) / 2;
                        int iconY = cell.y + Math.max(8, (cell.height / 2) - iconSize);
                        g2.drawImage(icon, iconX, iconY, iconSize, iconSize, null);
                    }

                    g2.setFont(optionFont);
                    g2.setColor(optionColor);

                    FontMetrics fm = g2.getFontMetrics(optionFont);
                    int textX = cell.x + (cell.width - fm.stringWidth(item.option)) / 2;
                    int textY = cell.y + cell.height - 14;
                    g2.drawString(item.option, textX, textY);
                }
            } finally {
                g2.dispose();
            }
        }

        private Shape createBubbleShape(Rectangle menuRect) {
            Path2D path = new Path2D.Float();

            float x = menuRect.x;
            float y = menuRect.y;
            float w = menuRect.width;
            float h = menuRect.height;
            float r = MENU_RADIUS;

            float cx = x + (w / 2f);
            float cy = y + (h / 2f);

            switch (popupLocation) {
                case POPUP_TOP:
                    path.moveTo(x + r, y);
                    path.lineTo(x + w - r, y);
                    path.quadTo(x + w, y, x + w, y + r);
                    path.lineTo(x + w, y + h - r);
                    path.quadTo(x + w, y + h, x + w - r, y + h);
                    path.lineTo(cx + (POINTER_WIDTH / 2f), y + h);
                    path.lineTo(cx, y + h + POINTER_DEPTH);
                    path.lineTo(cx - (POINTER_WIDTH / 2f), y + h);
                    path.lineTo(x + r, y + h);
                    path.quadTo(x, y + h, x, y + h - r);
                    path.lineTo(x, y + r);
                    path.quadTo(x, y, x + r, y);
                    break;

                case POPUP_BOTTOM:
                    path.moveTo(x + r, y);
                    path.lineTo(cx - (POINTER_WIDTH / 2f), y);
                    path.lineTo(cx, y - POINTER_DEPTH);
                    path.lineTo(cx + (POINTER_WIDTH / 2f), y);
                    path.lineTo(x + w - r, y);
                    path.quadTo(x + w, y, x + w, y + r);
                    path.lineTo(x + w, y + h - r);
                    path.quadTo(x + w, y + h, x + w - r, y + h);
                    path.lineTo(x + r, y + h);
                    path.quadTo(x, y + h, x, y + h - r);
                    path.lineTo(x, y + r);
                    path.quadTo(x, y, x + r, y);
                    break;

                case POPUP_LEFT:
                    path.moveTo(x + r, y);
                    path.lineTo(x + w - r, y);
                    path.quadTo(x + w, y, x + w, y + r);
                    path.lineTo(x + w, cy - (POINTER_WIDTH / 2f));
                    path.lineTo(x + w + POINTER_DEPTH, cy);
                    path.lineTo(x + w, cy + (POINTER_WIDTH / 2f));
                    path.lineTo(x + w, y + h - r);
                    path.quadTo(x + w, y + h, x + w - r, y + h);
                    path.lineTo(x + r, y + h);
                    path.quadTo(x, y + h, x, y + h - r);
                    path.lineTo(x, y + r);
                    path.quadTo(x, y, x + r, y);
                    break;

                case POPUP_RIGHT:
                default:
                    path.moveTo(x + r, y);
                    path.lineTo(x + w - r, y);
                    path.quadTo(x + w, y, x + w, y + r);
                    path.lineTo(x + w, y + h - r);
                    path.quadTo(x + w, y + h, x + w - r, y + h);
                    path.lineTo(x + r, y + h);
                    path.quadTo(x, y + h, x, y + h - r);
                    path.lineTo(x, cy + (POINTER_WIDTH / 2f));
                    path.lineTo(x - POINTER_DEPTH, cy);
                    path.lineTo(x, cy - (POINTER_WIDTH / 2f));
                    path.lineTo(x, y + r);
                    path.quadTo(x, y, x + r, y);
                    break;
            }

            path.closePath();
            return path;
        }

        private int getIndexAt(Point p) {
            List<MenuItemData> itemList = getMenuItems();
            Rectangle menuRect = getMenuRect();
            if (!menuRect.contains(p)) {
                return -1;
            }

            int cols = getEffectiveCols();
            int rows = getEffectiveRows();

            int relX = p.x - menuRect.x;
            int relY = p.y - menuRect.y;

            int col = relX / cellWidth;
            int row = relY / cellHeight;

            if (col < 0 || col >= cols || row < 0 || row >= rows) {
                return -1;
            }

            int idx = (row * cols) + col;
            return idx < itemList.size() ? idx : -1;
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            int idx = getIndexAt(e.getPoint());
            setHoverIndex(idx);
            setCursor(idx >= 0 ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
        }

        @Override
        public void mouseExited(MouseEvent e) {
            setHoverIndex(-1);
            setCursor(Cursor.getDefaultCursor());
        }

        @Override
        public void mousePressed(MouseEvent e) {
            int idx = getIndexAt(e.getPoint());
            if (idx < 0) {
                return;
            }

            List<MenuItemData> itemList = getMenuItems();
            if (idx < itemList.size()) {
                setSelectedItem(itemList.get(idx).option);
                hidePopup();
            }
        }

        @Override public void mouseDragged(MouseEvent e) { }
        @Override public void mouseClicked(MouseEvent e) { }
        @Override public void mouseReleased(MouseEvent e) { }
        @Override public void mouseEntered(MouseEvent e) { }
    }
}