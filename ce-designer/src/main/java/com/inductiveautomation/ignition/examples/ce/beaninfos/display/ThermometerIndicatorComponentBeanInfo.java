package com.inductiveautomation.ignition.examples.ce.beaninfos.display;

import com.inductiveautomation.factorypmi.designer.property.customizers.DynamicPropertyProviderCustomizer;
import com.inductiveautomation.factorypmi.designer.property.customizers.StyleCustomizer;
import com.inductiveautomation.ignition.examples.ce.components.display.ThermometerIndicatorComponent;
import com.inductiveautomation.vision.api.designer.beans.CommonBeanInfo;
import com.inductiveautomation.vision.api.designer.beans.VisionBeanDescriptor;

import javax.swing.*;
import java.awt.*;
import java.beans.IntrospectionException;
import java.net.URL;

public class ThermometerIndicatorComponentBeanInfo extends CommonBeanInfo {

    public ThermometerIndicatorComponentBeanInfo() {
        super(ThermometerIndicatorComponent.class,
                DynamicPropertyProviderCustomizer.VALUE_DESCRIPTOR,
                StyleCustomizer.VALUE_DESCRIPTOR);
    }

    @Override
    protected void initProperties() throws IntrospectionException {
        super.initProperties();

        removeProp("opaque");

        addProp("value", "Value", "The current temperature value shown by the thermometer.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addProp("minValue", "Min Value", "Minimum value of the thermometer scale.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addProp("maxValue", "Max Value", "Maximum value of the thermometer scale.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);

        addProp("bulbSize", "Bulb Size", "Diameter of the bottom bulb in pixels.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("fineTicks", "Fine Ticks", "Number of fine tick marks between each major tick.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("majorTicks", "Major Ticks", "Number of major tick marks shown in the scale.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("gap", "Gap", "Inner gap between the glass shell and the mercury fill.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("highColor", "High Color", "Color used at the high end of the thermometer gradient.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("lowColor", "Low Color", "Color used at the low end of the thermometer gradient.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("shadowOpacity", "Shadow Opacity", "Opacity of the thermometer shadow effect from 0.0 to 1.0.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("showScale", "Show Scale", "If true, the right-side scale and labels are shown.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("foreground", "Foreground Color", "Text color used for tooltip and scale labels.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("font", "Font", "Font used for scale labels.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("background", "Background Color", "Optional component background color.", CAT_APPEARANCE, BOUND_MASK);
        addProp("showTooltip", "Show Tooltip", "If true, the temperature tooltip is displayed.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);

        addEnumProp(
                "unit",
                "Unit",
                "Selects the unit displayed on the scale and tooltip.",
                CAT_BEHAVIOR,
                new int[]{
                        ThermometerIndicatorComponent.UNIT_CELSIUS,
                        ThermometerIndicatorComponent.UNIT_FAHRENHEIT
                },
                new String[]{
                        "Celsius",
                        "Fahrenheit"
                }
        );
    }

    @Override
    protected void initDesc() {
        VisionBeanDescriptor bean = getBeanDescriptor();
        bean.setName("Thermometer Indicator");
        bean.setDisplayName("Thermometer Indicator");
        bean.setShortDescription("A vertical thermometer display with gradient mercury fill, scale, and tooltip.");
    }

    @Override
    public Image getIcon(int kind) {
        URL url = getClass().getResource("/images/thermometer_icon.png");
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