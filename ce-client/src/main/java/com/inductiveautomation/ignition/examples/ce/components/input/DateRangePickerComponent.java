package com.inductiveautomation.ignition.examples.ce.components.input;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateRangePickerComponent extends JComponent implements MouseListener {

    public static final int WEEK_START_SUNDAY = 0;
    public static final int WEEK_START_MONDAY = 1;

    private static final DateTimeFormatter BUTTON_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    private static final DateTimeFormatter FOOTER_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    private static final DateTimeFormatter MONTH_FORMAT =
            DateTimeFormatter.ofPattern("MMM yyyy");

    private static final DateTimeFormatter TIME_LABEL_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm");


    private static final int CHEVRON_ANIM_DELAY = 15;
    private static final float CHEVRON_ANIM_STEP = 24f;

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
    private int weekStartDay = WEEK_START_SUNDAY;
    private boolean productionDayTimes = false;

    private float chevronRotation = 0f;
    private final Timer chevronTimer;

    private final Image headerIcon = loadHeaderIcon();
    private final Image presetLogo = loadImage("/images/tesla_logo.png");

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

        chevronTimer = new Timer(CHEVRON_ANIM_DELAY, e -> {
            float target = isOpen ? 180f : 0f;

            if (Math.abs(chevronRotation - target) <= CHEVRON_ANIM_STEP) {
                chevronRotation = target;
                ((Timer) e.getSource()).stop();
            } else if (chevronRotation < target) {
                chevronRotation += CHEVRON_ANIM_STEP;
            } else {
                chevronRotation -= CHEVRON_ANIM_STEP;
            }

            repaint();
        });
        chevronTimer.setRepeats(true);

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

        if (old != open) {
            if (!chevronTimer.isRunning()) {
                chevronTimer.start();
            }
        } else {
            repaint();
        }
    }

    public int getWeekStartDay() {
        return weekStartDay;
    }

    public void setWeekStartDay(int weekStartDay) {
        int old = this.weekStartDay;
        int newValue = (weekStartDay == WEEK_START_MONDAY) ? WEEK_START_MONDAY : WEEK_START_SUNDAY;
        if (old == newValue) {
            return;
        }

        this.weekStartDay = newValue;
        firePropertyChange("weekStartDay", old, this.weekStartDay);

        if (popupPanel != null) {
            popupPanel.revalidateTimeFields();
            popupPanel.repaint();
        }
        repaint();
    }


    public boolean isProductionDayTimes() {
        return productionDayTimes;
    }

    public void setProductionDayTimes(boolean productionDayTimes) {
        boolean old = this.productionDayTimes;
        this.productionDayTimes = productionDayTimes;
        firePropertyChange("productionDayTimes", old, this.productionDayTimes);
        if (popupPanel != null) {
            popupPanel.revalidateTimeFields();
            popupPanel.repaint();
        }
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

            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();

            int leftPadding = 12;
            int rightPadding = 12;
            int chevronSpace = 18;

            int iconSize = Math.max(14, Math.min(18, h - 18));
            int iconX = leftPadding;
            int iconY = (h - iconSize) / 2;

            int textGap = 8;
            int textX = iconX + iconSize + textGap;
            int textY = (h - fm.getHeight()) / 2 + fm.getAscent();

            paintHeaderIcon(g2, iconX, iconY, iconSize, btnText);

            Shape oldClip = g2.getClip();
            g2.setClip(textX, 0, Math.max(0, w - textX - rightPadding - chevronSpace), h);

            g2.setColor(btnText);
            g2.drawString(text, textX, textY);

            g2.setClip(oldClip);

            paintAnimatedChevron(g2, w - 14, h / 2, 8, btnText, chevronRotation);
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

    private class PopupPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

        private final int popupW = 814;

        private final int panelRadius = 8;

        private final int presetPanelW = 192;
        private final int presetLogoSize = 64;
        private final int footerH = 52;
        private final int sliderAreaH = 112;
        private final int monthHeaderH = 28;
        private final int monthHeaderGap = 10;
        private final int weekHeaderH = 22;
        private final int cellH = 34;
        private final int weekColW = 30;
        private final int monthGap = 14;
        private final int weekRowCount = 6;
        private final int calendarTopPad = 12;
        private final int calendarSliderGap = 10;
        private final int presetRowH = 32;
        private final int presetScrollbarW = 8;
        private final int calendarH = monthHeaderH + monthHeaderGap + weekHeaderH + weekRowCount * cellH;
        private final int popupH = footerH + calendarTopPad + calendarH + calendarSliderGap + sliderAreaH;

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

        private Rectangle startMinusRect = new Rectangle();
        private Rectangle startPlusRect = new Rectangle();
        private Rectangle endMinusRect = new Rectangle();
        private Rectangle endPlusRect = new Rectangle();
        private Rectangle productionToggleRect = new Rectangle();

        private final JTextField startTimeField = new JTextField();
        private final JTextField endTimeField = new JTextField();
        private boolean syncingTimeFields = false;

        private boolean hoverStartMinus = false;
        private boolean hoverStartPlus = false;
        private boolean hoverEndMinus = false;
        private boolean hoverEndPlus = false;
        private boolean hoverProductionToggle = false;

        private int hoverPreset = -1;
        private int hoverWeekLeft = -1;
        private int hoverWeekRight = -1;

        private Rectangle leftMonthRect = new Rectangle();
        private Rectangle rightMonthRect = new Rectangle();
        private Rectangle leftPrevRect = new Rectangle();
        private Rectangle rightNextRect = new Rectangle();

        private Rectangle presetListRect = new Rectangle();
        private Rectangle presetScrollTrackRect = new Rectangle();
        private Rectangle presetScrollThumbRect = new Rectangle();
        private int presetScrollOffset = 0;
        private boolean draggingPresetScroll = false;
        private int presetScrollDragAnchorY = 0;
        private int presetScrollDragAnchorOffset = 0;

        private LocalDate firstClickDate = null;

        private final String[] presets = {
                "This Shift",
                "Previous Shift",
                "Today",
                "Yesterday",
                "This Week",
                "Previous Week",
                "Last 7 Days",
                "Last 15 Days",
                "Last 30 Days",
                "This Month",
                "Previous Month",
                "This Quarter",
                "Previous Quarter",
                "Last 6 Months",
                "Last 1 Year"
        };

        PopupPanel() {
            setOpaque(false);
            setBorder(null);
            setLayout(null);
            setPreferredSize(new Dimension(popupW, popupH));
            configureTimeField(startTimeField, true);
            configureTimeField(endTimeField, false);
            add(startTimeField);
            add(endTimeField);
            addMouseListener(this);
            addMouseMotionListener(this);
            addMouseWheelListener(this);
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
            hoverStartMinus = false;
            hoverStartPlus = false;
            hoverEndMinus = false;
            hoverEndPlus = false;
            hoverProductionToggle = false;
            draggingStartSlider = false;
            draggingEndSlider = false;
            draggingPresetScroll = false;
            presetScrollOffset = 0;
            setCursor(Cursor.getDefaultCursor());
            syncTimeFields();

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

                paintPresets(g2, panelX + 10, panelY + 14, presetPanelW - 20, contentBottom - panelY - 20, t);
                paintMonths(g2, panelX + presetPanelW + 12, panelY + calendarTopPad, panelW - presetPanelW - 24, calendarH, t);
                paintSliders(g2, panelX + presetPanelW + 18, contentBottom - sliderAreaH + 10, panelW - presetPanelW - 36, sliderAreaH - 10, t);
                paintFooter(g2, panelX, panelY + panelH - footerH, panelW, footerH, t);
            } finally {
                g2.dispose();
            }
        }

        private void paintPresets(Graphics2D g2, int x, int y, int w, int h, Theme t) {
            int contentY = y;
            int logoGap = 8;

            if (presetLogo != null) {
                int logoX = x + (w - presetLogoSize) / 2;
                g2.drawImage(presetLogo, logoX, contentY, presetLogoSize, presetLogoSize, null);
                contentY += presetLogoSize + logoGap;
            }

            int toggleH = 22;
            productionToggleRect.setBounds(x + 2, contentY, w - 4, toggleH);
            paintProductionToggle(g2, productionToggleRect, t);
            contentY += toggleH + 8;

            int listY = contentY;
            int listH = Math.max(0, y + h - listY);
            int maxScroll = maxPresetScrollOffset(listH);
            presetScrollOffset = clamp(presetScrollOffset, 0, maxScroll);

            boolean scrollNeeded = maxScroll > 0;
            int listW = scrollNeeded ? w - presetScrollbarW - 4 : w;
            presetListRect.setBounds(x, listY, listW, listH);

            Graphics2D listG = (Graphics2D) g2.create();
            try {
                listG.clipRect(x, listY, listW, listH);

                Font normal = getFont().deriveFont(Font.PLAIN, 11f);
                Font hoverFont = normal.deriveFont(Font.BOLD);

                for (int i = 0; i < presets.length; i++) {
                    int ry = listY + i * presetRowH - presetScrollOffset;
                    if (ry + presetRowH <= listY || ry >= listY + listH) {
                        continue;
                    }

                    Rectangle r = new Rectangle(x, ry, listW, presetRowH);
                    boolean active = isPresetActive(i);
                    boolean hover = (i == hoverPreset);

                    if (active) {
                        listG.setColor(withAlpha(primaryColor, dark ? 42 : 24));
                        listG.fillRoundRect(r.x, r.y, r.width, r.height, 4, 4);
                    } else if (hover) {
                        listG.setColor(t.hoverSoft);
                        listG.fillRoundRect(r.x, r.y, r.width, r.height, 4, 4);
                    }

                    listG.setFont((hover || active) ? hoverFont : normal);
                    listG.setColor(active ? primaryColor : t.text);
                    FontMetrics fm = listG.getFontMetrics();
                    int ty = r.y + (r.height - fm.getHeight()) / 2 + fm.getAscent();
                    listG.drawString(presets[i], r.x + 8, ty);
                }
            } finally {
                listG.dispose();
            }

            paintPresetScrollbar(g2, t);
        }

        private void paintPresetScrollbar(Graphics2D g2, Theme t) {
            int listH = presetListRect.height;
            int maxScroll = maxPresetScrollOffset(listH);
            if (maxScroll <= 0) {
                presetScrollTrackRect.setBounds(0, 0, 0, 0);
                presetScrollThumbRect.setBounds(0, 0, 0, 0);
                return;
            }

            int trackX = presetListRect.x + presetListRect.width + 2;
            int trackY = presetListRect.y;
            int trackH = presetListRect.height;
            presetScrollTrackRect.setBounds(trackX, trackY, presetScrollbarW, trackH);

            g2.setColor(t.sliderTrack);
            g2.fillRoundRect(trackX, trackY, presetScrollbarW, trackH, 4, 4);

            int thumbH = Math.max(24, (int) Math.round((listH / (double) presetListHeight()) * trackH));
            int thumbTravel = Math.max(0, trackH - thumbH);
            int thumbY = trackY + (maxScroll == 0 ? 0 : (int) Math.round((presetScrollOffset / (double) maxScroll) * thumbTravel));
            presetScrollThumbRect.setBounds(trackX, thumbY, presetScrollbarW, thumbH);

            g2.setColor(t.textMutedStrong);
            g2.fillRoundRect(trackX, thumbY, presetScrollbarW, thumbH, 4, 4);
        }

        private int presetListHeight() {
            return presets.length * presetRowH;
        }

        private int maxPresetScrollOffset(int viewportH) {
            return Math.max(0, presetListHeight() - viewportH);
        }

        private Integer presetIndexAt(Point p) {
            if (!presetListRect.contains(p)) {
                return null;
            }

            int relY = p.y - presetListRect.y + presetScrollOffset;
            int index = relY / presetRowH;
            if (index < 0 || index >= presets.length) {
                return null;
            }

            int itemY = presetListRect.y + index * presetRowH - presetScrollOffset;
            if (p.y >= itemY && p.y < itemY + presetRowH) {
                return index;
            }
            return null;
        }

        private void updatePresetScrollFromDrag(int mouseY) {
            int listH = presetListRect.height;
            int maxScroll = maxPresetScrollOffset(listH);
            if (maxScroll <= 0) {
                return;
            }

            int thumbH = presetScrollThumbRect.height;
            int trackH = presetScrollTrackRect.height;
            int thumbTravel = Math.max(1, trackH - thumbH);
            int deltaY = mouseY - presetScrollDragAnchorY;
            presetScrollOffset = clamp(
                    presetScrollDragAnchorOffset + (int) Math.round((deltaY / (double) thumbTravel) * maxScroll),
                    0,
                    maxScroll
            );
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

            int weekHeaderY = gridY + monthHeaderH + monthHeaderGap;
            g2.setFont(weekFont);
            g2.setColor(t.textMuted);
            String[] weekdayLabels = getWeekdayLabels();
            for (int i = 0; i < weekdayLabels.length; i++) {
                int cx;
                if (i == 0) {
                    cx = gridX + weekColW / 2;
                } else {
                    cx = gridX + weekColW + (i - 1) * dayColW + dayColW / 2;
                }
                drawCenteredString(g2, weekdayLabels[i], cx, weekHeaderY + 14);
            }

            LocalDate gridStart = calendarGridStart(month);
            LocalDate today = LocalDate.now();

            int startY = weekHeaderY + weekHeaderH;
            g2.setFont(dayFont);

            for (int gridRow = 0; gridRow < weekRowCount; gridRow++) {
                LocalDate weekStart = gridStart.plusDays(gridRow * 7L);
                if (!weekContainsMonth(weekStart, month)) {
                    continue;
                }

                int rowY = startY + gridRow * cellH;

                int weekNum = weekStart.get(WeekFields.of(getFirstDayOfWeek(), 1).weekOfWeekBasedYear());
                Rectangle weekRect = new Rectangle(gridX, rowY, weekColW, cellH);
                boolean hoverWeek = left ? (hoverWeekLeft == gridRow) : (hoverWeekRight == gridRow);

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

            int buttonSize = 20;
            int buttonGap = 8;

            int startX = x;
            int endX = x + sectionW + sectionGap;
            int inputY = y + 4;
            int sliderY = y + 60;

            g2.setFont(getFont().deriveFont(Font.BOLD, 12f));
            g2.setColor(t.textStrong);
            g2.drawString("Start Time", startX, y + 18);
            g2.drawString("End Time", endX, y + 18);

            int fieldW = 58;
            int fieldH = 24;
            startTimeField.setBounds(startX + 78, inputY, fieldW, fieldH);
            endTimeField.setBounds(endX + 68, inputY, fieldW, fieldH);
            styleTimeField(startTimeField, t);
            styleTimeField(endTimeField, t);
            if (!startTimeField.isFocusOwner() && !endTimeField.isFocusOwner()) {
                syncTimeFields();
            }


            int sliderWidth = sectionW - ((buttonSize * 2) + (buttonGap * 2)) - 10;

            startMinusRect.setBounds(startX, sliderY - 10, buttonSize, buttonSize);
            startSliderRect.setBounds(startMinusRect.x + buttonSize + buttonGap, sliderY - 7, sliderWidth, 18);
            startPlusRect.setBounds(startSliderRect.x + startSliderRect.width + buttonGap, sliderY - 10, buttonSize, buttonSize);

            endMinusRect.setBounds(endX, sliderY - 10, buttonSize, buttonSize);
            endSliderRect.setBounds(endMinusRect.x + buttonSize + buttonGap, sliderY - 7, sliderWidth, 18);
            endPlusRect.setBounds(endSliderRect.x + endSliderRect.width + buttonGap, sliderY - 10, buttonSize, buttonSize);

            paintIconButton(g2, startMinusRect, "-", hoverStartMinus, t);
            paintSlider(g2, startSliderRect, workingStart.getHour() * 60 + workingStart.getMinute(), t);
            paintIconButton(g2, startPlusRect, "+", hoverStartPlus, t);

            paintIconButton(g2, endMinusRect, "-", hoverEndMinus, t);
            paintSlider(g2, endSliderRect, workingEnd.getHour() * 60 + workingEnd.getMinute(), t);
            paintIconButton(g2, endPlusRect, "+", hoverEndPlus, t);
        }

        private void paintProductionToggle(Graphics2D g2, Rectangle r, Theme t) {
            boolean active = DateRangePickerComponent.this.isProductionDayTimes();

            int trackW = 40;
            int trackH = 20;
            int trackX = r.x + 2;
            int trackY = r.y + (r.height - trackH) / 2;
            int trackArc = trackH;

            if (hoverProductionToggle && !active) {
                g2.setColor(withAlpha(primaryColor, dark ? 36 : 18));
                g2.fillRoundRect(trackX - 2, trackY - 2, trackW + 4, trackH + 4, trackArc + 4, trackArc + 4);
            }

            g2.setColor(active ? primaryColor : t.sliderTrack);
            g2.fillRoundRect(trackX, trackY, trackW, trackH, trackArc, trackArc);

            int knobD = trackH - 4;
            int knobX = active ? trackX + trackW - knobD - 2 : trackX + 2;
            int knobY = trackY + 2;
            g2.setColor(t.sliderKnobOuter);
            g2.fillOval(knobX, knobY, knobD, knobD);

            g2.setFont(getFont().deriveFont(Font.BOLD, 11f));
            g2.setColor(active ? primaryColor : t.textMutedStrong);
            FontMetrics fm = g2.getFontMetrics();
            int ty = r.y + (r.height - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString("Production Day", trackX + trackW + 8, ty);
        }

        private void paintSlider(Graphics2D g2, Rectangle r, int minutes, Theme t) {
            int lineY = r.y + r.height / 2;
            int lineX1 = r.x;
            int lineX2 = r.x + r.width;

            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(t.sliderTrack);
            g2.drawLine(lineX1, lineY, lineX2, lineY);

            int knobX = r.x + (int) Math.round((minutes / 1439.0) * r.width);
            knobX = clamp(knobX, r.x, r.x + r.width);

            g2.setColor(primaryColor);
            g2.drawLine(lineX1, lineY, knobX, lineY);

            int outerD = 14;
            int innerD = 8;
            int outerX = knobX - outerD / 2;
            int outerY = lineY - outerD / 2;
            int innerX = knobX - innerD / 2;
            int innerY = lineY - innerD / 2;

            g2.setColor(t.sliderKnobOuter);
            g2.fillOval(outerX, outerY, outerD, outerD);

            g2.setColor(t.sliderKnobBorder);
            g2.setStroke(new BasicStroke(1f));
            g2.drawOval(outerX, outerY, outerD, outerD);

            g2.setColor(primaryColor);
            g2.fillOval(innerX, innerY, innerD, innerD);
        }

        private void paintIconButton(Graphics2D g2, Rectangle r, String text, boolean hover, Theme t) {
            Graphics2D g = (Graphics2D) g2.create();
            try {
                enableQuality(g);

                if (hover) {
                    g.setColor(t.hoverSoft);
                    g.fillOval(r.x, r.y, r.width, r.height);
                }

                g.setFont(getFont().deriveFont(Font.BOLD, Math.max(14f, r.height * 0.75f)));
                g.setColor(t.textStrong);

                FontMetrics fm = g.getFontMetrics();
                int tx = r.x + (r.width - fm.stringWidth(text)) / 2;
                int ty = r.y + (r.height - fm.getHeight()) / 2 + fm.getAscent();

                g.drawString(text, tx, ty);
            } finally {
                g.dispose();
            }
        }

        private void paintFooter(Graphics2D g2, int x, int y, int w, int h, Theme t) {
            int pad = 12;

            String footerTxt = "From: " + FOOTER_FORMAT.format(workingStart)
                    + " | To: " + FOOTER_FORMAT.format(workingEnd);

            g2.setFont(getFont().deriveFont(Font.PLAIN, 11f));
            g2.setColor(t.textMuted);
            FontMetrics fm = g2.getFontMetrics();
            int ty = y + (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(footerTxt, x + pad, ty);

            int btnH = 30;
            int btnW = 74;
            int gap = 10;

            int applyX = x + w - pad - btnW;
            int cancelX = applyX - gap - btnW;
            int btnY = y + (h - btnH) / 2;

            cancelRect.setBounds(cancelX, btnY, btnW, btnH);
            applyRect.setBounds(applyX, btnY, btnW, btnH);

            if (hoverCancel) {
                g2.setColor(t.cancelHoverBg);
                g2.fillRoundRect(cancelRect.x, cancelRect.y, cancelRect.width, cancelRect.height, 6, 6);
            }
            g2.setColor(t.cancelText);
            g2.setFont(getFont().deriveFont(Font.BOLD, 12f));
            drawCenteredString(g2, "Cancel", cancelRect.x + cancelRect.width / 2, cancelRect.y + 19);

            g2.setColor(primaryColor);
            g2.fillRoundRect(applyRect.x, applyRect.y, applyRect.width, applyRect.height, 6, 6);
            g2.setColor(Color.WHITE);
            drawCenteredString(g2, "Apply", applyRect.x + applyRect.width / 2, applyRect.y + 19);
        }

        private boolean isPresetActive(int index) {
            LocalDateTime[] range = getPresetRange(index);
            return workingStart.toLocalDate().equals(range[0].toLocalDate())
                    && workingEnd.toLocalDate().equals(range[1].toLocalDate())
                    && workingStart.toLocalTime().equals(range[0].toLocalTime())
                    && workingEnd.toLocalTime().equals(range[1].toLocalTime());
        }

        private LocalDateTime[] getPresetRange(int index) {
            LocalDate today = LocalDate.now();

            switch (index) {
                case 0:
                    return getShiftRange(0);
                case 1:
                    return getShiftRange(-1);
                case 2:
                    return rangeForWholeDays(today, today);
                case 3: {
                    LocalDate d = today.minusDays(1);
                    return rangeForWholeDays(d, d);
                }
                case 4: {
                    LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(getFirstDayOfWeek()));
                    return rangeForWholeDays(weekStart, weekStart.plusDays(6));
                }
                case 5: {
                    LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(getFirstDayOfWeek())).minusWeeks(1);
                    return rangeForWholeDays(weekStart, weekStart.plusDays(6));
                }
                case 6:
                    return rangeForWholeDays(today.minusDays(6), today);
                case 7:
                    return rangeForWholeDays(today.minusDays(14), today);
                case 8:
                    return rangeForWholeDays(today.minusDays(29), today);
                case 9: {
                    LocalDate monthStart = today.withDayOfMonth(1);
                    return rangeForWholeDays(monthStart, monthStart.with(TemporalAdjusters.lastDayOfMonth()));
                }
                case 10: {
                    LocalDate monthStart = today.withDayOfMonth(1).minusMonths(1);
                    return rangeForWholeDays(monthStart, monthStart.with(TemporalAdjusters.lastDayOfMonth()));
                }
                case 11: {
                    LocalDate quarterStart = getQuarterStart(today);
                    return rangeForWholeDays(quarterStart, quarterStart.plusMonths(3).minusDays(1));
                }
                case 12: {
                    LocalDate quarterStart = getQuarterStart(today).minusMonths(3);
                    return rangeForWholeDays(quarterStart, quarterStart.plusMonths(3).minusDays(1));
                }
                case 13:
                    return rangeForWholeDays(today.minusMonths(6), today);
                case 14:
                    return rangeForWholeDays(today.minusYears(1), today);
                default:
                    return rangeForWholeDays(today, today);
            }
        }

        private LocalDateTime[] getShiftRange(int offset) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime currentStart;
            LocalDateTime currentEnd;
            LocalTime time = now.toLocalTime();

            if (!time.isBefore(LocalTime.of(5, 0)) && time.isBefore(LocalTime.of(17, 0))) {
                currentStart = now.toLocalDate().atTime(5, 0);
                currentEnd = now.toLocalDate().atTime(17, 0);
            } else if (!time.isBefore(LocalTime.of(17, 0))) {
                currentStart = now.toLocalDate().atTime(17, 0);
                currentEnd = now.toLocalDate().plusDays(1).atTime(5, 0);
            } else {
                currentStart = now.toLocalDate().minusDays(1).atTime(17, 0);
                currentEnd = now.toLocalDate().atTime(5, 0);
            }

            if (offset < 0) {
                for (int i = 0; i < Math.abs(offset); i++) {
                    currentEnd = currentStart;
                    currentStart = currentStart.minusHours(12);
                }
            } else if (offset > 0) {
                for (int i = 0; i < offset; i++) {
                    currentStart = currentEnd;
                    currentEnd = currentEnd.plusHours(12);
                }
            }

            return new LocalDateTime[]{currentStart, currentEnd};
        }

        private LocalDateTime[] rangeForWholeDays(LocalDate start, LocalDate end) {
            if (DateRangePickerComponent.this.isProductionDayTimes()) {
                return new LocalDateTime[]{start.atTime(5, 0), end.plusDays(1).atTime(5, 0)};
            }
            return new LocalDateTime[]{start.atStartOfDay(), end.atTime(23, 59)};
        }

        private LocalDate getQuarterStart(LocalDate date) {
            int firstMonth = ((date.getMonthValue() - 1) / 3) * 3 + 1;
            return LocalDate.of(date.getYear(), firstMonth, 1);
        }

        private void applyPreset(int index) {
            LocalDateTime[] range = getPresetRange(index);
            workingStart = range[0];
            workingEnd = range[1];
            leftMonth = YearMonth.from(workingStart.toLocalDate());
            firstClickDate = null;
            syncTimeFields();
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

        private LocalDate calendarGridStart(YearMonth month) {
            return month.atDay(1).with(TemporalAdjusters.previousOrSame(getFirstDayOfWeek()));
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
            YearMonth month = area == leftMonthRect ? leftMonth : leftMonth.plusMonths(1);
            LocalDate gridStart = calendarGridStart(month);
            int dayColW = (area.width - weekColW) / 7;
            int startY = area.y + monthHeaderH + monthHeaderGap + weekHeaderH;

            if (my < startY || my >= startY + weekRowCount * cellH) {
                return null;
            }

            int row = (my - startY) / cellH;
            if (row < 0 || row >= weekRowCount) {
                return null;
            }

            LocalDate weekStart = gridStart.plusDays(row * 7L);
            if (!weekContainsMonth(weekStart, month)) {
                return null;
            }

            int relX = mx - area.x;

            if (relX < weekColW) {
                return null;
            }

            int col = (relX - weekColW) / dayColW;
            if (col < 0 || col > 6) {
                return null;
            }

            return weekStart.plusDays(col);
        }

        private Integer weekRowAtMonth(Rectangle area, int mx, int my) {
            YearMonth month = area == leftMonthRect ? leftMonth : leftMonth.plusMonths(1);
            LocalDate gridStart = calendarGridStart(month);
            int startY = area.y + monthHeaderH + monthHeaderGap + weekHeaderH;

            if (my < startY || my >= startY + weekRowCount * cellH) {
                return null;
            }

            int relX = mx - area.x;
            if (relX < 0 || relX >= weekColW) {
                return null;
            }

            int row = (my - startY) / cellH;
            if (row < 0 || row >= weekRowCount) {
                return null;
            }

            LocalDate weekStart = gridStart.plusDays(row * 7L);
            if (!weekContainsMonth(weekStart, month)) {
                return null;
            }

            return row;
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
                LocalDateTime[] range = rangeForWholeDays(clicked, clicked);
                workingStart = range[0];
                workingEnd = range[1];
            } else {
                LocalDate a = firstClickDate;
                LocalDate b = clicked;

                if (b.isBefore(a)) {
                    LocalDate tmp = a;
                    a = b;
                    b = tmp;
                }

                LocalDateTime[] range = rangeForWholeDays(a, b);
                workingStart = range[0];
                workingEnd = range[1];
                firstClickDate = null;
            }

            syncTimeFields();
            repaint();
        }

        private void handleWeekSelection(YearMonth month, int visibleRow) {
            LocalDate weekStart = calendarGridStart(month).plusDays(visibleRow * 7L);
            LocalDate weekEnd = weekStart.plusDays(6);

            LocalDateTime[] range = rangeForWholeDays(weekStart, weekEnd);
            workingStart = range[0];
            workingEnd = range[1];
            firstClickDate = null;
            syncTimeFields();
            repaint();
        }

        private void updateSlider(MouseEvent e) {
            if (draggingStartSlider) {
                int mins = sliderMinutesFromX(startSliderRect, e.getX());
                setManualTimeMode();
                workingStart = workingStart.withHour(mins / 60).withMinute(mins % 60);
                syncTimeFields();
                repaint();
            } else if (draggingEndSlider) {
                int mins = sliderMinutesFromX(endSliderRect, e.getX());
                setManualTimeMode();
                workingEnd = workingEnd.withHour(mins / 60).withMinute(mins % 60);
                syncTimeFields();
                repaint();
            }
        }

        private int sliderMinutesFromX(Rectangle r, int mx) {
            double ratio = (mx - r.x) / (double) r.width;
            ratio = Math.max(0, Math.min(1, ratio));
            return (int) Math.round(ratio * 1439.0);
        }

        private void adjustStartMinutes(int delta) {
            int current = workingStart.getHour() * 60 + workingStart.getMinute();
            int updated = clamp(current + delta, 0, 1439);
            setManualTimeMode();
            workingStart = workingStart.withHour(updated / 60).withMinute(updated % 60);
            syncTimeFields();
            repaint();
        }

        private void adjustEndMinutes(int delta) {
            int current = workingEnd.getHour() * 60 + workingEnd.getMinute();
            int updated = clamp(current + delta, 0, 1439);
            setManualTimeMode();
            workingEnd = workingEnd.withHour(updated / 60).withMinute(updated % 60);
            syncTimeFields();
            repaint();
        }

        private void configureTimeField(final JTextField field, final boolean startField) {
            field.setHorizontalAlignment(JTextField.CENTER);
            field.setColumns(5);
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(185, 185, 185)),
                    BorderFactory.createEmptyBorder(1, 4, 1, 4)
            ));
            field.addActionListener(e -> commitTimeField(field, startField));
            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    commitTimeField(field, startField);
                }
            });
        }

        private void styleTimeField(JTextField field, Theme t) {
            field.setFont(getFont().deriveFont(Font.PLAIN, 12f));
            field.setForeground(t.textStrong);
            field.setBackground(dark ? new Color(44, 47, 53) : Color.WHITE);
            field.setCaretColor(t.textStrong);
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(t.sliderKnobBorder),
                    BorderFactory.createEmptyBorder(1, 4, 1, 4)
            ));
        }

        void revalidateTimeFields() {
            syncTimeFields();
        }

        private void syncTimeFields() {
            if (workingStart == null || workingEnd == null) {
                return;
            }
            syncingTimeFields = true;
            startTimeField.setText(TIME_LABEL_FORMAT.format(workingStart.toLocalTime()));
            endTimeField.setText(TIME_LABEL_FORMAT.format(workingEnd.toLocalTime()));
            syncingTimeFields = false;
        }

        private void commitTimeField(JTextField field, boolean startField) {
            if (syncingTimeFields || workingStart == null || workingEnd == null) {
                return;
            }

            LocalTime parsed = parseManualTime(field.getText());
            if (parsed == null) {
                syncTimeFields();
                return;
            }

            setManualTimeMode();
            if (startField) {
                workingStart = workingStart.withHour(parsed.getHour()).withMinute(parsed.getMinute());
            } else {
                workingEnd = workingEnd.withHour(parsed.getHour()).withMinute(parsed.getMinute());
            }
            syncTimeFields();
            repaint();
        }

        private LocalTime parseManualTime(String text) {
            if (text == null) {
                return null;
            }
            String value = text.trim();
            try {
                if (value.matches("\\d{1,2}:\\d{1,2}")) {
                    String[] parts = value.split(":");
                    int hour = Integer.parseInt(parts[0]);
                    int minute = Integer.parseInt(parts[1]);
                    if (hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59) {
                        return LocalTime.of(hour, minute);
                    }
                } else if (value.matches("\\d{3,4}")) {
                    int split = value.length() - 2;
                    int hour = Integer.parseInt(value.substring(0, split));
                    int minute = Integer.parseInt(value.substring(split));
                    if (hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59) {
                        return LocalTime.of(hour, minute);
                    }
                }
            } catch (Exception ignored) {
            }
            return null;
        }

        private void setManualTimeMode() {
            if (DateRangePickerComponent.this.isProductionDayTimes()) {
                DateRangePickerComponent.this.setProductionDayTimes(false);
            }
        }

        private void toggleProductionDayTimes() {
            boolean newValue = !DateRangePickerComponent.this.isProductionDayTimes();
            DateRangePickerComponent.this.setProductionDayTimes(newValue);
            if (newValue) {
                LocalDate start = workingStart.toLocalDate();
                LocalDate end = workingEnd.toLocalDate();
                if (workingEnd.toLocalTime().equals(LocalTime.of(5, 0)) && workingEnd.toLocalDate().isAfter(start)) {
                    end = workingEnd.toLocalDate().minusDays(1);
                }
                LocalDateTime[] range = rangeForWholeDays(start, end);
                workingStart = range[0];
                workingEnd = range[1];
                syncTimeFields();
            }
            repaint();
        }

        private DayOfWeek getFirstDayOfWeek() {
            return DateRangePickerComponent.this.getWeekStartDay() == WEEK_START_MONDAY
                    ? DayOfWeek.MONDAY
                    : DayOfWeek.SUNDAY;
        }

        private String[] getWeekdayLabels() {
            String[] labels = new String[8];
            labels[0] = "W";
            String[] names = {"Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"};
            DayOfWeek first = getFirstDayOfWeek();
            for (int i = 0; i < 7; i++) {
                DayOfWeek day = first.plus(i);
                labels[i + 1] = names[day.getValue() - 1];
            }
            return labels;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            Point p = e.getPoint();

            if (startMinusRect.contains(p)) {
                adjustStartMinutes(-1);
                return;
            }

            if (startPlusRect.contains(p)) {
                adjustStartMinutes(1);
                return;
            }

            if (endMinusRect.contains(p)) {
                adjustEndMinutes(-1);
                return;
            }

            if (endPlusRect.contains(p)) {
                adjustEndMinutes(1);
                return;
            }

            if (productionToggleRect.contains(p)) {
                toggleProductionDayTimes();
                return;
            }

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

            Integer presetIdx = presetIndexAt(p);
            if (presetIdx != null) {
                applyPreset(presetIdx);
                return;
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
            Point p = e.getPoint();
            if (presetScrollThumbRect.contains(p)) {
                draggingPresetScroll = true;
                presetScrollDragAnchorY = p.y;
                presetScrollDragAnchorOffset = presetScrollOffset;
                return;
            }
            if (presetScrollTrackRect.contains(p) && presetScrollTrackRect.height > 0) {
                int maxScroll = maxPresetScrollOffset(presetListRect.height);
                if (maxScroll > 0) {
                    if (p.y < presetScrollThumbRect.y) {
                        presetScrollOffset = clamp(presetScrollOffset - presetListRect.height, 0, maxScroll);
                    } else if (p.y > presetScrollThumbRect.y + presetScrollThumbRect.height) {
                        presetScrollOffset = clamp(presetScrollOffset + presetListRect.height, 0, maxScroll);
                    }
                    repaint();
                }
                return;
            }
            if (startSliderRect.contains(p)) {
                draggingStartSlider = true;
                updateSlider(e);
            } else if (endSliderRect.contains(p)) {
                draggingEndSlider = true;
                updateSlider(e);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            draggingStartSlider = false;
            draggingEndSlider = false;
            draggingPresetScroll = false;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (draggingPresetScroll) {
                updatePresetScrollFromDrag(e.getY());
                repaint();
                return;
            }
            updateSlider(e);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (!presetListRect.contains(e.getPoint())) {
                return;
            }
            int maxScroll = maxPresetScrollOffset(presetListRect.height);
            if (maxScroll <= 0) {
                return;
            }
            presetScrollOffset = clamp(presetScrollOffset + e.getWheelRotation() * presetRowH, 0, maxScroll);
            repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            Point p = e.getPoint();

            hoverPreset = -1;
            Integer presetIdx = presetIndexAt(p);
            if (presetIdx != null) {
                hoverPreset = presetIdx;
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

            hoverStartMinus = startMinusRect.contains(p);
            hoverStartPlus = startPlusRect.contains(p);
            hoverEndMinus = endMinusRect.contains(p);
            hoverEndPlus = endPlusRect.contains(p);
            hoverProductionToggle = productionToggleRect.contains(p);

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
                            endSliderRect.contains(p) ||
                            hoverStartMinus ||
                            hoverStartPlus ||
                            hoverEndMinus ||
                            hoverEndPlus ||
                            hoverProductionToggle ||
                            presetScrollThumbRect.contains(p) ||
                            presetScrollTrackRect.contains(p);

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
            hoverStartMinus = false;
            hoverStartPlus = false;
            hoverEndMinus = false;
            hoverEndPlus = false;
            hoverProductionToggle = false;
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

    private Image loadHeaderIcon() {
        return loadImage("/images/daterange_picker_icon.png");
    }

    private Image loadImage(String resourcePath) {
        try {
            InputStream is = DateRangePickerComponent.class.getResourceAsStream(resourcePath);
            if (is != null) {
                try {
                    BufferedImage img = ImageIO.read(is);
                    if (img != null) {
                        return img;
                    }
                } finally {
                    is.close();
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private void paintHeaderIcon(Graphics2D g2, int x, int y, int size, Color tint) {
        if (headerIcon == null || size <= 0) {
            return;
        }

        BufferedImage tinted = tintImage(headerIcon, tint != null ? tint : getHeaderTextColor(), size, size);
        if (tinted != null) {
            g2.drawImage(tinted, x, y, null);
        }
    }

    private BufferedImage tintImage(Image image, Color tint, int width, int height) {
        if (image == null || width <= 0 || height <= 0) {
            return null;
        }

        BufferedImage src = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = src.createGraphics();
        try {
            enableQuality(g2);
            g2.drawImage(image, 0, 0, width, height, null);
            g2.setComposite(AlphaComposite.SrcIn);
            g2.setColor(tint != null ? tint : Color.GRAY);
            g2.fillRect(0, 0, width, height);
        } finally {
            g2.dispose();
        }
        return src;
    }

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

    private static void paintAnimatedChevron(Graphics2D g2, int cx, int cy, int size, Color color, float rotationDeg) {
        Graphics2D g = (Graphics2D) g2.create();
        try {
            g.translate(cx, cy);
            g.rotate(Math.toRadians(rotationDeg));
            g.setColor(color);
            g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(-size / 2, -1, 0, size / 3);
            g.drawLine(0, size / 3, size / 2, -1);
        } finally {
            g.dispose();
        }
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