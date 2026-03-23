package com.inductiveautomation.ignition.examples.ce.components.input;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.beans.Beans;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ExpandableSearchBoxComponent extends JComponent
        implements MouseListener, FocusListener, KeyListener {

    public static final int ICON_LEFT = 0;
    public static final int ICON_RIGHT = 1;

    public static final int EXPAND_RIGHT = 0;
    public static final int EXPAND_LEFT = 1;

    public static final int MODE_EXPANDABLE = 0;
    public static final int MODE_FIXED = 1;

    private static final int DEFAULT_SIZE = 40;
    private static final int DEFAULT_EXPANDED_WIDTH = 280;

    private static final int ANIMATION_DELAY = 15;
    private static final float ANIMATION_STEP = 0.12f;

    private String searchText = "";
    private boolean expanded = false;

    private int mode = MODE_EXPANDABLE;

    private Color borderColor = new Color(210, 210, 210);
    private float borderWidth = 1f;

    private Color iconColor = new Color(66, 133, 244);
    private int iconPosition = ICON_LEFT;
    private int expandDirection = EXPAND_RIGHT;
    private int iconSize = 14;

    private String placeholderText = "Search";
    private Color placeholderColor = new Color(150, 150, 150);

    private int cornerRadius = -1;
    private int expandedWidth = DEFAULT_EXPANDED_WIDTH;

    private boolean pressing = false;
    private boolean hoverClear = false;

    private AnimatedOverlayPanel overlayPanel;
    private PlaceholderTextField overlayTextField;
    private JLayeredPane hostLayeredPane;
    private PropertyChangeListener focusTracker;
    private ComponentListener hostWindowListener;
    private final ComponentListener selfBoundsListener;

    private Timer animationTimer;
    private float animationProgress = 0f; // 0 collapsed, 1 expanded
    private float animationTarget = 0f;

    private boolean deferUpdates = false;

    public boolean isDeferUpdates() {
        return deferUpdates;
    }

    public void setDeferUpdates(boolean deferUpdates) {
        boolean old = this.deferUpdates;
        this.deferUpdates = deferUpdates;
        firePropertyChange("deferUpdates", old, this.deferUpdates);
    }

    public ExpandableSearchBoxComponent() {
        setOpaque(false);
        setFocusable(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        setBackground(Color.WHITE);
        setForeground(new Color(90, 90, 90));
        setFont(new Font("Dialog", Font.PLAIN, 14));

        setPreferredSize(new Dimension(DEFAULT_SIZE, DEFAULT_SIZE));
        setMinimumSize(new Dimension(28, 28));

        addMouseListener(this);
        addFocusListener(this);
        addKeyListener(this);

        selfBoundsListener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                handleSelfBoundsChanged();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                handleSelfBoundsChanged();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                handleSelfBoundsChanged();
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                if (mode == MODE_FIXED) {
                    hideOverlayNow();
                }
            }
        };
        addComponentListener(selfBoundsListener);

        animationTimer = new Timer(ANIMATION_DELAY, e -> stepAnimation());
    }

    // ---------------------------------------------------------------------
    // Properties
    // ---------------------------------------------------------------------

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        String old = this.searchText;
        this.searchText = searchText != null ? searchText : "";
        if (overlayTextField != null && !this.searchText.equals(overlayTextField.getText())) {
            overlayTextField.setText(this.searchText);
        }
        firePropertyChange("searchText", old, this.searchText);

        enforceModeState();
        repaint();
        repaintOverlay();
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        boolean desired = expanded;

        if (mode == MODE_FIXED) {
            desired = true;
        } else if (shouldForceDesignerCollapse()) {
            desired = false;
        }

        boolean old = this.expanded;
        if (old == desired) {
            if (desired) {
                showOverlay();
                repaintOverlay();
            }
            return;
        }

        this.expanded = desired;
        firePropertyChange("expanded", old, this.expanded);

        if (this.expanded) {
            showOverlay();
            animationTarget = 1f;
            if (!animationTimer.isRunning()) {
                animationTimer.start();
            }
        } else {
            animationTarget = 0f;
            if (!animationTimer.isRunning()) {
                animationTimer.start();
            }
        }

        repaint();
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        int old = this.mode;
        this.mode = (mode == MODE_FIXED) ? MODE_FIXED : MODE_EXPANDABLE;
        firePropertyChange("mode", old, this.mode);

        if (this.mode == MODE_FIXED) {
            expanded = true;
            animationProgress = 1f;
            animationTarget = 1f;
            showOverlay();
        } else {
            if (shouldForceDesignerCollapse()) {
                expanded = false;
                animationProgress = 0f;
                animationTarget = 0f;
                hideOverlayNow();
            } else if (expanded) {
                showOverlay();
            }
        }

        enforceModeState();
        repaint();
        repaintOverlay();
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        Color old = this.borderColor;
        this.borderColor = borderColor != null ? borderColor : new Color(210, 210, 210);
        firePropertyChange("borderColor", old, this.borderColor);
        repaint();
        repaintOverlay();
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(float borderWidth) {
        float old = this.borderWidth;
        this.borderWidth = Math.max(0f, borderWidth);
        firePropertyChange("borderWidth", old, this.borderWidth);
        repaint();
        repaintOverlay();
    }

    public Color getIconColor() {
        return iconColor;
    }

    public void setIconColor(Color iconColor) {
        Color old = this.iconColor;
        this.iconColor = iconColor != null ? iconColor : new Color(66, 133, 244);
        firePropertyChange("iconColor", old, this.iconColor);
        repaint();
        repaintOverlay();
    }

    public int getIconPosition() {
        return iconPosition;
    }

    public void setIconPosition(int iconPosition) {
        int old = this.iconPosition;
        this.iconPosition = (iconPosition == ICON_RIGHT) ? ICON_RIGHT : ICON_LEFT;
        firePropertyChange("iconPosition", old, this.iconPosition);
        repaintOverlay();
    }

    public int getExpandDirection() {
        return expandDirection;
    }

    public void setExpandDirection(int expandDirection) {
        int old = this.expandDirection;
        this.expandDirection = (expandDirection == EXPAND_LEFT) ? EXPAND_LEFT : EXPAND_RIGHT;
        firePropertyChange("expandDirection", old, this.expandDirection);
        repositionOverlay();
    }

    public int getIconSize() {
        return iconSize;
    }

    public void setIconSize(int iconSize) {
        int old = this.iconSize;
        this.iconSize = Math.max(8, iconSize);
        firePropertyChange("iconSize", old, this.iconSize);
        repaint();
        repaintOverlay();
    }

    public String getPlaceholderText() {
        return placeholderText;
    }

    public void setPlaceholderText(String placeholderText) {
        String old = this.placeholderText;
        this.placeholderText = placeholderText != null ? placeholderText : "Search";
        firePropertyChange("placeholderText", old, this.placeholderText);
        repaintOverlay();
    }

    public Color getPlaceholderColor() {
        return placeholderColor;
    }

    public void setPlaceholderColor(Color placeholderColor) {
        Color old = this.placeholderColor;
        this.placeholderColor = placeholderColor != null ? placeholderColor : new Color(150, 150, 150);
        firePropertyChange("placeholderColor", old, this.placeholderColor);
        repaintOverlay();
    }


    public int getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(int cornerRadius) {
        int old = this.cornerRadius;
        this.cornerRadius = Math.max(-1, cornerRadius);
        firePropertyChange("cornerRadius", old, this.cornerRadius);
        repaint();
        repaintOverlay();
    }

    public int getExpandedWidth() {
        return expandedWidth;
    }

    public void setExpandedWidth(int expandedWidth) {
        int old = this.expandedWidth;
        this.expandedWidth = Math.max(120, expandedWidth);
        firePropertyChange("expandedWidth", old, this.expandedWidth);
        repositionOverlay();
        repaintOverlay();
    }

    @Override
    public void setBackground(Color bg) {
        Color old = getBackground();
        super.setBackground(bg);

        if (overlayTextField != null) {
            overlayTextField.setBackground(new Color(0, 0, 0, 0));
        }

        firePropertyChange("background", old, bg);
        repaint();
        repaintOverlay();
    }

    @Override
    public void setForeground(Color fg) {
        Color old = getForeground();
        super.setForeground(fg);
        if (overlayTextField != null) {
            overlayTextField.setForeground(fg);
            overlayTextField.setCaretColor(fg);
        }
        firePropertyChange("foreground", old, fg);
        repaint();
        repaintOverlay();
    }

    @Override
    public void setFont(Font font) {
        Font old = getFont();
        super.setFont(font);
        if (overlayTextField != null) {
            overlayTextField.setFont(font);
        }
        firePropertyChange("font", old, font);
        repaint();
        repaintOverlay();
    }

    @Override
    public void setEnabled(boolean enabled) {
        boolean old = isEnabled();
        super.setEnabled(enabled);
        if (overlayTextField != null) {
            overlayTextField.setEnabled(enabled);
        }
        if (!enabled) {
            setExpanded(false);
            pressing = false;
            hoverClear = false;
            setCursor(Cursor.getDefaultCursor());
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        firePropertyChange("enabled", old, enabled);
        repaint();
        repaintOverlay();
    }

    // ---------------------------------------------------------------------
    // Mode helpers
    // ---------------------------------------------------------------------

    private boolean shouldForceDesignerCollapse() {
        return mode == MODE_EXPANDABLE && Beans.isDesignTime();
    }

    private void enforceModeState() {
        if (mode == MODE_FIXED) {
            if (!expanded) {
                expanded = true;
                firePropertyChange("expanded", false, true);
            }
            animationProgress = 1f;
            animationTarget = 1f;
            if (animationTimer.isRunning()) {
                animationTimer.stop();
            }
            showOverlay();
            repositionOverlay();
            repaintOverlay();
            return;
        }

        if (shouldForceDesignerCollapse()) {
            if (animationTimer.isRunning()) {
                animationTimer.stop();
            }
            boolean old = expanded;
            expanded = false;
            animationProgress = 0f;
            animationTarget = 0f;
            if (old) {
                firePropertyChange("expanded", true, false);
            }
            hideOverlayNow();
            repaint();
        }
    }

    // ---------------------------------------------------------------------
    // Animation / overlay
    // ---------------------------------------------------------------------

    private void stepAnimation() {
        if (mode == MODE_FIXED) {
            animationProgress = 1f;
            animationTarget = 1f;
            animationTimer.stop();
            showOverlay();
            repositionOverlay();
            repaint();
            return;
        }

        if (animationProgress < animationTarget) {
            animationProgress = Math.min(animationTarget, animationProgress + ANIMATION_STEP);
        } else if (animationProgress > animationTarget) {
            animationProgress = Math.max(animationTarget, animationProgress - ANIMATION_STEP);
        }

        if (overlayPanel != null) {
            repositionOverlay();
            overlayPanel.repaint();
        }
        repaint();

        if (Math.abs(animationProgress - animationTarget) < 0.0001f) {
            animationProgress = animationTarget;
            animationTimer.stop();

            if (animationProgress <= 0f) {
                hideOverlayNow();
            } else if (animationProgress >= 1f && overlayTextField != null) {
                SwingUtilities.invokeLater(() -> {
                    if (mode == MODE_EXPANDABLE && !shouldForceDesignerCollapse()) {
                        overlayTextField.requestFocusInWindow();
                        overlayTextField.setCaretPosition(overlayTextField.getText().length());
                    }
                });
            }
        }
    }

    private void ensureOverlayCreated() {
        if (overlayPanel != null) {
            return;
        }

        overlayPanel = new AnimatedOverlayPanel();
        overlayPanel.setOpaque(false);
        overlayPanel.setLayout(null);

        overlayTextField = new PlaceholderTextField();
        overlayTextField.setOpaque(false);
        overlayTextField.setBorder(null);
        overlayTextField.setForeground(getForeground());
        overlayTextField.setCaretColor(getForeground());
        overlayTextField.setBackground(new Color(0, 0, 0, 0));
        overlayTextField.setFont(getFont());
        overlayTextField.setMargin(new Insets(0, 0, 0, 0));
        overlayTextField.addFocusListener(this);
        overlayTextField.addKeyListener(this);
        overlayTextField.addActionListener(e -> commitSearchText(true));

        overlayTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                syncTextFromField();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                syncTextFromField();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                syncTextFromField();
            }
        });

        overlayPanel.add(overlayTextField);

        overlayPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (mode == MODE_FIXED) {
                    if (overlayTextField != null) {
                        overlayTextField.requestFocusInWindow();
                    }
                    return;
                }

                if (isClearIconHit(e.getPoint())) {
                    handleClearAction();
                } else if (overlayTextField != null) {
                    overlayTextField.requestFocusInWindow();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoverClear = false;
                updateOverlayCursor(null);
                overlayPanel.repaint();
            }
        });

        overlayPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                boolean old = hoverClear;
                hoverClear = (mode == MODE_EXPANDABLE) && isClearIconHit(e.getPoint());
                updateOverlayCursor(e.getPoint());
                if (old != hoverClear) {
                    overlayPanel.repaint();
                }
            }
        });
    }

    private void handleSelfBoundsChanged() {
        if (mode == MODE_FIXED) {
            showOverlay();
        }
        repositionOverlay();
        repaintOverlay();
    }

    private boolean commitSearchText(boolean fireSubmitted) {
        String newText = overlayTextField != null && overlayTextField.getText() != null
                ? overlayTextField.getText()
                : this.searchText;

        String old = this.searchText;
        boolean changed = !old.equals(newText);
        this.searchText = newText;

        if (changed) {
            firePropertyChange("searchText", old, this.searchText);
        }

        if (fireSubmitted) {
            firePropertyChange("searchSubmitted", false, true);
        }

        repaintOverlay();
        repaint();
        return changed;
    }

    private void syncTextFromField() {
        if (deferUpdates) {
            repaintOverlay();
            return; // wait for Enter
        }

        commitSearchText(false);
    }

    private void showOverlay() {
        if (!isShowing()) {
            return;
        }

        JRootPane rootPane = SwingUtilities.getRootPane(this);
        if (rootPane == null) {
            return;
        }

        JLayeredPane layeredPane = rootPane.getLayeredPane();
        if (layeredPane == null) {
            return;
        }

        ensureOverlayCreated();

        if (hostLayeredPane != layeredPane) {
            if (hostLayeredPane != null && overlayPanel.getParent() == hostLayeredPane) {
                hostLayeredPane.remove(overlayPanel);
            }
            hostLayeredPane = layeredPane;
            hostLayeredPane.add(overlayPanel, JLayeredPane.POPUP_LAYER);
        }

        overlayTextField.setText(searchText);
        overlayTextField.setForeground(getForeground());
        overlayTextField.setCaretColor(getForeground());
        overlayTextField.setBackground(new Color(0, 0, 0, 0));
        overlayTextField.setFont(getFont());

        overlayPanel.setVisible(true);

        if (mode == MODE_EXPANDABLE) {
            installFocusTracker();
        } else {
            uninstallFocusTracker();
        }

        repositionOverlay();
        overlayPanel.repaint();
        hostLayeredPane.repaint();
    }

    private void hideOverlayNow() {
        uninstallFocusTracker();
        hoverClear = false;

        if (overlayPanel != null) {
            overlayPanel.setVisible(false);
            overlayPanel.setCursor(Cursor.getDefaultCursor());
        }
        if (hostLayeredPane != null) {
            hostLayeredPane.repaint();
        }
    }

    private void repositionOverlay() {
        if (overlayPanel == null || hostLayeredPane == null || !isShowing()) {
            return;
        }

        Point p = SwingUtilities.convertPoint(this, 0, 0, hostLayeredPane);

        int collapsedW = getWidth();
        int currentW;

        if (mode == MODE_FIXED) {
            currentW = Math.max(collapsedW, expandedWidth);
        } else {
            int fullW = expandedWidth;
            currentW = collapsedW + Math.round((fullW - collapsedW) * animationProgress);
        }

        int currentH = Math.max(32, getHeight());

        int x;
        if (mode == MODE_FIXED) {
            x = (expandDirection == EXPAND_LEFT)
                    ? p.x + getWidth() - currentW
                    : p.x;
        } else if (expandDirection == EXPAND_RIGHT) {
            x = p.x;
        } else {
            x = p.x + getWidth() - currentW;
        }

        int y = p.y;

        overlayPanel.setBounds(x, y, currentW, currentH);
        layoutOverlayChildren(currentW, currentH);
        overlayPanel.revalidate();
        overlayPanel.repaint();
    }

    private void repaintOverlay() {
        if (overlayPanel != null && overlayPanel.isVisible()) {
            overlayPanel.repaint();
        }
    }

    private void layoutOverlayChildren(int w, int h) {
        if (overlayTextField == null) {
            return;
        }

        int slot = Math.max(h, DEFAULT_SIZE);
        int tfX = slot;
        int tfY = Math.max(4, h / 6);
        int tfW;

        if (mode == MODE_FIXED) {
            tfW = Math.max(0, w - slot - 10);
        } else {
            tfW = Math.max(0, w - (slot * 2));
        }

        int tfH = Math.max(18, h - (tfY * 2));

        overlayTextField.setBounds(tfX, tfY, tfW, tfH);

        boolean showField = mode == MODE_FIXED || animationProgress > 0.22f;
        overlayTextField.setVisible(showField && tfW > 10);
    }

    private void installFocusTracker() {
        if (focusTracker != null) {
            return;
        }

        focusTracker = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Object newValue = evt.getNewValue();
                if (!(newValue instanceof Component)) {
                    return;
                }

                Component c = (Component) newValue;

                if (c == ExpandableSearchBoxComponent.this ||
                        SwingUtilities.isDescendingFrom(c, ExpandableSearchBoxComponent.this) ||
                        (overlayPanel != null && SwingUtilities.isDescendingFrom(c, overlayPanel))) {
                    return;
                }

                if (mode == MODE_EXPANDABLE) {
                    setExpanded(false);
                }
            }
        };

        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addPropertyChangeListener("focusOwner", focusTracker);
    }

    private void uninstallFocusTracker() {
        if (focusTracker != null) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager()
                    .removePropertyChangeListener("focusOwner", focusTracker);
            focusTracker = null;
        }
    }

    private boolean isClearIconHit(Point p) {
        return mode == MODE_EXPANDABLE && p != null && getOverlayClearIconRect().contains(p);
    }

    private void updateOverlayCursor(Point p) {
        if (overlayPanel == null || !isEnabled()) {
            return;
        }

        if (mode == MODE_FIXED) {
            overlayPanel.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
            return;
        }

        if (p != null && isClearIconHit(p)) {
            overlayPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            overlayPanel.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        }
    }

    private void handleClearAction() {
        if (mode == MODE_FIXED) {
            return;
        }

        if (searchText != null && !searchText.isEmpty()) {
            setSearchText("");
            if (overlayTextField != null) {
                overlayTextField.setText("");
                overlayTextField.requestFocusInWindow();
            }
        } else {
            setExpanded(false);
        }
    }

    // ---------------------------------------------------------------------
    // Geometry
    // ---------------------------------------------------------------------

    private Rectangle getMainIconRect() {
        int w = getWidth();
        int h = getHeight();
        int size = Math.max(8, Math.min(iconSize, Math.min(w, h) - 12));
        int x = (w - size) / 2;
        int y = (h - size) / 2;
        return new Rectangle(x, y, size, size);
    }

    private Rectangle getOverlaySearchIconRect() {
        int w = overlayPanel != null ? overlayPanel.getWidth() : getWidth();
        int h = overlayPanel != null ? overlayPanel.getHeight() : Math.max(32, getHeight());

        int slot = Math.max(h, DEFAULT_SIZE);
        int size = Math.max(8, Math.min(iconSize, h - 12));
        int y = (h - size) / 2;

        int slotX = (iconPosition == ICON_LEFT) ? 0 : Math.max(0, w - slot);
        int x = slotX + ((slot - size) / 2);

        return new Rectangle(x, y, size, size);
    }

    private Rectangle getOverlayClearIconRect() {
        int w = overlayPanel != null ? overlayPanel.getWidth() : getWidth();
        int h = overlayPanel != null ? overlayPanel.getHeight() : Math.max(32, getHeight());

        int slot = Math.max(h, DEFAULT_SIZE);
        int size = Math.max(8, Math.min(iconSize, h - 12));
        int y = (h - size) / 2;

        int slotX = (iconPosition == ICON_LEFT) ? Math.max(0, w - slot) : 0;
        int x = slotX + ((slot - size) / 2);

        return new Rectangle(x, y, size, size);
    }

    private int getEffectiveRadius(int w, int h) {
        if (cornerRadius == -1) {
            return h;
        }
        return Math.max(0, Math.min(cornerRadius, Math.min(w, h)));
    }

    private boolean isInMainButton(Point p) {
        if (p == null) {
            return false;
        }
        return new Ellipse2D.Float(0, 0, getWidth(), getHeight()).contains(p);
    }

    // ---------------------------------------------------------------------
    // Paint
    // ---------------------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (mode == MODE_FIXED) {
            return;
        }

        if (animationProgress > 0.02f) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        float inset = borderWidth / 2f;
        Shape shape = new Ellipse2D.Float(
                inset,
                inset,
                Math.max(1f, w - borderWidth),
                Math.max(1f, h - borderWidth)
        );

        Color fill = getBackground() != null ? getBackground() : Color.WHITE;
        Color stroke = borderColor != null ? borderColor : new Color(210, 210, 210);
        Color icon = iconColor != null ? iconColor : new Color(66, 133, 244);

        if (!isEnabled()) {
            fill = withAlpha(fill, 140);
            stroke = withAlpha(stroke, 120);
            icon = withAlpha(icon, 120);
        } else if (pressing) {
            fill = fill.darker();
        }

        g2.setColor(fill);
        g2.fill(shape);

        if (borderWidth > 0f) {
            g2.setStroke(new BasicStroke(borderWidth));
            g2.setColor(stroke);
            g2.draw(shape);
        }

        paintSearchIcon(g2, getMainIconRect(), icon);
        g2.dispose();
    }

    private void paintSearchIcon(Graphics2D g2, Rectangle r, Color c) {
        g2.setColor(c);
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        int lens = Math.max(5, Math.min(r.width, r.height) - 4);
        int lensX = r.x;
        int lensY = r.y;
        g2.drawOval(lensX, lensY, lens, lens);

        int handleX1 = lensX + lens - 1;
        int handleY1 = lensY + lens - 1;
        int handleX2 = r.x + r.width;
        int handleY2 = r.y + r.height;
        g2.drawLine(handleX1, handleY1, handleX2, handleY2);
    }

    private void paintClearIcon(Graphics2D g2, Rectangle r, Color c) {
        g2.setColor(c);
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        int p = 2;
        g2.drawLine(r.x + p, r.y + p, r.x + r.width - p, r.y + r.height - p);
        g2.drawLine(r.x + r.width - p, r.y + p, r.x + p, r.y + r.height - p);
    }

    private Color withAlpha(Color c, int alpha) {
        if (c == null) {
            c = Color.GRAY;
        }
        alpha = Math.max(0, Math.min(255, alpha));
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    private class AnimatedOverlayPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            float inset = borderWidth / 2f;
            int radius = getEffectiveRadius(w, h);

            Shape shape;
            if (mode == MODE_EXPANDABLE && w <= h + 2) {
                shape = new Ellipse2D.Float(
                        inset,
                        inset,
                        Math.max(1f, w - borderWidth),
                        Math.max(1f, h - borderWidth)
                );
            } else {
                shape = new RoundRectangle2D.Float(
                        inset,
                        inset,
                        Math.max(1f, w - borderWidth),
                        Math.max(1f, h - borderWidth),
                        radius,
                        radius
                );
            }

            Color fill = ExpandableSearchBoxComponent.this.getBackground();
            if (fill == null) {
                fill = Color.WHITE;
            }

            Color stroke = borderColor != null ? borderColor : new Color(210, 210, 210);
            Color icon = iconColor != null ? iconColor : new Color(66, 133, 244);

            if (!isEnabled()) {
                fill = withAlpha(fill, 140);
                stroke = withAlpha(stroke, 120);
                icon = withAlpha(icon, 120);
            }

            g2.setColor(fill);
            g2.fill(shape);

            if (borderWidth > 0f) {
                g2.setStroke(new BasicStroke(borderWidth));
                g2.setColor(stroke);
                g2.draw(shape);
            }

            paintSearchIcon(g2, getOverlaySearchIconRect(), icon);

            if (mode == MODE_EXPANDABLE && animationProgress > 0.35f) {
                Color clearColor;
                if (searchText != null && !searchText.isEmpty()) {
                    clearColor = hoverClear ? withAlpha(icon, 220) : withAlpha(icon, 150);
                } else {
                    clearColor = hoverClear ? withAlpha(icon, 180) : withAlpha(icon, 110);
                }
                paintClearIcon(g2, getOverlayClearIconRect(), clearColor);
            }

            g2.dispose();
        }
    }

    private class PlaceholderTextField extends JTextField {

        @Override
        public boolean isOpaque() {
            return false;
        }

        @Override
        protected void paintComponent(Graphics g) {
            setBackground(new Color(0, 0, 0, 0));
            super.paintComponent(g);

            if (getText() != null && !getText().isEmpty()) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Font baseFont = ExpandableSearchBoxComponent.this.getFont();
            if (baseFont == null) {
                baseFont = new Font("Dialog", Font.PLAIN, 14);
            }

            Font placeholderFont = baseFont;

            g2.setFont(placeholderFont);
            g2.setColor(placeholderColor != null ? placeholderColor : new Color(150, 150, 150));

            FontMetrics fm = g2.getFontMetrics();
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholderText != null ? placeholderText : "", 0, y);

            g2.dispose();
        }
    }

    // ---------------------------------------------------------------------
    // Events
    // ---------------------------------------------------------------------

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!isEnabled() || mode == MODE_FIXED) {
            return;
        }

        if (shouldForceDesignerCollapse()) {
            setExpanded(false);
            return;
        }

        if (isInMainButton(e.getPoint())) {
            setExpanded(!expanded);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!isEnabled() || mode == MODE_FIXED || shouldForceDesignerCollapse()) {
            return;
        }

        if (isInMainButton(e.getPoint()) && animationProgress <= 0.02f) {
            pressing = true;
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pressing = false;
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) {
        pressing = false;
        repaint();
    }

    @Override
    public void focusGained(FocusEvent e) {
        repaint();
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (mode == MODE_EXPANDABLE && shouldForceDesignerCollapse()) {
            setExpanded(false);
        }
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!isEnabled()) {
            return;
        }

        Object src = e.getSource();
        boolean fromTextField = src == overlayTextField;

        if (mode == MODE_FIXED) {
            if (fromTextField && e.getKeyCode() == KeyEvent.VK_ENTER) {
                commitSearchText(true);
            }
            return;
        }

        if (shouldForceDesignerCollapse()) {
            setExpanded(false);
            return;
        }

        if (fromTextField) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                setExpanded(false);
            }
            return;
        }

        if (!expanded && (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE)) {
            setExpanded(true);
            return;
        }

        if (expanded && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            setExpanded(false);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { }

    @Override
    public void addNotify() {
        super.addNotify();

        SwingUtilities.invokeLater(() -> {
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w != null && hostWindowListener == null) {
                hostWindowListener = new ComponentAdapter() {
                    @Override
                    public void componentMoved(ComponentEvent e) {
                        repositionOverlay();
                    }

                    @Override
                    public void componentResized(ComponentEvent e) {
                        repositionOverlay();
                    }
                };
                w.addComponentListener(hostWindowListener);
            }

            enforceModeState();

            if (mode == MODE_FIXED) {
                showOverlay();
            }
        });
    }

    @Override
    public void removeNotify() {
        uninstallFocusTracker();

        Window w = SwingUtilities.getWindowAncestor(this);
        if (w != null && hostWindowListener != null) {
            w.removeComponentListener(hostWindowListener);
        }
        hostWindowListener = null;

        if (hostLayeredPane != null && overlayPanel != null && overlayPanel.getParent() == hostLayeredPane) {
            hostLayeredPane.remove(overlayPanel);
            hostLayeredPane.repaint();
        }

        if (animationTimer != null) {
            animationTimer.stop();
        }

        overlayPanel = null;
        overlayTextField = null;
        hostLayeredPane = null;

        if (mode == MODE_FIXED) {
            animationProgress = 1f;
            animationTarget = 1f;
        } else {
            animationProgress = 0f;
            animationTarget = 0f;
        }

        super.removeNotify();
    }
}