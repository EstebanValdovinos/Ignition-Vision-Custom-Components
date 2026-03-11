package com.inductiveautomation.ignition.examples.ce.beaninfos.input;

import com.inductiveautomation.factorypmi.designer.property.customizers.DynamicPropertyProviderCustomizer;
import com.inductiveautomation.factorypmi.designer.property.customizers.StyleCustomizer;
import com.inductiveautomation.ignition.examples.ce.components.input.DateRangePickerComponent;
import com.inductiveautomation.vision.api.designer.beans.CommonBeanInfo;
import com.inductiveautomation.vision.api.designer.beans.VisionBeanDescriptor;

import java.beans.IntrospectionException;

public class DateRangePickerComponentBeanInfo extends CommonBeanInfo {

    public DateRangePickerComponentBeanInfo() {
        super(DateRangePickerComponent.class,
                DynamicPropertyProviderCustomizer.VALUE_DESCRIPTOR,
                StyleCustomizer.VALUE_DESCRIPTOR);
    }

    @Override
    protected void initProperties() throws IntrospectionException {
        super.initProperties();

        removeProp("opaque");

        addProp("startDate", "Start Date", "The applied start datetime.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addProp("endDate", "End Date", "The applied end datetime.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);

        addProp("primaryColor", "Primary Color", "The color used for selected start and end days, active week number hover, and apply button.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("rangeColor", "Range Color", "The fill color used for the days inside the selected range.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("todayColor", "Today Color", "The highlight color used for the current day.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("backgroundColor", "Background Color", "The background color of the popup calendar body.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("lineColor", "Line Color", "The color used for the time slider track.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("headerColor", "Header Color", "The background color of the collapsed button header.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("headerTextColor", "Header Text Color", "The text color of the collapsed button header.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("foreground", "Foreground Color", "The main text color used in the popup calendar.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("font", "Font", "The font used for the component text.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);

        addProp("dark", "Dark", "Enables dark theme mode.", CAT_BEHAVIOR, BOUND_MASK);
        addProp("open", "Is Open", "True while the popup is open.", CAT_BEHAVIOR, BOUND_MASK);
        addProp("enabled", "Enabled", "Enables or disables the component.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
    }

    @Override
    protected void initDesc() {
        VisionBeanDescriptor bean = getBeanDescriptor();
        bean.setName("Date Range Picker");
        bean.setDisplayName("Date Range Picker");
        bean.setShortDescription("A two-month popup calendar for selecting a date and time range.");
    }

    @Override
    public java.awt.Image getIcon(int kind) {
        switch (kind) {
            case java.beans.BeanInfo.ICON_COLOR_16x16:
            case java.beans.BeanInfo.ICON_MONO_16x16:
                return new javax.swing.ImageIcon(
                        getClass().getResource("/images/daterange_picker_icon.png")
                ).getImage();

            case java.beans.BeanInfo.ICON_COLOR_32x32:
            case java.beans.BeanInfo.ICON_MONO_32x32:
                return new javax.swing.ImageIcon(
                        getClass().getResource("/images/daterange_picker_icon.png")
                ).getImage();
        }
        return null;
    }
}