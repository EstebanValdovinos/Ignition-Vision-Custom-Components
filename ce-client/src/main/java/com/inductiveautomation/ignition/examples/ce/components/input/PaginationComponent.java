package com.inductiveautomation.ignition.examples.ce.components.input;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class PaginationComponent extends JComponent {

    public static final int TYPE_INPUT = 0;
    public static final int TYPE_NUMBERS = 1;

    public static final int NAV_LABEL_MODE_ICON_ONLY = 0;
    public static final int NAV_LABEL_MODE_TEXT_ONLY = 1;
    public static final int NAV_LABEL_MODE_ICON_AND_TEXT = 2;

    private static final String DEFAULT_SEPARATOR = "of";
    private static final String DEFAULT_ELLIPSIS = "...";

    // -----------------------------
    // Data
    // -----------------------------
    private int type = TYPE_NUMBERS;
    private int currentPage = 5;
    private int totalPages = 10;
    private int visiblePageCount = 5;

    // -----------------------------
    // Behavior
    // -----------------------------
    private boolean enabled = true;
    private boolean showFirstButton = true;
    private boolean showPreviousButton = true;
    private boolean showNextButton = true;
    private boolean showLastButton = true;
    private boolean showPageInput = true;
    private boolean showTotalPages = true;
    private boolean editablePageInput = true;
    private boolean showEllipsis = true;
    private boolean alwaysShowFirstLastPage = true;
    private boolean centerSelectedPage = true;
    private boolean commitOnEnter = true;
    private boolean commitOnFocusLost = true;
    private boolean autoClampPage = true;

    // -----------------------------
    // Navigation label behavior
    // -----------------------------
    private int navLabelMode = NAV_LABEL_MODE_ICON_AND_TEXT;
    private String firstButtonText = "First";
    private String previousButtonText = "Prev";
    private String nextButtonText = "Next";
    private String lastButtonText = "Last";

    // -----------------------------
    // Appearance
    // -----------------------------
    private Color background = new Color(255,255,255,0);
    private Color foreground = new Color(60, 60, 60);
    private Color borderColor = new Color(210, 210, 210);
    private int borderWidth = 0;
    private int cornerRadius = 10;
    private int padding = 6;
    private int buttonGap = 10;
    private int buttonWidth = 36;
    private int buttonHeight = 32;
    private int inputWidth = 40;
    private int pageButtonMinWidth = 36;
    private Color hoverBackground = new Color(245, 247, 250);
    private Color selectedPageBackground = new Color(235, 245, 255);
    private Color selectedPageForeground = new Color(0, 102, 204);
    private Color selectedPageBorderColor = new Color(160, 200, 240);
    private Color inputBackground = Color.WHITE;
    private Color inputForeground = new Color(60, 60, 60);
    private Color inputBorderColor = new Color(190, 190, 190);
    private Color secondaryTextColor = new Color(130, 130, 130);
    private Color iconColor = new Color(90, 150, 220);

    // New nav button border controls
    private boolean showNavButtonBorder = false;
    private Color navButtonBorderColor = new Color(120, 185, 245);

    private final JTextField pageField = new JTextField("1");
    private final List<HitRegion> hitRegions = new ArrayList<>();

    private String hoveredKey = null;
    private String pressedKey = null;

    public PaginationComponent() {
        setLayout(null);
        setOpaque(false);
        setFont(new Font("Dialog", Font.PLAIN, 12));
        setPreferredSize(new Dimension(620, 40));
        setMinimumSize(new Dimension(180, 36));

        configurePageField();
        installMouseHandlers();
        updatePageField();
        updateFieldVisibility();
        syncFieldStyle();
    }

    // -------------------------------------------------------------------------
    // Setup
    // -------------------------------------------------------------------------

    private void configurePageField() {
        pageField.setHorizontalAlignment(JTextField.CENTER);
        pageField.setBorder(BorderFactory.createEmptyBorder());
        pageField.setOpaque(false);
        pageField.setFont(getFont());
        pageField.setText(String.valueOf(currentPage));
        pageField.setCaretColor(inputForeground);
        pageField.setForeground(inputForeground);
        pageField.setSelectionColor(new Color(210, 230, 255));
        pageField.setSelectedTextColor(inputForeground);

        pageField.addActionListener(e -> {
            if (commitOnEnter) {
                commitPageField();
            }
        });

        pageField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (enabled && editablePageInput) {
                    SwingUtilities.invokeLater(pageField::selectAll);
                }
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (commitOnFocusLost) {
                    commitPageField();
                }
                repaint();
            }
        });

        add(pageField);
    }

    private void installMouseHandlers() {
        MouseAdapter mouseAdapter = new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                if (!enabled) {
                    setCursor(Cursor.getDefaultCursor());
                    return;
                }

                String old = hoveredKey;
                hoveredKey = findHitKey(e.getPoint());

                if (hoveredKey != null) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(Cursor.getDefaultCursor());
                }

                if (!equalsNullable(old, hoveredKey)) {
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                String oldHover = hoveredKey;
                hoveredKey = null;
                pressedKey = null;
                setCursor(Cursor.getDefaultCursor());

                if (oldHover != null) {
                    repaint();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (!enabled || !SwingUtilities.isLeftMouseButton(e)) {
                    return;
                }

                pressedKey = findHitKey(e.getPoint());
                if (pressedKey != null) {
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!enabled || !SwingUtilities.isLeftMouseButton(e)) {
                    return;
                }

                String releasedKey = findHitKey(e.getPoint());
                String actionKey = pressedKey;
                pressedKey = null;

                if (actionKey != null && actionKey.equals(releasedKey)) {
                    handleClick(actionKey);
                }

                repaint();
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    private boolean equalsNullable(String a, String b) {
        return a == null ? b == null : a.equals(b);
    }

    private void syncFieldStyle() {
        pageField.setFont(getFont());
        pageField.setForeground(inputForeground);
        pageField.setCaretColor(inputForeground);
        pageField.setEnabled(enabled && editablePageInput);
        pageField.setFocusable(enabled && editablePageInput);
    }

    // -------------------------------------------------------------------------
    // Layout
    // -------------------------------------------------------------------------

    @Override
    public void doLayout() {
        super.doLayout();

        if (type == TYPE_INPUT && showPageInput) {
            int fieldH = getFieldHeight();
            int fieldY = (getHeight() - fieldH) / 2;

            FontMetrics fm = getFontMetrics(getFont());
            int contentW = measureInputModeWidth(fm);
            int startX = getCenteredStartX(contentW);

            if (showFirstButton) {
                startX += measureNavButtonWidth(fm, "first") + buttonGap;
            }
            if (showPreviousButton) {
                startX += measureNavButtonWidth(fm, "prev") + buttonGap;
            }

            pageField.setBounds(startX, fieldY, inputWidth, fieldH);
        } else {
            pageField.setBounds(0, 0, 0, 0);
        }
    }

    private int getCenteredStartX(int contentWidth) {
        int available = getWidth() - (padding * 2);
        int offset = Math.max(0, (available - contentWidth) / 2);
        return padding + offset;
    }

    private int getFieldHeight() {
        return Math.max(22, buttonHeight);
    }

    private int getButtonArc() {
        return Math.min(12, Math.max(6, buttonHeight));
    }

    private int measureInputModeWidth(FontMetrics fm) {
        int width = 0;

        if (showFirstButton) width += measureNavButtonWidth(fm, "first");
        if (showPreviousButton) {
            if (width > 0) width += buttonGap;
            width += measureNavButtonWidth(fm, "prev");
        }

        if (showPageInput) {
            if (width > 0) width += buttonGap;
            width += inputWidth;
        }

        if (showTotalPages) {
            if (width > 0) width += buttonGap;
            String text = DEFAULT_SEPARATOR + " " + totalPages;
            width += fm.stringWidth(text);
        }

        if (showNextButton) {
            if (width > 0) width += buttonGap;
            width += measureNavButtonWidth(fm, "next");
        }

        if (showLastButton) {
            if (width > 0) width += buttonGap;
            width += measureNavButtonWidth(fm, "last");
        }

        return width;
    }

    private int measureNumbersModeWidth(FontMetrics fm) {
        int width = 0;

        if (showFirstButton) width += measureNavButtonWidth(fm, "first");
        if (showPreviousButton) {
            if (width > 0) width += buttonGap;
            width += measureNavButtonWidth(fm, "prev");
        }

        List<PageToken> tokens = buildPageTokens();
        for (PageToken token : tokens) {
            if (width > 0) width += buttonGap;
            int tokenWidth = token.ellipsis
                    ? Math.max(pageButtonMinWidth - 6, fm.stringWidth(token.text) + 8)
                    : Math.max(pageButtonMinWidth, fm.stringWidth(token.text) + 16);
            width += tokenWidth;
        }

        if (showNextButton) {
            if (width > 0) width += buttonGap;
            width += measureNavButtonWidth(fm, "next");
        }

        if (showLastButton) {
            if (width > 0) width += buttonGap;
            width += measureNavButtonWidth(fm, "last");
        }

        return width;
    }

    private int measureNavButtonWidth(FontMetrics fm, String key) {
        String label = getNavLabelText(key);
        boolean drawIcon = shouldDrawNavIcon();
        boolean drawText = shouldDrawNavText() && label != null && !label.isEmpty();

        int width = 0;

        if (drawIcon) {
            width += 12;
        }
        if (drawText) {
            if (width > 0) width += 4;
            width += fm.stringWidth(label);
        }

        width += 16; // left-right padding
        return Math.max(buttonWidth, width);
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        int contentWidth = type == TYPE_INPUT ? measureInputModeWidth(fm) : measureNumbersModeWidth(fm);
        int width = contentWidth + (padding * 2) + 10;
        int height = Math.max(buttonHeight, getFieldHeight()) + (padding * 2) + 4;
        return new Dimension(Math.max(width, 180), Math.max(height, 36));
    }

    // -------------------------------------------------------------------------
    // Painting
    // -------------------------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            applyQualityHints(g2);

            int w = getWidth();
            int h = getHeight();

            paintOuterBackground(g2, w, h);

            hitRegions.clear();

            if (type == TYPE_INPUT) {
                paintInputMode(g2);
            } else {
                paintNumbersMode(g2);
            }
        } finally {
            g2.dispose();
        }
    }

    private void applyQualityHints(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    private void paintOuterBackground(Graphics2D g2, int w, int h) {
        // Respect full transparency
        if (background != null && background.getAlpha() > 0) {
            g2.setColor(background);
            g2.fillRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);
        }

        if (borderWidth > 0 && borderColor != null && borderColor.getAlpha() > 0) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(borderWidth));
            int inset = Math.max(0, borderWidth / 2);
            g2.drawRoundRect(inset, inset, w - borderWidth - 1, h - borderWidth - 1, cornerRadius, cornerRadius);
        }
    }

    private void paintInputMode(Graphics2D g2) {
        FontMetrics fm = g2.getFontMetrics(getFont());
        int contentW = measureInputModeWidth(fm);
        int x = getCenteredStartX(contentW);
        int y = (getHeight() - buttonHeight) / 2;

        if (showFirstButton) {
            int bw = measureNavButtonWidth(fm, "first");
            x = paintNavButton(g2, "first", x, y, bw, buttonHeight, currentPage <= 1);
            x += buttonGap;
        }

        if (showPreviousButton) {
            int bw = measureNavButtonWidth(fm, "prev");
            x = paintNavButton(g2, "prev", x, y, bw, buttonHeight, currentPage <= 1);
            x += buttonGap;
        }

        if (showPageInput) {
            Rectangle inputRect = new Rectangle(x, (getHeight() - getFieldHeight()) / 2, inputWidth, getFieldHeight());
            paintInputField(g2, inputRect);
            x += inputWidth;
            if (showTotalPages || showNextButton || showLastButton) {
                x += buttonGap;
            }
        }

        if (showTotalPages) {
            String text = DEFAULT_SEPARATOR + " " + totalPages;
            int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.setColor(secondaryTextColor);
            g2.setFont(getFont());
            g2.drawString(text, x, ty);
            x += fm.stringWidth(text);

            if (showNextButton || showLastButton) {
                x += buttonGap;
            }
        }

        if (showNextButton) {
            int bw = measureNavButtonWidth(fm, "next");
            x = paintNavButton(g2, "next", x, y, bw, buttonHeight, currentPage >= totalPages);
            x += buttonGap;
        }

        if (showLastButton) {
            int bw = measureNavButtonWidth(fm, "last");
            paintNavButton(g2, "last", x, y, bw, buttonHeight, currentPage >= totalPages);
        }
    }

    private void paintNumbersMode(Graphics2D g2) {
        FontMetrics fm = g2.getFontMetrics(getFont());
        int contentW = measureNumbersModeWidth(fm);
        int x = getCenteredStartX(contentW);
        int y = (getHeight() - buttonHeight) / 2;

        if (showFirstButton) {
            int bw = measureNavButtonWidth(fm, "first");
            x = paintNavButton(g2, "first", x, y, bw, buttonHeight, currentPage <= 1);
            x += buttonGap;
        }

        if (showPreviousButton) {
            int bw = measureNavButtonWidth(fm, "prev");
            x = paintNavButton(g2, "prev", x, y, bw, buttonHeight, currentPage <= 1);
            x += buttonGap;
        }

        List<PageToken> tokens = buildPageTokens();
        for (int i = 0; i < tokens.size(); i++) {
            PageToken token = tokens.get(i);

            int width = token.ellipsis
                    ? Math.max(pageButtonMinWidth - 6, fm.stringWidth(token.text) + 8)
                    : Math.max(pageButtonMinWidth, fm.stringWidth(token.text) + 16);

            Rectangle r = new Rectangle(x, y, width, buttonHeight);

            if (token.ellipsis) {
                drawCenteredText(g2, token.text, r, getFont(), secondaryTextColor);
            } else {
                paintPageButton(g2, "page:" + token.page, token.text, r, token.page == currentPage);
            }

            x += width;
            if (i < tokens.size() - 1 || showNextButton || showLastButton) {
                x += buttonGap;
            }
        }

        if (showNextButton) {
            int bw = measureNavButtonWidth(fm, "next");
            x = paintNavButton(g2, "next", x, y, bw, buttonHeight, currentPage >= totalPages);
            x += buttonGap;
        }

        if (showLastButton) {
            int bw = measureNavButtonWidth(fm, "last");
            paintNavButton(g2, "last", x, y, bw, buttonHeight, currentPage >= totalPages);
        }
    }

    private void paintInputField(Graphics2D g2, Rectangle r) {
        boolean focused = pageField.hasFocus() && pageField.isVisible();

        if (inputBackground != null && inputBackground.getAlpha() > 0) {
            g2.setColor(inputBackground);
            g2.fillRoundRect(r.x, r.y, r.width, r.height, 8, 8);
        }

        if (focused) {
            g2.setColor(new Color(170, 210, 255));
            g2.drawRoundRect(r.x, r.y, r.width, r.height, 8, 8);
            g2.setColor(inputBorderColor);
            g2.drawRoundRect(r.x + 1, r.y + 1, r.width - 2, r.height - 2, 7, 7);
        } else {
            g2.setColor(inputBorderColor);
            g2.drawRoundRect(r.x, r.y, r.width, r.height, 8, 8);
        }
    }

    private int paintNavButton(Graphics2D g2, String key, int x, int y, int w, int h, boolean disabled) {
        Rectangle r = new Rectangle(x, y, w, h);
        boolean hovered = key.equals(hoveredKey);
        boolean pressed = key.equals(pressedKey);

        paintNavButtonBase(g2, r, hovered, pressed, disabled);
        paintNavButtonContent(g2, key, r, disabled ? secondaryTextColor : iconColor);

        if (!disabled) {
            hitRegions.add(new HitRegion(key, r));
        }

        return x + w;
    }

    private void paintNavButtonBase(Graphics2D g2, Rectangle r, boolean hovered, boolean pressed, boolean disabled) {
        int arc = getButtonArc();

        if (disabled) {
            if (showNavButtonBorder) {
                g2.setColor(new Color(238, 238, 238));
                g2.drawRoundRect(r.x, r.y, r.width, r.height, arc, arc);
            }
            return;
        }

        if (pressed) {
            g2.setColor(new Color(232, 238, 245));
            g2.fillRoundRect(r.x, r.y, r.width, r.height, arc, arc);
        } else if (hovered) {
            g2.setColor(hoverBackground);
            g2.fillRoundRect(r.x, r.y, r.width, r.height, arc, arc);
        }

        if (showNavButtonBorder) {
            g2.setColor(navButtonBorderColor);
            g2.drawRoundRect(r.x, r.y, r.width, r.height, arc, arc);
        }
    }

    private void paintNavButtonContent(Graphics2D g2, String key, Rectangle r, Color fg) {
        boolean drawIcon = shouldDrawNavIcon();
        String label = getNavLabelText(key);
        boolean drawText = shouldDrawNavText() && label != null && !label.isEmpty();

        FontMetrics fm = g2.getFontMetrics(getFont());

        int iconW = drawIcon ? 12 : 0;
        int textW = drawText ? fm.stringWidth(label) : 0;
        int gap = (drawIcon && drawText) ? 4 : 0;
        int contentW = iconW + gap + textW;

        int startX = r.x + (r.width - contentW) / 2;
        int centerY = r.y + r.height / 2;

        g2.setColor(fg);

        boolean iconLeft = "first".equals(key) || "prev".equals(key);

        if (drawIcon && iconLeft) {
            paintChevronIcon(g2, key, startX, centerY, fg);
            startX += iconW + gap;
        }

        if (drawText) {
            int ty = r.y + ((r.height - fm.getHeight()) / 2) + fm.getAscent();
            g2.setFont(getFont());
            g2.drawString(label, startX, ty);
            startX += textW + gap;
        }

        if (drawIcon && !iconLeft) {
            paintChevronIcon(g2, key, startX, centerY, fg);
        }
    }

    private void paintChevronIcon(Graphics2D g2, String key, int x, int centerY, Color color) {
        Graphics2D g = (Graphics2D) g2.create();
        try {
            g.setColor(color);
            g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            if ("prev".equals(key)) {
                drawSingleChevronLeft(g, x, centerY);
            } else if ("next".equals(key)) {
                drawSingleChevronRight(g, x, centerY);
            } else if ("first".equals(key)) {
                drawDoubleChevronLeft(g, x, centerY);
            } else if ("last".equals(key)) {
                drawDoubleChevronRight(g, x, centerY);
            }
        } finally {
            g.dispose();
        }
    }

    private void drawSingleChevronLeft(Graphics2D g, int x, int cy) {
        Path2D p = new Path2D.Float();
        p.moveTo(x + 7, cy - 5);
        p.lineTo(x + 2, cy);
        p.lineTo(x + 7, cy + 5);
        g.draw(p);
    }

    private void drawSingleChevronRight(Graphics2D g, int x, int cy) {
        Path2D p = new Path2D.Float();
        p.moveTo(x + 3, cy - 5);
        p.lineTo(x + 8, cy);
        p.lineTo(x + 3, cy + 5);
        g.draw(p);
    }

    private void drawDoubleChevronLeft(Graphics2D g, int x, int cy) {
        drawSingleChevronLeft(g, x - 2, cy);
        drawSingleChevronLeft(g, x + 3, cy);
    }

    private void drawDoubleChevronRight(Graphics2D g, int x, int cy) {
        drawSingleChevronRight(g, x + 1, cy);
        drawSingleChevronRight(g, x + 6, cy);
    }

    private void paintPageButton(Graphics2D g2, String key, String text, Rectangle r, boolean selected) {
        boolean hovered = key.equals(hoveredKey);
        boolean pressed = key.equals(pressedKey);

        paintPageButtonBase(g2, r, selected, hovered, pressed);
        drawCenteredText(g2, text, r, getFont(), selected ? selectedPageForeground : foreground);

        hitRegions.add(new HitRegion(key, r));
    }

    private void paintPageButtonBase(Graphics2D g2, Rectangle r, boolean selected, boolean hovered, boolean pressed) {
        int arc = getButtonArc();

        if (selected) {
            g2.setColor(selectedPageBackground);
            g2.fillRoundRect(r.x, r.y, r.width, r.height, arc, arc);

            g2.setColor(selectedPageBorderColor);
            g2.drawRoundRect(r.x, r.y, r.width, r.height, arc, arc);

            if (pressed) {
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fillRoundRect(r.x, r.y, r.width, r.height, arc, arc);
            }
            return;
        }

        if (pressed) {
            g2.setColor(new Color(232, 238, 245));
            g2.fillRoundRect(r.x, r.y, r.width, r.height, arc, arc);
            g2.setColor(new Color(205, 215, 225));
            g2.drawRoundRect(r.x, r.y, r.width, r.height, arc, arc);
            return;
        }

        if (hovered) {
            g2.setColor(hoverBackground);
            g2.fillRoundRect(r.x, r.y, r.width, r.height, arc, arc);
            g2.setColor(new Color(215, 220, 228));
            g2.drawRoundRect(r.x, r.y, r.width, r.height, arc, arc);
        }
    }

    private void drawCenteredText(Graphics2D g2, String text, Rectangle r, Font font, Color color) {
        g2.setFont(font);
        g2.setColor(color);
        FontMetrics fm = g2.getFontMetrics();

        int tx = r.x + (r.width - fm.stringWidth(text)) / 2;
        int ty = r.y + ((r.height - fm.getHeight()) / 2) + fm.getAscent();

        g2.drawString(text, tx, ty);
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    private boolean shouldDrawNavIcon() {
        return navLabelMode == NAV_LABEL_MODE_ICON_ONLY || navLabelMode == NAV_LABEL_MODE_ICON_AND_TEXT;
    }

    private boolean shouldDrawNavText() {
        return navLabelMode == NAV_LABEL_MODE_TEXT_ONLY || navLabelMode == NAV_LABEL_MODE_ICON_AND_TEXT;
    }

    private String getNavLabelText(String key) {
        if ("first".equals(key)) return firstButtonText;
        if ("prev".equals(key)) return previousButtonText;
        if ("next".equals(key)) return nextButtonText;
        if ("last".equals(key)) return lastButtonText;
        return "";
    }

    private String findHitKey(Point p) {
        for (HitRegion region : hitRegions) {
            if (region.bounds.contains(p)) {
                return region.key;
            }
        }
        return null;
    }

    private void handleClick(String key) {
        if ("first".equals(key)) {
            setCurrentPage(1);
        } else if ("prev".equals(key)) {
            setCurrentPage(currentPage - 1);
        } else if ("next".equals(key)) {
            setCurrentPage(currentPage + 1);
        } else if ("last".equals(key)) {
            setCurrentPage(totalPages);
        } else if (key != null && key.startsWith("page:")) {
            try {
                int page = Integer.parseInt(key.substring(5));
                setCurrentPage(page);
            } catch (Exception ignored) {
                // Ignore invalid page token
            }
        }
    }

    private void commitPageField() {
        if (!editablePageInput) {
            updatePageField();
            return;
        }

        String text = pageField.getText() != null ? pageField.getText().trim() : "";
        if (text.isEmpty()) {
            updatePageField();
            return;
        }

        try {
            int value = Integer.parseInt(text);
            if (autoClampPage) {
                value = clampPage(value);
            }
            setCurrentPage(value);
        } catch (NumberFormatException ex) {
            updatePageField();
        }
    }

    private int clampPage(int page) {
        if (totalPages <= 0) {
            return 1;
        }
        return Math.max(1, Math.min(totalPages, page));
    }

    private void updatePageField() {
        pageField.setText(String.valueOf(currentPage));
    }

    private void updateFieldVisibility() {
        boolean visible = type == TYPE_INPUT && showPageInput;
        pageField.setVisible(visible);
        syncFieldStyle();
        repaint();
    }

    private List<PageToken> buildPageTokens() {
        List<PageToken> tokens = new ArrayList<>();

        if (totalPages <= 0) {
            return tokens;
        }

        int maxVisible = Math.max(3, visiblePageCount);

        if (totalPages <= maxVisible || !showEllipsis) {
            for (int i = 1; i <= totalPages; i++) {
                tokens.add(PageToken.page(i));
            }
            return tokens;
        }

        if (alwaysShowFirstLastPage) {
            int middleSlots = Math.max(1, maxVisible - 2);
            int start;
            int end;

            if (centerSelectedPage) {
                start = currentPage - (middleSlots / 2);
                end = start + middleSlots - 1;
            } else {
                start = currentPage;
                end = start + middleSlots - 1;
            }

            if (start < 2) {
                start = 2;
                end = start + middleSlots - 1;
            }

            if (end > totalPages - 1) {
                end = totalPages - 1;
                start = end - middleSlots + 1;
            }

            if (start < 2) {
                start = 2;
            }

            tokens.add(PageToken.page(1));

            if (start > 2) {
                tokens.add(PageToken.ellipsis(DEFAULT_ELLIPSIS));
            }

            for (int i = start; i <= end; i++) {
                if (i > 1 && i < totalPages) {
                    tokens.add(PageToken.page(i));
                }
            }

            if (end < totalPages - 1) {
                tokens.add(PageToken.ellipsis(DEFAULT_ELLIPSIS));
            }

            tokens.add(PageToken.page(totalPages));
        } else {
            int start;
            int end;

            if (centerSelectedPage) {
                start = currentPage - (maxVisible / 2);
                end = start + maxVisible - 1;
            } else {
                start = currentPage;
                end = start + maxVisible - 1;
            }

            if (start < 1) {
                start = 1;
                end = maxVisible;
            }

            if (end > totalPages) {
                end = totalPages;
                start = Math.max(1, end - maxVisible + 1);
            }

            for (int i = start; i <= end; i++) {
                tokens.add(PageToken.page(i));
            }

            if (end < totalPages) {
                tokens.add(PageToken.ellipsis(DEFAULT_ELLIPSIS));
                tokens.add(PageToken.page(totalPages));
            }
        }

        return tokens;
    }

    // -------------------------------------------------------------------------
    // Getters / Setters
    // -------------------------------------------------------------------------

    public int getType() {
        return type;
    }

    public void setType(int type) {
        int old = this.type;
        if (old == type) return;
        this.type = type;
        firePropertyChange("type", old, type);
        updateFieldVisibility();
        revalidate();
        repaint();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        int value = autoClampPage ? clampPage(currentPage) : currentPage;
        int old = this.currentPage;
        if (old == value) {
            updatePageField();
            return;
        }
        this.currentPage = value;
        firePropertyChange("currentPage", old, value);
        updatePageField();
        repaint();
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        int newValue = Math.max(1, totalPages);
        int old = this.totalPages;
        if (old == newValue) return;
        this.totalPages = newValue;
        firePropertyChange("totalPages", old, newValue);

        if (currentPage > this.totalPages) {
            setCurrentPage(this.totalPages);
        } else {
            revalidate();
            repaint();
        }
    }

    public int getVisiblePageCount() {
        return visiblePageCount;
    }

    public void setVisiblePageCount(int visiblePageCount) {
        int newValue = Math.max(3, visiblePageCount);
        int old = this.visiblePageCount;
        if (old == newValue) return;
        this.visiblePageCount = newValue;
        firePropertyChange("visiblePageCount", old, newValue);
        revalidate();
        repaint();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        boolean old = this.enabled;
        if (old == enabled) return;
        this.enabled = enabled;
        super.setEnabled(enabled);
        firePropertyChange("enabled", old, enabled);
        syncFieldStyle();
        repaint();
    }

    public boolean isShowFirstButton() {
        return showFirstButton;
    }

    public void setShowFirstButton(boolean showFirstButton) {
        boolean old = this.showFirstButton;
        if (old == showFirstButton) return;
        this.showFirstButton = showFirstButton;
        firePropertyChange("showFirstButton", old, showFirstButton);
        revalidate();
        repaint();
    }

    public boolean isShowPreviousButton() {
        return showPreviousButton;
    }

    public void setShowPreviousButton(boolean showPreviousButton) {
        boolean old = this.showPreviousButton;
        if (old == showPreviousButton) return;
        this.showPreviousButton = showPreviousButton;
        firePropertyChange("showPreviousButton", old, showPreviousButton);
        revalidate();
        repaint();
    }

    public boolean isShowNextButton() {
        return showNextButton;
    }

    public void setShowNextButton(boolean showNextButton) {
        boolean old = this.showNextButton;
        if (old == showNextButton) return;
        this.showNextButton = showNextButton;
        firePropertyChange("showNextButton", old, showNextButton);
        revalidate();
        repaint();
    }

    public boolean isShowLastButton() {
        return showLastButton;
    }

    public void setShowLastButton(boolean showLastButton) {
        boolean old = this.showLastButton;
        if (old == showLastButton) return;
        this.showLastButton = showLastButton;
        firePropertyChange("showLastButton", old, showLastButton);
        revalidate();
        repaint();
    }

    public boolean isShowPageInput() {
        return showPageInput;
    }

    public void setShowPageInput(boolean showPageInput) {
        boolean old = this.showPageInput;
        if (old == showPageInput) return;
        this.showPageInput = showPageInput;
        firePropertyChange("showPageInput", old, showPageInput);
        updateFieldVisibility();
        revalidate();
        repaint();
    }

    public boolean isShowTotalPages() {
        return showTotalPages;
    }

    public void setShowTotalPages(boolean showTotalPages) {
        boolean old = this.showTotalPages;
        if (old == showTotalPages) return;
        this.showTotalPages = showTotalPages;
        firePropertyChange("showTotalPages", old, showTotalPages);
        revalidate();
        repaint();
    }

    public boolean isEditablePageInput() {
        return editablePageInput;
    }

    public void setEditablePageInput(boolean editablePageInput) {
        boolean old = this.editablePageInput;
        if (old == editablePageInput) return;
        this.editablePageInput = editablePageInput;
        firePropertyChange("editablePageInput", old, editablePageInput);
        syncFieldStyle();
        repaint();
    }

    public boolean isShowEllipsis() {
        return showEllipsis;
    }

    public void setShowEllipsis(boolean showEllipsis) {
        boolean old = this.showEllipsis;
        if (old == showEllipsis) return;
        this.showEllipsis = showEllipsis;
        firePropertyChange("showEllipsis", old, showEllipsis);
        revalidate();
        repaint();
    }

    public boolean isAlwaysShowFirstLastPage() {
        return alwaysShowFirstLastPage;
    }

    public void setAlwaysShowFirstLastPage(boolean alwaysShowFirstLastPage) {
        boolean old = this.alwaysShowFirstLastPage;
        if (old == alwaysShowFirstLastPage) return;
        this.alwaysShowFirstLastPage = alwaysShowFirstLastPage;
        firePropertyChange("alwaysShowFirstLastPage", old, alwaysShowFirstLastPage);
        revalidate();
        repaint();
    }

    public boolean isCenterSelectedPage() {
        return centerSelectedPage;
    }

    public void setCenterSelectedPage(boolean centerSelectedPage) {
        boolean old = this.centerSelectedPage;
        if (old == centerSelectedPage) return;
        this.centerSelectedPage = centerSelectedPage;
        firePropertyChange("centerSelectedPage", old, centerSelectedPage);
        revalidate();
        repaint();
    }

    public boolean isCommitOnEnter() {
        return commitOnEnter;
    }

    public void setCommitOnEnter(boolean commitOnEnter) {
        boolean old = this.commitOnEnter;
        if (old == commitOnEnter) return;
        this.commitOnEnter = commitOnEnter;
        firePropertyChange("commitOnEnter", old, commitOnEnter);
    }

    public boolean isCommitOnFocusLost() {
        return commitOnFocusLost;
    }

    public void setCommitOnFocusLost(boolean commitOnFocusLost) {
        boolean old = this.commitOnFocusLost;
        if (old == commitOnFocusLost) return;
        this.commitOnFocusLost = commitOnFocusLost;
        firePropertyChange("commitOnFocusLost", old, commitOnFocusLost);
    }

    public boolean isAutoClampPage() {
        return autoClampPage;
    }

    public void setAutoClampPage(boolean autoClampPage) {
        boolean old = this.autoClampPage;
        if (old == autoClampPage) return;
        this.autoClampPage = autoClampPage;
        firePropertyChange("autoClampPage", old, autoClampPage);
        setCurrentPage(currentPage);
    }

    public int getNavLabelMode() {
        return navLabelMode;
    }

    public void setNavLabelMode(int navLabelMode) {
        int old = this.navLabelMode;
        if (old == navLabelMode) return;
        this.navLabelMode = navLabelMode;
        firePropertyChange("navLabelMode", old, navLabelMode);
        revalidate();
        repaint();
    }

    public String getFirstButtonText() {
        return firstButtonText;
    }

    public void setFirstButtonText(String firstButtonText) {
        String old = this.firstButtonText;
        String value = firstButtonText != null ? firstButtonText : "";
        if (value.equals(old)) return;
        this.firstButtonText = value;
        firePropertyChange("firstButtonText", old, this.firstButtonText);
        revalidate();
        repaint();
    }

    public String getPreviousButtonText() {
        return previousButtonText;
    }

    public void setPreviousButtonText(String previousButtonText) {
        String old = this.previousButtonText;
        String value = previousButtonText != null ? previousButtonText : "";
        if (value.equals(old)) return;
        this.previousButtonText = value;
        firePropertyChange("previousButtonText", old, this.previousButtonText);
        revalidate();
        repaint();
    }

    public String getNextButtonText() {
        return nextButtonText;
    }

    public void setNextButtonText(String nextButtonText) {
        String old = this.nextButtonText;
        String value = nextButtonText != null ? nextButtonText : "";
        if (value.equals(old)) return;
        this.nextButtonText = value;
        firePropertyChange("nextButtonText", old, this.nextButtonText);
        revalidate();
        repaint();
    }

    public String getLastButtonText() {
        return lastButtonText;
    }

    public void setLastButtonText(String lastButtonText) {
        String old = this.lastButtonText;
        String value = lastButtonText != null ? lastButtonText : "";
        if (value.equals(old)) return;
        this.lastButtonText = value;
        firePropertyChange("lastButtonText", old, this.lastButtonText);
        revalidate();
        repaint();
    }

    @Override
    public Color getBackground() {
        return background;
    }

    @Override
    public void setBackground(Color background) {
        Color old = this.background;
        Color value = background != null ? background : new Color(0, 0, 0, 0);
        if (value.equals(old)) return;
        this.background = value;
        firePropertyChange("background", old, this.background);
        repaint();
    }

    @Override
    public Color getForeground() {
        return foreground;
    }

    @Override
    public void setForeground(Color foreground) {
        Color old = this.foreground;
        Color value = foreground != null ? foreground : new Color(60, 60, 60);
        if (value.equals(old)) return;
        this.foreground = value;
        firePropertyChange("foreground", old, this.foreground);
        repaint();
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        Color old = this.borderColor;
        Color value = borderColor != null ? borderColor : new Color(210, 210, 210);
        if (value.equals(old)) return;
        this.borderColor = value;
        firePropertyChange("borderColor", old, this.borderColor);
        repaint();
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        int value = Math.max(0, borderWidth);
        int old = this.borderWidth;
        if (old == value) return;
        this.borderWidth = value;
        firePropertyChange("borderWidth", old, this.borderWidth);
        repaint();
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(int cornerRadius) {
        int value = Math.max(0, cornerRadius);
        int old = this.cornerRadius;
        if (old == value) return;
        this.cornerRadius = value;
        firePropertyChange("cornerRadius", old, this.cornerRadius);
        repaint();
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        int value = Math.max(0, padding);
        int old = this.padding;
        if (old == value) return;
        this.padding = value;
        firePropertyChange("padding", old, this.padding);
        revalidate();
        repaint();
    }

    public int getButtonGap() {
        return buttonGap;
    }

    public void setButtonGap(int buttonGap) {
        int value = Math.max(0, buttonGap);
        int old = this.buttonGap;
        if (old == value) return;
        this.buttonGap = value;
        firePropertyChange("buttonGap", old, this.buttonGap);
        revalidate();
        repaint();
    }

    public int getButtonWidth() {
        return buttonWidth;
    }

    public void setButtonWidth(int buttonWidth) {
        int value = Math.max(18, buttonWidth);
        int old = this.buttonWidth;
        if (old == value) return;
        this.buttonWidth = value;
        firePropertyChange("buttonWidth", old, this.buttonWidth);
        revalidate();
        repaint();
    }

    public int getButtonHeight() {
        return buttonHeight;
    }

    public void setButtonHeight(int buttonHeight) {
        int value = Math.max(18, buttonHeight);
        int old = this.buttonHeight;
        if (old == value) return;
        this.buttonHeight = value;
        firePropertyChange("buttonHeight", old, this.buttonHeight);
        revalidate();
        repaint();
    }

    public int getInputWidth() {
        return inputWidth;
    }

    public void setInputWidth(int inputWidth) {
        int value = Math.max(24, inputWidth);
        int old = this.inputWidth;
        if (old == value) return;
        this.inputWidth = value;
        firePropertyChange("inputWidth", old, this.inputWidth);
        revalidate();
        repaint();
    }

    public int getPageButtonMinWidth() {
        return pageButtonMinWidth;
    }

    public void setPageButtonMinWidth(int pageButtonMinWidth) {
        int value = Math.max(20, pageButtonMinWidth);
        int old = this.pageButtonMinWidth;
        if (old == value) return;
        this.pageButtonMinWidth = value;
        firePropertyChange("pageButtonMinWidth", old, this.pageButtonMinWidth);
        revalidate();
        repaint();
    }

    public Color getHoverBackground() {
        return hoverBackground;
    }

    public void setHoverBackground(Color hoverBackground) {
        Color old = this.hoverBackground;
        Color value = hoverBackground != null ? hoverBackground : new Color(245, 247, 250);
        if (value.equals(old)) return;
        this.hoverBackground = value;
        firePropertyChange("hoverBackground", old, this.hoverBackground);
        repaint();
    }

    public Color getSelectedPageBackground() {
        return selectedPageBackground;
    }

    public void setSelectedPageBackground(Color selectedPageBackground) {
        Color old = this.selectedPageBackground;
        Color value = selectedPageBackground != null ? selectedPageBackground : new Color(235, 245, 255);
        if (value.equals(old)) return;
        this.selectedPageBackground = value;
        firePropertyChange("selectedPageBackground", old, this.selectedPageBackground);
        repaint();
    }

    public Color getSelectedPageForeground() {
        return selectedPageForeground;
    }

    public void setSelectedPageForeground(Color selectedPageForeground) {
        Color old = this.selectedPageForeground;
        Color value = selectedPageForeground != null ? selectedPageForeground : new Color(0, 102, 204);
        if (value.equals(old)) return;
        this.selectedPageForeground = value;
        firePropertyChange("selectedPageForeground", old, this.selectedPageForeground);
        repaint();
    }

    public Color getSelectedPageBorderColor() {
        return selectedPageBorderColor;
    }

    public void setSelectedPageBorderColor(Color selectedPageBorderColor) {
        Color old = this.selectedPageBorderColor;
        Color value = selectedPageBorderColor != null ? selectedPageBorderColor : new Color(160, 200, 240);
        if (value.equals(old)) return;
        this.selectedPageBorderColor = value;
        firePropertyChange("selectedPageBorderColor", old, this.selectedPageBorderColor);
        repaint();
    }

    public Color getInputBackground() {
        return inputBackground;
    }

    public void setInputBackground(Color inputBackground) {
        Color old = this.inputBackground;
        Color value = inputBackground != null ? inputBackground : Color.WHITE;
        if (value.equals(old)) return;
        this.inputBackground = value;
        firePropertyChange("inputBackground", old, this.inputBackground);
        repaint();
    }

    public Color getInputForeground() {
        return inputForeground;
    }

    public void setInputForeground(Color inputForeground) {
        Color old = this.inputForeground;
        Color value = inputForeground != null ? inputForeground : new Color(60, 60, 60);
        if (value.equals(old)) return;
        this.inputForeground = value;
        firePropertyChange("inputForeground", old, this.inputForeground);
        syncFieldStyle();
        repaint();
    }

    public Color getInputBorderColor() {
        return inputBorderColor;
    }

    public void setInputBorderColor(Color inputBorderColor) {
        Color old = this.inputBorderColor;
        Color value = inputBorderColor != null ? inputBorderColor : new Color(190, 190, 190);
        if (value.equals(old)) return;
        this.inputBorderColor = value;
        firePropertyChange("inputBorderColor", old, this.inputBorderColor);
        repaint();
    }

    public Color getSecondaryTextColor() {
        return secondaryTextColor;
    }

    public void setSecondaryTextColor(Color secondaryTextColor) {
        Color old = this.secondaryTextColor;
        Color value = secondaryTextColor != null ? secondaryTextColor : new Color(130, 130, 130);
        if (value.equals(old)) return;
        this.secondaryTextColor = value;
        firePropertyChange("secondaryTextColor", old, this.secondaryTextColor);
        repaint();
    }

    public Color getIconColor() {
        return iconColor;
    }

    public void setIconColor(Color iconColor) {
        Color old = this.iconColor;
        Color value = iconColor != null ? iconColor : new Color(90, 150, 220);
        if (value.equals(old)) return;
        this.iconColor = value;
        firePropertyChange("iconColor", old, this.iconColor);
        repaint();
    }

    public boolean isShowNavButtonBorder() {
        return showNavButtonBorder;
    }

    public void setShowNavButtonBorder(boolean showNavButtonBorder) {
        boolean old = this.showNavButtonBorder;
        if (old == showNavButtonBorder) return;
        this.showNavButtonBorder = showNavButtonBorder;
        firePropertyChange("showNavButtonBorder", old, showNavButtonBorder);
        repaint();
    }

    public Color getNavButtonBorderColor() {
        return navButtonBorderColor;
    }

    public void setNavButtonBorderColor(Color navButtonBorderColor) {
        Color old = this.navButtonBorderColor;
        Color value = navButtonBorderColor != null ? navButtonBorderColor : new Color(120, 185, 245);
        if (value.equals(old)) return;
        this.navButtonBorderColor = value;
        firePropertyChange("navButtonBorderColor", old, this.navButtonBorderColor);
        repaint();
    }

    @Override
    public void setFont(Font font) {
        Font old = getFont();
        super.setFont(font != null ? font : new Font("Dialog", Font.PLAIN, 12));
        if (pageField != null) {
            pageField.setFont(getFont());
        }
        firePropertyChange("font", old, getFont());
        revalidate();
        repaint();
    }

    // -------------------------------------------------------------------------
    // Helper classes
    // -------------------------------------------------------------------------

    private static class HitRegion {
        final String key;
        final Rectangle bounds;

        HitRegion(String key, Rectangle bounds) {
            this.key = key;
            this.bounds = bounds;
        }
    }

    private static class PageToken {
        final boolean ellipsis;
        final int page;
        final String text;

        private PageToken(boolean ellipsis, int page, String text) {
            this.ellipsis = ellipsis;
            this.page = page;
            this.text = text;
        }

        static PageToken page(int page) {
            return new PageToken(false, page, String.valueOf(page));
        }

        static PageToken ellipsis(String text) {
            return new PageToken(true, -1, text);
        }
    }
}