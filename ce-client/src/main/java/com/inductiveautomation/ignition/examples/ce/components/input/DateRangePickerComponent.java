package com.inductiveautomation.ignition.examples.ce.components.input;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateRangePickerComponent extends JComponent implements MouseListener {

    private static final DateTimeFormatter BUTTON_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    private static final DateTimeFormatter FOOTER_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    private static final DateTimeFormatter MONTH_FORMAT =
            DateTimeFormatter.ofPattern("MMM yyyy");

    private static final DateTimeFormatter TIME_LABEL_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm");

    private static final String[] WEEKDAY_LABELS = {"W", "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};

    private Date startDate = dateFrom(LocalDate.now().atStartOfDay());
    private Date endDate = dateFrom(LocalDate.now().atTime(23, 59));

    private Color primaryColor = new Color(13, 110, 253);
    private Color rangeColor = new Color(230, 242, 255);
    private Color todayColor = new Color(240, 182, 7);
    private Color backgroundColor = Color.WHITE;
    private Color lineColor = new Color(213, 213, 213);
    private Color headerColor = new Color(255, 255, 255);
    private Color headerTextColor = new Color(85, 85, 85);

    private boolean dark = false;
    private boolean isOpen = false;

    private final JPopupMenu popupMenu;
    private final PopupPanel popupPanel;
    private long lastToggleTime = 0L;

    public DateRangePickerComponent() {
        setOpaque(false);
        setFocusable(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(new Font("Dialog", Font.BOLD, 13));
        setForeground(new Color(70, 70, 70));
        setBackground(new Color(245, 245, 245));
        setPreferredSize(new Dimension(335, 45));
        setMinimumSize(new Dimension(180, 28));

        popupMenu = new JPopupMenu();
        popupMenu.setOpaque(false);
        popupMenu.setBorder(BorderFactory.createEmptyBorder());
        popupMenu.setLayout(new BorderLayout());

        popupPanel = new PopupPanel();
        popupMenu.add(popupPanel, BorderLayout.CENTER);

        addMouseListener(this);
    }

    // ---------------------------------------------------------------------
    // Properties
    // ---------------------------------------------------------------------

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        Date old = this.startDate;
        this.startDate = startDate;
        firePropertyChange("startDate", old, this.startDate);
        repaint();
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        Date old = this.endDate;
        this.endDate = endDate;
        firePropertyChange("endDate", old, this.endDate);
        repaint();
    }

    public Color getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(Color primaryColor) {
        Color old = this.primaryColor;
        this.primaryColor = primaryColor != null ? primaryColor : new Color(13, 110, 253);
        firePropertyChange("primaryColor", old, this.primaryColor);
        repaint();
    }

    public Color getRangeColor() {
        return rangeColor;
    }

    public void setRangeColor(Color rangeColor) {
        Color old = this.rangeColor;
        this.rangeColor = rangeColor != null ? rangeColor : new Color(230, 242, 255);
        firePropertyChange("rangeColor", old, this.rangeColor);
        repaint();
    }

    public Color getTodayColor() {
        return todayColor;
    }

    public void setTodayColor(Color todayColor) {
        Color old = this.todayColor;
        this.todayColor = todayColor != null ? todayColor : new Color(240, 182, 7);
        firePropertyChange("todayColor", old, this.todayColor);
        repaint();
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        Color old = this.backgroundColor;
        this.backgroundColor = backgroundColor != null ? backgroundColor : Color.WHITE;
        firePropertyChange("backgroundColor", old, this.backgroundColor);
        repaint();
    }

    public Color getLineColor() {
        return lineColor;
    }

    public void setLineColor(Color lineColor) {
        Color old = this.lineColor;
        this.lineColor = lineColor != null ? lineColor : new Color(213, 213, 213);
        firePropertyChange("lineColor", old, this.lineColor);
        repaint();
    }

    public Color getHeaderColor() {
        return headerColor;
    }

    public void setHeaderColor(Color headerColor) {
        Color old = this.headerColor;
        this.headerColor = headerColor != null ? headerColor : new Color(230, 231, 233);
        firePropertyChange("headerColor", old, this.headerColor);
        repaint();
    }

    public Color getHeaderTextColor() {
        return headerTextColor;
    }

    public void setHeaderTextColor(Color headerTextColor) {
        Color old = this.headerTextColor;
        this.headerTextColor = headerTextColor != null ? headerTextColor : new Color(85, 85, 85);
        firePropertyChange("headerTextColor", old, this.headerTextColor);
        repaint();
    }

    public boolean isDark() {
        return dark;
    }

    public void setDark(boolean dark) {
        boolean old = this.dark;
        this.dark = dark;
        firePropertyChange("dark", old, this.dark);
        repaint();
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        boolean old = this.isOpen;
        this.isOpen = open;
        firePropertyChange("isOpen", old, this.isOpen);
        repaint();
    }

    // ---------------------------------------------------------------------
    // Paint button
    // ---------------------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            enableQuality(g2);

            int w = getWidth();
            int h = getHeight();

            Color btnBg = dark ? mix(getHeaderColor(), new Color(50, 52, 57), 0.70f) : getHeaderColor();
            Color btnText = dark ? new Color(225, 228, 232) : getHeaderTextColor();
            Color btnBorder = dark ? new Color(72, 76, 84) : new Color(210, 212, 216);

            g2.setColor(btnBg);
            g2.fillRoundRect(0, 0, w - 1, h - 1, 8, 8);

            g2.setColor(btnBorder);
            g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);

            String text = formatRangeText(startDate, endDate);
            FontMetrics fm = g2.getFontMetrics(getFont());
            int textY = (h - fm.getHeight()) / 2 + fm.getAscent();

            g2.setFont(getFont());
            g2.setColor(btnText);
            g2.drawString(text, 12, textY);

            paintChevronDown(g2, w - 14, h / 2, 8, btnText);
        } finally {
            g2.dispose();
        }
    }

    private String formatRangeText(Date start, Date end) {
        if (start == null || end == null) {
            return "";
        }
        return BUTTON_FORMAT.format(toLocalDateTime(start)) + " | " + BUTTON_FORMAT.format(toLocalDateTime(end));
    }

    private void openPopup() {
        popupPanel.beginSession();

        Dimension popupSize = popupPanel.getPreferredSize();
        int popupW = popupSize.width;
        int popupH = popupSize.height;

        int x = 0;
        int y = getHeight() + 2;

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

        popupMenu.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) { }

            @Override
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {
                setOpen(false);
                lastToggleTime = System.currentTimeMillis();
                popupMenu.removePopupMenuListener(this);
            }

            @Override
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {
                setOpen(false);
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

    @Override
    public void mousePressed(MouseEvent e) {
        if (!isEnabled()) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastToggleTime < 150) {
            return;
        }

        if (popupMenu.isVisible()) {
            popupMenu.setVisible(false);
            lastToggleTime = now;
        } else {
            openPopup();
            lastToggleTime = now;
        }
    }

    @Override public void mouseClicked(MouseEvent e) { }
    @Override public void mouseReleased(MouseEvent e) { }
    @Override public void mouseEntered(MouseEvent e) { }
    @Override public void mouseExited(MouseEvent e) { }

    // ---------------------------------------------------------------------
    // Popup panel
    // ---------------------------------------------------------------------

    private class PopupPanel extends JPanel implements MouseListener, MouseMotionListener {

        private final int popupW = 728;
        private final int popupH = 398;

        private final int panelRadius = 8;

        private final int presetPanelW = 120;
        private final int footerH = 52;
        private final int sliderAreaH = 86;
        private final int monthHeaderH = 28;
        private final int weekHeaderH = 22;
        private final int cellH = 34;
        private final int weekColW = 30;
        private final int monthGap = 14;

        private LocalDateTime workingStart;
        private LocalDateTime workingEnd;
        private YearMonth leftMonth;

        private LocalDateTime committedStart;
        private LocalDateTime committedEnd;
        private YearMonth committedLeftMonth;

        private LocalDate hoverLocalDate;

        private Rectangle applyRect = new Rectangle();
        private Rectangle cancelRect = new Rectangle();
        private boolean hoverApply = false;
        private boolean hoverCancel = false;

        private Rectangle startSliderRect = new Rectangle();
        private Rectangle endSliderRect = new Rectangle();
        private boolean draggingStartSlider = false;
        private boolean draggingEndSlider = false;

        private int hoverPreset = -1;
        private int hoverWeekLeft = -1;
        private int hoverWeekRight = -1;

        private Rectangle leftMonthRect = new Rectangle();
        private Rectangle rightMonthRect = new Rectangle();
        private Rectangle leftPrevRect = new Rectangle();
        private Rectangle rightNextRect = new Rectangle();

        private final List<Rectangle> presetRects = new ArrayList<>();

        private LocalDate firstClickDate = null;

        private final String[] presets = {
                "Today",
                "Yesterday",
                "Last 7 Days",
                "Last 15 Days",
                "Last 30 Days",
                "Last 6 Months",
                "Last 1 Year"
        };

        PopupPanel() {
            setOpaque(false);
            setBorder(null);
            setPreferredSize(new Dimension(popupW, popupH));
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        void beginSession() {
            workingStart = toLocalDateTime(startDate != null ? startDate : new Date());
            workingEnd = toLocalDateTime(endDate != null ? endDate : new Date());

            if (workingEnd.isBefore(workingStart)) {
                LocalDateTime tmp = workingStart;
                workingStart = workingEnd;
                workingEnd = tmp;
            }

            committedStart = workingStart;
            committedEnd = workingEnd;

            leftMonth = YearMonth.from(workingStart.toLocalDate());
            committedLeftMonth = leftMonth;

            hoverLocalDate = null;
            firstClickDate = null;
            hoverPreset = -1;
            hoverWeekLeft = -1;
            hoverWeekRight = -1;
            hoverApply = false;
            hoverCancel = false;
            draggingStartSlider = false;
            draggingEndSlider = false;
            setCursor(Cursor.getDefaultCursor());

            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            try {
                enableQuality(g2);

                Theme t = theme();

                int panelX = 0;
                int panelY = 0;
                int panelW = getWidth() - 1;
                int panelH = getHeight() - 1;

                g2.setColor(t.panelBg);
                g2.fillRoundRect(panelX, panelY, panelW, panelH, panelRadius, panelRadius);

                g2.setColor(t.panelBorder);
                g2.drawRoundRect(panelX, panelY, panelW, panelH, panelRadius, panelRadius);

                int contentBottom = panelY + panelH - footerH;

                g2.setColor(t.divider);
                g2.drawLine(panelX + presetPanelW, panelY + 16, panelX + presetPanelW, contentBottom - 1);
                g2.drawLine(panelX, contentBottom, panelX + panelW, contentBottom);

                paintPresets(g2, panelX + 10, panelY + 18, presetPanelW - 20, contentBottom - panelY - 24, t);
                paintMonths(g2, panelX + presetPanelW + 12, panelY + 12, panelW - presetPanelW - 24, contentBottom - panelY - sliderAreaH - 18, t);
                paintSliders(g2, panelX + presetPanelW + 18, contentBottom - sliderAreaH + 10, panelW - presetPanelW - 36, sliderAreaH - 10, t);
                paintFooter(g2, panelX, panelY + panelH - footerH, panelW, footerH, t);
            } finally {
                g2.dispose();
            }
        }

        private void paintPresets(Graphics2D g2, int x, int y, int w, int h, Theme t) {
            presetRects.clear();

            Font normal = getFont().deriveFont(Font.PLAIN, 11f);
            Font hoverFont = normal.deriveFont(Font.BOLD);

            int rowH = 34;
            for (int i = 0; i < presets.length; i++) {
                int ry = y + i * rowH;
                Rectangle r = new Rectangle(x, ry, w, 26);
                presetRects.add(r);

                boolean active = isPresetActive(i);
                boolean hover = (i == hoverPreset);

                if (active) {
                    g2.setColor(withAlpha(primaryColor, dark ? 42 : 24));
                    g2.fillRoundRect(r.x, r.y, r.width, r.height, 4, 4);
                } else if (hover) {
                    g2.setColor(t.hoverSoft);
                    g2.fillRoundRect(r.x, r.y, r.width, r.height, 4, 4);
                }

                g2.setFont((hover || active) ? hoverFont : normal);
                g2.setColor(active ? primaryColor : t.text);
                FontMetrics fm = g2.getFontMetrics();
                int ty = r.y + (r.height - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(presets[i], r.x + 8, ty);
            }
        }

        private void paintMonths(Graphics2D g2, int x, int y, int w, int h, Theme t) {
            int monthW = (w - monthGap) / 2;
            leftMonthRect.setBounds(x, y, monthW, h);
            rightMonthRect.setBounds(x + monthW + monthGap, y, monthW, h);

            YearMonth rightMonth = leftMonth.plusMonths(1);

            paintSingleMonth(g2, leftMonthRect, leftMonth, true, t);
            paintSingleMonth(g2, rightMonthRect, rightMonth, false, t);

            g2.setColor(t.divider);
            int dividerX = leftMonthRect.x + leftMonthRect.width + monthGap / 2;
            g2.drawLine(dividerX, y + 10, dividerX, y + h - 10);
        }

        private void paintSingleMonth(Graphics2D g2, Rectangle area, YearMonth month, boolean left, Theme t) {
            Font titleFont = getFont().deriveFont(Font.BOLD, 13f);
            Font weekFont = getFont().deriveFont(Font.BOLD, 12f);
            Font dayFont = getFont().deriveFont(Font.PLAIN, 12f);

            int gridX = area.x;
            int gridY = area.y;
            int dayColW = (area.width - weekColW) / 7;

            Rectangle navRect = new Rectangle();
            if (left) {
                navRect.setBounds(gridX + 2, gridY + 3, 22, 22);
                leftPrevRect.setBounds(navRect);
                paintChevronLeft(g2, navRect.x + navRect.width / 2, navRect.y + navRect.height / 2, 10, t.textMutedStrong);
            } else {
                navRect.setBounds(gridX + area.width - 24, gridY + 3, 22, 22);
                rightNextRect.setBounds(navRect);
                paintChevronRight(g2, navRect.x + navRect.width / 2, navRect.y + navRect.height / 2, 10, t.textMutedStrong);
            }

            g2.setFont(titleFont);
            g2.setColor(t.textStrong);
            String title = MONTH_FORMAT.format(month.atDay(1));
            FontMetrics tfm = g2.getFontMetrics();
            int tx = gridX + (area.width - tfm.stringWidth(title)) / 2;
            int ty = gridY + 18;
            g2.drawString(title, tx, ty);

            int weekHeaderY = gridY + monthHeaderH;
            g2.setFont(weekFont);
            g2.setColor(t.textMuted);
            for (int i = 0; i < WEEKDAY_LABELS.length; i++) {
                int cx;
                if (i == 0) {
                    cx = gridX + weekColW / 2;
                } else {
                    cx = gridX + weekColW + (i - 1) * dayColW + dayColW / 2;
                }
                drawCenteredString(g2, WEEKDAY_LABELS[i], cx, weekHeaderY + 14);
            }

            List<LocalDate> weekStarts = getVisibleWeekStarts(month);
            LocalDate today = LocalDate.now();

            int startY = weekHeaderY + weekHeaderH;
            g2.setFont(dayFont);

            for (int visibleRow = 0; visibleRow < weekStarts.size(); visibleRow++) {
                LocalDate weekStart = weekStarts.get(visibleRow);
                int rowY = startY + visibleRow * cellH;

                int weekNum = weekStart.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                Rectangle weekRect = new Rectangle(gridX, rowY, weekColW, cellH);
                boolean hoverWeek = left ? (hoverWeekLeft == visibleRow) : (hoverWeekRight == visibleRow);

                g2.setColor(hoverWeek ? primaryColor : t.weekNumber);
                Font weekNumberFont = hoverWeek ? dayFont.deriveFont(Font.BOLD) : dayFont;
                g2.setFont(weekNumberFont);
                drawCenteredString(g2, String.valueOf(weekNum), weekRect.x + weekRect.width / 2, weekRect.y + 22);

                if (hoverWeek) {
                    FontMetrics ufm = g2.getFontMetrics();
                    String txt = String.valueOf(weekNum);
                    int sw = ufm.stringWidth(txt);
                    int ux = weekRect.x + (weekRect.width - sw) / 2;
                    int uy = weekRect.y + 25;
                    g2.drawLine(ux, uy, ux + sw, uy);
                }

                for (int col = 0; col < 7; col++) {
                    LocalDate cellDate = weekStart.plusDays(col);
                    int cx = gridX + weekColW + col * dayColW;
                    Rectangle cell = new Rectangle(cx, rowY, dayColW, cellH);

                    boolean inThisMonth = YearMonth.from(cellDate).equals(month);
                    boolean isToday = cellDate.equals(today);
                    boolean isHover = hoverLocalDate != null && cellDate.equals(hoverLocalDate) && inThisMonth;
                    boolean isStart = cellDate.equals(workingStart.toLocalDate());
                    boolean isEnd = cellDate.equals(workingEnd.toLocalDate());

                    boolean isStartPaint = isStart && inThisMonth;
                    boolean isEndPaint = isEnd && inThisMonth;

                    boolean inRangeLogical = !cellDate.isBefore(workingStart.toLocalDate()) && !cellDate.isAfter(workingEnd.toLocalDate());
                    boolean inRangePaint = inRangeLogical && inThisMonth && !isStartPaint && !isEndPaint;

                    if (inRangePaint) {
                        int fillY = cell.y + 4;
                        int fillH = cell.height - 8;
                        g2.setColor(dark ? mix(rangeColor, new Color(255, 255, 255), 0.18f) : rangeColor);

                        int fillX = cell.x;
                        int fillW = cell.width;

                        if (col > 0) {
                            fillX -= 1;
                            fillW += 1;
                        }
                        if (col < 6) {
                            fillW += 1;
                        }

                        g2.fillRect(fillX, fillY, fillW, fillH);
                    }

                    if (isStartPaint) {
                        g2.setColor(primaryColor);
                        g2.fillRoundRect(cell.x + 2, cell.y + 4, cell.width - 4, cell.height - 8, 4, 4);
                    }

                    if (isEndPaint) {
                        g2.setColor(primaryColor);
                        g2.fillRoundRect(cell.x + 2, cell.y + 4, cell.width - 4, cell.height - 8, 4, 4);
                    }

                    if (isToday && !isStartPaint && !isEndPaint && inThisMonth) {
                        int d = Math.min(cell.width - 10, cell.height - 10);
                        int ox = cell.x + (cell.width - d) / 2;
                        int oy = cell.y + (cell.height - d) / 2;
                        g2.setColor(todayColor);
                        g2.fillOval(ox, oy, d, d);
                    }

                    if (isHover && !isStartPaint && !isEndPaint && !isToday) {
                        int d = Math.min(cell.width - 10, cell.height - 10);
                        int ox = cell.x + (cell.width - d) / 2;
                        int oy = cell.y + (cell.height - d) / 2;
                        g2.setColor(withAlpha(primaryColor, dark ? 58 : 40));
                        g2.fillOval(ox, oy, d, d);
                    }

                    if (isStartPaint || isEndPaint) {
                        g2.setColor(Color.WHITE);
                    } else if (inRangePaint) {
                        g2.setColor(dark ? new Color(45, 48, 54) : t.text);
                    } else if (!inThisMonth) {
                        g2.setColor(t.overflowText);
                    } else {
                        g2.setColor(t.text);
                    }

                    drawCenteredString(g2, String.valueOf(cellDate.getDayOfMonth()), cell.x + cell.width / 2, cell.y + 22);
                }
            }
        }

        private void paintSliders(Graphics2D g2, int x, int y, int w, int h, Theme t) {
            int sectionGap = 36;
            int sectionW = (w - sectionGap) / 2;

            int startX = x;
            int endX = x + sectionW + sectionGap;
            int sliderY = y + 36;

            g2.setFont(getFont().deriveFont(Font.BOLD, 13f));
            g2.setColor(t.textStrong);
            g2.drawString("Start Time: " + TIME_LABEL_FORMAT.format(workingStart.toLocalTime()), startX, y + 12);
            g2.drawString("End Time: " + TIME_LABEL_FORMAT.format(workingEnd.toLocalTime()), endX, y + 12);

            startSliderRect.setBounds(startX, sliderY, sectionW - 10, 18);
            endSliderRect.setBounds(endX, sliderY, sectionW - 10, 18);

            paintSlider(g2, startSliderRect, workingStart.getHour() * 60 + workingStart.getMinute(), t);
            paintSlider(g2, endSliderRect, workingEnd.getHour() * 60 + workingEnd.getMinute(), t);
        }

        private void paintSlider(Graphics2D g2, Rectangle r, int minutes, Theme t) {
            int lineY = r.y + r.height / 2;
            int lineX1 = r.x;
            int lineX2 = r.x + r.width;

            g2.setColor(t.sliderTrack);
            g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(lineX1, lineY, lineX2, lineY);

            int knobX = lineX1 + (int) Math.round((minutes / 1439.0) * r.width);
            knobX = clamp(knobX, lineX1, lineX2);

            g2.setColor(primaryColor);
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(lineX1, lineY, knobX, lineY);

            g2.setColor(t.sliderKnobOuter);
            g2.fillOval(knobX - 7, lineY - 7, 14, 14);
            g2.setColor(t.sliderKnobBorder);
            g2.setStroke(new BasicStroke(1f));
            g2.drawOval(knobX - 7, lineY - 7, 14, 14);

            g2.setColor(primaryColor);
            g2.fillOval(knobX - 3, lineY - 3, 6, 6);
        }

        private void paintFooter(Graphics2D g2, int x, int y, int w, int h, Theme t) {
            String footerText = "Start: " + FOOTER_FORMAT.format(workingStart) + " | End: " + FOOTER_FORMAT.format(workingEnd);
            g2.setFont(getFont().deriveFont(Font.PLAIN, 13f));
            g2.setColor(t.text);
            g2.drawString(footerText, x + 26, y + 31);

            int btnW = 68;
            int btnH = 30;
            int gap = 12;

            applyRect.setBounds(x + w - btnW - 16, y + 10, btnW, btnH);
            cancelRect.setBounds(applyRect.x - btnW - gap, y + 10, btnW, btnH);

            g2.setFont(getFont().deriveFont(Font.BOLD, 13f));

            Color applyBg = hoverApply ? mix(primaryColor, Color.WHITE, 0.10f) : primaryColor;
            Color applyBorder = hoverApply ? mix(primaryColor, Color.BLACK, 0.10f) : primaryColor;

            g2.setColor(applyBg);
            g2.fillRoundRect(applyRect.x, applyRect.y, applyRect.width, applyRect.height, 4, 4);
            g2.setColor(applyBorder);
            g2.drawRoundRect(applyRect.x, applyRect.y, applyRect.width, applyRect.height, 4, 4);
            g2.setColor(Color.WHITE);
            drawCenteredString(g2, "Apply", applyRect.x + applyRect.width / 2, applyRect.y + 20);

            if (hoverCancel) {
                g2.setColor(t.cancelHoverBg);
                g2.fillRoundRect(cancelRect.x, cancelRect.y, cancelRect.width, cancelRect.height, 4, 4);
            }
            g2.setColor(t.cancelText);
            drawCenteredString(g2, "Cancel", cancelRect.x + cancelRect.width / 2, cancelRect.y + 20);
        }

        private boolean isPresetActive(int index) {
            LocalDateTime[] range = getPresetRange(index);
            return range[0].equals(workingStart) && range[1].equals(workingEnd);
        }

        private LocalDateTime[] getPresetRange(int index) {
            LocalDate today = LocalDate.now();

            switch (index) {
                case 0:
                    return new LocalDateTime[]{today.atStartOfDay(), today.atTime(23, 59)};
                case 1: {
                    LocalDate d = today.minusDays(1);
                    return new LocalDateTime[]{d.atStartOfDay(), d.atTime(23, 59)};
                }
                case 2:
                    return new LocalDateTime[]{today.minusDays(6).atStartOfDay(), today.atTime(23, 59)};
                case 3:
                    return new LocalDateTime[]{today.minusDays(14).atStartOfDay(), today.atTime(23, 59)};
                case 4:
                    return new LocalDateTime[]{today.minusDays(29).atStartOfDay(), today.atTime(23, 59)};
                case 5:
                    return new LocalDateTime[]{today.minusMonths(6).atStartOfDay(), today.atTime(23, 59)};
                default:
                    return new LocalDateTime[]{today.minusYears(1).atStartOfDay(), today.atTime(23, 59)};
            }
        }

        private void applyPreset(int index) {
            LocalDateTime[] range = getPresetRange(index);
            workingStart = range[0];
            workingEnd = range[1];
            leftMonth = YearMonth.from(workingStart.toLocalDate());
            firstClickDate = null;
            repaint();
        }

        private void applyWorkingValues() {
            DateRangePickerComponent.this.setStartDate(dateFrom(workingStart));
            DateRangePickerComponent.this.setEndDate(dateFrom(workingEnd));

            popupMenu.setVisible(false);
            repaint();
            DateRangePickerComponent.this.repaint();
        }

        private void cancelWorkingValues() {
            workingStart = committedStart;
            workingEnd = committedEnd;
            leftMonth = committedLeftMonth;
            firstClickDate = null;

            popupMenu.setVisible(false);
            repaint();
        }

        private List<LocalDate> getVisibleWeekStarts(YearMonth month) {
            List<LocalDate> rows = new ArrayList<>();
            LocalDate firstOfMonth = month.atDay(1);
            LocalDate gridStart = firstOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

            for (int i = 0; i < 6; i++) {
                LocalDate weekStart = gridStart.plusDays(i * 7L);
                if (weekContainsMonth(weekStart, month)) {
                    rows.add(weekStart);
                }
            }
            return rows;
        }

        private boolean weekContainsMonth(LocalDate weekStart, YearMonth month) {
            for (int i = 0; i < 7; i++) {
                if (YearMonth.from(weekStart.plusDays(i)).equals(month)) {
                    return true;
                }
            }
            return false;
        }

        private LocalDate dateAtMonthCell(Rectangle area, int mx, int my) {
            List<LocalDate> weekStarts = getVisibleWeekStarts(area == leftMonthRect ? leftMonth : leftMonth.plusMonths(1));
            int dayColW = (area.width - weekColW) / 7;
            int startY = area.y + monthHeaderH + weekHeaderH;

            if (my < startY || my >= startY + weekStarts.size() * cellH) {
                return null;
            }

            int row = (my - startY) / cellH;
            int relX = mx - area.x;

            if (relX < weekColW) {
                return null;
            }

            int col = (relX - weekColW) / dayColW;
            if (col < 0 || col > 6) {
                return null;
            }

            return weekStarts.get(row).plusDays(col);
        }

        private Integer weekRowAtMonth(Rectangle area, int mx, int my) {
            List<LocalDate> weekStarts = getVisibleWeekStarts(area == leftMonthRect ? leftMonth : leftMonth.plusMonths(1));
            int startY = area.y + monthHeaderH + weekHeaderH;

            if (my < startY || my >= startY + weekStarts.size() * cellH) {
                return null;
            }

            int relX = mx - area.x;
            if (relX < 0 || relX >= weekColW) {
                return null;
            }

            return (my - startY) / cellH;
        }

        private boolean isOverflowCell(Rectangle area, LocalDate date) {
            if (date == null) {
                return false;
            }
            YearMonth month = area == leftMonthRect ? leftMonth : leftMonth.plusMonths(1);
            return !YearMonth.from(date).equals(month);
        }

        private void handleDaySelection(LocalDate clicked) {
            if (clicked == null) {
                return;
            }

            if (firstClickDate == null) {
                firstClickDate = clicked;
                workingStart = clicked.atStartOfDay();
                workingEnd = clicked.atTime(23, 59);
            } else {
                LocalDate a = firstClickDate;
                LocalDate b = clicked;

                if (b.isBefore(a)) {
                    LocalDate tmp = a;
                    a = b;
                    b = tmp;
                }

                workingStart = a.atStartOfDay();
                workingEnd = b.atTime(23, 59);
                firstClickDate = null;
            }

            repaint();
        }

        private void handleWeekSelection(YearMonth month, int visibleRow) {
            List<LocalDate> weekStarts = getVisibleWeekStarts(month);
            LocalDate weekStart = weekStarts.get(visibleRow);
            LocalDate weekEnd = weekStart.plusDays(6);

            workingStart = weekStart.atStartOfDay();
            workingEnd = weekEnd.atTime(23, 59);
            firstClickDate = null;
            repaint();
        }

        private void updateSlider(MouseEvent e) {
            if (draggingStartSlider) {
                int mins = sliderMinutesFromX(startSliderRect, e.getX());
                workingStart = workingStart.withHour(mins / 60).withMinute(mins % 60);
                repaint();
            } else if (draggingEndSlider) {
                int mins = sliderMinutesFromX(endSliderRect, e.getX());
                workingEnd = workingEnd.withHour(mins / 60).withMinute(mins % 60);
                repaint();
            }
        }

        private int sliderMinutesFromX(Rectangle r, int mx) {
            double ratio = (mx - r.x) / (double) r.width;
            ratio = Math.max(0, Math.min(1, ratio));
            return (int) Math.round(ratio * 1439.0);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            Point p = e.getPoint();

            if (applyRect.contains(p)) {
                applyWorkingValues();
                return;
            }

            if (cancelRect.contains(p)) {
                cancelWorkingValues();
                return;
            }

            if (leftPrevRect.contains(p)) {
                leftMonth = leftMonth.minusMonths(1);
                repaint();
                return;
            }

            if (rightNextRect.contains(p)) {
                leftMonth = leftMonth.plusMonths(1);
                repaint();
                return;
            }

            for (int i = 0; i < presetRects.size(); i++) {
                if (presetRects.get(i).contains(p)) {
                    applyPreset(i);
                    return;
                }
            }

            Integer leftWeek = weekRowAtMonth(leftMonthRect, p.x, p.y);
            if (leftWeek != null) {
                handleWeekSelection(leftMonth, leftWeek);
                return;
            }

            Integer rightWeek = weekRowAtMonth(rightMonthRect, p.x, p.y);
            if (rightWeek != null) {
                handleWeekSelection(leftMonth.plusMonths(1), rightWeek);
                return;
            }

            LocalDate leftDate = dateAtMonthCell(leftMonthRect, p.x, p.y);
            if (leftDate != null) {
                handleDaySelection(leftDate);
                return;
            }

            LocalDate rightDate = dateAtMonthCell(rightMonthRect, p.x, p.y);
            if (rightDate != null) {
                handleDaySelection(rightDate);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (startSliderRect.contains(e.getPoint())) {
                draggingStartSlider = true;
                updateSlider(e);
            } else if (endSliderRect.contains(e.getPoint())) {
                draggingEndSlider = true;
                updateSlider(e);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            draggingStartSlider = false;
            draggingEndSlider = false;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            updateSlider(e);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            Point p = e.getPoint();

            hoverPreset = -1;
            for (int i = 0; i < presetRects.size(); i++) {
                if (presetRects.get(i).contains(p)) {
                    hoverPreset = i;
                    break;
                }
            }

            hoverWeekLeft = -1;
            hoverWeekRight = -1;

            Integer wl = weekRowAtMonth(leftMonthRect, e.getX(), e.getY());
            if (wl != null) {
                hoverWeekLeft = wl;
            }

            Integer wr = weekRowAtMonth(rightMonthRect, e.getX(), e.getY());
            if (wr != null) {
                hoverWeekRight = wr;
            }

            LocalDate leftDate = dateAtMonthCell(leftMonthRect, e.getX(), e.getY());
            LocalDate rightDate = dateAtMonthCell(rightMonthRect, e.getX(), e.getY());

            LocalDate d = null;
            if (leftDate != null && !isOverflowCell(leftMonthRect, leftDate)) {
                d = leftDate;
            } else if (rightDate != null && !isOverflowCell(rightMonthRect, rightDate)) {
                d = rightDate;
            }

            hoverLocalDate = d;

            hoverApply = applyRect.contains(p);
            hoverCancel = cancelRect.contains(p);

            boolean hand =
                    hoverApply ||
                            hoverCancel ||
                            hoverPreset >= 0 ||
                            hoverWeekLeft >= 0 ||
                            hoverWeekRight >= 0 ||
                            d != null ||
                            leftPrevRect.contains(p) ||
                            rightNextRect.contains(p) ||
                            startSliderRect.contains(p) ||
                            endSliderRect.contains(p);

            setCursor(Cursor.getPredefinedCursor(hand ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            repaint();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            hoverLocalDate = null;
            hoverPreset = -1;
            hoverWeekLeft = -1;
            hoverWeekRight = -1;
            hoverApply = false;
            hoverCancel = false;
            setCursor(Cursor.getDefaultCursor());
            repaint();
        }

        @Override public void mouseEntered(MouseEvent e) { }

        private Theme theme() {
            if (!dark) {
                return new Theme(
                        backgroundColor != null ? backgroundColor : Color.WHITE,
                        new Color(205, 205, 205),
                        new Color(238, 238, 238),
                        new Color(70, 70, 70),
                        new Color(55, 55, 55),
                        new Color(135, 135, 135),
                        new Color(95, 95, 95),
                        new Color(180, 180, 180),
                        new Color(185, 185, 185),
                        withAlpha(primaryColor, 12),
                        lineColor != null ? lineColor : new Color(213, 213, 213),
                        Color.WHITE,
                        new Color(190, 190, 190),
                        new Color(0, 0, 0, 0),
                        new Color(40, 40, 40)
                );
            }

            Color baseBg = mix(backgroundColor, new Color(34, 36, 41), 0.78f);
            return new Theme(
                    baseBg,
                    new Color(70, 74, 82),
                    new Color(64, 68, 75),
                    new Color(222, 226, 230),
                    new Color(235, 238, 241),
                    new Color(144, 151, 160),
                    new Color(182, 188, 195),
                    new Color(133, 140, 149),
                    new Color(112, 118, 126),
                    withAlpha(primaryColor, 20),
                    mix(lineColor, new Color(120, 126, 136), 0.55f),
                    new Color(245, 247, 250),
                    new Color(120, 126, 136),
                    withAlpha(primaryColor, 18),
                    new Color(229, 233, 238)
            );
        }

        private class Theme {
            final Color panelBg;
            final Color panelBorder;
            final Color divider;
            final Color text;
            final Color textStrong;
            final Color textMuted;
            final Color textMutedStrong;
            final Color weekNumber;
            final Color overflowText;
            final Color hoverSoft;
            final Color sliderTrack;
            final Color sliderKnobOuter;
            final Color sliderKnobBorder;
            final Color cancelHoverBg;
            final Color cancelText;

            Theme(Color panelBg, Color panelBorder, Color divider, Color text, Color textStrong,
                  Color textMuted, Color textMutedStrong, Color weekNumber, Color overflowText,
                  Color hoverSoft, Color sliderTrack, Color sliderKnobOuter, Color sliderKnobBorder,
                  Color cancelHoverBg, Color cancelText) {
                this.panelBg = panelBg;
                this.panelBorder = panelBorder;
                this.divider = divider;
                this.text = text;
                this.textStrong = textStrong;
                this.textMuted = textMuted;
                this.textMutedStrong = textMutedStrong;
                this.weekNumber = weekNumber;
                this.overflowText = overflowText;
                this.hoverSoft = hoverSoft;
                this.sliderTrack = sliderTrack;
                this.sliderKnobOuter = sliderKnobOuter;
                this.sliderKnobBorder = sliderKnobBorder;
                this.cancelHoverBg = cancelHoverBg;
                this.cancelText = cancelText;
            }
        }
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private static void enableQuality(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    private static void drawCenteredString(Graphics2D g2, String text, int centerX, int baselineY) {
        FontMetrics fm = g2.getFontMetrics();
        int x = centerX - fm.stringWidth(text) / 2;
        g2.drawString(text, x, baselineY);
    }

    private static void paintChevronDown(Graphics2D g2, int cx, int cy, int size, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(cx - size / 2, cy - 1, cx, cy + size / 3);
        g2.drawLine(cx, cy + size / 3, cx + size / 2, cy - 1);
    }

    private static void paintChevronLeft(Graphics2D g2, int cx, int cy, int size, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(cx + size / 3, cy - size / 2, cx - size / 3, cy);
        g2.drawLine(cx - size / 3, cy, cx + size / 3, cy + size / 2);
    }

    private static void paintChevronRight(Graphics2D g2, int cx, int cy, int size, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(cx - size / 3, cy - size / 2, cx + size / 3, cy);
        g2.drawLine(cx + size / 3, cy, cx - size / 3, cy + size / 2);
    }

    private static Color withAlpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private static Date dateFrom(LocalDateTime dt) {
        return Date.from(dt.atZone(ZoneId.systemDefault()).toInstant());
    }

    private static Color mix(Color a, Color b, float ratioTowardB) {
        float t = Math.max(0f, Math.min(1f, ratioTowardB));
        float u = 1f - t;
        int r = Math.round(a.getRed() * u + b.getRed() * t);
        int g = Math.round(a.getGreen() * u + b.getGreen() * t);
        int bl = Math.round(a.getBlue() * u + b.getBlue() * t);
        int al = Math.round(a.getAlpha() * u + b.getAlpha() * t);
        return new Color(r, g, bl, al);
    }
}