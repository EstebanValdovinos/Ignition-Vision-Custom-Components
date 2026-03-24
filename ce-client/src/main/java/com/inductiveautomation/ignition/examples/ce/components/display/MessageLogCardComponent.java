package com.inductiveautomation.ignition.examples.ce.components.display;

import com.inductiveautomation.ignition.client.images.ImageLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageLogCardComponent extends JComponent {

    private static final DateTimeFormatter TODAY_TIME_FORMAT =
            DateTimeFormatter.ofPattern("hh:mm:ss a");
    private static final DateTimeFormatter FULL_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm:ss a");

    private String title = "Lorem Ipsum";
    private String message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";
    private Date logDate = new Date();

    private String iconPath = "";
    private Color iconColor = Color.WHITE;
    private int iconSize = 16;
    private boolean showIcon = true;
    private boolean showDate = true;

    private Color titleBgColor = new Color(13, 110, 253);
    private Color titleTextColor = Color.WHITE;
    private Font titleFont = new Font("SansSerif", Font.BOLD, 12);

    private Font dateFont = new Font("SansSerif", Font.PLAIN, 11);
    private Color dateColor = Color.WHITE;

    private float shadowOpacity = 0.18f;
    private Color shadowColor = Color.BLACK;
    private int shadowDepth = 10;

    private int cornerRadius = 8;
    private int headerHeight = 35;
    private int padding = 10;

    private boolean messageEditable = false;

    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_RIGHT = 2;

    public static final int ALIGN_TOP = 0;
    public static final int ALIGN_MIDDLE = 1;
    public static final int ALIGN_BOTTOM = 2;

    private int horizontalAlign = ALIGN_LEFT;
    private int verticalAlign = ALIGN_TOP;

    private final JTextArea editor;
    private boolean editing = false;

    public MessageLogCardComponent() {
        setPreferredSize(new Dimension(360, 100));
        setMinimumSize(new Dimension(180, 70));
        setOpaque(false);
        setLayout(null);

        super.setBackground(Color.WHITE);
        super.setForeground(new Color(33, 37, 41));
        super.setFont(new Font("SansSerif", Font.PLAIN, 11));

        ToolTipManager.sharedInstance().registerComponent(this);

        editor = new JTextArea();
        editor.setVisible(false);
        editor.setLineWrap(true);
        editor.setWrapStyleWord(true);
        editor.setOpaque(true);
        editor.setBorder(new EmptyBorder(0, 0, 0, 0));
        add(editor);

        updateEditorStyle();

        editor.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                commitEditorText();
            }
        });

        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!editing) {
                    return;
                }

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    cancelEditorText();
                    e.consume();
                    return;
                }

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (e.isAltDown()) {
                        insertEditorLineBreak();
                    } else {
                        commitEditorText();
                    }
                    e.consume();
                }
            }
        });

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();

                if (!SwingUtilities.isLeftMouseButton(e)) {
                    return;
                }

                if (isPointInHeader(e.getPoint())) {
                    firePropertyChange("onHeaderClicked", false, true);
                    return;
                }

                if (messageEditable && isPointInBody(e.getPoint())) {
                    startEditing();
                }
            }
        };

        addMouseListener(mouseHandler);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (editing) {
                    editor.setBounds(getEditorBounds());
                }
                revalidate();
                repaint();
            }
        });
    }

    // ---------------------------------------------------------------------
    // Properties
    // ---------------------------------------------------------------------

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        String old = this.title;
        this.title = title != null ? title : "";
        firePropertyChange("title", old, this.title);
        repaint();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        String old = this.message;
        this.message = message != null ? message : "";
        firePropertyChange("message", old, this.message);

        if (editing) {
            editor.setText(this.message);
        }

        repaint();
    }

    public Date getLogDate() {
        return logDate;
    }

    public void setLogDate(Date logDate) {
        Date old = this.logDate;
        this.logDate = logDate != null ? logDate : new Date();
        firePropertyChange("logDate", old, this.logDate);
        repaint();
    }

    public String getFormattedDateText() {
        return formatLogDate(logDate);
    }

    public Color getTitleBgColor() {
        return titleBgColor;
    }

    public void setTitleBgColor(Color titleBgColor) {
        Color old = this.titleBgColor;
        this.titleBgColor = titleBgColor != null ? titleBgColor : new Color(13, 110, 253);
        firePropertyChange("titleBgColor", old, this.titleBgColor);
        repaint();
    }

    public Color getTitleTextColor() {
        return titleTextColor;
    }

    public void setTitleTextColor(Color titleTextColor) {
        Color old = this.titleTextColor;
        this.titleTextColor = titleTextColor != null ? titleTextColor : Color.WHITE;
        firePropertyChange("titleTextColor", old, this.titleTextColor);
        repaint();
    }

    public Font getTitleFont() {
        return titleFont;
    }

    public void setTitleFont(Font titleFont) {
        Font old = this.titleFont;
        this.titleFont = titleFont != null ? titleFont : new Font("SansSerif", Font.BOLD, 12);
        firePropertyChange("titleFont", old, this.titleFont);
        repaint();
    }

    public Font getDateFont() {
        return dateFont;
    }

    public void setDateFont(Font dateFont) {
        Font old = this.dateFont;
        this.dateFont = dateFont != null ? dateFont : new Font("SansSerif", Font.PLAIN, 11);
        firePropertyChange("dateFont", old, this.dateFont);
        repaint();
    }

    public Color getDateColor() {
        return dateColor;
    }

    public void setDateColor(Color dateColor) {
        Color old = this.dateColor;
        this.dateColor = dateColor != null ? dateColor : Color.WHITE;
        firePropertyChange("dateColor", old, this.dateColor);
        repaint();
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

    public Color getIconColor() {
        return iconColor;
    }

    public void setIconColor(Color iconColor) {
        Color old = this.iconColor;
        this.iconColor = iconColor != null ? iconColor : Color.WHITE;
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

    public boolean isShowIcon() {
        return showIcon;
    }

    public void setShowIcon(boolean showIcon) {
        boolean old = this.showIcon;
        this.showIcon = showIcon;
        firePropertyChange("showIcon", old, this.showIcon);
        repaint();
    }

    public boolean isShowDate() {
        return showDate;
    }

    public void setShowDate(boolean showDate) {
        boolean old = this.showDate;
        this.showDate = showDate;
        firePropertyChange("showDate", old, this.showDate);
        repaint();
    }

    public float getShadowOpacity() {
        return shadowOpacity;
    }

    public void setShadowOpacity(float shadowOpacity) {
        float old = this.shadowOpacity;
        this.shadowOpacity = Math.max(0f, Math.min(1f, shadowOpacity));
        firePropertyChange("shadowOpacity", old, this.shadowOpacity);
        repaint();
    }

    public Color getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(Color shadowColor) {
        Color old = this.shadowColor;
        this.shadowColor = shadowColor != null ? shadowColor : Color.BLACK;
        firePropertyChange("shadowColor", old, this.shadowColor);
        repaint();
    }

    public int getShadowDepth() {
        return shadowDepth;
    }

    public void setShadowDepth(int shadowDepth) {
        int old = this.shadowDepth;
        this.shadowDepth = Math.max(0, shadowDepth);
        firePropertyChange("shadowDepth", old, this.shadowDepth);
        revalidate();
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
    }

    public int getHeaderHeight() {
        return headerHeight;
    }

    public void setHeaderHeight(int headerHeight) {
        int old = this.headerHeight;
        this.headerHeight = Math.max(24, headerHeight);
        firePropertyChange("headerHeight", old, this.headerHeight);
        revalidate();
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

    public boolean isMessageEditable() {
        return messageEditable;
    }

    public void setMessageEditable(boolean messageEditable) {
        boolean old = this.messageEditable;
        this.messageEditable = messageEditable;
        firePropertyChange("messageEditable", old, this.messageEditable);

        if (!this.messageEditable && editing) {
            commitEditorText();
        }

        repaint();
    }

    public int getHorizontalAlign() {
        return horizontalAlign;
    }

    public void setHorizontalAlign(int horizontalAlign) {
        int old = this.horizontalAlign;
        this.horizontalAlign = normalizeHorizontalAlign(horizontalAlign);
        firePropertyChange("horizontalAlign", old, this.horizontalAlign);
        updateEditorStyle();
        repaint();
    }

    public int getVerticalAlign() {
        return verticalAlign;
    }

    public void setVerticalAlign(int verticalAlign) {
        int old = this.verticalAlign;
        this.verticalAlign = normalizeVerticalAlign(verticalAlign);
        firePropertyChange("verticalAlign", old, this.verticalAlign);
        repaint();
    }

    @Override
    public void setBackground(Color bg) {
        Color old = getBackground();
        super.setBackground(bg != null ? bg : Color.WHITE);
        firePropertyChange("background", old, getBackground());
        updateEditorStyle();
        repaint();
    }

    @Override
    public void setForeground(Color fg) {
        Color old = getForeground();
        super.setForeground(fg != null ? fg : new Color(33, 37, 41));
        firePropertyChange("foreground", old, getForeground());
        updateEditorStyle();
        repaint();
    }

    @Override
    public void setFont(Font font) {
        Font old = getFont();
        super.setFont(font != null ? font : new Font("SansSerif", Font.PLAIN, 11));
        firePropertyChange("font", old, getFont());
        updateEditorStyle();
        revalidate();
        repaint();
    }

    // ---------------------------------------------------------------------
    // Tooltip
    // ---------------------------------------------------------------------

    @Override
    public String getToolTipText(MouseEvent event) {
        if (event == null) {
            return null;
        }

        Point p = event.getPoint();

        if (isPointInHeader(p)) {
            if (isTitleTruncated()) {
                return emptyToNull(title);
            }
            return null;
        }

        if (isPointInBody(p)) {
            if (isMessageTruncated()) {
                return toHtmlMultiline(emptyToNull(message));
            }
        }

        return null;
    }

    // ---------------------------------------------------------------------
    // Editing
    // ---------------------------------------------------------------------

    private void startEditing() {
        if (!messageEditable || editing) {
            return;
        }

        editing = true;
        updateEditorStyle();
        editor.setText(message != null ? message : "");
        editor.setBounds(getEditorBounds());
        editor.setVisible(true);
        editor.requestFocusInWindow();
        editor.setCaretPosition(editor.getText().length());
        repaint();
    }

    private void commitEditorText() {
        if (!editing) {
            return;
        }

        editing = false;
        String newText = editor.getText();
        editor.setVisible(false);

        if (newText == null) {
            newText = "";
        }

        if (!newText.equals(message)) {
            setMessage(newText);
        } else {
            repaint();
        }
    }

    private void cancelEditorText() {
        if (!editing) {
            return;
        }

        editing = false;
        editor.setText(message != null ? message : "");
        editor.setVisible(false);
        repaint();
    }

    private void insertEditorLineBreak() {
        if (!editing) {
            return;
        }

        int pos = editor.getCaretPosition();
        try {
            editor.getDocument().insertString(pos, "\n", null);
            editor.setCaretPosition(pos + 1);
        } catch (Exception ignored) {
        }
    }

    private void updateEditorStyle() {
        editor.setFont(getFont());
        editor.setForeground(getForeground());
        editor.setBackground(getBackground());
        editor.setCaretColor(getForeground());
        editor.setSelectionColor(new Color(0, 120, 215, 80));
        editor.setSelectedTextColor(getForeground());

        // String h = normalizeHorizontalAlign(horizontalAlign);
        if (horizontalAlign == ALIGN_CENTER) {
            editor.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            editor.setAlignmentX(0.5f);
        } else if (horizontalAlign == ALIGN_RIGHT) {
            editor.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            editor.setAlignmentX(1f);
        } else {
            editor.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            editor.setAlignmentX(0f);
        }
    }

    private Rectangle getEditorBounds() {
        int depth = Math.max(0, shadowDepth);
        int x = depth + padding;
        int y = depth + headerHeight + 8;
        int w = Math.max(20, getWidth() - (depth * 2) - (padding * 2));
        int h = Math.max(20, getHeight() - (depth * 2) - headerHeight - padding - 8);
        return new Rectangle(x, y, w, h);
    }

    private boolean isPointInHeader(Point p) {
        int depth = Math.max(0, shadowDepth);
        int x = depth;
        int y = depth;
        int w = Math.max(0, getWidth() - depth * 2);
        return p.x >= x && p.x <= x + w && p.y >= y && p.y <= y + headerHeight;
    }

    private boolean isPointInBody(Point p) {
        int depth = Math.max(0, shadowDepth);
        int x = depth;
        int y = depth + headerHeight;
        int w = Math.max(0, getWidth() - depth * 2);
        int h = Math.max(0, getHeight() - depth * 2 - headerHeight);
        return p.x >= x && p.x <= x + w && p.y >= y && p.y <= y + h;
    }

    @Override
    public void doLayout() {
        super.doLayout();
        if (editing) {
            editor.setBounds(getEditorBounds());
        }
    }

    // ---------------------------------------------------------------------
    // Paint
    // ---------------------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            enableQuality(g2);

            float w = getWidth();
            float h = getHeight();

            int depth = Math.max(0, shadowDepth);
            float cardX = depth;
            float cardY = depth;
            float cardW = Math.max(0, w - (depth * 2f));
            float cardH = Math.max(0, h - (depth * 2f));

            Shape cardShape = new RoundRectangle2D.Float(
                    cardX, cardY, cardW, cardH, cornerRadius, cornerRadius
            );

            paintShadow(g2, cardX, cardY, cardW, cardH);

            Shape oldClip = g2.getClip();
            g2.setClip(cardShape);

            g2.setColor(titleBgColor);
            g2.fill(new Rectangle2D.Float(cardX, cardY, cardW, headerHeight));

            g2.setColor(getBackground());
            g2.fill(new Rectangle2D.Float(cardX, cardY + headerHeight, cardW, Math.max(0, cardH - headerHeight)));

            g2.setClip(oldClip);

            g2.setColor(new Color(0, 0, 0, 20));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(cardShape);

            g2.setColor(new Color(0, 0, 0, 20));
            g2.draw(new Line2D.Float(cardX, cardY + headerHeight, cardX + cardW, cardY + headerHeight));

            paintHeader(g2, cardX, cardY, cardW);

            if (!editing) {
                paintBody(g2, cardX, cardY, cardW, cardH);
            }

        } finally {
            g2.dispose();
        }
    }

    private void paintShadow(Graphics2D g2, float cardX, float cardY, float cardW, float cardH) {
        int depth = Math.max(0, shadowDepth);
        if (depth <= 0 || shadowOpacity <= 0f) {
            return;
        }

        Color base = shadowColor != null ? shadowColor : Color.BLACK;
        int maxAlpha = Math.max(0, Math.min(255, Math.round(shadowOpacity * 255f)));

        for (int i = depth; i > 0; i--) {
            float progress = 1f - ((float) i / (float) depth);
            int alpha = Math.max(0, Math.min(255, Math.round(maxAlpha * progress * progress)));

            g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha));
            g2.setStroke(new BasicStroke(1.5f));

            float sx = cardX - i;
            float sy = cardY - i;
            float sw = cardW + (i * 2f);
            float sh = cardH + (i * 2f);
            float sr = cornerRadius + i;

            g2.draw(new RoundRectangle2D.Float(sx, sy, sw, sh, sr, sr));
        }
    }

    private void paintHeader(Graphics2D g2, float cardX, float cardY, float cardW) {
        float leftX = cardX + padding;
        float rightX = cardX + cardW - padding;

        float currentX = leftX;

        if (showIcon) {
            float iconY = cardY + (headerHeight - iconSize) / 2f;
            paintHeaderIcon(g2, Math.round(currentX), Math.round(iconY), iconSize, iconColor);
            currentX += iconSize + 6f;
        }

        String dateText = showDate ? getFormattedDateText() : "";
        float dateReserve = 0f;

        if (showDate && dateText != null && !dateText.isEmpty()) {
            g2.setFont(dateFont != null ? dateFont : new Font("SansSerif", Font.PLAIN, 11));
            FontMetrics fmDate = g2.getFontMetrics();
            dateReserve = fmDate.stringWidth(dateText) + 10f;
        }

        int titleMaxWidth = Math.max(20, Math.round(rightX - currentX - dateReserve));

        g2.setFont(titleFont != null ? titleFont : new Font("SansSerif", Font.BOLD, 12));
        g2.setColor(titleTextColor != null ? titleTextColor : Color.WHITE);

        FontMetrics fmTitle = g2.getFontMetrics();
        float titleBaseline = cardY + (headerHeight - fmTitle.getHeight()) / 2f + fmTitle.getAscent();
        String displayTitle = ellipsize(fmTitle, title != null ? title : "", titleMaxWidth);
        g2.drawString(displayTitle, currentX, titleBaseline);

        if (showDate && dateText != null && !dateText.isEmpty()) {
            g2.setFont(dateFont != null ? dateFont : new Font("SansSerif", Font.PLAIN, 11));
            g2.setColor(dateColor != null ? dateColor : Color.WHITE);
            FontMetrics fmDate = g2.getFontMetrics();

            String fittedDate = trimTextLeftToWidth(g2, dateText, Math.max(40, Math.round(cardW * 0.45f)));
            int fittedDateWidth = fmDate.stringWidth(fittedDate);
            float dateX = rightX - fittedDateWidth;
            float dateBaseline = cardY + (headerHeight - fmDate.getHeight()) / 2f + fmDate.getAscent();
            g2.drawString(fittedDate, dateX, dateBaseline);
        }
    }

    private void paintBody(Graphics2D g2, float cardX, float cardY, float cardW, float cardH) {
        g2.setFont(getFont());
        g2.setColor(getForeground());

        FontMetrics fm = g2.getFontMetrics();

        int areaX = Math.round(cardX + padding);
        int areaY = Math.round(cardY + headerHeight + 8);
        int areaW = Math.max(20, Math.round(cardW - (padding * 2f)));
        int areaH = Math.max(10, Math.round(cardH - headerHeight - padding - 8));

        WrappedTextLayout layout = buildWrappedLayout(message != null ? message : "", fm, areaW, areaH);

        int textBlockHeight = layout.lines.size() * fm.getHeight();
        int drawY;

        switch (verticalAlign) {
            case ALIGN_MIDDLE:
                drawY = areaY + Math.max(0, (areaH - textBlockHeight) / 2) + fm.getAscent();
                break;
            case ALIGN_BOTTOM:
                drawY = areaY + Math.max(0, areaH - textBlockHeight) + fm.getAscent();
                break;
            default:
                drawY = areaY + fm.getAscent();
                break;
        }

        for (String line : layout.lines) {
            int lineWidth = fm.stringWidth(line);
            int drawX;

            switch (horizontalAlign) {
                case ALIGN_CENTER:
                    drawX = areaX + Math.max(0, (areaW - lineWidth) / 2);
                    break;
                case ALIGN_RIGHT:
                    drawX = areaX + Math.max(0, areaW - lineWidth);
                    break;
                default:
                    drawX = areaX;
                    break;
            }

            g2.drawString(line, drawX, drawY);
            drawY += fm.getHeight();
        }
    }

    // ---------------------------------------------------------------------
    // Layout helpers
    // ---------------------------------------------------------------------

    private WrappedTextLayout buildWrappedLayout(String text, FontMetrics fm, int maxWidth, int maxHeight) {
        WrappedTextLayout result = new WrappedTextLayout();

        if (text == null || text.isEmpty()) {
            return result;
        }

        String[] rawParagraphs = text.split("\\r?\\n", -1);
        List<String> allLines = new ArrayList<>();

        for (int p = 0; p < rawParagraphs.length; p++) {
            String paragraph = rawParagraphs[p];

            if (paragraph.isEmpty()) {
                allLines.add("");
                continue;
            }

            String[] words = paragraph.split("\\s+");
            StringBuilder line = new StringBuilder();

            for (String word : words) {
                String test = line.length() == 0 ? word : line + " " + word;

                if (fm.stringWidth(test) <= maxWidth) {
                    line.setLength(0);
                    line.append(test);
                } else {
                    if (line.length() > 0) {
                        allLines.add(line.toString());
                        line.setLength(0);
                        line.append(word);
                    } else {
                        allLines.add(ellipsize(fm, word, maxWidth));
                    }
                }
            }

            if (line.length() > 0) {
                allLines.add(line.toString());
            }

            if (p < rawParagraphs.length - 1) {
                allLines.add("");
            }
        }

        int maxLines = Math.max(1, maxHeight / Math.max(1, fm.getHeight()));

        if (allLines.size() <= maxLines) {
            result.lines.addAll(allLines);
            result.truncated = false;
            return result;
        }

        for (int i = 0; i < maxLines; i++) {
            if (i == maxLines - 1) {
                result.lines.add(ellipsize(fm, allLines.get(i), maxWidth));
            } else {
                result.lines.add(allLines.get(i));
            }
        }

        if (!result.lines.isEmpty()) {
            String last = result.lines.get(result.lines.size() - 1);
            if (!last.endsWith("...")) {
                result.lines.set(result.lines.size() - 1, ellipsize(fm, last + " ...", maxWidth));
            }
        }

        result.truncated = true;
        return result;
    }

    private boolean isMessageTruncated() {
        FontMetrics fm = getFontMetrics(getFont());
        int depth = Math.max(0, shadowDepth);
        int areaW = Math.max(20, getWidth() - (depth * 2) - (padding * 2));
        int areaH = Math.max(10, getHeight() - (depth * 2) - headerHeight - padding - 8);
        return buildWrappedLayout(message != null ? message : "", fm, areaW, areaH).truncated;
    }

    private boolean isTitleTruncated() {
        FontMetrics fmTitle = getFontMetrics(titleFont != null ? titleFont : new Font("SansSerif", Font.BOLD, 12));
        FontMetrics fmDate = getFontMetrics(dateFont != null ? dateFont : new Font("SansSerif", Font.PLAIN, 11));

        int depth = Math.max(0, shadowDepth);
        int cardW = Math.max(0, getWidth() - depth * 2);

        int currentX = padding;
        if (showIcon) {
            currentX += iconSize + 6;
        }

        int reserve = 0;
        if (showDate) {
            reserve = fmDate.stringWidth(getFormattedDateText()) + 10;
        }

        int titleMaxWidth = Math.max(20, cardW - padding - currentX - reserve);
        String safeTitle = title != null ? title : "";
        return !safeTitle.equals(ellipsize(fmTitle, safeTitle, titleMaxWidth));
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private void paintHeaderIcon(Graphics2D g2, int x, int y, int size, Color tint) {
        Image img = loadIcon();
        if (img != null) {
            BufferedImage tinted = tintImage(img, tint != null ? tint : Color.WHITE, size, size);
            g2.drawImage(tinted, x, y, null);
            return;
        }

        paintDefaultUserIcon(g2, x, y, size, tint != null ? tint : Color.WHITE);
    }

    private void paintDefaultUserIcon(Graphics2D g2, int x, int y, int size, Color color) {
        float cx = x + (size / 2f);

        float headR = size * 0.22f;
        Shape head = new Ellipse2D.Float(cx - headR, y + size * 0.08f, headR * 2f, headR * 2f);

        float shoulderW = size * 0.75f;
        float shoulderH = size * 0.42f;
        float shoulderX = cx - (shoulderW / 2f);
        float shoulderY = y + size - shoulderH;
        Shape shoulders = new Arc2D.Float(shoulderX, shoulderY, shoulderW, shoulderH, 0, 180, Arc2D.CHORD);

        g2.setColor(color);
        g2.fill(head);
        g2.fill(shoulders);
    }

    private Image loadIcon() {
        if (iconPath == null || iconPath.trim().isEmpty()) {
            return null;
        }

        try {
            return ImageLoader.getInstance().loadImage(iconPath.trim());
        } catch (Exception ignored) {
            return null;
        }
    }

    private BufferedImage tintImage(Image src, Color tint, int w, int h) {
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        try {
            enableQuality(g2);
            g2.drawImage(src, 0, 0, w, h, null);
            g2.setComposite(AlphaComposite.SrcAtop);
            g2.setColor(tint);
            g2.fillRect(0, 0, w, h);
        } finally {
            g2.dispose();
        }
        return out;
    }

    private String formatLogDate(Date value) {
        if (value == null) {
            return "";
        }

        Instant now = Instant.now();
        Instant then = value.toInstant();

        if (then.isAfter(now)) {
            then = now;
        }

        Duration duration = Duration.between(then, now);
        long seconds = Math.max(0, duration.getSeconds());
        long minutes = seconds / 60;
        long hours = minutes / 60;

        ZoneId zone = ZoneId.systemDefault();
        LocalDate eventDate = then.atZone(zone).toLocalDate();
        LocalDate today = now.atZone(zone).toLocalDate();

        if (seconds < 60) {
            return "Just now";
        }
        if (minutes < 60) {
            return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
        }
        if (hours < 24 && eventDate.equals(today)) {
            return "Today at " + lowerAmPm(TODAY_TIME_FORMAT.format(then.atZone(zone)));
        }

        return lowerAmPm(FULL_DATE_FORMAT.format(then.atZone(zone)));
    }

    private String lowerAmPm(String text) {
        if (text == null) {
            return "";
        }
        return text.replace(" AM", " am").replace(" PM", " pm");
    }

    private String ellipsize(FontMetrics fm, String text, int maxWidth) {
        if (text == null) {
            return "";
        }
        if (fm.stringWidth(text) <= maxWidth) {
            return text;
        }

        String ellipsis = "...";
        int ellipsisW = fm.stringWidth(ellipsis);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            String next = sb.toString() + text.charAt(i);
            if (fm.stringWidth(next) + ellipsisW > maxWidth) {
                break;
            }
            sb.append(text.charAt(i));
        }
        return sb + ellipsis;
    }

    private String trimTextLeftToWidth(Graphics2D g2, String text, int maxWidth) {
        if (text == null) {
            return "";
        }
        FontMetrics fm = g2.getFontMetrics();
        if (fm.stringWidth(text) <= maxWidth) {
            return text;
        }

        String ellipsis = "...";
        int ellipsisW = fm.stringWidth(ellipsis);
        StringBuilder tail = new StringBuilder();

        for (int i = text.length() - 1; i >= 0; i--) {
            String next = text.charAt(i) + tail.toString();
            if (fm.stringWidth(next) + ellipsisW > maxWidth) {
                break;
            }
            tail.insert(0, text.charAt(i));
        }

        return ellipsis + tail;
    }

    private int normalizeHorizontalAlign(int value) {
        if (value == ALIGN_CENTER || value == ALIGN_RIGHT) {
            return value;
        }
        return ALIGN_LEFT;
    }

    private int normalizeVerticalAlign(int value) {
        if (value == ALIGN_MIDDLE || value == ALIGN_BOTTOM) {
            return value;
        }
        return ALIGN_TOP;
    }

    private String emptyToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value;
    }

    private String toHtmlMultiline(String text) {
        if (text == null) {
            return null;
        }
        String escaped = text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br>");
        return "<html>" + escaped + "</html>";
    }

    private void enableQuality(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    private static class WrappedTextLayout {
        private final List<String> lines = new ArrayList<>();
        private boolean truncated = false;
    }
}