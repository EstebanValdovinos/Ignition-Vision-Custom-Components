package com.inductiveautomation.ignition.examples.ce.beaninfos.display;

import com.inductiveautomation.factorypmi.designer.property.customizers.DynamicPropertyProviderCustomizer;
import com.inductiveautomation.factorypmi.designer.property.customizers.StyleCustomizer;
import com.inductiveautomation.ignition.examples.ce.components.display.PieDonutChartComponent;
import com.inductiveautomation.vision.api.designer.beans.CommonBeanInfo;
import com.inductiveautomation.vision.api.designer.beans.VisionBeanDescriptor;

import javax.swing.*;
import java.awt.*;
import java.beans.IntrospectionException;
import java.net.URL;

public class PieDonutChartComponentBeanInfo extends CommonBeanInfo {

    public PieDonutChartComponentBeanInfo() {
        super(PieDonutChartComponent.class,
                DynamicPropertyProviderCustomizer.VALUE_DESCRIPTOR,
                StyleCustomizer.VALUE_DESCRIPTOR);
    }

    @Override
    protected void initProperties() throws IntrospectionException {
        super.initProperties();

        removeProp("opaque");
        removeProp("font");
        removeProp("foreground");

        addEnumProp(
                "chartType",
                "Chart Type",
                "Selects whether the chart is drawn as pie, donut, or rings.",
                CAT_APPEARANCE,
                new int[]{
                        PieDonutChartComponent.CHART_PIE,
                        PieDonutChartComponent.CHART_DONUT,
                        PieDonutChartComponent.CHART_RINGS
                },
                new String[]{
                        "Pie",
                        "Donut",
                        "Rings"
                }
        );

        addEnumProp(
                "ringsAlignment",
                "Rings Alignment",
                "Controls whether rings are arranged vertically or horizontally.",
                CAT_APPEARANCE,
                new int[]{
                        PieDonutChartComponent.RINGS_VERTICAL,
                        PieDonutChartComponent.RINGS_HORIZONTAL
                },
                new String[]{
                        "Vertical",
                        "Horizontal"
                }
        );

        addProp("holeSize", "Hole Size", "Center hole size percentage used when donut mode is selected.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("showLabels", "Show Labels", "Shows outer labels for pie/donut and ring labels for rings.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("showPercentages", "Show Percentages", "Shows percentages inside pie and donut slices.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("strokeWidth", "Stroke Width", "Ring stroke width used in rings mode.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("ringGap", "Ring Gap", "Gap in pixels between rings when rings mode is selected.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("chartTopGap", "Chart Top Gap", "Gap between the title/subtitle block and the chart.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);

        addProp("borderStroke", "Border Stroke", "Border stroke width.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("borderRadius", "Border Radius", "Border corner radius. 0 means square corners.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);

        addProp("data", "Data", "Dataset with columns value, label, and color.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addProp("chartTitle", "Chart Title", "Title shown above the chart.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addProp("subtitle", "Subtitle", "Subtitle shown below the chart title.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addProp("units", "Units", "Units suffix used in rings mode.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);

        addProp("trackColor", "Track Color", "Background track color used in rings mode.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("background", "Background Color", "Component background color.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("borderColor", "Border Color", "Border color.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);

        addProp("titleFont", "Title Font", "Font used for the chart title.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("subtitleFont", "Subtitle Font", "Font used for the subtitle.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("labelFont", "Label Font", "Font used for labels and outer values in pie/donut.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("valueFont", "Value Font", "Font used for ring center value and pie/donut percentages.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);

        addProp("titleColor", "Title Color", "Color used for the chart title.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("subtitleColor", "Subtitle Color", "Color used for the subtitle.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("labelColor", "Label Color", "Color used for labels and outer values in pie/donut.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("valueColor", "Value Color", "Color used for ring center value and pie/donut percentages.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
    }

    @Override
    protected void initDesc() {
        VisionBeanDescriptor bean = getBeanDescriptor();
        bean.setName("Pie Donut Chart");
        bean.setDisplayName("Pie Donut Chart");
        bean.setShortDescription("A chart component that supports pie, donut, and multi-ring display modes.");
    }

    @Override
    public Image getIcon(int kind) {
        URL url = getClass().getResource("/images/pie_donut_chart_icon.png");
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