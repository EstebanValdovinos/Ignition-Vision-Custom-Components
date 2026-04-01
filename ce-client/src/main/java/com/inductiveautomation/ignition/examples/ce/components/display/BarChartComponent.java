package com.inductiveautomation.ignition.examples.ce.components.display;

import com.inductiveautomation.ignition.common.BasicDataset;
import com.inductiveautomation.ignition.common.Dataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class BarChartComponent extends JComponent implements MouseListener, MouseMotionListener {

    public static final int MODE_GROUPED = 0;
    public static final int MODE_STACKED = 1;

    private static final float DEFAULT_PADDING = 25f;
    private static final int TARGET_LABEL_COUNT = 10;

    private Dataset data = createDefaultData();
    private Dataset seriesProperties = createDefaultSeriesProperties();

    private String chartTitle = "Availability Breakdown";
    private boolean loading = false;

    private int chartMode = MODE_STACKED;

    private boolean autoRange = false;
    private float lowerBound = 0f;
    private float upperBound = 100f;

    private Color axisLineColor = new Color(200, 200, 200);
    private float axisLineWidth = 1f;
    private int xAxisGap = 5;

    private boolean showAxisLabels = true;
    private boolean showYAxis = false;
    private boolean showLegends = true;

    private float categoryGapRatio = 0.20f;   // % of each category slot reserved as gap
    private float barGapRatio = 0.08f;        // % gap between grouped bars
    private int cornerRadius = 0;

    private int hoverIndex = -1;
    private int selectedIndex = -1;

    public BarChartComponent() {
        setPreferredSize(new Dimension(500, 260));
        setMinimumSize(new Dimension(220, 140));
        setOpaque(false);
        setFont(new Font("Dialog", Font.BOLD, 14));
        setForeground(new Color(70, 70, 70));
        setBackground(new Color(0, 0, 0, 0));

        addMouseListener(this);
        addMouseMotionListener(this);
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    // ---------------------------------------------------------------------
    // Properties
    // ---------------------------------------------------------------------

    public Dataset getData() {
        return data;
    }

    public void setData(Dataset data) {
        Dataset old = this.data;
        this.data = data != null ? data : createDefaultData();
        clampInteractionIndexes();
        firePropertyChange("data", old, this.data);
        repaint();
    }

    public Dataset getSeriesProperties() {
        return seriesProperties;
    }

    public void setSeriesProperties(Dataset seriesProperties) {
        Dataset old = this.seriesProperties;
        this.seriesProperties = seriesProperties != null ? seriesProperties : createDefaultSeriesProperties();
        firePropertyChange("seriesProperties", old, this.seriesProperties);
        repaint();
    }

    public String getChartTitle() {
        return chartTitle;
    }

    public void setChartTitle(String chartTitle) {
        String old = this.chartTitle;
        this.chartTitle = chartTitle != null ? chartTitle : "";
        firePropertyChange("chartTitle", old, this.chartTitle);
        repaint();
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        boolean old = this.loading;
        this.loading = loading;
        firePropertyChange("loading", old, this.loading);
        repaint();
    }

    public int getChartMode() {
        return chartMode;
    }

    public void setChartMode(int chartMode) {
        int old = this.chartMode;
        this.chartMode = (chartMode == MODE_STACKED) ? MODE_STACKED : MODE_GROUPED;
        firePropertyChange("chartMode", old, this.chartMode);
        repaint();
    }

    public boolean isAutoRange() {
        return autoRange;
    }

    public void setAutoRange(boolean autoRange) {
        boolean old = this.autoRange;
        this.autoRange = autoRange;
        firePropertyChange("autoRange", old, this.autoRange);
        repaint();
    }

    public float getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(float lowerBound) {
        float old = this.lowerBound;
        this.lowerBound = lowerBound;
        firePropertyChange("lowerBound", old, this.lowerBound);
        repaint();
    }

    public float getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(float upperBound) {
        float old = this.upperBound;
        this.upperBound = upperBound;
        firePropertyChange("upperBound", old, this.upperBound);
        repaint();
    }

    public Color getAxisLineColor() {
        return axisLineColor;
    }

    public void setAxisLineColor(Color axisLineColor) {
        Color old = this.axisLineColor;
        this.axisLineColor = axisLineColor != null ? axisLineColor : new Color(200, 200, 200);
        firePropertyChange("axisLineColor", old, this.axisLineColor);
        repaint();
    }

    public float getAxisLineWidth() {
        return axisLineWidth;
    }

    public void setAxisLineWidth(float axisLineWidth) {
        float old = this.axisLineWidth;
        this.axisLineWidth = Math.max(0f, axisLineWidth);
        firePropertyChange("axisLineWidth", old, this.axisLineWidth);
        repaint();
    }

    public int getXAxisGap() {
        return xAxisGap;
    }

    public void setXAxisGap(int xAxisGap) {
        int old = this.xAxisGap;
        this.xAxisGap = Math.max(0, xAxisGap);
        firePropertyChange("xAxisGap", old, this.xAxisGap);
        repaint();
    }

    public boolean isShowAxisLabels() {
        return showAxisLabels;
    }

    public void setShowAxisLabels(boolean showAxisLabels) {
        boolean old = this.showAxisLabels;
        this.showAxisLabels = showAxisLabels;
        firePropertyChange("showAxisLabels", old, this.showAxisLabels);
        repaint();
    }

    public boolean isShowYAxis() {
        return showYAxis;
    }

    public void setShowYAxis(boolean showYAxis) {
        boolean old = this.showYAxis;
        this.showYAxis = showYAxis;
        firePropertyChange("showYAxis", old, this.showYAxis);
        repaint();
    }

    public boolean isShowLegends() {
        return showLegends;
    }

    public void setShowLegends(boolean showLegends) {
        boolean old = this.showLegends;
        this.showLegends = showLegends;
        firePropertyChange("showLegends", old, this.showLegends);
        repaint();
    }

    public float getCategoryGapRatio() {
        return categoryGapRatio;
    }

    public void setCategoryGapRatio(float categoryGapRatio) {
        float old = this.categoryGapRatio;
        this.categoryGapRatio = clamp(categoryGapRatio, 0f, 0.8f);
        firePropertyChange("categoryGapRatio", old, this.categoryGapRatio);
        repaint();
    }

    public float getBarGapRatio() {
        return barGapRatio;
    }

    public void setBarGapRatio(float barGapRatio) {
        float old = this.barGapRatio;
        this.barGapRatio = clamp(barGapRatio, 0f, 0.8f);
        firePropertyChange("barGapRatio", old, this.barGapRatio);
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

    public int getHoverIndex() {
        return hoverIndex;
    }

    public void setHoverIndex(int hoverIndex) {
        int old = this.hoverIndex;
        int max = getRowCount() - 1;
        if (hoverIndex < -1) hoverIndex = -1;
        if (hoverIndex > max) hoverIndex = max;
        this.hoverIndex = hoverIndex;
        firePropertyChange("hoverIndex", old, this.hoverIndex);
        repaint();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        int old = this.selectedIndex;
        int max = getRowCount() - 1;
        if (selectedIndex < -1) selectedIndex = -1;
        if (selectedIndex > max) selectedIndex = max;
        this.selectedIndex = selectedIndex;
        firePropertyChange("selectedIndex", old, this.selectedIndex);
        repaint();
    }

    @Override
    public void setForeground(Color fg) {
        Color old = getForeground();
        super.setForeground(fg != null ? fg : new Color(70, 70, 70));
        firePropertyChange("foreground", old, getForeground());
        repaint();
    }

    @Override
    public void setFont(Font font) {
        Font old = getFont();
        super.setFont(font != null ? font : new Font("Dialog", Font.BOLD, 14));
        firePropertyChange("font", old, getFont());
        revalidate();
        repaint();
    }

    @Override
    public void setEnabled(boolean enabled) {
        boolean old = isEnabled();
        super.setEnabled(enabled);
        firePropertyChange("enabled", old, enabled);
        if (!enabled) {
            setHoverIndex(-1);
        }
        repaint();
    }

    // ---------------------------------------------------------------------
    // Paint
    // ---------------------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            float width = getWidth();
            float height = getHeight();

            if (!hasUsableData() || loading) {
                if (!loading) {
                    g2.setColor(Color.GRAY);
                    String msg = "No Data";
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(msg, (int) ((width - fm.stringWidth(msg)) / 2f), (int) (height / 2f));
                }
                return;
            }

            g2.setFont(getFont());
            g2.setColor(getForeground());
            FontMetrics titleMetrics = g2.getFontMetrics();
            String safeTitle = chartTitle != null ? chartTitle : "";
            int titleWidth = titleMetrics.stringWidth(safeTitle);
            int titleAscent = titleMetrics.getAscent();
            g2.drawString(safeTitle, (int) ((width - titleWidth) / 2f), titleAscent + 5);

            int legendHeight = showLegends ? 30 : 0;
            float graphTopY = (titleAscent + 5) + 10f;
            float graphBottomY = height - DEFAULT_PADDING - xAxisGap - legendHeight;
            float availableGraphHeight = graphBottomY - graphTopY;

            int rowCount = data.getRowCount();
            int colCount = data.getColumnCount();

            Range range = computeRange();
            float minVal = range.lower;
            float maxVal = range.upper;
            float yRange = maxVal - minVal;
            if (yRange == 0f) {
                yRange = 1f;
            }

            paintBars(g2, width, graphTopY, graphBottomY, availableGraphHeight, rowCount, colCount, minVal, yRange);
            paintXAxis(g2, width, graphBottomY, rowCount);
            if (showYAxis) {
                paintYAxis(g2, graphTopY, graphBottomY, availableGraphHeight, minVal, maxVal);
            }
            if (showLegends) {
                paintLegend(g2, width, height, colCount);
            }
            paintHoverTooltip(g2, width, graphTopY, graphBottomY, availableGraphHeight, rowCount, colCount, minVal, yRange);

        } finally {
            g2.dispose();
        }
    }

    private void paintBars(Graphics2D g2,
                           float width,
                           float graphTopY,
                           float graphBottomY,
                           float availableGraphHeight,
                           int rowCount,
                           int colCount,
                           float minVal,
                           float yRange) {

        float plotWidth = width - (2f * DEFAULT_PADDING);
        if (rowCount <= 0 || colCount <= 1 || plotWidth <= 0f) {
            return;
        }

        float categorySlot = plotWidth / rowCount;
        float categoryGap = categorySlot * categoryGapRatio;
        float categoryUsable = Math.max(1f, categorySlot - categoryGap);
        float categoryStartOffset = (categorySlot - categoryUsable) / 2f;

        int seriesCount = colCount - 1;
        float scaleRatio = availableGraphHeight / yRange;

        for (int row = 0; row < rowCount; row++) {
            float slotX = DEFAULT_PADDING + (row * categorySlot);
            float catX = slotX + categoryStartOffset;

            if (chartMode == MODE_STACKED) {
                paintStackedCategory(g2, row, seriesCount, catX, categoryUsable, graphBottomY, minVal, scaleRatio);
            } else {
                paintGroupedCategory(g2, row, seriesCount, catX, categoryUsable, graphBottomY, minVal, scaleRatio);
            }
        }

        paintBarLabels(g2, rowCount, seriesCount, plotWidth, graphBottomY, minVal, scaleRatio);
    }

    private void paintGroupedCategory(Graphics2D g2,
                                      int row,
                                      int seriesCount,
                                      float catX,
                                      float categoryUsable,
                                      float graphBottomY,
                                      float minVal,
                                      float scaleRatio) {

        float barGap = categoryUsable * barGapRatio;
        float totalGap = Math.max(0, seriesCount - 1) * barGap;
        float barWidth = seriesCount > 0 ? Math.max(1f, (categoryUsable - totalGap) / seriesCount) : categoryUsable;

        for (int s = 0; s < seriesCount; s++) {
            float x = catX + (s * (barWidth + barGap));
            double val = getNumericValue(row, s + 1, minVal);

            float topY = graphBottomY - (float) ((val - minVal) * scaleRatio);
            float h = graphBottomY - topY;
            if (h < 0f) h = 0f;

            Color fill = getSeriesColor(s, "barColor", getSeriesColor(s, "lineColor", Color.BLUE));
            Color border = getSeriesColor(s, "borderColor", fill.darker());
            float stroke = getSeriesFloat(s, "borderWidth", 0f);

            paintSingleBar(g2, x, topY, barWidth, h, fill, border, stroke);
        }
    }

    private void paintStackedCategory(Graphics2D g2,
                                      int row,
                                      int seriesCount,
                                      float catX,
                                      float categoryUsable,
                                      float graphBottomY,
                                      float minVal,
                                      float scaleRatio) {

        float currentTop = graphBottomY;

        for (int s = seriesCount - 1; s >= 0; s--) {
            double val = getNumericValue(row, s + 1, minVal);
            float h = (float) ((val - minVal) * scaleRatio);
            if (h < 0f) h = 0f;

            float y = currentTop - h;

            Color fill = getSeriesColor(s, "barColor", getSeriesColor(s, "lineColor", Color.BLUE));
            Color border = getSeriesColor(s, "borderColor", fill.darker());
            float stroke = getSeriesFloat(s, "borderWidth", 0f);

            paintSingleBar(g2, catX, y, categoryUsable, h, fill, border, stroke);
            currentTop = y;
        }
    }

    private void paintSingleBar(Graphics2D g2,
                                float x,
                                float y,
                                float w,
                                float h,
                                Color fill,
                                Color border,
                                float strokeWidth) {

        if (w <= 0f || h <= 0f) {
            return;
        }

        int arc = Math.max(0, cornerRadius);
        Shape shape;
        if (arc > 0) {
            shape = new java.awt.geom.RoundRectangle2D.Float(x, y, w, h, arc, arc);
        } else {
            shape = new Rectangle2D.Float(x, y, w, h);
        }

        g2.setColor(fill != null ? fill : Color.BLUE);
        g2.fill(shape);

        if (strokeWidth > 0f) {
            g2.setColor(border != null ? border : Color.GRAY);
            g2.setStroke(new BasicStroke(strokeWidth));
            g2.draw(shape);
        }
    }

    private void paintBarLabels(Graphics2D g2,
                                int rowCount,
                                int seriesCount,
                                float plotWidth,
                                float graphBottomY,
                                float minVal,
                                float scaleRatio) {

        g2.setFont(new Font("Dialog", Font.PLAIN, 10));
        FontMetrics fm = g2.getFontMetrics();

        float categorySlot = plotWidth / rowCount;
        float categoryGap = categorySlot * categoryGapRatio;
        float categoryUsable = Math.max(1f, categorySlot - categoryGap);
        float categoryStartOffset = (categorySlot - categoryUsable) / 2f;

        for (int row = 0; row < rowCount; row++) {
            if (chartMode == MODE_STACKED) {
                float runningTop = graphBottomY;
                for (int s = seriesCount - 1; s >= 0; s--) {
                    boolean showLabel = getSeriesBoolean(s, "showLabels", true);
                    if (!showLabel) continue;

                    double val = getNumericValue(row, s + 1, minVal);
                    float h = (float) ((val - minVal) * scaleRatio);
                    if (h < 14f) {
                        runningTop -= h;
                        continue;
                    }

                    float centerY = runningTop - (h / 2f);
                    String text = formatLabelValue(val);
                    int textW = fm.stringWidth(text);
                    float centerX = DEFAULT_PADDING + (row * categorySlot) + categoryStartOffset + (categoryUsable / 2f);

                    g2.setColor(new Color(45, 45, 45));
                    g2.drawString(text, Math.round(centerX - (textW / 2f)), Math.round(centerY + (fm.getAscent() / 2f) - 2f));

                    runningTop -= h;
                }
            } else {
                float barGap = categoryUsable * barGapRatio;
                float totalGap = Math.max(0, seriesCount - 1) * barGap;
                float barWidth = seriesCount > 0 ? Math.max(1f, (categoryUsable - totalGap) / seriesCount) : categoryUsable;

                for (int s = 0; s < seriesCount; s++) {
                    boolean showLabel = getSeriesBoolean(s, "showLabels", true);
                    if (!showLabel) continue;

                    double val = getNumericValue(row, s + 1, minVal);
                    float topY = graphBottomY - (float) ((val - minVal) * scaleRatio);
                    float h = graphBottomY - topY;
                    if (h < 14f) continue;

                    float x = DEFAULT_PADDING + (row * categorySlot) + categoryStartOffset + (s * (barWidth + barGap));
                    String text = formatLabelValue(val);
                    int textW = fm.stringWidth(text);

                    float tx = x + (barWidth / 2f) - (textW / 2f);
                    float ty = topY + (h / 2f) + (fm.getAscent() / 2f) - 2f;

                    g2.setColor(new Color(45, 45, 45));
                    g2.drawString(text, Math.round(tx), Math.round(ty));
                }
            }
        }
    }

    private void paintXAxis(Graphics2D g2, float width, float graphBottomY, int rowCount) {
        g2.setColor(axisLineColor);
        g2.setStroke(new BasicStroke(axisLineWidth));
        float axisY = graphBottomY + xAxisGap;
        float originX = DEFAULT_PADDING - xAxisGap;

        g2.drawLine(Math.round(originX), Math.round(axisY), Math.round(width - originX), Math.round(axisY));

        float plotWidth = width - (2f * DEFAULT_PADDING);
        float categorySlot = rowCount > 0 ? plotWidth / rowCount : 0f;
        int labelStep = Math.max(1, Math.round(rowCount / (float) TARGET_LABEL_COUNT));

        g2.setFont(new Font("Dialog", Font.PLAIN, 10));
        FontMetrics fm = g2.getFontMetrics();

        for (int row = 0; row < rowCount; row++) {
            float px = DEFAULT_PADDING + (row * categorySlot) + (categorySlot / 2f);
            if (row % labelStep == 0) {
                g2.drawLine(Math.round(px), Math.round(axisY), Math.round(px), Math.round(axisY + 6f));
                if (showAxisLabels) {
                    Object xValue = data.getValueAt(row, 0);
                    String text = xValue != null ? String.valueOf(xValue) : "";
                    int strW = fm.stringWidth(text);
                    g2.setColor(getForeground());
                    g2.drawString(text, Math.round(px - strW / 2f), Math.round(axisY + 18f));
                    g2.setColor(axisLineColor);
                }
            } else {
                g2.drawLine(Math.round(px), Math.round(axisY), Math.round(px), Math.round(axisY + 3f));
            }
        }
    }

    private void paintYAxis(Graphics2D g2,
                            float graphTopY,
                            float graphBottomY,
                            float availableGraphHeight,
                            float lower,
                            float upper) {

        g2.setColor(axisLineColor);
        g2.setStroke(new BasicStroke(axisLineWidth));

        float axisX = DEFAULT_PADDING - xAxisGap;
        g2.drawLine(Math.round(axisX), Math.round(graphTopY), Math.round(axisX), Math.round(graphBottomY + xAxisGap));

        int tickCount = 5;
        g2.setFont(new Font("Dialog", Font.PLAIN, 10));
        FontMetrics fm = g2.getFontMetrics();

        for (int i = 0; i <= tickCount; i++) {
            float pct = i / (float) tickCount;
            float y = graphBottomY - (pct * availableGraphHeight);
            float value = lower + ((upper - lower) * pct);

            g2.drawLine(Math.round(axisX - 5f), Math.round(y), Math.round(axisX), Math.round(y));

            if (showAxisLabels) {
                String label = formatAxisValue(value);
                int sw = fm.stringWidth(label);
                g2.setColor(getForeground());
                g2.drawString(label, Math.round(axisX - sw - 8f), Math.round(y + (fm.getAscent() / 2f) - 2f));
                g2.setColor(axisLineColor);
            }
        }
    }

    private void paintLegend(Graphics2D g2, float width, float height, int colCount) {
        List<LegendItem> items = new ArrayList<LegendItem>();

        g2.setFont(new Font("Dialog", Font.PLAIN, 11));
        FontMetrics fm = g2.getFontMetrics();

        for (int col = 1; col < colCount; col++) {
            int seriesRow = col - 1;
            String label = capitalize(data.getColumnName(col));
            Color color = getSeriesColor(seriesRow, "barColor", getSeriesColor(seriesRow, "lineColor", Color.BLUE));
            int itemWidth = 14 + fm.stringWidth(label) + 18;
            items.add(new LegendItem(label, color, itemWidth));
        }

        int totalLegendWidth = 0;
        for (LegendItem item : items) {
            totalLegendWidth += item.width;
        }

        float startX = (width - totalLegendWidth) / 2f;
        float legendY = height - 10f;

        for (LegendItem item : items) {
            g2.setColor(item.color);
            g2.fillOval(Math.round(startX), Math.round(legendY - 8f), 8, 8);

            g2.setColor(getForeground());
            g2.drawString(item.label, Math.round(startX + 14f), Math.round(legendY));
            startX += item.width;
        }
    }

    private void paintHoverTooltip(Graphics2D g2,
                                   float width,
                                   float graphTopY,
                                   float graphBottomY,
                                   float availableGraphHeight,
                                   int rowCount,
                                   int colCount,
                                   float minVal,
                                   float yRange) {

        int idx = hoverIndex;
        if (idx < 0 || idx >= rowCount) {
            return;
        }

        float plotWidth = width - (2f * DEFAULT_PADDING);
        float categorySlot = plotWidth / rowCount;
        float px = DEFAULT_PADDING + (idx * categorySlot) + (categorySlot / 2f);
        float axisY = graphBottomY + xAxisGap;

        g2.setColor(new Color(200, 200, 200));
        g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5f}, 0));
        g2.drawLine(Math.round(px), Math.round(graphTopY), Math.round(px), Math.round(axisY));

        String dateLabel = String.valueOf(data.getValueAt(idx, 0));
        List<TooltipRow> rows = new ArrayList<TooltipRow>();

        for (int col = 1; col < colCount; col++) {
            int seriesRow = col - 1;
            double val = getNumericValue(idx, col, minVal);
            String columnName = data.getColumnName(col);
            Color barColor = getSeriesColor(seriesRow, "barColor", getSeriesColor(seriesRow, "lineColor", Color.BLUE));
            boolean showToolTip = getSeriesBoolean(seriesRow, "showToolTip", true);

            if (showToolTip) {
                rows.add(new TooltipRow(barColor, capitalize(columnName), val));
            }

            if (chartMode == MODE_STACKED) {
                float stackTotalBefore = 0f;
                for (int s = colCount - 1; s > col; s--) {
                    stackTotalBefore += (float) getNumericValue(idx, s, minVal);
                }

                float stackTopVal = stackTotalBefore + (float) val;
                float topY = graphBottomY - (((stackTopVal - minVal) / yRange) * availableGraphHeight);
                float bottomY = graphBottomY - (((stackTotalBefore - minVal) / yRange) * availableGraphHeight);
                float cy = topY + ((bottomY - topY) / 2f);

                g2.setColor(Color.WHITE);
                g2.fillOval(Math.round(px) - 4, Math.round(cy) - 4, 8, 8);
                g2.setColor(barColor);
                g2.setStroke(new BasicStroke(1f));
                g2.drawOval(Math.round(px) - 4, Math.round(cy) - 4, 8, 8);
            }
        }

        if (rows.isEmpty()) {
            return;
        }

        Font headerFont = new Font("Dialog", Font.BOLD, 11);
        Font listFont = new Font("Dialog", Font.PLAIN, 11);

        g2.setFont(headerFont);
        FontMetrics fmHeader = g2.getFontMetrics();
        int headerW = fmHeader.stringWidth(dateLabel) + 20;

        g2.setFont(listFont);
        FontMetrics fmList = g2.getFontMetrics();
        int maxRowW = 0;
        for (TooltipRow row : rows) {
            String txt = row.label + ": " + formatTooltipValue(row.value);
            int w = 10 + 5 + fmList.stringWidth(txt) + 20;
            if (w > maxRowW) {
                maxRowW = w;
            }
        }

        int boxW = Math.max(headerW, maxRowW);
        int lineH = 16;
        int boxH = 25 + (rows.size() * lineH) + 5;

        float tipX = px + 15f;
        if (tipX + boxW > width) {
            tipX = px - boxW - 15f;
        }
        float tipY = graphTopY + 10f;

        g2.setColor(new Color(0, 0, 0, 40));
        g2.fillRoundRect(Math.round(tipX) + 2, Math.round(tipY) + 2, boxW, boxH, 5, 5);

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(Math.round(tipX), Math.round(tipY), boxW, boxH, 5, 5);

        g2.setColor(new Color(100, 100, 100));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(Math.round(tipX), Math.round(tipY), boxW, boxH, 5, 5);

        g2.setColor(Color.BLACK);
        g2.setFont(headerFont);
        g2.drawString(dateLabel, Math.round(tipX + 10f), Math.round(tipY + 18f));

        g2.setColor(new Color(220, 220, 220));
        g2.drawLine(Math.round(tipX), Math.round(tipY + 24f), Math.round(tipX + boxW), Math.round(tipY + 24f));

        g2.setFont(listFont);
        int y = Math.round(tipY + 40f);
        for (TooltipRow row : rows) {
            g2.setColor(row.color);
            g2.fillOval(Math.round(tipX + 10f), y - 8, 6, 6);

            g2.setColor(new Color(55, 55, 55));
            g2.drawString(row.label + ": " + formatTooltipValue(row.value), Math.round(tipX + 22f), y);
            y += lineH;
        }
    }

    // ---------------------------------------------------------------------
    // Dataset + calculations
    // ---------------------------------------------------------------------

    private int getRowCount() {
        return data != null ? data.getRowCount() : 0;
    }

    private boolean hasUsableData() {
        return data != null && data.getRowCount() > 0 && data.getColumnCount() > 1;
    }

    private void clampInteractionIndexes() {
        int max = getRowCount() - 1;
        if (hoverIndex > max) hoverIndex = -1;
        if (selectedIndex > max) selectedIndex = -1;
    }

    private Range computeRange() {
        if (!autoRange) {
            float low = lowerBound;
            float high = upperBound;
            if (Float.compare(low, high) == 0) {
                high = low + 1f;
            }
            if (high < low) {
                float t = low;
                low = high;
                high = t;
            }
            return new Range(low, high);
        }

        float min = 0f;
        float max = Float.MIN_VALUE;

        if (chartMode == MODE_STACKED) {
            for (int row = 0; row < data.getRowCount(); row++) {
                float sum = 0f;
                for (int col = 1; col < data.getColumnCount(); col++) {
                    sum += (float) getNumericValue(row, col, 0d);
                }
                if (sum > max) {
                    max = sum;
                }
            }
        } else {
            for (int row = 0; row < data.getRowCount(); row++) {
                for (int col = 1; col < data.getColumnCount(); col++) {
                    double val = getNumericValue(row, col, 0d);
                    if (val > max) {
                        max = (float) val;
                    }
                }
            }
        }

        if (max <= min) {
            max = min + 1f;
        } else {
            max *= 1.08f;
        }

        return new Range(min, max);
    }

    private double getNumericValue(int row, int col, double fallback) {
        if (data == null || row < 0 || row >= data.getRowCount() || col < 0 || col >= data.getColumnCount()) {
            return fallback;
        }
        try {
            Object value = data.getValueAt(row, col);
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            if (value != null) {
                return Double.parseDouble(String.valueOf(value));
            }
        } catch (Exception ignored) {
        }
        return fallback;
    }

    private int findSeriesPropertyColumn(String name) {
        if (seriesProperties == null || name == null) return -1;

        for (int i = 0; i < seriesProperties.getColumnCount(); i++) {
            String col = seriesProperties.getColumnName(i);
            if (col != null && col.trim().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    private Color getSeriesColor(int row, String columnName, Color fallback) {
        if (seriesProperties == null || row < 0 || row >= seriesProperties.getRowCount()) {
            return fallback;
        }
        int col = findSeriesPropertyColumn(columnName);
        if (col < 0) return fallback;

        try {
            Object value = seriesProperties.getValueAt(row, col);
            if (value instanceof Color) {
                return (Color) value;
            }
        } catch (Exception ignored) {
        }
        return fallback;
    }

    private float getSeriesFloat(int row, String columnName, float fallback) {
        if (seriesProperties == null || row < 0 || row >= seriesProperties.getRowCount()) {
            return fallback;
        }
        int col = findSeriesPropertyColumn(columnName);
        if (col < 0) return fallback;

        try {
            Object value = seriesProperties.getValueAt(row, col);
            if (value instanceof Number) {
                return ((Number) value).floatValue();
            }
            if (value != null) {
                return Float.parseFloat(String.valueOf(value));
            }
        } catch (Exception ignored) {
        }
        return fallback;
    }

    private boolean getSeriesBoolean(int row, String columnName, boolean fallback) {
        if (seriesProperties == null || row < 0 || row >= seriesProperties.getRowCount()) {
            return fallback;
        }
        int col = findSeriesPropertyColumn(columnName);
        if (col < 0) return fallback;

        try {
            Object value = seriesProperties.getValueAt(row, col);
            if (value instanceof Boolean) {
                return (Boolean) value;
            }
            if (value != null) {
                return Boolean.parseBoolean(String.valueOf(value));
            }
        } catch (Exception ignored) {
        }
        return fallback;
    }

    private static Dataset createDefaultData() {
        String[] columnNames = new String[]{"date", "running", "faulted", "plannedDowntime", "nonproduction"};
        Class<?>[] columnTypes = new Class<?>[]{String.class, Double.class, Double.class, Double.class, Double.class};

        Object[][] rows = new Object[][]{
                {"Mar 20", 0.0, 0.0, 100.0, 0.0},
                {"Mar 21", 37.0, 14.0, 49.0, 0.0},
                {"Mar 22", 23.0, 75.0, 2.0, 0.0},
                {"Mar 23", 28.0, 72.0, 0.0, 0.0}
        };

        return buildDataset(columnNames, columnTypes, rows);
    }

    private static Dataset createDefaultSeriesProperties() {
        String[] columnNames = new String[]{"barColor", "borderColor", "borderWidth", "showLabels", "showToolTip"};
        Class<?>[] columnTypes = new Class<?>[]{Color.class, Color.class, Float.class, Boolean.class, Boolean.class};

        Object[][] rows = new Object[][]{
                {new Color(87, 181, 80), new Color(87, 181, 80).darker(), 0f, true, true},
                {new Color(209, 77, 89), new Color(209, 77, 89).darker(), 0f, true, true},
                {new Color(88, 133, 233), new Color(88, 133, 233).darker(), 0f, true, true},
                {new Color(155, 143, 255), new Color(155, 143, 255).darker(), 0f, true, true}
        };

        return buildDataset(columnNames, columnTypes, rows);
    }

    private static Dataset buildDataset(String[] columnNames, Class<?>[] columnTypes, Object[][] rowMajorData) {
        int rowCount = rowMajorData.length;
        int colCount = columnNames.length;

        Object[][] columnMajor = new Object[colCount][rowCount];
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                columnMajor[c][r] = rowMajorData[r][c];
            }
        }

        return new BasicDataset(columnNames, columnTypes, columnMajor);
    }

    // ---------------------------------------------------------------------
    // Mouse interaction
    // ---------------------------------------------------------------------

    private int indexAtPoint(Point p) {
        if (p == null || !hasUsableData()) {
            return -1;
        }

        float width = getWidth();
        float titleHeight = getFontMetrics(getFont()).getAscent() + 15f;
        int legendHeight = showLegends ? 30 : 0;

        float graphTopY = titleHeight;
        float graphBottomY = getHeight() - DEFAULT_PADDING - xAxisGap - legendHeight;

        if (p.y < graphTopY || p.y > graphBottomY + xAxisGap + 20f) {
            return -1;
        }

        float plotWidth = width - (2f * DEFAULT_PADDING);
        int rowCount = data.getRowCount();
        if (rowCount <= 0 || plotWidth <= 0f) {
            return -1;
        }

        float categorySlot = plotWidth / rowCount;
        int idx = (int) ((p.x - DEFAULT_PADDING) / categorySlot);
        if (idx < 0 || idx >= rowCount) {
            return -1;
        }
        return idx;
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        int idx = indexAtPoint(event != null ? event.getPoint() : null);
        if (idx < 0 || idx >= getRowCount()) {
            return null;
        }
        return String.valueOf(data.getValueAt(idx, 0));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!isEnabled()) return;
        setHoverIndex(indexAtPoint(e.getPoint()));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!isEnabled()) return;
        setSelectedIndex(indexAtPoint(e.getPoint()));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (!isEnabled()) return;
        setHoverIndex(-1);
    }

    @Override public void mouseEntered(MouseEvent e) { }
    @Override public void mousePressed(MouseEvent e) { }
    @Override public void mouseReleased(MouseEvent e) { }

    // ---------------------------------------------------------------------
    // Formatting helpers
    // ---------------------------------------------------------------------

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private String capitalize(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }
        String s = value.trim();
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private String formatAxisValue(double value) {
        return String.format("%.0f", value);
    }

    private String formatTooltipValue(double value) {
        return String.format("%.1f", value);
    }

    private String formatLabelValue(double value) {
        if (Math.abs(value - Math.rint(value)) < 0.0001d) {
            return String.format("%.0f%%", value);
        }
        return String.format("%.1f%%", value);
    }

    // ---------------------------------------------------------------------
    // Inner classes
    // ---------------------------------------------------------------------

    private static class Range {
        final float lower;
        final float upper;

        Range(float lower, float upper) {
            this.lower = lower;
            this.upper = upper;
        }
    }

    private static class LegendItem {
        final String label;
        final Color color;
        final int width;

        LegendItem(String label, Color color, int width) {
            this.label = label;
            this.color = color;
            this.width = width;
        }
    }

    private static class TooltipRow {
        final Color color;
        final String label;
        final double value;

        TooltipRow(Color color, String label, double value) {
            this.color = color;
            this.label = label;
            this.value = value;
        }
    }
}