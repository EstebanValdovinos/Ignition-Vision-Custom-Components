package com.inductiveautomation.ignition.examples.ce.beaninfos.display;

import com.inductiveautomation.factorypmi.designer.property.customizers.DynamicPropertyProviderCustomizer;
import com.inductiveautomation.factorypmi.designer.property.customizers.StyleCustomizer;
import com.inductiveautomation.ignition.examples.ce.components.display.TeslaLoadingSpinnerComponent;
import com.inductiveautomation.vision.api.designer.beans.CommonBeanInfo;
import com.inductiveautomation.vision.api.designer.beans.VisionBeanDescriptor;

import java.beans.IntrospectionException;

public class TeslaLoadingSpinnerComponentBeanInfo extends CommonBeanInfo {

    public TeslaLoadingSpinnerComponentBeanInfo() {
        super(TeslaLoadingSpinnerComponent.class,
                DynamicPropertyProviderCustomizer.VALUE_DESCRIPTOR,
                StyleCustomizer.VALUE_DESCRIPTOR);
    }

    @Override
    protected void initProperties() throws IntrospectionException {
        super.initProperties();

        addProp("enabled", "Enabled", "Enables or disables the spinner animation and rendering.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("clockwise", "Clockwise", "If true the spinner rotates clockwise, otherwise counterclockwise.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);

        addProp("spinnerColor", "Spinner Color", "Applies color to the spinner bar.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("iconPath", "Icon Path", "Ignition image path for the icon shown in the center of the spinner.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("spinnerWidth", "Spinner Width", "Defines the width of the spinner bar.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("text", "Text", "If specified, shows the text below the spinner.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("background","Background", "Background color used for the center disk.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("foreground", "Foreground", "Foreground color used for text and icon tint.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("font", "Font", "Font used for the text below the spinner.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);

    }

    @Override
    protected void initDesc() {
        VisionBeanDescriptor bean = getBeanDescriptor();
        bean.setName("Loading Spinner");
        bean.setDisplayName("Loading Spinner");
        bean.setShortDescription("A Tesla-inspired animated loading spinner with center icon pulse.");
    }

    @Override
    public java.awt.Image getIcon(int kind) {
        switch (kind) {
            case java.beans.BeanInfo.ICON_COLOR_16x16:
            case java.beans.BeanInfo.ICON_MONO_16x16:
                return new javax.swing.ImageIcon(
                        getClass().getResource("/images/loading_spinner_icon.png")
                ).getImage();

            case java.beans.BeanInfo.ICON_COLOR_32x32:
            case java.beans.BeanInfo.ICON_MONO_32x32:
                return new javax.swing.ImageIcon(
                        getClass().getResource("/images/loading_spinner_icon.png")
                ).getImage();
        }
        return null;
    }
}
