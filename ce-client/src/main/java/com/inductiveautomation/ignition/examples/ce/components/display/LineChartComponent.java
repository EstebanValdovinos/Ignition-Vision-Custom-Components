package com.inductiveautomation.ignition.examples.ce.components.display;

import com.inductiveautomation.ignition.common.BasicDataset;
import com.inductiveautomation.ignition.common.Dataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class LineChartComponent extends JComponent implements MouseListener, MouseMotionListener {

    private static final float DEFAULT_PADDING = 25f;
    private static final int TARGET_LABEL_COUNT = 10;

    private Dataset data = createDefaultData();
    private Dataset seriesProperties = createDefaultSeriesProperties();

    private String chartTitle = "Quality";
    private boolean loading = false;

    private boolean autoRange = false;
    private float lowerBound = 0f;
    private float upperBound = 100f;

    private Color axisLineColor = new Color(200, 200, 200);
    private float axisLineWidth = 1f;
    private int xAxisGap = 5;

    private boolean showAxisLabels = true;
    private boolean showYAxis = true;
    private boolean showLegends = true;

    private int hoverIndex = -1;
    private int selectedIndex = -1;

    public LineChartComponent() {
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

    public int getHoverIndex() {
        return hoverIndex;
    }

    public void setHoverIndex(int hoverIndex) {
        int old = this.hoverIndex;
        int max = getRowCount() - 1;
        if (hoverIndex < -1) {
            hoverIndex = -1;
        }
        if (hoverIndex > max) {
            hoverIndex = max;
        }
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
        if (selectedIndex < -1) {
            selectedIndex = -1;
        }
        if (selectedIndex > max) {
            selectedIndex = max;
        }
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
            // g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

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
            int titleWidth = titleMetrics.stringWidth(chartTitle != null ? chartTitle : "");
            int titleAscent = titleMetrics.getAscent();
            g2.drawString(chartTitle != null ? chartTitle : "", (int) ((width - titleWidth) / 2f), titleAscent + 5);

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

            float xStep = rowCount > 1 ? (width - (2f * DEFAULT_PADDING)) / (rowCount - 1f) : 0f;
            float scaleRatio = availableGraphHeight / yRange;

            paintFills(g2, graphTopY, graphBottomY, rowCount, colCount, xStep, scaleRatio, minVal);
            paintLinesAndMarkers(g2, graphTopY, graphBottomY, availableGraphHeight, rowCount, colCount, xStep, scaleRatio, minVal, yRange);
            paintXAxis(g2, width, graphBottomY, rowCount, xStep);
            if (showYAxis) {
                paintYAxis(g2, graphTopY, graphBottomY, availableGraphHeight, minVal, maxVal);
            }
            if (showLegends) {
                paintLegend(g2, width, height, colCount);
            }
            paintHoverTooltip(g2, width, graphTopY, graphBottomY, rowCount, colCount, xStep, scaleRatio, minVal);

        } finally {
            g2.dispose();
        }
    }

    private void paintFills(Graphics2D g2,
                            float graphTopY,
                            float graphBottomY,
                            int rowCount,
                            int colCount,
                            float xStep,
                            float scaleRatio,
                            float minVal) {

        Integer[] seriesOrder = new Integer[Math.max(0, colCount - 1)];
        for (int i = 1; i < colCount; i++) {
            seriesOrder[i - 1] = i;
        }

        Arrays.sort(seriesOrder, Comparator.comparingDouble(this::sumSeries).reversed());

        for (Integer col : seriesOrder) {
            int seriesRow = col - 1;
            Color fillTop = getSeriesColor(seriesRow, "fillColorTop", new Color(100, 100, 100, 100));
            Color fillBottom = getSeriesColor(seriesRow, "fillColorBottom", new Color(255, 255, 255, 0));

            GeneralPath path = new GeneralPath();
            path.moveTo(DEFAULT_PADDING, graphBottomY);

            for (int row = 0; row < rowCount; row++) {
                double val = getNumericValue(row, col, minVal);
                float px = DEFAULT_PADDING + (row * xStep);
                float py = graphBottomY - (float) ((val - minVal) * scaleRatio);
                path.lineTo(px, py);
            }

            path.lineTo(DEFAULT_PADDING + ((rowCount - 1) * xStep), graphBottomY);
            path.closePath();

            g2.setPaint(new GradientPaint(0, graphTopY, fillTop, 0, graphBottomY, fillBottom));
            g2.fill(path);
        }
    }

    private void paintLinesAndMarkers(Graphics2D g2,
                                      float graphTopY,
                                      float graphBottomY,
                                      float availableGraphHeight,
                                      int rowCount,
                                      int colCount,
                                      float xStep,
                                      float scaleRatio,
                                      float minVal,
                                      float yRange) {

        for (int col = 1; col < colCount; col++) {
            int seriesRow = col - 1;

            Color lineColor = getSeriesColor(seriesRow, "lineColor", Color.BLUE);
            float lineStroke = getSeriesFloat(seriesRow, "lineStroke", 1.5f);
            Color shapeColor = getSeriesColor(seriesRow, "shapeColor", Color.WHITE);
            int shapeSize = Math.max(1, Math.round(getSeriesFloat(seriesRow, "shapeSize", 2f)));
            boolean showShapes = getSeriesBoolean(seriesRow, "showShapes", true);
            boolean showLabels = getSeriesBoolean(seriesRow, "showLabels", true);
            boolean dashLine = getSeriesBoolean(seriesRow, "dashLine", false);

            GeneralPath linePath = new GeneralPath();
            double startVal = getNumericValue(0, col, minVal);
            linePath.moveTo(DEFAULT_PADDING, graphBottomY - (float) ((startVal - minVal) * scaleRatio));

            for (int row = 1; row < rowCount; row++) {
                double val = getNumericValue(row, col, minVal);
                float px = DEFAULT_PADDING + (row * xStep);
                float py = graphBottomY - (float) ((val - minVal) * scaleRatio);
                linePath.lineTo(px, py);
            }

            g2.setColor(lineColor);
            if (dashLine) {
                g2.setStroke(new BasicStroke(lineStroke, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5f}, 0));
            } else {
                g2.setStroke(new BasicStroke(lineStroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            }
            g2.draw(linePath);

            if (!showShapes && !showLabels) {
                continue;
            }

            int lastLabelEndX = -100;
            for (int row = 0; row < rowCount; row++) {
                double val = getNumericValue(row, col, minVal);
                float px = DEFAULT_PADDING + (row * xStep);
                float py = graphBottomY - (float) ((val - minVal) * scaleRatio);

                if (showShapes) {
                    int dia = shapeSize * 2;
                    g2.setColor(shapeColor);
                    g2.fillOval(Math.round(px) - shapeSize, Math.round(py) - shapeSize, dia, dia);
                    g2.setColor(lineColor);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawOval(Math.round(px) - shapeSize, Math.round(py) - shapeSize, dia, dia);
                }

                if (showLabels && (row > 0 || !showYAxis)) {
                    g2.setColor(getForeground());
                    g2.setFont(new Font("Dialog", Font.PLAIN, 10));
                    FontMetrics fm = g2.getFontMetrics();
                    String label = String.format("%.1f%%", val);

                    float labelPy = graphBottomY - (float) (((val - minVal) / yRange) * availableGraphHeight);
                    int textWidth = fm.stringWidth(label);
                    int textHeight = fm.getAscent();
                    int labelStartX = Math.round(px - (textWidth / 2f));

                    if (labelStartX > lastLabelEndX + 5) {
                        int labelY = Math.round(labelPy - 10f);
                        if (labelY < graphTopY + textHeight) {
                            labelY = Math.round(labelPy + textHeight + 10f);
                        }
                        g2.drawString(label, labelStartX, labelY);
                        lastLabelEndX = labelStartX + textWidth;
                    }
                }
            }
        }
    }

    private void paintXAxis(Graphics2D g2, float width, float graphBottomY, int rowCount, float xStep) {
        g2.setColor(axisLineColor);
        g2.setStroke(new BasicStroke(axisLineWidth));
        float axisY = graphBottomY + xAxisGap;
        float originX = DEFAULT_PADDING - xAxisGap;

        g2.drawLine(Math.round(originX), Math.round(axisY), Math.round(width - originX), Math.round(axisY));

        int labelStep = Math.max(1, Math.round(rowCount / (float) TARGET_LABEL_COUNT));
        g2.setFont(new Font("Dialog", Font.PLAIN, 10));
        FontMetrics fm = g2.getFontMetrics();

        for (int row = 0; row < rowCount; row++) {
            float px = DEFAULT_PADDING + (row * xStep);
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
        float axisY = graphBottomY + xAxisGap;
        float originX = DEFAULT_PADDING - xAxisGap;
        g2.drawLine(Math.round(originX), Math.round(graphTopY), Math.round(originX), Math.round(axisY));

        g2.setFont(new Font("Dialog", Font.PLAIN, 10));
        FontMetrics fm = g2.getFontMetrics();

        int labelHeightSpace = fm.getHeight() + 12;
        int maxTicks = Math.max(1, (int) (availableGraphHeight / labelHeightSpace));
        int yTickCount = Math.max(2, Math.min(10, maxTicks));

        float yRange = upper - lower;
        if (yRange == 0f) {
            yRange = 1f;
        }
        float yTickStep = yRange / yTickCount;

        for (int i = 0; i <= yTickCount; i++) {
            float val = lower + (i * yTickStep);
            float py = graphBottomY - (i * (availableGraphHeight / yTickCount));

            g2.drawLine(Math.round(originX), Math.round(py), Math.round(originX - 3f), Math.round(py));

            if (showAxisLabels) {
                String label = Math.abs(val - Math.round(val)) < 0.0001f
                        ? String.valueOf(Math.round(val))
                        : String.format("%.1f", val);
                int labelW = fm.stringWidth(label);
                g2.setColor(getForeground());
                g2.drawString(label, Math.round(DEFAULT_PADDING - labelW - 5f), Math.round(py + 4f));
                g2.setColor(axisLineColor);
            }
        }
    }

    private void paintLegend(Graphics2D g2, float width, float height, int colCount) {
        g2.setFont(new Font("Dialog", Font.PLAIN, 11));
        FontMetrics fm = g2.getFontMetrics();

        List<LegendItem> items = new ArrayList<LegendItem>();
        int totalLegendWidth = 0;

        for (int col = 1; col < colCount; col++) {
            int seriesRow = col - 1;
            Color lineColor = getSeriesColor(seriesRow, "lineColor", Color.BLUE);
            String name = data.getColumnName(col);
            String displayName = name != null ? capitalize(name) : "Series " + col;
            int itemWidth = 10 + 5 + fm.stringWidth(displayName) + 15;
            totalLegendWidth += itemWidth;
            items.add(new LegendItem(lineColor, displayName, itemWidth));
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
                                   int rowCount,
                                   int colCount,
                                   float xStep,
                                   float scaleRatio,
                                   float minVal) {

        int idx = hoverIndex;
        if (idx < 0 || idx >= rowCount) {
            return;
        }

        float px = DEFAULT_PADDING + (idx * xStep);
        float axisY = graphBottomY + xAxisGap;

        g2.setColor(new Color(200, 200, 200));
        g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5f}, 0));
        g2.drawLine(Math.round(px), Math.round(graphTopY), Math.round(px), Math.round(axisY));

        String dateLabel = String.valueOf(data.getValueAt(idx, 0));
        List<TooltipRow> rows = new ArrayList<TooltipRow>();
        int hoverShapeSize = 4;

        for (int col = 1; col < colCount; col++) {
            int seriesRow = col - 1;
            double val = getNumericValue(idx, col, minVal);
            String columnName = data.getColumnName(col);
            Color lineColor = getSeriesColor(seriesRow, "lineColor", Color.BLUE);
            boolean showToolTip = getSeriesBoolean(seriesRow, "showToolTip", true);

            if (showToolTip) {
                rows.add(new TooltipRow(lineColor, capitalize(columnName), val));
            }

            float py = graphBottomY - (float) ((val - minVal) * scaleRatio);
            g2.setColor(Color.WHITE);
            g2.fillOval(Math.round(px) - hoverShapeSize, Math.round(py) - hoverShapeSize, hoverShapeSize * 2, hoverShapeSize * 2);
            g2.setColor(lineColor);
            g2.setStroke(new BasicStroke(1f));
            g2.drawOval(Math.round(px) - hoverShapeSize, Math.round(py) - hoverShapeSize, hoverShapeSize * 2, hoverShapeSize * 2);
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
            String txt = row.label + ": " + String.format("%.1f%%", row.value);
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
        float currY = tipY + 40f;
        for (TooltipRow row : rows) {
            String txt = row.label + ": " + String.format("%.1f%%", row.value);
            g2.setColor(row.color);
            g2.fillOval(Math.round(tipX + 10f), Math.round(currY - 8f), 6, 6);
            g2.setColor(Color.DARK_GRAY);
            g2.drawString(txt, Math.round(tipX + 22f), Math.round(currY));
            currY += lineH;
        }
    }

    // ---------------------------------------------------------------------
    // Mouse interaction
    // ---------------------------------------------------------------------

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!isEnabled()) {
            return;
        }
        setSelectedIndex(hoverIndex);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!isEnabled() || !hasUsableData()) {
            return;
        }

        int rowCount = data.getRowCount();
        float width = getWidth();
        float xStep = rowCount > 1 ? (width - (2f * DEFAULT_PADDING)) / (rowCount - 1f) : 0f;
        float mouseX = e.getX() - DEFAULT_PADDING;

        int idx;
        if (xStep > 0f) {
            idx = Math.round(mouseX / xStep);
        } else {
            idx = 0;
        }

        if (idx < 0) {
            idx = 0;
        }
        if (idx >= rowCount) {
            idx = rowCount - 1;
        }

        if (hoverIndex != idx) {
            setHoverIndex(idx);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setHoverIndex(-1);
    }

    @Override public void mouseEntered(MouseEvent e) { }
    @Override public void mousePressed(MouseEvent e) { }
    @Override public void mouseReleased(MouseEvent e) { }
    @Override public void mouseDragged(MouseEvent e) { mouseMoved(e); }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private boolean hasUsableData() {
        return data != null && data.getRowCount() > 1 && data.getColumnCount() > 1;
    }

    private int getRowCount() {
        return data != null ? data.getRowCount() : 0;
    }

    private void clampInteractionIndexes() {
        int max = getRowCount() - 1;
        if (hoverIndex > max) {
            hoverIndex = max;
        }
        if (selectedIndex > max) {
            selectedIndex = max;
        }
        if (max < 0) {
            hoverIndex = -1;
            selectedIndex = -1;
        }
    }

    private Range computeRange() {
        if (!autoRange || data == null) {
            float lower = lowerBound;
            float upper = upperBound;
            if (upper <= lower) {
                upper = lower + 1f;
            }
            return new Range(lower, upper);
        }

        double minFound = Double.POSITIVE_INFINITY;
        double maxFound = Double.NEGATIVE_INFINITY;
        boolean hasData = false;

        for (int col = 1; col < data.getColumnCount(); col++) {
            for (int row = 0; row < data.getRowCount(); row++) {
                Object value = data.getValueAt(row, col);
                Double num = toDouble(value);
                if (num != null) {
                    minFound = Math.min(minFound, num);
                    maxFound = Math.max(maxFound, num);
                    hasData = true;
                }
            }
        }

        if (!hasData) {
            return new Range(0f, 100f);
        }

        double margin = (maxFound - minFound) * 0.05d;
        if (margin == 0d) {
            margin = 1d;
        }
        return new Range((float) (minFound - margin), (float) (maxFound + margin));
    }

    private double sumSeries(int col) {
        double total = 0d;
        if (data == null) {
            return total;
        }
        for (int row = 0; row < data.getRowCount(); row++) {
            Double value = toDouble(data.getValueAt(row, col));
            if (value != null) {
                total += value;
            }
        }
        return total;
    }

    private double getNumericValue(int row, int col, float fallback) {
        if (data == null) {
            return fallback;
        }
        Double value = toDouble(data.getValueAt(row, col));
        return value != null ? value : fallback;
    }

    private Double toDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value != null) {
            try {
                return Double.parseDouble(String.valueOf(value));
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private int findSeriesColumn(String name) {
        if (seriesProperties == null || name == null) {
            return -1;
        }
        for (int i = 0; i < seriesProperties.getColumnCount(); i++) {
            String col = seriesProperties.getColumnName(i);
            if (col != null && col.trim().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    private Object getSeriesValue(int row, String columnName) {
        if (seriesProperties == null || row < 0 || row >= seriesProperties.getRowCount()) {
            return null;
        }
        int col = findSeriesColumn(columnName);
        if (col < 0 || col >= seriesProperties.getColumnCount()) {
            return null;
        }
        try {
            return seriesProperties.getValueAt(row, col);
        } catch (Exception ignored) {
            return null;
        }
    }

    private Color getSeriesColor(int row, String columnName, Color fallback) {
        Object value = getSeriesValue(row, columnName);
        return value instanceof Color ? (Color) value : fallback;
    }

    private float getSeriesFloat(int row, String columnName, float fallback) {
        Object value = getSeriesValue(row, columnName);
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        if (value != null) {
            try {
                return Float.parseFloat(String.valueOf(value));
            } catch (Exception ignored) {
            }
        }
        return fallback;
    }

    private boolean getSeriesBoolean(int row, String columnName, boolean fallback) {
        Object value = getSeriesValue(row, columnName);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value != null) {
            String s = String.valueOf(value).trim();
            if ("true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s)) {
                return Boolean.parseBoolean(s);
            }
        }
        return fallback;
    }

    private String capitalize(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
        String trimmed = text.trim();
        return Character.toUpperCase(trimmed.charAt(0)) + trimmed.substring(1);
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        return null;
    }

    private static Dataset createDefaultData() {
        String[] columnNames = new String[]{"date", "quality"};
        Class<?>[] columnTypes = new Class<?>[]{String.class, Double.class};
        Object[][] data = new Object[][]{
                {
                        "Mar 20",
                        "Mar 21",
                        "Mar 22",
                        "Mar 23",
                        "Mar 24",
                        "Mar 25"
                },
                {
                        85.2,
                        93.0,
                        92.4,
                        84.3,
                        75.2,
                        64.8
                }
        };

        return new BasicDataset(columnNames, columnTypes, data);
    }

    private static Dataset createDefaultSeriesProperties() {
        String[] columnNames = new String[]{
                "lineColor",
                "lineStroke",
                "shapeColor",
                "shapeSize",
                "showShapes",
                "showLabels",
                "dashLine",
                "fillColorTop",
                "fillColorBottom",
                "showToolTip"
        };
        Class<?>[] columnTypes = new Class<?>[]{
                Color.class,
                Float.class,
                Color.class,
                Integer.class,
                Boolean.class,
                Boolean.class,
                Boolean.class,
                Color.class,
                Color.class,
                Boolean.class
        };
        Object[][] data = new Object[][]{
                {
                    new Color(0, 123, 255)
                },
                {
                    1.5f
                },
                {
                    Color.WHITE
                },
                {
                    3
                },
                {
                    true
                },
                {
                    true
                },
                {
                    false
                },
                {
                    new Color(0, 123, 255, 140)
                },
                {
                    new Color(255, 255, 255, 0)
                },
                {
                    true
                }
        };
        return new BasicDataset(columnNames, columnTypes, data);
    }

    private static class Range {
        final float lower;
        final float upper;

        Range(float lower, float upper) {
            this.lower = lower;
            this.upper = upper;
        }
    }

    private static class LegendItem {
        final Color color;
        final String label;
        final int width;

        LegendItem(Color color, String label, int width) {
            this.color = color;
            this.label = label;
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
