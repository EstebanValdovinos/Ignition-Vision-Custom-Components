package com.inductiveautomation.ignition.examples.ce.components.display;

import com.inductiveautomation.ignition.common.BasicDataset;
import com.inductiveautomation.ignition.common.Dataset;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class PieDonutChartComponent extends JComponent {

    public static final int CHART_PIE = 0;
    public static final int CHART_DONUT = 1;
    public static final int CHART_RINGS = 2;

    public static final int RINGS_VERTICAL = 0;
    public static final int RINGS_HORIZONTAL = 1;

    private Dataset data = createDefaultDataset();

    private int chartType = CHART_DONUT;
    private String chartTitle = "Property Distribution";
    private String subtitle = "By Service Provided";
    private String units = "%";
    private int holeSize = 55;

    private boolean showLabels = true;
    private boolean showPercentages = true;
    private float strokeWidth = 14f;
    private Color trackColor = new Color(220, 220, 220);

    private int ringsAlignment = RINGS_HORIZONTAL;
    private int ringGap = 12;
    private int chartTopGap = 20;

    private float borderStroke = 1f;
    private Color borderColor = new Color(170, 170, 170);
    private int borderRadius = 10;

    private Font titleFont;
    private Font subtitleFont;
    private Font labelFont;
    private Font valueFont;

    private Color titleColor;
    private Color subtitleColor;
    private Color labelColor;
    private Color valueColor;

    public PieDonutChartComponent() {
        setPreferredSize(new Dimension(500, 385));
        setMinimumSize(new Dimension(180, 140));

        super.setBackground(new Color(245, 245, 245));
        setOpaque(true);

        initializeStyleDefaults();
    }

    // ------------------------
    // Properties
    // ------------------------

    public Dataset getData() {
        return data;
    }

    public void setData(Dataset data) {
        Dataset old = this.data;
        this.data = (data != null) ? data : createDefaultDataset();
        firePropertyChange("data", old, this.data);
        repaint();
    }

    public int getChartType() {
        return chartType;
    }

    public void setChartType(int chartType) {
        int old = this.chartType;
        if (chartType != CHART_PIE && chartType != CHART_DONUT && chartType != CHART_RINGS) {
            chartType = CHART_DONUT;
        }
        this.chartType = chartType;
        firePropertyChange("chartType", old, this.chartType);
        repaint();
    }

    public String getChartTitle() {
        return chartTitle;
    }

    public void setChartTitle(String chartTitle) {
        String old = this.chartTitle;
        this.chartTitle = chartTitle;
        firePropertyChange("chartTitle", old, this.chartTitle);
        repaint();
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        String old = this.subtitle;
        this.subtitle = subtitle;
        firePropertyChange("subtitle", old, this.subtitle);
        repaint();
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        String old = this.units;
        this.units = units;
        firePropertyChange("units", old, this.units);
        repaint();
    }

    public int getHoleSize() {
        return holeSize;
    }

    public void setHoleSize(int holeSize) {
        int old = this.holeSize;
        this.holeSize = Math.max(10, Math.min(85, holeSize));
        firePropertyChange("holeSize", old, this.holeSize);
        repaint();
    }

    public boolean isShowLabels() {
        return showLabels;
    }

    public void setShowLabels(boolean showLabels) {
        boolean old = this.showLabels;
        this.showLabels = showLabels;
        firePropertyChange("showLabels", old, this.showLabels);
        repaint();
    }

    public boolean isShowPercentages() {
        return showPercentages;
    }

    public void setShowPercentages(boolean showPercentages) {
        boolean old = this.showPercentages;
        this.showPercentages = showPercentages;
        firePropertyChange("showPercentages", old, this.showPercentages);
        repaint();
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        float old = this.strokeWidth;
        this.strokeWidth = Math.max(1f, strokeWidth);
        firePropertyChange("strokeWidth", old, this.strokeWidth);
        repaint();
    }

    public Color getTrackColor() {
        return trackColor;
    }

    public void setTrackColor(Color trackColor) {
        Color old = this.trackColor;
        this.trackColor = trackColor;
        firePropertyChange("trackColor", old, this.trackColor);
        repaint();
    }

    public int getRingsAlignment() {
        return ringsAlignment;
    }

    public void setRingsAlignment(int ringsAlignment) {
        int old = this.ringsAlignment;
        if (ringsAlignment != RINGS_VERTICAL && ringsAlignment != RINGS_HORIZONTAL) {
            ringsAlignment = RINGS_HORIZONTAL;
        }
        this.ringsAlignment = ringsAlignment;
        firePropertyChange("ringsAlignment", old, this.ringsAlignment);
        repaint();
    }

    public int getRingGap() {
        return ringGap;
    }

    public void setRingGap(int ringGap) {
        int old = this.ringGap;
        this.ringGap = Math.max(0, ringGap);
        firePropertyChange("ringGap", old, this.ringGap);
        repaint();
    }

    public int getChartTopGap() {
        return chartTopGap;
    }

    public void setChartTopGap(int chartTopGap) {
        int old = this.chartTopGap;
        this.chartTopGap = Math.max(0, chartTopGap);
        firePropertyChange("chartTopGap", old, this.chartTopGap);
        repaint();
    }

    public float getBorderStroke() {
        return borderStroke;
    }

    public void setBorderStroke(float borderStroke) {
        float old = this.borderStroke;
        this.borderStroke = Math.max(0f, borderStroke);
        firePropertyChange("borderStroke", old, this.borderStroke);
        repaint();
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        Color old = this.borderColor;
        this.borderColor = borderColor != null ? borderColor : new Color(170, 170, 170);
        firePropertyChange("borderColor", old, this.borderColor);
        repaint();
    }

    public int getBorderRadius() {
        return borderRadius;
    }

    public void setBorderRadius(int borderRadius) {
        int old = this.borderRadius;
        this.borderRadius = Math.max(0, borderRadius);
        firePropertyChange("borderRadius", old, this.borderRadius);
        repaint();
    }

    public Font getTitleFont() {
        return titleFont;
    }

    public void setTitleFont(Font titleFont) {
        Font old = this.titleFont;
        this.titleFont = titleFont != null ? titleFont : deriveDefaultTitleFont();
        firePropertyChange("titleFont", old, this.titleFont);
        repaint();
    }

    public Font getSubtitleFont() {
        return subtitleFont;
    }

    public void setSubtitleFont(Font subtitleFont) {
        Font old = this.subtitleFont;
        this.subtitleFont = subtitleFont != null ? subtitleFont : deriveDefaultSubtitleFont();
        firePropertyChange("subtitleFont", old, this.subtitleFont);
        repaint();
    }

    public Font getLabelFont() {
        return labelFont;
    }

    public void setLabelFont(Font labelFont) {
        Font old = this.labelFont;
        this.labelFont = labelFont != null ? labelFont : deriveDefaultLabelFont();
        firePropertyChange("labelFont", old, this.labelFont);
        repaint();
    }

    public Font getValueFont() {
        return valueFont;
    }

    public void setValueFont(Font valueFont) {
        Font old = this.valueFont;
        this.valueFont = valueFont != null ? valueFont : deriveDefaultValueFont();
        firePropertyChange("valueFont", old, this.valueFont);
        repaint();
    }

    public Color getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(Color titleColor) {
        Color old = this.titleColor;
        this.titleColor = titleColor != null ? titleColor : deriveDefaultTitleColor();
        firePropertyChange("titleColor", old, this.titleColor);
        repaint();
    }

    public Color getSubtitleColor() {
        return subtitleColor;
    }

    public void setSubtitleColor(Color subtitleColor) {
        Color old = this.subtitleColor;
        this.subtitleColor = subtitleColor != null ? subtitleColor : deriveDefaultSubtitleColor();
        firePropertyChange("subtitleColor", old, this.subtitleColor);
        repaint();
    }

    public Color getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(Color labelColor) {
        Color old = this.labelColor;
        this.labelColor = labelColor != null ? labelColor : deriveDefaultLabelColor();
        firePropertyChange("labelColor", old, this.labelColor);
        repaint();
    }

    public Color getValueColor() {
        return valueColor;
    }

    public void setValueColor(Color valueColor) {
        Color old = this.valueColor;
        this.valueColor = valueColor != null ? valueColor : deriveDefaultValueColor();
        firePropertyChange("valueColor", old, this.valueColor);
        repaint();
    }

    @Override
    public void setBackground(Color bg) {
        Color old = getBackground();
        super.setBackground(bg);
        setOpaque(bg != null && bg.getAlpha() > 0);
        firePropertyChange("background", old, bg);
        repaint();
    }

    // ------------------------
    // Paint
    // ------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        int w = getWidth();
        int h = getHeight();

        Shape clipShape = createBackgroundShape(w, h);
        g2.setClip(clipShape);

        Color bg = getBackground();
        if (isOpaque() && bg != null && bg.getAlpha() > 0) {
            g2.setColor(bg);
            g2.fill(clipShape);
        }

        List<ChartRow> rows = extractRows();

        if (rows.isEmpty()) {
            paintEmptyState(g2, w, h);
        } else if (chartType == CHART_RINGS) {
            paintRings(g2, rows, w, h);
        } else {
            paintPieOrDonut(g2, rows, w, h);
        }

        g2.setClip(null);
        paintBorder(g2, w, h);

        g2.dispose();
    }

    private Shape createBackgroundShape(int w, int h) {
        if (borderRadius > 0) {
            return new RoundRectangle2D.Float(
                    borderStroke / 2f,
                    borderStroke / 2f,
                    w - borderStroke,
                    h - borderStroke,
                    borderRadius,
                    borderRadius
            );
        }
        return new Rectangle.Float(
                borderStroke / 2f,
                borderStroke / 2f,
                w - borderStroke,
                h - borderStroke
        );
    }

    private void paintBorder(Graphics2D g2, int w, int h) {
        if (borderStroke <= 0f) {
            return;
        }

        g2.setColor(borderColor != null ? borderColor : new Color(170, 170, 170));
        g2.setStroke(new BasicStroke(borderStroke));

        Shape borderShape;
        if (borderRadius > 0) {
            borderShape = new RoundRectangle2D.Float(
                    borderStroke / 2f,
                    borderStroke / 2f,
                    w - borderStroke,
                    h - borderStroke,
                    borderRadius,
                    borderRadius
            );
        } else {
            borderShape = new Rectangle.Float(
                    borderStroke / 2f,
                    borderStroke / 2f,
                    w - borderStroke,
                    h - borderStroke
            );
        }

        g2.draw(borderShape);
    }

    private void paintEmptyState(Graphics2D g2, int w, int h) {
        g2.setColor(getSafeLabelColor());
        g2.setFont(getSafeLabelFont());

        String msg = "No chart data";
        FontMetrics fm = g2.getFontMetrics();
        int x = (w - fm.stringWidth(msg)) / 2;
        int y = h / 2;

        g2.drawString(msg, x, y);
    }

    private void paintPieOrDonut(Graphics2D g2, List<ChartRow> rows, int w, int h) {
        int topPad = 16;
        int titleGap = 6;
        int subtitleGap = 6;
        int bottomPad = 12;
        int sidePad = 10;

        Font safeTitleFont = getSafeTitleFont();
        Font safeSubtitleFont = getSafeSubtitleFont();
        Font safeLabelFont = getSafeLabelFont();
        Font safeValueFont = getSafeValueFont();

        int headerHeight = 0;

        if (chartTitle != null && !chartTitle.trim().isEmpty()) {
            g2.setFont(safeTitleFont);
            g2.setColor(getSafeTitleColor());
            FontMetrics tfm = g2.getFontMetrics();
            String safeTitle = chartTitle.trim();
            int tx = (w - tfm.stringWidth(safeTitle)) / 2;
            int ty = topPad + tfm.getAscent();
            g2.drawString(safeTitle, tx, ty);
            headerHeight += tfm.getHeight() + titleGap;
        }

        if (subtitle != null && !subtitle.trim().isEmpty()) {
            g2.setFont(safeSubtitleFont);
            g2.setColor(getSafeSubtitleColor());
            FontMetrics sfm = g2.getFontMetrics();
            String safeSubtitle = subtitle.trim();
            int sx = (w - sfm.stringWidth(safeSubtitle)) / 2;
            int sy = topPad + headerHeight + sfm.getAscent();
            g2.drawString(safeSubtitle, sx, sy);
            headerHeight += sfm.getHeight() + subtitleGap;
        }

        int chartTop = topPad + headerHeight + chartTopGap;
        int availableW = w - (sidePad * 2);
        int availableH = h - chartTop - bottomPad;

        int diameter = Math.min(availableW, availableH) - 24;
        diameter = Math.max(60, diameter);

        float cx = w / 2f;
        float cy = chartTop + (availableH / 2f);

        float radius = diameter / 2f;
        float chartX = cx - radius;
        float chartY = cy - radius;

        double total = 0.0;
        for (ChartRow row : rows) {
            total += Math.max(0.0, row.value);
        }
        if (total <= 0.0) {
            total = 1.0;
        }

        float holeDiameter = diameter * (holeSize / 100f);
        float hx = cx - (holeDiameter / 2f);
        float hy = cy - (holeDiameter / 2f);

        float startAngle = 90f;
        for (ChartRow row : rows) {
            double safeValue = Math.max(0.0, row.value);
            float extent = (float) ((safeValue / total) * 360.0);

            Shape sliceShape;
            if (chartType == CHART_DONUT) {
                sliceShape = createDonutSlice(chartX, chartY, diameter, startAngle, -extent, hx, hy, holeDiameter);
            } else {
                sliceShape = new Arc2D.Float(chartX, chartY, diameter, diameter, startAngle, -extent, Arc2D.PIE);
            }

            g2.setColor(row.color);
            g2.fill(sliceShape);

            g2.setColor(new Color(255, 255, 255, 200));
            g2.setStroke(new BasicStroke(2f));
            g2.draw(sliceShape);

            float mid = startAngle - (extent / 2f);
            double rad = Math.toRadians(mid);

            if (showPercentages) {
                String pctText = formatPercent((safeValue / total) * 100.0);
                g2.setFont(safeValueFont);
                FontMetrics pfm = g2.getFontMetrics();
                float pctRadius = (chartType == CHART_DONUT) ? radius * 0.72f : radius * 0.60f;
                float px = (float) (cx + Math.cos(rad) * pctRadius);
                float py = (float) (cy - Math.sin(rad) * pctRadius);

                g2.setColor(getSafeValueColor());
                g2.drawString(
                        pctText,
                        px - (pfm.stringWidth(pctText) / 2f),
                        py + (pfm.getAscent() / 3f)
                );
            }

            if (showLabels) {
                float lineInner = radius + 2f;
                float lineOuter = radius + 16f;
                float x1 = (float) (cx + Math.cos(rad) * lineInner);
                float y1 = (float) (cy - Math.sin(rad) * lineInner);
                float x2 = (float) (cx + Math.cos(rad) * lineOuter);
                float y2 = (float) (cy - Math.sin(rad) * lineOuter);
                float labelArm = (Math.cos(rad) >= 0) ? 14f : -14f;
                float x3 = x2 + labelArm;
                float y3 = y2;

                g2.setColor(row.color.darker());
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(new Line2D.Float(x1, y1, x2, y2));
                g2.draw(new Line2D.Float(x2, y2, x3, y3));

                String valueText = formatValue(row.value);

                g2.setFont(safeLabelFont);
                FontMetrics lfm = g2.getFontMetrics();

                float textX = (labelArm > 0) ? x3 + 4f : x3 - 4f;
                float valueY = y3 - 2f;
                float labelY = valueY + lfm.getHeight() - 2f;

                g2.setColor(getSafeLabelColor());
                if (labelArm > 0) {
                    g2.drawString(valueText, textX, valueY);
                    g2.drawString(row.label, textX, labelY);
                } else {
                    g2.drawString(valueText, textX - lfm.stringWidth(valueText), valueY);
                    g2.drawString(row.label, textX - lfm.stringWidth(row.label), labelY);
                }
            }

            startAngle -= extent;
        }
    }

    private Shape createDonutSlice(float chartX, float chartY, float diameter,
                                   float startAngle, float extent,
                                   float holeX, float holeY, float holeDiameter) {
        Area area = new Area(new Arc2D.Float(chartX, chartY, diameter, diameter, startAngle, extent, Arc2D.PIE));
        area.subtract(new Area(new Ellipse2D.Float(holeX, holeY, holeDiameter, holeDiameter)));
        return area;
    }

    private void paintRings(Graphics2D g2, List<ChartRow> rows, int w, int h) {
        int outerPad = 12;
        int titleGap = 6;
        int subtitleGap = 6;
        int top = outerPad;

        Font safeTitleFont = getSafeTitleFont();
        Font safeSubtitleFont = getSafeSubtitleFont();

        if (chartTitle != null && !chartTitle.trim().isEmpty()) {
            g2.setColor(getSafeTitleColor());
            g2.setFont(safeTitleFont);
            FontMetrics tfm = g2.getFontMetrics();
            int tx = (w - tfm.stringWidth(chartTitle.trim())) / 2;
            int ty = top + tfm.getAscent();
            g2.drawString(chartTitle.trim(), tx, ty);
            top += tfm.getHeight() + titleGap;
        }

        if (subtitle != null && !subtitle.trim().isEmpty()) {
            g2.setColor(getSafeSubtitleColor());
            g2.setFont(safeSubtitleFont);
            FontMetrics sfm = g2.getFontMetrics();
            int sx = (w - sfm.stringWidth(subtitle.trim())) / 2;
            int sy = top + sfm.getAscent();
            g2.drawString(subtitle.trim(), sx, sy);
            top += sfm.getHeight() + subtitleGap;
        }

        top += chartTopGap;

        int availableW = w - (outerPad * 2);
        int availableH = h - top - outerPad;

        int count = Math.max(1, rows.size());
        int gap = Math.max(0, ringGap);

        if (ringsAlignment == RINGS_HORIZONTAL) {
            float totalGap = (count - 1) * gap;
            float cellW = (availableW - totalGap) / (float) count;
            float cellH = availableH;

            for (int i = 0; i < count; i++) {
                float cellX = outerPad + i * (cellW + gap);
                float cellY = top;
                paintRingCell(g2, rows.get(i), cellX, cellY, cellW, cellH);
            }
        } else {
            float totalGap = (count - 1) * gap;
            float cellW = availableW;
            float cellH = (availableH - totalGap) / (float) count;

            for (int i = 0; i < count; i++) {
                float cellX = outerPad;
                float cellY = top + i * (cellH + gap);
                paintRingCell(g2, rows.get(i), cellX, cellY, cellW, cellH);
            }
        }
    }

    private void paintRingCell(Graphics2D g2, ChartRow row, float x, float y, float w, float h) {
        float padding = 8f;
        float circleAreaH = showLabels ? h * 0.72f : h * 0.88f;
        float diameter = Math.min(w, circleAreaH) - (padding * 2f);
        diameter = Math.max(26f, diameter);

        float cx = x + (w / 2f);
        float cy = y + (circleAreaH / 2f);

        float ringStroke = Math.max(1f, Math.min(strokeWidth, diameter * 0.30f));

        float arcX = cx - (diameter / 2f) + (ringStroke / 2f);
        float arcY = cy - (diameter / 2f) + (ringStroke / 2f);
        float arcSize = diameter - ringStroke;

        double pct = Math.max(0.0, Math.min(100.0, row.value));
        float extent = (float) ((pct / 100.0) * 360.0);

        g2.setStroke(new BasicStroke(ringStroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        Color safeTrack = trackColor != null ? trackColor : new Color(220, 220, 220);
        g2.setColor(safeTrack);
        g2.draw(new Arc2D.Float(arcX, arcY, arcSize, arcSize, 90, -360, Arc2D.OPEN));

        g2.setColor(row.color);
        g2.draw(new Arc2D.Float(arcX, arcY, arcSize, arcSize, 90, -extent, Arc2D.OPEN));

        String valueText = formatValue(row.value);
        String unitsText = units != null ? units : "";

        Font ringValueFont = getSafeValueFont();
        Font ringUnitsFont = ringValueFont.deriveFont(Math.max(6f, ringValueFont.getSize2D() * 0.75f));

        FontMetrics valueFm = g2.getFontMetrics(ringValueFont);
        FontMetrics unitsFm = g2.getFontMetrics(ringUnitsFont);

        int valueWidth = valueFm.stringWidth(valueText);
        int unitsWidth = unitsText.length() > 0 ? unitsFm.stringWidth(unitsText) : 0;
        int totalTextWidth = valueWidth + unitsWidth;

        float valueX = cx - (totalTextWidth / 2f);
        float valueBaselineY = cy + ((valueFm.getAscent() - valueFm.getDescent()) / 2f);

        g2.setColor(getSafeValueColor());
        g2.setFont(ringValueFont);
        g2.drawString(valueText, valueX, valueBaselineY);

        if (unitsText.length() > 0) {
            float unitsX = valueX + valueWidth;
            float unitsBaselineY = valueBaselineY - (ringValueFont.getSize2D() * 0.08f);
            g2.setFont(ringUnitsFont);
            g2.drawString(unitsText, unitsX, unitsBaselineY);
        }

        if (showLabels) {
            String safeLabel = row.label != null ? row.label : "";
            Font ringLabelFont = getSafeLabelFont();

            g2.setFont(ringLabelFont);
            g2.setColor(getSafeLabelColor());
            FontMetrics lfm = g2.getFontMetrics();

            float labelX = cx - (lfm.stringWidth(safeLabel) / 2f);
            float labelY = y + circleAreaH + lfm.getAscent();
            g2.drawString(safeLabel, labelX, labelY);
        }
    }

    // ------------------------
    // Data helpers
    // ------------------------

    private List<ChartRow> extractRows() {
        List<ChartRow> rows = new ArrayList<ChartRow>();

        Dataset ds = data;
        if (ds == null || ds.getRowCount() <= 0 || ds.getColumnCount() <= 0) {
            return rows;
        }

        int valueCol = findColumn(ds, "value", 0);
        int labelCol = findColumn(ds, "label", 1);
        int colorCol = findColumn(ds, "color", 2);

        for (int r = 0; r < ds.getRowCount(); r++) {
            Object valueObj = safeGet(ds, r, valueCol);
            Object labelObj = safeGet(ds, r, labelCol);
            Object colorObj = safeGet(ds, r, colorCol);

            double value = toDouble(valueObj);
            String label = labelObj != null ? String.valueOf(labelObj) : "Item " + (r + 1);
            Color color = toColor(colorObj, getFallbackColor(r));

            rows.add(new ChartRow(value, label, color));
        }

        return rows;
    }

    private Object safeGet(Dataset ds, int row, int col) {
        if (col < 0 || col >= ds.getColumnCount()) {
            return null;
        }
        return ds.getValueAt(row, col);
    }

    private int findColumn(Dataset ds, String expectedName, int fallbackIndex) {
        for (int i = 0; i < ds.getColumnCount(); i++) {
            String name = ds.getColumnName(i);
            if (name != null && name.trim().equalsIgnoreCase(expectedName)) {
                return i;
            }
        }
        return Math.min(fallbackIndex, ds.getColumnCount() - 1);
    }

    private double toDouble(Object value) {
        if (value == null) {
            return 0.0;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        try {
            return Double.parseDouble(String.valueOf(value).trim());
        } catch (Exception ignored) {
            return 0.0;
        }
    }

    private Color toColor(Object value, Color fallback) {
        if (value == null) {
            return fallback;
        }

        if (value instanceof Color) {
            return (Color) value;
        }

        String s = String.valueOf(value).trim();
        if (s.length() == 0) {
            return fallback;
        }

        try {
            if (s.startsWith("#")) {
                return Color.decode(s);
            }

            if (s.startsWith("0x") || s.startsWith("0X")) {
                return Color.decode(s);
            }

            if (s.contains(",")) {
                String[] parts = s.split(",");
                if (parts.length >= 3) {
                    int r = Integer.parseInt(parts[0].trim());
                    int g = Integer.parseInt(parts[1].trim());
                    int b = Integer.parseInt(parts[2].trim());
                    int a = (parts.length >= 4) ? Integer.parseInt(parts[3].trim()) : 255;
                    return new Color(clamp255(r), clamp255(g), clamp255(b), clamp255(a));
                }
            }
        } catch (Exception ignored) {
        }

        return fallback;
    }

    private int clamp255(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private Color getFallbackColor(int index) {
        Color[] palette = new Color[]{
                new Color(83, 138, 214),
                new Color(245, 158, 56),
                new Color(76, 161, 85),
                new Color(61, 176, 214),
                new Color(220, 200, 0),
                new Color(147, 104, 205)
        };
        return palette[index % palette.length];
    }

    private String formatValue(double value) {
        if (Math.abs(value - Math.rint(value)) < 0.000001) {
            return String.valueOf((long) Math.rint(value));
        }
        return String.format("%.1f", value);
    }

    private String formatPercent(double value) {
        return String.format("%.1f%%", value);
    }

    // ------------------------
    // Style defaults
    // ------------------------

    private void initializeStyleDefaults() {
        titleFont = deriveDefaultTitleFont();
        subtitleFont = deriveDefaultSubtitleFont();
        labelFont = deriveDefaultLabelFont();
        valueFont = deriveDefaultValueFont();

        titleColor = deriveDefaultTitleColor();
        subtitleColor = deriveDefaultSubtitleColor();
        labelColor = deriveDefaultLabelColor();
        valueColor = deriveDefaultValueColor();
    }

    private Font deriveDefaultTitleFont() {
        return new Font("Dialog", Font.BOLD, 17);
    }

    private Font deriveDefaultSubtitleFont() {
        return new Font("Dialog", Font.PLAIN, 12);
    }

    private Font deriveDefaultLabelFont() {
        return new Font("Dialog", Font.PLAIN, 11);
    }

    private Font deriveDefaultValueFont() {
        return new Font("Dialog", Font.BOLD, 11);
    }

    private Color deriveDefaultTitleColor() {
        return new Color(45, 45, 45);
    }

    private Color deriveDefaultSubtitleColor() {
        return new Color(110, 110, 110);
    }

    private Color deriveDefaultLabelColor() {
        return new Color(110, 110, 110);
    }

    private Color deriveDefaultValueColor() {
        return Color.WHITE;
    }

    private Font getSafeTitleFont() {
        return titleFont != null ? titleFont : deriveDefaultTitleFont();
    }

    private Font getSafeSubtitleFont() {
        return subtitleFont != null ? subtitleFont : deriveDefaultSubtitleFont();
    }

    private Font getSafeLabelFont() {
        return labelFont != null ? labelFont : deriveDefaultLabelFont();
    }

    private Font getSafeValueFont() {
        return valueFont != null ? valueFont : deriveDefaultValueFont();
    }

    private Color getSafeTitleColor() {
        return titleColor != null ? titleColor : deriveDefaultTitleColor();
    }

    private Color getSafeSubtitleColor() {
        return subtitleColor != null ? subtitleColor : deriveDefaultSubtitleColor();
    }

    private Color getSafeLabelColor() {
        return labelColor != null ? labelColor : deriveDefaultLabelColor();
    }

    private Color getSafeValueColor() {
        return valueColor != null ? valueColor : deriveDefaultValueColor();
    }

    private static Dataset createDefaultDataset() {
        String[] columnNames = new String[]{"value", "label", "color"};
        Class<?>[] columnTypes = new Class<?>[]{Double.class, String.class, Color.class};

        Object[][] data = new Object[][]{
                {42.5, 31.2, 28.9, 19.8, 15.6, 8.3},
                {"Cloud Services", "Enterprise Software", "Cybersecurity", "Data Analytics", "AI & Machine Learning", "Other"},
                {
                        new Color(83, 138, 214),
                        new Color(245, 158, 56),
                        new Color(76, 161, 85),
                        new Color(61, 176, 214),
                        new Color(220, 200, 0),
                        new Color(147, 104, 205)
                }
        };

        return new BasicDataset(columnNames, columnTypes, data);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(500, 385);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(180, 140);
    }

    private static class ChartRow {
        final double value;
        final String label;
        final Color color;

        ChartRow(double value, String label, Color color) {
            this.value = value;
            this.label = label;
            this.color = color;
        }
    }
}