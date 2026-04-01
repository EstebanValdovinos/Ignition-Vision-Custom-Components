package com.inductiveautomation.ignition.examples.ce.beaninfos.display;

import com.inductiveautomation.factorypmi.designer.property.customizers.DynamicPropertyProviderCustomizer;
import com.inductiveautomation.factorypmi.designer.property.customizers.StyleCustomizer;
import com.inductiveautomation.ignition.examples.ce.components.display.BarChartComponent;
import com.inductiveautomation.vision.api.designer.beans.CommonBeanInfo;
import com.inductiveautomation.vision.api.designer.beans.VisionBeanDescriptor;

import javax.swing.*;
import java.awt.*;
import java.beans.IntrospectionException;
import java.net.URL;

public class BarChartComponentBeanInfo extends CommonBeanInfo {

    public BarChartComponentBeanInfo() {
        super(BarChartComponent.class,
                DynamicPropertyProviderCustomizer.VALUE_DESCRIPTOR,
                StyleCustomizer.VALUE_DESCRIPTOR);
    }

    @Override
    protected void initProperties() throws IntrospectionException {
        super.initProperties();

        removeProp("opaque");
        removeProp("background");

        addProp("data", "Data", "Dataset where column 0 contains X-axis labels and columns 1..n contain numeric series values.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addProp("seriesProperties", "Series Properties", "Dataset with per-series styling columns: barColor, borderColor, borderWidth, showLabels, and showToolTip.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addProp("chartTitle", "Chart Title", "Title shown centered above the chart.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addProp("hoverIndex", "Hover Index", "Current hovered X-axis index. Use -1 when nothing is hovered.", CAT_DATA, BOUND_MASK);
        addProp("selectedIndex", "Selected Index", "Current clicked X-axis index. Use -1 for no selection.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addProp("loading", "Loading", "If true, suppresses chart drawing and leaves the component in loading state.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);

        addEnumProp("chartMode", "Chart Mode", "Selects whether the chart is drawn as grouped bars or stacked bars.", CAT_BEHAVIOR, new int[]{BarChartComponent.MODE_GROUPED, BarChartComponent.MODE_STACKED}, new String[]{"Grouped", "Stacked"});
        addProp("autoRange", "Auto Range", "Automatically computes lower and upper bounds from the data with a small margin.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("lowerBound", "Lower Bound", "Manual lower bound used when Auto Range is false.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("upperBound", "Upper Bound", "Manual upper bound used when Auto Range is false.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("showAxisLabels", "Show Axis Labels", "If true, X and Y axis labels are rendered.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("showYAxis", "Show Y Axis", "If true, the Y-axis line and tick marks are rendered.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("showLegends", "Show Legends", "If true, the legend is rendered at the bottom center.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("enabled", "Enabled", "Enables or disables mouse interaction for hover and selection.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);

        addProp("foreground", "Foreground Color", "Primary text color used for the title, labels, and legends.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("font", "Font", "Font used for the title. Axis, tooltip, and legend fonts derive from it.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("axisLineColor", "Axis Line Color", "Color used for axis lines and tick marks.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("axisLineWidth", "Axis Line Width", "Stroke width used for axis lines.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("xAxisGap", "X Axis Gap", "Gap between the plotted area and the X-axis baseline.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("categoryGapRatio", "Category Gap Ratio", "Percentage of each category slot reserved as spacing between categories.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("barGapRatio", "Bar Gap Ratio", "Percentage of grouped category width reserved as spacing between bars.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("cornerRadius", "Corner Radius", "Corner radius used for the bars. Use 0 for square bars.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
    }

    @Override
    protected void initDesc() {
        VisionBeanDescriptor bean = getBeanDescriptor();
        bean.setName("Bar Chart");
        bean.setDisplayName("Bar Chart");
        bean.setShortDescription("A modern dataset-driven bar chart with grouped and stacked display modes, hover tooltip, and legend.");
    }

    @Override
    public Image getIcon(int kind) {
        URL url = getClass().getResource("/images/bar_chart_icon.png");
        if (url == null) {
            return null;
        }
        switch (kind) {
            case java.beans.BeanInfo.ICON_COLOR_16x16:
            case java.beans.BeanInfo.ICON_MONO_16x16:
            case java.beans.BeanInfo.ICON_COLOR_32x32:
            case java.beans.BeanInfo.ICON_MONO_32x32:
                return new ImageIcon(url).getImage();
            default:
                return null;
        }
    }
}